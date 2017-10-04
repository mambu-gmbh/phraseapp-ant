package com.mambu.ant.order;

/**
 * @author aifrim.
 */
public final class OrderMessageHelper {

    //oreder message for the translators where details are given
    private static final String ORDER_MESSAGE = "\n" +
            "Dear Translator,\n\n" +

            "This is a request to translate some modules of Mambu's core banking platform. The core module has already been translated, this order involves only some messages that should be added before the release. \n" +
            "Please follow the translation style guide closely. \n" +
            "To get an idea of the application you can login to our test environment at %s with username \"%s\" and password \"%s\". Feel free to click any button and change any data , except for user data.\n" +
            "If you have any questions, please don't hesitate to contact me directly at %s\n\n" +

            "Thanks for your effort! \n" +
            "%s, Mambu GmbH (www.mambu.com)";

    private OrderMessageHelper(){};


    public static String getOrderMessage(String contactEmail, String contactName, String  mambuAppURL,
                                         String mambuUsername, String  mambuPassword) {

        return String.format(ORDER_MESSAGE,mambuAppURL, mambuUsername, mambuPassword, contactEmail, contactName);

    }

}
