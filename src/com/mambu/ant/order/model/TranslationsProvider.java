package com.mambu.ant.order.model;

/**
 * @author aifrim.
 */
public enum TranslationsProvider {

    //for quality available options: standard, pro
    GENGO("gengo", "pro", null),

    //for quality available options: regular, premium, enterprise
    TEXT_MASTER("textmaster", "enterprise", "C023");

    private final String providerKey;
    private final String defaultQuality;
    /**
     *  Mandatory(for TextMaster). Category to use, more codes can be found here: https://phraseapp.com/docs/api/v2/orders/
     */
    private final String category;

    TranslationsProvider(String providerKey, String defaultQuality, String category) {

        this.providerKey = providerKey;
        this.defaultQuality = defaultQuality;
        this.category = category;
    }

    public String getProviderKey() {
        return providerKey;
    }

    public String getDefaultQuality() {
        return defaultQuality;
    }

    public String getCategory() {
        return category;
    }
}
