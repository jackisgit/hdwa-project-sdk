package com.hdwa.sdk.entity.criteria;


import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataValue;

public class Match_array_elemMatch_all extends MatchBase {
    public boolean pass;
    public CriteriaBase value;
    private boolean change = false;

    public Match_array_elemMatch_all(CriteriaBase value, boolean change) {
        this.value = value;
        this.change = change;
    }

    public boolean match(SceneDataValue item) {
        if (this.pass) {
            return true;
        }

        if (item == null) {
            return false;
        }
        if (item.value_object != null) {
            SceneDataObject sdo = item.value_object;
            boolean match_result = this.value.match(sdo);
            return match_result;
        } else {
            boolean result = false;
            for (SceneDataObject sdo : item.value_array.set) {
                boolean match_result = this.value.match(sdo);
                if (match_result) {
                    result = true;
                    break;
                }
            }
            return result;
        }
    }

    public boolean change() {
        return this.change;
    }
}
