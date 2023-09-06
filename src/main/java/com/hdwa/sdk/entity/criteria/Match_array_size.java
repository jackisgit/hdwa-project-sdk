package com.hdwa.sdk.entity.criteria;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.SceneDataValue;

public class Match_array_size extends MatchBase {
    public boolean pass;
    public Object value;
    private boolean change = false;

    public Match_array_size(Object value, boolean change) {
        if (value instanceof JSONObject) {
            JSONObject valueObject = (JSONObject) value;
            if (valueObject.containsKey("pass")) {
                this.pass = true;
                return;
            }
        }

        this.value = value;
        this.change = change;
    }

    public boolean match(SceneDataValue item) {
        if (this.pass) {
            return true;
        }

        int itemValue = 0;
        if (item != null) {
            if (item.value_array.isSingleValueSet) {
                itemValue = item.value_array.singleValueSet.size();
            } else {
                itemValue = item.value_array.set.size();
            }
        }

        boolean result;
        if (this.value instanceof Integer) {
            int valueInt = (Integer) this.value;
            result = itemValue == valueInt;
        } else {
            result = true;
            JSONObject valueJSON = (JSONObject) this.value;
            for (String key : valueJSON.keySet()) {
                int valueInner = (Integer) valueJSON.get(key);
                boolean match_result = MatchUtil.match(key, itemValue, valueInner);
                if (!match_result) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    public boolean change() {
        return this.change;
    }
}
