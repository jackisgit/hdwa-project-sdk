package com.hdwa.alarm.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class JsonUtil {

    // Convert Java object to JSON string
    public static String toJsonString(Object object) {
        return JSON.toJSONString(object);
    }

    // Parse JSON string to Java object
    public static <T> T parseJson(String jsonString, Class<T> clazz) {
        return JSON.parseObject(jsonString, clazz);
    }

    // Parse JSON string to JSONArray
    public static JSONArray parseJsonArray(String jsonString) {
        return JSON.parseArray(jsonString);
    }

    // Convert Java object to JSONObject
    public static JSONObject convertToJsonObject(Object object) {
        return (JSONObject) JSON.toJSON(object);
    }

    // Convert JSONObject to Java object
    public static <T> T convertToObject(JSONObject jsonObject, Class<T> clazz) {
        return JSON.toJavaObject(jsonObject, clazz);
    }

    // Convert JSON array string to List of Java objects
    public static <T> List<T> jsonToList(String jsonArrayString, Class<T> clazz) {
        return JSON.parseArray(jsonArrayString, clazz);
    }
}