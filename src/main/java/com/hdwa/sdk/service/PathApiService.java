package com.hdwa.sdk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.PathApiParam;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.utils.CalculateApiJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author abao
 * @since 2023/8/2
 * 路径接口方式查询服务
 */
@Slf4j
@Service
public class PathApiService {

    @Value("${project.id}")
    private String projectId;

    /**
     * 路径查询
     *
     * @param param
     * @return
     */
    public Object post(PathApiParam param) {
        try {
            JSONArray valuePath = param.getPath();
            RepositoryImpl repository = DataContainer.projectMap.get(projectId);
            if (repository == null) {
                return "null";
            }
            if (valuePath.size() == 0) {
                return repository.objectData.toJSON(1);
            }
            Object valueObject = CalculateApiJsonUtil.getValueObject(repository, valuePath);

            Object result = CalculateApiJsonUtil.getValueJson(valueObject);
            if (result instanceof JSONArray) {
                ((JSONArray) result).forEach(this::removeAttribute);
            } else if (result instanceof JSONObject) {
                removeAttribute(result);
            }
            return result;
        } catch (Exception e) {
            log.error("按路径查询接口出现异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除引用的属性
     *
     * @param o
     */
    private void removeAttribute(Object o) {
        JSONObject jsonObject = (JSONObject) o;
        jsonObject.remove("所在建筑");
        jsonObject.remove("所在楼层");
        jsonObject.remove("所在空间");
        jsonObject.remove("被设备控制");
        jsonObject.remove("控制设备");
        jsonObject.remove("被设备供电");
        jsonObject.remove("所属系统");
        jsonObject.remove("给设备供电");
        jsonObject.remove("关联系统");
    }
}
