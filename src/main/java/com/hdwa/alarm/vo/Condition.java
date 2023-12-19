package com.hdwa.alarm.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * 报警定义条件
 **/
@Data
public class Condition {
    List<String> infoCode;
    List<JSONObject> infoCodes;
    private JSONObject configs;
    private String trigger;
    private String end;
    private int triggerUphold;
    private int endUphold;
    private JSONObject effectTime;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}