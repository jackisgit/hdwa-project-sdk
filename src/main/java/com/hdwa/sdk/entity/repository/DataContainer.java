package com.hdwa.sdk.entity.repository;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.SceneDataPrimitive;
import com.hdwa.sdk.entity.scene.SceneDataSet;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import com.hdwa.sdk.utils.PacketBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author abao
 * @since 2023/8/2
 * 路径数据容器
 */
public class DataContainer {
    /**
     * <p>数据容器</p>
     * <p>projectId-----数据</p>
     */
    public static Map<String, RepositoryImpl> projectMap = new HashMap<>(16);


    /**
     * <p>IOT采集数据</p>
     * <p>运行点位值--数据</p>
     * <p>运行点位值 对象数据--->数据</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public static ConcurrentHashMap<String, SceneDataPrimitive> point2sdv = new ConcurrentHashMap<>(16);

    /**
     * <p>IOT采集数据</p>
     * <p>数据--运行点位值</p>
     * <p>数据 --->运行点位值</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public static ConcurrentHashMap<SceneDataPrimitive, String> sdv2point = new ConcurrentHashMap<>(16);


    /**
     * <p>IOT设置数据</p>
     * <p>设定点位值--数据</p>
     * <p>设定点位值 对象数据--->数据</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public static ConcurrentHashMap<String, SceneDataPrimitive> set2sdv = new ConcurrentHashMap<>(16);

    /**
     * <p>IOT设置数据</p>
     * <p>数据--设定点位值</p>
     * <p>数据 --->设定点位值</p>
     * <p>数据来源 physical_world/object/*.json</p>
     */
    public static ConcurrentHashMap<SceneDataPrimitive, String> sdv2set = new ConcurrentHashMap<>(16);


    /**
     * <p>报警数据</p>
     */
    public static SceneDataSet alarmArray = new SceneDataSet(false, true);

    /**
     * <p>报警数据</p>
     * <p>报警列表</p>
     * <p>objId--sdv</p>
     * <p>对象id --->报警列表</p>
     */
    public static Map<String, SceneDataValue> id2alarmList = new HashMap<>(16);


    /**
     * <p>报警数据</p>
     * <p>报警数量</p>
     * <p>objId--sdv</p>
     * <p>对象id --->报警数量</p>
     */
    public static Map<String, SceneDataValue> id2alarmCount = new HashMap<>(16);


    /**
     * <p>报警数据</p>
     * <p>报警缓存数据</p>
     */
    public static PacketBuffer<JSONObject> alarmBuffer = new PacketBuffer<>();

}
