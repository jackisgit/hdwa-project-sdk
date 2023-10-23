package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.scene.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author abao
 * @since 2023/7/24
 * 动态接口工具类
 */
public class BaseApiUtil {


    /**
     * Array转List<sdo>
     *
     * @param array
     * @return
     */
    public static List<SceneDataObject> arrayToSdoList(JSONArray array) {
        List<SceneDataObject> result = new ArrayList<>();
        for (Object item : array) {
            JSONObject arrayItem = (JSONObject) item;
            SceneDataObject sod = new SceneDataObject(null, null, null, null, null, null, null);
            for (String kpKey : arrayItem.keySet()) {
                Object kpValue = arrayItem.get(kpKey);
                SceneDataValue svInner = new SceneDataValue(null, sod, kpKey, null);
                svInner.finish = true;
                svInner.value_prim = new SceneDataPrimitive();
                svInner.value_prim.value = kpValue;
                sod.put(kpKey, svInner);
            }
            result.add(sod);
        }
        return result;
    }

    /**
     * Array转List<sdv>
     *
     * @param array
     * @return
     */
    public static List<SceneDataValue> arrayToSdvList(JSONArray array) {
        List<SceneDataValue> result = new CopyOnWriteArrayList<SceneDataValue>();
        for (Object item : array) {
            SceneDataValue svInner = new SceneDataValue(null, null, null, null);
            svInner.finish = true;
            svInner.value_prim = new SceneDataPrimitive();
            svInner.value_prim.value = item;
            result.add(svInner);
        }
        return result;
    }


    /**
     * object转Sdo
     *
     * @param arrayItem
     * @return
     */
    public static SceneDataObject objectToSdo(JSONObject arrayItem) {
        SceneDataObject sod = new SceneDataObject(null, null, null, null, null, null, null);
        for (String kpKey : arrayItem.keySet()) {
            Object kpValue = arrayItem.get(kpKey);
            SceneDataValue svInner = new SceneDataValue(null, sod, kpKey, null);
            svInner.finish = true;
            svInner.value_prim = new SceneDataPrimitive();
            svInner.value_prim.value = kpValue;
            sod.put(kpKey, svInner);
        }
        return sod;
    }

    /**
     * 获取下级所有非custom自定义类型的对象
     *
     * @param object
     * @return
     */
    public static List<SceneProperty> getPropertyListBy(SceneObject object) {
        List<SceneProperty> result = new ArrayList<>();
        for (SceneProperty property : object.propertyList) {
            result.addAll(getPropertyListBy(property));
        }
        return result;
    }

    /**
     * 获取下级所有非custom自定义类型的属性
     *
     * @param property
     * @return
     */
    public static List<SceneProperty> getPropertyListBy(SceneProperty property) {
        List<SceneProperty> result = new ArrayList<>();
        switch (property.propertyValueType) {
            case BaseDecConstant.STATIC:
                result.add(property);
                if (property.propertyValueSchema.equals(BaseDecConstant.JSONARRAY)) {
                    if (property.static_array == null) {
                        property.static_array = new SceneObject[0];
                    }
                    for (SceneObject object : property.static_array) {
                        result.addAll(getPropertyListBy(object));
                    }
                    if (property.query_attached != null) {
                        for (SceneProperty spInner : property.query_attached) {
                            result.addAll(getPropertyListBy(spInner));
                        }
                    }
                }
                break;
            case BaseDecConstant.QUERY:
                result.add(property);
                if (property.query_attached != null) {
                    for (SceneProperty spInner : property.query_attached) {
                        result.addAll(getPropertyListBy(spInner));
                    }
                }
                break;
            case BaseDecConstant.CUSTOM:
                if (property.custom_object == null) {
                    property.custom_object = new SceneObject();
                }
                result = getPropertyListBy(property.custom_object);
                break;
            case BaseDecConstant.DEAMON:
                result.add(property);
                break;

            default:
        }
        return result;
    }

