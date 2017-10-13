package com.mambu.ant.phraseapp.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Base class for integrations with Phrase APP APIs.
 *
 * @author fgavrilescu
 */
abstract class BaseApi {

	private static final String PHRASE_APP_HEADER_RATE_LIMIT_RESET = "X-Rate-Limit-Reset";
	private static final String PHRASE_APP_HEADER_RATE_LIMIT_REMAINING = "X-Rate-Limit-Remaining";

	private static final Pattern LOCK_KEY_PATTERN = Pattern.compile(".*https://api\\.phraseapp\\.com/(api/)?v2/projects/[a-z0-9]+/(?<key>[a-z]+)[/?]?.*");

	// each distinct API will have its own lock, so if the rate limiter is activated for a certain API all other remain unaffected
	private final Map<String, Lock> locks = new ConcurrentHashMap<>();

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


	protected String invokeAsString(Request request) throws IOException {

		HttpResponse response = invoke(request);
		return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
	}

	protected HttpResponse invoke(Request request) throws IOException {

		HttpResponse response = executor.execute(request).returnResponse();
		validateStatus(response);

		// the PhraseAPP API could already be in rate limiter mode, some time before this service was called
		if (isResponseRateLimited(response)) {
			log("Request '%s' was rate limited. Retrying...", request);
			sleepTillRateLimitTimeouts(request, response);
			return invoke(request);
		}

		if (isRateLimitReached(response)) {
			sleepTillRateLimitTimeouts(request, response);
		}

		return response;
	}

	private void validateStatus(HttpResponse response) {

		if (!isResponseRateLimited(response) &&
				!isResponse2xxSuccessful(response)) {
			throw new RuntimeException("Invalid HTTP response returned: " + response.getStatusLine().getStatusCode());
		}
	}

	private boolean isResponseRateLimited(HttpResponse httpResponse) {

		return httpResponse.getStatusLine().getStatusCode() == 429;
	}

	private boolean isResponse2xxSuccessful(HttpResponse httpResponse) {

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		return statusCode >= 200 && statusCode < 300;
	}


	private void sleepTillRateLimitTimeouts(Request request, HttpResponse httpResponse) {

		log("Rate limit reached while executing '%s'.", request);

		executeWithApiLock(request, () -> {
			long endOfRateLimitPeriodUnixTime = getRateLimitReset(httpResponse);
			long currentUnixTime = getCurrentUnixTime();
			long sleepTime = endOfRateLimitPeriodUnixTime - currentUnixTime;
			if (sleepTime > 0) {
				log("Sleeping for '%ds'.", sleepTime);
				try {
					Thread.sleep(TimeUnit.SECONDS.toMillis(sleepTime));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw new RuntimeException(e);
				}
			}
		});

	}

	private void executeWithApiLock(Request request, Runnable execution) {

		String lockKey = initLock(request);
		Lock lock = locks.get(lockKey);
		lock.lock();
		try {
			execution.run();
		} finally {
			lock.unlock();
		}
	}

	private String initLock(Request request) {

		Matcher lockKeyMatcher = LOCK_KEY_PATTERN.matcher(request.toString());
		if (lockKeyMatcher.find()) {
			String key = lockKeyMatcher.group("key");
			locks.putIfAbsent(key, new ReentrantLock());
			return key;
		}
		throw new RuntimeException("Probably the regex is not good enough to extract the lock key");
	}

	private boolean isRateLimitReached(HttpResponse httpResponse) {

		// 2 , because there are maximum 2 threads. it avoids unnecessary hits to PhraseAPP API in cases of high concurrency
		// (because the rate limit headers are read from the response and not known at request time).
		return getRateLimitRemaining(httpResponse) <= 2;
	}

	private long getCurrentUnixTime() {

		return System.currentTimeMillis() / 1000L;
	}

	private long getRateLimitReset(HttpResponse httpResponse) {

		Header rateLimitResetHeader = httpResponse.getFirstHeader(PHRASE_APP_HEADER_RATE_LIMIT_RESET);
		return Long.parseLong(rateLimitResetHeader.getValue());
	}

	private int getRateLimitRemaining(HttpResponse httpResponse) {

		Header rateLimitRemainingHeader = httpResponse.getFirstHeader(PHRASE_APP_HEADER_RATE_LIMIT_REMAINING);
		// not all API calls are rate limited
		if (rateLimitRemainingHeader == null) {
			return Integer.MAX_VALUE;
		}
		return Integer.parseInt(rateLimitRemainingHeader.getValue());
	}

	protected void log(String format, Object... args) {

		logger.accept(String.format(format, args));
	}
}
