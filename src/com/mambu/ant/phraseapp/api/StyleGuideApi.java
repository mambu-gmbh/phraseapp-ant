package com.mambu.ant.phraseapp.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import com.google.gson.reflect.TypeToken;
import com.mambu.ant.helper.JSONHelper;
import com.mambu.ant.phraseapp.PhraseApiSettings;
import com.mambu.ant.styleGuide.model.ResponseStyleGuideLightModel;

/**
 * Integration with Phrase APP StyleGuide API.
 *
 * @author aifrim.
 */
public class StyleGuideApi  extends BaseApi{

    private static final String STYLE_GUIDES_ENDPOINT = Constants.BASE_ENDPOINT + "/styleguides";

    private final String baseUrl;

    public StyleGuideApi(PhraseApiSettings settings) {
        super(settings);

        this.baseUrl = String.format(STYLE_GUIDES_ENDPOINT, settings.getProjectId());
    }

    public List<ResponseStyleGuideLightModel> getAll() {


        try {
            String url = new URIBuilder(baseUrl).addParameter("per_page", "100").toString();

            String response = invokeAsString(Request.Get(url));

            Type listType = new TypeToken<List<ResponseStyleGuideLightModel>>(){}.getType();

            List<ResponseStyleGuideLightModel> result = JSONHelper.fromJSON(response, listType);

            return result;

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
