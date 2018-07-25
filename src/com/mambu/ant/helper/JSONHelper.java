package com.mambu.ant.helper;

import java.lang.reflect.Type;

import com.google.gson.Gson;

/**
 * Helper class ued for JSON transformations
 *
 * @author aifrim.
 */
public final class JSONHelper {

    private JSONHelper(){}

    /**
     * Convert object to JSON
     */
    public static String toJSON(Object pojo) {

        return new Gson().toJson(pojo);

    }

    /**
     * Convert object from JSON to given type
     */
    public static <T extends Object> T fromJSON(String content, Type type) {

       return new Gson().fromJson(content, type);
    }

    /**
     * Convert object from JSON to given class
     */
    public static <T extends Object> T fromJSON(String content, Class<T> objectClass) {

        return new Gson().fromJson(content, objectClass);

    }
 }
