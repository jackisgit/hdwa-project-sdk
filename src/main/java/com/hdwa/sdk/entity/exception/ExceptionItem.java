package com.hdwa.sdk.entity.exception;

public class ExceptionItem extends Exception {
    public String path;
    public String type;
    public String content;

    public ExceptionItem(String path, String type, String content) {
        this.path = path;
        this.type = type;
        this.content = content;
    }
}
