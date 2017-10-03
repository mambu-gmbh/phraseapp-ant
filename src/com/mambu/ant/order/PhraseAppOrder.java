package com.mambu.ant.order;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;

import com.mambu.ant.BaseTask;
import com.mambu.ant.order.model.OrderModel;
import com.mambu.ant.order.model.TranslationsProvider;

/**
 * Ant target for ordering translations to PhraseApp
 * @author aifrim.
 */
public class PhraseAppOrder  extends BaseTask {

    private static final String TRANSLATION_SEPARATOR = ",";

    private String gengoTranslations;
    private String textMasterTranslations = "tz,th";
    private String contactEmail;
    private String contactName;

    /**
     * Create a  PhraseApp session and for the given list of internationalization locales, oredrs will be created
     */
    @Override
    public void execute() throws BuildException {

        List<OrderModel> orders = new ArrayList<>();

        if(StringUtils.isEmpty(gengoTranslations) && StringUtils.isEmpty(textMasterTranslations)) {
            log("No translations were requested for order. Please fill one of the following parameters: 'gengoTranslations', 'textMasterTranslations");
        }

    }

    private List<OrderModel> getOrdersForProvider(TranslationsProvider provider, String gengoTranslations) {

        List<OrderModel> result = new ArrayList<>();

        if(StringUtils.isEmpty(gengoTranslations)) {

            return result;
        }

        String[] translations = StringUtils.split(gengoTranslations, TRANSLATION_SEPARATOR);

        for(String translation: translations) {

            String styleGuideId = "someId";

            OrderModel orderModel = createOrderModel(provider, translation, styleGuideId);

            result.add(orderModel);
        }

        return result;

    }

    private OrderModel createOrderModel(TranslationsProvider provider, String languageId, String styleGuideId) {

        return OrderModel.newBuilder().lsp(provider.getProviderKey()).source_locale_id(DEFAULT_LOCALE_CODE)
                .target_locale_ids(new String[] {languageId}).translation_type(provider.getDefaultQuality())
                .message(OrderMessageHelper.getOrderMessage(contactEmail, contactName)).styleguide_id(styleGuideId).build();

    }

    public void setGengoTranslations(String gengoTranslations) {
        this.gengoTranslations = gengoTranslations;
    }

    public void setTextMasterTranslations(String textMasterTranslations) {
        this.textMasterTranslations = textMasterTranslations;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
