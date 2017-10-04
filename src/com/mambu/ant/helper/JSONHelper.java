package com.mambu.ant.helper;

import java.lang.reflect.Type;

import com.google.gson.Gson;

/**
 * @author aifrim.
 */
public final class JSONHelper {

    private JSONHelper(){}

    public static String toJSON(Object pojo) {

        return new Gson().toJson(pojo);

    }

    public static <T extends Object> T fromJSON(String content, Type type) {

       return new Gson().fromJson(content, type);
    }

    public static <T extends Object> T fromJSON(String content, Class<T> objectClass) {

        return new Gson().fromJson(content, objectClass);

    }
 }
