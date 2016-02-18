package com.mambu.ant;

import java.io.IOException;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class PhraseAppHelper {

	public static final String PHRASE_APP_BASE_URL = "https://api.phraseapp.com/api/v2/projects/";

	public static String getAsString(HttpsURLConnection connectionUpload)
			throws IOException {

		Scanner scanner = new Scanner(connectionUpload.getInputStream(),
				"UTF-8");
		Scanner scanner2 = scanner.useDelimiter("\\A");

		String fileContent = scanner2.hasNext() ? scanner2.next() : "";

		scanner2.close();
		scanner.close();

		return fileContent;

	}

}
