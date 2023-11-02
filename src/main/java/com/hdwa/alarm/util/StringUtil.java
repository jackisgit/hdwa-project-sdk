package com.hdwa.alarm.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static boolean isNull(JSONArray lackFields, JSONObject jsonObject, String... params) {
        lackFields = lackFields == null ? new JSONArray() : lackFields;
        if (jsonObject == null) {
            return false;
        }
        Object value;
        for (String param : params) {
            value = jsonObject.get(param);
            if (value instanceof JSONObject) {
                if (value == null || ((JSONObject) value).isEmpty()) {
                    if (!lackFields.contains(param)) {
                        lackFields.add(param);
                    }
                }
            } else if (value instanceof JSONArray) {
                if (value == null || ((JSONArray) value).isEmpty()) {
                    if (!lackFields.contains(param)) {
                        lackFields.add(param);
                    }
                }
            } else {
                if (value == null || "".equals(JSON.toJSON(value))) {
                    if (!lackFields.contains(param)) {
                        lackFields.add(param);
                    }
                }
            }
        }
        return lackFields.size() != 0;
    }

    /**
     * 判断String是否为null或空
     *
     * @param params
     * @return
     */
    public static boolean isNull(String... params) {
        for (String param : params) {
            if (param == null || "".equals(param)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断JSONObects是否包含parmas中的字段
     *
     * @param jsonObject
     * @param params
     * @return
     */
    public static boolean isNull(JSONObject jsonObject, String... params) {
        for (String param : params) {
            if (isNull(jsonObject.getString(param))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断JSONObects是否包含parmas中的字段
     *
     * @param jsonObject
     * @param params
     * @return
     */
    public static boolean isExist(JSONObject jsonObject, String... params) {
        for (String param : params) {
            if (jsonObject.containsKey(param)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断JSONObect中时候包含parmas中字段的为空数组
     *
     * @param jsonObject
     * @param params
     * @return
     */
    public static boolean isEmptyList(JSONObject jsonObject, String... params) {
        JSONArray jsonArray;
        for (String param : params) {
            jsonArray = jsonObject.getJSONArray(param);
            if (jsonArray == null || jsonArray.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /*
     * Description: 判断字符串中source是否含有字符串specialChars
     * @param source
     * @param specialChars
     * @return boolean
     * @author cuixubin
     * @since 2018年6月14日: 上午10:41:01
     * Update By cuixubin 2018年6月14日: 上午10:41:01
     */
    public static boolean strContainStr(String source, String specialChars) {
        String regEx = "[" + specialChars + "]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(source);
        return m.find();
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 右侧补齐String长度
     *
     * @param str
     * @param length
     * @param sign
     * @return
     */
    public static String completLengthFromRight(String str, int length, String sign) {
        if (str == null) {
            str = "";
        }
        while (str.length() < length) {
            str = str + sign;
        }
        return str;
    }

    /**
     * 随机生成字符串
     *
     * @param length 随机字符串长度
     * @return
     */
    public static String randomString(int length) {
        String baseStr = "abcdefghrgklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVXYZ0123456789";
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(baseStr.length());
            buffer.append(baseStr.charAt(index));
        }
        return buffer.toString();
    }

    /**
     * 根据key返回字符串  无值则返回"" 有则返回值
     *
     * @param jsObject
     * @param key
     * @return
     */
    public static String getJSONString(JSONObject jsObject, String key) {
        String value = jsObject.getString(key);
        if (StringUtils.isNotEmpty(value)) {
            return value;
        }
        return "";
    }

    /**
     * 过滤null null转化为空字符串
     *
     * @param str
     */
    public static String nullChangeEmptyString(String str) {
        return str == null ? "" : str;
    }

    /**
     * String
     * 用于修改时  根据传参检查是否为null
     * 不为null 则赋值  否则不赋值
     *
     * @param setObject
     * @param jsObject
     * @param key
     */
    public static void setUpdateStringVal(JSONObject setObject, JSONObject jsObject, String key) {
        String value = jsObject.getString(key);
        if (value != null) {
            setObject.put(key, value);
        }
    }

    /**
     * Jsonarray
     * 用于修改时  根据传参检查是否为null
     * 不为null 则赋值  否则不赋值
     *
     * @param setObject
     * @param jsObject
     * @param key
     */
    public static void setUpdateArrayVal(JSONObject setObject, JSONObject jsObject, String key) {
        JSONArray value = jsObject.getJSONArray(key);
        if (value != null) {
            setObject.put(key, value);
        }
    }

    /**
     * Jsonobject
     * 用于修改时  根据传参检查是否为null
     * 不为null 则赋值  否则不赋值
     *
     * @param setObject
     * @param jsObject
     * @param key
     */
    public static void setUpdateObjectVal(JSONObject setObject, JSONObject jsObject, String key) {
        JSONObject value = jsObject.getJSONObject(key);
        if (value != null) {
            setObject.put(key, value);
        }
    }

    /**
     * Is json object boolean.
     *
     * @param content the content
     * @return the boolean
     */
    public static boolean isJSONObject(String content) {
        try {
            Object jsobj = JSONObject.parse(content);
            return jsobj instanceof JSONObject;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Is json array boolean.
     *
     * @param content the content
     * @return the boolean
     */
    public static boolean isJSONArray(String content) {
        try {
            Object jsobj = JSONObject.parse(content);
            return jsobj instanceof JSONArray;
        } catch (Exception e) {
            return false;
        }
    }

    public static <T, R> List<T> tranferContentToDTO(String content, Class<T> t) {
        if (StringUtils.isNotBlank(content)) {
            // 把字符串转成json对象
            return JSONArray.parseArray(content, t);
        }
        return new ArrayList<T>();
    }


    public static <T, R> T tranferItemToDTO(String content, Class<T> t) throws Exception {
        if (StringUtils.isNotBlank(content)) {
            return JSONObject.parseObject(content, t);
        }
        return t.newInstance();
    }

    public static String getStringValue(Object value, Object defaultValue) {
        if (value == null) {
            if (defaultValue != null) {
                return defaultValue.toString();
            }
            return "";
        }
        return value.toString();
    }

    public static String getString(Object obj) {
        return getString(obj, "");
    }

    public static String getString(Object obj, String defaultValue) {
        return obj != null ? obj.toString() : defaultValue;
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(Object obj) {
        try {
            Integer.parseInt(String.valueOf(obj));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 获取整数 ，不是则返回0
     */
    public static int getInt(Object obj) {
        return getInt(obj, 0);
    }

    public static int getInt(Object obj, int defaultValue) {
        return (obj != null) && (isInteger(obj)) ? Integer.parseInt(String.valueOf(obj)) : defaultValue;
    }

    /**
     * 判断字符串是否是浮点数
     */
    public static boolean isLong(Object obj) {
        try {
            Long.parseLong(String.valueOf(obj));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是数字
     */
    public static boolean isNumber(Object obj) {
        return isInteger(obj) || isDouble(obj) || isLong(obj);
    }

    /**
     * 获取长整数 ，不是长整数则返回0
     */
    public static long getLong(Object obj) {
        return getLong(obj, 0L);
    }

    public static long getLong(Object obj, long defaultValue) {
        return (obj != null) && (isLong(obj)) ? Long.parseLong(String.valueOf(obj)) : defaultValue;
    }

    /**
     * 判断字符串是否是单精度浮点数
     */
    public static boolean isFloat(Object obj) {
        try {
            Float.parseFloat(String.valueOf(obj));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 获取单精度浮点数
     */
    public static float getFloat(Object obj) {
        return getFloat(obj, 0.0F);
    }

    public static float getFloat(Object obj, float defaultValue) {
        return (obj != null) && (isFloat(obj)) ? Float.parseFloat(String.valueOf(obj)) : defaultValue;
    }

    /**
     * 判断字符串是否是浮点数
     */
    public static boolean isDouble(Object obj) {
        try {
            Double.parseDouble(String.valueOf(obj));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 获取双精度浮点数
     */
    public static double getDouble(Object obj) {
        return getDouble(obj, 0.0D);
    }

    public static double getDouble(Object obj, double defaultValue) {
        return (obj != null) && (isDouble(obj)) ? Double.parseDouble(String.valueOf(obj)) : defaultValue;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if ((obj instanceof CharSequence)) {
            return ((CharSequence) obj).length() == 0;
        }
        if ((obj instanceof Collection)) {
            return CollectionUtils.isEmpty((Collection) obj);
        }
        if ((obj instanceof Map)) {
            return (((Map) obj).isEmpty());
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        /*
         * 和上面的判断数组效果一样，但是不会报空指针 if (obj instanceof Object[]) { return
         * (((Object[])obj).length == 0); }
         */
        return false;
    }

    /**
     * 只有true 或者 "true" 返回 true,否则返回false
     *
     * @param obj
     * @return true or false
     */
    public static boolean getBoolean(Object obj) {
        return !isEmpty(obj) && Boolean.valueOf(obj.toString());
    }
}