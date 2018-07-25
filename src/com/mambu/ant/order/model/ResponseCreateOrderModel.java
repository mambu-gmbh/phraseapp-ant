package com.mambu.ant.order.model;

import java.util.List;

/**
 * Pojo class used for keeping order responses from phrase app
 *
 * @author aifrim.
 */
public class ResponseCreateOrderModel extends RequestCreateOrderModel {

    public static class TargetLocale {

        private String id;
        private String name;
        private String code;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    /**
     * The id of the order, returned by phraseapp
     */
    private String id;

    /**
     * The target locales for which the order was posted
     */
    private List<TargetLocale> target_locales;

    /**
     * The percentage of the completed order
     */
    private String progress_percent;

    /**
     * The state of the order (completed, in_progress,etc)
     */
    private String state;

    public String getId() {
        return id;
    }

    public List<TargetLocale> getTarget_locales() {
        return target_locales;
    }

    public String getProgress_percent() {
        return progress_percent;
    }

    public String getState() {
        return state;
    }
}
