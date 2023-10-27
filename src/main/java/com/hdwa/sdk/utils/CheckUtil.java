package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.exception.ExceptionItem;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.scene.DataObjectBase;
import com.hdwa.sdk.entity.scene.DataProperty;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CheckUtil {

    private static void add(List<DataProperty> result, DataProperty item) {
        boolean exist = false;
        for (DataProperty sp : result) {
            if (sp.equals(item)) {
                exist = true;
                break;
            }
        }
        if (!exist) {
            result.add(item);
        }
    }

    private static void addAll(List<DataProperty> result, List<DataProperty> itemList) {
        for (DataProperty sp : itemList) {
            add(result, sp);
        }
    }

    public static List<DataProperty> getPropertyBefore(RepositoryBase Repository, DataProperty dataProperty) throws Exception {
        List<DataProperty> result = new CopyOnWriteArrayList<DataProperty>();
        DataProperty parentTmp = dataProperty;
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

        if (dataProperty.propertyValueType.equals("static")) {
            if (dataProperty.propertyValueSchema.equals("JSONArray")) {
            } else {
                if (Repository.attachproperty2host.containsKey(dataProperty)) {
                    add(result, Repository.attachproperty2host.get(dataProperty));
                }
            }
        } else {
            getPropertyBefore_query(Repository, dataProperty, result);
        }
        return result;
    }

    private static void getPropertyBefore_query(RepositoryBase Repository, DataProperty dataProperty, List<DataProperty> result) throws Exception {
        JSONObject sql_json = JSON.parseObject(dataProperty.querySql);
        String requireSchema = null;
        boolean requireSingleValueSet = false;
        if (dataProperty.propertyValueType.equals("query")) {
            requireSchema = dataProperty.propertyValueSchema;
        }
        List<ExceptionItem> errorList = new CopyOnWriteArrayList<ExceptionItem>();
        ExamineUtil.check_sql(Repository, dataProperty, dataProperty.propertyValueType, requireSchema, requireSingleValueSet, false, false,
                sql_json, errorList);
        if (errorList.size() > 0) {
            throw new ExceptionWrapper(errorList);
        }

        Map<String, Map<String, Boolean>> refList = new ConcurrentHashMap<String, Map<String, Boolean>>();
        query(sql_json, refList);

        for (String refString : refList.keySet()) {
            Map<String, Boolean> columns = refList.get(refString);
            String[] splits = refString.split("'");
            Object parentData;
            if (splits[0].startsWith("ancestor_")) {
                int generate = Integer.parseInt(splits[0].substring("ancestor_".length()));
                parentData = dataProperty;
                while (generate > 0) {
                    if (parentData instanceof DataProperty) {
                        DataProperty tmpProperty = (DataProperty) parentData;
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
                        DataObjectBase tmpObject = (DataObjectBase) parentData;
                        if (Repository.staticobject2host.containsKey(tmpObject)) {
                            parentData = Repository.staticobject2host.get(tmpObject);
                            generate--;
                        } else {
                            throw new Exception("refString ancestor error: " + refString);
                        }
                    }
                }
            } else {
                DataProperty equalSP = null;
                for (DataProperty DataProperty : Repository.dataObjectBase.propertyList) {
                    if (DataProperty.propertyName.equals(splits[0])) {
                        equalSP = DataProperty;
                        break;
                    }
                }
                parentData = equalSP;
            }

            List<DataProperty> spList;
            if (parentData instanceof DataProperty) {
                DataProperty tmpProperty = (DataProperty) parentData;
                spList = getProperty(Repository, tmpProperty, splits, 1);
            } else {
                DataObjectBase tmpObject = (DataObjectBase) parentData;
                spList = getProperty(Repository, tmpObject, splits, 1);
            }
            addAll(result, spList);
            if (columns != null) {
                List<DataProperty> tmpList = new CopyOnWriteArrayList<DataProperty>();
                for (String column : columns.keySet()) {
                    List<DataProperty> attachedInner = get_attached(Repository, spList, column);
                    tmpList.addAll(attachedInner);
                }
                result.addAll(tmpList);
            }
        }
    }

    private static List<DataProperty> getProperty(RepositoryBase Repository, DataObjectBase parentData, String[] splits, int splits_index)
            throws Exception {
        String name = splits[splits_index];
        DataProperty findSP = null;
        for (DataProperty spInner : parentData.propertyList) {
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

    private static List<DataProperty> getProperty(RepositoryBase Repository, DataProperty parentData, String[] splits, int splits_index)
            throws Exception {
        List<DataProperty> result = new CopyOnWriteArrayList<DataProperty>();
        if (parentData.propertyValueType.equals("deamon")) {
            throw new Exception("ref path cant be deamon");
        }
        if (splits_index == splits.length) {
            result.add(parentData);
        } else if (parentData.propertyValueType.equals("static") && parentData.propertyValueSchema.equals("JSONArray")) {
            String split = splits[splits_index];
            int index_ = split.indexOf('=');
            if (index_ != -1) {
                String propertyName = split.substring(0, index_);
                String propertyValue = split.substring(index_ + 1);
                for (DataObjectBase soInner : parentData.staticArray) {
                    boolean matchInner = false;
                    for (DataProperty spInner : soInner.propertyList) {
                        if (spInner.propertyName.equals(propertyName)) {
                            if (spInner.propertyValueType.equals("static")) {
                                matchInner = propertyValue.equals(spInner.staticValue);
                            } else {
                                matchInner = true;
                            }
                        }
                    }
                    if (matchInner) {
                        List<DataProperty> resultInner = getProperty(Repository, soInner, splits, splits_index + 1);
                        result.addAll(resultInner);
                    }
                }
            } else {
                for (DataObjectBase soInner : parentData.staticArray) {
                    DataProperty sp_attach = null;
                    for (DataProperty spInner : soInner.propertyList) {
                        if (spInner.propertyName.equals(splits[splits_index])) {
                            sp_attach = spInner;
                            break;
                        }
                    }
                    if (sp_attach != null) {
                        List<DataProperty> resultInner = getProperty(Repository, soInner, splits, splits_index);
                        result.addAll(resultInner);
                    }
                }
            }
        } else {
            String name = splits[splits_index];
            DataProperty sp_attach = null;
            if (parentData.queryAttached != null) {
                for (DataProperty spInner : parentData.queryAttached) {
                    if (spInner.propertyName.equals(name)) {
                        sp_attach = spInner;
                        break;
                    }
                }
            }
            if (sp_attach != null) {
                result = getProperty(Repository, sp_attach, splits, splits_index + 1);
            } else {
                DataProperty sp_custom = null;
                if (parentData.customObject != null) {
                    for (DataProperty spInner : parentData.customObject.propertyList) {
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

    private static List<DataProperty> get_attached(RepositoryBase Repository, List<DataProperty> startList, String column) throws Exception {
        List<DataProperty> result = new CopyOnWriteArrayList<DataProperty>();
        for (DataProperty spTmp : startList) {
            DataProperty attach_column = null;
            if (spTmp.queryAttached != null) {
                for (DataProperty spTmp2 : spTmp.queryAttached) {
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
                List<DataProperty> parent_ref_Set = get_parent_ref_Set(Repository, spTmp);
                List<DataProperty> resultInner = get_attached(Repository, parent_ref_Set, column);
                result.addAll(resultInner);
            }
        }
        return result;
    }

    private static List<DataProperty> get_parent_ref_Set(RepositoryBase Repository, DataProperty dataProperty) throws Exception {
        List<DataProperty> result = new CopyOnWriteArrayList<DataProperty>();
        if (!dataProperty.propertyValueType.equals("query")) {
            return result;
        }

        JSONObject sql_json = JSON.parseObject(dataProperty.querySql);
        Map<String, Boolean> refMap = get_parent_ref(sql_json);
        for (String refString : refMap.keySet()) {
            String[] splits = refString.split("'");
            Object parentData;
            if (splits[0].startsWith("ancestor_")) {
                int generate = Integer.parseInt(splits[0].substring("ancestor_".length()));
                parentData = dataProperty;
                while (generate > 0) {
                    if (parentData instanceof DataProperty) {
                        DataProperty tmpProperty = (DataProperty) parentData;
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
                        DataObjectBase tmpObject = (DataObjectBase) parentData;
                        if (Repository.staticobject2host.containsKey(tmpObject)) {
                            parentData = Repository.staticobject2host.get(tmpObject);
                            generate--;
                        } else {
                            throw new Exception("refString error: " + refString);
                        }
                    }
                }
            } else {
                DataProperty equalSP = null;
                for (DataProperty DataProperty : Repository.dataObjectBase.propertyList) {
                    if (DataProperty.propertyName.equals(splits[0])) {
                        equalSP = DataProperty;
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
                        DataProperty tmpSP = (DataProperty) tmpObj;
                        if (tmpSP.propertyValueType.equals("static") && tmpSP.propertyValueSchema.equals("JSONArray")) {
                            String propertyName = split.substring(0, index_);
                            String propertyValue = split.substring(index_ + 1);
                            for (DataObjectBase soInner : tmpSP.staticArray) {
                                boolean matchInner = false;
                                for (DataProperty spInner : soInner.propertyList) {
                                    if (spInner.propertyName.equals(propertyName)) {
                                        if (spInner.propertyValueType.equals("static")) {
                                            matchInner = propertyValue.equals(spInner.staticValue);
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
                        if (tmpObj instanceof DataProperty) {
                            DataProperty tmpProperty = (DataProperty) tmpObj;
                            if (tmpProperty.propertyValueType.equals("custom")) {
                                for (DataProperty spInner : tmpProperty.customObject.propertyList) {
                                    if (spInner.propertyName.equals(split)) {
                                        tmpListInner.add(spInner);
                                    }
                                }
                            } else if (tmpProperty.propertyValueType.equals("query") && (tmpProperty.propertyValueSchema.equals("JSONObject")
                                    || tmpProperty.propertyValueSchema.equals("JSONArray"))) {
                                for (DataProperty spInner : tmpProperty.queryAttached) {
                                    if (spInner.propertyName.equals(split)) {
                                        tmpListInner.add(spInner);
                                    }
                                }
                            } else if (tmpProperty.propertyValueType.equals("static") && tmpProperty.propertyValueSchema.equals("JSONArray")) {
                                if (tmpProperty.queryAttached != null) {
                                    for (DataProperty spInner : tmpProperty.queryAttached) {
                                        if (spInner.propertyName.equals(split)) {
                                            tmpListInner.add(spInner);
                                        }
                                    }
                                }
                                for (DataObjectBase soInner : tmpProperty.staticArray) {
                                    for (DataProperty spInner : soInner.propertyList) {
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
                            DataObjectBase tmpObject = (DataObjectBase) tmpObj;
                            for (DataProperty spInner : tmpObject.propertyList) {
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
                DataProperty spInner = (DataProperty) objInner;
                result.add(spInner);
            }
        }
        return result;
    }

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
                            for (Object o : ReturnColumnsArray) {
                                String column = (String) o;
                                columnMap.put(column, true);
                            }
                        }
                    }
                }
                if (sql_json.get("GroupBy") != null) {
                    Object ReturnColumns = sql_json.get("GroupBy");
                    {
                        JSONArray ReturnColumnsArray = (JSONArray) ReturnColumns;
                        for (Object o : ReturnColumnsArray) {
                            String column = (String) o;
                            columnMap.put(column, true);
                        }
                    }
                }
                if (sql_json.get("OrderBy") != null) {
                    Object ReturnColumns = sql_json.get("OrderBy");
                    {
                        JSONArray ReturnColumnsArray = (JSONArray) ReturnColumns;
                        for (Object o : ReturnColumnsArray) {
                            JSONObject columnWrapper = (JSONObject) o;
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
                for (Object criteria : Criterias) {
                    JSONObject CriteriaObjectInner = (JSONObject) criteria;
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
                for (Object criteria : Criterias) {
                    JSONObject CriteriaObjectInner = (JSONObject) criteria;
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
                result.put(refString, new ConcurrentHashMap<String, Boolean>());
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
            for (Object child : children) {
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
