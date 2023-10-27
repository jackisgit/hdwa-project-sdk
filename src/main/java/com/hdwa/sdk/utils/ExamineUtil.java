package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.exception.ExceptionItem;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.scene.DataProperty;

import java.util.List;

public class ExamineUtil {

    public static void check_sql(RepositoryBase Repository, DataProperty dataProperty, String propertyValueType, String requireSchema,
                                 boolean requireMultiValueSet, boolean requireSingleValueSet, boolean isSubQuery, JSONObject sql_json, List<ExceptionItem> errorList)
            throws Exception {
        if (propertyValueType != null && propertyValueType.equals("query")) {
            if (sql_json.containsKey("QueryType")) {
                String QueryType = (String) sql_json.get("QueryType");
                if (isSubQuery && !QueryType.equals("select")) {
                    errorList.add(
                            new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "SubQuery error QueryType: " + QueryType, null));
                }
                if (isSubQuery || QueryType.equals("select")) {
                    String[] keys = {"QueryType", "Target", "Criteria", "UniqueReturnColumn", "ReturnColumns", "Aggregation", "GroupBy", "OrderBy",
                            "Limit"};
                    ExamineAssistUtil.useless(Repository, dataProperty, sql_json, keys, errorList);
                    // 根据requireSchema和requireSingleValueSet检查
                    boolean is_SingleAggregation = sql_json.get("Aggregation") != null && (sql_json.get("Aggregation") instanceof JSONObject);
                    boolean is_MultiAggregation = sql_json.get("Aggregation") != null && (sql_json.get("Aggregation") instanceof JSONArray);
                    boolean is_SingleValueSet = sql_json.get("UniqueReturnColumn") != null;
                    boolean check_UniqueReturnColumn = false;
                    boolean check_ReturnColumns = false;
                    boolean check_SingleAggregation = false;
                    boolean check_MultiAggregation = false;
                    boolean check_GroupBy = false;
                    boolean check_OrderBy = false;
                    boolean check_Limit = false;
                    if (requireSchema == null) {
                        if (is_SingleValueSet) {
                            requireSchema = "JSONArray";
                        } else if (is_SingleAggregation) {
                            requireSchema = "int";
                        } else if (is_MultiAggregation) {
                            if (sql_json.get("GroupBy") == null) {
                                requireSchema = "JSONObject";
                            } else {
                                requireSchema = "JSONArray";
                            }
                        } else {
                            requireSchema = "JSONArray";
                        }
                    }
                    if (requireSchema.equals("JSONArray")) {
                        if (requireSingleValueSet) {
                            // 单值集合
                            if (!is_SingleValueSet) {
                                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                        "SingleValueSet need UniqueReturnColumn", sql_json.toString()));
                            }
                            check_UniqueReturnColumn = true;
                            if (sql_json.get("ReturnColumns") != null) {
                                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "ReturnColumns useless",
                                        sql_json.toString()));
                            }
                        } else if (requireMultiValueSet) {
                            // 多值集合
                            if (is_SingleValueSet) {
                                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                        "MultiValueSet can't UniqueReturnColumn", sql_json.toString()));
                            }
                            check_ReturnColumns = true;
                            if (sql_json.get("UniqueReturnColumn") != null) {
                                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "UniqueReturnColumn useless",
                                        sql_json.toString()));
                            }
                        } else {
                            if (sql_json.get("UniqueReturnColumn") != null) {
                                check_UniqueReturnColumn = true;
                            }
                            if (sql_json.get("ReturnColumns") != null) {
                                check_ReturnColumns = true;
                            }
                        }
                        // 集合
                        if (is_SingleAggregation) {
                            errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "Set can't SingleAggregation",
                                    sql_json.toString()));
                        }
                        // 返回集合的，如果有多聚合，那么必须有GroupBy
                        if (is_MultiAggregation) {
                            if (sql_json.get("GroupBy") == null) {
                                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                        "Set/SingleValueSet with Aggregation need GroupBy", sql_json.toString()));
                            }
                            check_MultiAggregation = true;
                            check_GroupBy = true;
                        }
                        check_OrderBy = true;
                        check_Limit = true;
                    } else if (requireSchema.equals("JSONObject")) {
                        if (sql_json.get("UniqueReturnColumn") != null) {
                            errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "UniqueReturnColumn useless",
                                    sql_json.toString()));
                        }
                        // 集合
                        if (is_SingleAggregation) {
                            errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                    requireSchema + " can't SingleAggregation", sql_json.toString()));
                        }
                        if (is_MultiAggregation) {
                            check_MultiAggregation = true;
                            if (sql_json.get("GroupBy") != null) {
                                check_GroupBy = true;
                                check_OrderBy = true;
                                check_Limit = true;
                            } else {
                                if (sql_json.get("ReturnColumns") != null) {
                                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "ReturnColumns useless",
                                            sql_json.toString()));
                                }
                                if (sql_json.get("OrderBy") != null) {
                                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                            requireSchema + " with Aggregation can't OrderBy", sql_json.toString()));
                                }
                                if (sql_json.get("Limit") != null) {
                                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                            requireSchema + " with Aggregation can't Limit", sql_json.toString()));
                                }
                            }
                        }
                    } else {
                        // 原始值：单聚合或者单值集合
                        if (is_SingleAggregation || is_SingleValueSet) {
                            if (is_SingleAggregation) {
                                check_SingleAggregation = true;
                                if (sql_json.get("GroupBy") != null) {
                                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                            "SingleAggregation can't GroupBy", sql_json.toString()));
                                }
                                if (sql_json.get("OrderBy") != null) {
                                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                            "SingleAggregation can't OrderBy", sql_json.toString()));
                                }
                                if (sql_json.get("Limit") != null) {
                                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                            "SingleAggregation can't Limit", sql_json.toString()));
                                }
                            } else {
                                check_UniqueReturnColumn = true;
                            }
                        } else {
                            errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                    "requireSchema: " + requireSchema + " requireSingleValueSet: " + requireSingleValueSet + "    "
                                            + " is_SingleAggregation: " + is_SingleAggregation + " is_SingleValueSet: " + is_SingleValueSet,
                                    sql_json.toString()));
                        }
                    }

                    if (sql_json.get("UniqueReturnColumn") != null && sql_json.get("ReturnColumns") != null) {
                        errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                "UniqueReturnColumn ReturnColumns is enemy", sql_json.toString()));
                    }
                    ExamineAssistUtil.check_UniqueReturnColumn(Repository, dataProperty, sql_json, errorList, check_UniqueReturnColumn);
                    ExamineAssistUtil.check_ReturnColumns(Repository, dataProperty, sql_json, errorList, check_ReturnColumns);
                    ExamineAssistUtil.check_Aggregation(Repository, dataProperty, sql_json, errorList, check_SingleAggregation,
                            check_MultiAggregation);
                    ExamineAssistUtil.check_GroupBy(Repository, dataProperty, sql_json, errorList, check_GroupBy);
                    ExamineAssistUtil.check_OrderBy(Repository, dataProperty, sql_json, errorList, check_OrderBy);
                    ExamineAssistUtil.check_Limit(Repository, dataProperty, sql_json, errorList, check_Limit);
                    {
                        Object Target = sql_json.get("Target");
                        checkSet(Repository, dataProperty, Target, false, errorList);
                    }
                } else if (QueryType.equals("expression")) {
                    String[] keys = {"QueryType", "Criteria", "expression"};
                    ExamineAssistUtil.useless(Repository, dataProperty, sql_json, keys, errorList);
                    for (String jsonKey : sql_json.keySet()) {
                        if (!jsonKey.equals("QueryType") && !jsonKey.equals("Criteria") && !jsonKey.equals("expression")) {
                            errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "useless key in sql: ", jsonKey));
                        }
                    }
                } else if (QueryType.equals("quote")) {
                    String[] keys = {"QueryType", "Criteria"};
                    ExamineAssistUtil.useless(Repository, dataProperty, sql_json, keys, errorList);
                    if (dataProperty != null
                            && (dataProperty.propertyValueSchema.equals("JSONArray") || dataProperty.propertyValueSchema.equals("JSONObject"))) {
                        errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                "quote propertyValueSchema is " + dataProperty.propertyValueSchema, null));
                    }
                    JSONObject CriteriaObject = (JSONObject) sql_json.get("Criteria");
                    if (CriteriaObject == null) {
                        errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "quote'Criteria is null", null));
                    } else {
                        if (!CriteriaObject.containsKey("quote")) {
                            errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                    "quote'Criteria doesn't contain quote", null));
                        }
                    }
                    for (String jsonKey : sql_json.keySet()) {
                        if (!jsonKey.equals("QueryType") && !jsonKey.equals("Criteria")) {
                            errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "useless key in sql: ", jsonKey));
                        }
                    }
                } else {
                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "error QueryType: " + QueryType,
                            sql_json.toString()));
                }

                Object Criteria = sql_json.get("Criteria");
                if (sql_json.get("Criteria") != null && sql_json.get("Criteria") instanceof JSONObject) {
                    JSONObject CriteriaObject = (JSONObject) sql_json.get("Criteria");
                    checkCriteria(Repository, dataProperty, CriteriaObject, errorList);
                } else {
                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                            "Criteria type error " + (Criteria == null ? "null" : Criteria.toString() + "(" + Criteria.getClass().getName() + ")"),
                            sql_json.toString()));
                }
            } else if (sql_json.containsKey("SetOperator")) {
                checkSet(Repository, dataProperty, sql_json, requireSingleValueSet, errorList);
            } else if (sql_json.get("ref") != null) {
                // 条件中的引用集合
                if (requireSingleValueSet) {
                    // 单值集合
                } else {
                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                            "refString must be SingleValueSet: " + sql_json.toString(), sql_json.toString()));
                }
            } else {
                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "query error: " + sql_json.toString(),
                        sql_json.toString()));
            }
        } else if (propertyValueType != null && propertyValueType.equals("deamon")) {
            String QueryType = (String) sql_json.get("QueryType");
            if (QueryType != null && (QueryType.equals("trend") || QueryType.equals("curve"))) {
                JSONObject CriteriaObject = (JSONObject) sql_json.get("Criteria");
                if (CriteriaObject == null) {
                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "curve'Criteria is null", null));
                } else {
                    if (!CriteriaObject.containsKey("currData")) {
                        errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                QueryType + "'Criteria doesn't contain currData", null));
                    }
                }
            } else {
                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "QueryType error", QueryType));
            }
            for (String jsonKey : sql_json.keySet()) {
                if (!jsonKey.equals("QueryType") && !jsonKey.equals("Criteria")) {
                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "useless key in sql: ", jsonKey));
                }
            }
        } else {
            Object refObject = sql_json.get("ref");
            if (refObject != null && refObject instanceof String) {
            } else {
                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "ref error: " + refObject, sql_json.toString()));
            }
        }
    }

    private static void checkCriteria(RepositoryBase Repository, DataProperty dataProperty, JSONObject CriteriaObject,
                                      List<ExceptionItem> errorList) throws Exception {
        Object LogicOperator = CriteriaObject.get("LogicOperator");
        if (LogicOperator != null) {
            if (LogicOperator instanceof String) {
                String LogicOperatorString = (LogicOperator).toString();
                if (LogicOperatorString.equals("and") || LogicOperatorString.equals("or")) {
                    String[] keys = {"LogicOperator", "Criterias"};
                    ExamineAssistUtil.useless(Repository, dataProperty, CriteriaObject, keys, errorList);
                    Object CriteriasObject = CriteriaObject.get("Criterias");
                    if (CriteriasObject == null || !(CriteriasObject instanceof JSONArray)) {
                        errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "Criterias item error:"
                                + (CriteriasObject == null ? "null" : CriteriasObject.toString() + "(" + CriteriasObject.getClass().getName() + ")"),
                                CriteriaObject.toString()));
                    } else {
                        JSONArray Criterias = (JSONArray) CriteriasObject;
                        for (int i = 0; i < Criterias.size(); i++) {
                            Object CriteriaObjectInner = Criterias.get(i);
                            if (CriteriaObjectInner == null || !(CriteriaObjectInner instanceof JSONObject)) {
                                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                        "CriteriaInner item error:" + (CriteriaObjectInner == null ? "null"
                                                : CriteriaObjectInner.toString() + "(" + CriteriaObjectInner.getClass().getName() + ")"),
                                        CriteriaObject.toString()));
                            } else {
                                JSONObject CriteriaObjectInnerJSON = (JSONObject) CriteriaObjectInner;
                                checkCriteria(Repository, dataProperty, CriteriaObjectInnerJSON, errorList);
                            }
                        }
                        if (LogicOperatorString.equals("and")) {
                        } else {
                        }
                    }
                } else if (LogicOperatorString.equals("not")) {
                    String[] keys = {"LogicOperator", "Criteria"};
                    ExamineAssistUtil.useless(Repository, dataProperty, CriteriaObject, keys, errorList);
                    Object CriteriaObjectInner = CriteriaObject.get("Criteria");
                    if (CriteriaObjectInner == null || !(CriteriaObjectInner instanceof JSONObject)) {
                        errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                "CriteriaInner item error:" + (CriteriaObjectInner == null ? "null"
                                        : CriteriaObjectInner.toString() + "(" + CriteriaObjectInner.getClass().getName() + ")"),
                                CriteriaObject.toString()));
                    } else {
                        JSONObject CriteriaObjectInnerJSON = (JSONObject) CriteriaObjectInner;
                        checkCriteria(Repository, dataProperty, CriteriaObjectInnerJSON, errorList);
                    }
                } else {
                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                            "LogicOperator value error: " + LogicOperatorString, CriteriaObject.toString()));
                }
            } else {
                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                        "LogicOperator type error: " + LogicOperator.getClass().getName(), CriteriaObject.toString()));
            }
        } else {
            for (String itemKey : CriteriaObject.keySet()) {
                Object itemValue = CriteriaObject.get(itemKey);
                if (itemValue instanceof JSONObject) {
                    JSONObject valueInner = (JSONObject) itemValue;
                    if (valueInner.get("ref") != null) {
                        checkCriteriaRef(Repository, dataProperty, valueInner, errorList);
                    } else {
                        for (String itemKeyInner : valueInner.keySet()) {
                            Object itemValueInner = valueInner.get(itemKeyInner);
                            switch (itemKeyInner) {
                                case "e":
                                case "ne":
                                case "gt":
                                case "gte":
                                case "lt":
                                case "lte":
                                case "regex":
                                case "contain":
                                case "startwith":
                                case "array_size":
                                    if (itemValueInner instanceof JSONObject) {
                                        JSONObject valueInnerInner = (JSONObject) itemValueInner;
                                        if (valueInnerInner.get("ref") != null) {
                                            checkCriteriaRef(Repository, dataProperty, valueInnerInner, errorList);
                                        }
                                    }
                                    if (itemKeyInner.equals("regex") || itemKeyInner.equals("contain") || itemKeyInner.equals("startwith")) {
                                        if (itemValueInner == null || !(itemValueInner instanceof String)) {
                                            errorList
                                                    .add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                                            itemKeyInner + " type error: " + (itemValueInner == null ? "null"
                                                                    : itemValueInner.toString() + "(" + itemValueInner.getClass().getName() + ")"),
                                                            itemValue.toString()));
                                        }
                                    } else if (itemKeyInner.equals("array_size")) {
                                        if (itemValueInner == null) {
                                            errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "array_size null",
                                                    itemValue.toString()));
                                        } else {
                                            if (itemValueInner instanceof Integer) {
                                            } else if (itemValueInner instanceof JSONObject) {
                                                JSONObject itemValueInnerJSON = (JSONObject) itemValueInner;
                                                String[] keys = {"e", "ne", "gt", "gte", "lt", "lte"};
                                                ExamineAssistUtil.useless(Repository, dataProperty, itemValueInnerJSON, keys, errorList);
                                                for (String keyInner2 : itemValueInnerJSON.keySet()) {
                                                    Object valueInner2 = itemValueInnerJSON.get(keyInner2);
                                                    if (valueInner2 == null || !(valueInner2 instanceof Integer)) {
                                                        errorList
                                                                .add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                                                        itemKeyInner + " " + keyInner2 + " type error: " + (valueInner2 == null ? "null"
                                                                                : valueInner2.toString() + "(" + valueInner2.getClass().getName() + ")"),
                                                                        itemValue.toString()));
                                                    }
                                                }
                                            } else {
                                                errorList
                                                        .add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                                                itemKeyInner + " type error: " + (itemValueInner == null ? "null"
                                                                        : itemValueInner.toString() + "(" + itemValueInner.getClass().getName() + ")"),
                                                                itemValue.toString()));
                                            }
                                        }
                                    }
                                    break;
                                case "in":
                                case "notin":
                                case "array_e":
                                case "array_ne":
                                case "array_include":
                                case "array_included":
                                case "array_exclude":
                                case "array_intersect":
                                    if (itemValueInner instanceof JSONObject && ((JSONObject) itemValueInner).containsKey("pass")) {
                                    } else {
                                        checkSet(Repository, dataProperty, itemValueInner, true, errorList);
                                    }
                                    break;
                                case "array_elemMatch_exist":
                                case "array_elemMatch_all":
                                    JSONObject itemValueInnerJSON = (JSONObject) itemValueInner;
                                    checkCriteria(Repository, dataProperty, itemValueInnerJSON, errorList);
                                    break;
                                default:
                                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                            "Criteria item key error: " + itemKeyInner, itemValue.toString()));
                                    break;
                            }
                        }
                    }
                } else if (itemValue instanceof JSONArray) {
                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "Criteria item type error: " + "JSONArray",
                            itemValue.toString()));
                }
            }
        }
        return;
    }

    private static void checkCriteriaRef(RepositoryBase Repository, DataProperty dataProperty, JSONObject valueInner, List<ExceptionItem> errorList)
            throws Exception {
        Object refObject = valueInner.get("ref");
        if (!(refObject instanceof String)) {
            errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "ref type error:" + refObject.getClass().getName(),
                    valueInner.toString()));
            return;
        }
        String refString = (String) refObject;
        String[] splits = refString.split("'");
        if (splits[0].startsWith("ancestor_")) {
        } else {
        }
        return;
    }

    private static void checkSet(RepositoryBase Repository, DataProperty dataProperty, Object setDesc, boolean isSingleValueSet,
                                 List<ExceptionItem> errorList) throws Exception {
        if (setDesc instanceof JSONArray) {
            JSONArray setDescArray = (JSONArray) setDesc;
            for (int i = 0; i < setDescArray.size(); i++) {
                Object arrayItem = setDescArray.get(i);
                if (isSingleValueSet) {
                    if (arrayItem != null && (arrayItem instanceof JSONObject || arrayItem instanceof JSONArray)) {
                        errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                "SingleValueSet item error:"
                                        + (arrayItem == null ? "null" : arrayItem.toString() + "(" + arrayItem.getClass().getName() + ")"),
                                arrayItem.toString()));
                    }
                } else {
                    if (arrayItem == null || !(arrayItem instanceof JSONObject)) {
                        errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                "MultiValueSet item error:"
                                        + (arrayItem == null ? "null" : arrayItem.toString() + "(" + arrayItem.getClass().getName() + ")"),
                                arrayItem.toString()));
                    }
                }
            }
            return;
        }

        JSONObject descSet = (JSONObject) setDesc;
        if (descSet.get("Source") != null) {
            String Source = (descSet.get("Source")).toString();
            if (Source.equals("ref")) {
                Object refObject = descSet.get("ref");
                if (refObject == null || !(refObject instanceof String)) {
                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                            "ref type error:" + (refObject == null ? "null" : refObject.toString() + "(" + refObject.getClass().getName() + ")"),
                            descSet.toString()));
                } else {
                    String refString = (String) refObject;
                    checkSetRef(Repository, dataProperty, refString, isSingleValueSet, false, errorList);
                }
            } else {
            }
        } else if (descSet.get("SetOperator") != null) {
            Object SetOperatorObject = descSet.get("SetOperator");
            if (SetOperatorObject == null || !(SetOperatorObject instanceof String)) {
                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "SetOperator type error:"
                        + (SetOperatorObject == null ? "null" : SetOperatorObject.toString() + "(" + SetOperatorObject.getClass().getName() + ")"),
                        descSet.toString()));
            } else {
                String SetOperator = (String) descSet.get("SetOperator");
                if (SetOperator.equals("add") || SetOperator.equals("merge") || SetOperator.equals("unite")) {
                    String[] keys = {"SetOperator", "SetArray"};
                    ExamineAssistUtil.useless(Repository, dataProperty, descSet, keys, errorList);
                    Object SetArrayObject = descSet.get("SetArray");
                    if (SetArrayObject == null || !(SetArrayObject instanceof JSONArray)) {
                        errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "SetArray type error:"
                                + (SetArrayObject == null ? "null" : SetArrayObject.toString() + "(" + SetArrayObject.getClass().getName() + ")"),
                                descSet.toString()));
                    } else {
                        JSONArray SetArray = (JSONArray) SetArrayObject;
                        for (Object SetArrayItem : SetArray) {
                            checkSet(Repository, dataProperty, SetArrayItem, isSingleValueSet, errorList);
                        }
                        if (SetOperator.equals("add")) {
                        } else if (SetOperator.equals("merge")) {
                        } else if (SetOperator.equals("unite")) {
                        }
                    }
                } else if (SetOperator.equals("sub")) {
                    String[] keys = {"SetOperator", "Set1", "Set2"};
                    ExamineAssistUtil.useless(Repository, dataProperty, descSet, keys, errorList);
                    checkSet(Repository, dataProperty, descSet.get("Set1"), isSingleValueSet, errorList);
                    checkSet(Repository, dataProperty, descSet.get("Set2"), isSingleValueSet, errorList);
                }
            }
        } else {
            check_sql(Repository, dataProperty, "query", "JSONArray", !isSingleValueSet, isSingleValueSet, true, descSet, errorList);
        }
    }

    private static void checkSetRef(RepositoryBase Repository, DataProperty dataProperty, String refString, boolean isSingleValueSet,
                                    boolean isDeamon, List<ExceptionItem> errorList) throws Exception {
        String[] splits = refString.split("'");
        if (splits[0].startsWith("ancestor_")) {
        } else {
        }
    }

}
