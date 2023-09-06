package com.hdwa.sdk.entity.criteria;


import com.hdwa.sdk.entity.scene.SceneDataValue;
import com.hdwa.sdk.utils.DataUtil;

import java.util.HashSet;
import java.util.List;

public class Match_array_exclude extends MatchBase {
    public boolean pass;
    public HashSet<Object> value;
    private boolean change = false;

    public Match_array_exclude(HashSet<Object> value, boolean change) {
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

        boolean result = true;
        if (sdvList != null) {
            for (SceneDataValue sdvInner : sdvList) {
                Object itemValue = sdvInner.value_prim.value;
                itemValue = DataUtil.primitive_normalize(itemValue);
                if (this.value.contains(itemValue)) {
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
