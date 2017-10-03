package com.mambu.ant.phraseapp.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import com.mambu.ant.phraseapp.PhraseApiSettings;

/**
 * @author fgavrilescu
 */
public class LocalesApi extends BaseApi {

	private static final String LOCALES_ENDPOINT = Constants.BASE_ENDPOINT + "/locales";

	private final String baseUrl;
	private final Consumer<String> logger;

	public LocalesApi(PhraseApiSettings settings) {

		super(settings);

		this.baseUrl = String.format(LOCALES_ENDPOINT, settings.getProjectId());
		this.logger = settings.getLogger();
	}

	public String getAll() {

		try {
			String url = new URIBuilder(baseUrl).addParameter("per_page", "100").toString();

			logger.accept("Getting all locales using a 'GET' request to URL : " + url);
			return invoke(Request.Get(url))
					.returnContent().asString();
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}

	}
}
