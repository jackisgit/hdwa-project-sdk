package com.hdwa.sdk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.PathApiParam;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.utils.CalculateApiJsonUtil;
import com.hdwa.sdk.utils.FilterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author abao
 * @since 2023/8/2
 * 路径接口方式查询服务
 */
@Slf4j
@Service
public class PathApiService {

    /**
     * 路径查询
     *
     * @param param
     * @return
     */
    public Object post(PathApiParam param) {
        JSONArray valuePath = param.getPath();
        try {
            RepositoryImpl repository = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);
            if (repository == null) {
                return "null";
            }
            if (valuePath.size() == 0) {
                return repository.objectData.toJSON(1);
            }
            Object valueObject = CalculateApiJsonUtil.getValueObject(repository, valuePath);

            return CalculateApiJsonUtil.getValueJson(valueObject);
        } catch (Exception e) {
            log.error("按路径查询接口出现异常：" + valuePath, e);
            throw e;
        }
    }


    /**
     * 路径查询分页
     *
     * @param param
     * @return
     */
    public Object postPage(PathApiParam param) {
        try {
            RepositoryImpl repository = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);
            if (repository == null) {
                return "null";
            }
            return FilterUtil.postPage(repository, (JSONObject) JSON.toJSON(param));
        } catch (Exception e) {
            log.error("按路径查询分页接口出现异常：" + param.getPath(), e);
            throw e;
        }
    }
}
