package com.hdwa.alarm.cache;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.googlecode.aviator.AviatorEvaluator;
import com.hdwa.alarm.util.JsonUtil;
import com.hdwa.alarm.vo.AlarmDefineVO;
import com.hdwa.alarm.vo.AlarmStateVO;
import com.hdwa.alarm.vo.Condition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 报警存放报警定义缓存的的数据
 **/
@Slf4j
public class AlarmInfoCache {
    public static List<String> isolationSystemList = new ArrayList<>();
    /**
     * 报警定义  <报警定义标识，报警时间状态>
     * 报警定义标识默认为：报警编码-报警对象id
     */
    public static ConcurrentHashMap<String, AlarmStateVO> alarmStateMap = new ConcurrentHashMap<>(1024);
    /**
     * 报警定义  <报警定义标识，报警定义详情>
     * 报警定义标识默认为：报警编码-报警对象id
     */
    public static ConcurrentHashMap<String, AlarmDefineVO> alarmDefineMap = new ConcurrentHashMap<>(1024);
    /**
     * 信息点-报警定义 <表号-功能号，[报警定义1,报警定义2]>
     */
    public static ConcurrentHashMap<String, List<AlarmDefineVO>> infoAlarmMap = new ConcurrentHashMap<>(1024);

    /**
     * 获取报警定义id, 中台报警定义ID没有实际意义，根据objId-itemId拼接作为唯一标识
     */
    public static String getAlarmDefineId(AlarmDefineVO alarmDefine) {
        return alarmDefine.getObjId() + "-" + alarmDefine.getItemId();
    }

    public static String getAlarmDefineId(JSONObject alarmDefine) {
        return alarmDefine.getString("objId") + "-" + alarmDefine.getString("itemId");
    }

    /**
     * 获取报警状态 <报警定义标识，报警时间状态>
     */
    public static AlarmStateVO getAlarmState(String definitionId) {
        return alarmStateMap.get(definitionId);
    }

    /**
     * 设置报警定义
     */
    public static AlarmStateVO setAlarmState(String definitionId, AlarmStateVO alarmState) {
        return alarmStateMap.put(definitionId, alarmState);
    }

    /**
     * 获取报警定义Map <报警定义标识，报警定义详情>
     */
    public static ConcurrentHashMap<String, AlarmDefineVO> getAlarmDefineMap() {
        return alarmDefineMap;
    }

    /**
     * 设置报警定义Map <报警定义标识，报警定义详情>
     */
    public static void setAlarmDefineMap(ConcurrentHashMap<String, AlarmDefineVO> alarmDefineMaps) {
        alarmDefineMap = alarmDefineMaps;
    }

    /**
     * 获取信息点-报警定义map <表号-功能号，[报警定义1,报警定义2]>
     */
    public static ConcurrentHashMap<String, List<AlarmDefineVO>> getInfoAlarmMap() {
        return infoAlarmMap;
    }

    /**
     * 设置信息点-报警定义map <表号-功能号，[报警定义1,报警定义2]>
     */
    public static void setInfoAlarmMap(ConcurrentHashMap<String, List<AlarmDefineVO>> infoAlarmMaps) {
        infoAlarmMap = infoAlarmMaps;
    }

    /**
     * 根据报警定义id获取报警定义详情
     */
    public static AlarmDefineVO getAlarmDefinitionById(String definitionId) {
        return alarmDefineMap.get(definitionId);
    }

    /**
     * 保存报警定义详情
     */
    public static void putAlarmDefinitionById(String definitionId, AlarmDefineVO alarmDefine) {
        Condition condition = JsonUtil.parseJson(alarmDefine.getCondition(), Condition.class);
        List<JSONObject> infoCodes = condition.getInfoCodes();
        if (CollectionUtil.isEmpty(infoCodes)) {
            log.error("报警定义ID为[{}],报警定义详情为[{}] 的报警定义信息点为空,请检查!!!!!  ", definitionId, alarmDefine.toString());
            return;
        }
        String trigger = condition.getTrigger();
        String end = condition.getEnd();
        try {
            AviatorEvaluator.compile(trigger, true);
        } catch (Exception e) {
            log.warn("触发表达式：", trigger);
            log.error("触发表表达式不合法，请校验", e);
            throw new IllegalArgumentException("触发表表达式不合法", e);
        }
        try {
            AviatorEvaluator.compile(end, true);
        } catch (Exception e) {
            log.error("恢复表达式：", end);
            log.error("恢复表达式不合法，请校验", e);
            throw new IllegalArgumentException("恢复表达式不合法", e);
        }
        alarmDefineMap.put(definitionId, alarmDefine);
        for (int i = 0; i < infoCodes.size(); i++) {
            JSONObject infoCode = infoCodes.get(i);
            String meterId = infoCode.getString("meterId");
            String funcId = infoCode.getString("funcId");
            putAlarmDefinitionIdByMeterFuncId(meterId, funcId, alarmDefine);
        }
    }

