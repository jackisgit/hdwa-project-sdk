package com.hdwa.sdk.entity.criteria;


import com.hdwa.sdk.entity.scene.SceneDataValue;
import com.hdwa.sdk.utils.DataUtil;

import java.util.HashSet;

public class Match_notin extends MatchBase {
    public boolean pass;
    public HashSet<Object> value;
    private boolean change = false;

    public Match_notin(HashSet<Object> value, boolean change) {
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
        itemValue = DataUtil.primitive_normalize(itemValue);

        boolean result = !this.value.contains(itemValue);
        return result;
    }

    public boolean change() {
        return this.change;
    }
}
