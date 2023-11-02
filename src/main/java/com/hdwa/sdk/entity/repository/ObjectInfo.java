package com.hdwa.sdk.entity.repository;


import com.hdwa.sdk.entity.scene.DataObject;

/**
 * 对象信息
 */
public class ObjectInfo {
    public DataObject obj;
    public String objId;
    public String infoCode;

    public ObjectInfo(DataObject obj, String id, String info) {
        this.obj = obj;
        this.objId = id;
        this.infoCode = info;
    }
}
