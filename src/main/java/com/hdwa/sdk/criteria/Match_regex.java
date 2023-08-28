package com.hdwa.sdk.criteria;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.SceneDataValue;

import java.util.regex.Pattern;

public class Match_regex extends MatchBase {
    public boolean pass;
    public String value;
    Pattern regex;
    private boolean change = false;

    public Match_regex(Object value, boolean change) {
        if (value instanceof JSONObject) {
            JSONObject valueObject = (JSONObject) value;
            if (valueObject.containsKey("pass")) {
                this.pass = true;
                return;
            }
        }

        this.value = (String) value;
        regex = Pattern.compile(this.value);
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

        if (itemValue == null) {
            return false;
        }

        return regex.matcher(itemValue.toString()).matches();
    }

    public boolean change() {
        return this.change;
    }
}
