package com.mambu.ant.order;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.mambu.ant.BaseTask;
import com.sun.xml.internal.rngom.parse.host.Base;

/**
 * Ant target for ordering translations to PhraseApp
 * @author aifrim.
 */
public class PhraseAppOrder  extends BaseTask {

    private String internationalizations;

    /**
     * Create a  PhraseApp session and for the given list of internationalization locales, oredrs will be created
     */
    @Override
    public void execute() throws BuildException {


    }
}
