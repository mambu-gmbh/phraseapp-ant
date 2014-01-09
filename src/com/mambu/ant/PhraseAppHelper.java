package com.mambu.ant;

import java.io.IOException;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class PhraseAppHelper {

	public static String getAsString(HttpsURLConnection connectionUpload)
			throws IOException {
		String responseJsonUpload;
		Scanner sUpload = new java.util.Scanner(
				connectionUpload.getInputStream(), "UTF-8").useDelimiter("\\A");
		responseJsonUpload = sUpload.hasNext() ? sUpload.next() : "";
		return responseJsonUpload;
	}

}
