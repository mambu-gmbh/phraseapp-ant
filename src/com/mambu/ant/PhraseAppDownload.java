package com.mambu.ant;

import static com.mambu.ant.backup.Constants.DESTINATION_DIR;
import static java.util.stream.Collectors.toList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;

import com.mambu.ant.backup.BackupService;
import com.mambu.ant.backup.BackupServiceBuilder;
import com.mambu.ant.backup.BackupServiceBuilder.BackupServiceProvider;
import com.mambu.ant.phraseapp.PhraseApi;
import com.mambu.ant.phraseapp.PhraseApiSettings;

/**
 * Ant target for downloading translation files from PhraseApp and placing them
 * in the right Java package.
 */
public class PhraseAppDownload extends BaseTask {

	/**
	 * absolute path to the directory where the translation .properties files
	 * are located
	 */
	private String destinationDir;

	/**
	 * if set to true, places the translation files in the package according to
	 * the file names (e.g. com.company.module.i18n.module.properties goes to
	 * destinationDir/com/company/module/i18n/module.properties), if set to
	 * false places the translation file in a locale folder with the tag name as
	 * file name (e.g. com.company.module.i18n.module.properties goes to
	 * destinationDir /locale/com.company.module.i18n.module.properties)
	 */
	private boolean mergeInPackageStructure;

	/**
	 * if set to true, the main locale is included, if set to false the main
	 * locale is excluded
	 */
	private boolean includeMainLocale;

	// backup options

	/**
	 * if set to true, the translation files are backed up to a local file storage.
	 */
	private boolean backupFiles;

	/**
	 * the kind of backup to be performed. Currently only LOCAL but could be expanded to others as well.
	 */
	private BackupServiceProvider backupProvider = BackupServiceProvider.LOCAL;

	/**
	 * Internal test method to check if the Ant task is working
	 * 
	 * @param args
	 *            ignored
	 */
	public static void main(String[] args) {
		PhraseAppDownload download = new PhraseAppDownload();
		download.setDestination("");
		download.setUserAuthToken("");
		download.setProjectId("");
		download.setMergeInPackageStructure(false);
		download.setIncludeMainLocale(false);
		download.setBackupFiles(false);
		download.execute();
	}

	public void setDestination(String destinationDir) {
		this.destinationDir = destinationDir;
	}

	public void setMergeInPackageStructure(boolean mergeInPackageStructure) {
		this.mergeInPackageStructure = mergeInPackageStructure;
	}

	public void setIncludeMainLocale(boolean includeMainLocale) {
		this.includeMainLocale = includeMainLocale;
	}

	public void setBackupFiles(boolean backupFiles) {
		this.backupFiles = backupFiles;
	}
	/**
	 * Create a PhraseApp session, download all translation *.properties files
	 * for each locale and tag to the destination directory and destroy the
	 * session
	 */
	public void execute() throws org.apache.tools.ant.BuildException {
		try {

			initAPI();

			Map<String, String> locales = getMapOfLocales();
			List<String> tags = getListOfTags();
			tags = filterOnlyPropertiesTags(tags);
			downloadTranslationPropertiesFiles(locales, tags);

		} catch (Exception e) {
			log("An error occurred '" + e.getLocalizedMessage() + "'.");
			e.printStackTrace();
			throw new BuildException(e.getMessage());
		}

	}
	private List<String> filterOnlyPropertiesTags(List<String> tags) {

		return tags.stream()
			.filter(tag -> tag.endsWith(".properties"))
			.collect(toList());
	}

