package com.hdwa.sdk.entity.scene;

/**
 * @author abao
 * @since 2023/7/25
 * 场景对象
 */
public class SceneObject {

    /**
     * 属性集合，一个包含多个对象 为一个整体
     */
    public SceneProperty[] propertyList;

    public String allow_pass = "1";

    public SceneProperty[] getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(SceneProperty[] propertyList) {
        this.propertyList = propertyList;
    }

    public String getAllow_pass() {
        return allow_pass;
    }

    public void setAllow_pass(String allow_pass) {
        this.allow_pass = allow_pass;
    }
}
