package com.hdwa.sdk.entity.scene;

public class SceneObject {
    public SceneProperty[] propertyList;
    public String allow_pass = "1";// 1表示通过，0表示截断

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
