package com.mambu.ant.order.model;

import java.util.List;

/**
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

    private List<TargetLocale> target_locales;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TargetLocale> getTarget_locales() {
        return target_locales;
    }

    public void setTarget_locales(List<TargetLocale> target_locales) {
        this.target_locales = target_locales;
    }
}
