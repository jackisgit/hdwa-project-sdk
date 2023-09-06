package com.hdwa.sdk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.entity.repository.ObjectInfo;
import com.hdwa.sdk.entity.repository.RepositoryContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataPrimitive;
import com.hdwa.sdk.entity.scene.SceneDataSet;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import com.hdwa.sdk.enums.RelationModel;
import com.hdwa.sdk.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author abao
 * @since 2023/8/10
 * 物理世界服务
 */
@Slf4j
@Service
public class PhysicalWorldService {

    @Value("${project.id}")
    private String projectId;

    @Value("${project.groupCode}")
    private String groupCode;

    @Value("${dirName.physicalWorld}")
    private String physicalWorld;

    @Value("${dirName.temp}")
    private String temp;

    @Value("${url.dmp}")
    private String dmpUrl;


    /**
     * 下载物理世界数据
     *
     * @return
     */
    public Object downLoadPhysicalWorldData() {
        log.warn("************开始下载-物理世界数据");
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
            //创建点位文件夹
            File point = new File(tempFile + File.separator + BaseDecConstant.POINT);
            if (!point.exists()) {
                Files.createDirectories(point.toPath());
            }
            //创建对象文件夹
            File object = new File(tempFile + File.separator + BaseDecConstant.OBJECT);
            if (!object.exists()) {
                Files.createDirectories(object.toPath());
            }
            //创建关系文件夹
            File relation = new File(tempFile + File.separator + BaseDecConstant.RELATION);
            if (!relation.exists()) {
                Files.createDirectories(relation.toPath());
            }
            //请求接口下载数据
            JSONArray jsonArray = downClass(tempFile);
            List<String> list = new ArrayList<>();
            downObject(jsonArray, object, list);
            downPoint(jsonArray, point, list);
            downRelation(relation);
            //修改temp目录为当前时间目录
            FileUtil.tempToNowDate(tempFile, new File(getPath()));
            //只保留3个版本数据
            FileUtil.clearHistoryDirectory(new File(getPath()));
            log.warn("************结束下载-物理世界数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");

            return "ok";
        } catch (Exception e) {
            log.error("下载物理世界数据异常", e);
        }
        return null;
    }

    /**
     * 加载物理世界数据
     *
     * @return
     */
    public Object loadPhysicalWorldData(RepositoryImpl repository) {
        log.warn("************开始加载-物理世界数据");
        long startTime = System.currentTimeMillis();
        File maxDir = FileUtil.getMaxDir(new File(getPath()));
        loadClassDefData(repository, maxDir);
        loadPointDefData(repository, maxDir);
        loadObjectData(repository, maxDir);
        loadRelationData(repository, maxDir);
        disposePoint(repository);
        loadRelationRef(repository);
        log.warn("************结束加载-物理世界数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        return "ok";
    }


    /**
     * 加载类型定义数据
     */
    private void loadClassDefData(RepositoryImpl repository, File maxDir) {
        log.warn("*****开始加载-类型定义数据数据");
        long startTime = System.currentTimeMillis();
        try {
            JSONArray classArray = ReadFileUtil.readJsonArray(new File(maxDir + File.separator + UrlConstant.CLASS_ARRAY));
            SceneDataSet sds = new SceneDataSet(false, BaseDecConstant.RWD_CLASS_PATH);
            sds.set = RWDUtil.array2SDOList(classArray);
            repository.classArray = sds;

            for (Object o : classArray) {
                JSONObject classItem = (JSONObject) o;
                String objType = classItem.get(BaseDecConstant.OBJ_TYPE).toString();
                String code = classItem.get(BaseDecConstant.CODE).toString();
                String name = classItem.get(BaseDecConstant.NAME).toString();
                //区分类型定义
                if (code.equals(objType)) {
                    repository.objTypeMap.put(objType, false);
                } else {
                    repository.code2objTypeMap.put(code, objType);
                    repository.objTypeMap.put(objType, true);
                }
                repository.classCode2NameMap.put(code, name);
            }
            log.warn("*****结束加载-类型定义数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("加载类型定义数据异常", e);
        }
    }

    /**
     * 加载点位数据
     *
     * @param repository
     */
    private void loadPointDefData(RepositoryImpl repository, File maxDir) {
        log.warn("*****开始加载-点位定义数据");
        long startTime = System.currentTimeMillis();
        try {
            //所有类型的点位文件
            File[] files = new File(maxDir + File.separator + BaseDecConstant.POINT).listFiles();
            Map<String, SceneDataSet> sdsMap = new HashMap<>(16);
            Map<String, JSONArray> pointArray = new HashMap<>(16);
            //解析存放所有类型点位种dataSource的数据，例：{"classCode":"FFEACU","code":"0","name":"正常","infoCode":"orderFailAlarm"}，{"classCode":"FFEACU","code":"1","name":"报警","infoCode":"orderFailAlarm"}，这两条数据等于"dataSource":[{"code":"0","name":"正常"},{"code":"1","name":"报警"}]
            JSONArray dataSourceAll = new JSONArray();
            Arrays.stream(files).forEach(file -> {
                try {
                    //类型编码
                    String classCode = file.getName().substring(0, file.getName().indexOf('.'));
                    JSONArray array = ReadFileUtil.readJsonArray(file);
                    SceneDataSet sds = new SceneDataSet(false, BaseDecConstant.RWD_INFO_PATH + classCode);
                    sds.set = RWDUtil.array2SDOList(array);
                    sdsMap.put(classCode, sds);
                    pointArray.put(classCode, array);
                    array.stream()
                            .filter(o -> ((JSONObject) o).containsKey(BaseDecConstant.DATA_SOURCE))
                            .forEach(jsonObject -> {
                                Object dataSource = ((JSONObject) jsonObject).get(BaseDecConstant.DATA_SOURCE);
                                String pointCode = ((JSONObject) jsonObject).getString(BaseDecConstant.CODE);
                                //如果是数组就解析
                                if (dataSource instanceof JSONArray) {
                                    ((JSONArray) dataSource).forEach(data -> {
                                        JSONObject itemTmp = new JSONObject();
                                        itemTmp.putAll((JSONObject) data);
                                        itemTmp.put(BaseDecConstant.INFO_CODE, pointCode);
                                        itemTmp.put(BaseDecConstant.CLASS_CODE, classCode);
                                        dataSourceAll.add(itemTmp);
                                    });
                                }

                            });

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            repository.infoArrayDic = sdsMap;
            repository.infoArrayJson = pointArray;
            //保存dataSource数据
            SceneDataSet sds = new SceneDataSet(false);
            sds.set = RWDUtil.array2SDOList(dataSourceAll);
            repository.infoDataSource = sds;
            FileUtil.save(groupCode + File.separator + projectId + File.separator + temp + File.separator + UrlConstant.TMP_DATASOURCE, FastJsonUtil.toFormatString(dataSourceAll));
            log.warn("*****结束加载-点位定义数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("加载点位定义数据异常", e);
        }
    }

    /**
     * 加载对象数据
     *
     * @param repository
     */
    private void loadObjectData(RepositoryImpl repository, File maxDir) {
        log.warn("*****开始加载-对象数据");
        long startTime = System.currentTimeMillis();
        try {
            Map<String, SceneDataValue> objectMap = new HashMap<>(16);
            //对象文件
            File[] files = new File(maxDir + File.separator + BaseDecConstant.OBJECT).listFiles();
            Arrays.stream(files).forEach(file -> {
                try {
                    //类型编码
                    String classCode = file.getName().substring(0, file.getName().indexOf('.'));
                    if (repository.objTypeMap.containsKey(classCode) && repository.objTypeMap.get(classCode)) {
                        return;
                    }
                    //对象数据
                    JSONArray array = ReadFileUtil.readJsonArray(file);
                    //类型名称
                    String className = repository.classCode2NameMap.get(classCode);

                    array.forEach(o -> {
                        JSONObject jsonObject = (JSONObject) o;
                        jsonObject.put(BaseDecConstant.DATA_DICT_TYPE_NAME, className);
                        repository.id2object.put((String) jsonObject.get(BaseDecConstant.ID), jsonObject);
                    });
                    SceneDataSet sds = new SceneDataSet(false, BaseDecConstant.RWD_OBJECT_PATH + classCode);
                    SceneDataSet pointArray = repository.infoArrayDic.get(classCode);
                    //根据点位类型添加属性
                    pointArray.set.stream()
                            .filter(pointItem -> BaseApiUtil.getInfoTypeByTag(pointItem) != 0)
                            .forEach(pointItem -> sds.setColChange((String) pointItem.get(BaseDecConstant.CODE).value_prim.value));

                    sds.set = RWDUtil.array2SDOList(array);
                    SceneDataValue sceneDataValue = new SceneDataValue(null, null, null, null);
                    //添加属性
                    sds.set.forEach(sdsItem -> {
                        repository.id2sdv.put((String) sdsItem.value_object.get(BaseDecConstant.ID).value_prim.value, sdsItem);
                        sdsItem.parentArrayData = sceneDataValue;
                    });

                    sceneDataValue.value_array = sds;
                    sceneDataValue.finish = true;
                    objectMap.put(classCode, sceneDataValue);
                    String objType = repository.code2objTypeMap.get(classCode);
                    if (objType != null) {
                        if (!objectMap.containsKey(objType)) {
                            SceneDataValue objTypeSDV = new SceneDataValue(null, null, null, null);
                            objTypeSDV.value_array = new SceneDataSet(false);
                            objectMap.put(objType, objTypeSDV);
                        }
                        objectMap.get(objType).value_array.set.addAll(sds.set);

                        // TODO: 2023/8/28 可能无用
                        for (String col : sds.getColChange().keySet()) {
                            objectMap.get(objType).value_array.setColChange(col);
                        }
                    }
                    repository.objectArrayAll.set.addAll(sds.set);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            repository.objectArrayDic = objectMap;

            log.warn("*****结束加载-对象数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("加载对象数据异常", e);
        }

    }

    /**
     * 加载关系数据
     *
     * @param repository
     */
    private void loadRelationData(RepositoryImpl repository, File maxDir) {
        log.warn("*****开始加载-关系数据");
        long startTime = System.currentTimeMillis();
        try {
            //根据关系枚举匹配对象类型和对象数据
            Map<String, Map<String, SceneDataObject>> objTypeToMap = new HashMap<>(16);
            RelationModel.listObjType().forEach(objType -> {
                //对象id-->对象值
                Map<String, SceneDataObject> tempMap = new HashMap<>(16);
                //对象类型-->对象列表
                SceneDataSet sdvList = repository.objectArrayDic.get(objType).value_array;
                for (SceneDataObject sdv : sdvList.set) {
                    tempMap.put(sdv.value_object.get(BaseDecConstant.ID).value_prim.value.toString(), sdv);
                }
                objTypeToMap.put(objType, tempMap);
            });
            repository.objType2id2Value = objTypeToMap;

            Map<String, SceneDataSet> graphCodeMap = new HashMap<>(16);
            Map<String, SceneDataSet> relCodeMap = new HashMap<>(16);
            Map<String, Map<String, SceneDataSet>> relationMap = new HashMap<>(16);
            //图例文件夹
            File[] files = new File(maxDir + File.separator + BaseDecConstant.RELATION).listFiles();
            Arrays.stream(files).forEach(graphicDir -> {
                Map<String, SceneDataSet> tempSds = new HashMap<>(16);
                SceneDataSet graphCodeSet = new SceneDataSet(false, BaseDecConstant.RWD_RELATION_PATH + graphicDir.getName());
                //关系文件
                Arrays.stream(Objects.requireNonNull(graphicDir.listFiles())).forEach(file -> {
                    try {
                        //关系编码
                        String relCode = file.getName().substring(0, file.getName().indexOf('.'));
                        //关系数据
                        JSONArray array = ReadFileUtil.readJsonArray(file);
                        SceneDataSet sds = new SceneDataSet(false, BaseDecConstant.RWD_RELATION_PATH + graphicDir.getName() + "/" + relCode);
                        sds.set = RWDUtil.array2SDOList(array);
                        tempSds.put(relCode, sds);
                        graphCodeSet.set.addAll(sds.set);
                        if (!relCodeMap.containsKey(relCode)) {
                            relCodeMap.put(relCode, new SceneDataSet(false, BaseDecConstant.RWD_RELATION_PATH + relCode));
                        }
                        relCodeMap.get(relCode).set.addAll(sds.set);
                        repository.relationAll.set.addAll(sds.set);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                relationMap.put(graphicDir.getName(), tempSds);
                graphCodeMap.put(graphicDir.getName(), graphCodeSet);
            });
            repository.relCodeDic = relCodeMap;
            repository.graphCodeDic = graphCodeMap;
            repository.relationArrayDic = relationMap;
            log.warn("*****结束加载-关系数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("加载关系数据异常", e);
        }
    }

    /**
     * 处理点位
     *
     * @param repository
     */
    private void disposePoint(RepositoryImpl repository) {
        log.warn("*****开始加载-处理点位");
        long startTime = System.currentTimeMillis();
        try {
            repository.objectArrayDic.forEach((key, sdv) -> {
                if (repository.objTypeMap.containsKey(key)) {
                    return;
                }
                //点位清单
                SceneDataSet infoArray = repository.infoArrayDic.get(key);
                //对象清单
                SceneDataSet objectArray = repository.objectArrayDic.get(key).value_array;

                objectArray.set.forEach(sdo -> {
                    //对象id
                    String objId = (String) sdo.get(BaseDecConstant.ID).value_prim.value;
                    sdo.keySet().forEach(s -> {
                        //"seasonChangeSet":"Eq4401830001068b1b5b5951443eb56c20765b756f85-8006"
                        // 点位属性名称seasonChangeSet
                        SceneDataValue infoKey = sdo.get(s);
                        //点位属性值"Eq4401830001068b1b5b5951443eb56c20765b756f85-8006"
                        Object infoValue = sdo.get(s).value_prim.value;

                        //处理运行参数属性
                        if (BaseApiUtil.isRunParam(infoArray.set, s)) {
                            //需要是Sting
                            if (!(infoValue instanceof String)) {
                                return;
                            }
                            //点位值
                            String pointValue = (String) infoValue;
                            //检查有效性
                            if (BaseApiUtil.infoValueIsPoint(pointValue)) {
                                repository.object2info2point.putIfAbsent(objId, new HashMap<>(16));
                                HashMap<String, String> pointMap = repository.object2info2point.get(objId);
                                pointMap.putIfAbsent(s, pointValue);

                                if (!repository.point2ObjectInfoList.containsKey(pointValue)) {
                                    repository.point2ObjectInfoList.put(pointValue, new CopyOnWriteArrayList<>());
                                }
                                List<ObjectInfo> objectInfos = repository.point2ObjectInfoList.get(pointValue);
                                objectInfos.add(new ObjectInfo(sdo, objId, s));

                                // TODO: 2023/8/28  RepositoryContainer后续去掉
                                SceneDataPrimitive sdp = new SceneDataPrimitive();
                                sdp.change = true;
                                SceneDataPrimitive exist = repository.point2sdv.putIfAbsent(pointValue, sdp);
                                if (exist == null) {
                                   repository.sdv2point.putIfAbsent(sdp, pointValue);
                                }

                                infoKey.value_prim = repository.point2sdv.get(pointValue);
                                initSdv(sdo, s, pointValue);
                            } else {
                                sdo.remove(s);
                            }
                        }//处理设定点位属性
                        else if (BaseApiUtil.isSetParam(infoArray.set, s)) {
                            //需要是Sting
                            if (!(infoValue instanceof String)) {
                                return;
                            }
                            //点位值
                            String pointValue = (String) infoValue;
                            //检查有效性
                            if (BaseApiUtil.infoValueIsPoint(pointValue)) {
                                if (!repository.set2ObjectInfoList.containsKey(pointValue)) {
                                    repository.set2ObjectInfoList.put(pointValue, new CopyOnWriteArrayList<>());
                                }
                                List<ObjectInfo> objectInfos = repository.set2ObjectInfoList.get(pointValue);
                                objectInfos.add(new ObjectInfo(sdo, objId, s));

                                // TODO: 2023/8/28  RepositoryContainer后续去掉
                                SceneDataPrimitive sdp = new SceneDataPrimitive();
                                sdp.change = true;
                                SceneDataPrimitive exist = repository.set2sdv.putIfAbsent(pointValue, sdp);
                                if (exist == null) {
                                    repository.sdv2set.putIfAbsent(sdp, pointValue);
                                }

                                infoKey.value_prim = repository.set2sdv.get(pointValue);
                                initSdv(sdo, s, pointValue);
                            } else {
                                sdo.remove(s);
                            }
                        }
                    });
                });
            });
            log.warn("*****结束加载-处理点位-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("加载处理点位数据异常", e);
        }
    }

    /**
     * 初始对象
     * @param sdo
     * @param s
     * @param pointValue
     */
    private void initSdv(SceneDataObject sdo, String s, String pointValue) {
        SceneDataValue tempSdv = new SceneDataValue(null, null, null, null);
        tempSdv.finish = true;
        tempSdv.value_prim = new SceneDataPrimitive();
        tempSdv.value_prim.change = false;
        tempSdv.value_prim.value = pointValue;
        sdo.put(s + "-" + BaseDecConstant.METER_FUNGICIDE, tempSdv);
    }

    /**
     * 解析关系模版到对象数据
     *
     * @param repository
     */
    private void loadRelationRef(RepositoryImpl repository) {
        log.warn("*****开始加载-解析关系模版数据");
        long startTime = System.currentTimeMillis();
        try {
            addProperName(repository);
            addProperValue(repository);
            RelationModel.listRelation().forEach(rel -> {
                relationFormObject(rel, repository);
                relationToObject(rel, repository);
            });

            log.warn("*****结束加载-解析关系模版数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("加载解析关系模版数据异常", e);
        }
    }

    /**
     * 添加关系属性名称
     *
     * @param repository
     */
    private void addProperName(RepositoryImpl repository) {
        //添加关系属性名称
        RelationModel.listRelation().forEach(rel -> {
            //主对象数据
            Map<String, SceneDataObject> fromObjectMap = repository.objType2id2Value.get(rel.getObjFrom());
            //从对象数据
            Map<String, SceneDataObject> toObjectMap = repository.objType2id2Value.get(rel.getObjTo());
            //主对象角度有数据，添加属性名称
            if (StringUtils.isNotEmpty(rel.getFromName())) {
                fromObjectMap.forEach((s, sdo) -> {
                    //加入属性
                    if (!sdo.value_object.containsKey(rel.getFromName())) {
                        SceneDataValue tempSdv = new SceneDataValue(repository, sdo, rel.getFromName(), null);
                        tempSdv.finish = true;
                        tempSdv.value_array = new SceneDataSet(false);
                        tempSdv.value_array.set = new CopyOnWriteArrayList<>();
                        sdo.value_object.put(rel.getFromName(), tempSdv);
                    }
                });
            }
            //从对象角度有数据，添加属性名称
            if (StringUtils.isNotEmpty(rel.getToName())) {
                toObjectMap.forEach((s, sdo) -> {
                    //加入属性
                    if (!sdo.value_object.containsKey(rel.getToName())) {
                        SceneDataValue tempSdv = new SceneDataValue(repository, sdo, rel.getToName(), null);
                        tempSdv.finish = true;
                        tempSdv.value_array = new SceneDataSet(false);
                        tempSdv.value_array.set = new CopyOnWriteArrayList<>();

                        sdo.value_object.put(rel.getToName(), tempSdv);
                    }
                });
            }
        });
    }

    /**
     * 添加关系属性值
     *
     * @param repository
     */
    private void addProperValue(RepositoryImpl repository) {
        RelationModel.listRelation().stream()
                //筛选有数据的
                .filter(rel -> repository.relationArrayDic.containsKey(rel.getGraphCode()))
                .filter(rel -> repository.relationArrayDic.get(rel.getGraphCode()).containsKey(rel.getRelCode()))
                .forEach(rel -> {
                    //图例--relCode--object
                    List<SceneDataObject> sdoList = repository.relationArrayDic.get(rel.getGraphCode()).get(rel.getRelCode()).set;
                    //主对象数据集合
                    Map<String, SceneDataObject> fromObjectMap = repository.objType2id2Value.get(rel.getObjFrom());
                    //从对象数据集合
                    Map<String, SceneDataObject> toObjectMap = repository.objType2id2Value.get(rel.getObjTo());

                    sdoList.forEach(tempSdo -> {
                        //根据主对象id查找主对象数据
                        SceneDataObject objFrom = fromObjectMap.get(tempSdo.value_object.get(BaseDecConstant.OBJ_FROM).value_prim.value.toString());
                        //根据从对象id查找从对象数据
                        SceneDataObject objTo = toObjectMap.get(tempSdo.value_object.get(BaseDecConstant.OBJ_TO).value_prim.value.toString());

                        if (objFrom == null || objTo == null) {
                            return;
                        }
                        //主对象属性添加从对象id
                        if (StringUtils.isNotEmpty(rel.getFromName())) {
                            SceneDataValue tempSdv = objFrom.value_object.get(rel.getFromName());
                            if (tempSdv.value_array == null) {
                                tempSdv.value_array = new SceneDataSet(false);
                            }
                            tempSdv.value_array.set.add(objTo);
                        }
                        //从对象属性添加主对象id
                        if (StringUtils.isNotEmpty(rel.getToName())) {
                            SceneDataValue tempSdv = objTo.value_object.get(rel.getToName());
                            if (tempSdv.value_array == null) {
                                tempSdv.value_array = new SceneDataSet(false);
                            }
                            tempSdv.value_array.set.add(objFrom);
                        }
                    });
                });
    }


    /**
     * 主对象属性
     * @param rel
     * @param repository
     */
    private void relationFormObject(RelationModel.Rel rel, RepositoryImpl repository){
        //主对象
        if (StringUtils.isNotBlank(rel.getFromName()) && StringUtils.isNotBlank(rel.getFromMultiple())) {
            //主对象数据
            Map<String, SceneDataObject> fromObjectMap = repository.objType2id2Value.get(rel.getObjFrom());

            fromObjectMap.forEach((s, sdo) -> {
                //前面添加的属性名称
                SceneDataObject sdoFrom = fromObjectMap.get(s);
                SceneDataValue sdvFromItem = sdoFrom.value_object.get(rel.getFromName());
                if (sdvFromItem.value_array != null) {
                    //主对象为1
                    if (rel.getFromMultiple().equals(BaseDecConstant.ONE)) {
                        SceneDataValue tempSdvId = new SceneDataValue(repository, sdo, rel.getFromName() + BaseDecConstant.ID2, null);
                        tempSdvId.value_prim = new SceneDataPrimitive();
                        tempSdvId.finish = true;

                        SceneDataValue tempSdvName = new SceneDataValue(repository, sdo, rel.getFromName() + BaseDecConstant.NAME2, null);
                        tempSdvName.finish = true;
                        tempSdvName.value_prim = new SceneDataPrimitive();
                        //只有一条数据
                        if (sdvFromItem.value_array.set.size() == 1) {
                            tempSdvId.value_prim.value = sdvFromItem.value_array.set.get(BigInteger.ZERO.intValue()).get(BaseDecConstant.ID).value_prim.value;
                            // TODO: 2023/8/28 现实编码名称是否还有用？
                            if (sdvFromItem.value_array.set.get(BigInteger.ZERO.intValue()).get(BaseDecConstant.REALITY_CODE_NAME) == null) {
                                tempSdvName.value_prim.value = sdvFromItem.value_array.set.get(BigInteger.ZERO.intValue()).get(BaseDecConstant.LOCAL_NAME).value_prim.value;
                            } else {
                                tempSdvName.value_prim.value = sdvFromItem.value_array.set.get(BigInteger.ZERO.intValue()).get(BaseDecConstant.REALITY_CODE_NAME).value_prim.value;
                            }
                        }
                        //多条数据
                        else if (sdvFromItem.value_array.set.size() > 1) {
                            //从对象数据列表
                            sdvFromItem.value_array.set.forEach(tempSdo -> {
                                //赋值
                                if (tempSdvId.value_prim.value == null) {
                                    tempSdvId.value_prim.value = tempSdo.get(BaseDecConstant.ID).value_prim.value;
                                    // TODO: 2023/8/28 现实编码名称是否还有用？
                                    if (tempSdo.get(BaseDecConstant.REALITY_CODE_NAME) == null) {
                                        tempSdvName.value_prim.value = tempSdo.get(BaseDecConstant.LOCAL_NAME).value_prim.value;
                                    } else {
                                        tempSdvName.value_prim.value = tempSdo.get(BaseDecConstant.REALITY_CODE_NAME).value_prim.value;
                                    }
                                } else {
                                    //说明有多个关系
                                    if (!tempSdvId.value_prim.value.equals(tempSdo.get(BaseDecConstant.ID).value_prim.value)) {
                                        log.error(rel.getGraphCode() + "-----" + rel.getRelCode() + "----null：绑定了多个---" + rel.getToName() + "---" + tempSdvId.value_prim.value + "---" + tempSdo.get(BaseDecConstant.ID).value_prim.value);
                                    }
                                }
                            });
                        }

                        sdoFrom.value_object.put(rel.getFromName() + BaseDecConstant.ID2, tempSdvId);
                        sdoFrom.value_object.put(rel.getFromName() + BaseDecConstant.NAME2, tempSdvName);
                    }//主对象为n
                    else {
                        SceneDataValue tempSdvId = new SceneDataValue(repository, sdo, rel.getFromName() + BaseDecConstant.ID_DETAILED_LIST, null);
                        tempSdvId.finish = true;
                        tempSdvId.value_array = new SceneDataSet(true);
                        tempSdvId.value_array.singleValueSet = new CopyOnWriteArrayList<>();

                        sdvFromItem.value_array.set.forEach(tempSdo -> {
                            SceneDataValue tempSdv = new SceneDataValue(repository, null, null, null);
                            tempSdv.finish = true;
                            tempSdv.value_prim = new SceneDataPrimitive();
                            tempSdv.value_prim.value = tempSdo.get(BaseDecConstant.ID).value_prim.value;

                            tempSdvId.value_array.singleValueSet.add(tempSdv);
                        });
                        sdo.value_object.put(rel.getFromName() + BaseDecConstant.ID_DETAILED_LIST, tempSdvId);
                    }
                }
            });
        }
    }

    /**
     * 从对象属性
     * @param rel
     * @param repository
     */
    private void relationToObject(RelationModel.Rel rel,RepositoryImpl repository){
        //主对象
        if (StringUtils.isNotBlank(rel.getToName()) && StringUtils.isNotBlank(rel.getToMultiple())) {
            //主对象数据
            Map<String, SceneDataObject> toObjectMap = repository.objType2id2Value.get(rel.getObjTo());
            toObjectMap.forEach((s, sdo) -> {
                //前面添加的属性名称
                SceneDataObject sdoTo = toObjectMap.get(s);
                SceneDataValue sdvToItem = sdoTo.value_object.get(rel.getToName());
                if (sdvToItem.value_array != null) {
                    //主对象为1
                    if (rel.getToMultiple().equals(BaseDecConstant.ONE)) {
                        SceneDataValue tempSdvId = new SceneDataValue(repository, sdo, rel.getToName() + BaseDecConstant.ID2, null);
                        tempSdvId.value_prim = new SceneDataPrimitive();
                        tempSdvId.finish = true;

                        SceneDataValue tempSdvName = new SceneDataValue(repository, sdo, rel.getToName() + BaseDecConstant.NAME2, null);
                        tempSdvName.finish = true;
                        tempSdvName.value_prim = new SceneDataPrimitive();
                        //只有一条数据
                        if (sdvToItem.value_array.set.size() == 1) {
                            tempSdvId.value_prim.value = sdvToItem.value_array.set.get(BigInteger.ZERO.intValue()).get(BaseDecConstant.ID).value_prim.value;
                            // TODO: 2023/8/28 现实编码名称是否还有用？
                            if (sdvToItem.value_array.set.get(BigInteger.ZERO.intValue()).get(BaseDecConstant.REALITY_CODE_NAME) == null) {
                                tempSdvName.value_prim.value = sdvToItem.value_array.set.get(BigInteger.ZERO.intValue()).get(BaseDecConstant.LOCAL_NAME).value_prim.value;
                            } else {
                                tempSdvName.value_prim.value = sdvToItem.value_array.set.get(BigInteger.ZERO.intValue()).get(BaseDecConstant.REALITY_CODE_NAME).value_prim.value;
                            }
                        }
                        //多条数据
                        else if (sdvToItem.value_array.set.size() > 1) {
                            //从对象数据列表
                            sdvToItem.value_array.set.forEach(tempSdo -> {
                                //赋值
                                if (tempSdvId.value_prim.value == null) {
                                    tempSdvId.value_prim.value = tempSdo.get(BaseDecConstant.ID).value_prim.value;
                                    // TODO: 2023/8/28 现实编码名称是否还有用？
                                    if (tempSdo.get(BaseDecConstant.REALITY_CODE_NAME) == null) {
                                        tempSdvName.value_prim.value = tempSdo.get(BaseDecConstant.LOCAL_NAME).value_prim.value;
                                    } else {
                                        tempSdvName.value_prim.value = tempSdo.get(BaseDecConstant.REALITY_CODE_NAME).value_prim.value;
                                    }
                                } else {
                                    //说明有多个关系
                                    if (!tempSdvId.value_prim.value.equals(tempSdo.get(BaseDecConstant.ID).value_prim.value)) {
                                        log.error(rel.getGraphCode() + "-----" + rel.getRelCode() + "----null：绑定了多个---" + rel.getToName() + "---" + tempSdvId.value_prim.value + "---" + tempSdo.get(BaseDecConstant.ID).value_prim.value);
                                    }
                                }
                            });
                        }

                        sdoTo.value_object.put(rel.getToName() + BaseDecConstant.ID2, tempSdvId);
                        sdoTo.value_object.put(rel.getToName() + BaseDecConstant.NAME2, tempSdvName);
                    }//主对象为n
                    else {
                        SceneDataValue tempSdvId = new SceneDataValue(repository, sdo, rel.getToName() + BaseDecConstant.ID_DETAILED_LIST, null);
                        tempSdvId.finish = true;
                        tempSdvId.value_array = new SceneDataSet(true);
                        tempSdvId.value_array.singleValueSet = new CopyOnWriteArrayList<>();

                        sdvToItem.value_array.set.forEach(tempSdo -> {
                            SceneDataValue tempSdv = new SceneDataValue(repository, null, null, null);
                            tempSdv.finish = true;
                            tempSdv.value_prim = new SceneDataPrimitive();
                            tempSdv.value_prim.value = tempSdo.get(BaseDecConstant.ID).value_prim.value;

                            tempSdvId.value_array.singleValueSet.add(tempSdv);
                        });
                        sdo.value_object.put(rel.getToName() + BaseDecConstant.ID_DETAILED_LIST, tempSdvId);
                    }
                }
            });
        }
    }

    /**
     * 获取根目录路径
     *
     * @return
     */
    private String getPath() {
        return groupCode + File.separator + projectId + File.separator + physicalWorld;
    }


    /**
     * 下载类型定义数据
     *
     * @param file
     * @return
     * @throws Exception
     */
    public JSONArray downClass(File file) throws Exception {
        long startTime = System.currentTimeMillis();
        log.warn("*****开始下载-类型定义数据");
        JSONObject requestBody = new JSONObject();
        addProject(requestBody);
        JSONArray classArray = OkHttpClientUtil.httpPost(requestBody, dmpUrl + UrlConstant.LIST_CLASS_DEFINER_URL).getJSONArray(BaseDecConstant.DATA);
        FileUtil.save(file + File.separator + UrlConstant.CLASS_ARRAY, FastJsonUtil.toFormatString(classArray));
        log.warn("*****结束下载-类型定义数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        return classArray;
    }


    /**
     * 下载对象数据
     *
     * @param classArray
     * @param file
     * @param nullClassList
     * @throws Exception
     */
    private void downObject(JSONArray classArray, File file, List<String> nullClassList) throws Exception {
        long startTime = System.currentTimeMillis();
        log.warn("*****开始下载-对象数据");
        //类型定义数据--类型标记
        Map<String, Boolean> objTypeMap = new HashMap<>(16);
        for (Object objectTemp : classArray) {
            JSONObject temp = (JSONObject) objectTemp;
            String code = temp.getString(BaseDecConstant.CODE);
            String objType = temp.getString(BaseDecConstant.OBJ_TYPE);
            if (code.equals(objType)) {
                objTypeMap.put(objType, false);
            } else {
                objTypeMap.put(objType, true);
            }
        }
        for (Object objectTemp : classArray) {
            JSONObject temp = (JSONObject) objectTemp;
            String code = temp.getString(BaseDecConstant.CODE);
            String objType = temp.getString(BaseDecConstant.OBJ_TYPE);
            //筛选类型
            if (objTypeMap.containsKey(code) && objTypeMap.get(code)) {
                continue;
            }
            JSONObject paramTemp = new JSONObject();
            paramTemp.put(BaseDecConstant.CLASS_CODE, code);
            paramTemp.put(BaseDecConstant.OBJ_TYPE, objType);
            paramTemp.put(BaseDecConstant.VALID, BigInteger.ONE);
            JSONObject requestBody = new JSONObject();
            JSONObject criteriaJson = new JSONObject();
            criteriaJson.put(BaseDecConstant.CRITERIA_2, paramTemp);
            requestBody.put(BaseDecConstant.CONDITION, criteriaJson);
            addProject(requestBody);
            JSONArray objectArray = OkHttpClientUtil.httpPost(requestBody, dmpUrl + UrlConstant.LIST_OBJECT_DATA_URL).getJSONArray(BaseDecConstant.DATA);
            //没有数据的类型就不下载文件
            if (objectArray.size() == 0) {
                nullClassList.add(code);
                continue;
            }
            FileUtil.save(file + File.separator + code + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(objectArray));
        }
        log.warn("*****结束下载-对象数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
    }

    /**
     * 下载点位定义数据
     *
     * @param classArray
     * @param file
     * @param nullClassList
     * @throws Exception
     */
    private void downPoint(JSONArray classArray, File file, List<String> nullClassList) throws Exception {
        //下载点位定义数据
        long startTime = System.currentTimeMillis();
        log.warn("*****开始下载-点位定义数据");
        for (Object o : classArray) {
            String code = ((JSONObject) o).getString(BaseDecConstant.CODE);
            //没有的类型数据不下载点位数据
            if (nullClassList.contains(code)) {
                continue;
            }
            JSONObject requestBody = new JSONObject();
            requestBody.put(BaseDecConstant.GROUP_CODE, groupCode);
            requestBody.put(BaseDecConstant.PROJECT_ID, projectId);
            JSONObject condition = new JSONObject();
            condition.put(BaseDecConstant.CLASS_CODE, code);
            requestBody.put(BaseDecConstant.CONDITION, condition);
            addProject(requestBody);
            JSONArray classPointArray = OkHttpClientUtil.httpPost(requestBody, dmpUrl + UrlConstant.LIST_POINT_DEFINER_URL).getJSONArray(BaseDecConstant.DATA);
            FileUtil.save(file + File.separator + code + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(classPointArray));
        }
        log.warn("*****结束下载-点位定义数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
    }

    /**
     * 下载关系数据
     *
     * @param file
     * @throws Exception
     */
    private void downRelation(File file) throws Exception {
        long startTime = System.currentTimeMillis();
        log.warn("*****开始下载-关系数据");
        //关系模版
        List<RelationModel.Rel> relList = RelationModel.listRelation();
        for (RelationModel.Rel rel : relList) {
            //图类型
            String type = rel.getGraphCode();
            File typePath = new File(file + File.separator + type);
            if (!typePath.exists()) {
                Files.createDirectories(typePath.toPath());
            }
            //关系编码
            String relCode = rel.getRelCode();
            //查询数据
            JSONObject paramTemp = new JSONObject();
            paramTemp.put(BaseDecConstant.GRAPH_CODE, type);
            paramTemp.put(BaseDecConstant.REL_CODE, relCode);
            JSONObject requestBody = new JSONObject();
            JSONObject criteriaJson = new JSONObject();
            criteriaJson.put(BaseDecConstant.CRITERIA_2, paramTemp);
            requestBody.put(BaseDecConstant.CONDITION, criteriaJson);
            requestBody.put(BaseDecConstant.GROUP_CODE, groupCode);
            requestBody.put(BaseDecConstant.PROJECT_ID, projectId);
            addProject(requestBody);
            JSONArray relationArray = OkHttpClientUtil.httpPost(requestBody, dmpUrl + UrlConstant.LIST_RELATION_DATA_URL).getJSONArray(BaseDecConstant.DATA);
            FileUtil.save(typePath + File.separator + relCode + UrlConstant.JSON_FILE, FastJsonUtil.toFormatString(relationArray));
        }
        log.warn("*****结束下载-关系数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
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
