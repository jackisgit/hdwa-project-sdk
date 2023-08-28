package com.hdwa.sdk.entity.repository;


import com.hdwa.sdk.entity.scene.SceneDataObject;

public class ObjectInfo {
    public SceneDataObject obj;
    public String objId;
    public String infoCode;

    public ObjectInfo(SceneDataObject obj, String id, String info) {
        this.obj = obj;
        this.objId = id;
        this.infoCode = info;
    }
}
