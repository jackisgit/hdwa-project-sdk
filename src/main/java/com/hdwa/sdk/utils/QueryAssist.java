package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.repository.InfluenceFactor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QueryAssist {
    public boolean rowChangeNeed = false;
    public Map<String, Boolean> colChangeNeed = new ConcurrentHashMap<String, Boolean>();

    public InfluenceFactor rowFactor = new InfluenceFactor();
    public Map<String, InfluenceFactor> colFactorMap = new ConcurrentHashMap<String, InfluenceFactor>();

    public QueryAssist() {

    }

    public QueryAssist(boolean rowChangeNeed) {
        this.rowChangeNeed = rowChangeNeed;
    }

    public void merge(QueryAssist QueryAssistInner) {
        this.rowFactor.merge(QueryAssistInner.rowFactor);
        for (String key : QueryAssistInner.colFactorMap.keySet()) {
            InfluenceFactor InfluenceFactor = QueryAssistInner.colFactorMap.get(key);
            this.colFactorMap.putIfAbsent(key, new InfluenceFactor());
            this.colFactorMap.get(key).merge(InfluenceFactor);
        }
    }

    public JSONObject toJSON() throws Exception {
        JSONObject result = new JSONObject();
        if (rowChangeNeed) {
            result.put("base", this.rowFactor.toJSON());
            JSONObject col = new JSONObject();
            for (String key : this.colFactorMap.keySet()) {
                InfluenceFactor colFactor = this.colFactorMap.get(key);
                col.put(key, colFactor.toJSON());
            }
            result.put("col", col);
        } else {
        }
        return result;
    }
}
