package com.hdwa.sdk.entity.scene;

import java.util.Objects;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataPrimitive)) {
            return false;
        }

        DataPrimitive that = (DataPrimitive) o;

        if (change != that.change) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (change ? 1 : 0);
        return result;
    }
}