    /**
     * 根据firstTag secondTag 解析成数值
     *
     * @param info
     * @return
     */
    public static int getInfoTypeByTag(SceneDataObject info) {
        String firstTag = info.get(BaseDecConstant.FIRST_TAG) == null ? null : (String) info.get(BaseDecConstant.FIRST_TAG).value_prim.value;
        if (firstTag != null) {
            if (firstTag.contains(BaseDecConstant.RUN_PARAM)) {
                return 1;
            } else if (firstTag.contains(BaseDecConstant.EVENT_RECORD)) {
                return 1;
            } else if (firstTag.contains(BaseDecConstant.SET_PARAM)) {
                return 1;
            }
        }
        //其它就是静态参数，技术参数 等等
        return 0;
    }

    /**
     * 检查是否是运行参数
     *
     * @param infoArray
     * @param code
     * @return
     */
    public static boolean isRunParam(List<SceneDataObject> infoArray, String code) {
        for (SceneDataObject infoJSON : infoArray) {
            if (!infoJSON.get(BaseDecConstant.CODE).value_prim.value.equals(code)) {
                continue;
            }
            if (getInfoTypeByTag(infoJSON) == 1) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检查是否是设定参数
     *
     * @param infoArray
     * @param code
     * @return
     */
    public static boolean isSetParam(List<SceneDataObject> infoArray, String code) {
        for (SceneDataObject infoJSON : infoArray) {
            if (!infoJSON.get(BaseDecConstant.CODE).value_prim.value.equals(code)) {
                continue;
            }
            if (getInfoTypeByTag(infoJSON) == 2) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查点位参数值有效性
     *
     * @param infoValue
     * @return
     */
    public static boolean infoValueIsPoint(String infoValue) {
        int i = infoValue.lastIndexOf('-');
        if (i == -1 || i == 0 || i == infoValue.length() - 1) {
            return false;
        }
        try {
            Integer.parseInt(infoValue.substring(i + 1));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 根据属性名称，基础对象 查询属性
     *
     * @param name
     * @param objectSec
     * @return
     */
    public static SceneProperty getPropertyByName(String name, SceneObject objectSec) {
        SceneProperty base = new SceneProperty();
        for (SceneProperty property : objectSec.getPropertyList()) {
            if (name.equals(property.getPropertyName())) {
                base = property;
                break;
            }
        }
        return base;
    }


    /**
     * 根据属性名称，基础属性 查询属性
     *
     * @param name
     * @param propertySec
     * @return
     */
    public static SceneProperty getPropertyByName(String name, SceneProperty propertySec) {
        SceneProperty SceneProperty = new SceneProperty();
        if (propertySec.getCustom_object() != null) {
            SceneProperty = getPropertyByName(name, propertySec.getCustom_object());
        }
        return SceneProperty;
    }

    /**
     * json转为string
     *
     * @param value
     * @return
     */
    public static String jsonObjectToString(Object value) {
        return toString(value, true);
    }

    /**
     * 字符串解析
     *
     * @param value
     * @param isEnter
     * @return
     */
    private static String toString(Object value, boolean isEnter) {
        //值为空
        if (value == null) {
            return "null";
        }
        //值是字符串类型
        if (value instanceof String) {
            StringBuffer sb = new StringBuffer();
            elude((String) value, sb);
            return "\"" + sb + "\"";
        }
        //值是双精度
        if (value instanceof Double) {
            if (((Double) value).isInfinite() || ((Double) value).isNaN()) {
                return "null";
            } else {
                return value.toString();
            }
        }
        //值是单精度
        if (value instanceof Float) {
            if (((Float) value).isInfinite() || ((Float) value).isNaN()) {
                return "null";
            } else {
                return value.toString();
            }
        }
        //值是数值,值是布尔
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        //值是json对象
        if (value instanceof JSONObject) {
            JSONObject valueJson = (JSONObject) value;
            StringBuffer sb = new StringBuffer();
            boolean first = true;
            sb.append('{');
            for (String key : valueJson.keySet()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                if (isEnter) {
                    sb.append("\r\n\t");
                }
                sb.append('\"');
                elude(key, sb);
                sb.append('\"').append(':');
                //循环解析
                String valueString = toString(valueJson.get(key), isEnter);
                sb.append(valueString.replaceAll("\r\n", "\r\n\t"));
            }
            if (isEnter) {
                sb.append("\r\n");
            }
            sb.append('}');
            return sb.toString();
        }
        //值是json数组
        if (value instanceof JSONArray) {
            JSONArray valueJson = (JSONArray) value;
            boolean first = true;
            StringBuilder sb = new StringBuilder();

            sb.append('[');
            for (Object o : valueJson) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }

                if (isEnter) {
                    sb.append("\r\n\t");
                }

                if (o == null) {
                    sb.append("null");
                    continue;
                }
                //许欢解析
                String valueString = toString(o, isEnter);
                sb.append(valueString.replaceAll("\r\n", "\r\n\t"));
            }
            if (isEnter) {
                sb.append("\r\n");
            }
            sb.append(']');
            return sb.toString();
        }
        return value.toString();
    }


    /**
     * 字符串转义
     *
     * @param str
     * @param stringBuffer
     */
    public static void elude(String str, StringBuffer stringBuffer) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            switch (ch) {
                case '"':
                    stringBuffer.append("\\\"");
                    break;
                case '\\':
                    stringBuffer.append("\\\\");
                    break;
                case '\b':
                    stringBuffer.append("\\b");
                    break;
                case '\f':
                    stringBuffer.append("\\f");
                    break;
                case '\n':
                    stringBuffer.append("\\n");
                    break;
                case '\r':
                    stringBuffer.append("\\r");
                    break;
                case '\t':
                    stringBuffer.append("\\t");
                    break;
                case '/':
                    stringBuffer.append("\\/");
                    break;
                default:
                    if (ch <= '\u001F' || ch >= '\u007F' && ch <= '\u009F' || ch >= '\u2000' && ch <= '\u20FF') {
                        String ss = Integer.toHexString(ch);
                        stringBuffer.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            stringBuffer.append('0');
                        }
                        stringBuffer.append(ss.toUpperCase());
                    } else {
                        stringBuffer.append(ch);
                    }
            }
        }
    }


    /**
     * 获取下级所有非custom类型的SceneProperty
     *
     * @param baseObjectSec
     * @return
     */
    private static List<SceneProperty> getPropertySecAll(SceneObject baseObjectSec) {
        List<SceneProperty> result = new CopyOnWriteArrayList<>();
        for (SceneProperty sec : baseObjectSec.getPropertyList()) {
            result.addAll(getPropertySecAll(sec));
        }
        return result;
    }


    /**
     * 获取下级所有非custom类型的SceneProperty
     *
     * @param propertySec
     * @return
     */
    private static List<SceneProperty> getPropertySecAll(SceneProperty propertySec) {
        List<SceneProperty> result = new CopyOnWriteArrayList<>();
        switch (propertySec.getPropertyValueType()) {
            //静态类型
            case BaseDecConstant.STATIC:
                result.add(propertySec);
                if (propertySec.getPropertyValueSchema().equals(BaseDecConstant.JSONARRAY)) {
                    if (propertySec.getStatic_array() == null) {
                        propertySec.setStatic_array(new SceneObject[0]);
                    }
                    for (SceneObject baseObjectSec : propertySec.getStatic_array()) {
                        result.addAll(getPropertySecAll(baseObjectSec));
                    }
                    if (propertySec.getQuery_attached() != null) {
                        for (SceneProperty sec : propertySec.getQuery_attached()) {
                            result.addAll(getPropertySecAll(sec));
                        }
                    }
                }
                break;
            //查询类型
            case BaseDecConstant.QUERY:
                result.add(propertySec);
                if (propertySec.getQuery_attached() != null) {
                    for (SceneProperty sec : propertySec.getQuery_attached()) {
                        result.addAll(getPropertySecAll(sec));
                    }
                }
                break;
            //自定义类型
            case BaseDecConstant.CUSTOM:
                if (propertySec.getCustom_object() == null) {
                    propertySec.setCustom_object(new SceneObject());
                }
                result = getPropertySecAll(propertySec.getCustom_object());
                break;
            default:
        }
        return result;
    }
}