	private Map<String, String> getMapOfLocales() throws IOException {

		Map<String, String> locales = new HashMap<>();

		String response = phraseApi.locales().getAll();

		// parse JSON to locales list
		String[] localesJson = response.split("\\},\\{");
		for (String localeJson : localesJson) {

			// skip main locale if translation files merged to package
			// structure, as it's not needed (translations are contained
			// already in the Java Message interfaces), otherwise also include
			// main locale for a complete backup
			if ((includeMainLocale && localeJson.contains("\"code\":\"en\""))
					|| !localeJson.contains("\"code\":\"en\"")) {

				String[] localeJsonFields = localeJson.split(",");

				String localeId = null;
				String localeCode = null;
				for (String localeJsonField : localeJsonFields) {
					if (localeJsonField.contains("\"id\"") && localeId == null) {
						String prefixName = "\"id\":\"";
						String suffixName = "\"";
						localeId = localeJsonField.substring(
								localeJsonField.indexOf(prefixName)
										+ prefixName.length(),
								localeJsonField.lastIndexOf(suffixName));
					} else if (localeJsonField.contains("\"code\"")) {
						String prefixCode = "\"code\":\"";
						String suffixCode = "\"";
						localeCode = localeJsonField.substring(
								localeJsonField.indexOf(prefixCode)
										+ prefixCode.length(),
								localeJsonField.lastIndexOf(suffixCode));
					}
				}

				locales.put(localeCode, localeId);
			}
		}

		log("Found locales: " + locales);

		return locales;
	}

	private List<String> getListOfTags() throws IOException {

		List<String> tags = new ArrayList<String>();
		List<String> responses = new LinkedList<String>();

		// parse response as string
		responses = phraseApi.tags().getAll();

		for (String response : responses) {
			// parse JSON to locales list
			String[] tagsJson = response.split("\\},\\{");
			for (String tagJson : tagsJson) {

				// matches the name field: eq "name":"com.mambu.accounting.client.i18n.AccountingMessage.properties"
				Pattern namePattern = Pattern.compile("\"name\":\"[^,]*");

				Matcher matcher = namePattern.matcher(tagJson);
				matcher.find();
				String jsonNameElement = matcher.group();

				// split the name parameter, so we will remain just with
				// "com.mambu.accounting.client.i18n.AccountingMessage.properties"
				String nameEnclosedInDoubleQuotes = jsonNameElement.split(":")[1];
				// remove the double quotes, so we will remain with
				// com.mambu.accounting.client.i18n.AccountingMessage.properties
				String tag = nameEnclosedInDoubleQuotes.substring(1, nameEnclosedInDoubleQuotes.length() - 1);

				tags.add(tag);
			}
		}

		log("Found tags: " + Arrays.toString(tags.toArray()));

		return tags;
	}

	/**
	 * Get the last page from the Phraseapp header link field. It contains informations about the first, next, previous
	 * and the last page
	 * 
	 * <p>
	 * <https://api.phraseapp.com/v2/projects/255167955b335efb75fed179f65ea85b/tags?page=1&per_page=100>; rel=first,
	 * <https://api.phraseapp.com/v2/projects/255167955b335efb75fed179f65ea85b/tags?page=16&per_page=100>; rel=last,
	 * <https://api.phraseapp.com/v2/projects/255167955b335efb75fed179f65ea85b/tags?page=2&per_page=100>; rel=next
	 * </p>
	 * 
	 * @param linksField
	 * @return
	 */
	private Integer getLastPage(String linksField) {
		Pattern allPages = Pattern.compile("[?|&]page=[^>&]*");

		Matcher matcher = allPages.matcher(linksField);

		List<Integer> pageValues = new LinkedList<Integer>();

		while (matcher.find()) {
			String value = matcher.group().split("=")[1];

			pageValues.add(Integer.parseInt(value));
		}

		Integer maxPageSize = Collections.max(pageValues);
		return maxPageSize;
	}

