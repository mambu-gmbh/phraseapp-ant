package com.mambu.ant.phraseapp;

import com.mambu.ant.phraseapp.api.LocalesApi;
import com.mambu.ant.phraseapp.api.TagsApi;

/**
 * The gateway towards PhraseApp API.
 *
 * @author fgavrilescu
 */
public class PhraseApi {

	private final PhraseApiSettings settings;

	public PhraseApi(PhraseApiSettings settings) {

		this.settings = settings;
	}

	public LocalesApi locales() {

		return new LocalesApi(settings);
	}

	public TagsApi tags() {

		return new TagsApi(settings);
	}

}
