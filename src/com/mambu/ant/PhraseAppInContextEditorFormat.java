package com.mambu.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant target to generate PhraseApp In-Context Editor translation files for new
 * language
 */
public class PhraseAppInContextEditorFormat extends Task {

	private static final String PHRASE_LANGUAGE_CODE = "aa";
	private static final String PHRASE_APP_KEY_PATTERN = "(.*)\\.(.*)=(.*)";
	private static final String PHRASE_APP_KEY_FORMAT = "$2 = [[__phrase_$1\\.$2__]]";

	/**
	 * absolute path to the directory where the master translation .properties
	 * files are located
	 */
	private String sourceDir;

	/**
	 * absolute path to the directory where the translation .properties files
	 * are located
	 */
	private String destinationDir;

	/**
	 * Internal test method to check if the Ant task is working
	 * 
	 * @param args
	 *            ignored
	 */
	public static void main(String[] args) {
		PhraseAppInContextEditorFormat inContextEditorFormat = new PhraseAppInContextEditorFormat();
		inContextEditorFormat.setSource("tbd.");
		inContextEditorFormat.setDestination("tbd.");
		inContextEditorFormat.execute();
	}

	public void setSource(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public void setDestination(String destinationDir) {
		this.destinationDir = destinationDir;
	}

	/**
	 * Prepend class name to every key and substitute translation with
	 * In-Context Editor format
	 */
	public void execute() throws org.apache.tools.ant.BuildException {
		try {

			log("Looking for .properties files in: " + this.sourceDir);

			File dir = new File(this.sourceDir);

			// filter files with the extension ".properties" for the given
			// source directory
			File[] files = dir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.contains(".properties");
				}
			});

			// process each file separately
			if (files != null && files.length != 0) {

				for (final File file : files) {

					String prefix = getNamespacePrefix(file);
					log("Processing file: " + file.getName() + ", prepending '"
							+ prefix
							+ "' to every key to avoid namespace conflicts.");

					FileInputStream fis = new FileInputStream(file);
					InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
					Scanner scanner = new Scanner(isr);
					Scanner scanner2 = scanner.useDelimiter("\\A");
					String fileContent = scanner2.hasNext() ? scanner2.next()
							: "";
					scanner2.close();
					scanner.close();
					isr.close();
					fis.close();

					// prepend namespace prefix
					String fileContentWithPrefixes = "";
					for (String line : fileContent.split("\\r?\\n")) {
						if (!line.trim().isEmpty() && !line.startsWith("#")
								&& !(prefix + line).equals(prefix)
								&& !line.trim().endsWith(" =")
								&& !line.trim().equals("=")) {

							String prefixedLine = prefix + line + "\n";

							// replace translation with key in In-Context Editor
							// format
							if (prefixedLine.contains("{4}")) {
								String pattern = "^"
										+ PHRASE_APP_KEY_PATTERN
										+ "{0}(.*){1}(.*){2}(.*){3}(.*){4}(.*)$";
								prefixedLine = prefixedLine
										.replaceAll(
												pattern,
												PHRASE_APP_KEY_FORMAT
														+ " [<0>{0}</0>, <1>{1}</1>, <2>{2}</2>, <3>{3}</3>, <4>{4}</4>]");
							} else if (prefixedLine.contains("{3}")) {
								String pattern = "^" + PHRASE_APP_KEY_PATTERN
										+ "{0}(.*){1}(.*){2}(.*){3}(.*)$";
								prefixedLine = prefixedLine
										.replaceAll(
												pattern,
												PHRASE_APP_KEY_FORMAT
														+ " [<0>{0}</0>, <1>{1}</1>, <2>{2}</2>, <3>{3}</3>]");
							} else if (prefixedLine.contains("{2}")) {
								String pattern = "^" + PHRASE_APP_KEY_PATTERN
										+ "{0}(.*){1}(.*){2}(.*)$";
								prefixedLine = prefixedLine
										.replaceAll(
												pattern,
												PHRASE_APP_KEY_FORMAT
														+ " [<0>{0}</0>, <1>{1}</1>, <2>{2}</2>]");
							} else if (prefixedLine.contains("{1}")) {
								String pattern = "^" + PHRASE_APP_KEY_PATTERN
										+ "{0}(.*){1}(.*)$";
								prefixedLine = prefixedLine.replaceAll(pattern,
										PHRASE_APP_KEY_FORMAT
												+ " [<0>{0}</0>, <1>{1}</1>]");
							} else if (prefixedLine.contains("{0}")) {
								String pattern = "^" + PHRASE_APP_KEY_PATTERN
										+ "{0}(.*)$";
								prefixedLine = prefixedLine
										.replaceAll(pattern,
												PHRASE_APP_KEY_FORMAT
														+ " [<0>{0}</0>]");
							} else {
								String pattern = "^" + PHRASE_APP_KEY_PATTERN
										+ "$";
								prefixedLine = prefixedLine.replaceAll(pattern,
										PHRASE_APP_KEY_FORMAT);
							}

							fileContentWithPrefixes += prefixedLine;
						}
						// skip adding prefix for comments and empty lines
						else if (line.trim().isEmpty() || line.startsWith("#")) {
							fileContentWithPrefixes += line + "\n";
						}
					} // for each line

					// replace file content
					String destFileName = destinationDir
							+ System.getProperty("file.separator")
							+ file.getName()
									.replaceAll(".properties", "")
									.replaceAll(
											"\\.",
											System.getProperty("file.separator"))
							+ "_" + PHRASE_LANGUAGE_CODE + ".properties";
					FileOutputStream fos = new FileOutputStream(destFileName,
							false);
					byte[] newFileContent = ("# Generated for PhraseApp In-Context Editor"
							+ "\n\n" + fileContentWithPrefixes).getBytes();
					fos.write(newFileContent);
					fos.close();

				} // for each file

			} else {

				log("No .properties files were found in: " + this.sourceDir
						+ ".");

			}

		} catch (Exception e) {
			log("An error occurred: ");
			e.printStackTrace();
			throw new BuildException(e.getMessage());
		}
	}

	private String getNamespacePrefix(File file) {
		String prefix = "";
		String fileName = file.getName();
		String[] fileParts = fileName.split("\\.");
		// e.g. example.properties -> example
		if (fileParts.length == 2) {
			prefix = fileParts[0];
		}
		// e.g. error.jsp.properties -> error
		else if (fileParts.length >= 3
				&& fileParts[fileParts.length - 2].length() <= 4) {
			prefix = fileParts[fileParts.length - 3];
		}
		// e.g. com.mambu.mymambu.email.service.EmailService.properties
		// -> EmailService
		else if (fileParts.length >= 3
				&& fileParts[fileParts.length - 2].length() > 4) {
			prefix = fileParts[fileParts.length - 2];
		}
		// fallback
		else {
			prefix = file.getName().substring(0,
					file.getName().lastIndexOf("."));
		}
		return prefix + ".";
	}
}
