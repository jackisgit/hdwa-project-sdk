package com.hdwa.sdk.entity.criteria;


import com.hdwa.sdk.entity.scene.SceneDataObject;

/**
 * 父类
 */
public abstract class CriteriaBase {
    public String type;
    public abstract boolean match(SceneDataObject item);
}
