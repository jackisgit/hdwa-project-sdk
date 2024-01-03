package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class FastJsonUtil {

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
            return "\"" + sb + "\"";
        }

        if (value instanceof Double) {
            if (((Double) value).isInfinite() || ((Double) value).isNaN()) {
                return "null";
            } else {
                return value.toString();
            }
        }

        if (value instanceof Float) {
            if (((Float) value).isInfinite() || ((Float) value).isNaN()) {
                return "null";
            } else {
                return value.toString();
            }
        }

        if (value instanceof Number) {
            return value.toString();
        }

        if (value instanceof Boolean) {
            return value.toString();
        }

        if (value instanceof JSONObject) {
            JSONObject valueJSON = (JSONObject) value;
            StringBuffer sb = new StringBuffer();
            boolean first = true;

            sb.append('{');
            for (String key : valueJSON.keySet()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }

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
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (Object o : valueJSON) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }

                if (has_enter) {
                    sb.append("\r\n\t");
                }

                if (o == null) {
                    sb.append("null");
                    continue;
                }
                String valueString = toStringInner(o, has_enter);
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
                    if (ch <= '\u001F' || ch >= '\u007F' && ch <= '\u009F' || ch >= '\u2000' && ch <= '\u20FF') {
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
        }
    }

    /**
     * json转换为类
     *
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

            Constructor<?> constructorMethod = entityClass.getConstructor();
            Object targetObject = constructorMethod.newInstance();
            Method[] methodArray = entityClass.getMethods();
            for (Method method : methodArray) {
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

                                method.invoke(targetObject, targetFieldValue);
                            }
                        }
                        {
                            String fieldName = methodName.substring(3);
                            Class<?> paramClass = parameterTypes[0];
                            if (sourceEntity.containsKey(fieldName)) {
                                Object sourceFieldValue = sourceEntity.get(fieldName);
                                Object targetFieldValue = To_JavaObject(sourceFieldValue, paramClass);

                                method.invoke(targetObject, targetFieldValue);
                            }
                        }
                    }
                }
            }

            return targetObject;
        } else if (json instanceof String && "java.lang.String".equals(targetClassName)) {
            return (String) json;
        } else if (json instanceof String && "java.util.Date".equals(targetClassName)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            return sdf.parse((String) json);
        } else if ("java.lang.Long".equals(targetClassName) || "long".equals(targetClassName)) {
            if (json instanceof String) {
                return Long.parseLong((String) json);
            } else if (json instanceof Integer) {
                return ((Integer) json).longValue();
            } else if (json instanceof Long) {
                return (Long) json;
            } else if (json instanceof BigInteger) {
                return ((BigInteger) json).longValue();
            } else {
                return null;
            }
        } else if ("java.lang.Double".equals(targetClassName) || "double".equals(targetClassName)) {
            if (json instanceof String) {
                return Double.parseDouble((String) json);
            } else if (json instanceof Integer) {
                return ((Integer) json).doubleValue();
            } else if (json instanceof Long) {
                return ((Long) json).doubleValue();
            } else if (json instanceof BigInteger) {
                return ((BigInteger) json).doubleValue();
            } else if (json instanceof Float) {
                return ((Float) json).doubleValue();
            } else if (json instanceof Double) {
                return (Double) json;
            } else if (json instanceof BigDecimal) {
                return ((BigDecimal) json).doubleValue();
            } else {
                return null;
            }
        } else if ("java.lang.Boolean".equals(targetClassName) || "boolean".equals(targetClassName)) {
            if (json instanceof String) {
                return "true".equalsIgnoreCase((String) json);
            } else if (json instanceof Boolean) {
                return json;
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
            Map<String, Object> newMap = new HashMap<String, Object>();
            for (String key : sourceJSON.keySet()) {
                Object value = sourceJSON.get(key);
                if (value == null) {
                    continue;
                }
                if (value instanceof Integer) {
                    newMap.put(key, ((Integer) value).longValue());
                } else if (value instanceof BigInteger) {
                    newMap.put(key, ((BigInteger) value).longValue());
                } else if (value instanceof Float) {
                    newMap.put(key, ((Float) value).doubleValue());
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
            for (Object sourceItem : sourceJSON) {
                if ((sourceItem instanceof JSONObject || sourceItem instanceof JSONArray)) {
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
            for (Object o : sourceJSON) {
                result.add(Clone_JSON(o));
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
