package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class LogOfRun {
    public Date startTime;
    public Date endTime;
    public Map<String, String> resourceTime_new = new ConcurrentHashMap<String, String>();
    public Map<String, String> resourceTime_old = new ConcurrentHashMap<String, String>();
    public int step_count;
    public List<JSONObject> stepList = new CopyOnWriteArrayList<JSONObject>();
    public List<JSONObject> errorList = new CopyOnWriteArrayList<JSONObject>();
    public String status;// running,success,failure

    public LogOfRun() {
        this.startTime = new Date();
    }

    public String getTime_new(String name) {
        return this.resourceTime_new.get(name);
    }

    public void setTime_old(String name, String oldTime) {
        this.resourceTime_old.put(name, oldTime);
    }

    public void setTime_new(String name, String newTime) {
        this.resourceTime_new.put(name, newTime);
        String oldTime = resourceTime_old.get(name);
        log.warn(oldTime != null ? oldTime + " -> " + newTime : newTime);
    }

    public void error(String path, String message) {
        JSONObject item = new JSONObject();
        item.put("path", path);
        item.put("message", message);
        if (this.errorList.size() <= 1024) {
            this.errorList.add(item);
        }
    }

    public JSONObject toJSON() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject result = new JSONObject();
        if (startTime != null) {
            result.put("startTime", sdf.format(startTime));
        }
        String[] keys = resourceTime_new.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        JSONObject content = new JSONObject();
        for (String key : keys) {
            String time = (resourceTime_old.containsKey(key) && !resourceTime_old.get(key).equals(resourceTime_new.get(key))
                    ? resourceTime_old.get(key) + " -> " : "") + resourceTime_new.get(key);
            content.put(key, time);
        }
        result.put("content", content);
        if (this.step_count > 0) {
            result.put("step_count", step_count);
        }
        {
            JSONArray stepArray = new JSONArray();
            for (JSONObject step : stepList) {
                stepArray.add(step);
            }
            result.put("stepArray", stepArray);
        }
        {
            JSONArray errorArray = new JSONArray();
            for (JSONObject error : errorList) {
                errorArray.add(error);
            }
            result.put("errorArray", errorArray);
        }
        if (endTime != null) {
            result.put("endTime", sdf.format(endTime));
        }
        if (status != null) {
            result.put("status", status);
        }
        return result;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuffer sb = new StringBuffer();
        if (startTime != null) {
            sb.append("startTime: " + sdf.format(startTime));
        }
        String[] keys = resourceTime_new.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        for (String key : keys) {
            sb.append("<BR>");
            sb.append(key + ": " + (resourceTime_old.containsKey(key) ? resourceTime_old.get(key) + " -> " : "") + resourceTime_new.get(key));
        }
        if (endTime != null) {
            sb.append("<BR>");
            sb.append("endTime: " + sdf.format(endTime));
        }
        if (status != null) {
            sb.append("<BR>");
            sb.append("status: " + status);
        }
        return sb.toString();
    }
}
