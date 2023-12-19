package com.hdwa.alarm.util;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.alarm.cache.AlarmInfoCache;
import com.hdwa.alarm.vo.AlarmDefineVO;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 报警定义逻辑实现类（项目报警定义全量获取，报警定义添加修改删除同步更新）
 **/
public class AlarmDefineUtil {

    public static List<AlarmDefineVO> listAllAlarmDefine(List<AlarmDefineVO> alarmDefineList) {
        AlarmInfoCache.clearAllAlarmDefine();
        for (AlarmDefineVO alarmDefine : alarmDefineList) {
            AlarmInfoCache.putAlarmDefinitionById(alarmDefine);
        }
        return alarmDefineList;
    }

    /**
     * 批量修改（添加）报警定义 （根据报警条目+对象Id先删除，再添加）
     */
    public static void listSomeAlarmDefine(List<AlarmDefineVO> alarmDefineList) {
        for (AlarmDefineVO alarmDefine : alarmDefineList) {
            AlarmInfoCache.clearAlarmDefine(alarmDefine);
            AlarmInfoCache.putAlarmDefinitionById(alarmDefine);
        }
    }

    /**
     * 根据标号功能号删除报警定义
     */
    public static void deleteAlarmDefine(List<AlarmDefineVO> alarmDefineList) {
        for (AlarmDefineVO alarmDefine : alarmDefineList) {
            AlarmInfoCache.clearAlarmDefine(alarmDefine);
        }
    }

    /**
     * 更新报警定义，把系统隔离的报警全部移除
     * 因为隔离和屏蔽的报警定义不产生报警，但是要恢复，所以只能实时判断，所有作废
     */
    @Deprecated
    public static void updateAlarmDefine() {
        ConcurrentHashMap<String, AlarmDefineVO> alarmDefineMap = AlarmInfoCache.getAlarmDefineMap();
        if (ObjectUtil.isNotEmpty(alarmDefineMap)) {
            return;
        }
        for (AlarmDefineVO alarmDefine : alarmDefineMap.values()) {
            if (AlarmInfoCache.isolationSystemList.contains(alarmDefine.getSystemCode())) {
                AlarmInfoCache.clearAlarmDefine(alarmDefine);
            }
        }
    }

    public static void isolationAlarmDefine(List<JSONObject> alarmConfigs) {
        for (JSONObject alarmConfig : alarmConfigs) {
            AlarmInfoCache.isolationAlarmDefinitionById(alarmConfig);
        }
    }
}