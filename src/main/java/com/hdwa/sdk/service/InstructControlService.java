package com.hdwa.sdk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.entity.InstructControlParam;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.utils.ControlUtil;
import com.hdwa.sdk.utils.OkHttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author abao
 * @since 2023/9/7
 * 指令控制服务
 */
@Slf4j
@Service
public class InstructControlService {

    @Value("${project.id}")
    private String projectId;

    @Value("${project.groupCode}")
    private String groupCode;

    @Value("${url.iotProject}")
    private String iotProjectUrl;

    /**
     * 指令控制
     *
     * @param param
     * @return
     */
    public Object control(InstructControlParam param) {
        param.setGroupCode(groupCode);
        param.setProjectId(projectId);
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
            JSONArray points = ControlUtil.setPoints(DataContainer.projectMap.get(projectId), param);
            log.warn("*****下发控制指令参数：" + points);
            //下发操作
            JSONArray result = controlDelivery(points);
            log.warn("*****控制指令反馈结果：" + result);
            JSONObject resultDate = new JSONObject();
            //处理返回结果
            ControlUtil.disposeResult(resultDate, result);
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
        RepositoryImpl repository = DataContainer.projectMap.get(projectId);
        JSONObject result = new JSONObject();
        JSONArray points = new JSONArray();
        param.getData().forEach(stringObjectMap -> {
            //查询对象
            JSONObject obj = repository.id2object.get(stringObjectMap.get("id").toString());
            //控制点名称
            String code = stringObjectMap.get("code").toString();
            //控制值
            String value = stringObjectMap.get("value").toString();
            //功能号和仪表号
            String infoValue = (String) obj.get(code);
            //拆解
            int index_ = infoValue.lastIndexOf("-");
            String meter = infoValue.substring(0, index_);
            int funcid = Integer.parseInt(infoValue.substring(index_ + 1));
            JSONObject point = new JSONObject();
            point.put("meter", meter);
            point.put("funcid", funcid);
            point.put("data", value);
            points.add(point);
        });
        try {
            //控制请求
            log.warn("*****批量下发控制指令参数：" + points);
            JSONArray array = controlDelivery(points);
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
    private JSONArray controlDelivery(JSONArray points) throws Exception {
        JSONObject postJSON = new JSONObject();
        postJSON.put("building", projectId.substring(2));
        postJSON.put("points", points);
        return OkHttpClientUtil.httpPost(postJSON, iotProjectUrl + UrlConstant.iot_project_control).getJSONArray("points");
    }

}
