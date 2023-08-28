package com.hdwa.sdk.utils;

public class ValueObject {
    public int type;// 0:int;1:double;2:string
    public Long intValue;
    public Double doubleValue;
    public String stringValue;

    // public ValueObject(String text) {
    // try {
    // this.intValue = Long.parseLong(text);
    // } catch (Exception e) {
    // this.doubleValue = Double.parseDouble(text);
    // this.type = 1;
    // }
    // }

    public ValueObject() {

    }
    public ValueObject(int type, Long value) {
        this.type = type;
        this.intValue = value;
    }
    public ValueObject(int type, Double value) {
        this.type = type;
        this.doubleValue = value;
    }
    public ValueObject(String value) {
        this.type = 2;
        this.stringValue = value;
    }

    public Object value() {
        Object result;
        if (this.type == 0) {
            result = this.intValue;
        } else if (this.type == 1) {
            result = this.doubleValue;
        } else {
            result = this.stringValue;
        }
        return result;
    }

    public boolean is_null() {
        if (this.type == 2 && this.stringValue == null) {
            return true;
        } else {
            return false;
        }
    }
}
