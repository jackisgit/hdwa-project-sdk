package com.hdwa.sdk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.utils.FastJsonUtil;
import com.hdwa.sdk.utils.FileUtil;
import com.hdwa.sdk.utils.OkHttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author abao
 * @since 2023/8/15
 * ibms逻辑编组服务
 */
@Slf4j
@Service
public class IbmsLogicalGroupService {

    @Value("${project.id}")
    private String projectId;

    @Value("${project.groupCode}")
    private String groupCode;

    @Value("${dirName.ibmsLogicalGroup}")
    private String ibmsLogicalGroup;

    @Value("${dirName.temp}")
    private String temp;

    @Value("${url.monitor}")
    private String monitorUrl;

    @Autowired
    private PhysicalWorldService physicalWorldService;

    /**
     * 下载IBMS逻辑编组数据
     *
     * @return
     */
    public Object downLoadLogicalGroupData() {
        log.warn("************开始下载-IBMS逻辑编组数据");
        long startTime = System.currentTimeMillis();
        try {
            String ibmsPhysicalPath = groupCode + File.separator + projectId + File.separator + ibmsLogicalGroup;
            File tempFile = new File(ibmsPhysicalPath + File.separator + temp);
            //删除临时目录文件
            FileUtil.deleteRecursive(tempFile);
            //创建temp根目录文件夹
            if (!tempFile.exists()) {
                log.warn("*****文件路径" + tempFile.getPath());
                Files.createDirectories(tempFile.toPath());
            }
            //类型定义数据
            physicalWorldService.downClass(tempFile);
            //逻辑分组数据
            JSONArray groupArray = downLogicalGroupList(tempFile);
            downIbmsObjectGroup(groupArray, tempFile);
            List<String> list = new ArrayList<>();
            //修改temp目录为当前时间目录
            FileUtil.tempToNowDate(tempFile, new File(ibmsPhysicalPath));
            //只保留3个版本数据
            FileUtil.clearHistoryDirectory(new File(ibmsPhysicalPath));
            log.warn("************结束下载-IBMS逻辑编组数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");

            return "ok";
        } catch (Exception e) {
            log.error("下载IBMS逻辑编组数据异常", e);
        }
        return null;
    }

    /**
     * 下载逻辑分组集合
     *
     * @param file
     * @return
     * @throws Exception
     */
    private JSONArray downLogicalGroupList(File file) throws Exception {
        long startTime = System.currentTimeMillis();
        log.warn("*****开始下载-逻辑分组数据集合");
        JSONObject requestBody = new JSONObject();
        addProject(requestBody);
        JSONArray groupArray = OkHttpClientUtil.httpPost(requestBody, monitorUrl + UrlConstant.LOGICAL_GROUP_URL).getJSONArray(BaseDecConstant.CONTENT);
        FileUtil.save(file + File.separator + UrlConstant.IMBS_GROUP_ARRAY, FastJsonUtil.toFormatString(groupArray));
        log.warn("*****结束下载-逻辑分组数据集合-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        return groupArray;
    }


    /**
     * 下载ibms逻辑分组包含的设备对象数据
     *
     * @param groupArray
     * @param file
     */
    private void downIbmsObjectGroup(JSONArray groupArray, File file) {
        log.warn("*****开始下载-逻辑分组对象数据");
        long startTime = System.currentTimeMillis();
        //ibmsSceneCode-->< ibmsSceneClass-->objectId[] >
        Map<String, Map<String, List<String>>> sceneToClassToIds = new HashMap<>(16);
        groupArray.forEach(o -> {
            String ibmsSceneCode = ((JSONObject) o).getString(BaseDecConstant.IBMS_SCENE_CODE);
            String ibmsClassCode = ((JSONObject) o).getString(BaseDecConstant.IBMS_CLASS_CODE);
            String logicalGroupingId = ((JSONObject) o).getString(BaseDecConstant.LOGICAL_GROUPING_ID);
            //key sceneCode
            if (!sceneToClassToIds.containsKey(ibmsSceneCode)) {
                sceneToClassToIds.put(ibmsSceneCode, new HashMap<>(16));
            }
            //value ibmsSceneClass-->objectId[]
            Map<String, List<String>> classToObjIds = sceneToClassToIds.get(ibmsSceneCode);
            if (!classToObjIds.containsKey(ibmsClassCode)) {
                classToObjIds.put(ibmsClassCode, new CopyOnWriteArrayList<>());
            }
            classToObjIds.get(ibmsClassCode).add(logicalGroupingId);
        });

        sceneToClassToIds.forEach((sceneCode, classToObjIds) -> {
            //创建ibmsSceneCode的文件夹
            File sceneCodeDir = new File(file + File.separator + sceneCode);
            if (!sceneCodeDir.exists()) {
                sceneCodeDir.mkdir();
            }
            //下载ibmsClassCode逻辑编组对象数据
            classToObjIds.forEach((classCode, objIds) -> {
                log.warn("-----下载：" + sceneCode + "---" + classCode);
                JSONArray dataArray = new JSONArray();
                objIds.forEach(logicalGroupingId -> {
                    try {
                        JSONObject requestBody = new JSONObject();
                        addProject(requestBody);
                        requestBody.put(BaseDecConstant.LOGICAL_GROUPING_ID, logicalGroupingId);
                        JSONArray jsonArray = OkHttpClientUtil.httpPost(requestBody, monitorUrl + UrlConstant.LOGICAL_OBJECT_URL).getJSONArray(BaseDecConstant.CONTENT);
                        //添加逻辑分组id
                        jsonArray.forEach(o -> ((JSONObject) o).put(BaseDecConstant.LOGICAL_GROUPING_ID, logicalGroupingId));
                        dataArray.addAll(jsonArray);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                try {
                    FileUtil.save(sceneCodeDir + File.separator + classCode + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(dataArray));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        });
        log.warn("*****结束下载-逻辑分组对象数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
    }

    /**
     * 添加项目信息
     *
     * @param requestBody
     */
    private void addProject(JSONObject requestBody) {
        requestBody.put(BaseDecConstant.GROUP_CODE, groupCode);
        requestBody.put(BaseDecConstant.PROJECT_ID, projectId);
    }
}
