package com.mambu.ant.order.model;

/**
 * @author aifrim.
 */
public enum TranslationsProvider {

    //for quality available options: standard, pro
    GENGO("gengo", "pro"),

    //for quality available options: regular, premium, enterprise
    TEXT_MASTER("textmaster", "enterprise");

    private final String providerKey;
    private final String defaultQuality;

    TranslationsProvider(String providerKey, String defaultQuality) {

        this.providerKey = providerKey;
        this.defaultQuality = defaultQuality;
    }

    public String getProviderKey() {
        return providerKey;
    }

    public String getDefaultQuality() {
        return defaultQuality;
    }
}
