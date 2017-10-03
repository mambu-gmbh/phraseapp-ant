package com.mambu.ant.phraseapp.api;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import com.mambu.ant.phraseapp.PhraseApiSettings;

/**
 * Integration with Phrase APP Locales API.
 *
 * @author fgavrilescu
 */
public class LocalesApi extends BaseApi {

	private static final String LOCALES_ENDPOINT = Constants.BASE_ENDPOINT + "/locales";
	private static final String LOCALE_DOWNLOAD_PATH = "/%s/download";

	private final String baseUrl;

	public LocalesApi(PhraseApiSettings settings) {

		super(settings);

		this.baseUrl = String.format(LOCALES_ENDPOINT, settings.getProjectId());
	}

	public String getAll() {

		try {
			String url = new URIBuilder(baseUrl).addParameter("per_page", "100").toString();

			log("Getting all locales using a 'GET' request to URL : " + url);
			return invokeAsString(Request.Get(url));
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}

	}

	public String download(String localeId, String localeCode, String tag) {

		try {
			String url = new URIBuilder(String.format(baseUrl + LOCALE_DOWNLOAD_PATH, localeId))
					.addParameter("file_format", "properties")
					.addParameter("tag", tag)
					.toString();

			log("Getting '" + localeCode + "' translations for '" + tag
					+ "' using 'GET' request to URL: " + url);
			return invokeAsString(Request.Get(url));
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}

	}
}
