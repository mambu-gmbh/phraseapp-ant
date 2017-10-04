package com.mambu.ant.order;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;

import com.mambu.ant.BaseTask;
import com.mambu.ant.order.model.RequestCreateOrderModel;
import com.mambu.ant.order.model.ResponseCreateOrderModel;
import com.mambu.ant.order.model.TranslationsProvider;
import com.mambu.ant.phraseapp.PhraseApi;
import com.mambu.ant.phraseapp.PhraseApiSettings;
import com.mambu.ant.styleGuide.model.ResponseStyleGuideLightModel;

/**
 * Ant target for ordering translations to PhraseApp
 *
 * @author aifrim.
 */
public class PhraseAppOrder extends BaseTask {

    private static final String TRANSLATION_SEPARATOR = ",";

    //languages for which the translations should be ordered
    private String gengoTranslations;
    private String textMasterTranslations;

    //data for the email message that will be sent tot the translators
    private String contactEmail;
    private String contactName;
    private String mambuAppURL;
    private String mambuUsername;
    private String mambuPassword;

    /**
     * Phrase APP API integration
     */
    private PhraseApi phraseApi;

    /**
     * Internal test method to check if the Ant task is working
     *
     * @param args ignored
     */
    public static void main(String[] args) {

        PhraseAppOrder order = new PhraseAppOrder();

        order.setContactName("");
        order.setContactEmail("");
        order.setMambuAppURL("");
        order.setMambuUsername("");
        order.setMambuPassword("");

        order.setUserAuthToken("");
        order.setProjectId("");
//        order.setGengoTranslations("Chinese");
//        order.setTextMasterTranslations("Burmese-Myanmar");
        order.setGengoTranslations("Chinese,French,Romanian,Portuguese,Spanish,Indonesian");
        order.setTextMasterTranslations("Burmese-Myanmar,Georgian");

        order.execute();
    }

    /**
     * Create a  PhraseApp session and for the given list of internationalization locales, orders will be created
     */
    @Override
    public void execute() throws BuildException {

        try {

            if (StringUtils.isEmpty(gengoTranslations) && StringUtils.isEmpty(textMasterTranslations)) {

                log("No translations were requested for order. Please fill one of the following parameters: 'gengoTranslations', 'textMasterTranslations");

                return;
            }

            initAPI();

            List<ResponseStyleGuideLightModel> registeredStyleGuides = phraseApi.styleGuides().getAll();

            List<ResponseCreateOrderModel> resultOrderModelList = new ArrayList<>();

            makeOrdersForProvider(TranslationsProvider.GENGO, gengoTranslations, registeredStyleGuides, resultOrderModelList);
            makeOrdersForProvider(TranslationsProvider.TEXT_MASTER, textMasterTranslations, registeredStyleGuides, resultOrderModelList);

        } catch (Exception e) {

            log("An error occurred '" + e.getLocalizedMessage() + "'.");
            e.printStackTrace();

            throw new BuildException(e.getMessage());
        }
    }

    private void initAPI() {

        PhraseApiSettings settings = new PhraseApiSettings();
        settings.setProjectId(projectId);
        settings.setAuthenticationToken(userAuthToken);
        settings.setLogger(this::log);

        phraseApi = PhraseApi.createInstance(settings);
    }

    private void makeOrdersForProvider(TranslationsProvider provider, String input, List<ResponseStyleGuideLightModel> registeredStyleGuides,  List<ResponseCreateOrderModel> resultOrderModelList) {

        if (StringUtils.isEmpty(input)) {

            return;
        }

        String[] translations = StringUtils.split(input, TRANSLATION_SEPARATOR);

        for (String translation : translations) {

            String styleGuideId = getStyleGuideId(registeredStyleGuides, translation);

            RequestCreateOrderModel requestCreateOrderModel = createOrderModel(provider, translation, styleGuideId);

            ResponseCreateOrderModel response = phraseApi.orders().crateOrder(requestCreateOrderModel);
            resultOrderModelList.add(response);
        }

    }

    private String getStyleGuideId(List<ResponseStyleGuideLightModel> registeredStyleGuides, String translation) {

        for (ResponseStyleGuideLightModel responseStyleGuideLightModel : registeredStyleGuides) {

            if (responseStyleGuideLightModel.getTitle().startsWith(translation)) {

                return responseStyleGuideLightModel.getId();
            }

        }

        return null;

    }

    private RequestCreateOrderModel createOrderModel(TranslationsProvider provider, String languageId, String styleGuideId) {

        return RequestCreateOrderModel.newBuilder().lsp(provider.getProviderKey()).source_locale_id(DEFAULT_LOCALE_CODE)
                .target_locale_ids(new String[]{languageId.trim()}).translation_type(provider.getDefaultQuality())
                .message(OrderMessageHelper.getOrderMessage(contactEmail, contactName, mambuAppURL, mambuUsername, mambuPassword))
                .styleguide_id(styleGuideId).category(provider.getCategory()).build();

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

    public void setMambuAppURL(String mambuAppURL) {
        this.mambuAppURL = mambuAppURL;
    }

    public void setMambuUsername(String mambuUsername) {
        this.mambuUsername = mambuUsername;
    }

    public void setMambuPassword(String mambuPassword) {
        this.mambuPassword = mambuPassword;
    }
}
