package com.hdwa.sdk.entity.criteria;


import com.hdwa.sdk.entity.scene.SceneDataObject;

import java.util.List;

public class Criteria_and extends CriteriaBase {
    public List<CriteriaBase> criteriaList;

    public Criteria_and() {
        this.type = "and";
    }

    public boolean match(SceneDataObject item) {
        for (CriteriaBase criteria : this.criteriaList) {
            if (!criteria.match(item)) {
                return false;
            }
        }
        return true;
    }
}
