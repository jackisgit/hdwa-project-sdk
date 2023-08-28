package com.hdwa.sdk.criteria;


import com.hdwa.sdk.entity.scene.SceneDataObject;

public class Criteria_not extends CriteriaBase {
    public CriteriaBase criteria;

    public Criteria_not() {
        this.type = "not";
    }

    public boolean match(SceneDataObject item) {
        if (criteria.match(item)) {
            return false;
        } else {
            return true;
        }
    }
}
