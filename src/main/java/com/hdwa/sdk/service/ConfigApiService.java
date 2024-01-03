package com.hdwa.sdk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.DataObjectBase;
import com.hdwa.sdk.entity.scene.DataProperty;
import com.hdwa.sdk.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author abao
 * @since 2023/9/5
 * 动态接口服务
 */
@Slf4j
@Service
public class ConfigApiService {

    @Autowired
    private PointService pointService;

    @Value("${project.groupCode}")
    private String groupCode;

    @Value("${dirName.config}")
    private String config;

    @Value("${dirName.temp}")
    private String temp;

    @Value("${url.dmp}")
    private String dmpUrl;

    /**
     * 下载config接口文件
     */
    public boolean downLoadConfig() {
        try {
            log.warn("************开始下载-config接口数据");
            long startTime = System.currentTimeMillis();
            JSONObject requestBody = new JSONObject();
            addProject(requestBody);
            JSONObject jsonObject = OkHttpClientUtil.httpPost(requestBody, dmpUrl + UrlConstant.GET_CONFIG_API_URL);
            if (jsonObject == null) {
                log.error("****接口数据为空");
                return false;
            }

            File tempFile = new File(getPath() + File.separator + temp);
            //删除临时目录文件
            FileUtil.deleteRecursive(tempFile);
            //创建temp根目录文件夹
            if (!tempFile.exists()) {
                log.warn("*****文件路径" + tempFile.getPath());
                Files.createDirectories(tempFile.toPath());
            }

            FileUtil.save(tempFile + File.separator + BaseDecConstant.WD + "_" + BaseDecConstant.BASE_API_JSON + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(jsonObject));
            //修改temp目录为当前时间目录
            FileUtil.tempToNowDate(tempFile, new File(getPath()));
            //只保留3个版本数据
            FileUtil.clearHistoryDirectory(new File(getPath()));
            log.warn("************结束下载-config接口数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
            return true;
        } catch (Exception e) {
            log.error("下载config接口文件异常", e);
            return false;
        }
    }


    /**
     * 加载config接口数据
     *
     * @param repository
     */
    public boolean loadConfigData(RepositoryImpl repository) {
        log.warn("************开始加载-config接口数据************");
        long startTime = System.currentTimeMillis();
        try {
            File maxDir = FileUtil.getMaxDir(new File(getPath()));
            JSONObject jsonObject = (JSONObject) ReadFileUtil.readJson(new File(maxDir + File.separator + BaseDecConstant.WD + "_" + BaseDecConstant.BASE_API_JSON + UrlConstant.JSON_FILE));
            //一级节点
            JSONArray levelOneJson = jsonObject.getJSONArray(BaseDecConstant.PROPERTY_LIST);
            JSONObject sceneJson = new JSONObject();
            JSONArray twoPropertyList = new JSONArray();

            levelOneJson.forEach(o -> {
                //二级节点
                JSONObject levelTowJson = (JSONObject) o;
                //节点名称
                String propertyName = levelTowJson.getString(BaseDecConstant.PROPERTY_NAME);
                if (propertyName.equals(BaseDecConstant.GENERAL_QUERY)) {
                    generalQuery(repository, levelTowJson.getJSONObject(BaseDecConstant.CUSTOM_OBJECT));
                } else {
                    twoPropertyList.add(levelTowJson);
                }
            });
            sceneJson.put(BaseDecConstant.PROPERTY_LIST, twoPropertyList);
            DataObjectBase dataObjectBase = new DataObjectBase();
            FastJsonUtil.setJava(sceneJson, dataObjectBase);
            repository.sceneJSON = sceneJson;
            repository.dataObjectBase = dataObjectBase;
            //点位过滤配置
            pointService.filterPoint(repository, dataObjectBase);

            analysisData(repository);
            log.warn("************结束加载-config接口数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
            return true;
        } catch (Exception e) {
            log.error("加载config接口数据异常", e);
            return false;
        }
    }

    /**
     * 解析对象和属性
     *
     * @param repository
     */
    private void analysisData(RepositoryImpl repository) throws Exception {
        long startTime = System.currentTimeMillis();
        log.warn("*****开始-解析计算对象和属性");
        try {
            repository.property2SDV_enable = true;
            repository.property2SDV.clear();
            AnalysisApiJsonUtil.analysisMain(repository);
            List<List<DataProperty>> propertyList = CalculateApiJsonUtil.calculateProperty(repository);
            CalculateApiJsonUtil.calculateAll(repository, propertyList);
            repository.property2SDV_enable = false;
            repository.property2SDV.clear();
            log.warn("*****结束-解析计算对象和属性用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("解析-解析计算对象和属性-出现错误", e);
            throw e;
        }
    }

    /**
     * 获取根目录路径
     *
     * @return
     */
    private String getPath() {
        return groupCode + File.separator + BaseDecConstant.CURRENT_PROJECT_ID + File.separator + config;
    }

    /**
     * 解析generalQuery查询
     *
     * @param repository
     * @param customObject
     */
    private void generalQuery(RepositoryImpl repository, JSONObject customObject) {
        Map<String, JSONObject> queryMap = new HashMap<>(16);
        JSONArray propertyList = customObject.getJSONArray(BaseDecConstant.PROPERTY_LIST);
        propertyList.forEach(o -> {
            JSONObject property = (JSONObject) o;
            queryMap.put(property.getString(BaseDecConstant.PROPERTY_NAME), JSON.parseObject(property.getString(BaseDecConstant.QUERY_SQL)));
        });
        repository.general_queryMap = queryMap;
    }

    /**
     * 添加项目信息
     *
     * @param requestBody
     */
    private void addProject(JSONObject requestBody) {
        requestBody.put(BaseDecConstant.GROUP_CODE, groupCode);
        requestBody.put(BaseDecConstant.PROJECT_ID, BaseDecConstant.CURRENT_PROJECT_ID);
    }

}