    public static void putAlarmDefinitionById(AlarmDefineVO alarmDefine) {
        putAlarmDefinitionById(getAlarmDefineId(alarmDefine), alarmDefine);
    }

    /**
     * 隔离或取消隔离报警规则信息
     */
    public static void isolationAlarmDefinitionById(JSONObject alarmConfig) {
        String defineId = getAlarmDefineId(alarmConfig);
        AlarmDefineVO alarmDefine = alarmDefineMap.get(defineId);
        if (alarmDefine == null) {
            return;
        }
        alarmDefine.setOpen(alarmConfig.getIntValue("open"));

        JSONObject condition = alarmConfig.getJSONObject("condition");
        if (condition != null) {
            JSONObject effectTime = condition.getJSONObject("effectTime");
            if (effectTime != null) {
                JsonUtil.parseJson(alarmDefine.getCondition(), Condition.class).setEffectTime(effectTime);
            }
        }

    }

    /**
     * 根据表号功能号获取多条报警定义id
     */
    public static List<AlarmDefineVO> getAlarmDefinitionIdByMeterFuncId(String meterId, String funcId) {
        return infoAlarmMap.get(getKey(meterId, funcId));
    }

    /**
     * 根据表号功能号缓存报警定义
     */
    public static void putAlarmDefinitionIdByMeterFuncId(String meterId, String funcId, AlarmDefineVO definition) {
        List<AlarmDefineVO> definitionList = getAlarmDefinitionIdByMeterFuncId(meterId, funcId);
        if (CollectionUtils.isEmpty(definitionList)) {
            definitionList = new ArrayList<>();
        }
        //去重并添加
        definitionList = definitionList.stream().filter(a -> !getAlarmDefineId(definition).equals(getAlarmDefineId(a))).collect(Collectors.toList());
        definitionList.add(definition);
        infoAlarmMap.put(getKey(meterId, funcId), definitionList);
    }

    /**
     * 删除所有缓存的报警定义
     */
    public static void clearAllAlarmDefine() {
        alarmDefineMap.clear();
        infoAlarmMap.clear();
        //清除编译表达式缓存
        AviatorEvaluator.getInstance().clearExpressionCache();
    }

    /**
     * 根据报警定义删除缓存
     */
    public static void clearAlarmDefine(AlarmDefineVO ad) {
        String defineId = getAlarmDefineId(ad);
        AlarmDefineVO alarmDefine = alarmDefineMap.get(defineId);
        if (!Objects.isNull(alarmDefine)) {
            List<JSONObject> infoCodes = JsonUtil.parseJson(alarmDefine.getCondition(), Condition.class).getInfoCodes();
            infoCodes.stream().forEach(info -> {
                String meterId = info.getString("meterId");
                String funcId = info.getString("funcId");
                List<AlarmDefineVO> AlarmDefineList = infoAlarmMap.getOrDefault(getKey(meterId, funcId), new ArrayList<>());
                infoAlarmMap.put(getKey(meterId, funcId), AlarmDefineList.stream().filter(adTmp -> !getAlarmDefineId(adTmp).equals(defineId)).collect(Collectors.toList()));
            });
        }

        alarmDefineMap.remove(defineId);
        alarmStateMap.remove(defineId);
    }

    /**
     * 判断是否包含有改表号功能号的改报警定义
     */
    public static boolean hasKey(String meterId, String funcId) {
        return infoAlarmMap.containsKey(getKey(meterId, funcId));
    }

    /**
     * 拼接表号-功能号为一个key
     */
    public static String getKey(String meterId, String funcId) {
        return meterId + "-" + funcId;
    }
}
