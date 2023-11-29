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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author abao
 * @since 2023/8/15
 * ibms逻辑编组服务
 */
@Slf4j
@Service
public class IbmsLogicalGroupService {

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
            File tempFile = new File(getPath() + File.separator + temp);
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
            //修改temp目录为当前时间目录
            FileUtil.tempToNowDate(tempFile, new File(getPath()));
            //只保留3个版本数据
            FileUtil.clearHistoryDirectory(new File(getPath()));
            log.warn("************结束下载-IBMS逻辑编组数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");

            return "ok";
        } catch (Exception e) {
            log.error("下载IBMS逻辑编组数据异常", e);
        }
        return null;
    }

    /**
     * 加载IBMS逻辑编组数据
     *
     * @return
     * @return
     */
    public String loadLogicalGroupData(RepositoryImpl repository) throws Exception {
        log.warn("************开始加载-IBMS逻辑编组数据");
        long startTime = System.currentTimeMillis();
        File maxDir = FileUtil.getMaxDir(new File(getPath()));
        loadGroupData(repository, maxDir);
        log.warn("************结束加载-IBMS逻辑编组数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        return "ok";
    }

    /**
     * 加载逻辑编组
     *
     * @param repository
     * @param maxDir
     */
    private void loadGroupData(RepositoryImpl repository, File maxDir) throws Exception {
        log.warn("*****开始加载-逻辑编组数据");
        long startTime = System.currentTimeMillis();
        try {
            //逻辑编组
            JSONArray groupArray = ReadFileUtil.readJsonArray(new File(maxDir + File.separator + UrlConstant.IMBS_GROUP_ARRAY));
            //添加一个id属性
            groupArray.forEach(o -> {
                JSONObject arrayItem = (JSONObject) o;
                arrayItem.put(BaseDecConstant.ID, arrayItem.get(BaseDecConstant.LOGICAL_GROUPING_ID));
            });
            DataSet sds = new DataSet(false, BaseDecConstant.IBMS_GROUP);
            sds.set = BaseApiUtil.arrayToSdoList(groupArray);
            repository.IBMSGroupArray = sds;
            File[] dirs = maxDir.listFiles();
            Arrays.stream(dirs)
                    .filter(File::isDirectory)
                    .forEach(dir -> {
                        Map<String, DataSet> ibmsClassMap = new HashMap<>(16);
                        Arrays.stream(Objects.requireNonNull(dir.listFiles())).forEach(file -> {
                            String classCode = file.getName().substring(0, file.getName().indexOf('.'));
                            //数据
                            JSONArray array;
                            try {
                                array = ReadFileUtil.readJsonArray(file);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            DataSet tempSds = new DataSet(false, BaseDecConstant.IBMS_GROUP_OBJECT + "/" + dir.getName() + "/" + classCode);
                            tempSds.set = BaseApiUtil.arrayToSdoList(array);
                            ibmsClassMap.put(classCode, tempSds);
                        });
                        repository.IBMSArrayDic.put(dir.getName(), ibmsClassMap);
                        //照明分组处理
                        if (dir.getName().equals(BaseDecConstant.GGZM) || dir.getName().equals(BaseDecConstant.YJZM)) {
                            Map<String, DataSet> arrayMap = repository.IBMSArrayDic.get(dir.getName());
                            levelGroupOneData(repository, dir, arrayMap);
                            levelGroupOneDataScene(repository, dir, arrayMap);
                            levelGroupTowData(repository, dir, arrayMap);
                            levelGroupTowDataScene(repository, dir, arrayMap);
                            lightingCircuit(repository, dir, arrayMap);
                            lightingScene(repository, dir, arrayMap);
                        }
                    });
            log.warn("*****结束加载-逻辑编组数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("加载逻辑编组数据异常", e);
            throw e;
        }
    }


    /**
     * 照明回路一级逻辑编组数据
     *
     * @param repository
     * @param dir
     */
    private void levelGroupOneData(RepositoryImpl repository, File dir, Map<String, DataSet> arrayMap) {
        try {
            JSONArray levelGroupOne = new JSONArray();
            repository.IBMSGroupArray.set.forEach(itemSdo -> {
                String parentId = itemSdo.get(BaseDecConstant.PARENT_ID).valuePrim.value.toString();
                String ibmsClassCode = itemSdo.get(BaseDecConstant.IBMS_CLASS_CODE).valuePrim.value.toString();
                String ibmsSceneCode = itemSdo.get(BaseDecConstant.IBMS_SCENE_CODE).valuePrim.value.toString();
                if (!ibmsSceneCode.equals(dir.getName()) || !ibmsClassCode.equals(BaseDecConstant.LIGHTING_CIRCUIT) || !"0".equals(parentId)) {
                    return;
                }
                String logicalGroupingName = itemSdo.get(BaseDecConstant.LOGICAL_GROUPING_NAME).valuePrim.value.toString();
                String logicalGroupingId = itemSdo.get(BaseDecConstant.LOGICAL_GROUPING_ID).valuePrim.value.toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(BaseDecConstant.ID, logicalGroupingId);
                jsonObject.put(BaseDecConstant.NAME, logicalGroupingName);
                levelGroupOne.add(jsonObject);
            });
            DataSet levelGroupSdsOne = new DataSet(false);
            levelGroupSdsOne.set = BaseApiUtil.arrayToSdoList(levelGroupOne);
            arrayMap.put(BaseDecConstant.PRIMARY_GROUPING, levelGroupSdsOne);

            FileUtil.save(groupCode + File.separator + BaseDecConstant.CURRENT_PROJECT_ID + File.separator + temp + File.separator + BaseDecConstant.TEMP2 + dir.getName() + "-" + BaseDecConstant.GROUP_ONE + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(levelGroupOne));
        } catch (Exception e) {
            log.error("处理照明一级逻辑编组数据异常", e);
        }
    }

    /**
     * 照明场景一级逻辑编组数据
     *
     * @param repository
     * @param dir
     */
    private void levelGroupOneDataScene(RepositoryImpl repository, File dir, Map<String, DataSet> arrayMap) {
        try {
            JSONArray levelGroupOne = new JSONArray();
            repository.IBMSGroupArray.set.forEach(itemSdo -> {
                String parentId = itemSdo.get(BaseDecConstant.PARENT_ID).valuePrim.value.toString();
                String ibmsClassCode = itemSdo.get(BaseDecConstant.IBMS_CLASS_CODE).valuePrim.value.toString();
                String ibmsSceneCode = itemSdo.get(BaseDecConstant.IBMS_SCENE_CODE).valuePrim.value.toString();
                if (!ibmsSceneCode.equals(dir.getName()) || !ibmsClassCode.equals(BaseDecConstant.LIGHTING_SCENE) || !"0".equals(parentId)) {
                    return;
                }
                String logicalGroupingName = itemSdo.get(BaseDecConstant.LOGICAL_GROUPING_NAME).valuePrim.value.toString();
                String logicalGroupingId = itemSdo.get(BaseDecConstant.LOGICAL_GROUPING_ID).valuePrim.value.toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(BaseDecConstant.ID, logicalGroupingId);
                jsonObject.put(BaseDecConstant.NAME, logicalGroupingName);
                levelGroupOne.add(jsonObject);
            });
            DataSet levelGroupSdsOne = new DataSet(false);
            levelGroupSdsOne.set = BaseApiUtil.arrayToSdoList(levelGroupOne);
            arrayMap.put(BaseDecConstant.PRIMARY_GROUPING_SCENE, levelGroupSdsOne);

            FileUtil.save(groupCode + File.separator + BaseDecConstant.CURRENT_PROJECT_ID + File.separator + temp + File.separator + BaseDecConstant.TEMP2 + dir.getName() + "-" + BaseDecConstant.GROUP_ONE_SCENE + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(levelGroupOne));
        } catch (Exception e) {
            log.error("处理照明一级逻辑编组数据异常", e);
        }
    }


    /**
     * 照明二级逻辑编组数据
     *
     * @param repository
     * @param dir
     */
    private void levelGroupTowData(RepositoryImpl repository, File dir, Map<String, DataSet> arrayMap) {
        try {
            JSONArray levelGroupTow = new JSONArray();
            repository.IBMSGroupArray.set.forEach(itemSdo -> {
                String parentId = itemSdo.get(BaseDecConstant.PARENT_ID).valuePrim.value.toString();
                String ibmsClassCode = itemSdo.get(BaseDecConstant.IBMS_CLASS_CODE).valuePrim.value.toString();
                String ibmsSceneCode = itemSdo.get(BaseDecConstant.IBMS_SCENE_CODE).valuePrim.value.toString();
                if (!ibmsSceneCode.equals(dir.getName()) || !ibmsClassCode.equals(BaseDecConstant.LIGHTING_CIRCUIT) || !"0".equals(parentId)) {
                    return;
                }
                String logicalGroupingName = itemSdo.get(BaseDecConstant.LOGICAL_GROUPING_NAME).valuePrim.value.toString();
                String logicalGroupingId = itemSdo.get(BaseDecConstant.LOGICAL_GROUPING_ID).valuePrim.value.toString();
                String firstCode = itemSdo.get(BaseDecConstant.FIRST_CODE) == null ? "" : itemSdo.get(BaseDecConstant.FIRST_CODE).valuePrim.value.toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(BaseDecConstant.ID, logicalGroupingId);
                jsonObject.put(BaseDecConstant.NAME, logicalGroupingName);
                jsonObject.put(BaseDecConstant.PRIMARY_GROUPING, parentId);
                jsonObject.put(BaseDecConstant.GROUPING_TYPE, firstCode);
                //楼层编码
                if (dir.getName().equals(BaseDecConstant.GGZM)) {
                    if (itemSdo.get(BaseDecConstant.FLOOR_ID) != null) {
                        String floorId = (String) itemSdo.get(BaseDecConstant.FLOOR_ID).valuePrim.value;
                        jsonObject.put(BaseDecConstant.FLOOR_CODE, floorId);
                    }
                }
                levelGroupTow.add(jsonObject);
            });
            DataSet levelGroupSdsOne = new DataSet(false);
            levelGroupSdsOne.set = BaseApiUtil.arrayToSdoList(levelGroupTow);
            arrayMap.put(BaseDecConstant.TWO_GROUPING, levelGroupSdsOne);

            FileUtil.save(groupCode + File.separator + BaseDecConstant.CURRENT_PROJECT_ID + File.separator + temp + File.separator + BaseDecConstant.TEMP2 + dir.getName() + "-" + BaseDecConstant.GROUP_TWO + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(levelGroupTow));
        } catch (Exception e) {
            log.error("处理照明二级逻辑编组数据异常", e);
        }
    }

    /**
     * 照明场景二级逻辑编组数据
     *
     * @param repository
     * @param dir
     */
    private void levelGroupTowDataScene(RepositoryImpl repository, File dir, Map<String, DataSet> arrayMap) {
        try {
            JSONArray levelGroupTow = new JSONArray();
            repository.IBMSGroupArray.set.forEach(itemSdo -> {
                String parentId = itemSdo.get(BaseDecConstant.PARENT_ID).valuePrim.value.toString();
                String ibmsClassCode = itemSdo.get(BaseDecConstant.IBMS_CLASS_CODE).valuePrim.value.toString();
                String ibmsSceneCode = itemSdo.get(BaseDecConstant.IBMS_SCENE_CODE).valuePrim.value.toString();
                if (!ibmsSceneCode.equals(dir.getName()) || !ibmsClassCode.equals(BaseDecConstant.LIGHTING_SCENE) || !"0".equals(parentId)) {
                    return;
                }
                String logicalGroupingName = itemSdo.get(BaseDecConstant.LOGICAL_GROUPING_NAME).valuePrim.value.toString();
                String logicalGroupingId = itemSdo.get(BaseDecConstant.LOGICAL_GROUPING_ID).valuePrim.value.toString();
                String firstCode = itemSdo.get(BaseDecConstant.FIRST_CODE) == null ? "" : itemSdo.get(BaseDecConstant.FIRST_CODE).valuePrim.value.toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(BaseDecConstant.ID, logicalGroupingId);
                jsonObject.put(BaseDecConstant.NAME, logicalGroupingName);
                jsonObject.put(BaseDecConstant.PRIMARY_GROUPING, parentId);
                jsonObject.put(BaseDecConstant.GROUPING_TYPE, firstCode);
                //楼层编码
                if (dir.getName().equals(BaseDecConstant.GGZM)) {
                    if (itemSdo.get(BaseDecConstant.FLOOR_ID) != null) {
                        String floorId = (String) itemSdo.get(BaseDecConstant.FLOOR_ID).valuePrim.value;
                        jsonObject.put(BaseDecConstant.FLOOR_CODE, floorId);
                    }
                }
                levelGroupTow.add(jsonObject);
            });
            DataSet levelGroupSdsOne = new DataSet(false);
            levelGroupSdsOne.set = BaseApiUtil.arrayToSdoList(levelGroupTow);
            arrayMap.put(BaseDecConstant.TWO_GROUPING_SCENE, levelGroupSdsOne);

            FileUtil.save(groupCode + File.separator + BaseDecConstant.CURRENT_PROJECT_ID + File.separator + temp + File.separator + BaseDecConstant.TEMP2 + dir.getName() + "-" + BaseDecConstant.GROUP_TWO_SCENE + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(levelGroupTow));
        } catch (Exception e) {
            log.error("处理照明场景二级逻辑编组数据异常", e);
        }
    }


    /**
     * 照明回路数据
     *
     * @param repository
     * @param dir
     */
    private void lightingCircuit(RepositoryImpl repository, File dir, Map<String, DataSet> arrayMap) {
        try {
            DataSet circuitSds = arrayMap.get(BaseDecConstant.LIGHTING_CIRCUIT) == null ? new DataSet(false) : arrayMap.get(BaseDecConstant.LIGHTING_CIRCUIT);
            JSONArray circuitArray = new JSONArray();
            //一级编组数据
            DataSet leveOne = arrayMap.get(BaseDecConstant.PRIMARY_GROUPING);
            //二级编组数据
            DataSet leveTwo = arrayMap.get(BaseDecConstant.TWO_GROUPING);
            circuitSds.set.forEach(temp -> {
                String logicalGroupingId = temp.get(BaseDecConstant.LOGICAL_GROUPING_ID).valuePrim.value.toString();

                DataObject sdoOne = null;
                DataObject sdoTwo = null;

                for (DataObject sdoInner : leveTwo.set) {
                    String id = sdoInner.get(BaseDecConstant.ID).valuePrim.value.toString();
                    if (id.equals(logicalGroupingId)) {
                        sdoTwo = sdoInner;
                        break;
                    }
                }

                String oneId = null;
                String twoId = null;
                if (sdoTwo != null) {
                    oneId = sdoTwo.get(BaseDecConstant.PRIMARY_GROUPING).valuePrim.value.toString();
                    twoId = sdoTwo.get(BaseDecConstant.ID).valuePrim.value.toString();
                    for (DataObject sdoInner : leveOne.set) {
                        String idInner = sdoInner.get(BaseDecConstant.ID).valuePrim.value.toString();
                        if (idInner.equals(oneId)) {
                            sdoOne = sdoInner;
                            break;
                        }
                    }
                }

                String objId = temp.get(BaseDecConstant.OBJ_ID).valuePrim.value.toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(BaseDecConstant.CIRCUIT_ID, objId);

                if (oneId != null && sdoOne != null && twoId != null) {
                    String groupType = (String) sdoTwo.get(BaseDecConstant.GROUPING_TYPE).valuePrim.value;
                    jsonObject.put(BaseDecConstant.GROUPING_TYPE, groupType);
                    jsonObject.put(BaseDecConstant.PRIMARY_GROUPING, oneId);
                    jsonObject.put(BaseDecConstant.PRIMARY_GROUPING_NAME, sdoOne.get(BaseDecConstant.NAME).valuePrim.value);
                    jsonObject.put(BaseDecConstant.TWO_GROUPING, twoId);
                    jsonObject.put(BaseDecConstant.TWO_GROUPING_NAME, sdoTwo.get(BaseDecConstant.NAME).valuePrim.value);
                    if (!repository.id2sdv.containsKey(objId)) {
                        log.warn(dir.getName() + " " + "回路不存在: " + objId);
                        return;
                    }

                    DataObject illuminationSdo = repository.id2sdv.get(objId);
                    DataValue illuminationArray = illuminationSdo.get(BaseDecConstant.DEVICE_CONTROLLED);
                    if (illuminationArray != null
                            && illuminationArray.valueArray != null
                            && illuminationArray.valueArray.set != null
                            && illuminationArray.valueArray.set.size() > 0) {

                        DataObject tempSdo = illuminationArray.valueArray.set.get(0);
                        jsonObject.put(BaseDecConstant.MODEL_CODE, tempSdo.get(BaseDecConstant.ID).valuePrim.value);
                        jsonObject.put(BaseDecConstant.MODEL_NAME, getName(tempSdo));

                        DataValue tempSdv = tempSdo.get(BaseDecConstant.POWERED_BY_EQUIPMENT);
                        if (tempSdv.valueArray.set.size() > 0) {
                            DataObject tempSdo2 = tempSdv.valueArray.set.get(0);
                            jsonObject.put(BaseDecConstant.DISTRIBUTION_BOX_CODE, tempSdo2.get(BaseDecConstant.ID).valuePrim.value);
                            jsonObject.put(BaseDecConstant.DISTRIBUTION_BOX_NAME, getName(tempSdo2));
                            // TODO: 2023/9/5 所在物业空间已经取消了
                      /*  SceneDataValue GeneralZoneArray = tempSdo2.get(BaseDecConstant.PROPERTY_SPACE);
                        if (GeneralZoneArray.valueArray.set.size() > 0) {
                            SceneDataObject GeneralZone = GeneralZoneArray.valueArray.set.get(0);
                            jsonObject.put(BaseDecConstant.ELECTRIC_WELL_CODE, GeneralZone.get(BaseDecConstant.ID).valuePrim.value);
                            jsonObject.put(BaseDecConstant.ELECTRIC_WELL_NAME, getName(GeneralZone));
                        }*/
                        }
                    }
                    if (dir.getName().equals(BaseDecConstant.GGZM)) {
                        DataValue floorArray = illuminationSdo.get(BaseDecConstant.PLACE_FLOOR);
                        if (floorArray != null && floorArray.valueArray != null && floorArray.valueArray.set != null
                                && floorArray.valueArray.set.size() == 1) {
                            DataObject floor = floorArray.valueArray.set.get(0);
                            jsonObject.put(BaseDecConstant.FLOOR_CODE, floor.get(BaseDecConstant.ID).valuePrim.value);
                            jsonObject.put(BaseDecConstant.FLOOR_NAME, getName(floor));
                        }
                    }
                }
                circuitArray.add(jsonObject);
            });
            DataSet circuit = new DataSet(false);
            circuit.set = BaseApiUtil.arrayToSdoList(circuitArray);
            arrayMap.put(BaseDecConstant.LOOP, circuit);
            FileUtil.save(groupCode + File.separator + BaseDecConstant.CURRENT_PROJECT_ID + File.separator + temp + File.separator + BaseDecConstant.TEMP2 + dir.getName() + "-" + BaseDecConstant.CIRCUIT + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(circuitArray));

            //加入回路信息
            circuit.set.forEach(sdo -> {
                String id = (String) sdo.get(BaseDecConstant.CIRCUIT_ID).valuePrim.value;
                if (repository.id2sdv.containsKey(id)) {
                    DataObject eqpSdo = repository.id2sdv.get(id);
                    //防止只有回路编组数据覆盖完整数据
                    if (eqpSdo.get(BaseDecConstant.PRIMARY_GROUPING_NAME) != null) {
                        if (eqpSdo.get(BaseDecConstant.PRIMARY_GROUPING_NAME).valuePrim.value != null || eqpSdo.get(BaseDecConstant.TWO_GROUPING_NAME).valuePrim.value != null) {
                            return;
                        }
                    }
                    DataValue sdv = new DataValue(null, null, null, null);
                    sdv.valuePrim = new DataPrimitive();
                    sdv.valuePrim.change = false;
                    //eqpSdo.put(BaseDecConstant.MODEL_NAME, sdo.containsKey(BaseDecConstant.MODEL_NAME) ? sdo.get(BaseDecConstant.MODEL_NAME) : sdv);
                    //eqpSdo.put(BaseDecConstant.DISTRIBUTION_BOX_NAME, sdo.containsKey(BaseDecConstant.DISTRIBUTION_BOX_NAME) ? sdo.get(BaseDecConstant.DISTRIBUTION_BOX_NAME) : sdv);
                    //eqpSdo.put(BaseDecConstant.DISTRIBUTION_BOX_CODE, sdo.containsKey(BaseDecConstant.DISTRIBUTION_BOX_CODE) ? sdo.get(BaseDecConstant.DISTRIBUTION_BOX_CODE) : sdv);
                    //eqpSdo.put(BaseDecConstant.ELECTRIC_WELL_NAME, sdo.containsKey(BaseDecConstant.ELECTRIC_WELL_NAME) ? sdo.get(BaseDecConstant.ELECTRIC_WELL_NAME) : sdv);
                    //eqpSdo.put(BaseDecConstant.ELECTRIC_WELL_CODE, sdo.containsKey(BaseDecConstant.ELECTRIC_WELL_CODE) ? sdo.get(BaseDecConstant.ELECTRIC_WELL_CODE) : sdv);
                    eqpSdo.put(BaseDecConstant.PRIMARY_GROUPING_NAME, sdo.containsKey(BaseDecConstant.PRIMARY_GROUPING_NAME) ? sdo.get(BaseDecConstant.PRIMARY_GROUPING_NAME) : sdv);
                    eqpSdo.put(BaseDecConstant.TWO_GROUPING_NAME, sdo.containsKey(BaseDecConstant.TWO_GROUPING_NAME) ? sdo.get(BaseDecConstant.TWO_GROUPING_NAME) : sdv);
                    eqpSdo.put(BaseDecConstant.GROUPING_TYPE, sdo.containsKey(BaseDecConstant.GROUPING_TYPE) ? sdo.get(BaseDecConstant.GROUPING_TYPE) : sdv);
                    if (dir.getName().equals(BaseDecConstant.GGZM)) {
                        eqpSdo.put(BaseDecConstant.FLOOR_CODE, sdo.containsKey(BaseDecConstant.FLOOR_CODE) ? sdo.get(BaseDecConstant.FLOOR_CODE) : sdv);
                        eqpSdo.put(BaseDecConstant.FLOOR_NAME, sdo.containsKey(BaseDecConstant.FLOOR_NAME) ? sdo.get(BaseDecConstant.FLOOR_NAME) : sdv);
                    }
                }
            });
        } catch (Exception e) {
            log.error("处理照明回路编组数据异常", e);
        }
    }

    /**
     * 照明场景数据
     *
     * @param repository
     * @param dir
     */
    private void lightingScene(RepositoryImpl repository, File dir, Map<String, DataSet> arrayMap) {
        try {
            DataSet sceneSds = arrayMap.get(BaseDecConstant.LIGHTING_SCENE) == null ? new DataSet(false) : arrayMap.get(BaseDecConstant.LIGHTING_SCENE);
            JSONArray sceneArray = new JSONArray();
            //一级编组数据
            DataSet leveOne = arrayMap.get(BaseDecConstant.PRIMARY_GROUPING);
            //二级编组数据
            DataSet leveTwo = arrayMap.get(BaseDecConstant.TWO_GROUPING);
            sceneSds.set.forEach(temp -> {
                String logicalGroupingId = temp.get(BaseDecConstant.LOGICAL_GROUPING_ID).valuePrim.value.toString();

                DataObject sdoOne = null;
                DataObject sdoTwo = null;

                for (DataObject sdoInner : leveTwo.set) {
                    String id = sdoInner.get(BaseDecConstant.ID).valuePrim.value.toString();
                    if (id.equals(logicalGroupingId)) {
                        sdoTwo = sdoInner;
                        break;
                    }
                }

                String oneId = null;
                String twoId = null;
                if (sdoTwo != null) {
                    oneId = sdoTwo.get(BaseDecConstant.PRIMARY_GROUPING).valuePrim.value.toString();
                    twoId = sdoTwo.get(BaseDecConstant.ID).valuePrim.value.toString();
                    for (DataObject sdoInner : leveOne.set) {
                        String idInner = sdoInner.get(BaseDecConstant.ID).valuePrim.value.toString();
                        if (idInner.equals(oneId)) {
                            sdoOne = sdoInner;
                            break;
                        }
                    }
                }

                String objId = temp.get(BaseDecConstant.OBJ_ID).valuePrim.value.toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(BaseDecConstant.SCENE_ID, objId);

                if (oneId != null && sdoOne != null && twoId != null) {
                    String groupType = (String) sdoTwo.get(BaseDecConstant.GROUPING_TYPE).valuePrim.value;
                    jsonObject.put(BaseDecConstant.GROUPING_TYPE, groupType);
                    jsonObject.put(BaseDecConstant.PRIMARY_GROUPING, oneId);
                    jsonObject.put(BaseDecConstant.PRIMARY_GROUPING_NAME, sdoOne.get(BaseDecConstant.NAME).valuePrim.value);
                    jsonObject.put(BaseDecConstant.TWO_GROUPING, twoId);
                    jsonObject.put(BaseDecConstant.TWO_GROUPING_NAME, sdoTwo.get(BaseDecConstant.NAME).valuePrim.value);
                    if (!repository.id2sdv.containsKey(objId)) {
                        log.warn(dir.getName() + " " + "场景不存在: " + objId);
                        return;
                    }

                    DataObject illuminationSdo = repository.id2sdv.get(objId);
                    if (dir.getName().equals(BaseDecConstant.GGZM)) {
                        DataValue floorArray = illuminationSdo.get(BaseDecConstant.PLACE_FLOOR);
                        if (floorArray != null && floorArray.valueArray != null && floorArray.valueArray.set != null
                                && floorArray.valueArray.set.size() == 1) {
                            DataObject floor = floorArray.valueArray.set.get(0);
                            jsonObject.put(BaseDecConstant.FLOOR_CODE, floor.get(BaseDecConstant.ID).valuePrim.value);
                            jsonObject.put(BaseDecConstant.FLOOR_NAME, getName(floor));
                        }
                    }
                }
                sceneArray.add(jsonObject);
            });
            DataSet scene = new DataSet(false);
            scene.set = BaseApiUtil.arrayToSdoList(sceneArray);
            arrayMap.put(BaseDecConstant.SCENE_NAME, scene);
            FileUtil.save(groupCode + File.separator + BaseDecConstant.CURRENT_PROJECT_ID + File.separator + temp + File.separator + BaseDecConstant.TEMP2 + dir.getName() + "-" + BaseDecConstant.SCENE + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(sceneArray));

            //加入场景信息
            scene.set.forEach(sdo -> {
                String id = (String) sdo.get(BaseDecConstant.SCENE_ID).valuePrim.value;
                if (repository.id2sdv.containsKey(id)) {
                    DataObject eqpSdo = repository.id2sdv.get(id);
                    //防止只有场景编组数据覆盖完整数据
                    if (eqpSdo.get(BaseDecConstant.PRIMARY_GROUPING_NAME) != null) {
                        if (eqpSdo.get(BaseDecConstant.PRIMARY_GROUPING_NAME).valuePrim.value != null || eqpSdo.get(BaseDecConstant.TWO_GROUPING_NAME).valuePrim.value != null) {
                            return;
                        }
                    }
                    DataValue sdv = new DataValue(null, null, null, null);
                    sdv.valuePrim = new DataPrimitive();
                    sdv.valuePrim.change = false;
                    eqpSdo.put(BaseDecConstant.PRIMARY_GROUPING_NAME, sdo.containsKey(BaseDecConstant.PRIMARY_GROUPING_NAME) ? sdo.get(BaseDecConstant.PRIMARY_GROUPING_NAME) : sdv);
                    eqpSdo.put(BaseDecConstant.TWO_GROUPING_NAME, sdo.containsKey(BaseDecConstant.TWO_GROUPING_NAME) ? sdo.get(BaseDecConstant.TWO_GROUPING_NAME) : sdv);
                    eqpSdo.put(BaseDecConstant.GROUPING_TYPE, sdo.containsKey(BaseDecConstant.GROUPING_TYPE) ? sdo.get(BaseDecConstant.GROUPING_TYPE) : sdv);
                    if (dir.getName().equals(BaseDecConstant.GGZM)) {
                        eqpSdo.put(BaseDecConstant.FLOOR_CODE, sdo.containsKey(BaseDecConstant.FLOOR_CODE) ? sdo.get(BaseDecConstant.FLOOR_CODE) : sdv);
                        eqpSdo.put(BaseDecConstant.FLOOR_NAME, sdo.containsKey(BaseDecConstant.FLOOR_NAME) ? sdo.get(BaseDecConstant.FLOOR_NAME) : sdv);
                    }
                }
            });
        } catch (Exception e) {
            log.error("处理照明场景编组数据异常", e);
        }
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
        //requestBody.put(BaseDecConstant.PARENT_ID, "0");
        addProject(requestBody);
        JSONArray groupArray = OkHttpClientUtil.httpPost(requestBody, monitorUrl + UrlConstant.LOGICAL_GROUP_URL).getJSONArray(BaseDecConstant.DATA);
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
                        JSONArray jsonArray = OkHttpClientUtil.httpPost(requestBody, monitorUrl + UrlConstant.LOGICAL_OBJECT_URL).getJSONObject(BaseDecConstant.RESULT).getJSONArray(BaseDecConstant.DATA);
                        //添加逻辑分组id
                        jsonArray.forEach(o -> ((JSONObject) o).put(BaseDecConstant.LOGICAL_GROUPING_ID, logicalGroupingId));
                        dataArray.addAll(jsonArray);
                    } catch (Exception e) {
                        log.error("-----下载：" + sceneCode + "---" + classCode + "，异常" + e);
                    }
                });
                try {
                    if (dataArray.size() > 0) {
                        FileUtil.save(sceneCodeDir + File.separator + classCode + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(dataArray));
                    }
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
        requestBody.put(BaseDecConstant.PROJECT_ID, BaseDecConstant.CURRENT_PROJECT_ID);
    }

    /**
     * 获取根目录路径
     *
     * @return
     */
    private String getPath() {
        return groupCode + File.separator + BaseDecConstant.CURRENT_PROJECT_ID + File.separator + ibmsLogicalGroup;
    }

    /**
     * 获取照明回路名称，取得现实编码名称
     *
     * @param sdo
     * @return
     */
    private String getName(DataObject sdo) {
        String result;
        if (sdo.containsKey(BaseDecConstant.REALITY_CODE_NAME)) {
            DataValue sdv = sdo.get(BaseDecConstant.REALITY_CODE_NAME);
            if (sdv.valuePrim != null) {
                result = (String) sdv.valuePrim.value;
                return result;
            }
        }
        return null;
    }
}
