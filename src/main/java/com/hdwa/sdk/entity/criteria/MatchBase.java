package com.hdwa.sdk.entity.criteria;


import com.hdwa.sdk.entity.scene.DataValue;

/**
 * 父类
 */
public abstract class MatchBase {
    public abstract boolean match(DataValue item);
    public abstract boolean change();
}
