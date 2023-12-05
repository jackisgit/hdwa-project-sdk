package com.hdwa.sdk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.DataObject;
import com.hdwa.sdk.entity.scene.DataPrimitive;
import com.hdwa.sdk.entity.scene.DataSet;
import com.hdwa.sdk.entity.scene.DataValue;
import com.hdwa.sdk.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author abao
 * @since 2023/8/15
 * IBMS物理世界服务
 */
@Slf4j
@Service
public class IbmsPhysicalWorldService {

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
    public boolean downLoadIbmsPhysicalWorldData() {
        log.warn("************开始下载-IBMS物理世界数据");
        long startTime = System.currentTimeMillis();
        try {
            File tempFile = new File(getPath() + File.separator + temp);
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
            FileUtil.tempToNowDate(tempFile, new File(getPath()));
            //只保留3个版本数据
            FileUtil.clearHistoryDirectory(new File(getPath()));
            log.warn("************结束下载-IBMS物理世界数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
            return true;
        } catch (Exception e) {
            log.error("下载IBMS物理世界数据异常", e);
            return false;
        }
    }

    /**
     * 加载IBMS对象数据
     *
     * @param repository
     */
    public boolean loadObjectData(RepositoryImpl repository) {
        log.warn("************开始加载-IBMS物理世界数据************");
        long startTime = System.currentTimeMillis();
        try {
            File maxDir = FileUtil.getMaxDir(new File(getPath()));
            //场景数据
            JSONArray sceneArray = ReadFileUtil.readJsonArray(new File(maxDir + File.separator + UrlConstant.SCENE_ARRAY));
            DataSet sceneSds = new DataSet(false);
            sceneSds.set = BaseApiUtil.arrayToSdoList(sceneArray);
            repository.ZKTSceneArray = sceneSds;

            //类型定义数据
            JSONArray classArray = ReadFileUtil.readJsonArray(new File(maxDir + File.separator + UrlConstant.CLASS_ARRAY));
            DataSet classSds = new DataSet(false, BaseDecConstant.ZKT_CLASS);
            classSds.set = BaseApiUtil.arrayToSdoList(classArray);
            repository.ZKTClassArray = classSds;

            Map<String, Map<String, DataValue>> objectArrayMap = new HashMap<>(16);
            classArray.forEach(item -> {
                JSONObject classItem = (JSONObject) item;
                String ibmsSceneCode = (String) classItem.get(BaseDecConstant.IBMS_SCENE_CODE);
                String ibmsClassCode = (String) classItem.get(BaseDecConstant.IBMS_CLASS_CODE);
                String code = (String) classItem.get(BaseDecConstant.CODE);
                String flag = (String) classItem.get(BaseDecConstant.FLAG);
                //不存在初始对象
                if (!objectArrayMap.containsKey(ibmsSceneCode)) {
                    objectArrayMap.put(ibmsSceneCode, new HashMap<>(16));
                }
                Map<String, DataValue> mapSdv = objectArrayMap.get(ibmsSceneCode);

                File objectFile = new File(maxDir + File.separator + BaseDecConstant.OBJECT + File.separator + ibmsSceneCode + File.separator + ibmsClassCode + UrlConstant.JSON_FILE);
                //没有数据的类型不做处理
                if (!objectFile.exists()) {
                    return;
                }
                //对象数据
                JSONArray objectArray;
                try {
                    objectArray = ReadFileUtil.readJsonArray(objectFile);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                DataSet objectSds = new DataSet(false, BaseDecConstant.ZKT_OBJECT + "/" + ibmsSceneCode + "/" + ibmsClassCode);
                //点位数据
                DataSet infoArray = repository.infoArrayDic.get(code);
                if (infoArray != null) {
                    infoArray.set.forEach(sdoTemp -> {
                        //采集点位
                        if (BaseApiUtil.getInfoTypeByTag(sdoTemp) != 0) {
                            objectSds.setColChange(sdoTemp.get(BaseDecConstant.CODE).valuePrim.value.toString());
                        }
                    });
                } else {
                    log.warn("***{}-{}-{}：缺少点位定义数据", ibmsSceneCode, ibmsClassCode, code);
                }
                DataValue objSdv = new DataValue(null, null, null, null);
                objectArray.forEach(temp -> {
                    JSONObject objItem = (JSONObject) temp;
                    DataObject sdo = repository.id2sdv.get(objItem.get(BaseDecConstant.ID));
                    if (sdo == null) {
                        return;
                    }
                    //有引用参数
                    if (flag != null && flag.equals(BaseDecConstant.REFERENCE)) {
                        objectSds.set.add(sdo);
                    } else {
                        //加入ibmsSceneCode属性
                        DataValue tempSdv = new DataValue(null, null, null, null);
                        tempSdv.finish = true;
                        tempSdv.valuePrim = new DataPrimitive();
                        tempSdv.valuePrim.value = ibmsSceneCode;
                        sdo.put(BaseDecConstant.IBMS_SCENE_CODE, tempSdv);
                        //加入ibmsClassCode属性
                        tempSdv = new DataValue(null, null, null, null);
                        tempSdv.finish = true;
                        tempSdv.valuePrim = new DataPrimitive();
                        tempSdv.valuePrim.value = ibmsClassCode;
                        sdo.put(BaseDecConstant.IBMS_CLASS_CODE, tempSdv);

                        DataObject sdoSub = new DataObject(repository, null, null, objSdv, null, null, sdo);
                        objectSds.set.add(sdoSub);
                    }
                });
                objSdv.valueArray = objectSds;
                objSdv.finish = true;
                mapSdv.put(ibmsClassCode, objSdv);
            });
            repository.ZKTObjectArrayDic = objectArrayMap;
            log.warn("************结束加载-IBMS物理世界数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
            return true;
        } catch (Exception e) {
            log.error("加载IBMS物理世界数据异常", e);
            return false;
        }
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
                if (objectArray == null || objectArray.size() == 0) {
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
        requestBody.put(BaseDecConstant.PROJECT_ID, BaseDecConstant.CURRENT_PROJECT_ID);
    }

    /**
     * 获取根目录路径
     *
     * @return
     */
    private String getPath() {
        return groupCode + File.separator + BaseDecConstant.CURRENT_PROJECT_ID + File.separator + ibmsPhysicalWorld;
    }

}
