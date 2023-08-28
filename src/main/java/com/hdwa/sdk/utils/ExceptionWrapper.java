package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.exception.ExceptionItem;

import java.util.List;

@SuppressWarnings("serial")
public class ExceptionWrapper extends Exception {
    public List<ExceptionItem> itemList;

    public ExceptionWrapper(List<ExceptionItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        for (ExceptionItem ExceptionItem : itemList) {
            sb.append("\n");
            sb.append("path: " + ExceptionItem.path + "\t" + "type: " + ExceptionItem.type + "\t" + "content: " + ExceptionItem.content);
        }
        return sb.toString();
    }

    public JSONArray getMessageArray() {
        JSONArray result = new JSONArray();
        for (ExceptionItem ExceptionItem : itemList) {
            JSONObject item = new JSONObject();
            item.put("path", ExceptionItem.path);
            item.put("type", ExceptionItem.type);
            item.put("content", ExceptionItem.content);
            result.add(item);
        }
        return result;
    }
}
