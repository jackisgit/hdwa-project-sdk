package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogOfDownload {
    public Date startTime;
    public Date endTime;
    public List<JSONObject> downloadList = new CopyOnWriteArrayList<JSONObject>();
    public String pathOld;
    public String pathNew;
    public long fileCountOld;
    public long fileSizeOld;
    public long fileCountNew;
    public long fileSizeNew;
    public List<JSONObject> changeList = new CopyOnWriteArrayList<JSONObject>();
    public List<JSONObject> errorList = new CopyOnWriteArrayList<JSONObject>();
    public String status;// running,success,failure

    public LogOfDownload() {
        this.startTime = new Date();
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
        DecimalFormat df = new DecimalFormat("###,###");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject result = new JSONObject();
        if (startTime != null) {
            result.put("startTime", sdf.format(startTime));
        }
        // {
        // JSONArray downloadArray = new JSONArray();
        // downloadArray.addAll(downloadList);
        // result.put("downloadArray", downloadArray);
        // }
        if (downloadList.size() > 0) {
            int downloadCount = 0;
            long donwloadSize = 0L;
            for (JSONObject item : downloadList) {
                downloadCount++;
                donwloadSize += item.getLong("filesize");
            }
            result.put("downloadCount", df.format(downloadCount));
            result.put("donwloadSize", df.format(donwloadSize));
        }
        {
            JSONArray changeArray = new JSONArray();
            changeArray.addAll(changeList);
            result.put("changeArray", changeArray);
        }
        {
            JSONArray errorArray = new JSONArray();
            errorArray.addAll(errorList);
            result.put("errorArray", errorArray);
        }
        if (pathOld != null) {
            result.put("pathOld", pathOld);
        }
        if (pathNew != null) {
            result.put("pathNew", pathNew);
        }
        if (endTime != null) {
            result.put("endTime", sdf.format(endTime));
        }
        if (status != null) {
            result.put("status", status);
        }
        return result;
    }
}
