package com.mambu.ant.phraseapp;

import java.util.function.Consumer;

/**
 * Global settings used by PhraseApp API integration.
 *
 * @author fgavrilescu
 */
public class PhraseApiSettings {

	private String projectId;
	private String authenticationToken;
	private Consumer<String> logger;

	public String getProjectId() {

		return projectId;
	}
	public void setProjectId(String projectId) {

		this.projectId = projectId;
	}
	public String getAuthenticationToken() {

		return authenticationToken;
	}
	public void setAuthenticationToken(String authenticationToken) {

		this.authenticationToken = authenticationToken;
	}
	public Consumer<String> getLogger() {

		return logger;
	}
	public void setLogger(Consumer<String> logger) {

		this.logger = logger;
	}
}
