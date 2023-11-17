package com.hdwa.sdk.entity.scene;

/**
 * @author abao
 * @since 2023/7/25
 * 场景对象
 */
public class DataObjectBase {

    /**
     * 属性集合，一个包含多个对象 为一个整体
     */
    public DataProperty[] propertyList;

    public String allowPass = "1";

    public DataProperty[] getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(DataProperty[] propertyList) {
        this.propertyList = propertyList;
    }

    public String getAllowPass() {
        return allowPass;
    }

    public void setAllowPass(String allowPass) {
        this.allowPass = allowPass;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
