package com.hdwa.sdk.criteria;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.SceneDataValue;

public class Match_e extends MatchBase {
    public boolean pass;
    public Object value;
    private boolean change = false;

    public Match_e(Object value, boolean change) {
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

        Object itemValue = null;
        if (item != null) {
            itemValue = (item.value_prim.value);
        }

        if (itemValue == null && value == null) {
            return true;
        }
        if (itemValue == null && value != null) {
            return false;
        }
        if (itemValue != null && value == null) {
            return false;
        }

        return MatchUtil.match("e", itemValue, value);
    }

    public boolean change() {
        return this.change;
    }
}
