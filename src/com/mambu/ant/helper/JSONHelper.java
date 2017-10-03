package com.mambu.ant.helper;

import com.google.gson.Gson;

/**
 * @author aifrim.
 */
public final class JSONHelper {

    private JSONHelper(){}

    public static String toJSON(Object pojo) {

        return new Gson().toJson(pojo);

    }

    public static <T extends Object> T fromJSON(String content, Class<T> clasz) {

       return new Gson().fromJson(content, clasz);

    }
}
