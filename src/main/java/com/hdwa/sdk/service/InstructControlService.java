package com.hdwa.sdk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.entity.InstructControlParam;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.utils.ControlUtil;
import com.hdwa.sdk.utils.OkHttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author abao
 * @since 2023/9/7
 * 指令控制服务
 */
@Slf4j
@Service
public class InstructControlService {

    @Value("${project.groupCode}")
    private String groupCode;

    @Value("${url.iotProject}")
    private String iotProjectUrl;

    @Value("${url.monitor}")
    private String monitorUrl;

    @Autowired
    private ConfigApiService configApiService;


    /**
     * 指令控制
     *
     * @param param
     * @return
     */
    public Object control(InstructControlParam param) {
        param.setGroupCode(groupCode);
        param.setProjectId(BaseDecConstant.CURRENT_PROJECT_ID);
        param.getInfoValueSet().forEach((s, o) -> {
            if (o instanceof String) {
                try {
                    int parseValue = Integer.parseInt((String) o);
                    param.getInfoValueSet().put(s, parseValue);
                } catch (Exception e) {
                    double parseValue = Double.parseDouble((String) o);
                    param.getInfoValueSet().put(s, parseValue);
                }
            }
        });
        try {
            //组装要下发的指令
            JSONObject data = ControlUtil.setPoints(DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID), param);
            JSONArray points = data.getJSONArray("points");
            log.warn("*****下发控制指令参数：" + points);
            //下发操作
            JSONArray result = controlDelivery(points, param.getPath().toString());
            log.warn("*****控制指令反馈结果：" + result);
            JSONObject resultDate = new JSONObject();
            //处理返回结果
            ControlUtil.disposeResult(resultDate, result);

            //保存日志
            //ControlUtil.saveOperationLog(param.getUserId(), param.getUsername(), (List<SceneDataObject>) data.get("objectList"), param.getInfoValueSet(), result, monitorUrl);
            return resultDate;
        } catch (Exception e) {
            log.error("******下发控制指令异常", e);
        }
        return null;
    }

    /**
     * 单点位控制多设备
     *
     * @param param
     * @return
     */
    public Object controlByEquBatch(InstructControlParam param) {
        RepositoryImpl repository = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);
        JSONObject result = new JSONObject();
        JSONArray points = new JSONArray();
        param.getData().forEach(stringObjectMap -> {
            //查询对象
            JSONObject obj = repository.id2object.get(stringObjectMap.get(BaseDecConstant.ID).toString());
            //控制点名称
            String code = stringObjectMap.get(BaseDecConstant.CODE).toString();
            //控制值
            String value = stringObjectMap.get("value").toString();
            //功能号和仪表号
            String infoValue = (String) obj.get(code);
            //拆解
            int index_ = infoValue.lastIndexOf("-");
            String meter = infoValue.substring(0, index_);
            int funcId = Integer.parseInt(infoValue.substring(index_ + 1));
            JSONObject point = new JSONObject();
            point.put("meter", meter);
            point.put("funcid", funcId);
            point.put("data", value);
            points.add(point);
        });
        try {
            //控制请求
            log.warn("*****批量下发控制指令参数：" + points);
            JSONArray array = controlDelivery(points, param.getPath().toString());
            log.warn("*****批量下发控制指令反馈结果：" + array);
            //处理返回值
            ControlUtil.disposeResult(result, array);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.put("result", "failure");
        }
        return result;
    }


    /**
     * 控制命令下发
     *
     * @param points
     * @return
     * @throws Exception
     */
    private JSONArray controlDelivery(JSONArray points, String path) throws Exception {
        JSONObject postJSON = new JSONObject();
        postJSON.put("building", BaseDecConstant.CURRENT_PROJECT_ID.substring(2));
        postJSON.put("points", points);
        JSONArray data = OkHttpClientUtil.httpPost(postJSON, iotProjectUrl + UrlConstant.iot_project_control).getJSONArray("points");
        //new Thread(() -> refresh(points, path)).start();
        //refresh(points, path);
        return data;
    }


    /**
     * 如果下发的有手自动点位 就刷新接口，统计手自动数量
     *
     * @param points
     * @param path
     */
    private void refresh(JSONArray points, String path) {
        try {
            if (path.contains("照明") || path.contains("回路") || path.contains("编组") || path.contains("末端") || path.contains("空调")) {
                if (points.toString().contains(BaseDecConstant.MANUAL_AUTO_SET)) {
                    configApiService.analysisDataRefresh(DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID));
                }
            }
        } catch (Exception e) {
            log.error("***手自动统计刷新接口错误", e);
        }
    }
}
