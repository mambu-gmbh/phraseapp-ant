package com.mambu.ant.order.model;

/**
 * @author aifrim.
 */
public class ResponseCreateOrderModel extends RequestCreateOrderModel {

    /**
     * The id of the order, returned by phraseapp
     */
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
