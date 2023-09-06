package com.hdwa.sdk.service;

import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.entity.ExcelSheetEntity;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneObject;
import com.hdwa.sdk.entity.scene.SceneProperty;
import com.hdwa.sdk.enums.PointEnum;
import com.hdwa.sdk.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author abao
 * @since 2023/8/16
 * 点位服务
 */
@Slf4j
@Service
public class PointService {

    @Value("${project.id}")
    private String projectId;

    @Value("${project.groupCode}")
    private String groupCode;

    @Value("${dirName.point}")
    private String point;

    @Value("${dirName.temp}")
    private String temp;


    /**
     * 下载点位数据
     *
     * @return
     */
    public Object downLoadPoint() {
        log.warn("************开始下载点位数据");
        long startTime = System.currentTimeMillis();
        try {
            String pointPath = groupCode + File.separator + projectId + File.separator + point;
            File tempFile = new File(pointPath + File.separator + temp);
            //删除临时目录文件
            FileUtil.deleteRecursive(tempFile);
            //创建temp根目录文件夹
            if (!tempFile.exists()) {
                log.warn("*****文件路径" + tempFile.getPath());
                Files.createDirectories(tempFile.toPath());
            }
            // TODO: 2023/8/16 需要定时加载最新的点位数据
            InputStream inputStream = readPointXlsx();
            Map<String, ExcelSheetEntity> pointMap = ExcelUtil.readExcel(inputStream);
            downPoint(pointMap, tempFile);

            //修改temp目录为当前时间目录
            FileUtil.tempToNowDate(tempFile, new File(pointPath));
            //只保留3个版本数据
            FileUtil.clearHistoryDirectory(new File(pointPath));
            log.warn("************结束下载-点位数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
            return "ok";
        } catch (Exception e) {
            log.error("下载点位数据异常", e);
        }
        return null;
    }

    /**
     * 加载点位数据
     *
     * @param repository
     * @return
     */
    public Object loadPointData(RepositoryImpl repository) {
        log.warn("************开始加载-点位数据");
        long startTime = System.currentTimeMillis();
        File maxDir = FileUtil.getMaxDir(new File(getPath()));
        try {
            JSONArray pointList = ReadFileUtil.readJsonArray(new File(maxDir + File.separator + UrlConstant.POINT_LIST));
            repository.InfoPointListArray.set = RWDUtil.array2SDOList(pointList);
            JSONArray pointRelation = ReadFileUtil.readJsonArray(new File(maxDir + File.separator + UrlConstant.POINT_RELATION));
            repository.InfoPointRelationArray.set = RWDUtil.array2SDOList(pointRelation);
            log.warn("************结束加载-点位数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("加载点位数据异常", e);
        }
        return "ok";
    }

    /**
     * 读取点位配置文件
     *
     * @return
     */
    private InputStream readPointXlsx() {
        //jar同级目录
        try {
            String filePath = System.getProperty(BaseDecConstant.USER_DIR) + File.separator + BaseDecConstant.POINT_FILE_NAME;
            InputStream inputStream = ResourceUtil.getStream(filePath);
            if (inputStream != null) {
                log.warn("*****加载pointExcel文件路径：" + filePath);
                return inputStream;
            }
        } catch (Exception e) {
        }
        //classPath/config目录默认文件
        try {
            String filePath = File.separator + BaseDecConstant.CONFIG_DIR + File.separator + BaseDecConstant.POINT_FILE_NAME;
            InputStream inputStream = new ClassPathResource(filePath).getInputStream();
            log.warn("*****加载pointExcel文件路径：" + filePath);
            return inputStream;
        } catch (Exception e) {
            log.error("*****加载pointExcel默认文件路径异常", e);
        }
        log.error("*****加载pointExcel文件路径：未找到任何文件");
        return null;
    }


    /**
     * 配置点位
     *
     * @param pointMap
     */
    private void downPoint(Map<String, ExcelSheetEntity> pointMap, File file) throws Exception {
        JSONArray pointArray = new JSONArray();
        JSONArray pointRelationArray = new JSONArray();
        pointMap.forEach((sheetName, excelSheetEntity) -> {
            for (int i = 1; i < excelSheetEntity.getContentList().size(); i++) {
                String ibmsSceneCode = getValue(excelSheetEntity, PointEnum.modularCode.getName(), i);
                String ibmsClassCode = getValue(excelSheetEntity, PointEnum.ibmsCode.getName(), i);
                String pointType = getValue(excelSheetEntity, PointEnum.pointType.getName(), i);
                String pointCode = getValue(excelSheetEntity, PointEnum.pointCode.getName(), i);
                //关键属性无数据就不解析
                if (StringUtils.isEmpty(ibmsSceneCode) || StringUtils.isEmpty(ibmsClassCode) || StringUtils.isEmpty(pointType) || StringUtils.isEmpty(pointCode)) {
                    continue;
                }
                String pointName = getValue(excelSheetEntity, PointEnum.pointName.getName(), i);
                String pointAlias = getValue(excelSheetEntity, PointEnum.pointAlias.getName(), i);
                String isShow = getValue(excelSheetEntity, PointEnum.isShow.getName(), i);
                String isKey = getValue(excelSheetEntity, PointEnum.isKey.getName(), i);
                String isBatchControl = getValue(excelSheetEntity, PointEnum.isBatchControl.getName(), i);
                String controlFeedbackCode = getValue(excelSheetEntity, PointEnum.controlFeedbackCode.getName(), i);
                String forceModel = getValue(excelSheetEntity, PointEnum.forceModel.getName(), i);
                String forceModelFeedback = getValue(excelSheetEntity, PointEnum.forceModelFeedback.getName(), i);

                pointCode = pointCode.substring(0, 1).toLowerCase() + pointCode.substring(1);

                if (StringUtils.isNotEmpty(controlFeedbackCode)) {
                    controlFeedbackCode = controlFeedbackCode.substring(0, 1).toLowerCase() + controlFeedbackCode.substring(1);
                }

                if (StringUtils.isNotEmpty(forceModel)) {
                    forceModel = forceModel.substring(0, 1).toLowerCase() + forceModel.substring(1);
                }

                if (StringUtils.isNotEmpty(forceModelFeedback)) {
                    forceModelFeedback = forceModelFeedback.substring(0, 1).toLowerCase() + forceModelFeedback.substring(1);
                }

                //写入到点位文件
                JSONObject pointObject = new JSONObject();
                pointObject.put(BaseDecConstant.SEQUENCE_NO, i);
                pointObject.put(BaseDecConstant.IBMS_SCENE_CODE, ibmsSceneCode);
                pointObject.put(BaseDecConstant.IBMS_CLASS_CODE, ibmsClassCode);
                pointObject.put(BaseDecConstant.INFO_CODE, pointCode);
                pointObject.put(BaseDecConstant.INFO_TYPE, pointType);
                pointObject.put(BaseDecConstant.INFO_NAME, pointName);
                pointObject.put(BaseDecConstant.INFO_ALIAS, pointAlias);
                pointObject.put(BaseDecConstant.IS_KEY_POINT, isKey != null && isKey.equals(BaseDecConstant.Y));
                pointObject.put(BaseDecConstant.IS_BATCH_CONTROL_PARAM, isBatchControl != null && isBatchControl.equals(BaseDecConstant.Y));
                pointObject.put(BaseDecConstant.IS_VISIBLE, isShow != null && isShow.equals(BaseDecConstant.Y));
                pointArray.add(pointObject);

                //写入到点位关系文件
                JSONObject pointRelationObject = new JSONObject();

                if (controlFeedbackCode != null) {
                    pointRelationObject.put(PointEnum.controlFeedback.getName(), controlFeedbackCode);
                }
                if (forceModel != null) {
                    pointRelationObject.put(PointEnum.forceModel.getName(), forceModel);
                }
                if (forceModelFeedback != null) {
                    pointRelationObject.put(PointEnum.forceModelFeedback.getName(), forceModelFeedback);
                }
                if (pointRelationObject.size() > 0) {
                    pointRelationObject.put(BaseDecConstant.IBMS_SCENE_CODE, ibmsSceneCode);
                    pointRelationObject.put(BaseDecConstant.IBMS_CLASS_CODE, ibmsClassCode);
                    pointRelationObject.put(PointEnum.controlPoint.getName(), pointCode);

                    pointRelationArray.add(pointRelationObject);
                }
            }
        });
        FileUtil.save(file + File.separator + UrlConstant.POINT_LIST, FastJsonUtil.toFormatString(pointArray));
        FileUtil.save(file + File.separator + UrlConstant.POINT_RELATION, FastJsonUtil.toFormatString(pointRelationArray));
    }

    /**
     * 获得单元格的值
     *
     * @param excelSheetEntity
     * @param name
     * @param index
     * @return
     */
    private String getValue(ExcelSheetEntity excelSheetEntity, String name, int index) {
        int indexTemp = -1;
        for (int i = 0; i < excelSheetEntity.getTitleColList().size(); i++) {
            if (excelSheetEntity.getTitleColList().get(i).equals(name)) {
                indexTemp = i;
                break;
            }
        }
        String value = excelSheetEntity.getContentList().get(index).get(indexTemp);
        if (StringUtils.isNotEmpty(value)) {
            value = value.trim();
        }
        return value;
    }

    /**
     * 获取根目录路径
     *
     * @return
     */
    private String getPath() {
        return groupCode + File.separator + projectId + File.separator + point;
    }


    /**
     * 根据point.xls过滤设备
     *
     * @param Repository
     * @throws Exception
     */
    public void filterPoint(RepositoryImpl Repository, SceneObject sceneObject) {
        log.warn("*****开始加载-点位配置过滤设备");
        long startTime = System.currentTimeMillis();
        try {
            // TODO: 2023/9/6 需要优化
            Map<String, String> SceneName2Code = new HashMap<>(16);
            for (com.hdwa.sdk.entity.scene.SceneDataObject SceneDataObject : Repository.ZKTSceneArray.set) {
                String id = (String) SceneDataObject.get("id").value_prim.value;
                String name = (String) SceneDataObject.get("名称").value_prim.value;
                String alias = null;
                if (SceneDataObject.containsKey("别名")) {
                    alias = (String) SceneDataObject.get("别名").value_prim.value;
                }
                SceneName2Code.put(name, id);
                if (alias != null && alias.length() > 0) {
                    String[] aliasArray = alias.split(",");
                    for (String one_alias : aliasArray) {
                        SceneName2Code.put(one_alias, id);
                    }
                }
            }
            Map<String, Map<String, String>> SceneClassName = new ConcurrentHashMap<String, Map<String, String>>();
            for (SceneDataObject SceneDataObject : Repository.ZKTClassArray.set) {
                String ibmsSceneCode = (String) SceneDataObject.get("ibmsSceneCode").value_prim.value;
                String ibmsClassCode = (String) SceneDataObject.get("ibmsClassCode").value_prim.value;
                String name = (String) SceneDataObject.get("名称").value_prim.value;
                String alias = null;
                if (SceneDataObject.containsKey("别名")) {
                    alias = (String) SceneDataObject.get("别名").value_prim.value;
                }
                SceneClassName.putIfAbsent(ibmsSceneCode, new ConcurrentHashMap<String, String>());
                SceneClassName.get(ibmsSceneCode).put(name, ibmsClassCode);
                if (alias != null && alias.length() > 0) {
                    String[] aliasArray = alias.split(",");
                    for (String one_alias : aliasArray) {
                        SceneClassName.get(ibmsSceneCode).put(one_alias, ibmsClassCode);
                    }
                }
            }
            Map<String, Boolean> SceneVisible = new ConcurrentHashMap<String, Boolean>();
            Map<String, Map<String, Boolean>> SceneClassVisible = new ConcurrentHashMap<String, Map<String, Boolean>>();
            for (SceneDataObject SceneDataObject : Repository.InfoPointListArray.set) {
                String ibmsSceneCode = (String) SceneDataObject.get("ibmsSceneCode").value_prim.value;
                String ibmsClassCode = (String) SceneDataObject.get("ibmsClassCode").value_prim.value;
                boolean isVisible = (Boolean) SceneDataObject.get("isVisible").value_prim.value;
                SceneClassVisible.putIfAbsent(ibmsSceneCode, new ConcurrentHashMap<String, Boolean>());
                SceneClassVisible.get(ibmsSceneCode).putIfAbsent(ibmsClassCode, false);
                SceneVisible.putIfAbsent(ibmsSceneCode, false);
                if (isVisible) {
                    SceneClassVisible.get(ibmsSceneCode).put(ibmsClassCode, true);
                    SceneVisible.put(ibmsSceneCode, true);
                }
            }
          /*  for (String SceneCode : SceneVisible.keySet()) {
                boolean isVisible = SceneVisible.get(SceneCode);
                log.warn("excel " + isVisible + "\t" + SceneCode);
            }
            for (String SceneCode : SceneClassVisible.keySet()) {
                Map<String, Boolean> classVisible = SceneClassVisible.get(SceneCode);
                for (String ClassCode : classVisible.keySet()) {
                    boolean isVisible = classVisible.get(ClassCode);
                    log.warn("excel " + isVisible + "\t" + SceneCode + "\t" + ClassCode);
                }
            }
*/

            String[] parentPathArray = {"场景数据'首页'模块统计'模块", "场景数据'首页'模块统计'设备运行统计"};
            for (String parentPath : parentPathArray) {
                List<Object> tmpList = PathUtil.getByPath(sceneObject, parentPath);
                for (Object tmp : tmpList) {
                    SceneProperty spInner = (SceneProperty) tmp;
                    if (spInner.propertyValueType.equals("static") && spInner.propertyValueSchema.equals("JSONArray")) {
                        for (SceneObject soScene : spInner.static_array) {
                            String SceneName = null;
                            for (SceneProperty spInner2 : soScene.propertyList) {
                                if (spInner2.propertyName.equals("名称")) {
                                    SceneName = spInner2.static_value;
                                    break;
                                }
                            }
                            if (!SceneName2Code.containsKey(SceneName)) {
                                continue;
                            }
                            String SceneCode = SceneName2Code.get(SceneName);
                            //log.warn("scan " + SceneCode + "\t" + parentPath + "'名称=" + SceneName);

                            if (!SceneVisible.containsKey(SceneCode)) {
                                continue;
                            }
                            boolean isVisible = SceneVisible.get(SceneCode);
                            if (!isVisible) {
                                //log.warn(" delete " + parentPath + "'名称=" + SceneName);
                                soScene.allow_pass = "0";
                            }
                        }

                        List<SceneObject> static_array = new ArrayList<SceneObject>();
                        boolean has_delete = false;
                        for (SceneObject soScene : spInner.static_array) {
                            if (soScene.allow_pass.equals("0")) {
                                has_delete = true;
                            } else {
                                static_array.add(soScene);
                            }
                        }
                        if (has_delete) {
                            spInner.static_array = static_array.toArray(new SceneObject[0]);
                        }
                    }
                }
            }


            parentPathArray = new String[]{"基础对象类型'设备", "基础对象'设备", "基础对象'品质", "基础对象'运营", "基础对象'安全", "基础对象'系统", "基础对象'逻辑编组", "场景数据'设备", "场景数据'品质",
                    "场景数据'运营", "场景数据'安全"};
            List<SceneProperty> equipTypeList = new ArrayList<>();
            List<String> SceneCodeList = new ArrayList<>();
            List<String> SceneNameList = new ArrayList<>();
            List<String> PathList = new ArrayList<>();

            for (String parentPath : parentPathArray) {
                List<Object> tmpList = PathUtil.getByPath(sceneObject, parentPath);
                for (Object tmp : tmpList) {
                    SceneProperty spInner = (SceneProperty) tmp;
                    if (spInner.propertyValueType.equals("static") && spInner.propertyValueSchema.equals("JSONArray")) {
                        for (SceneObject soScene : spInner.static_array) {
                            String SceneName = null;
                            for (SceneProperty spInner2 : soScene.propertyList) {
                                if (spInner2.propertyName.equals("名称")) {
                                    SceneName = spInner2.static_value;
                                    break;
                                }
                            }
                            if (!SceneName2Code.containsKey(SceneName)) {
                                continue;
                            }
                            String SceneCode = SceneName2Code.get(SceneName);

                            SceneProperty equipType = null;
                            SceneProperty equipType_gl = null;
                            SceneProperty gailan = null;
                            for (SceneProperty spInner2 : soScene.propertyList) {
                                if (spInner2.propertyName.equals("设备类型")) {
                                    equipType = spInner2;
                                } else if (spInner2.propertyName.equals("系统概览")) {
                                    if (spInner2.propertyValueType.equals("static") && spInner2.propertyValueSchema.equals("JSONArray")) {
                                        gailan = spInner2;
                                    } else if (spInner2.propertyValueType.equals("query") && spInner2.propertyValueSchema.equals("JSONArray")) {
                                        for (SceneProperty spInner2_att : spInner2.query_attached) {
                                            if (spInner2_att.propertyName.equals("设备类型")) {
                                                equipType_gl = spInner2_att;
                                                break;
                                            }
                                        }
                                    } else if (spInner2.propertyValueType.equals("custom")) {
                                        for (SceneProperty spInner2_att : spInner2.custom_object.propertyList) {
                                            if (spInner2_att.propertyName.equals("设备类型")) {
                                                equipType_gl = spInner2_att;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (equipType != null) {
                                equipTypeList.add(equipType);
                                SceneCodeList.add(SceneCode);
                                SceneNameList.add(SceneName);
                                PathList.add(parentPath + "'" + "名称=" + SceneName + "'" + "设备类型");
                            }
                            if (equipType_gl != null) {
                                equipTypeList.add(equipType_gl);
                                SceneCodeList.add(SceneCode);
                                SceneNameList.add(SceneName);
                                PathList.add(parentPath + "'" + "名称=" + SceneName + "'系统概览'" + "设备类型");
                            }
                            if (gailan != null) {
                                equipTypeList.add(gailan);
                                SceneCodeList.add(SceneCode);
                                SceneNameList.add(SceneName);
                                PathList.add(parentPath + "'" + "名称=" + SceneName + "'系统概览");
                            }
                        }
                    } else if (spInner.propertyValueType.equals("custom")) {
                        for (SceneProperty spInner2 : spInner.custom_object.propertyList) {
                            String SceneName = spInner2.propertyName;
                            if (!SceneName2Code.containsKey(SceneName)) {
                                continue;
                            }
                            String SceneCode = SceneName2Code.get(SceneName);

                            SceneProperty floor = null;
                            for (SceneProperty spInner3 : spInner2.custom_object.propertyList) {
                                if (spInner3.propertyName.equals("楼层数据")) {
                                    floor = spInner3;
                                    break;
                                }
                            }
                            if (floor == null) {
                                continue;
                            }

                            SceneProperty equipType = null;
                            SceneProperty gailan = null;
                            for (SceneProperty spInner2_att : floor.query_attached) {
                                if (spInner2_att.propertyName.equals("设备类型")) {
                                    equipType = spInner2_att;
                                } else if (spInner2_att.propertyName.equals("系统概览")) {
                                    gailan = spInner2_att;
                                }
                            }
                            if (equipType != null) {
                                equipTypeList.add(equipType);
                                SceneCodeList.add(SceneCode);
                                SceneNameList.add(SceneName);
                                PathList.add(parentPath + "'" + SceneName + "'楼层数据" + "'设备类型");
                            }
                            if (gailan != null) {
                                equipTypeList.add(gailan);
                                SceneCodeList.add(SceneCode);
                                SceneNameList.add(SceneName);
                                PathList.add(parentPath + "'" + SceneName + "'楼层数据" + "'系统概览");
                            }
                        }
                    }
                }


                for (int i = 0; i < equipTypeList.size(); i++) {
                    SceneProperty equipType = equipTypeList.get(i);
                    String SceneCode = SceneCodeList.get(i);
                    String SceneName = SceneNameList.get(i);
                    String Path = PathList.get(i);
                    if (SceneCode.equals("sbzwy")) {
                        continue;
                    }
                    //log.warn("scan " + SceneCode + "\t" + Path);
                    for (SceneObject soEquipType : equipType.static_array) {
                        SceneProperty spName = null;
                        SceneProperty spList = null;
                        for (SceneProperty spInner2 : soEquipType.propertyList) {
                            if (spInner2.propertyName.equals("清单")) {
                                spList = spInner2;
                            } else if (spInner2.propertyName.equals("名称")) {
                                spName = spInner2;
                            }
                        }
                        String ibmsSceneCode;
                        String ibmsClassCode;
                        {
                            ibmsSceneCode = SceneCode;
                            ibmsClassCode = SceneClassName.get(ibmsSceneCode).get(spName.static_value);
                        }
                        if (ibmsClassCode == null) {
                            continue;
                        }

                        if (!SceneClassVisible.containsKey(ibmsSceneCode) || !SceneClassVisible.get(ibmsSceneCode).containsKey(ibmsClassCode)) {
                            continue;
                        }
                        boolean isVisible = SceneClassVisible.get(ibmsSceneCode).get(ibmsClassCode);
                        if (!isVisible) {
                            //log.warn("delete " + SceneName + " " + SceneCode + "\t" + Path + "'名称=" + spName.static_value);
                            soEquipType.allow_pass = "0";
                        }
                    }
                    List<SceneObject> static_array = new ArrayList<SceneObject>();
                    boolean has_delete = false;
                    for (SceneObject soEquipType : equipType.static_array) {
                        if (soEquipType.allow_pass.equals("0")) {
                            has_delete = true;
                        } else {
                            static_array.add(soEquipType);
                        }
                    }
                    if (has_delete) {
                        equipType.static_array = static_array.toArray(new SceneObject[0]);
                    }
                }
            }
            log.warn("*****结束加载-点位配置过滤设备-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("过滤点位配置设备时异常", e);
        }
    }

}
