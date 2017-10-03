package com.mambu.ant.phraseapp.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.mambu.ant.phraseapp.PhraseApiSettings;

/**
 * @author fgavrilescu
 */
abstract class BaseApi {

	private final Executor executor;
	private final Consumer<String> logger;

	protected BaseApi(PhraseApiSettings settings) {

		HttpHost phraseApiHost = new HttpHost(Constants.PHRASE_HOSTNAME, Constants.PHRASE_PORT, Constants.PHRASE_PROTOCOL);
		Credentials phraseApiCredentials = new UsernamePasswordCredentials(settings.getAuthenticationToken(), "");

		HttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(RequestConfig.custom()
						.setCookieSpec(CookieSpecs.STANDARD).build())
				.build();

		this.logger = settings.getLogger();
		this.executor = Executor.newInstance(httpClient)
				.auth(phraseApiHost, phraseApiCredentials)
				.authPreemptive(phraseApiHost);
	}

	protected HttpResponse invoke(Request request) throws IOException {

		//todo not threadsafe. put locks on the respective API endpoints. the thread safety issue is not that bad
		// although. we might get multiple calls to the same API, all returning rateLimit=0, thus all sleeping

		HttpResponse response = executor.execute(request).returnResponse();

		int rateLimitRemaining = getRateLimitRemaining(response);
		if (rateLimitRemaining == 0) {
			long endOfRateLimitPeriodUnixTime = getRateLimitReset(response);
			long currentUnixTime = System.currentTimeMillis() / 1000L;
			try {
				long sleepTime = endOfRateLimitPeriodUnixTime - currentUnixTime;
				log("Rate limit reached while executing '" + request + "'. Sleeping for '" + sleepTime + "s'.");
				Thread.sleep(TimeUnit.SECONDS.toMillis(sleepTime));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				// todo: throw e
			}
		}

		return response;
	}

	private long getRateLimitReset(HttpResponse httpResponse) {

		Header rateLimitResetHeader = httpResponse.getFirstHeader("X-Rate-Limit-Reset");
		return Long.parseLong(rateLimitResetHeader.getValue());
	}

	private int getRateLimitRemaining(HttpResponse httpResponse) {

		Header rateLimitRemainingHeader = httpResponse.getFirstHeader("X-Rate-Limit-Remaining");
		// not all API calls are rate limited
		if (rateLimitRemainingHeader == null) {
			return 1;
		}
		return Integer.parseInt(rateLimitRemainingHeader.getValue());
	}

	protected String invokeAsString(Request request) throws IOException {

		HttpResponse response = invoke(request);
		return EntityUtils.toString(response.getEntity(), "UTF-8");
	}

	protected void log(String message) {

		logger.accept(message);
	}
}
