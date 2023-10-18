package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hdwa.sdk.entity.criteria.Criteria;
import com.hdwa.sdk.entity.criteria.CriteriaBase;
import com.hdwa.sdk.entity.criteria.Match;
import com.hdwa.sdk.entity.criteria.MatchBase;
import com.hdwa.sdk.entity.expression.AdvancedExpressionLexer;
import com.hdwa.sdk.entity.expression.AdvancedExpressionParser;
import com.hdwa.sdk.entity.expression.AdvancedExpressionScanner;
import com.hdwa.sdk.entity.expression.AdvancedExpressionWalker;
import com.hdwa.sdk.entity.repository.InfluenceFactor;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.repository.WalkerWrapper;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataPrimitive;
import com.hdwa.sdk.entity.scene.SceneDataSet;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class QueryUtil {

    public static Object parse_static(String PropertyValueSchema, String static_value) {
        if (static_value == null || static_value.length() == 0) {
            return null;
        }

        switch (PropertyValueSchema) {
            case "string":
                return static_value;
            case "double":
                return Double.parseDouble(static_value);
            case "int":
                return Integer.parseInt(static_value);
            case "boolean":
                return Boolean.parseBoolean(static_value);
        }
        return null;
    }


    public static Object query(RepositoryBase Repository, SceneDataValue sv, JSONObject sql_json, QueryAssist queryAssist) throws Exception {
        if (sql_json.get("QueryType") != null) {
            String QueryType = (String) sql_json.get("QueryType");
            JSONObject CriteriaObject = (JSONObject) sql_json.get("Criteria");
            Object result;
            if (QueryType.equals("expression")) {
                Map<String, Boolean> varDict;
                Map<String, Boolean> varStringDict;
                AdvancedExpressionWalker walker = null;
                ReentrantLock lock = null;
                if (sv != null && sv.rel_property != null) {
                    varDict = Repository.p2varDict.get(sv.rel_property);
                    varStringDict = Repository.p2varStringDict.get(sv.rel_property);

                    if (varDict == null) {
                        varDict = new HashMap<>(16);
                    }
                    if (varStringDict == null) {
                        varStringDict = new HashMap<>(16);
                    }
                    {
                        WalkerWrapper WalkerWrapper = Repository.p2walker1.get(sv.rel_property);
                        if (WalkerWrapper != null) {
                            walker = WalkerWrapper.walker;
                            lock = WalkerWrapper.lock;
                        }
                    }
                } else {
                    ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream((sql_json.get("expression") + "$").getBytes()));
                    AdvancedExpressionLexer lexer = new AdvancedExpressionLexer(input);
                    CommonTokenStream tokens = new CommonTokenStream(lexer);
                    AdvancedExpressionParser parser = new AdvancedExpressionParser(tokens);
                    AdvancedExpressionParser.prog_return r = parser.prog();
                    CommonTree t = (CommonTree) r.getTree();

                    CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
                    nodes.setTokenStream(tokens);

                    AdvancedExpressionScanner scanner = new AdvancedExpressionScanner(nodes);
                    scanner.prog();
                    {
                        for (String var : scanner.varDict.keySet()) {
                            if (!CriteriaObject.containsKey(var)) {
                                throw new Exception("var: " + var + " not exist in Criteria");
                            }
                        }
                    }
                    {
                        for (String varString : scanner.varStringDict.keySet()) {
                            if (!CriteriaObject.containsKey(varString)) {
                                throw new Exception("varString: " + varString + " not exist in Criteria");
                            }
                        }
                    }

                    varDict = scanner.varDict;
                    varStringDict = scanner.varStringDict;
                    walker = new AdvancedExpressionWalker(nodes);
                    lock = new ReentrantLock(true);
                }
                try {
                    if (lock != null) {
                        lock.lock();
                    }
                    if (walker != null) {
                        walker.clear();
                    }
                    if (walker != null) {
                        walker.reset();
                    }
                    SceneDataPrimitive SceneValuePrimitive = new SceneDataPrimitive();
                    result = SceneValuePrimitive;
                    CriteriaBase criteria = parseCriteria(Repository, sv, CriteriaObject, queryAssist, new ConcurrentHashMap<String, Boolean>());
                    Criteria.CriteriaDefault CriteriaDefault = (Criteria.CriteriaDefault) criteria;
                    for (String var : varDict.keySet()) {
                        Match.MatchE me = (Match.MatchE) CriteriaDefault.column2MatchList.get(var).get(0);
                        if (me.change()) {
                            SceneValuePrimitive.change = true;
                        }
                        if (me.value == null) {
                            walker.put_null(var);
                        } else {
                            if (me.value instanceof Integer) {
                                walker.put(var, (long) ((Integer) me.value));
                            } else if (me.value instanceof Long) {
                                walker.put(var, (Long) me.value);
                            } else if (me.value instanceof BigInteger) {
                                walker.put(var, ((BigInteger) me.value).longValue());
                            } else if (me.value instanceof Float) {
                                walker.put(var, (double) ((Float) me.value));
                            } else if (me.value instanceof Double) {
                                walker.put(var, (Double) me.value);
                            } else if (me.value instanceof BigDecimal) {
                                walker.put(var, ((BigDecimal) me.value).doubleValue());
                            } else {
                                //throw new Exception(me.value.getClass().toString());
                                if (me.value.toString().contains("null")) {
                                    walker.put_null(var);
                                }

                            }
                        }
                    }
                    for (String var : varStringDict.keySet()) {
                        Match.MatchE me = (Match.MatchE) CriteriaDefault.column2MatchList.get(var).get(0);
                        if (me.change()) {
                            SceneValuePrimitive.change = true;
                        }
                        if (me.value == null) {
                            walker.putString(var, null);
                        } else {
                            // 交付数据信息点可能是JSONObject或者JSONArray，expression用到的地方使用contains
                            if (me.value instanceof String) {
                                walker.putString(var, (String) me.value);
                            } else if (me.value instanceof JSONObject) {
                                walker.putString(var, me.value.toString());
                            } else if (me.value instanceof JSONArray) {
                                walker.putString(var, me.value.toString());
                            } else {
                                throw new Exception(me.value.getClass().toString());
                            }
                        }
                    }
                    ValueObject prog_result = null;
                    if (walker != null) {
                        prog_result = walker.prog();
                    }
                    if (prog_result != null) {
                        SceneValuePrimitive.value = prog_result.value();
                    }
                } finally {
                    if (lock != null) {
                        lock.unlock();
                    }
                }
            } else if (QueryType.equals("quote")) {
                CriteriaBase criteria = parseCriteria(Repository, sv, CriteriaObject, queryAssist, new ConcurrentHashMap<String, Boolean>());
                Criteria.CriteriaDefault CriteriaDefault = (Criteria.CriteriaDefault) criteria;
                MatchBase MatchBase = CriteriaDefault.column2MatchList.get("quote").get(0);
                if (sv != null && sv.rel_property != null && sv.rel_property.propertyValueSchema.equals("JSONArray")) {
                    SceneDataSet resultTmp = new SceneDataSet(true);
                    result = resultTmp;
                    resultTmp.singleValueSet = new CopyOnWriteArrayList<SceneDataValue>();
                    if (MatchBase instanceof Match.MatchIn) {
                        Match.MatchIn me = (Match.MatchIn) MatchBase;
                        if (me.change()) {
                            resultTmp.setRowChange(true);
                        }
                        Object[] array = me.value.toArray();
                        for (Object arrayItem : array) {
                            SceneDataValue sdvInner = new SceneDataValue(null, null, null, null);
                            sdvInner.value_prim = new SceneDataPrimitive();
                            sdvInner.value_prim.value = arrayItem;
                            resultTmp.singleValueSet.add(sdvInner);
                        }
                    } else {
                        throw new Exception("quote error");
                    }
                } else {
                    SceneDataPrimitive SceneValuePrimitive = new SceneDataPrimitive();
                    result = SceneValuePrimitive;
                    if (MatchBase instanceof Match.MatchE) {
                        Match.MatchE me = (Match.MatchE) MatchBase;
                        if (me.change()) {
                            SceneValuePrimitive.change = true;
                        }
                        SceneValuePrimitive.value = me.value;
                    } else if (MatchBase instanceof Match.MatchIn) {
                        Match.MatchIn me = (Match.MatchIn) MatchBase;
                        if (me.change()) {
                            SceneValuePrimitive.change = true;
                        }
                        Object[] array = me.value.toArray();
                        if (array.length > 0) {
                            SceneValuePrimitive.value = me.value.toArray()[0];
                        } else {
                            SceneValuePrimitive.value = null;
                        }
                    } else {
                        throw new Exception("quote error");
                    }
                }
            } else {
                // 构造返回列
                List<String> ReturnColumns = null;
                if (sql_json.get("ReturnColumns") != null) {
                    SceneDataSet ReturnColumns_ori = parseSet(Repository, sv, sql_json.get("ReturnColumns"), new QueryAssist(), true);
                    ReturnColumns = new CopyOnWriteArrayList<String>();
                    for (SceneDataValue sdv : ReturnColumns_ori.singleValueSet) {
                        ReturnColumns.add((String) sdv.value_prim.value);
                    }
                }
                String UniqueReturnColumn = null;
                if (sql_json.get("UniqueReturnColumn") != null) {
                    UniqueReturnColumn = (sql_json.get("UniqueReturnColumn")).toString();
                }

                // 处理聚合函数和GroupBy
                Object Aggregation = null;
                if (sql_json.get("Aggregation") != null) {
                    Aggregation = sql_json.get("Aggregation");
                }
                JSONArray GroupBy = null;
                if (sql_json.get("GroupBy") != null) {
                    GroupBy = (JSONArray) sql_json.get("GroupBy");
                }
                JSONArray OrderBy = null;
                if (sql_json.get("OrderBy") != null) {
                    OrderBy = (JSONArray) sql_json.get("OrderBy");
                }
                JSONObject Limit = null;
                if (sql_json.get("Limit") != null) {
                    Limit = (JSONObject) sql_json.get("Limit");
                }

                // 构建影响因素需求
                QueryAssist QueryAssist_before = queryAssist;
                QueryAssist QueryAssist_1 = new QueryAssist();
                if (ReturnColumns != null || UniqueReturnColumn != null) {
                    if (ReturnColumns != null) {
                        QueryAssist_1.rowChangeNeed = QueryAssist_before.rowChangeNeed;
                        if (QueryAssist_1.rowChangeNeed) {
                            for (String key : QueryAssist_before.colChangeNeed.keySet()) {
                                QueryAssist_1.colChangeNeed.put(key, true);
                            }
                        }
                    } else if (UniqueReturnColumn != null) {
                        QueryAssist_1.rowChangeNeed = QueryAssist_before.rowChangeNeed;
                        if (QueryAssist_1.rowChangeNeed) {
                            QueryAssist_1.colChangeNeed.put(UniqueReturnColumn, true);
                        }
                    }
                    QueryAssist_before = QueryAssist_1;
                }
                QueryAssist QueryAssist_2 = new QueryAssist();
                if (OrderBy != null || Limit != null) {
                    if (OrderBy != null && Limit != null) {
                        QueryAssist_2.rowChangeNeed = QueryAssist_before.rowChangeNeed;
                        if (QueryAssist_2.rowChangeNeed) {
                            for (int i = 0; i < OrderBy.size(); i++) {
                                JSONObject Item = OrderBy.getJSONObject(i);
                                String Column = (String) Item.get("Column");
                                QueryAssist_2.colChangeNeed.put(Column, true);
                            }
                            for (String key : QueryAssist_before.colChangeNeed.keySet()) {
                                QueryAssist_2.colChangeNeed.put(key, true);
                            }
                        }
                        QueryAssist_before = QueryAssist_2;
                    }
                }
                QueryAssist QueryAssist_3 = new QueryAssist();
                if (Aggregation != null) {
                    if (GroupBy != null) {
                        QueryAssist_3.rowChangeNeed = QueryAssist_before.rowChangeNeed;
                        if (QueryAssist_3.rowChangeNeed) {
                            for (Object o : GroupBy) {
                                String Column = (String) o;
                                QueryAssist_3.colChangeNeed.put(Column, true);
                            }

                            JSONArray AggregationArray = (JSONArray) Aggregation;
                            for (int i = 0; i < AggregationArray.size(); i++) {
                                JSONObject Item = AggregationArray.getJSONObject(i);
                                String Column = (String) Item.get("Column");
                                String Function = (String) Item.get("Function");
                                String Name = (String) Item.get("Name");
                                if (Function.equals("count")) {
                                } else {
                                    if (QueryAssist_before.colChangeNeed.containsKey(Name)) {
                                        QueryAssist_3.colChangeNeed.put(Column, true);
                                    }
                                }
                            }
                        }
                    } else if (Aggregation instanceof JSONArray) {
                        QueryAssist_3.rowChangeNeed = QueryAssist_before.rowChangeNeed;
                        if (QueryAssist_3.rowChangeNeed) {
                            JSONArray AggregationArray = (JSONArray) Aggregation;
                            for (int i = 0; i < AggregationArray.size(); i++) {
                                JSONObject Item = AggregationArray.getJSONObject(i);
                                String Column = (String) Item.get("Column");
                                String Function = (String) Item.get("Function");
                                String Name = (String) Item.get("Name");
                                if (Function.equals("count")) {
                                } else {
                                    if (QueryAssist_before.colChangeNeed.containsKey(Name)) {
                                        QueryAssist_3.colChangeNeed.put(Column, true);
                                    }
                                }
                            }
                        }
                    } else {
                        QueryAssist_3.rowChangeNeed = QueryAssist_before.rowChangeNeed;
                        if (QueryAssist_3.rowChangeNeed) {
                            JSONObject AggregationObject = (JSONObject) Aggregation;
                            {
                                JSONObject Item = AggregationObject;
                                String Column = (String) Item.get("Column");
                                String Function = (String) Item.get("Function");
                                if (Function.equals("count")) {
                                } else {
                                    QueryAssist_3.colChangeNeed.put(Column, true);
                                }
                            }
                        }
                    }
                    QueryAssist_before = QueryAssist_3;
                }
                // 构造查询条件
                QueryAssist QueryAssist2Criteria = new QueryAssist();
                QueryAssist2Criteria.rowChangeNeed = QueryAssist_before.rowChangeNeed;
                Map<String, Boolean> CriteriaColumns = new ConcurrentHashMap<String, Boolean>();
                CriteriaBase criteria = parseCriteria(Repository, sv, CriteriaObject, QueryAssist2Criteria, CriteriaColumns);

                // 构造备选集合
                QueryAssist QueryAssist2Target = new QueryAssist();
                QueryAssist2Target.rowChangeNeed = QueryAssist_before.rowChangeNeed;
                if (QueryAssist2Target.rowChangeNeed) {
                    for (String key : QueryAssist_before.colChangeNeed.keySet()) {
                        QueryAssist2Target.colChangeNeed.put(key, true);
                    }
                    for (String key : CriteriaColumns.keySet()) {
                        QueryAssist2Target.colChangeNeed.put(key, true);
                    }
                }

                // 开始解析查询目标
                Object Target = sql_json.get("Target");
                SceneDataSet targetSet = parseSet(Repository, sv, Target, QueryAssist2Target, false);
                if (QueryAssist2Criteria.rowChangeNeed) {
                    QueryAssist2Criteria.rowFactor.merge(QueryAssist2Target.rowFactor);
                    for (String key : QueryAssist_before.colChangeNeed.keySet()) {
                        if (QueryAssist2Target.colFactorMap.containsKey(key)) {
                            InfluenceFactor InfluenceFactor = QueryAssist2Target.colFactorMap.get(key);
                            QueryAssist2Criteria.colFactorMap.putIfAbsent(key, new InfluenceFactor());
                            QueryAssist2Criteria.colFactorMap.get(key).merge(InfluenceFactor);
                        }
                    }
                    for (String key : CriteriaColumns.keySet()) {
                        if (QueryAssist2Target.colFactorMap.containsKey(key)) {
                            InfluenceFactor InfluenceFactor = QueryAssist2Target.colFactorMap.get(key);
                            QueryAssist2Criteria.rowFactor.merge(InfluenceFactor);
                        }
                    }
                }

                // 开始查询
                result = query_select(targetSet, criteria);
                QueryAssist QueryAssist_after = QueryAssist2Criteria;
                if (Aggregation != null) {
                    result = query_aggregation((SceneDataSet) result, Aggregation, GroupBy);
                    if (GroupBy != null) {
                        if (QueryAssist_3.rowChangeNeed) {
                            QueryAssist_3.rowFactor.merge(QueryAssist_after.rowFactor);
                            for (Object o : GroupBy) {
                                String Column = (String) o;
                                if (QueryAssist_after.colFactorMap.containsKey(Column)) {
                                    InfluenceFactor InfluenceFactor = QueryAssist_after.colFactorMap.get(Column);
                                    QueryAssist_3.rowFactor.merge(InfluenceFactor);
                                }
                            }

                            JSONArray AggregationArray = (JSONArray) Aggregation;
                            for (int i = 0; i < AggregationArray.size(); i++) {
                                JSONObject Item = AggregationArray.getJSONObject(i);
                                String Column = (String) Item.get("Column");
                                String Function = (String) Item.get("Function");
                                String Name = (String) Item.get("Name");
                                if (Function.equals("count")) {
                                } else {
                                    if (QueryAssist_after.colFactorMap.containsKey(Column)) {
                                        InfluenceFactor InfluenceFactor = QueryAssist_after.colFactorMap.get(Column);
                                        QueryAssist_3.colFactorMap.putIfAbsent(Name, new InfluenceFactor());
                                        QueryAssist_3.colFactorMap.get(Name).merge(InfluenceFactor);
                                    }
                                }
                            }
                        }
                    } else if (Aggregation instanceof JSONArray) {
                        if (QueryAssist_3.rowChangeNeed) {
                            JSONArray AggregationArray = (JSONArray) Aggregation;
                            for (int i = 0; i < AggregationArray.size(); i++) {
                                JSONObject Item = AggregationArray.getJSONObject(i);
                                String Column = (String) Item.get("Column");
                                String Function = (String) Item.get("Function");
                                String Name = (String) Item.get("Name");
                                QueryAssist_3.colFactorMap.putIfAbsent(Name, new InfluenceFactor());
                                if (Function.equals("count")) {
                                    QueryAssist_3.colFactorMap.get(Name).merge(QueryAssist_after.rowFactor);
                                } else {
                                    QueryAssist_3.colFactorMap.get(Name).merge(QueryAssist_after.rowFactor);
                                    if (QueryAssist_after.colFactorMap.containsKey(Column)) {
                                        InfluenceFactor InfluenceFactor = QueryAssist_after.colFactorMap.get(Column);
                                        QueryAssist_3.colFactorMap.get(Name).merge(InfluenceFactor);
                                    }
                                }
                            }
                        }
                    } else {
                        if (QueryAssist_3.rowChangeNeed) {
                            QueryAssist_3.rowFactor.merge(QueryAssist_after.rowFactor);
                            JSONObject AggregationObject = (JSONObject) Aggregation;
                            {
                                String Column = (String) AggregationObject.get("Column");
                                String Function = (String) AggregationObject.get("Function");
                                if (Function.equals("count")) {
                                } else {
                                    if (QueryAssist_after.colFactorMap.containsKey(Column)) {
                                        InfluenceFactor InfluenceFactor = QueryAssist_after.colFactorMap.get(Column);
                                        QueryAssist_3.rowFactor.merge(InfluenceFactor);
                                    }
                                }
                            }
                        }
                    }
                    QueryAssist_after = QueryAssist_3;
                }
                if (OrderBy != null || Limit != null) {
                    result = query_after1((SceneDataSet) result, OrderBy, Limit);
                    if (OrderBy != null && Limit != null) {
                        if (QueryAssist_2.rowChangeNeed) {
                            QueryAssist_2.rowFactor.merge(QueryAssist_after.rowFactor);
                            for (int i = 0; i < OrderBy.size(); i++) {
                                JSONObject Item = OrderBy.getJSONObject(i);
                                String Column = (String) Item.get("Column");
                                if (QueryAssist_after.colFactorMap.containsKey(Column)) {
                                    InfluenceFactor InfluenceFactor = QueryAssist_after.colFactorMap.get(Column);
                                    QueryAssist_2.rowFactor.merge(InfluenceFactor);
                                }
                            }
                            QueryAssist_2.colFactorMap = QueryAssist_after.colFactorMap;
                        }
                        QueryAssist_after = QueryAssist_2;
                    }
                }
                if (ReturnColumns != null || UniqueReturnColumn != null) {
                    result = query_after2((SceneDataSet) result, ReturnColumns, UniqueReturnColumn);
                    if (ReturnColumns != null) {
                        if (QueryAssist_1.rowChangeNeed) {
                            QueryAssist_1.rowFactor.merge(QueryAssist_after.rowFactor);
                            for (String Column : ReturnColumns) {
                                if (QueryAssist_after.colFactorMap.containsKey(Column)) {
                                    InfluenceFactor InfluenceFactor = QueryAssist_after.colFactorMap.get(Column);
                                    QueryAssist_1.colFactorMap.putIfAbsent(Column, new InfluenceFactor());
                                    QueryAssist_1.colFactorMap.get(Column).merge(InfluenceFactor);
                                }
                            }
                        }
                    } else {
                        if (QueryAssist_1.rowChangeNeed) {
                            QueryAssist_1.rowFactor.merge(QueryAssist_after.rowFactor);
                            {
                                if (QueryAssist_after.colFactorMap.containsKey(UniqueReturnColumn)) {
                                    InfluenceFactor InfluenceFactor = QueryAssist_after.colFactorMap.get(UniqueReturnColumn);
                                    QueryAssist_1.rowFactor.merge(InfluenceFactor);
                                }
                            }
                        }
                    }
                    QueryAssist_after = QueryAssist_1;
                }

                // 整理返回QueryAssist
                if (queryAssist.rowChangeNeed) {
                    queryAssist.rowFactor = QueryAssist_after.rowFactor;
                    queryAssist.colFactorMap = QueryAssist_after.colFactorMap;
                }
            }
            return result;
        } else if (sql_json.get("SetOperator") != null) {
            return parseSet(Repository, sv, sql_json, queryAssist, false);
        } else {
            return null;
        }
    }

    public static Object select_node(RepositoryBase Repository, SceneDataValue sv, JSONObject sql_json, SceneDataSet targetSet) throws Exception {
        // 构造查询条件
        JSONObject CriteriaObject = (JSONObject) sql_json.get("Criteria");
        CriteriaBase criteria = parseCriteria(Repository, sv, CriteriaObject, new QueryAssist(), new ConcurrentHashMap<String, Boolean>());

        // 构造返回列
        List<String> ReturnColumns = null;
        if (sql_json.get("ReturnColumns") != null) {
            SceneDataSet ReturnColumns_ori = parseSet(Repository, sv, sql_json.get("ReturnColumns"), new QueryAssist(), true);
            ReturnColumns = new CopyOnWriteArrayList<String>();
            for (SceneDataValue sdv : ReturnColumns_ori.singleValueSet) {
                ReturnColumns.add((String) sdv.value_prim.value);
            }
        }
        String UniqueReturnColumn = null;
        if (sql_json.get("UniqueReturnColumn") != null) {
            UniqueReturnColumn = (sql_json.get("UniqueReturnColumn")).toString();
        }

        // 处理聚合函数和GroupBy
        Object Aggregation = null;
        if (sql_json.get("Aggregation") != null) {
            Aggregation = sql_json.get("Aggregation");
        }
        JSONArray GroupBy = null;
        if (sql_json.get("GroupBy") != null) {
            GroupBy = (JSONArray) sql_json.get("GroupBy");
        }
        JSONArray OrderBy = null;
        if (sql_json.get("OrderBy") != null) {
            OrderBy = (JSONArray) sql_json.get("OrderBy");
        }
        JSONObject Limit = null;
        if (sql_json.get("Limit") != null) {
            Limit = (JSONObject) sql_json.get("Limit");
        }

        return select_execute(targetSet, criteria, Aggregation, GroupBy, OrderBy, Limit, ReturnColumns, UniqueReturnColumn,
                new QueryAssist());
    }

    private static Object select_execute(SceneDataSet targetSet, CriteriaBase criteria, Object Aggregation, JSONArray GroupBy, JSONArray OrderBy,
                                         JSONObject Limit, List<String> ReturnColumns, String UniqueReturnColumn, QueryAssist QueryAssist) throws Exception {
        Object result;
        result = query_select(targetSet, criteria);
        if (Aggregation != null) {
            result = query_aggregation((SceneDataSet) result, Aggregation, GroupBy);
        }
        if (OrderBy != null || Limit != null) {
            result = query_after1((SceneDataSet) result, OrderBy, Limit);
        }
        if (ReturnColumns != null || UniqueReturnColumn != null) {
            result = query_after2((SceneDataSet) result, ReturnColumns, UniqueReturnColumn);
        }

        return result;
    }

    private static CriteriaBase parseCriteria(RepositoryBase Repository, SceneDataValue sv, JSONObject CriteriaObject, QueryAssist queryAssist,
                                              Map<String, Boolean> CriteriaColumns) throws Exception {
        CriteriaBase criteria;
        Object LogicOperator = CriteriaObject.get("LogicOperator");
        if (LogicOperator != null) {
            String LogicOperatorString = (LogicOperator).toString();
            if (LogicOperatorString.equals("and") || LogicOperatorString.equals("or")) {
                JSONArray Criterias = (JSONArray) CriteriaObject.get("Criterias");
                List<CriteriaBase> criteriaList = new CopyOnWriteArrayList<CriteriaBase>();
                for (Object o : Criterias) {
                    JSONObject CriteriaObjectInner = (JSONObject) o;
                    CriteriaBase criteriaInner = parseCriteria(Repository, sv, CriteriaObjectInner, queryAssist, CriteriaColumns);
                    criteriaList.add(criteriaInner);
                }
                if (LogicOperatorString.equals("and")) {
                    Criteria.CriteriaAnd Criteria_and = new Criteria.CriteriaAnd();
                    Criteria_and.criteriaList = criteriaList;
                    criteria = Criteria_and;
                } else {
                    Criteria.CriteriaOr Criteria_or = new Criteria.CriteriaOr();
                    Criteria_or.criteriaList = criteriaList;
                    criteria = Criteria_or;
                }
            } else if (LogicOperatorString.equals("not")) {
                JSONObject CriteriaObjectInner = (JSONObject) CriteriaObject.get("Criteria");
                CriteriaBase criteriaInner = parseCriteria(Repository, sv, CriteriaObjectInner, queryAssist, CriteriaColumns);
                Criteria.CriteriaNot Criteria_not = new Criteria.CriteriaNot();
                Criteria_not.criteria = criteriaInner;
                criteria = Criteria_not;
            } else {
                criteria = null;
            }
        } else {
            Criteria.CriteriaDefault CriteriaDefault = new Criteria.CriteriaDefault();
            for (String itemKey : CriteriaObject.keySet()) {
                CriteriaColumns.put(itemKey, true);
                Object itemValue = CriteriaObject.get(itemKey);
                List<MatchBase> matchList = new CopyOnWriteArrayList<MatchBase>();
                if (itemValue instanceof JSONObject) {
                    JSONObject valueInner = (JSONObject) itemValue;
                    if (valueInner.get("ref") != null) {
                        QueryAssist QueryAssistInner = new QueryAssist(queryAssist.rowChangeNeed);
                        SceneDataPrimitive svInner_value_primitive = parseCriteriaRef(Repository, valueInner, sv, QueryAssistInner);
                        if (QueryAssistInner.rowChangeNeed) {
                            queryAssist.rowFactor.merge(QueryAssistInner.rowFactor);
                        }
                        Match.MatchE MatchInner = new Match.MatchE(svInner_value_primitive.value, svInner_value_primitive.change);
                        matchList.add(MatchInner);
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
                                case "array_size": {
                                    boolean change = false;
                                    if (itemValueInner instanceof JSONObject) {
                                        JSONObject valueInnerInner = (JSONObject) itemValueInner;
                                        if (valueInnerInner.get("ref") != null) {
                                            QueryAssist QueryAssistInner = new QueryAssist(queryAssist.rowChangeNeed);
                                            SceneDataPrimitive svInner_value_primitive = parseCriteriaRef(Repository, valueInnerInner, sv,
                                                    QueryAssistInner);
                                            if (QueryAssistInner.rowChangeNeed) {
                                                queryAssist.rowFactor.merge(QueryAssistInner.rowFactor);
                                            }
                                            itemValueInner = svInner_value_primitive.value;
                                            change = svInner_value_primitive.change;
                                        }
                                    }
                                    switch (itemKeyInner) {
                                        case "e": {
                                            Match.MatchE MatchInner = new Match.MatchE(itemValueInner, change);
                                            matchList.add(MatchInner);
                                            break;
                                        }
                                        case "ne": {
                                            Match.MatchNe MatchInner = new Match.MatchNe(itemValueInner, change);
                                            matchList.add(MatchInner);
                                            break;
                                        }
                                        case "gt": {
                                            Match.MatchGt MatchInner = new Match.MatchGt(itemValueInner, change);
                                            matchList.add(MatchInner);
                                            break;
                                        }
                                        case "gte": {
                                            Match.MatchGte MatchInner = new Match.MatchGte(itemValueInner, change);
                                            matchList.add(MatchInner);
                                            break;
                                        }
                                        case "lt": {
                                            Match.MatchLt MatchInner = new Match.MatchLt(itemValueInner, change);
                                            matchList.add(MatchInner);
                                            break;
                                        }
                                        case "lte": {
                                            Match.MatchLte MatchInner = new Match.MatchLte(itemValueInner, change);
                                            matchList.add(MatchInner);
                                            break;
                                        }
                                        case "regex": {
                                            Match.MatchRegex MatchInner = new Match.MatchRegex(itemValueInner, change);
                                            matchList.add(MatchInner);
                                            break;
                                        }
                                        case "contain": {
                                            Match.MatchContain MatchInner = new Match.MatchContain(itemValueInner, change);
                                            matchList.add(MatchInner);
                                            break;
                                        }
                                        case "startwith": {
                                            Match.MatchStartwith MatchInner = new Match.MatchStartwith(itemValueInner, change);
                                            matchList.add(MatchInner);
                                            break;
                                        }
                                        case "array_size": {
                                            Match.MatchArraySize MatchInner = new Match.MatchArraySize(itemValueInner, change);
                                            matchList.add(MatchInner);
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case "in":
                                case "notin":
                                case "array_e":
                                case "array_ne":
                                case "array_include":
                                case "array_included":
                                case "array_exclude":
                                case "array_intersect":
                                    if (itemValueInner instanceof JSONObject && ((JSONObject) itemValueInner).containsKey("pass")) {
                                        switch (itemKeyInner) {
                                            case "in": {
                                                Match.MatchIn MatchInner = new Match.MatchIn(null, false);
                                                MatchInner.pass = true;
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "notin": {
                                                Match.MatchNotin MatchInner = new Match.MatchNotin(null, false);
                                                MatchInner.pass = true;
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "array_e": {
                                                Match.MatchArrayE MatchInner = new Match.MatchArrayE(null, false);
                                                MatchInner.pass = true;
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "array_ne": {
                                                Match.MatchArrayNe MatchInner = new Match.MatchArrayNe(null, false);
                                                MatchInner.pass = true;
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "array_include": {
                                                Match.MatchArrayInclude MatchInner = new Match.MatchArrayInclude(null, false);
                                                MatchInner.pass = true;
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "array_included": {
                                                Match.MatchArrayIncluded MatchInner = new Match.MatchArrayIncluded(null, false);
                                                MatchInner.pass = true;
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "array_exclude": {
                                                Match.MatchArrayExclude MatchInner = new Match.MatchArrayExclude(null, false);
                                                MatchInner.pass = true;
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "array_intersect": {
                                                Match.MatchArrayIntersect MatchInner = new Match.MatchArrayIntersect(null, false);
                                                MatchInner.pass = true;
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                        }
                                    } else {
                                        HashSet<Object> valueSet = new HashSet<Object>();
                                        QueryAssist QueryAssistInner = new QueryAssist(queryAssist.rowChangeNeed);
                                        SceneDataSet resultArray = parseSet(Repository, sv, itemValueInner, QueryAssistInner, true);
                                        if (QueryAssistInner.rowChangeNeed) {
                                            queryAssist.rowFactor.merge(QueryAssistInner.rowFactor);
                                        }
                                        for (SceneDataValue resultItem : resultArray.singleValueSet) {
                                            if ((resultItem) == null || (resultItem).value_prim == null
                                                    || (resultItem).value_prim.value == null) {
                                                valueSet.add(null);
                                            } else {
                                                Object normalize_value = DataUtil.primitive_normalize((resultItem).value_prim.value);
                                                valueSet.add(normalize_value);
                                            }
                                        }
                                        switch (itemKeyInner) {
                                            case "in": {
                                                Match.MatchIn MatchInner = new Match.MatchIn(valueSet, resultArray.getRowChange());
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "notin": {
                                                Match.MatchNotin MatchInner = new Match.MatchNotin(valueSet, resultArray.getRowChange());
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "array_e": {
                                                Match.MatchArrayE MatchInner = new Match.MatchArrayE(valueSet, resultArray.getRowChange());
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "array_ne": {
                                                Match.MatchArrayNe MatchInner = new Match.MatchArrayNe(valueSet, resultArray.getRowChange());
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "array_include": {
                                                Match.MatchArrayInclude MatchInner = new Match.MatchArrayInclude(valueSet, resultArray.getRowChange());
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "array_included": {
                                                Match.MatchArrayIncluded MatchInner = new Match.MatchArrayIncluded(valueSet, resultArray.getRowChange());
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "array_exclude": {
                                                Match.MatchArrayExclude MatchInner = new Match.MatchArrayExclude(valueSet, resultArray.getRowChange());
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                            case "array_intersect": {
                                                Match.MatchArrayIntersect MatchInner = new Match.MatchArrayIntersect(valueSet, resultArray.getRowChange());
                                                matchList.add(MatchInner);
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                case "array_elemMatch_exist":
                                case "array_elemMatch_all": {
                                    boolean change = false;
                                    JSONObject itemValueInnerJSON = (JSONObject) itemValueInner;
                                    CriteriaBase criteria_elemMatch = parseCriteria(Repository, sv, itemValueInnerJSON, new QueryAssist(),
                                            new ConcurrentHashMap<String, Boolean>());
                                    if (itemKeyInner.equals("array_elemMatch_exist")) {
                                        Match.MatchArrayElemMatchExist MatchInner = new Match.MatchArrayElemMatchExist(criteria_elemMatch, change);
                                        matchList.add(MatchInner);
                                    } else if (itemKeyInner.equals("array_elemMatch_all")) {
                                        Match.MatchArrayElemMatchAll MatchInner = new Match.MatchArrayElemMatchAll(criteria_elemMatch, change);
                                        matchList.add(MatchInner);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    Match.MatchE Match_e = new Match.MatchE(itemValue, false);
                    matchList.add(Match_e);
                }
                CriteriaDefault.column2MatchList.put(itemKey, matchList);
            }
            criteria = CriteriaDefault;
        }
        return criteria;
    }

    private static SceneDataPrimitive parseCriteriaRef(RepositoryBase Repository, JSONObject valueInner, SceneDataValue sv, QueryAssist QueryAssist) {
        String refString = (valueInner.get("ref")).toString();
        String[] splits = refString.split("'");
        boolean change = false;
        int index_split;
        SceneDataValue svInner;
        if (splits[0].startsWith("ancestor_")) {
            int generate = Integer.parseInt(splits[0].substring("ancestor_".length()));
            Object tmp = sv;
            while (generate > 0) {
                if (tmp instanceof SceneDataValue) {
                    SceneDataValue tmpData = (SceneDataValue) tmp;
                    tmp = tmpData.parentObjectData;
                } else if (tmp != null) {
                    SceneDataObject tmpData = (SceneDataObject) tmp;
                    tmp = tmpData.parentObjectData != null ? tmpData.parentObjectData : tmpData.parentArrayData;
                }
                generate--;
            }

            SceneDataObject parentData = (SceneDataObject) tmp;
            // 比较值只能是value_object
            if (parentData.hasColChange(splits[1])) {
                change = true;
            }
            index_split = 2;
            svInner = parentData.get(splits[1]);
        } else if (splits[0].equals("base_value")) {
            change = true;
            index_split = 2;
            svInner = Repository.base_value.get(splits[1]);
        } else {
            if (Repository.objectData.hasColChange(splits[0])) {
                change = true;
            }
            index_split = 1;
            svInner = Repository.objectData.get(splits[0]);
        }
        SceneDataValue last_sdv = null;
        for (int i = index_split; i < splits.length; i++) {
            String split = splits[i];
            int index_ = split.indexOf('=');
            if (index_ != -1) {
                String propertyName = split.substring(0, index_);
                String propertyValue = split.substring(index_ + 1);
                if (svInner != null && (svInner.value_array.getRowChange() || svInner.value_array.hasColChange(propertyName))) {
                    change = true;
                }
                SceneDataValue sv_valid = null;
                if (svInner != null) {
                    for (SceneDataObject sdb : svInner.value_array.set) {
                        if (sdb.containsKey(propertyName) && propertyValue.equals(sdb.get(propertyName).value_prim.value)) {
                            SceneDataValue svWrapper = new SceneDataValue(null, sdb, propertyName, null);
                            svWrapper.value_object = sdb;
                            sv_valid = svWrapper;
                        }
                    }
                }
                last_sdv = svInner;
                svInner = sv_valid;
            } else {
                if (last_sdv != null) {
                    if (last_sdv.value_array.getRowChange() || last_sdv.value_array.hasColChange(split)) {
                        change = true;
                    }
                } else {
                    if (svInner != null && svInner.value_object != null && (svInner.value_object.getRowChange() || svInner.value_object.hasColChange(split))) {
                        change = true;
                    }
                }
                last_sdv = null;
                if (svInner != null) {
                    if (svInner.value_object != null) {
                        svInner = svInner.value_object.get(split);
                    }
                }
            }
        }
        SceneDataPrimitive result = new SceneDataPrimitive();
        if (change) {
            result.change = true;
        }
        if (svInner != null && svInner.value_prim != null) {
            if (svInner.value_prim.change) {
                result.change = true;
            }
            result.value = svInner.value_prim.value;
            if (QueryAssist.rowChangeNeed) {
                QueryAssist.rowFactor.valueChange.putIfAbsent(svInner, true);
            }
        }
        return result;
    }

    private static SceneDataSet parseSet(RepositoryBase Repository, SceneDataValue sv, Object setDesc, QueryAssist QueryAssist,
                                         boolean isSingleValueSet) throws Exception {
        SceneDataSet result;
        if (setDesc instanceof JSONArray) {
            result = new SceneDataSet(isSingleValueSet);
            result.setRowChange(false);
            if (isSingleValueSet) {
                List<SceneDataValue> sdvList = BaseApiUtil.arrayToSdvList((JSONArray) setDesc);
                result.singleValueSet = new CopyOnWriteArrayList<>();
                result.singleValueSet.addAll(sdvList);
            } else {
                List<SceneDataObject> sdvList = BaseApiUtil.arrayToSdoList((JSONArray) setDesc);
                result.set = new CopyOnWriteArrayList<>();
                result.set.addAll(sdvList);
            }
            return result;
        }

        JSONObject descSet = (JSONObject) setDesc;
        if (descSet.get("Source") != null) {
            result = null;
            String Source = (descSet.get("Source")).toString();
            if (Source.equals("ref")) {
                String refString = (descSet.get("ref")).toString();
                result = parseSetRef(Repository, sv, refString, QueryAssist, isSingleValueSet, false);
            } else {
                result = Repository.ParseSource(descSet, Source);
                if (QueryAssist.rowChangeNeed) {
                    QueryAssist.rowFactor.rowChange.put(result, true);
                    for (String col : QueryAssist.colChangeNeed.keySet()) {
                        QueryAssist.colFactorMap.putIfAbsent(col, new InfluenceFactor());
                        QueryAssist.colFactorMap.get(col).colChange.putIfAbsent(result, new ConcurrentHashMap<String, Boolean>());
                        QueryAssist.colFactorMap.get(col).colChange.get(result).put(col, true);
                    }
                }
            }
            return result;
        } else if (descSet.get("ref") != null && isSingleValueSet) {
            String refString = (descSet.get("ref")).toString();
            result = parseSetRef(Repository, sv, refString, QueryAssist, isSingleValueSet, false);
            return result;
        } else if (descSet.get("SetOperator") != null) {
            result = new SceneDataSet(isSingleValueSet);
            if (isSingleValueSet) {
                result.singleValueSet = new CopyOnWriteArrayList<SceneDataValue>();

                String SetOperator = (descSet.get("SetOperator")).toString();
                if (SetOperator.equals("add") || SetOperator.equals("merge") || SetOperator.equals("unite")) {
                    JSONArray SetArray = (JSONArray) descSet.get("SetArray");
                    List<SceneDataSet> resultList = new CopyOnWriteArrayList<SceneDataSet>();
                    for (Object SetArrayItem : SetArray) {
                        SceneDataSet resultItem = parseSet(Repository, sv, SetArrayItem, QueryAssist, isSingleValueSet);
                        resultList.add(resultItem);
                        if (resultItem.getRowChange()) {
                            result.setRowChange(true);
                        }
                    }
                    switch (SetOperator) {
                        case "add":
                            for (SceneDataSet resultItem : resultList) {
                                result.singleValueSet.addAll(resultItem.singleValueSet);
                            }
                            break;
                        case "merge":
                            for (SceneDataSet resultItem : resultList) {
                                for (SceneDataValue resultItemItem : resultItem.singleValueSet) {
                                    boolean exist = false;
                                    for (SceneDataValue existItem : result.singleValueSet) {
                                        if (CompareUtil.Instance().CompareObject(existItem, resultItemItem)) {
                                            exist = true;
                                            break;
                                        }
                                    }
                                    if (!exist) {
                                        result.singleValueSet.add(resultItemItem);
                                    }
                                }
                            }
                            break;
                        case "unite":
                            SceneDataSet resultFirst = resultList.get(0);
                            for (SceneDataValue existItem : resultFirst.singleValueSet) {
                                int exist = 0;
                                for (int i = 1; i < resultList.size(); i++) {
                                    SceneDataSet resultItem = resultList.get(i);
                                    for (SceneDataValue resultItemItem : resultItem.singleValueSet) {
                                        if (CompareUtil.Instance().CompareObject(existItem, resultItemItem)) {
                                            exist++;
                                            break;
                                        }
                                    }
                                }
                                if (exist == resultList.size() - 1) {
                                    result.singleValueSet.add(existItem);
                                }
                            }
                            break;
                    }
                } else if (SetOperator.equals("sub")) {
                    SceneDataSet Set1 = parseSet(Repository, sv, descSet.get("Set1"), QueryAssist, isSingleValueSet);
                    SceneDataSet Set2 = parseSet(Repository, sv, descSet.get("Set2"), QueryAssist, isSingleValueSet);

                    for (SceneDataValue existItem : Set1.singleValueSet) {
                        boolean exist = false;
                        for (SceneDataValue resultItemItem : Set2.singleValueSet) {
                            if (CompareUtil.Instance().CompareObject(existItem, resultItemItem)) {
                                exist = true;
                                break;
                            }
                        }
                        if (!exist) {
                            result.singleValueSet.add(existItem);
                        }
                    }
                }
            } else {
                result.set = new CopyOnWriteArrayList<SceneDataObject>();

                String SetOperator = (descSet.get("SetOperator")).toString();
                if (SetOperator.equals("add") || SetOperator.equals("merge") || SetOperator.equals("unite")) {
                    JSONArray SetArray = (JSONArray) descSet.get("SetArray");
                    List<SceneDataSet> resultList = new CopyOnWriteArrayList<SceneDataSet>();
                    for (Object SetArrayItem : SetArray) {
                        SceneDataSet resultItem = parseSet(Repository, sv, SetArrayItem, QueryAssist, isSingleValueSet);
                        resultList.add(resultItem);
                        if (resultItem.getRowChange()) {
                            result.setRowChange(true);
                        }
                        for (String col : resultItem.getColChange().keySet()) {
                            result.setColChange(col);
                        }
                    }
                    if (SetOperator.equals("add")) {
                        for (SceneDataSet resultItem : resultList) {
                            result.set.addAll(resultItem.set);
                        }
                    } else if (SetOperator.equals("merge")) {
                        for (SceneDataSet resultItem : resultList) {
                            for (SceneDataObject resultItemItem : resultItem.set) {
                                boolean exist = false;
                                for (SceneDataObject existItem : result.set) {
                                    if (CompareUtil.Instance().Compare(existItem, resultItemItem)) {
                                        exist = true;
                                        break;
                                    }
                                }
                                if (!exist) {
                                    result.set.add(resultItemItem);
                                }
                            }
                        }
                    } else if (SetOperator.equals("unite")) {
                        SceneDataSet resultFirst = resultList.get(0);
                        for (SceneDataObject existItem : resultFirst.set) {
                            int exist = 0;
                            for (int i = 1; i < resultList.size(); i++) {
                                SceneDataSet resultItem = resultList.get(i);
                                for (SceneDataObject resultItemItem : resultItem.set) {
                                    if (CompareUtil.Instance().Compare(existItem, resultItemItem)) {
                                        exist++;
                                        break;
                                    }
                                }
                            }
                            if (exist == resultList.size() - 1) {
                                result.set.add(existItem);
                            }
                        }
                    }
                } else if (SetOperator.equals("sub")) {
                    SceneDataSet Set1 = parseSet(Repository, sv, descSet.get("Set1"), QueryAssist, isSingleValueSet);
                    SceneDataSet Set2 = parseSet(Repository, sv, descSet.get("Set2"), QueryAssist, isSingleValueSet);

                    for (SceneDataObject existItem : Set1.set) {
                        boolean exist = false;
                        for (SceneDataObject resultItemItem : Set2.set) {
                            if (CompareUtil.Instance().Compare(existItem, resultItemItem)) {
                                exist = true;
                                break;
                            }
                        }
                        if (!exist) {
                            result.set.add(existItem);
                        }
                    }
                }
            }
            return result;
        } else {

            QueryAssist QueryAssistInner = new QueryAssist();
            QueryAssistInner.rowChangeNeed = QueryAssist.rowChangeNeed;
            for (String key : QueryAssist.colChangeNeed.keySet()) {
                QueryAssistInner.colChangeNeed.put(key, QueryAssist.colChangeNeed.get(key));
            }
            result = (SceneDataSet) query(Repository, sv, descSet, QueryAssistInner);
            QueryAssist.merge(QueryAssistInner);
            return result;
        }
    }

    public static SceneDataSet parseSetRef(RepositoryBase Repository, SceneDataValue sv, String refString, QueryAssist QueryAssist,
                                           boolean isSingleValueSet, boolean isDeamon) throws Exception {
        SceneDataSet result = new SceneDataSet(isSingleValueSet);

        String[] splits = refString.split("'");
        Object parentData;
        int splits_index;
        if (splits[0].startsWith("ancestor_")) {
            int generate = Integer.parseInt(splits[0].substring("ancestor_".length()));
            Object tmp = sv;
            while (generate > 0) {
                if (tmp instanceof SceneDataValue) {
                    SceneDataValue tmpData = (SceneDataValue) tmp;
                    tmp = tmpData.parentObjectData;
                } else if (tmp instanceof SceneDataObject) {
                    SceneDataObject tmpData = (SceneDataObject) tmp;
                    tmp = tmpData.parentObjectData != null ? tmpData.parentObjectData : tmpData.parentArrayData;
                }
                generate--;
            }
            parentData = tmp;
            splits_index = 1;
        } else {
            parentData = Repository.objectData;
            splits_index = 0;
        }

        List<SceneDataValue> svList = new CopyOnWriteArrayList<SceneDataValue>();
        if (isSingleValueSet) {
            // 查询目标可以是value_object或者value_array
            if (parentData instanceof SceneDataValue) {
                SceneDataValue tmpData = (SceneDataValue) parentData;
                svList.add(tmpData);
            } else if (parentData != null) {
                SceneDataObject tmpData = (SceneDataObject) parentData;
                SceneDataValue svWrapper = new SceneDataValue(null, null, null, null);
                svWrapper.value_object = tmpData;
                svList.add(svWrapper);
            }
            for (int i = splits_index; i < splits.length; i++) {
                String split = splits[i];
                int index_ = split.indexOf('=');
                List<SceneDataValue> svListInner = new CopyOnWriteArrayList<SceneDataValue>();
                for (SceneDataValue svInner : svList) {
                    if (svInner.value_object != null) {
                        if (svInner.value_object.getRowChange() || svInner.value_object.hasColChange(split)) {
                            result.setRowChange(true);
                        }
                        if (index_ != -1) {
                            throw new Exception(refString);
                        }
                        svListInner.add(svInner.value_object.get(split));
                    } else if (svInner.value_array != null) {
                        if (index_ != -1) {
                            String propertyName = split.substring(0, index_);
                            String propertyValue = split.substring(index_ + 1);
                            if (svInner.value_array.getRowChange() || svInner.value_array.hasColChange(propertyName)) {
                                result.setRowChange(true);
                            }
                            for (SceneDataObject sdb : svInner.value_array.set) {
                                SceneDataObject sod = (SceneDataObject) sdb;
                                if (sod.containsKey(propertyName) && propertyValue.equals(sod.get(propertyName).value_prim.value)) {
                                    SceneDataValue svWrapper = new SceneDataValue(null, sod, propertyName, null);
                                    svWrapper.value_object = sod;
                                    svListInner.add(svWrapper);
                                }
                            }
                        } else {
                            if (svInner.value_array.getRowChange() || svInner.value_array.hasColChange(split)) {
                                result.setRowChange(true);
                            }
                            for (SceneDataObject sdb : svInner.value_array.set) {
                                SceneDataObject sod = (SceneDataObject) sdb;
                                svListInner.add(sod.get(split));
                            }
                        }
                    }
                }
                svList = svListInner;
            }
            result.singleValueSet = new CopyOnWriteArrayList<SceneDataValue>();
            for (SceneDataValue svTmp : svList) {
                if (svTmp.value_prim != null) {
                    result.singleValueSet.add(svTmp);
                    if (QueryAssist.rowChangeNeed) {
                        QueryAssist.rowFactor.valueChange.put(svTmp, true);
                    }
                } else if (svTmp.value_array != null) {
                    result.singleValueSet.addAll(svTmp.value_array.singleValueSet);
                    if (QueryAssist.rowChangeNeed) {
                        QueryAssist.rowFactor.rowChange.put(svTmp.value_array, true);
                    }
                }
            }
            for (SceneDataValue svTmp : svList) {
                if (svTmp.value_prim != null) {
                    if (svTmp.value_prim.change) {
                        result.setRowChange(true);
                    }
                } else if (svTmp.value_array != null) {
                    if (svTmp.value_array.getRowChange()) {
                        result.setRowChange(true);
                    }
                }
            }
        } else {
            // 查询目标可以是value_object或者value_array
            if (parentData instanceof SceneDataValue) {
                SceneDataValue tmpData = (SceneDataValue) parentData;
                svList.add(tmpData);
            } else if (parentData != null) {
                SceneDataObject tmpData = (SceneDataObject) parentData;
                SceneDataValue svWrapper = new SceneDataValue(null, null, null, null);
                svWrapper.value_object = tmpData;
                svList.add(svWrapper);
            }
            for (int i = splits_index; i < splits.length; i++) {
                String split = splits[i];
                int index_ = split.indexOf('=');
                List<SceneDataValue> svListInner = new CopyOnWriteArrayList<SceneDataValue>();
                for (SceneDataValue svInner : svList) {
                    if (svInner == null) {
                        continue;
                    }
                    if (svInner.value_object != null) {
                        if (svInner.value_object.getRowChange()) {
                            result.setRowChange(true);
                        }
                        if (index_ != -1) {
                            throw new Exception(refString);
                        }
                        svListInner.add(svInner.value_object.get(split));
                    } else if (svInner.value_array != null) {
                        if (index_ != -1) {
                            String propertyName = split.substring(0, index_);
                            String propertyValue = split.substring(index_ + 1);
                            if (svInner.value_array.getRowChange()) {
                                result.setRowChange(true);
                            }
                            for (SceneDataObject sdb : svInner.value_array.set) {
                                SceneDataObject sod = (SceneDataObject) sdb;
                                if (sod.containsKey(propertyName) && propertyValue.equals(sod.get(propertyName).value_prim.value)) {
                                    SceneDataValue svWrapper = new SceneDataValue(null, sod, propertyName, null);
                                    svWrapper.value_object = sod;
                                    svListInner.add(svWrapper);
                                }
                            }
                        } else {
                            if (svInner.value_array.getRowChange()) {
                                result.setRowChange(true);
                            }
                            for (SceneDataObject sdb : svInner.value_array.set) {
                                svListInner.add(sdb.get(split));
                            }
                        }
                    }
                }
                svList = svListInner;
            }
            if (isDeamon) {
                result.singleValueSet = new CopyOnWriteArrayList<>();
                result.singleValueSet.addAll(svList);
            } else {
                result.set = new CopyOnWriteArrayList<>();
                for (SceneDataValue svTmp : svList) {
                    if (svTmp == null) {
                        continue;
                    }
                    if (svTmp.value_array != null && svTmp.value_array.getRowChange()) {
                        result.setRowChange(true);
                    }

                    if (svTmp.value_array != null) {
                        result.set.addAll(svTmp.value_array.set);
                    }
                    if (QueryAssist.rowChangeNeed) {
                        QueryAssist.rowFactor.rowChange.put(svTmp.value_array, true);
                        for (String col : QueryAssist.colChangeNeed.keySet()) {
                            QueryAssist.colFactorMap.putIfAbsent(col, new InfluenceFactor());
                            QueryAssist.colFactorMap.get(col).colChange.putIfAbsent(svTmp.value_array, new ConcurrentHashMap<String, Boolean>());
                            QueryAssist.colFactorMap.get(col).colChange.get(svTmp.value_array).put(col, true);
                        }
                    }
                }
                if (!result.getRowChange()) {
                    for (SceneDataValue svTmp : svList) {
                        if (svTmp == null || svTmp.value_array == null) {
                            continue;
                        }
                        for (String col : svTmp.value_array.getColChange().keySet()) {
                            result.setColChange(col);
                        }
                    }
                }
            }
        }
        return result;
    }

    private static SceneDataSet query_select(SceneDataSet set, CriteriaBase criteria) {
        SceneDataSet result = new SceneDataSet(false);
        result.set = new CopyOnWriteArrayList<SceneDataObject>();
        if (set != null) {
            for (int i = 0; i < set.set.size(); i++) {
                SceneDataObject setValue = set.set.get(i);
                if (criteria.match(setValue)) {
                    result.set.add(setValue);
                }
            }

            if (set.getRowChange() || criteriaColumnChange(set.getColChange(), criteria) || criteriaValueChange(criteria)) {
                result.setRowChange(true);
            } else {
                for (String col : set.getColChange().keySet()) {
                    result.setColChange(col);
                }
            }
        }


        return result;
    }

    private static boolean criteriaColumnChange(Map<String, Boolean> colChangeMap, CriteriaBase criteria) {
        if (criteria instanceof Criteria.CriteriaAnd || criteria instanceof Criteria.CriteriaOr) {
            List<CriteriaBase> criteriaList;
            if (criteria instanceof Criteria.CriteriaAnd) {
                Criteria.CriteriaAnd Criteria_and = (Criteria.CriteriaAnd) criteria;
                criteriaList = Criteria_and.criteriaList;
            } else {
                Criteria.CriteriaOr Criteria_or = (Criteria.CriteriaOr) criteria;
                criteriaList = Criteria_or.criteriaList;
            }
            for (CriteriaBase criteriaInner : criteriaList) {
                boolean tmp = criteriaColumnChange(colChangeMap, criteriaInner);
                if (tmp) {
                    return true;
                }
            }
            return false;
        } else if (criteria instanceof Criteria.CriteriaNot) {
            Criteria.CriteriaNot Criteria_not = (Criteria.CriteriaNot) criteria;
            CriteriaBase criteriaInner = Criteria_not.criteria;
            return criteriaColumnChange(colChangeMap, criteriaInner);
        } else if (criteria instanceof Criteria.CriteriaDefault) {
            Criteria.CriteriaDefault CriteriaDefault = (Criteria.CriteriaDefault) criteria;
            for (String col : CriteriaDefault.column2MatchList.keySet()) {
                if (colChangeMap.containsKey(col)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean criteriaValueChange(CriteriaBase criteria) {
        if (criteria instanceof Criteria.CriteriaAnd || criteria instanceof Criteria.CriteriaOr) {
            List<CriteriaBase> criteriaList;
            if (criteria instanceof Criteria.CriteriaAnd) {
                Criteria.CriteriaAnd Criteria_and = (Criteria.CriteriaAnd) criteria;
                criteriaList = Criteria_and.criteriaList;
            } else {
                Criteria.CriteriaOr Criteria_or = (Criteria.CriteriaOr) criteria;
                criteriaList = Criteria_or.criteriaList;
            }
            for (CriteriaBase criteriaInner : criteriaList) {
                boolean tmp = criteriaValueChange(criteriaInner);
                if (tmp) {
                    return true;
                }
            }
            return false;
        } else if (criteria instanceof Criteria.CriteriaNot) {
            Criteria.CriteriaNot Criteria_not = (Criteria.CriteriaNot) criteria;
            CriteriaBase criteriaInner = Criteria_not.criteria;
            boolean tmp = criteriaValueChange(criteriaInner);
            return tmp;
        } else if (criteria instanceof Criteria.CriteriaDefault) {
            Criteria.CriteriaDefault CriteriaDefault = (Criteria.CriteriaDefault) criteria;
            for (String col : CriteriaDefault.column2MatchList.keySet()) {
                List<MatchBase> matchList = CriteriaDefault.column2MatchList.get(col);
                for (MatchBase matchBase : matchList) {
                    if (matchBase.change()) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private static Object query_aggregation(SceneDataSet set, Object Aggregation, JSONArray GroupBy) throws Exception {
        JSONArray agg_array = null;
        JSONObject AggregationObject = null;
        JSONArray AggregationArray = null;
        if (Aggregation instanceof JSONObject) {
            AggregationObject = (JSONObject) Aggregation;
            JSONObject cloneObject = (JSONObject) FastJsonUtil.Clone_JSON(AggregationObject);
            cloneObject.put("Name", cloneObject.get("Function"));
            agg_array = new JSONArray();

            agg_array.add(AggregationObject);
        } else if (Aggregation instanceof JSONArray) {
            AggregationArray = (JSONArray) Aggregation;
            agg_array = (JSONArray) FastJsonUtil.Clone_JSON(AggregationArray);
        }
        Map<String, Boolean> columnDic = new ConcurrentHashMap<String, Boolean>();
        for (Object aggItemObject : agg_array) {
            JSONObject aggItem = (JSONObject) aggItemObject;
            String Function = (aggItem.get("Function")).toString();
            if (!Function.equals("count")) {
                String Column = (aggItem.get("Column")).toString();
                columnDic.put(Column, true);
            }
        }
        Map<String, SceneDataObject> agg_count = new ConcurrentHashMap<String, SceneDataObject>();
        Map<String, Map<String, List<SceneDataPrimitive>>> agg_items = new ConcurrentHashMap<String, Map<String, List<SceneDataPrimitive>>>();

        for (int i = 0; i < set.set.size(); i++) {
            SceneDataObject setValue = set.set.get(i);
            String key;
            if (GroupBy == null) {
                key = "default";
            } else {
                JSONObject keyObject = new JSONObject();
                for (int ii = 0; ii < GroupBy.size(); ii++) {
                    String GroupByColumn = (GroupBy.get(ii)).toString();
                    SceneDataValue sdvColumn = setValue.get(GroupByColumn);
                    if (sdvColumn != null && sdvColumn.value_prim != null) {
                        keyObject.put(GroupByColumn, sdvColumn.value_prim.value);
                    } else {
                        keyObject.put(GroupByColumn, null);
                    }
                }
                key = JSONObject.toJSONString(keyObject, SerializerFeature.WriteMapNullValue);
            }
            if (!agg_items.containsKey(key)) {
                agg_items.put(key, new ConcurrentHashMap<String, List<SceneDataPrimitive>>());
            }
            if (!agg_count.containsKey(key)) {
                SceneDataObject countObject = new SceneDataObject(null, null, null, null, null, null, null);
                SceneDataValue sdvvvv = new SceneDataValue(null, countObject, "count", null);
                sdvvvv.value_prim = new SceneDataPrimitive();
                sdvvvv.value_prim.value = 0;
                countObject.put("count", sdvvvv);
                agg_count.put(key, countObject);
            }
            {
                SceneDataObject countObject = agg_count.get(key);
                SceneDataValue countValue = countObject.get("count");
                countValue.value_prim.value = (Integer) countValue.value_prim.value + 1;
            }
            Map<String, List<SceneDataPrimitive>> itemListDic = agg_items.get(key);
            for (String dicKey : columnDic.keySet()) {
                if (!itemListDic.containsKey(dicKey)) {
                    itemListDic.put(dicKey, new CopyOnWriteArrayList<SceneDataPrimitive>());
                }
                List<SceneDataPrimitive> items = itemListDic.get(dicKey);
                if (setValue.containsKey(dicKey)) {
                    items.add(setValue.get(dicKey).value_prim);
                }
            }
        }

        if (GroupBy == null) {
            if (AggregationObject != null) {
                SceneDataPrimitive result = new SceneDataPrimitive();
                result.value = query_aggregationProcess(agg_count, agg_items, AggregationObject, "default");
                if (set.getRowChange()) {
                    result.change = true;
                } else {
                    String Function = (String) AggregationObject.get("Function");
                    if (!Function.equals("count")) {
                        String Column = (String) AggregationObject.get("Column");
                        if (set.hasColChange(Column)) {
                            result.change = true;
                        }
                    }
                }
                return result;
            } else {
                SceneDataObject result = new SceneDataObject(null, null, null, null, null, null, null);
                for (Object agg_oneObject : AggregationArray) {
                    JSONObject agg_one = (JSONObject) agg_oneObject;
                    String Name = (agg_one.get("Name")).toString();
                    SceneDataValue sdvvv = new SceneDataValue(null, null, null, null);
                    sdvvv.finish = true;
                    sdvvv.value_prim = new SceneDataPrimitive();
                    sdvvv.value_prim.value = query_aggregationProcess(agg_count, agg_items, agg_one, "default");
                    result.put(Name, sdvvv);
                }
                if (set.getRowChange()) {
                    for (Object aggItemObject : agg_array) {
                        JSONObject aggItem = (JSONObject) aggItemObject;
                        String Name = (aggItem.get("Name")).toString();
                        result.setColChange(Name);
                    }
                } else {
                    for (Object aggItemObject : agg_array) {
                        JSONObject aggItem = (JSONObject) aggItemObject;
                        String Function = (aggItem.get("Function")).toString();
                        String Name = (aggItem.get("Name")).toString();
                        if (!Function.equals("count")) {
                            String Column = (aggItem.get("Column")).toString();
                            if (set.hasColChange(Column)) {
                                result.setColChange(Name);
                            }
                        }
                    }
                }
                return result;
            }
        } else {
            SceneDataSet result = new SceneDataSet(false);
            result.set = new CopyOnWriteArrayList<SceneDataObject>();
            for (String key : agg_count.keySet()) {
                JSONObject keyObject = JSON.parseObject(key);
                SceneDataObject resultItem = new SceneDataObject(null, null, null, null, null, null, null);
                for (String keyObjectOneKey : keyObject.keySet()) {
                    Object keyObjectOneValue = keyObject.get(keyObjectOneKey);
                    SceneDataValue sdvvv = new SceneDataValue(null, resultItem, keyObjectOneKey, null);
                    sdvvv.value_prim = new SceneDataPrimitive();
                    sdvvv.value_prim.value = keyObjectOneValue;
                    sdvvv.finish = true;
                    resultItem.put(keyObjectOneKey, sdvvv);
                }
                for (Object agg_oneObject : AggregationArray) {
                    JSONObject agg_one = (JSONObject) agg_oneObject;
                    String Name = (agg_one.get("Name")).toString();
                    SceneDataValue sdvvv = new SceneDataValue(null, resultItem, Name, null);
                    sdvvv.finish = true;
                    sdvvv.value_prim = new SceneDataPrimitive();
                    sdvvv.value_prim.value = query_aggregationProcess(agg_count, agg_items, agg_one, key);
                    resultItem.put(Name, sdvvv);
                }
                result.set.add(resultItem);
            }
            if (set.getRowChange()) {
                result.setRowChange(true);
            } else {
                for (Object o : GroupBy) {
                    String GroupByColumn = o.toString();
                    if (set.hasColChange(GroupByColumn)) {
                        result.setRowChange(true);
                    }
                }
            }
            if (!result.getRowChange()) {
                for (Object aggItemObject : agg_array) {
                    JSONObject aggItem = (JSONObject) aggItemObject;
                    String Function = (aggItem.get("Function")).toString();
                    if (!Function.equals("count")) {
                        String Column = (aggItem.get("Column")).toString();
                        String Name = (aggItem.get("Name")).toString();
                        if (set.hasColChange(Column)) {
                            result.setColChange(Name);
                        }
                    }
                }
            }
            return result;
        }
    }

    private static Object query_aggregationProcess(Map<String, SceneDataObject> agg_count,
                                                   Map<String, Map<String, List<SceneDataPrimitive>>> agg_items, JSONObject agg_obj, String key) throws Exception {
        Object result = null;
        String Function = (agg_obj.get("Function")).toString();
        if (Function.equals("count")) {
            if (!agg_count.containsKey(key)) {
                result = 0;
            } else {
                result = agg_count.get(key).get("count").value_prim.value;
            }
        } else {
            String Column = (agg_obj.get("Column")).toString();
            List<SceneDataPrimitive> agg_items_one = new CopyOnWriteArrayList<SceneDataPrimitive>();
            if (agg_items.get(key) != null && agg_items.get(key).containsKey(Column)) {
                agg_items_one = agg_items.get(key).get(Column);
            }
            switch (Function) {
                case "sum":
                case "avg":
                    double sum = 0.0;
                    int count_valid = 0;
                    for (SceneDataPrimitive jtSDP : agg_items_one) {
                        Object jt = jtSDP != null ? jtSDP.value : null;
                        if (jt != null) {
                            double jtValue = 0.0;
                            if (jt instanceof Integer) {
                                jtValue = ((Integer) jt).doubleValue();
                            } else if (jt instanceof Long) {
                                jtValue = ((Long) jt).doubleValue();
                            } else if (jt instanceof BigInteger) {
                                jtValue = ((BigInteger) jt).doubleValue();
                            } else if (jt instanceof Float) {
                                jtValue = ((Float) jt).doubleValue();
                            } else if (jt instanceof Double) {
                                jtValue = (Double) jt;
                            } else if (jt instanceof BigDecimal) {
                                jtValue = ((BigDecimal) jt).doubleValue();
                            } else {
                                //throw new Exception(jt.getClass().toString());
                            }
                            sum += jtValue;
                            count_valid++;
                        }
                    }
                    if (count_valid == 0) {
                    } else {
                        if (Function.equals("sum")) {
                            result = sum;
                        } else {
                            result = sum / count_valid;
                        }
                    }
                    break;
                case "max":
                case "min":
                    for (SceneDataPrimitive jtSDP : agg_items_one) {
                        Object jt = jtSDP != null ? jtSDP.value : null;
                        if (jt != null) {
                            double jtValue;
                            if (jt instanceof Integer) {
                                jtValue = ((Integer) jt).doubleValue();
                            } else if (jt instanceof Long) {
                                jtValue = ((Long) jt).doubleValue();
                            } else if (jt instanceof Float) {
                                jtValue = ((Float) jt).doubleValue();
                            } else {
                                jtValue = (Double) jt;
                            }
                            if (result == null) {
                                result = jt;
                            } else {
                                double resultValue;
                                if (result instanceof Integer) {
                                    resultValue = ((Integer) result).doubleValue();
                                } else if (result instanceof Long) {
                                    resultValue = ((Long) result).doubleValue();
                                } else if (result instanceof Float) {
                                    resultValue = ((Float) result).doubleValue();
                                } else {
                                    resultValue = (Double) result;
                                }
                                if (Function.equals("max") && resultValue < jtValue || Function.equals("min") && resultValue > jtValue) {
                                    result = jt;
                                }
                            }
                        }
                    }
                    break;
                case "equal_value":
                    Double value = null;
                    boolean equal = true;
                    for (SceneDataPrimitive jtSDP : agg_items_one) {
                        Object jt = jtSDP != null ? jtSDP.value : null;
                        if (jt != null) {
                            if (value == null) {
                                value = (Double) jt;
                            } else {
                                if (value.doubleValue() != (Double) jt) {
                                    equal = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (equal) {
                        result = value;
                    }
                    break;
            }
        }
        return result;
    }

    private static SceneDataSet query_after1(SceneDataSet set, JSONArray OrderBy, JSONObject Limit) {
        SceneDataSet resultArray = new SceneDataSet(false);
        resultArray.set = new CopyOnWriteArrayList<SceneDataObject>();
        resultArray.set.addAll(set.set);
        if (OrderBy != null && OrderBy.size() > 0) {
            resultArray.set.sort(new ComparatorSceneDataObject(OrderBy));
        }
        if (Limit != null) {
            int Limit_Skip = Limit.getIntValue("Skip");
            int Limit_Count = Limit.getIntValue("Count");
            List<SceneDataObject> contentList = new CopyOnWriteArrayList<SceneDataObject>();
            for (int index_ = (int) Limit_Skip; index_ < resultArray.set.size() && index_ < Limit_Skip + Limit_Count; index_++) {
                contentList.add(resultArray.set.get(index_));
            }
            resultArray.set = contentList;
        }
        if (OrderBy != null && OrderBy.size() > 0 && Limit != null) {
            if (set.getRowChange()) {
                resultArray.setRowChange(true);
            } else {
                for (Object o : OrderBy) {
                    JSONObject item = (JSONObject) o;
                    String Column = (String) item.get("Column");
                    if (set.hasColChange(Column)) {
                        resultArray.setRowChange(true);
                    }
                }
            }
            if (!resultArray.getRowChange()) {
                for (String col : set.getColChange().keySet()) {
                    resultArray.setColChange(col);
                }
            }
        } else {
            resultArray.setRowChange(set.getRowChange());
            for (String col : set.getColChange().keySet()) {
                resultArray.setColChange(col);
            }
        }
        return resultArray;
    }

    private static SceneDataSet query_after2(SceneDataSet set, List<String> ReturnColumns, String UniqueReturnColumn) {
        SceneDataSet resultArray = new SceneDataSet(UniqueReturnColumn != null);
        resultArray.set = new CopyOnWriteArrayList<SceneDataObject>();
        resultArray.set.addAll(set.set);

        if (UniqueReturnColumn != null) {
            List<SceneDataValue> resultArray_new = new CopyOnWriteArrayList<SceneDataValue>();
            for (SceneDataObject setValue : resultArray.set) {
                resultArray_new.add(setValue.get(UniqueReturnColumn));
            }
            resultArray.singleValueSet = resultArray_new;
            if (set.getRowChange()) {
                resultArray.setRowChange(true);
            } else {
                if (set.hasColChange(UniqueReturnColumn)) {
                    resultArray.setRowChange(true);
                }
            }
            return resultArray;
        } else {
            Map<String, Boolean> ReturnColumnMap = new ConcurrentHashMap<String, Boolean>();
            for (String Column : ReturnColumns) {
                ReturnColumnMap.put(Column, true);
            }
            List<SceneDataObject> resultArray_new = new CopyOnWriteArrayList<SceneDataObject>();
            for (SceneDataObject setValue : resultArray.set) {
                SceneDataObject resultItem = new SceneDataObject(null, null, null, null, null, null, setValue);
                resultItem.fatherReturnColumnMap = ReturnColumnMap;
                resultArray_new.add(resultItem);
            }
            resultArray.set = resultArray_new;
            resultArray.setRowChange(set.getRowChange());
            for (Object jtoken : ReturnColumns) {
                String col = (jtoken).toString();
                if (set.hasColChange(col)) {
                    resultArray.setColChange(col);
                }
            }
            return resultArray;
        }
    }
}
