package com.hdwa.sdk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataSet;
import com.hdwa.sdk.entity.scene.SceneDataValue;
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
    public Object loadLogicalGroupData(RepositoryImpl repository) {
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
    private void loadGroupData(RepositoryImpl repository, File maxDir) {
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

            SceneDataSet sds = new SceneDataSet(false, BaseDecConstant.IBMS_GROUP);
            sds.set = RWDUtil.array2SDOList(groupArray);
            repository.IBMSGroupArray = sds;
            File[] dirs = maxDir.listFiles();
            Arrays.stream(dirs)
                    .filter(File::isDirectory)
                    .forEach(dir -> {

                        Map<String, SceneDataSet> ibmsClassMap = new HashMap<>(16);
                        Arrays.stream(Objects.requireNonNull(dir.listFiles())).forEach(file -> {
                            String classCode = file.getName().substring(0, file.getName().indexOf('.'));
                            //数据
                            JSONArray array;
                            try {
                                array = ReadFileUtil.readJsonArray(file);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            SceneDataSet tempSds = new SceneDataSet(false, BaseDecConstant.IBMS_GROUP_OBJECT + "/" + dir.getName() + "/" + classCode);
                            tempSds.set = RWDUtil.array2SDOList(array);
                            ibmsClassMap.put(classCode, tempSds);
                        });
                        repository.IBMSArrayDic.put(dir.getName(), ibmsClassMap);
                        //照明分组处理
                        if (dir.getName().equals(BaseDecConstant.GGZM) || dir.getName().equals(BaseDecConstant.YJZM)) {
                            Map<String, SceneDataSet> arrayMap = repository.IBMSArrayDic.get(dir.getName());
                            levelGroupOneData(repository, dir, arrayMap);
                            levelGroupTowData(repository, dir, arrayMap);
                            lightingCircuit(repository, dir, arrayMap);
                        }
                    });

            log.warn("*****结束加载-逻辑编组数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("加载逻辑编组数据异常", e);
        }
    }


    /**
     * 照明一级逻辑编组数据
     *
     * @param repository
     * @param dir
     */
    private void levelGroupOneData(RepositoryImpl repository, File dir, Map<String, SceneDataSet> arrayMap) {
        JSONArray levelGroupOne = new JSONArray();
        repository.IBMSGroupArray.set.forEach(itemSdo -> {
            String parentId = itemSdo.get(BaseDecConstant.PARENT_ID).value_prim.value.toString();
            String ibmsClassCode = itemSdo.get(BaseDecConstant.IBMS_CLASS_CODE).value_prim.value.toString();
            String ibmsSceneCode = itemSdo.get(BaseDecConstant.IBMS_SCENE_CODE).value_prim.value.toString();
            if (!ibmsSceneCode.equals(dir.getName()) || !ibmsClassCode.equals(BaseDecConstant.LIGHTING_CIRCUIT) || !parentId.equals("0")) {
                return;
            }
            String logicalGroupingName = itemSdo.get(BaseDecConstant.LOGICAL_GROUPING_NAME).value_prim.value.toString();
            String logicalGroupingId = itemSdo.get(BaseDecConstant.LOGICAL_GROUPING_ID).value_prim.value.toString();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(BaseDecConstant.ID, logicalGroupingId);
            jsonObject.put(BaseDecConstant.NAME, logicalGroupingName);
            levelGroupOne.add(jsonObject);
        });
        SceneDataSet levelGroupSdsOne = new SceneDataSet(false);
        levelGroupSdsOne.set = RWDUtil.array2SDOList(levelGroupOne);
        arrayMap.put(BaseDecConstant.PRIMARY_GROUPING, levelGroupSdsOne);
        try {
            FileUtil.save(groupCode + File.separator + projectId + File.separator + temp + File.separator + BaseDecConstant.TEMP2 + dir.getName() + "-" + BaseDecConstant.GROUP_ONE + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(levelGroupOne));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 照明二级逻辑编组数据
     *
     * @param repository
     * @param dir
     */
    private void levelGroupTowData(RepositoryImpl repository, File dir, Map<String, SceneDataSet> arrayMap) {
        JSONArray levelGroupTow = new JSONArray();
        repository.IBMSGroupArray.set.forEach(itemSdo -> {
            String parentId = itemSdo.get(BaseDecConstant.PARENT_ID).value_prim.value.toString();
            String ibmsClassCode = itemSdo.get(BaseDecConstant.IBMS_CLASS_CODE).value_prim.value.toString();
            String ibmsSceneCode = itemSdo.get(BaseDecConstant.IBMS_SCENE_CODE).value_prim.value.toString();
            if (!ibmsSceneCode.equals(dir.getName()) || !ibmsClassCode.equals(BaseDecConstant.LIGHTING_CIRCUIT) || parentId.equals("0")) {
                return;
            }
            String logicalGroupingName = itemSdo.get(BaseDecConstant.LOGICAL_GROUPING_NAME).value_prim.value.toString();
            String logicalGroupingId = itemSdo.get(BaseDecConstant.LOGICAL_GROUPING_ID).value_prim.value.toString();
            String firstCode = itemSdo.get(BaseDecConstant.FIRST_CODE).value_prim.value.toString();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(BaseDecConstant.ID, logicalGroupingId);
            jsonObject.put(BaseDecConstant.NAME, logicalGroupingName);
            jsonObject.put(BaseDecConstant.PRIMARY_GROUPING, parentId);
            jsonObject.put(BaseDecConstant.GROUPING_TYPE, firstCode);
            //楼层编码
            if (dir.getName().equals(BaseDecConstant.GGZM)) {
                String floorId = (String) itemSdo.get(BaseDecConstant.FLOOR_ID).value_prim.value;
                jsonObject.put(BaseDecConstant.FLOOR_CODE, floorId);
            }
            levelGroupTow.add(jsonObject);
        });
        SceneDataSet levelGroupSdsOne = new SceneDataSet(false);
        levelGroupSdsOne.set = RWDUtil.array2SDOList(levelGroupTow);
        arrayMap.put(BaseDecConstant.TWO_GROUPING, levelGroupSdsOne);
        try {
            FileUtil.save(groupCode + File.separator + projectId + File.separator + temp + File.separator + BaseDecConstant.TEMP2 + dir.getName() + "-" + BaseDecConstant.GROUP_TWO + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(levelGroupTow));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 照明回路数据
     *
     * @param repository
     * @param dir
     */
    private void lightingCircuit(RepositoryImpl repository, File dir, Map<String, SceneDataSet> arrayMap) {
        SceneDataSet circuitSds = arrayMap.get(BaseDecConstant.LIGHTING_CIRCUIT) == null ? new SceneDataSet(false) : arrayMap.get(BaseDecConstant.LIGHTING_CIRCUIT);
        JSONArray circuitArray = new JSONArray();
        //一级编组数据
        SceneDataSet leveOne = arrayMap.get(BaseDecConstant.PRIMARY_GROUPING);
        //二级编组数据
        SceneDataSet leveTwo = arrayMap.get(BaseDecConstant.TWO_GROUPING);
        circuitSds.set.forEach(temp -> {
            String logicalGroupingId = temp.get(BaseDecConstant.LOGICAL_GROUPING_ID).value_prim.value.toString();

            SceneDataObject sdoOne = null;
            SceneDataObject sdoTwo = null;

            for (SceneDataObject sdoInner : leveTwo.set) {
                String id = sdoInner.get(BaseDecConstant.ID).value_prim.value.toString();
                if (id.equals(logicalGroupingId)) {
                    sdoTwo = sdoInner;
                    break;
                }
            }

            String oneId = null;
            String twoId = null;
            if (sdoTwo != null) {
                oneId = sdoTwo.get(BaseDecConstant.PRIMARY_GROUPING).value_prim.value.toString();
                twoId = sdoTwo.get(BaseDecConstant.ID).value_prim.value.toString();
                for (SceneDataObject sdoInner : leveOne.set) {
                    String idInner = sdoInner.get(BaseDecConstant.ID).value_prim.value.toString();
                    if (idInner.equals(oneId)) {
                        sdoOne = sdoInner;
                        break;
                    }
                }
            }

            String objId = temp.get(BaseDecConstant.OBJ_ID).value_prim.value.toString();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(BaseDecConstant.CIRCUIT_ID, objId);

            if (oneId != null && sdoOne != null && twoId != null) {
                String groupType = (String) sdoTwo.get(BaseDecConstant.GROUPING_TYPE).value_prim.value;
                jsonObject.put(BaseDecConstant.GROUPING_TYPE, groupType);
                jsonObject.put(BaseDecConstant.PRIMARY_GROUPING, oneId);
                jsonObject.put(BaseDecConstant.PRIMARY_GROUPING_NAME, sdoOne.get(BaseDecConstant.NAME).value_prim.value);
                jsonObject.put(BaseDecConstant.TWO_GROUPING, twoId);
                jsonObject.put(BaseDecConstant.TWO_GROUPING_NAME, sdoTwo.get(BaseDecConstant.NAME).value_prim.value);
                if (!repository.id2sdv.containsKey(objId)) {
                    log.warn(dir.getName() + " " + "回路不存在: " + objId);
                    return;
                }

                SceneDataObject illuminationSdo = repository.id2sdv.get(objId);
                SceneDataValue illuminationArray = illuminationSdo.get(BaseDecConstant.DEVICE_CONTROLLED);
                if (illuminationArray != null
                        && illuminationArray.value_array != null
                        && illuminationArray.value_array.set != null
                        && illuminationArray.value_array.set.size() > 0) {

                    SceneDataObject tempSdo = illuminationArray.value_array.set.get(0);
                    jsonObject.put(BaseDecConstant.MODEL_CODE, tempSdo.get(BaseDecConstant.ID).value_prim.value);
                    jsonObject.put(BaseDecConstant.MODEL_NAME, getName(tempSdo));

                    SceneDataValue tempSdv = tempSdo.get(BaseDecConstant.POWERED_BY_EQUIPMENT);
                    if (tempSdv.value_array.set.size() > 0) {
                        SceneDataObject tempSdo2 = tempSdv.value_array.set.get(0);
                        jsonObject.put(BaseDecConstant.DISTRIBUTION_BOX_CODE, tempSdo2.get(BaseDecConstant.ID).value_prim.value);
                        jsonObject.put(BaseDecConstant.DISTRIBUTION_BOX_NAME, getName(tempSdo2));

                        SceneDataValue GeneralZoneArray = tempSdo2.get(BaseDecConstant.PROPERTY_SPACE);
                        if (GeneralZoneArray.value_array.set.size() > 0) {
                            SceneDataObject GeneralZone = GeneralZoneArray.value_array.set.get(0);
                            jsonObject.put(BaseDecConstant.ELECTRIC_WELL_CODE, GeneralZone.get(BaseDecConstant.ID).value_prim.value);
                            jsonObject.put(BaseDecConstant.ELECTRIC_WELL_NAME, getName(GeneralZone));
                        }
                    }
                }

                if (dir.getName().equals(BaseDecConstant.GGZM)) {
                    SceneDataValue floorArray = illuminationSdo.get(BaseDecConstant.PLACE_FLOOR);
                    if (floorArray != null && floorArray.value_array != null && floorArray.value_array.set != null
                            && floorArray.value_array.set.size() == 1) {
                        SceneDataObject floor = floorArray.value_array.set.get(0);
                        jsonObject.put(BaseDecConstant.FLOOR_CODE, floor.get(BaseDecConstant.FLOOR_ID).value_prim.value);
                        jsonObject.put(BaseDecConstant.FLOOR_NAME, getName(floor));
                    }
                }
            }
        });
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

    /**
     * 获取根目录路径
     *
     * @return
     */
    private String getPath() {
        return groupCode + File.separator + projectId + File.separator + ibmsLogicalGroup;
    }

    /**
     * 获取照明回路名称，取得现实编码名称
     *
     * @param sdo
     * @return
     */
    private String getName(SceneDataObject sdo) {
        String result;
        if (sdo.containsKey(BaseDecConstant.REALITY_CODE_NAME)) {
            SceneDataValue sdv = sdo.get(BaseDecConstant.REALITY_CODE_NAME);
            if (sdv.value_prim != null) {
                result = (String) sdv.value_prim.value;
                return result;
            }
        }
        return null;
    }
}
