package com.mambu.ant.phraseapp;

import com.mambu.ant.phraseapp.api.LocalesApi;
import com.mambu.ant.phraseapp.api.OrdersApi;
import com.mambu.ant.phraseapp.api.StyleGuideApi;
import com.mambu.ant.phraseapp.api.TagsApi;

/**
 * The gateway towards PhraseApp API.
 *
 * @author fgavrilescu
 */
public class PhraseApi {

	private final LocalesApi localesApi;
	private final TagsApi tagsApi;
	private final OrdersApi ordersApi;
	private final StyleGuideApi styleGuideApi;

	private PhraseApi(PhraseApiSettings settings) {

		this.localesApi = new LocalesApi(settings);
		this.tagsApi = new TagsApi(settings);
		this.ordersApi = new OrdersApi(settings);
		this.styleGuideApi = new StyleGuideApi(settings);
	}

	public static PhraseApi createInstance(PhraseApiSettings settings) {

		return new PhraseApi(settings);
	}

	public LocalesApi locales() {

		return localesApi;
	}

	public TagsApi tags() {

		return tagsApi;
	}

	public OrdersApi orders() {

		return ordersApi;
	}

	public StyleGuideApi styleGuides() {

		return styleGuideApi;
	}

}
