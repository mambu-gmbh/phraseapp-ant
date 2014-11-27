package com.mambu.ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant target for downloading translation files from PhraseApp and placing them
 * in the right Java package.
 */
public class PhraseAppDownload extends Task {

	/**
	 * absolute path to the directory where the translation .properties files
	 * are located
	 */
	private String destinationDir;

	/**
	 * the auth token of the PhraseApp project
	 */
	private String projectAuthToken;

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

	/**
	 * Internal test method to check if the Ant task is working
	 * 
	 * @param args
	 *            ignored
	 */
	public static void main(String[] args) {
		PhraseAppDownload download = new PhraseAppDownload();
		download.setDestination("");
		download.setProjectAuthToken("");
		download.setMergeInPackageStructure(false);
		download.setIncludeMainLocale(false);
		download.execute();
	}

	public void setDestination(String destinationDir) {
		this.destinationDir = destinationDir;
	}

	public void setProjectAuthToken(String projectAuthToken) {
		this.projectAuthToken = projectAuthToken;
	}

	public void setMergeInPackageStructure(boolean mergeInPackageStructure) {
		this.mergeInPackageStructure = mergeInPackageStructure;
	}

	public void setIncludeMainLocale(boolean includeMainLocale) {
		this.includeMainLocale = includeMainLocale;
	}

	/**
	 * Create a PhraseApp session, download all translation *.properties files
	 * for each locale and tag to the destination directory and destroy the
	 * session
	 */
	public void execute() throws org.apache.tools.ant.BuildException {
		try {

			// configure the SSLContext with another TrustManager to accept all
			// SSL certificates
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(new KeyManager[0],
					new TrustManager[] { new DefaultTrustManager() },
					new SecureRandom());
			SSLContext.setDefault(ctx);

			Map<String, String> locales = getMapOfLocales();

			List<String> tags = getListOfTags();

			downloadTranslationPropertiesFiles(locales, tags);

		} catch (Exception e) {
			log("An error occurred '" + e.getLocalizedMessage() + "'.");
			e.printStackTrace();
			throw new BuildException(e.getMessage());
		}

	}

	private Map<String, String> getMapOfLocales() throws IOException {

		Map<String, String> locales = new HashMap<String, String>();

		URL url = new URL("https://phraseapp.com/api/v1/locales?auth_token="
				+ projectAuthToken);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

		log("Getting all locales using a 'GET' request to URL : "
				+ url.toExternalForm());

		// execute request
		int responseCode = con.getResponseCode();

		log("Response Code : " + responseCode);

		// parse response as string
		String response = PhraseAppHelper.getAsString(con);

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

				String localeName = null;
				String localeCode = null;
				for (String localeJsonField : localeJsonFields) {
					if (localeJsonField.contains("\"name\"")) {
						String prefixName = "\"name\":\"";
						String suffixName = "\"";
						localeName = localeJsonField.substring(
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

				locales.put(localeCode, localeName);
			}
		}

		log("Found locales: " + locales);

		return locales;
	}

	private List<String> getListOfTags() throws IOException {

		List<String> tags = new ArrayList<String>();

		URL url = new URL("https://phraseapp.com/api/v1/tags?auth_token="
				+ projectAuthToken);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

		log("Getting all tags using a 'GET' request to URL : "
				+ url.toExternalForm());

		// execute request
		int responseCode = con.getResponseCode();

		log("Response Code : " + responseCode);

		// parse response as string
		String response = PhraseAppHelper.getAsString(con);

		// parse JSON to locales list
		String[] tagsJson = response.split("\\},\\{");
		for (String tagJson : tagsJson) {
			String prefix = "\"name\":\"";
			String suffix = "\"";
			String tag = tagJson.substring(
					tagJson.indexOf(prefix) + prefix.length(),
					tagJson.lastIndexOf(suffix));
			tags.add(tag);
		}

		log("Found tags: " + Arrays.toString(tags.toArray()));

		return tags;
	}

	/**
	 * Downloads all translations for the given locales and tags and saves them
	 * as Java .properties files.
	 * 
	 * @param locales
	 *            list of locales to download translations for all given tags
	 *            for
	 * @param tags
	 *            list of tags to download translations of a locale for
	 */
	private void downloadTranslationPropertiesFiles(
			final Map<String, String> locales, List<String> tags) {

		final AtomicInteger failedDownloads = new AtomicInteger(0);

		// max 2 parallel connections are allowed by phraseapp, otherwise returns HTTP error code 429 
		ExecutorService exec = Executors.newFixedThreadPool(2);

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
									URL url = new URL(
											"https://phraseapp.com/api/v1/translations/download?auth_token="
													+ projectAuthToken
													+ "&locale="
													+ locales.get(localeCode)
													+ "&format=properties&tag="
													+ tag);
									HttpsURLConnection con = (HttpsURLConnection) url
											.openConnection();

									log("Getting '" + locales.get(localeCode)
											+ "' translations for '" + tag
											+ "' using 'GET' request to URL: "
											+ url.toExternalForm());

									// execute request
									int responseCode = con.getResponseCode();

									log("Response Code for '"
											+ locales.get(localeCode)
											+ "' translation file '" + tag
											+ "' : " + responseCode);

									// parse response as string
									String response = PhraseAppHelper
											.getAsString(con);

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

									// remove comments
									List<String> responseFiltered = new ArrayList<String>();
									for (String responseLine : response
											.split("\n")) {
										if (!responseLine.startsWith("#")) {
											responseFiltered.add(responseLine);
										}
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

									log("Wrote content for '"
											+ locales.get(localeCode)
											+ "' translation file '" + tag
											+ "' to file '" + fileName + "'.");

								} catch (MalformedURLException e) {
									failedDownloads.incrementAndGet();
									log("Could not download content for file '"
											+ tag + "' due to invalid URL '"
											+ e.getLocalizedMessage() + "'.");
									e.printStackTrace();
								} catch (IOException e) {
									failedDownloads.incrementAndGet();
									log("Could not download content for file '"
											+ tag + "' due to IO error '"
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
				} else {

				}
			} catch (InterruptedException e) {
				log("Download of translation files failed due to '"
						+ e.getLocalizedMessage() + "'.");
				e.printStackTrace();
			}
		}

	}
}
