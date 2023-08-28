package com.hdwa.sdk.criteria;


import com.hdwa.sdk.entity.scene.SceneDataValue;
import com.hdwa.sdk.utils.DataUtil;

import java.util.HashSet;
import java.util.List;

public class Match_array_ne extends MatchBase {
    public boolean pass;
    public HashSet<Object> value;
    private boolean change = false;

    public Match_array_ne(HashSet<Object> value, boolean change) {
        this.value = value;
        this.change = change;
    }

    public boolean match(SceneDataValue item) {
        if (this.pass) {
            return true;
        }

        List<SceneDataValue> sdvList = null;
        if (item != null && item.value_array != null) {
            sdvList = (item.value_array.singleValueSet);
        }

        HashSet<Object> setInner = new HashSet<Object>();
        if (sdvList != null) {
            for (SceneDataValue sdvInner : sdvList) {
                setInner.add(DataUtil.primitive_normalize(sdvInner.value_prim.value));
            }
        }

        boolean result = false;
        for (Object itemValue : this.value) {
            if (!setInner.contains(itemValue)) {
                result = true;
                break;
            }
        }
        if (result) {
            return result;
        }
        for (Object itemValue : setInner) {
            if (!this.value.contains(itemValue)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean change() {
        return this.change;
    }
}
