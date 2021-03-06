package com.mambu.ant.order.model;

/**
 * Pojo class used for posting order requests
 *
 * @author aifrim.
 */
public class RequestCreateOrderModel {

    /**
     * Mandatory. Name of the LSP(translations provider) that should process this order. Can be one of gengo, textmaster.
     */
    private String lsp;

    /**
     * Mandatory. Source locale for the order. Can be the name or public id of the source locale. Preferred is the public id.
     */
    private String source_locale_id;

    /**
     * Mandatory. List of target locales you want the source content translate to. Can be the name or public id of the target locales. Preferred is the public id.
     */
    private String[] target_locale_ids;

    /**
     * Mandatory. Name of the quality level, availability depends on the LSP. Can be one of: standard, pro (for orders processed by Gengo) and one of regular, premium, enterprise (for orders processed by TextMaster)
     */
    private String translation_type;

    /**
     * Optional. Style guide for translators to be sent with the order.
     */
    private String styleguide_id;

    /**
     * Optional. Message that is displayed to the translators for description.
     */
    private String message;

    /**
     * Optional(Default: true). Order translations for keys with untranslated content in the selected target locales.
     */
    private Boolean include_untranslated_keys;

    /**
     * Optional(Default: false). Order translations for keys with unverified content in the selected target locales. Default: false
     */
    private Boolean include_unverified_translations;

    /**
     * Mandatory(for TextMaster). Category to use, more codes can be found here: https://phraseapp.com/docs/api/v2/orders/
     */
    private String category ;//Banking/Financial Services/Insurance

    protected RequestCreateOrderModel(){}

    private RequestCreateOrderModel(Builder builder) {

        lsp = builder.lsp;
        source_locale_id = builder.source_locale_id;
        target_locale_ids = builder.target_locale_ids;
        translation_type = builder.translation_type;
        styleguide_id = builder.styleguide_id;
        message = builder.message;
        include_untranslated_keys = builder.include_untranslated_keys;
        include_unverified_translations = builder.include_unverified_translations;
        category = builder.category;
    }

    public static final class Builder {

        private String lsp;
        private String source_locale_id;
        private String[] target_locale_ids;
        private String translation_type;
        private String styleguide_id;
        private String message;
        private Boolean include_untranslated_keys  = true;
        private Boolean include_unverified_translations  = false;
        private String category;

        private Builder() {
        }

        public Builder lsp(String val) {
            lsp = val;
            return this;
        }

        public Builder source_locale_id(String val) {
            source_locale_id = val;
            return this;
        }

        public Builder target_locale_ids(String[] val) {
            target_locale_ids = val;
            return this;
        }

        public Builder translation_type(String val) {
            translation_type = val;
            return this;
        }

        public Builder styleguide_id(String val) {
            styleguide_id = val;
            return this;
        }

        public Builder message(String val) {
            message = val;
            return this;
        }

        public Builder include_untranslated_keys(Boolean val) {
            include_untranslated_keys = val;
            return this;
        }

        public Builder include_unverified_translations(Boolean val) {
            include_unverified_translations = val;
            return this;
        }

        public Builder category(String val) {
            category = val;
            return this;
        }

        public RequestCreateOrderModel build() {
            return new RequestCreateOrderModel(this);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}
