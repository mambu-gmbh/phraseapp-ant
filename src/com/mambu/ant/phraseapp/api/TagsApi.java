package com.mambu.ant.phraseapp.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;

import com.mambu.ant.phraseapp.PhraseApiSettings;

/**
 * Integration with Phrase APP Tags API.
 *
 * @author fgavrilescu
 */
public class TagsApi extends BaseApi {

	private static final String LOCALES_ENDPOINT = Constants.BASE_ENDPOINT + "/tags";

	private static final Pattern NEXT_LINK_REL = Pattern.compile("(.*)<(?<url>.*)>; rel=next(.*)");

	private final String baseUrl;

	public TagsApi(PhraseApiSettings settings) {

		super(settings);

		this.baseUrl = String.format(LOCALES_ENDPOINT, settings.getProjectId());
	}

	public List<String> getAll() {

		try {
			String url = new URIBuilder(baseUrl).addParameter("per_page", "100").toString();
			List<String> tagList = new ArrayList<>();
			load(url, tagList);
			return tagList;
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}

	}

	private void load(String url, List<String> accumulator) throws IOException {

		log("Getting all tags using a 'GET' request to URL: %s", url);
		HttpResponse response = invoke(Request.Get(url));
		accumulator.add(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
		String nextPageUrl = getNextPageUrl(response);
		if (nextPageUrl != null) {
			load(nextPageUrl, accumulator);
		}
	}

	private String getNextPageUrl(HttpResponse response) {

		Header linksHeader = response.getFirstHeader("Link");
		if (linksHeader == null) {
			return null;
		}

		Matcher matcher = NEXT_LINK_REL.matcher(linksHeader.getValue());
		if (matcher.find()) {
			return matcher.group("url");
		}

		return null;
	}


}
