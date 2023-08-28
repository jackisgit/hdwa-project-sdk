package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataPrimitive;
import com.hdwa.sdk.entity.scene.SceneDataSet;
import com.hdwa.sdk.entity.scene.SceneDataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RWDUtil {

    public static boolean infoValue_is_point(String infoValue) {
        int index_ = infoValue.lastIndexOf('-');
        if (index_ == -1 || index_ == 0 || index_ == infoValue.length() - 1) {
            return false;
        }

        String funcid = infoValue.substring(index_ + 1);
        try {
            Integer.parseInt(funcid);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static SceneDataObject object2sod(JSONObject arrayItem) {
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

    public static void array2SDV(JSONArray array, SceneDataValue sv) {
        sv.value_array = new SceneDataSet(false);
        sv.value_array.set = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject arrayItem = (JSONObject) array.get(i);
            SceneDataObject sod = new SceneDataObject(null, null, null, null, null, null, null);
            for (String kpKey : arrayItem.keySet()) {
                Object kpValue = arrayItem.get(kpKey);
                SceneDataValue svInner = new SceneDataValue(null, sod, kpKey, null);
                svInner.finish = true;
                svInner.value_prim = new SceneDataPrimitive();
                svInner.value_prim.value = kpValue;
                sod.put(kpKey, svInner);
            }
            sv.value_array.set.add(sod);
        }
    }

    /**
     * Array转List<SceneDataObject>
     *
     * @param array
     * @return
     */
    public static List<SceneDataObject> array2SDOList(JSONArray array) {
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

    public static List<SceneDataValue> array2SDVList(JSONArray array) {
        List<SceneDataValue> result = new CopyOnWriteArrayList<SceneDataValue>();
        for (int i = 0; i < array.size(); i++) {
            Object item = array.get(i);
            SceneDataValue svInner = new SceneDataValue(null, null, null, null);
            svInner.finish = true;
            svInner.value_prim = new SceneDataPrimitive();
            svInner.value_prim.value = item;
            result.add(svInner);
        }
        return result;
    }

    public static int getInfoType(SceneDataObject infoJSON) {
        String firstTag = infoJSON.get("firstTag") == null ? null : (String) infoJSON.get("firstTag").value_prim.value;
        String secondTag = infoJSON.get("secondTag") == null ? null : (String) infoJSON.get("secondTag").value_prim.value;
        if (firstTag != null) {
            if (firstTag.contains("运行参数")) {
                return 1;
            } else if (firstTag.contains("事件记录")) {
                if (secondTag != null && secondTag.contains("报警消息")) {
                    return 1;
                } else {
                    return 1;
                }
            } else if (firstTag.contains("设定参数")) {
                if (secondTag != null && secondTag.contains("设定反馈值")) {
                    return 1;
                } else {
                    return 2;
                }
            }
        }
        return 0;
    }

    public static boolean isRunParam(List<SceneDataObject> infoArray, String code) {
        for (int i = 0; i < infoArray.size(); i++) {
            SceneDataObject infoJSON = infoArray.get(i);
            if (!infoJSON.get("code").value_prim.value.equals(code)) {
                continue;
            }
            if (getInfoType(infoJSON) == 1) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSetParam(List<SceneDataObject> infoArray, String code) {
        for (int i = 0; i < infoArray.size(); i++) {
            SceneDataObject infoJSON = infoArray.get(i);
            if (!infoJSON.get("code").value_prim.value.equals(code)) {
                continue;
            }
            if (getInfoType(infoJSON) == 2) {
                return true;
            }
        }
        return false;
    }
}