	/**
	 * Downloads all translations for the given locales and tags and saves them as Java .properties files.
	 * 
	 * @param locales
	 *            list of locales to download translations for all given tags for
	 * @param tags
	 *            list of tags to download translations of a locale for
	 */
	private void downloadTranslationPropertiesFiles(
			final Map<String, String> locales, List<String> tags) {

		final AtomicInteger failedDownloads = new AtomicInteger(0);

		// max 2 parallel connections are allowed by phraseapp, otherwise returns HTTP error code 429 
		ExecutorService exec = Executors.newFixedThreadPool(2);

		List<File> downloadedFiles = new ArrayList<>();

		try {
			for (final String localeCode : locales.keySet()) {
				for (final String tag : tags) {
					exec.submit(new Runnable() {
						@Override
						public void run() {
							// exclude misc. and other auto generated upload
							// tags
							if (tag.endsWith(".properties")) {
								try {
									String response = phraseApi.locales().download(locales.get(localeCode), localeCode, tag);

									String fileName = "";
									if (mergeInPackageStructure) {
										fileName = destinationDir
												+ System.getProperty("file.separator")
												+ tag.replaceAll(".properties",
														"")
														.replaceAll(
																"\\.",
																System.getProperty("file.separator"))
												+ "_" + localeCode
												+ ".properties";
									} else {
										fileName = destinationDir
												+ System.getProperty("file.separator")
												+ localeCode
												+ System.getProperty("file.separator")
												+ tag;
									}

									File file = new File(fileName);
									// create missing intermediary directories
									file.getParentFile().mkdirs();

									List<String> responseFiltered = new ArrayList<String>();
									for (String responseLine : response
											.split("\n")) {
										// skip comments
										if (responseLine.startsWith("#")) {
											continue;
										}

										// skip keys that don't have translations
										String[] keyAndValue = responseLine.split("=");
										if (keyAndValue.length > 1 && StringUtils.isBlank(keyAndValue[1])) {
											continue;
										}

										responseFiltered.add(responseLine);
									}

									// concatenate to one string and remove
									// prefixes
									response = "";
									for (String responseLineFiltered : responseFiltered) {
										String responseLineWithoutPrefix = responseLineFiltered
												.substring(responseLineFiltered
														.indexOf(".") + 1);
										response += responseLineWithoutPrefix
												+ "\n";
									}

									// replace Java properties file escaped
									// characters with actual UTF-8 characters
									response = StringEscapeUtils
											.unescapeJava(response);

									// write content to file as UTF-8
									Writer translationOut = new BufferedWriter(
											new OutputStreamWriter(
													new FileOutputStream(file),
													"UTF-8"));
									try {
										translationOut.write(response);
									} finally {
										translationOut.close();
									}

									downloadedFiles.add(file);

									log("Wrote content for '" + localeCode + "' translation file '" + tag
											+ "' to file '" + fileName + "'.");

								} catch (Exception e) {
									failedDownloads.incrementAndGet();
									log("Could not download content for file '"
											+ tag + "' due to '"
											+ e.getLocalizedMessage() + "'.");
									e.printStackTrace();
								}
							}
						}
					});
				}
			}
		} finally {
			exec.shutdown();
			try {
				if (exec.awaitTermination(10, TimeUnit.MINUTES)) {
					log("Download of all files was completed, download for "
							+ failedDownloads + " files failed.");
				}
			} catch (InterruptedException e) {
				log("Download of translation files failed due to '"
						+ e.getLocalizedMessage() + "'.");
				e.printStackTrace();
			}
		}

		backup(downloadedFiles);

	}

	private void backup(List<File> files) {

		if (backupFiles) {
			try {
				BackupService backupService = BackupServiceBuilder.create(backupProvider)
						.withProperty(DESTINATION_DIR, destinationDir)
						.build();

				long start = System.currentTimeMillis();
				backupService.backup(files);
				long end = System.currentTimeMillis();
				log("Successfully backed up translations with " + backupProvider + " provider in " + (end-start) + "ms");
			} catch (Exception e) {
				log("Backup of translation files failed due to '" + e.getLocalizedMessage() + "'.");
				e.printStackTrace();
			}
		}
	}
}
