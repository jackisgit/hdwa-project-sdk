package com.hdwa.sdk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.PathApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author abao
 * @since 2024/4/2
 * 手机端服务
 */
@Slf4j
@Service
public class MobileService {

    @Autowired
    private PathApiService pathApiService;

    private static final String ACCC_PREFIX = "ACCC";
    private static final String HVAC_PREFIX = "HVAC";
    private static final String SELT_PREFIX = "SELT";
    private static final String SEYJ_PREFIX = "SEYJ";
    private static final String SCENE_DATA = "[\"场景数据\",\"设备\",\"冷源\",\"";
    private static final String ENDPOINT_COLD_SOURCE = "\",\"系统\"]";
    private static final String ENDPOINT_HEATING_VENTILATION = "[\"基础对象\",\"设备\",\"末端\",\"手自动统计\"]";
    private static final String SELT_VENTILATION = "[\"基础对象\",\"品质\",\"公共照明\",\"回路和场景\"]";
    private static final String SEYJ_VENTILATION = "[\"基础对象\",\"品质\",\"夜景照明\",\"回路和场景\"]";
    private static final int STATUS_AUTO = 0;
    private static final int STATUS_MANUAL = 1;
    private static final int STATUS_BOTH = 2;
    private static final String NUMBER = "数量";
    private static final String MANUAL_AUTOMATIC_STATISTICS = "手自动统计";

    /**
     * 系统手自动状态
     *
     * @param params
     * @return
     */
    public JSONArray systemManual(Map<String, Object> params) {
        List<String> subSystem = (List<String>) params.get(BaseDecConstant.SUB_SYSTEM);
        JSONArray jsonArray = new JSONArray();
        subSystem.forEach(s -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(BaseDecConstant.SUB_SYSTEM, s);
            if (s.startsWith(ACCC_PREFIX)) {
                processAcccSystem(s, jsonObject);
            } else if (s.startsWith(HVAC_PREFIX)) {
                processHvacSystem(jsonObject);
            } else if (s.startsWith(SELT_PREFIX)) {
                lighting(jsonObject, SELT_VENTILATION);
            } else if (s.startsWith(SEYJ_PREFIX)) {
                lighting(jsonObject, SEYJ_VENTILATION);
            }
            jsonArray.add(jsonObject);
        });

        return jsonArray;
    }

    /**
     * 冷源系统
     *
     * @param subSystem
     */
    private void processAcccSystem(String subSystem, JSONObject result) {
        PathApiParam param = new PathApiParam();
        String str = SCENE_DATA + subSystem + ENDPOINT_COLD_SOURCE;
        param.setPath(JSONArray.parseArray(str));
        try {
            JSONObject jsonObject = (JSONObject) pathApiService.post(param);
            result.put(BaseDecConstant.STATUS, jsonObject.get(BaseDecConstant.MANUAL_AUTO_SET) == null ? "" : jsonObject.getIntValue(BaseDecConstant.MANUAL_AUTO_SET));
        } catch (Exception e) {
            result.put(BaseDecConstant.STATUS, "");
            log.error("****查询冷源数据出现异常" + param.getPath(), e);
        }
    }

    /**
     * 空调末端
     *
     */
    private void processHvacSystem(JSONObject result) {
        PathApiParam param = new PathApiParam();
        param.setPath(JSONArray.parseArray(ENDPOINT_HEATING_VENTILATION));
        try {
            JSONArray dataArray = (JSONArray) pathApiService.post(param);
            JSONObject data1 = (JSONObject) dataArray.get(0);
            JSONObject data2 = (JSONObject) dataArray.get(1);
            int manualCount = data1.getIntValue(NUMBER);
            int autoCount = data2.getIntValue(NUMBER);
            if (manualCount == 0 && autoCount > 0) {
                result.put(BaseDecConstant.STATUS, STATUS_AUTO);
            } else if (manualCount > 0 && autoCount == 0) {
                result.put(BaseDecConstant.STATUS, STATUS_MANUAL);
            } else {
                result.put(BaseDecConstant.STATUS, STATUS_BOTH);
            }
        } catch (Exception e) {
            result.put(BaseDecConstant.STATUS, "");
            log.error("****查询空调末端数据出现异常" + param.getPath(), e);
        }
    }

    /**
     * 照明
     *
     * @param result
     */
    private void lighting(JSONObject result, String path) {
        PathApiParam param = new PathApiParam();
        param.setPath(JSONArray.parseArray(path));
        try {
            JSONObject json = (JSONObject) pathApiService.post(param);
            JSONArray jsonArray = (JSONArray) json.get(MANUAL_AUTOMATIC_STATISTICS);
            JSONObject data1 = (JSONObject) jsonArray.get(0);
            JSONObject data2 = (JSONObject) jsonArray.get(1);
            int manualCount = data1.getIntValue(NUMBER);
            int autoCount = data2.getIntValue(NUMBER);
            if (manualCount == 0 && autoCount > 0) {
                result.put(BaseDecConstant.STATUS, STATUS_AUTO);
            } else if (manualCount > 0 && autoCount == 0) {
                result.put(BaseDecConstant.STATUS, STATUS_MANUAL);
            } else {
                result.put(BaseDecConstant.STATUS, STATUS_BOTH);
            }
        } catch (Exception e) {
            result.put(BaseDecConstant.STATUS, "");
            log.error("****查询照明数据出现异常" + param.getPath(), e);
        }
    }
}
