package com.mambu.ant;

import org.apache.tools.ant.Task;

import com.mambu.ant.phraseapp.PhraseApi;
import com.mambu.ant.phraseapp.PhraseApiSettings;

/**
 * Base task used for keeping the common data between defined ant tasks
 *
 * @author aifrim.
 */
public abstract class BaseTask extends Task {

    protected static final String DEFAULT_LOCALE_CODE = "English";

    /**
     * the auth token of the user who performs the action
     */
    protected String userAuthToken;

    /**
     * Project id from PhraseApp
     */
    protected String projectId;

    /**
     * Phrase APP API integration
     */
    protected PhraseApi phraseApi;


    public void setUserAuthToken(String userAuthToken) {
        this.userAuthToken = userAuthToken;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    protected void initAPI() {

        log("Initialising phrase app API...");

        PhraseApiSettings settings = new PhraseApiSettings();
        settings.setProjectId(projectId);
        settings.setAuthenticationToken(userAuthToken);
        settings.setLogger(this::log);

        phraseApi = PhraseApi.createInstance(settings);
    }


}
