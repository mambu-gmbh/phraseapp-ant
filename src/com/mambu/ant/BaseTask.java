package com.mambu.ant;

import org.apache.tools.ant.Task;

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


    public void setUserAuthToken(String userAuthToken) {
        this.userAuthToken = userAuthToken;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }


}
