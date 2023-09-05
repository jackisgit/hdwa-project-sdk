package com.hdwa.sdk.service;

import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.entity.ExcelSheetEntity;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
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
import java.util.Map;

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
}
