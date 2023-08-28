package com.hdwa.sdk.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class KeywordUtil {
    public static Map<String, Boolean> keyWordMap;
    public static Map<String, Boolean> prefixMap;
    public static Map<String, Boolean> suffixMap;
    public static List<String> keyProperty = new CopyOnWriteArrayList<String>();

    static {
        keyProperty.add("id");
        keyProperty.add("名称");
    }

    public static boolean containsKey(String key) {
        if (keyWordMap.containsKey(key)) {
            return true;
        }
        if (prefixMap != null) {
            for (String prefix : prefixMap.keySet()) {
                if (key.startsWith(prefix)) {
                    return true;
                }
            }
        }
        if (suffixMap != null) {
            for (String suffix : suffixMap.keySet()) {
                if (key.endsWith(suffix)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String keyDefault() {
        return keyProperty.get(0);
    }
}
