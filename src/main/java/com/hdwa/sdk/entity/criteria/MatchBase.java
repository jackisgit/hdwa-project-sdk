package com.hdwa.sdk.entity.criteria;


import com.hdwa.sdk.entity.scene.SceneDataValue;

/**
 * 父类
 */
public abstract class MatchBase {
    public abstract boolean match(SceneDataValue item);
    public abstract boolean change();
}
