package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.exception.ExceptionItem;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.scene.SceneObject;
import com.hdwa.sdk.entity.scene.SceneProperty;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CheckUtil {

    // 检查整个配置文件
    @SuppressWarnings("unused")
    public static JSONObject check(JSONObject sceneJSON_ori) {
        JSONObject result = new JSONObject();
        try {
            RepositoryBase Repository = new RepositoryBase(false, false, 1, 100L);
            SceneObject sceneObject = new SceneObject();
            {
                JSONArray PropertyList_ori = sceneJSON_ori.getJSONArray("PropertyList");
                JSONObject sceneJSON = new JSONObject();
                JSONArray PropertyList = new JSONArray();
                for (int i = 0; i < PropertyList_ori.size(); i++) {
                    JSONObject Property = PropertyList_ori.getJSONObject(i);
                    String PropertyName = Property.getString("PropertyName");
                    if (PropertyName.equals("general_query")) {
                    } else {
                        PropertyList.add(Property);
                    }
                }
                sceneJSON.put("PropertyList", PropertyList);
                FastJsonUtil.Set_JavaObject(sceneJSON, sceneObject);
                Repository.sceneJSON = sceneJSON;
            }
            Repository.sceneObject = sceneObject;
            ComputeUtil.RefreshRepository(Repository);
            List<List<SceneProperty>> spListList = ComputeUtil.computePrepare(Repository);
            result.put("Result", "success");
            return result;
        } catch (ExceptionWrapper e) {
            result.put("Result", "failure");
            result.put("ErrorCode", "1");
            result.put("Message", e.getMessageArray());
            return result;
        } catch (Exception e) {
            result.put("Result", "failure");
            result.put("ErrorCode", "2");
            return result;
        }
    }

    // 确保将item加入result中
    private static void add(List<SceneProperty> result, SceneProperty item) {
        boolean exist = false;
        for (SceneProperty sp : result) {
            if (sp.equals(item)) {
                exist = true;
                break;
            }
        }
        if (!exist) {
            result.add(item);
        }
    }

    // 确保将itemList中的所有元素加入result中
    private static void addAll(List<SceneProperty> result, List<SceneProperty> itemList) {
        for (SceneProperty sp : itemList) {
            add(result, sp);
        }
    }

    public static List<SceneProperty> getPropertyBefore(RepositoryBase Repository, SceneProperty sceneProperty) throws Exception {
        List<SceneProperty> result = new CopyOnWriteArrayList<SceneProperty>();
        // 往上追溯到根节点，其中query类型的都加入result中
        SceneProperty parentTmp = sceneProperty;
        while (true) {
            if (Repository.attachproperty2host.containsKey(parentTmp)) {
                parentTmp = Repository.attachproperty2host.get(parentTmp);
            } else if (Repository.property2customobject.containsKey(parentTmp)) {
                parentTmp = Repository.customobject2host.get(Repository.property2customobject.get(parentTmp));
            } else if (Repository.property2staticobject.containsKey(parentTmp)) {
                parentTmp = Repository.staticobject2host.get(Repository.property2staticobject.get(parentTmp));
            } else {
                parentTmp = null;
            }

            if (parentTmp == null) {
                break;
            }

            if (parentTmp.propertyValueType.equals("query")) {
                add(result, parentTmp);
                break;
            }
        }

        if (sceneProperty.propertyValueType.equals("static")) {
            if (sceneProperty.propertyValueSchema.equals("JSONArray")) {
            } else {
                // 静态附加属性依赖于宿主
                if (Repository.attachproperty2host.containsKey(sceneProperty)) {
                    add(result, Repository.attachproperty2host.get(sceneProperty));
                }
            }
            return result;
        } else {
            // query和deamon类型
            getPropertyBefore_query(Repository, sceneProperty, result);
            return result;
        }
    }

    // propertyValueType是query、deamon
    private static void getPropertyBefore_query(RepositoryBase Repository, SceneProperty sceneProperty, List<SceneProperty> result) throws Exception {
        JSONObject sql_json = JSON.parseObject(sceneProperty.query_sql);
        // Map存储引用项涉及哪些列
        String requireSchema = null;
        boolean requireSingleValueSet = false;
        if (sceneProperty.propertyValueType.equals("query")) {
            requireSchema = sceneProperty.propertyValueSchema;
        }
        List<ExceptionItem> errorList = new CopyOnWriteArrayList<ExceptionItem>();
        ExamineUtil.check_sql(Repository, sceneProperty, sceneProperty.propertyValueType, requireSchema, requireSingleValueSet, false, false,
                sql_json, errorList);
        if (errorList.size() > 0) {
            throw new ExceptionWrapper(errorList);
        }

        Map<String, Map<String, Boolean>> refList = new ConcurrentHashMap<String, Map<String, Boolean>>();
        query(sql_json, refList);
        // 查询目标中的引用为true，其他引用为false
        for (String refString : refList.keySet()) {
            Map<String, Boolean> columns = refList.get(refString);
            String[] splits = refString.split("'");
            Object parentData;
            if (splits[0].startsWith("ancestor_")) {
                int generate = Integer.parseInt(splits[0].substring("ancestor_".length()));
                parentData = sceneProperty;
                while (generate > 0) {
                    if (parentData instanceof SceneProperty) {
                        SceneProperty tmpProperty = (SceneProperty) parentData;
                        if (Repository.attachproperty2host.containsKey(tmpProperty)) {
                            parentData = Repository.attachproperty2host.get(tmpProperty);
                            generate = generate - 2;
                        } else if (Repository.property2customobject.containsKey(tmpProperty)) {
                            parentData = Repository.customobject2host.get(Repository.property2customobject.get(tmpProperty));
                            generate--;
                        } else if (Repository.property2staticobject.containsKey(tmpProperty)) {
                            parentData = Repository.property2staticobject.get(tmpProperty);
                            generate--;
                        } else {
                            throw new Exception("refString ancestor error: " + refString);
                        }
                    } else {
                        SceneObject tmpObject = (SceneObject) parentData;
                        if (Repository.staticobject2host.containsKey(tmpObject)) {
                            parentData = Repository.staticobject2host.get(tmpObject);
                            generate--;
                        } else {
                            throw new Exception("refString ancestor error: " + refString);
                        }
                    }
                }
            } else {
                SceneProperty equalSP = null;
                for (SceneProperty SceneProperty : Repository.sceneObject.propertyList) {
                    if (SceneProperty.propertyName.equals(splits[0])) {
                        equalSP = SceneProperty;
                        break;
                    }
                }
                parentData = equalSP;
            }

            List<SceneProperty> spList;
            if (parentData instanceof SceneProperty) {
                SceneProperty tmpProperty = (SceneProperty) parentData;
                spList = getProperty(Repository, tmpProperty, splits, 1);
            } else {
                SceneObject tmpObject = (SceneObject) parentData;
                spList = getProperty(Repository, tmpObject, splits, 1);
            }
            addAll(result, spList);
            // 查询中引用集合的关联依赖属性
            if (columns != null) {
                List<SceneProperty> tmpList = new CopyOnWriteArrayList<SceneProperty>();
                for (String column : columns.keySet()) {
                    List<SceneProperty> attachedInner = get_attached(Repository, spList, column);
                    tmpList.addAll(attachedInner);
                }
                for (SceneProperty spTmp : tmpList) {
                    result.add(spTmp);
                }
            }
        }
    }

    // 沿著引用集合往下找
    private static List<SceneProperty> getProperty(RepositoryBase Repository, SceneObject parentData, String[] splits, int splits_index)
            throws Exception {
        String name = splits[splits_index];
        SceneProperty findSP = null;
        for (SceneProperty spInner : parentData.propertyList) {
            if (spInner.propertyName.equals(name)) {
                findSP = spInner;
                break;
            }
        }

        if (findSP != null) {
            return getProperty(Repository, findSP, splits, splits_index + 1);
        } else {
            throw new Exception("don't contain " + name);
        }
    }

    // 根据当前所处parent级别获取依赖属性
    private static List<SceneProperty> getProperty(RepositoryBase Repository, SceneProperty parentData, String[] splits, int splits_index)
            throws Exception {
        List<SceneProperty> result = new CopyOnWriteArrayList<SceneProperty>();
        if (parentData.propertyValueType.equals("deamon")) {
            throw new Exception("ref path cant be deamon");
        }
        if (splits_index == splits.length) {
            result.add(parentData);
        } else if (parentData.propertyValueType.equals("static") && parentData.propertyValueSchema.equals("JSONArray")) {
            // 静态数组根据是否有匹配，找下一级Object的对应属性
            String split = splits[splits_index];
            int index_ = split.indexOf('=');
            if (index_ != -1) {
                String propertyName = split.substring(0, index_);
                String propertyValue = split.substring(index_ + 1);
                for (SceneObject soInner : parentData.static_array) {
                    boolean matchInner = false;
                    for (SceneProperty spInner : soInner.propertyList) {
                        if (spInner.propertyName.equals(propertyName)) {
                            if (spInner.propertyValueType.equals("static")) {
                                matchInner = propertyValue.equals(spInner.static_value);
                            } else {
                                matchInner = true;
                            }
                        }
                    }
                    if (matchInner) {
                        List<SceneProperty> resultInner = getProperty(Repository, soInner, splits, splits_index + 1);
                        result.addAll(resultInner);
                    }
                }
            } else {
                for (SceneObject soInner : parentData.static_array) {
                    SceneProperty sp_attach = null;
                    for (SceneProperty spInner : soInner.propertyList) {
                        if (spInner.propertyName.equals(splits[splits_index])) {
                            sp_attach = spInner;
                            break;
                        }
                    }
                    if (sp_attach != null) {
                        List<SceneProperty> resultInner = getProperty(Repository, soInner, splits, splits_index);
                        result.addAll(resultInner);
                    }
                }
            }
        } else {
            String name = splits[splits_index];
            SceneProperty sp_attach = null;
            if (parentData.query_attached != null) {
                for (SceneProperty spInner : parentData.query_attached) {
                    if (spInner.propertyName.equals(name)) {
                        sp_attach = spInner;
                        break;
                    }
                }
            }
            if (sp_attach != null) {
                result = getProperty(Repository, sp_attach, splits, splits_index + 1);
            } else {
                SceneProperty sp_custom = null;
                if (parentData.custom_object != null) {
                    for (SceneProperty spInner : parentData.custom_object.propertyList) {
                        if (spInner.propertyName.equals(name)) {
                            sp_custom = spInner;
                            break;
                        }
                    }
                }
                if (sp_custom != null) {
                    result = getProperty(Repository, sp_custom, splits, splits_index + 1);
                } else if (parentData.propertyValueType.equals("query") && splits_index == splits.length - 1) {
                    result.add(parentData);
                }
            }
        }
        return result;
    }

    // 根据关联column寻找依赖的属性，递归往前找
    private static List<SceneProperty> get_attached(RepositoryBase Repository, List<SceneProperty> startList, String column) throws Exception {
        List<SceneProperty> result = new CopyOnWriteArrayList<SceneProperty>();
        for (SceneProperty spTmp : startList) {
            SceneProperty attach_column = null;
            if (spTmp.query_attached != null) {
                for (SceneProperty spTmp2 : spTmp.query_attached) {
                    if (column.equals(spTmp2.propertyName)) {
                        if (spTmp2.propertyValueType.equals("query")
                                && (!spTmp2.propertyValueSchema.equals("JSONObject") && !spTmp2.propertyValueSchema.equals("JSONArray"))) {
                            attach_column = spTmp2;
                        } else if (spTmp2.propertyValueType.equals("static")
                                && (!spTmp2.propertyValueSchema.equals("JSONObject") && !spTmp2.propertyValueSchema.equals("JSONArray"))) {
                            attach_column = spTmp2;
                        } else {
                            throw new Exception("get_attached error: " + "propertyValueType: " + spTmp2.propertyValueType + "\t"
                                    + "propertyValueSchema: " + spTmp2.propertyValueSchema);
                        }
                        break;
                    }
                }
            }
            if (attach_column != null) {
                result.add(attach_column);
            } else {
                // 往前递归
                List<SceneProperty> parent_ref_Set = get_parent_ref_Set(Repository, spTmp);
                List<SceneProperty> resultInner = get_attached(Repository, parent_ref_Set, column);
                result.addAll(resultInner);
            }
        }
        return result;
    }

    // 寻找集合依赖的集合
    private static List<SceneProperty> get_parent_ref_Set(RepositoryBase Repository, SceneProperty sceneProperty) throws Exception {
        List<SceneProperty> result = new CopyOnWriteArrayList<SceneProperty>();
        if (!sceneProperty.propertyValueType.equals("query")) {
            return result;
        }

        JSONObject sql_json = JSON.parseObject(sceneProperty.query_sql);
        Map<String, Boolean> refMap = get_parent_ref(sql_json);
        for (String refString : refMap.keySet()) {
            String[] splits = refString.split("'");
            Object parentData;
            if (splits[0].startsWith("ancestor_")) {
                int generate = Integer.parseInt(splits[0].substring("ancestor_".length()));
                parentData = sceneProperty;
                while (generate > 0) {
                    if (parentData instanceof SceneProperty) {
                        SceneProperty tmpProperty = (SceneProperty) parentData;
                        if (Repository.attachproperty2host.containsKey(tmpProperty)) {
                            parentData = Repository.attachproperty2host.get(tmpProperty);
                            generate = generate - 2;
                        } else if (Repository.property2customobject.containsKey(tmpProperty)) {
                            parentData = Repository.customobject2host.get(Repository.property2customobject.get(tmpProperty));
                            generate--;
                        } else if (Repository.property2staticobject.containsKey(tmpProperty)) {
                            parentData = Repository.property2staticobject.get(tmpProperty);
                            generate--;
                        } else {
                            throw new Exception("refString error: " + refString);
                        }
                    } else {
                        SceneObject tmpObject = (SceneObject) parentData;
                        if (Repository.staticobject2host.containsKey(tmpObject)) {
                            parentData = Repository.staticobject2host.get(tmpObject);
                            generate--;
                        } else {
                            throw new Exception("refString error: " + refString);
                        }
                    }
                }
            } else {
                SceneProperty equalSP = null;
                for (SceneProperty SceneProperty : Repository.sceneObject.propertyList) {
                    if (SceneProperty.propertyName.equals(splits[0])) {
                        equalSP = SceneProperty;
                        break;
                    }
                }
                parentData = equalSP;
            }

            int splits_index = 1;
            List<Object> tmpList = new CopyOnWriteArrayList<Object>();
            tmpList.add(parentData);
            for (int i = splits_index; i < splits.length; i++) {
                List<Object> tmpListInner = new CopyOnWriteArrayList<Object>();
                String split = splits[i];
                int index_ = split.indexOf('=');
                if (index_ != -1) {
                    for (Object tmpObj : tmpList) {
                        SceneProperty tmpSP = (SceneProperty) tmpObj;
                        if (tmpSP.propertyValueType.equals("static") && tmpSP.propertyValueSchema.equals("JSONArray")) {
                            String propertyName = split.substring(0, index_);
                            String propertyValue = split.substring(index_ + 1);
                            for (SceneObject soInner : tmpSP.static_array) {
                                boolean matchInner = false;
                                for (SceneProperty spInner : soInner.propertyList) {
                                    if (spInner.propertyName.equals(propertyName)) {
                                        if (spInner.propertyValueType.equals("static")) {
                                            matchInner = propertyValue.equals(spInner.static_value);
                                        } else {
                                            matchInner = true;
                                        }
                                    }
                                }
                                if (matchInner) {
                                    tmpListInner.add(soInner);
                                }
                            }

                        } else if (tmpSP.propertyValueType.equals("query") && tmpSP.propertyValueSchema.equals("JSONArray")) {
                            tmpListInner.add(tmpSP);
                        } else {
                            throw new Exception("get_parent_ref_Set error: " + "propertyValueType: " + tmpSP.propertyValueType + "\t"
                                    + "propertyValueSchema: " + tmpSP.propertyValueSchema);
                        }
                    }
                } else {
                    for (Object tmpObj : tmpList) {
                        if (tmpObj instanceof SceneProperty) {
                            SceneProperty tmpProperty = (SceneProperty) tmpObj;
                            if (tmpProperty.propertyValueType.equals("custom")) {
                                for (SceneProperty spInner : tmpProperty.custom_object.propertyList) {
                                    if (spInner.propertyName.equals(split)) {
                                        tmpListInner.add(spInner);
                                    }
                                }
                            } else if (tmpProperty.propertyValueType.equals("query") && (tmpProperty.propertyValueSchema.equals("JSONObject")
                                    || tmpProperty.propertyValueSchema.equals("JSONArray"))) {
                                for (SceneProperty spInner : tmpProperty.query_attached) {
                                    if (spInner.propertyName.equals(split)) {
                                        tmpListInner.add(spInner);
                                    }
                                }
                            } else if (tmpProperty.propertyValueType.equals("static") && tmpProperty.propertyValueSchema.equals("JSONArray")) {
                                if (tmpProperty.query_attached != null) {
                                    for (SceneProperty spInner : tmpProperty.query_attached) {
                                        if (spInner.propertyName.equals(split)) {
                                            tmpListInner.add(spInner);
                                        }
                                    }
                                }
                                for (SceneObject soInner : tmpProperty.static_array) {
                                    for (SceneProperty spInner : soInner.propertyList) {
                                        if (spInner.propertyName.equals(split)) {
                                            tmpListInner.add(spInner);
                                        }
                                    }
                                }
                            } else {
                                throw new Exception("get_parent_ref_Set error: " + "propertyValueType: " + tmpProperty.propertyValueType + "\t"
                                        + "propertyValueSchema: " + tmpProperty.propertyValueSchema);
                            }
                        } else {
                            SceneObject tmpObject = (SceneObject) tmpObj;
                            for (SceneProperty spInner : tmpObject.propertyList) {
                                if (spInner.propertyName.equals(split)) {
                                    tmpListInner.add(spInner);
                                }
                            }
                        }
                    }
                }
                tmpList = tmpListInner;
            }
            for (Object objInner : tmpList) {
                SceneProperty spInner = (SceneProperty) objInner;
                result.add(spInner);
            }
        }
        return result;
    }

    // 根据语句寻找依赖集合
    private static Map<String, Boolean> get_parent_ref(Object obj) throws Exception {
        Map<String, Boolean> columnMap = new ConcurrentHashMap<String, Boolean>();
        if (!(obj instanceof JSONObject)) {
            return columnMap;
        }

        JSONObject sql_json = (JSONObject) obj;
        String QueryType = (String) sql_json.get("QueryType");
        if (QueryType != null) {
            if (QueryType.equals("select")) {
                JSONObject TargetObject = sql_json.getJSONObject("Target");
                if (TargetObject.containsKey("Source")) {
                    String Source = TargetObject.getString("Source");
                    if (Source.equals("ref")) {
                        String ref = TargetObject.getString("ref");
                        columnMap.put(ref, true);
                    }
                } else if (TargetObject.get("SetOperator") != null) {
                    columnMap = get_parent_ref(TargetObject);
                }
            } else {
                throw new Exception("get_parent_ref QueryType error: " + QueryType);
            }
        } else if (sql_json.get("SetOperator") != null) {
            String SetOperator = (sql_json.get("SetOperator")).toString();
            if (SetOperator.equals("add") || SetOperator.equals("merge") || SetOperator.equals("unite")) {
                JSONArray SetArray = (JSONArray) sql_json.get("SetArray");
                for (Object SetArrayItem : SetArray) {
                    Map<String, Boolean> tmpMap = get_parent_ref(SetArrayItem);
                    for (String key : tmpMap.keySet()) {
                        columnMap.put(key, true);
                    }
                }
            } else if (SetOperator.equals("sub")) {
                {
                    Map<String, Boolean> tmpMap = get_parent_ref(sql_json.get("Set1"));
                    for (String key : tmpMap.keySet()) {
                        columnMap.put(key, true);
                    }
                }
                {
                    Map<String, Boolean> tmpMap = get_parent_ref(sql_json.get("Set2"));
                    for (String key : tmpMap.keySet()) {
                        columnMap.put(key, true);
                    }
                }
            }
        }
        return columnMap;
    }

    // 查询依赖的ref集合，以及依赖哪些列
    public static void query(JSONObject sql_json, Map<String, Map<String, Boolean>> result) throws Exception {
        if (sql_json.containsKey("QueryType")) {
            String QueryType = (String) sql_json.get("QueryType");
            if (QueryType.equals("select")) {
                // 查询条件和返回值中的所有列
                Map<String, Boolean> columnMap = new ConcurrentHashMap<String, Boolean>();
                if (sql_json.get("UniqueReturnColumn") != null || sql_json.get("ReturnColumns") != null) {
                    if (sql_json.get("UniqueReturnColumn") != null) {
                        Object UniqueReturnColumn = sql_json.get("UniqueReturnColumn");
                        {
                            columnMap.put((String) UniqueReturnColumn, true);
                        }
                    } else {
                        Object ReturnColumns = sql_json.get("ReturnColumns");
                        {
                            JSONArray ReturnColumnsArray = (JSONArray) ReturnColumns;
                            for (int i = 0; i < ReturnColumnsArray.size(); i++) {
                                String column = (String) ReturnColumnsArray.get(i);
                                columnMap.put(column, true);
                            }
                        }
                    }
                }
                if (sql_json.get("GroupBy") != null) {
                    Object ReturnColumns = sql_json.get("GroupBy");
                    {
                        JSONArray ReturnColumnsArray = (JSONArray) ReturnColumns;
                        for (int i = 0; i < ReturnColumnsArray.size(); i++) {
                            String column = (String) ReturnColumnsArray.get(i);
                            columnMap.put(column, true);
                        }
                    }
                }
                if (sql_json.get("OrderBy") != null) {
                    Object ReturnColumns = sql_json.get("OrderBy");
                    {
                        JSONArray ReturnColumnsArray = (JSONArray) ReturnColumns;
                        for (int i = 0; i < ReturnColumnsArray.size(); i++) {
                            JSONObject columnWrapper = (JSONObject) ReturnColumnsArray.get(i);
                            Object ColumnObject = columnWrapper.get("Column");
                            String column = (String) ColumnObject;
                            columnMap.put(column, true);
                        }
                    }
                }
                {
                    JSONObject CriteriaObject = (JSONObject) sql_json.get("Criteria");
                    parseCriteriaColumn(CriteriaObject, columnMap);
                }
                // 扫描备选集合
                {
                    JSONObject Target = (JSONObject) sql_json.get("Target");
                    Map<String, Map<String, Boolean>> resultInner = parseTarget(Target, columnMap);
                    for (String refString : resultInner.keySet()) {
                        if (!result.containsKey(refString)) {
                            result.put(refString, new ConcurrentHashMap<String, Boolean>());
                        }
                        Map<String, Boolean> columnMapTmp = result.get(refString);
                        Map<String, Boolean> columnMapInner = resultInner.get(refString);
                        for (String tmp : columnMapInner.keySet()) {
                            columnMapTmp.put(tmp, true);
                        }
                    }
                }
            }

            // 扫描查询条件
            JSONObject CriteriaObject = (JSONObject) sql_json.get("Criteria");
            {
                parseCriteria(CriteriaObject, result);
            }
        } else if (sql_json.get("SetOperator") != null) {
            parseSet(sql_json, result);
        }
    }

    private static void parseCriteriaColumn(JSONObject CriteriaObject, Map<String, Boolean> columnMap) throws Exception {
        Object LogicOperator = CriteriaObject.get("LogicOperator");
        if (LogicOperator != null) {
            String LogicOperatorString = (LogicOperator).toString();
            if (LogicOperatorString.equals("and") || LogicOperatorString.equals("or")) {
                JSONArray Criterias = (JSONArray) CriteriaObject.get("Criterias");
                for (int i = 0; i < Criterias.size(); i++) {
                    JSONObject CriteriaObjectInner = (JSONObject) Criterias.get(i);
                    parseCriteriaColumn(CriteriaObjectInner, columnMap);
                }
            } else if (LogicOperatorString.equals("not")) {
                JSONObject CriteriaObjectInner = (JSONObject) CriteriaObject.get("Criteria");
                parseCriteriaColumn(CriteriaObjectInner, columnMap);
            }
        } else {
            for (String itemKey : CriteriaObject.keySet()) {
                columnMap.put(itemKey, true);
            }
        }
    }

    private static void parseCriteria(JSONObject CriteriaObject, Map<String, Map<String, Boolean>> result) throws Exception {
        Object LogicOperator = CriteriaObject.get("LogicOperator");
        if (LogicOperator != null) {
            String LogicOperatorString = (LogicOperator).toString();
            if (LogicOperatorString.equals("and") || LogicOperatorString.equals("or")) {
                JSONArray Criterias = (JSONArray) CriteriaObject.get("Criterias");
                for (int i = 0; i < Criterias.size(); i++) {
                    JSONObject CriteriaObjectInner = (JSONObject) Criterias.get(i);
                    parseCriteria(CriteriaObjectInner, result);
                }
            } else if (LogicOperatorString.equals("not")) {
                JSONObject CriteriaObjectInner = (JSONObject) CriteriaObject.get("Criteria");
                parseCriteria(CriteriaObjectInner, result);
            }
        } else {
            for (String itemKey : CriteriaObject.keySet()) {
                Object itemValue = CriteriaObject.get(itemKey);
                if (itemValue instanceof JSONObject) {
                    JSONObject valueInner = (JSONObject) itemValue;
                    for (String itemKeyInner : valueInner.keySet()) {
                        Object itemValueInner = valueInner.get(itemKeyInner);
                        if (itemKeyInner.equals("ref")) {
                            String refString = (itemValueInner).toString();
                            if (!result.containsKey(refString)) {
                                result.put(refString, new ConcurrentHashMap<String, Boolean>());
                            }
                        } else if (itemKeyInner.equals("in") || itemKeyInner.equals("notin") || itemKeyInner.equals("array_e")
                                || itemKeyInner.equals("array_ne") || itemKeyInner.equals("array_include") || itemKeyInner.equals("array_included")
                                || itemKeyInner.equals("array_exclude") || itemKeyInner.equals("array_intersect")) {
                            parseSet(itemValueInner, result);
                        }
                    }
                }
            }
        }
    }

    private static void parseSet(Object setDesc, Map<String, Map<String, Boolean>> result) throws Exception {
        if (setDesc instanceof JSONArray) {
            return;
        }

        JSONObject descSet = (JSONObject) setDesc;
        if (descSet.get("QueryType") != null) {
            query(descSet, result);
        } else if (descSet.get("SetOperator") != null) {
            String SetOperator = (descSet.get("SetOperator")).toString();
            if (SetOperator.equals("add") || SetOperator.equals("merge") || SetOperator.equals("unite")) {
                JSONArray SetArray = (JSONArray) descSet.get("SetArray");
                for (Object SetArrayItem : SetArray) {
                    parseSet(SetArrayItem, result);
                }
            } else if (SetOperator.equals("sub")) {
                parseSet(descSet.get("Set1"), result);
                parseSet(descSet.get("Set2"), result);
            }
        } else {
            query(descSet, result);
        }
    }

    private static Map<String, Map<String, Boolean>> parseTarget(JSONObject SetDesc, Map<String, Boolean> columnMap) throws Exception {
        Map<String, Map<String, Boolean>> result = new ConcurrentHashMap<String, Map<String, Boolean>>();
        if (SetDesc.containsKey("Source")) {
            String Source = SetDesc.getString("Source");
            if (Source.equals("ref")) {
                String refString = (SetDesc.get("ref")).toString();
                if (!result.containsKey(refString)) {
                    result.put(refString, new ConcurrentHashMap<String, Boolean>());
                }
                Map<String, Boolean> columnMapTmp = result.get(refString);
                for (String tmp : columnMap.keySet()) {
                    columnMapTmp.put(tmp, true);
                }
            }
        } else if (SetDesc.containsKey("QueryType")) {
            query(SetDesc, result);
            for (String key : result.keySet()) {
                Map<String, Boolean> columnMapTmp = result.get(key);
                for (String keyInner : columnMap.keySet()) {
                    columnMapTmp.put(keyInner, true);
                }
            }
        } else if (SetDesc.containsKey("SetOperator")) {
            String SetOperator = SetDesc.getString("SetOperator");
            JSONArray children = new JSONArray();
            if (SetOperator.equals("add") || SetOperator.equals("merge") || SetOperator.equals("unite")) {
                JSONArray SetArray = (JSONArray) SetDesc.get("SetArray");
                children.addAll(SetArray);
            } else if (SetOperator.equals("sub")) {
                Object Set1 = SetDesc.get("Set1");
                Object Set2 = SetDesc.get("Set2");
                children.add(Set1);
                children.add(Set2);
            }
            for (int i = 0; i < children.size(); i++) {
                Object child = children.get(i);
                if (child instanceof JSONObject) {
                    JSONObject TargetInner = (JSONObject) child;
                    Map<String, Map<String, Boolean>> resultInner = parseTarget(TargetInner, columnMap);
                    for (String refString : resultInner.keySet()) {
                        if (!result.containsKey(refString)) {
                            result.put(refString, new ConcurrentHashMap<String, Boolean>());
                        }
                        Map<String, Boolean> columnMapTmp = result.get(refString);
                        Map<String, Boolean> columnMapInner = resultInner.get(refString);
                        for (String column : columnMapInner.keySet()) {
                            columnMapTmp.put(column, true);
                        }
                    }
                }
            }
        }
        return result;
    }
}
