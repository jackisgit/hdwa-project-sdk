package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.exception.ExceptionItem;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.scene.DataProperty;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ExamineAssistUtil {
    public static void check_UniqueReturnColumn(RepositoryBase Repository, DataProperty dataProperty, JSONObject sql_json,
                                                List<ExceptionItem> errorList, boolean check_UniqueReturnColumn) throws Exception {
        if (check_UniqueReturnColumn) {
            if (sql_json.get("UniqueReturnColumn") != null) {
                Object UniqueReturnColumn = sql_json.get("UniqueReturnColumn");
                if (!(UniqueReturnColumn instanceof String)) {
                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                            "UniqueReturnColumn type error: " + UniqueReturnColumn.getClass().getName(), sql_json.toString()));
                }
            }
        }
    }

    public static void check_ReturnColumns(RepositoryBase Repository, DataProperty dataProperty, JSONObject sql_json, List<ExceptionItem> errorList,
                                           boolean check_ReturnColumns) throws Exception {
        if (check_ReturnColumns) {
            if (sql_json.get("ReturnColumns") != null) {
                Object ReturnColumns = sql_json.get("ReturnColumns");
                if (ReturnColumns instanceof JSONArray) {
                    JSONArray ReturnColumnsArray = (JSONArray) ReturnColumns;
                    for (Object itemObject : ReturnColumnsArray) {
                        if (itemObject instanceof String) {
                        } else {
                            errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                    "ReturnColumns item error:"
                                            + (itemObject == null ? "null" : itemObject.toString() + "(" + itemObject.getClass().getName() + ")"),
                                    sql_json.toString()));
                        }
                    }
                } else {
                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                            "ReturnColumns type error: " + ReturnColumns.getClass().getName(), sql_json.toString()));
                }
            }
        }
    }

    public static void check_Aggregation(RepositoryBase Repository, DataProperty dataProperty, JSONObject sql_json, List<ExceptionItem> errorList,
                                         boolean check_SingleAggregation, boolean check_MultiAggregation) throws Exception {
        if (sql_json.get("Aggregation") != null) {
            Object Aggregation = sql_json.get("Aggregation");
            if (Aggregation instanceof JSONObject || Aggregation instanceof JSONArray) {
                JSONArray itemList = new JSONArray();
                if (Aggregation instanceof JSONObject) {
                    JSONObject AggregationJSON = (JSONObject) Aggregation;
                    if (check_SingleAggregation) {
                        itemList.add(AggregationJSON);
                    }
                } else {
                    JSONArray AggregationArray = (JSONArray) Aggregation;
                    if (check_MultiAggregation) {
                        for (Object itemObject : AggregationArray) {
                            if (itemObject instanceof JSONObject) {
                                itemList.add(itemObject);
                            } else {
                                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                        "Aggregation item error:"
                                                + (itemObject == null ? "null" : itemObject + "(" + itemObject.getClass().getName() + ")"),
                                        sql_json.toString()));
                            }
                        }
                    }
                }
                String[] keys = {"Function", "Column", "Name"};
                duplicate(Repository, dataProperty, itemList, keys, errorList);
                useless(Repository, dataProperty, itemList, keys, errorList);
                for (Object o : itemList) {
                    JSONObject item = (JSONObject) o;
                    Object FunctionObject = item.get("Function");
                    if (FunctionObject instanceof String) {
                        String Function = (String) FunctionObject;
                        if (Function.equals("count")) {
                            if (item.get("Column") != null) {
                                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                        "Aggregation item count can't has Column", sql_json.toString()));
                            }
                        } else if (Function.equals("sum") || Function.equals("avg") || Function.equals("max") || Function.equals("min")
                                || Function.equals("equal_value")) {
                            Object itemObject = item.get("Column");
                            if (!(itemObject instanceof String)) {
                                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                        "Aggregation item need Column: "
                                                + (itemObject == null ? "null" : itemObject + "(" + itemObject.getClass().getName() + ")"),
                                        sql_json.toString()));
                            }
                        } else {
                            errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                    "Aggregation item Function error: " + Function, sql_json.toString()));
                        }
                        if (check_SingleAggregation) {
                            if (item.get("Name") != null) {
                                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                        "SingleAggregation item can't has Name", sql_json.toString()));
                            }
                        } else {
                            Object itemObject = item.get("Name");
                            if (!(itemObject instanceof String)) {
                                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                        "Aggregation item need Name: "
                                                + (itemObject == null ? "null" : itemObject + "(" + itemObject.getClass().getName() + ")"),
                                        sql_json.toString()));
                            }
                        }
                    } else {
                        errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "Aggregation item Function type error:"
                                + (FunctionObject == null ? "null" : FunctionObject + "(" + FunctionObject.getClass().getName() + ")"),
                                sql_json.toString()));
                    }
                }
            } else {
                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                        "Aggregation type error: " + Aggregation.getClass().getName(), sql_json.toString()));
            }
        }
    }

    public static void check_GroupBy(RepositoryBase Repository, DataProperty dataProperty, JSONObject sql_json, List<ExceptionItem> errorList,
                                     boolean check_GroupBy) throws Exception {
        if (sql_json.get("GroupBy") != null) {
            if (check_GroupBy) {
                Object GroupBy = sql_json.get("GroupBy");
                if (GroupBy instanceof JSONArray) {
                    JSONArray ReturnColumnsArray = (JSONArray) GroupBy;
                    for (Object columnWrapper : ReturnColumnsArray) {
                        if (!(columnWrapper instanceof String)) {
                            errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "GroupBy item error:"
                                    + (columnWrapper == null ? "null" : columnWrapper + "(" + columnWrapper.getClass().getName() + ")"),
                                    sql_json.toString()));
                        }
                    }
                } else {
                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                            "GroupBy type error:" + GroupBy.getClass().getName(), sql_json.toString()));
                }
            }
        }
    }

    public static void check_OrderBy(RepositoryBase Repository, DataProperty dataProperty, JSONObject sql_json, List<ExceptionItem> errorList,
                                     boolean check_OrderBy) throws Exception {
        if (sql_json.get("OrderBy") != null) {
            if (check_OrderBy) {
                Object OrderBy = sql_json.get("OrderBy");
                if (OrderBy instanceof JSONArray) {
                    JSONArray ReturnColumnsArray = (JSONArray) OrderBy;
                    String[] keys = {"Column", "Asc"};
                    String[] keys_dup = {"Column"};
                    duplicate(Repository, dataProperty, ReturnColumnsArray, keys_dup, errorList);
                    useless(Repository, dataProperty, ReturnColumnsArray, keys, errorList);
                    for (Object columnWrapperObject : ReturnColumnsArray) {
                        if (columnWrapperObject instanceof JSONObject) {
                            JSONObject columnWrapper = (JSONObject) columnWrapperObject;
                            Object ColumnObject = columnWrapper.get("Column");
                            Object AscObject = columnWrapper.get("Asc");
                            if (ColumnObject instanceof String && AscObject instanceof Boolean) {
                            } else {
                                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                        "OrderBy item error:" + columnWrapper, sql_json.toString()));
                            }
                        } else {
                            errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                                    "OrderBy item error:" + (columnWrapperObject == null ? "null"
                                            : columnWrapperObject + "(" + columnWrapperObject.getClass().getName() + ")"),
                                    sql_json.toString()));
                        }
                    }
                } else {
                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                            "OrderBy type error:" + OrderBy.getClass().getName(), sql_json.toString()));
                }
            }
        }
    }

    public static void check_Limit(RepositoryBase Repository, DataProperty dataProperty, JSONObject sql_json, List<ExceptionItem> errorList,
                                   boolean check_Limit) throws Exception {
        if (sql_json.get("Limit") != null) {
            if (check_Limit) {
                Object Limit = sql_json.get("Limit");
                if (Limit instanceof JSONObject) {
                    JSONObject LimitJSON = (JSONObject) Limit;
                    String[] keys = {"Skip", "Count"};
                    useless(Repository, dataProperty, LimitJSON, keys, errorList);
                    Object ColumnObject = LimitJSON.get("Skip");
                    Object AscObject = LimitJSON.get("Count");
                    if (ColumnObject instanceof Integer && AscObject instanceof Integer) {
                    } else {
                        errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "Limit error:" + LimitJSON.toString(),
                                sql_json.toString()));
                    }
                } else {
                    errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty),
                            "Limit type error:" + Limit.getClass().getName(), sql_json.toString()));
                }
            }
        }
    }

    public static void duplicate(RepositoryBase Repository, DataProperty dataProperty, JSONArray items, String[] keys,
                                 List<ExceptionItem> errorList) throws Exception {
        if (dataProperty == null) {
            return;
        }

        boolean[] flags = new boolean[items.size()];
        for (int i = 0; i < items.size(); i++) {
            if (flags[i]) {
                continue;
            }
            JSONObject item = (JSONObject) items.get(i);
            List<Integer> indexList = new CopyOnWriteArrayList<Integer>();
            for (int ii = i + 1; ii < items.size(); ii++) {
                if (flags[ii]) {
                    continue;
                }
                JSONObject item_next = (JSONObject) items.get(ii);
                boolean has_bad = false;
                for (String key : keys) {
                    boolean cmp = FastJsonCompareUtil.Instance().CompareObject(item.get(key), item_next.get(key));
                    if (!cmp) {
                        has_bad = true;
                    }
                }
                if (!has_bad) {
                    indexList.add(ii);
                }
            }
            if (indexList.size() > 0) {
                StringBuffer sb = new StringBuffer();
                sb.append(i);
                flags[i] = true;
                for (int index : indexList) {
                    sb.append(",");
                    sb.append(index);
                    flags[index] = true;
                }
                errorList.add(new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "duplicate key: (" + sb.toString() + ")",
                        item.toString()));
            }
        }
    }

    public static void useless(RepositoryBase Repository, DataProperty dataProperty, JSONArray items, String[] keys, List<ExceptionItem> errorList)
            throws Exception {
        if (dataProperty == null) {
            return;
        }

        for (Object o : items) {
            JSONObject item = (JSONObject) o;
            useless(Repository, dataProperty, item, keys, errorList);
        }
    }

    public static void useless(RepositoryBase Repository, DataProperty dataProperty, JSONObject item, String[] keys, List<ExceptionItem> errorList)
            throws Exception {
        if (dataProperty == null) {
            return;
        }

        boolean bad_exist = false;
        StringBuilder sb = new StringBuilder();
        for (String keyInner : item.keySet()) {
            boolean goodkey = false;
            for (String key : keys) {
                if (key.equals(keyInner)) {
                    goodkey = true;
                    break;
                }
            }
            if (!goodkey) {
                bad_exist = true;
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(keyInner);
            }
        }
        if (bad_exist) {
            errorList.add(
                    new ExceptionItem(PathUtil.getPropertyPath(Repository, dataProperty), "useless key: (" + sb + ")", item.toString()));
        }
    }
}
