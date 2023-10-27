package com.hdwa.sdk.entity.scene;

/**
 * @author abao
 * @since 2023/7/25
 * 值对象
 */
public class DataPrimitive {

    public Object value;
    public boolean change;

    @Override
    public String toString() {
        return value.toString();
    }
}
