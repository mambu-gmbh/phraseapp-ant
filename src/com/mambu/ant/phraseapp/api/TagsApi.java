package com.mambu.ant.phraseapp.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;

import com.mambu.ant.phraseapp.PhraseApiSettings;

/**
 * @author fgavrilescu
 */
public class TagsApi extends BaseApi {

	private static final String LOCALES_ENDPOINT = Constants.BASE_ENDPOINT + "/tags";

	private final String baseUrl;
	private final Consumer<String> logger;

	public TagsApi(PhraseApiSettings settings) {

		super(settings);

		this.baseUrl = String.format(LOCALES_ENDPOINT, settings.getProjectId());
		this.logger = settings.getLogger();
	}

	public String getAll() {

		try {
			String url = new URIBuilder(baseUrl).addParameter("per_page", "100").toString();

			logger.accept("Getting all tags using a 'GET' request to URL : " + url);
			HttpResponse response = invoke(Request.Get(url)).returnResponse();

			return "";
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}

	}
}
