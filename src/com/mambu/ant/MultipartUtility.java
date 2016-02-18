package com.mambu.ant;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * This utility class provides an abstraction layer for sending multipart HTTP POST requests to a web server.
 *
 */
public class MultipartUtility {

	private final String boundary;
	private static final String LINE_FEED = "\r\n";
	private HttpsURLConnection httpsConn;
	private String charset;
	private OutputStream outputStream;
	private PrintWriter writer;

	/**
	 * This constructor initializes a new HTTP POST request with content type is set to multipart/form-data
	 * 
	 * @param requestURL
	 * @param charset
	 * @throws IOException
	 */
	public MultipartUtility(String requestURL, String charset) throws IOException {
		this.charset = charset;

		// creates a unique boundary based on time stamp
		boundary = "===" + System.currentTimeMillis() + "===";

		URL url = new URL(requestURL);
		httpsConn = (HttpsURLConnection) url.openConnection();
		httpsConn.setHostnameVerifier(new HostnameVerifier() {

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		httpsConn.setUseCaches(false);
		httpsConn.setDoOutput(true); // indicates POST method
		httpsConn.setDoInput(true);
		httpsConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		outputStream = httpsConn.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
	}

	/**
	 * Adds a form field to the request
	 * 
	 * @param name
	 *            field name
	 * @param value
	 *            field value
	 */
	public void addFormField(String name, String value) {
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
		writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
		writer.append(LINE_FEED);
		writer.append(value).append(LINE_FEED);
		writer.flush();
	}

	/**
	 * Add a specific fileName and content to the request
	 * 
	 * @param fieldName
	 * @param fileName
	 * @param content
	 * @throws IOException
	 */
	public void addFilePartContent(String fieldName, String fileName, String content) throws IOException {
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"")
				.append(LINE_FEED);
		writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
		writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
		writer.append(LINE_FEED);
		writer.flush();

		byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

		outputStream.write(bytes, 0, bytes.length);
		outputStream.flush();

		writer.append(LINE_FEED);
		writer.flush();
	}

	/**
	 * Get the connection resulted after the parameters are added
	 * 
	 * @return
	 */
	public HttpsURLConnection getFinalizedConnection() {

		writer.append(LINE_FEED).flush();
		writer.append("--" + boundary + "--").append(LINE_FEED);
		writer.close();

		// checks server's status code first
		return httpsConn;
	}
}