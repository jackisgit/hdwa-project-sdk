package com.hdwa.sdk.service;

import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.entity.ExcelSheetEntity;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.DataObject;
import com.hdwa.sdk.entity.scene.DataObjectBase;
import com.hdwa.sdk.entity.scene.DataProperty;
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

/**
 * @author abao
 * @since 2023/8/16
 * 点位服务
 */
@Slf4j
@Service
public class PointService {

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
    public boolean downLoadPoint() {
        log.warn("************开始下载点位数据");
        long startTime = System.currentTimeMillis();
        try {
            String pointPath = groupCode + File.separator + BaseDecConstant.CURRENT_PROJECT_ID + File.separator + point;
            File tempFile = new File(pointPath + File.separator + temp);
            //删除临时目录文件
            FileUtil.deleteRecursive(tempFile);
            //创建temp根目录文件夹
            if (!tempFile.exists()) {
                log.warn("*****文件路径" + tempFile.getPath());
                Files.createDirectories(tempFile.toPath());
            }
            InputStream inputStream = readPointXlsx();

            Map<String, ExcelSheetEntity> pointMap = ExcelUtil.readExcel(inputStream);
            downPoint(pointMap, tempFile);

            //修改temp目录为当前时间目录
            FileUtil.tempToNowDate(tempFile, new File(pointPath));
            //只保留3个版本数据
            FileUtil.clearHistoryDirectory(new File(pointPath));
            DataContainer.pointMap = pointMap;
            log.warn("************结束下载-点位数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
            return true;
        } catch (Exception e) {
            log.error("下载点位数据异常", e);
            return false;
        }
    }

    /**
     * 加载点位数据
     *
     * @param repository
     * @return
     */
    public boolean loadPointData(RepositoryImpl repository) {
        log.warn("************开始加载-点位数据************");
        long startTime = System.currentTimeMillis();
        try {
            File maxDir = FileUtil.getMaxDir(new File(getPath()));
            JSONArray pointList = ReadFileUtil.readJsonArray(new File(maxDir + File.separator + UrlConstant.POINT_LIST));
            repository.InfoPointListArray.set = BaseApiUtil.arrayToSdoList(pointList);
            JSONArray pointRelation = ReadFileUtil.readJsonArray(new File(maxDir + File.separator + UrlConstant.POINT_RELATION));
            repository.InfoPointRelationArray.set = BaseApiUtil.arrayToSdoList(pointRelation);
            log.warn("************结束加载-点位数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
            return true;
        } catch (Exception e) {
            log.error("加载点位数据异常", e);
            return false;
        }
    }

    /**
     * 读取点位配置文件
     *
     * @return
     */
    public InputStream readPointXlsx() {
        //jar同级目录
        try {
            String filePath = System.getProperty(BaseDecConstant.USER_DIR) + File.separator + BaseDecConstant.POINT_FILE_NAME;
            InputStream inputStream = ResourceUtil.getStream(filePath);
            if (inputStream != null) {
                return inputStream;
            }
        } catch (Exception e) {
        }
        //classPath/config目录默认文件
        try {
            String filePath = File.separator + BaseDecConstant.CONFIG_DIR + File.separator + BaseDecConstant.POINT_FILE_NAME;
            return new ClassPathResource(filePath).getInputStream();
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
        return groupCode + File.separator + BaseDecConstant.CURRENT_PROJECT_ID + File.separator + point;
    }


    /**
     * 根据point.xls过滤设备
     *
     * @param Repository
     */
    public void filterPoint(RepositoryImpl Repository, DataObjectBase dataObjectBase) {
        log.warn("*****开始加载-点位配置过滤设备");
        long startTime = System.currentTimeMillis();
        try {
            Map<String, String> SceneName2Code = new HashMap<>(16);
            for (DataObject DataObject : Repository.ZKTSceneArray.set) {
                String id = (String) DataObject.get(BaseDecConstant.ID).valuePrim.value;
                String name = (String) DataObject.get(BaseDecConstant.NAME2).valuePrim.value;
                String alias = null;
                if (DataObject.containsKey(BaseDecConstant.ALIAS)) {
                    alias = (String) DataObject.get(BaseDecConstant.ALIAS).valuePrim.value;
                }
                SceneName2Code.put(name, id);
                if (alias != null && alias.length() > 0) {
                    String[] aliasArray = alias.split(",");
                    for (String one_alias : aliasArray) {
                        SceneName2Code.put(one_alias, id);
                    }
                }
            }
            Map<String, Map<String, String>> SceneClassName = new HashMap<>(16);
            for (DataObject DataObject : Repository.ZKTClassArray.set) {
                String ibmsSceneCode = (String) DataObject.get(BaseDecConstant.IBMS_SCENE_CODE).valuePrim.value;
                String ibmsClassCode = (String) DataObject.get(BaseDecConstant.IBMS_CLASS_CODE).valuePrim.value;
                String name = (String) DataObject.get(BaseDecConstant.NAME2).valuePrim.value;
                String alias = null;
                if (DataObject.containsKey(BaseDecConstant.ALIAS)) {
                    alias = (String) DataObject.get(BaseDecConstant.ALIAS).valuePrim.value;
                }
                SceneClassName.putIfAbsent(ibmsSceneCode, new HashMap<>(16));
                SceneClassName.get(ibmsSceneCode).put(name, ibmsClassCode);
                if (alias != null && alias.length() > 0) {
                    String[] aliasArray = alias.split(",");
                    for (String one_alias : aliasArray) {
                        SceneClassName.get(ibmsSceneCode).put(one_alias, ibmsClassCode);
                    }
                }
            }
            Map<String, Boolean> SceneVisible = new HashMap<>(16);
            Map<String, Map<String, Boolean>> SceneClassVisible = new HashMap<>(16);
            for (DataObject DataObject : Repository.InfoPointListArray.set) {
                String ibmsSceneCode = (String) DataObject.get(BaseDecConstant.IBMS_SCENE_CODE).valuePrim.value;
                String ibmsClassCode = (String) DataObject.get(BaseDecConstant.IBMS_CLASS_CODE).valuePrim.value;
                boolean isVisible = (Boolean) DataObject.get(BaseDecConstant.IS_VISIBLE).valuePrim.value;
                SceneClassVisible.putIfAbsent(ibmsSceneCode, new HashMap<>(16));
                SceneClassVisible.get(ibmsSceneCode).putIfAbsent(ibmsClassCode, false);
                SceneVisible.putIfAbsent(ibmsSceneCode, false);
                if (isVisible) {
                    SceneClassVisible.get(ibmsSceneCode).put(ibmsClassCode, true);
                    SceneVisible.put(ibmsSceneCode, true);
                }
            }
            for (String parentPath : BaseDecConstant.PARENT_PATH_ARRAY) {
                List<Object> tmpList = PathUtil.getByPath(dataObjectBase, parentPath);
                for (Object tmp : tmpList) {
                    DataProperty spInner = (DataProperty) tmp;
                    if (spInner.propertyValueType.equals(BaseDecConstant.STATIC) && spInner.propertyValueSchema.equals(BaseDecConstant.JSONARRAY)) {
                        for (DataObjectBase soScene : spInner.staticArray) {
                            String SceneName = null;
                            for (DataProperty spInner2 : soScene.propertyList) {
                                if (spInner2.propertyName.equals(BaseDecConstant.NAME2)) {
                                    SceneName = spInner2.staticValue;
                                    break;
                                }
                            }
                            if (!SceneName2Code.containsKey(SceneName)) {
                                continue;
                            }
                            String SceneCode = SceneName2Code.get(SceneName);
                            if (!SceneVisible.containsKey(SceneCode)) {
                                continue;
                            }
                            boolean isVisible = SceneVisible.get(SceneCode);
                            if (!isVisible) {
                                soScene.allowPass = "0";
                            }
                        }

                        List<DataObjectBase> static_array = new ArrayList<>();
                        boolean has_delete = false;
                        for (DataObjectBase soScene : spInner.staticArray) {
                            if (soScene.allowPass.equals("0")) {
                                has_delete = true;
                            } else {
                                static_array.add(soScene);
                            }
                        }
                        if (has_delete) {
                            spInner.staticArray = static_array.toArray(new DataObjectBase[0]);
                        }
                    }
                }
            }

            List<DataProperty> equipTypeList = new ArrayList<>();
            List<String> SceneCodeList = new ArrayList<>();
            for (String parentPath : BaseDecConstant.PARENT_PATH_ARRAY_2) {
                List<Object> tmpList = PathUtil.getByPath(dataObjectBase, parentPath);
                for (Object tmp : tmpList) {
                    DataProperty spInner = (DataProperty) tmp;
                    if (spInner.propertyValueType.equals(BaseDecConstant.STATIC) && spInner.propertyValueSchema.equals(BaseDecConstant.JSONARRAY)) {
                        for (DataObjectBase soScene : spInner.staticArray) {
                            String SceneName = null;
                            for (DataProperty spInner2 : soScene.propertyList) {
                                if (spInner2.propertyName.equals(BaseDecConstant.NAME2)) {
                                    SceneName = spInner2.staticValue;
                                    break;
                                }
                            }
                            if (!SceneName2Code.containsKey(SceneName)) {
                                continue;
                            }
                            String SceneCode = SceneName2Code.get(SceneName);

                            DataProperty equipType = null;
                            DataProperty equipType_gl = null;
                            DataProperty gailan = null;
                            for (DataProperty spInner2 : soScene.propertyList) {
                                if (spInner2.propertyName.equals(BaseDecConstant.DEVICE_TYPE)) {
                                    equipType = spInner2;
                                } else if (spInner2.propertyName.equals(BaseDecConstant.SYSTEM_OVERVIEW)) {
                                    if (spInner2.propertyValueType.equals(BaseDecConstant.STATIC) && spInner2.propertyValueSchema.equals(BaseDecConstant.JSONARRAY)) {
                                        gailan = spInner2;
                                    } else if (spInner2.propertyValueType.equals(BaseDecConstant.QUERY) && spInner2.propertyValueSchema.equals(BaseDecConstant.JSONARRAY)) {
                                        for (DataProperty spInner2_att : spInner2.queryAttached) {
                                            if (spInner2_att.propertyName.equals(BaseDecConstant.DEVICE_TYPE)) {
                                                equipType_gl = spInner2_att;
                                                break;
                                            }
                                        }
                                    } else if (spInner2.propertyValueType.equals(BaseDecConstant.CUSTOM)) {
                                        for (DataProperty spInner2_att : spInner2.customObject.propertyList) {
                                            if (spInner2_att.propertyName.equals(BaseDecConstant.DEVICE_TYPE)) {
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
                            }
                            if (equipType_gl != null) {
                                equipTypeList.add(equipType_gl);
                                SceneCodeList.add(SceneCode);
                            }
                            if (gailan != null) {
                                equipTypeList.add(gailan);
                                SceneCodeList.add(SceneCode);
                            }
                        }
                    } else if (spInner.propertyValueType.equals(BaseDecConstant.CUSTOM)) {
                        for (DataProperty spInner2 : spInner.customObject.propertyList) {
                            String SceneName = spInner2.propertyName;
                            if (!SceneName2Code.containsKey(SceneName)) {
                                continue;
                            }
                            String SceneCode = SceneName2Code.get(SceneName);

                            DataProperty floor = null;
                            for (DataProperty spInner3 : spInner2.customObject.propertyList) {
                                if (spInner3.propertyName.equals(BaseDecConstant.FLOOR_DATA)) {
                                    floor = spInner3;
                                    break;
                                }
                            }
                            if (floor == null) {
                                continue;
                            }

                            DataProperty equipType = null;
                            DataProperty gailan = null;
                            for (DataProperty spInner2_att : floor.queryAttached) {
                                if (spInner2_att.propertyName.equals(BaseDecConstant.DEVICE_TYPE)) {
                                    equipType = spInner2_att;
                                } else if (spInner2_att.propertyName.equals(BaseDecConstant.SYSTEM_OVERVIEW)) {
                                    gailan = spInner2_att;
                                }
                            }
                            if (equipType != null) {
                                equipTypeList.add(equipType);
                                SceneCodeList.add(SceneCode);
                            }
                            if (gailan != null) {
                                equipTypeList.add(gailan);
                                SceneCodeList.add(SceneCode);
                            }
                        }
                    }
                }

                for (int i = 0; i < equipTypeList.size(); i++) {
                    DataProperty equipType = equipTypeList.get(i);
                    String SceneCode = SceneCodeList.get(i);
                    for (DataObjectBase soEquipType : equipType.staticArray) {
                        DataProperty spName = null;
                        for (DataProperty spInner2 : soEquipType.propertyList) {
                            if (spInner2.propertyName.equals(BaseDecConstant.NAME2)) {
                                spName = spInner2;
                            }
                        }
                        String ibmsClassCode = SceneClassName.get(SceneCode).get(spName.staticValue);
                        if (ibmsClassCode == null) {
                            continue;
                        }

                        if (!SceneClassVisible.containsKey(SceneCode) || !SceneClassVisible.get(SceneCode).containsKey(ibmsClassCode)) {
                            continue;
                        }
                        boolean isVisible = SceneClassVisible.get(SceneCode).get(ibmsClassCode);
                        if (!isVisible) {
                            soEquipType.allowPass = "0";
                        }
                    }
                    List<DataObjectBase> static_array = new ArrayList<>();
                    boolean has_delete = false;
                    for (DataObjectBase soEquipType : equipType.staticArray) {
                        if (soEquipType.allowPass.equals("0")) {
                            has_delete = true;
                        } else {
                            static_array.add(soEquipType);
                        }
                    }
                    if (has_delete) {
                        equipType.staticArray = static_array.toArray(new DataObjectBase[0]);
                    }
                }
            }
            log.warn("*****结束加载-点位配置过滤设备-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("过滤点位配置设备时异常", e);
            throw e;
        }
    }

}
