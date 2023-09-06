package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FastJsonUtil {
    public static Set<Object> ValueArray2ValueSet(JSONArray valueArray) {
        Set<Object> valueSet = new HashSet<Object>();
        for (int index_va = 0; index_va < valueArray.size(); index_va++) {
            Object valueItem = valueArray.get(index_va);
            valueSet.add(valueItem);
        }
        return valueSet;
    }

    public static JSONArray ValueSet2ValueArray(Set<Object> valueSet) {
        JSONArray valueArray = new JSONArray();
        Iterator<Object> valueIter = valueSet.iterator();
        while (valueIter.hasNext()) {
            Object valueItem = valueIter.next();
            valueArray.add(valueItem);
        }
        return valueArray;
    }

    public static Set<Object> ValueSet_or(List<Set<Object>> valueSetList) {
        Set<Object> result = new HashSet<Object>();
        for (int i = 0; i < valueSetList.size(); i++) {
            Set<Object> valueSet = valueSetList.get(i);
            result.addAll(valueSet);
        }
        return result;
    }

    public static Set<Object> ValueSet_and(List<Set<Object>> valueSetList) {
        Set<Object> result = new HashSet<Object>();
        Set<Object> first = valueSetList.get(0);
        Iterator<Object> valueIter = first.iterator();
        while (valueIter.hasNext()) {
            Object valueItem = valueIter.next();
            boolean all_in = true;
            for (int i = 1; i < valueSetList.size(); i++) {
                Set<Object> valueSet = valueSetList.get(i);
                if (!valueSet.contains(valueItem)) {
                    all_in = false;
                    break;
                }
            }
            if (all_in) {
                result.add(valueItem);
            }
        }
        return result;
    }

    public static Set<Object> ValueSet_sub(Set<Object> valueSet1, Set<Object> valueSet2) {
        Set<Object> result = new HashSet<Object>();
        Iterator<Object> valueIter = valueSet1.iterator();
        while (valueIter.hasNext()) {
            Object valueItem = valueIter.next();
            if (!valueSet2.contains(valueItem)) {
                result.add(valueItem);
            }
        }
        return result;
    }

    public static String toFormatString(Object value) {
        return toStringInner(value, true);
    }

    public static String toString(Object value) {
        return toStringInner(value, false);
    }

    private static String toStringInner(Object value, boolean has_enter) {
        if (value == null) {
            return "null";
        }

        if (value instanceof String) {
            StringBuffer sb = new StringBuffer();
            escape((String) value, sb);
            return "\"" + sb.toString() + "\"";
        }

        if (value instanceof Double) {
            if (((Double) value).isInfinite() || ((Double) value).isNaN()) {
                return "null";
            } else {
                return value.toString();
            }
        }

        if (value instanceof Float) {
            if (((Float) value).isInfinite() || ((Float) value).isNaN())
                return "null";
            else
                return value.toString();
        }

        if (value instanceof Number)
            return value.toString();

        if (value instanceof Boolean)
            return value.toString();

        if (value instanceof JSONObject) {
            JSONObject valueJSON = (JSONObject) value;
            StringBuffer sb = new StringBuffer();
            boolean first = true;

            sb.append('{');
            for (String key : valueJSON.keySet()) {
                if (first)
                    first = false;
                else
                    sb.append(',');

                if (has_enter) {
                    sb.append("\r\n\t");
                }

                sb.append('\"');
                escape(key, sb);
                sb.append('\"').append(':');
                String valueString = toStringInner(valueJSON.get(key), has_enter);
                sb.append(valueString.replaceAll("\r\n", "\r\n\t"));
            }
            if (has_enter) {
                sb.append("\r\n");
            }
            sb.append('}');
            return sb.toString();
        }

        if (value instanceof JSONArray) {
            JSONArray valueJSON = (JSONArray) value;
            boolean first = true;
            StringBuffer sb = new StringBuffer();

            sb.append('[');
            for (int i = 0; i < valueJSON.size(); i++) {
                if (first)
                    first = false;
                else
                    sb.append(',');

                if (has_enter) {
                    sb.append("\r\n\t");
                }

                Object valueInner = valueJSON.get(i);
                if (valueInner == null) {
                    sb.append("null");
                    continue;
                }
                String valueString = toStringInner(valueInner, has_enter);
                sb.append(valueString.replaceAll("\r\n", "\r\n\t"));
            }
            if (has_enter) {
                sb.append("\r\n");
            }
            sb.append(']');
            return sb.toString();

        }

        return value.toString();
    }

    public static void escape(String s, StringBuffer sb) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    // Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        } // for
    }

    public static Long getLong(JSONObject json, String name) throws Exception {
        if (json.containsKey(name)) {
            Object item = json.get(name);
            if (item instanceof Integer) {
                return ((Integer) item).longValue();
            } else if (item instanceof Long) {
                return ((Long) item).longValue();
            } else if (item instanceof BigInteger) {
                return ((BigInteger) item).longValue();
            } else {
                throw new Exception("FastJsonUtil: " + "JSON property " + name + " cant Cast to Long:"
                        + JSONObject.toJSONString(json, SerializerFeature.WriteMapNullValue));
            }
        } else {
            return null;
        }
    }

    public static Long getDouble(JSONObject json, String name) throws Exception {
        if (json.containsKey(name)) {
            Object item = json.get(name);
            if (item instanceof Integer) {
                return ((Integer) item).longValue();
            } else if (item instanceof Long) {
                return ((Long) item).longValue();
            } else if (item instanceof BigInteger) {
                return ((BigInteger) item).longValue();
            } else if (item instanceof Float) {
                return ((Float) item).longValue();
            } else if (item instanceof Double) {
                return ((Double) item).longValue();
            } else if (item instanceof BigDecimal) {
                return ((BigDecimal) item).longValue();
            } else {
                throw new Exception("FastJsonUtil: " + "JSON property " + name + " cant Cast to Double:"
                        + JSONObject.toJSONString(json, SerializerFeature.WriteMapNullValue));
            }
        } else {
            return null;
        }
    }

    public static void Set_JSON(Object entity, JSONObject json) throws Exception {
        Class<?> targetClass = entity.getClass();

        Method[] targetMethodArray = targetClass.getMethods();
        for (int i = 0; i < targetMethodArray.length; i++) {
            Method method = targetMethodArray[i];
            int modifiers = method.getModifiers();
            String methodName = method.getName();
            if (modifiers == 1 && methodName.startsWith("get")) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 0) {
                    String fieldName = methodName.substring(3);
                    fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                    if (json.containsKey(fieldName)) {
                        json.remove(fieldName);
                    }
                    Object value = method.invoke(entity, new Object[]{});
                    if (value != null) {
                        String valueClassName = value.getClass().getName();
                        Object put_value = null;
                        if (valueClassName.equals("java.lang.String")) {
                            put_value = value;
                        } else if (valueClassName.equals("java.util.Date")) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                            put_value = sdf.format((Date) value);
                        } else if (valueClassName.equals("java.lang.Long") || valueClassName.equals("long")) {
                            put_value = value;
                        } else if (valueClassName.equals("java.lang.Double") || valueClassName.equals("double")) {
                            put_value = value;
                        } else if (valueClassName.equals("java.lang.Boolean") || valueClassName.equals("boolean")) {
                            put_value = value;
                        } else {
                            put_value = To_JSON(value);
                        }

                        json.put(fieldName, put_value);
                    }
                }
            }
        }
    }

    /**
     * json转换为类
     * @param json
     * @param entity
     * @throws Exception
     */
    public static void setJava(JSONObject json, Object entity) throws Exception {
        Class<?> targetClass = entity.getClass();
        if (json == null) {
            return;
        }
        Method[] targetMethodArray = targetClass.getMethods();
        for (Method method : targetMethodArray) {
            int modifiers = method.getModifiers();
            String methodName = method.getName();
            if (modifiers == 1 && methodName.startsWith("set")) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1) {
                    // 自适应大小写
                    {
                        String fieldName = methodName.substring(3);
                        fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                        Class<?> paramClass = parameterTypes[0];
                        if (json.containsKey(fieldName)) {
                            Object sourceFieldValue = json.get(fieldName);
                            Object targetFieldValue = To_JavaObject(sourceFieldValue, paramClass);

                            method.invoke(entity, targetFieldValue);
                        }
                    }
                    {
                        String fieldName = methodName.substring(3);
                        Class<?> paramClass = parameterTypes[0];
                        if (json.containsKey(fieldName)) {
                            Object sourceFieldValue = json.get(fieldName);
                            Object targetFieldValue = To_JavaObject(sourceFieldValue, paramClass);

                            method.invoke(entity, targetFieldValue);
                        }
                    }
                }
            }
        }
    }

    public static Object To_JSON(Object source) throws Exception {
        Class<?> sourceClass = source.getClass();
        if (sourceClass.isArray()) {
            JSONArray result = new JSONArray();
            int array_length = Array.getLength(source);
            for (int i = 0; i < array_length; i++) {
                Object item = Array.get(source, i);
                result.add(To_JSON(item));
            }
            return result;
        } else {
            JSONObject result = new JSONObject();
            Method[] meethodArray = sourceClass.getMethods();
            for (int i = 0; i < meethodArray.length; i++) {
                Method method = meethodArray[i];
                int modifiers = method.getModifiers();
                String methodName = method.getName();
                if (modifiers == 1 && methodName.startsWith("get")) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 0) {
                        String fieldName = methodName.substring(3);
                        fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                        Object value = method.invoke(source, new Object[]{});
                        Object put_value;
                        if (value == null) {
                            put_value = null;
                        } else {
                            String valueClassName = value.getClass().getName();
                            if (valueClassName.equals("java.lang.String")) {
                                put_value = value;
                            } else if (valueClassName.equals("java.util.Date")) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                                put_value = sdf.format(value);
                            } else if (valueClassName.equals("java.lang.Long") || valueClassName.equals("long")) {
                                put_value = value;
                            } else if (valueClassName.equals("java.lang.Double") || valueClassName.equals("double")) {
                                put_value = value;
                            } else if (valueClassName.equals("java.lang.Boolean") || valueClassName.equals("boolean")) {
                                put_value = value;
                            } else {
                                put_value = To_JSON(value);
                            }
                        }
                        result.put(fieldName, put_value);
                    }
                }
            }
            return result;
        }
    }

    public static Object To_JavaObject(Object json, Class<?> entityClass) throws Exception {
        if (json == null) {
            return null;
        }

        String targetClassName = entityClass.getName();
        if (json instanceof JSONArray && entityClass.isArray()) {
            JSONArray sourceEntity = (JSONArray) json;
            int sourceLength = sourceEntity.size();

            Class<?> targetComponentType = entityClass.getComponentType();
            Object targetObjectArray = Array.newInstance(targetComponentType, sourceLength);

            for (int i = 0; i < sourceLength; i++) {
                Object sourceObject = sourceEntity.get(i);

                Object targetObject = To_JavaObject(sourceObject, targetComponentType);
                Array.set(targetObjectArray, i, targetObject);
            }

            return targetObjectArray;
        } else if (json instanceof JSONObject) {
            JSONObject sourceEntity = (JSONObject) json;

            Constructor<?> constructorMethod = entityClass.getConstructor(new Class<?>[]{});
            Object targetObject = constructorMethod.newInstance(new Object[]{});
            Method[] methodArray = entityClass.getMethods();
            for (int i = 0; i < methodArray.length; i++) {
                Method method = methodArray[i];
                int modifiers = method.getModifiers();
                String methodName = method.getName();
                if (modifiers == 1 && methodName.startsWith("set")) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 1) {
                        // 自适应大小写
                        {
                            String fieldName = methodName.substring(3);
                            fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                            Class<?> paramClass = parameterTypes[0];
                            if (sourceEntity.containsKey(fieldName)) {
                                Object sourceFieldValue = sourceEntity.get(fieldName);
                                Object targetFieldValue = To_JavaObject(sourceFieldValue, paramClass);

                                method.invoke(targetObject, new Object[]{targetFieldValue});
                            }
                        }
                        {
                            String fieldName = methodName.substring(3);
                            Class<?> paramClass = parameterTypes[0];
                            if (sourceEntity.containsKey(fieldName)) {
                                Object sourceFieldValue = sourceEntity.get(fieldName);
                                Object targetFieldValue = To_JavaObject(sourceFieldValue, paramClass);

                                method.invoke(targetObject, new Object[]{targetFieldValue});
                            }
                        }
                    }
                }
            }

            return targetObject;
        } else if (json instanceof String && targetClassName.equals("java.lang.String")) {
            return (String) json;
        } else if (json instanceof String && targetClassName.equals("java.util.Date")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            return sdf.parse((String) json);
        } else if (targetClassName.equals("java.lang.Long") || targetClassName.equals("long")) {
            if (json instanceof String) {
                Long value = Long.parseLong((String) json);
                return value;
            } else if (json instanceof Integer) {
                return ((Integer) json).longValue();
            } else if (json instanceof Long) {
                return ((Long) json).longValue();
            } else if (json instanceof BigInteger) {
                return ((BigInteger) json).longValue();
            } else {
                return null;
            }
        } else if (targetClassName.equals("java.lang.Double") || targetClassName.equals("double")) {
            if (json instanceof String) {
                Double value = Double.parseDouble((String) json);
                return value;
            } else if (json instanceof Integer) {
                return ((Integer) json).doubleValue();
            } else if (json instanceof Long) {
                return ((Long) json).doubleValue();
            } else if (json instanceof BigInteger) {
                return ((BigInteger) json).doubleValue();
            } else if (json instanceof Float) {
                return ((Float) json).doubleValue();
            } else if (json instanceof Double) {
                return ((Double) json).doubleValue();
            } else if (json instanceof BigDecimal) {
                return ((BigDecimal) json).doubleValue();
            } else {
                return null;
            }
        } else if (targetClassName.equals("java.lang.Boolean") || targetClassName.equals("boolean")) {
            if (json instanceof String) {
                Boolean value = ((String) json).equalsIgnoreCase("true") ? true : false;
                return value;
            } else if (json instanceof Boolean) {
                Boolean value = (Boolean) json;
                return value;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static void Normalize(Object source) {
        if (source instanceof JSONObject) {
            JSONObject sourceJSON = (JSONObject) source;
            Map<String, Object> newMap = new ConcurrentHashMap<String, Object>();
            for (String key : sourceJSON.keySet()) {
                Object value = sourceJSON.get(key);
                if (value == null) {
                    continue;
                }
                if (value instanceof Integer) {
                    newMap.put(key, ((Integer) value).longValue());
                } else if (value instanceof Long) {
                } else if (value instanceof BigInteger) {
                    newMap.put(key, ((BigInteger) value).longValue());
                } else if (value instanceof Float) {
                    newMap.put(key, ((Float) value).doubleValue());
                } else if (value instanceof Double) {
                } else if (value instanceof BigDecimal) {
                    newMap.put(key, ((BigDecimal) value).doubleValue());
                } else if (value instanceof JSONObject || value instanceof JSONArray) {
                    Normalize(value);
                }
            }
            for (String key : newMap.keySet()) {
                Object value = newMap.get(key);
                sourceJSON.remove(key);
                sourceJSON.put(key, value);
            }
        } else if (source instanceof JSONArray) {
            JSONArray sourceJSON = (JSONArray) source;
            for (int i = 0; i < sourceJSON.size(); i++) {
                Object sourceItem = sourceJSON.get(i);
                if (sourceItem != null && (sourceItem instanceof JSONObject || sourceItem instanceof JSONArray)) {
                    Normalize(sourceItem);
                }
            }
        }
    }

    public static Object Clone_JSON(Object source) {
        if (source instanceof JSONObject) {
            JSONObject result = new JSONObject();
            JSONObject sourceJSON = (JSONObject) source;
            for (String key : sourceJSON.keySet()) {
                Object value = sourceJSON.get(key);
                result.put(key, Clone_JSON(value));
            }
            return result;
        } else if (source instanceof JSONArray) {
            JSONArray result = new JSONArray();
            JSONArray sourceJSON = (JSONArray) source;
            for (int i = 0; i < sourceJSON.size(); i++) {
                result.add(Clone_JSON(sourceJSON.get(i)));
            }
            return result;
        } else if (source instanceof String) {
            return source;
        } else if (source instanceof Double) {
            return source;
        } else if (source instanceof Long) {
            return source;
        } else if (source instanceof Boolean) {
            return source;
        }

        return null;
    }
}
