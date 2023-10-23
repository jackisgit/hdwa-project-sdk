package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataPrimitive;
import com.hdwa.sdk.entity.scene.SceneDataSet;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class FilterUtil {

    public static JSONObject postPage(RepositoryBase repository, JSONObject paramObject) {
        JSONObject result = new JSONObject();
        try {
            JSONArray path = (JSONArray) paramObject.get("path");
            JSONObject params = (JSONObject) paramObject.get("params");
            List<SceneDataObject> array = filter(repository, path, params);
            int pageSize = Integer.MAX_VALUE;
            int pageIndex = 0;
            if (paramObject.containsKey("page")) {
                pageSize = (Integer) paramObject.get("pageSize");
                pageIndex = (Integer) paramObject.get("pageIndex");
                result.put("pageSize", pageSize);
                result.put("pageIndex", pageIndex);
                int pageCount = (array.size() + pageSize - 1) / pageSize;
                result.put("pageCount", pageCount);
            }
            SceneDataValue sdv = new SceneDataValue(null, null, null, null);
            sdv.value_array = new SceneDataSet(false);
            sdv.value_array.set = new CopyOnWriteArrayList<SceneDataObject>();
            for (int i = pageSize * pageIndex; i < pageSize * (pageIndex + 1) && i < array.size(); i++) {
                sdv.value_array.set.add(array.get(i));
            }
            JSONArray content = (JSONArray) sdv.toJSON(true, -1);

            result.put("content", content);
            result.put("contentCount", content.size());

            result.put("count", array.size());
            result.put("result", "success");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("result", "failure");
        }
        return result;
    }

    private static List<SceneDataObject> filter(RepositoryBase Repository, JSONArray path, JSONObject params) throws Exception {
        SceneDataValue valueObject = (SceneDataValue) CalculateApiJsonUtil.getValueObject(Repository, path);

        RecursiveUtil.refreshObject(Repository, valueObject);

        String filter_rule = valueObject.rel_property.filter_rule;
        if (filter_rule == null || filter_rule.trim().length() == 0) {
            JSONObject sql_json = new JSONObject();
            sql_json.put("QueryType", "select");
            sql_json.put("Criteria", new JSONObject());
            filter_rule = JSONObject.toJSONString(sql_json, SerializerFeature.WriteMapNullValue);
        }
        JSONObject CriteriaObject = JSON.parseObject(filter_rule).getJSONObject("Criteria");
        List<String> refPropertyList = new CopyOnWriteArrayList<String>();
        JSONObject CriteriaNew = (JSONObject) parseCriteria(CriteriaObject, params);

        SceneDataSet targetSet = new SceneDataSet(false);
        targetSet.set = new CopyOnWriteArrayList<>();
        for (SceneDataObject sdb : valueObject.value_array.set) {
            if (sdb != null) {
                targetSet.set.add(sdb);
            }
        }
        SceneDataObject parentData = new SceneDataObject(null, null, null, null, null, null, null);
        for (String key : params.keySet()) {
            Object value = params.get(key);
            SceneDataValue svInner = new SceneDataValue(null, parentData, key, null);
            if (value instanceof JSONArray) {
                svInner.value_array = new SceneDataSet(true);
                svInner.value_array.singleValueSet = new CopyOnWriteArrayList<SceneDataValue>();
                JSONArray valueArray = (JSONArray) value;
                for (Object valueItem : valueArray) {
                    SceneDataValue svInner2 = new SceneDataValue(null, null, null, null);
                    svInner2.value_prim = new SceneDataPrimitive();
                    svInner2.value_prim.value = valueItem;
                    svInner.value_array.singleValueSet.add(svInner2);
                }
            } else {
                svInner.value_prim = new SceneDataPrimitive();
                svInner.value_prim.value = value;
            }
            parentData.put(key, svInner);
        }
        SceneDataValue sv = new SceneDataValue(Repository, parentData, valueObject.rel_property.propertyName, null);
        JSONObject sql_json = JSON.parseObject(filter_rule);
        sql_json.put("Criteria", CriteriaNew);
        SceneDataSet array = (SceneDataSet) QueryUtil.select_node(Repository, sv, sql_json, targetSet);

        return array.set;
    }

    public static Object parseCriteria(JSONObject CriteriaObject, JSONObject params) throws Exception {
        JSONObject pass = new JSONObject();
        pass.put("pass", true);
        boolean is_ref_ancestor_1 = false;
        Object ref_value = null;
        if (CriteriaObject.containsKey("ref")) {
            String refString = CriteriaObject.getString("ref");
            String[] splits = refString.split("'");
            if (splits[0].equals("ancestor_1")) {
                is_ref_ancestor_1 = true;
                ref_value = params.getOrDefault(splits[1], pass);
            }
        }
        if (is_ref_ancestor_1) {
            return ref_value;
        } else {
            JSONObject result = new JSONObject();
            for (String itemKey : CriteriaObject.keySet()) {
                Object itemValue = CriteriaObject.get(itemKey);
                if (itemValue instanceof JSONObject) {
                    Object valueNew = parseCriteria((JSONObject) itemValue, params);
                    result.put(itemKey, valueNew);
                } else if (itemValue instanceof JSONArray) {
                    JSONArray itemValueArray = (JSONArray) itemValue;
                    JSONArray itemValueNew = new JSONArray();
                    for (Object itemValueArrayItem : itemValueArray) {
                        if (itemValueArrayItem instanceof JSONObject) {
                            Object valueNew = parseCriteria((JSONObject) itemValueArrayItem, params);
                            itemValueNew.add(valueNew);
                        } else {
                            itemValueNew.add(itemValueArrayItem);
                        }
                    }
                    result.put(itemKey, itemValueNew);
                } else {
                    result.put(itemKey, itemValue);
                }
            }
            return result;
        }
    }
}
