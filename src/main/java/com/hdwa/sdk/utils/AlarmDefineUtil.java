package com.hdwa.sdk.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.cache.AlarmInfoCache;
import com.hdwa.sdk.vo.AlarmDefineVO;

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
     * @param alarmDefineList:
     * @description:批量修改（添加）报警定义 （根据报警条目+对象Id先删除，再添加）
     * @exception:
     * @author: LuoGuangyi
     * @company: Persagy Technology Co.,Ltd
     * @return: void
     * @since: 2020/10/26 20:38
     * @version: V1.0
     */
    public static void listSomeAlarmDefine(List<AlarmDefineVO> alarmDefineList) {
        for (AlarmDefineVO alarmDefine : alarmDefineList) {
        	AlarmInfoCache.clearAlarmDefine(alarmDefine);
        	AlarmInfoCache.putAlarmDefinitionById(alarmDefine);
        }
    }

    /**
     * @param alarmDefineList:
     * @description:根据标号功能号删除报警定义
     * @exception:
     * @author: LuoGuangyi
     * @company: Persagy Technology Co.,Ltd
     * @return: void
     * @since: 2020/10/29 18:26
     * @version: V1.0
     */
    public static void deleteAlarmDefine(List<AlarmDefineVO> alarmDefineList) {
        for (AlarmDefineVO alarmDefine : alarmDefineList) {
        	AlarmInfoCache.clearAlarmDefine(alarmDefine);
        }
    }

    /**
     * @param
     * @description: 更新报警定义，把系统隔离的报警全部移除
     * 因为隔离和屏蔽的报警定义不产生报警，但是要恢复，所以只能实时判断，所有作废
     * @author: LuoGuangyi
     * @createTime: 2021/03/25 18:08
     * @return: void
     * @expression
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