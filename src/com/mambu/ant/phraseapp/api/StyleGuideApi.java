package com.mambu.ant.phraseapp.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import com.mambu.ant.helper.JSONHelper;
import com.mambu.ant.phraseapp.PhraseApiSettings;
import com.mambu.ant.styleGuide.model.StyleGuideLightModel;

/**
 * @author aifrim.
 */
public class StyleGuideApi  extends BaseApi{

    private static final String STYLE_GUIDES_ENDPOINT = Constants.BASE_ENDPOINT + "/styleguides";

    private final String baseUrl;

    protected StyleGuideApi(PhraseApiSettings settings) {
        super(settings);

        this.baseUrl = String.format(STYLE_GUIDES_ENDPOINT, settings.getProjectId());
    }

    public List<StyleGuideLightModel> getAll() {


        try {
            String url = new URIBuilder(baseUrl).addParameter("per_page", "100").toString();
            List<String> tagList = new ArrayList<>();

            HttpResponse response = invoke(Request.Get(url));


        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
