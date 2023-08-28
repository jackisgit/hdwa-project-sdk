package com.hdwa.sdk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.utils.FastJsonUtil;
import com.hdwa.sdk.utils.FileUtil;
import com.hdwa.sdk.utils.OkHttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * @author abao
 * @since 2023/8/15
 * IBMS物理世界服务
 */
@Slf4j
@Service
public class IbmsPhysicalWorldService {

    @Value("${project.id}")
    private String projectId;

    @Value("${project.groupCode}")
    private String groupCode;

    @Value("${dirName.ibmsPhysicalWorld}")
    private String ibmsPhysicalWorld;

    @Value("${dirName.temp}")
    private String temp;

    @Value("${url.dmp}")
    private String dmpUrl;

    /**
     * 下载IBMS物理世界数据
     *
     * @return
     */
    public Object downLoadIbmsPhysicalWorldData() {
        log.warn("************开始下载-IBMS物理世界数据");
        long startTime = System.currentTimeMillis();
        try {
            String ibmsPhysicalPath = groupCode + File.separator + projectId + File.separator + ibmsPhysicalWorld;
            File tempFile = new File(ibmsPhysicalPath + File.separator + temp);
            //删除临时目录文件
            FileUtil.deleteRecursive(tempFile);
            //创建temp根目录文件夹
            if (!tempFile.exists()) {
                log.warn("*****文件路径" + tempFile.getPath());
                Files.createDirectories(tempFile.toPath());
            }
            //创建对象文件夹
            File object = new File(tempFile + File.separator + BaseDecConstant.OBJECT);
            if (!object.exists()) {
                Files.createDirectories(object.toPath());
            }
            downLoadSceneArray(tempFile);
            JSONArray classArray = downLoadIbmsClassArray(tempFile);
            downLoadIbmsObject(object, classArray);

            //修改temp目录为当前时间目录
            FileUtil.tempToNowDate(tempFile, new File(ibmsPhysicalPath));
            //只保留3个版本数据
            FileUtil.clearHistoryDirectory(new File(ibmsPhysicalPath));
            log.warn("************结束下载-IBMS物理世界数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
            return "ok";
        } catch (Exception e) {
            log.error("下载IBMS物理世界数据异常", e);
        }
        return null;
    }

    /**
     * 下载 基础对象-产品模块数据
     */
    private void downLoadSceneArray(File file) throws Exception {
        JSONObject requestBody = new JSONObject();
        addProject(requestBody);
        requestBody.put(BaseDecConstant.PATH, Arrays.asList(BaseDecConstant.BASE, BaseDecConstant.PRODUCT_MODULE));
        JSONArray sceneArray = OkHttpClientUtil.httpPost(requestBody, dmpUrl + UrlConstant.POST_URL).getJSONArray(BaseDecConstant.DATA);
        FileUtil.save(file + File.separator + UrlConstant.SCENE_ARRAY, FastJsonUtil.toFormatString(sceneArray));
    }


    /**
     * 下载 ibms类型定义数据
     */
    private JSONArray downLoadIbmsClassArray(File file) throws Exception {
        JSONObject requestBody = new JSONObject();
        addProject(requestBody);
        JSONObject condition = new JSONObject();
        condition.put(BaseDecConstant.IBMS, true);
        requestBody.put(BaseDecConstant.CONDITION, condition);
        JSONArray classArray = OkHttpClientUtil.httpPost(requestBody, dmpUrl + UrlConstant.LIST_CLASS_DEFINER_URL).getJSONArray(BaseDecConstant.DATA);
        FileUtil.save(file + File.separator + UrlConstant.CLASS_ARRAY, FastJsonUtil.toFormatString(classArray));
        return classArray;
    }


    /**
     * 下载 ibms对象数据
     */
    private void downLoadIbmsObject(File file, JSONArray classArray) {
        classArray.forEach(classItem -> {
            String ibmsSceneCode = ((JSONObject) classItem).getString(BaseDecConstant.IBMS_SCENE_CODE);
            String ibmsClassCode = ((JSONObject) classItem).getString(BaseDecConstant.IBMS_CLASS_CODE);
            JSONObject requestBody = new JSONObject();
            addProject(requestBody);
            JSONObject condition = new JSONObject();
            JSONObject criteria = new JSONObject();
            criteria.put(BaseDecConstant.IBMS_SCENE_CODE, ibmsSceneCode);
            criteria.put(BaseDecConstant.IBMS_CLASS_CODE, ibmsClassCode);
            condition.put(BaseDecConstant.CRITERIA_2, criteria);
            requestBody.put(BaseDecConstant.CONDITION, condition);
            try {
                JSONArray objectArray = OkHttpClientUtil.httpPost(requestBody, dmpUrl + UrlConstant.LIST_OBJECT_DATA_URL).getJSONArray(BaseDecConstant.DATA);
                //有数据的创建文件
                if (objectArray.size() == 0) {
                    return;
                }
                //创建文件夹
                File sceneDir = new File(file + File.separator + ibmsSceneCode);
                if (!sceneDir.exists()) {
                    sceneDir.mkdir();
                }
                FileUtil.save(sceneDir + File.separator + ibmsClassCode + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(objectArray));
            } catch (Exception e) {
                log.error("下载" + ibmsClassCode + "下的对象出现异常", e);
            }
        });
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
