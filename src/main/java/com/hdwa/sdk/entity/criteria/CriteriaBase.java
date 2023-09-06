package com.hdwa.sdk.entity.criteria;


import com.hdwa.sdk.entity.scene.SceneDataObject;

public abstract class CriteriaBase {
    public String type;// default and or not

    public abstract boolean match(SceneDataObject item);
}
