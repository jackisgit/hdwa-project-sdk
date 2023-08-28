package com.hdwa.sdk.criteria;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.SceneDataValue;

public class Match_contain extends MatchBase {
    public boolean pass;
    public String value;
    private boolean change = false;

    public Match_contain(Object value, boolean change) {
        if (value instanceof JSONObject) {
            JSONObject valueObject = (JSONObject) value;
            if (valueObject.containsKey("pass")) {
                this.pass = true;
                return;
            }
        }

        this.value = (String) value;
        this.change = change;
    }

    public boolean match(SceneDataValue item) {
        if (this.pass) {
            return true;
        }

        String itemValue = null;
        if (item != null) {
            itemValue = (String) (item.value_prim.value);
        }

        if (itemValue == null) {
            return false;
        }

        return itemValue.toLowerCase().contains(this.value.toLowerCase());
    }

    public boolean change() {
        return this.change;
    }
}
