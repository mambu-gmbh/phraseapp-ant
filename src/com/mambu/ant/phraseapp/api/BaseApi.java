package com.mambu.ant.phraseapp.api;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import com.mambu.ant.phraseapp.PhraseApiSettings;

/**
 * @author fgavrilescu
 */
abstract class BaseApi {

	private final Executor executor;

	protected BaseApi(PhraseApiSettings settings) {

		HttpHost phraseApiHost = new HttpHost(Constants.PHRASE_HOSTNAME, Constants.PHRASE_PORT, Constants.PHRASE_PROTOCOL);
		Credentials phraseApiCredentials = new UsernamePasswordCredentials(settings.getAuthenticationToken(), "");
		this.executor = Executor.newInstance()
				.auth(phraseApiHost, phraseApiCredentials)
				.authPreemptive(phraseApiHost);
	}

	protected Response invoke(Request request) throws IOException {
		// todo: add throttling here
		return executor.execute(request);
	}
}
