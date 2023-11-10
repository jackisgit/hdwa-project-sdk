package com.hdwa.sdk.utils;

import com.hdwa.sdk.entity.exception.ExceptionItem;

import java.util.List;

public class ExceptionWrapper extends Exception {
    public List<ExceptionItem> itemList;

    public ExceptionWrapper(List<ExceptionItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        for (ExceptionItem ExceptionItem : itemList) {
            sb.append("\n");
            sb.append("path: ").append(ExceptionItem.path).append("\t").append("type: ").append(ExceptionItem.type).append("\t").append("content: ").append(ExceptionItem.content);
        }
        return sb.toString();
    }
}
