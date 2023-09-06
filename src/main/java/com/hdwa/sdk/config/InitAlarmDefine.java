package com.hdwa.sdk.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.CommonConst;
import com.hdwa.sdk.constant.ExtraCommonConstant;
import com.hdwa.sdk.utils.AlarmDefineUtil;
import com.hdwa.sdk.vo.AlarmDefineVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 报警定义初始化
 */
@Slf4j
@Order(-20)
@Component
public class InitAlarmDefine implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		log.info("****************** 开始拉取报警规则信息 ******************");
		
		JSONObject params = new JSONObject();
		params.put(ExtraCommonConstant.USER_ID, CommonConst.systemId);
		params.put(ExtraCommonConstant.GROUP_CODE, CommonConst.groupCode);
		params.put(ExtraCommonConstant.PROJECT_ID, CommonConst.projectId);
		String ibmsClassCodeStr = HttpUtil.post(CommonConst.alarmServerUrl + ExtraCommonConstant.IBMS_ALARM_ALL_SCENE_CODE, params.toJSONString(), 180 * 1000);
		if (StrUtil.isBlank(ibmsClassCodeStr)) {
			log.warn("不存在产品模块配置，无报警规则要更新");
			return;
		}
		JSONObject ibmsClassCodeObject = JSONObject.parseObject(ibmsClassCodeStr);
		if (!ExtraCommonConstant.SUCCESS.equals(ibmsClassCodeObject.getString(ExtraCommonConstant.RESULT))) {
			log.warn("远程报警服务异常，无报警规则要更新");
			return;
		}
		JSONArray ibmsClassCodes = ibmsClassCodeObject.getJSONArray(ExtraCommonConstant.CONTENT);
		if (ibmsClassCodes == null || ibmsClassCodes.size() == 0) {
			log.warn("不存在产品模块配置，无报警规则要更新");
			return;
		}
		// 根据产品模块获取对应的报警规则
		String alarmDefineUrl = CommonConst.alarmServerUrl + ExtraCommonConstant.IBMS_ALARM_ALL_ALARM_DEFINE;
		for (int i = 0; i < ibmsClassCodes.size(); i++) {
			JSONObject jsonObject = ibmsClassCodes.getJSONObject(i);
			JSONObject alarmDefineParam = new JSONObject();
			alarmDefineParam.put(ExtraCommonConstant.USER_ID, CommonConst.systemId);
			alarmDefineParam.put(ExtraCommonConstant.GROUP_CODE, CommonConst.groupCode);
			alarmDefineParam.put(ExtraCommonConstant.PROJECT_ID, CommonConst.projectId);
			alarmDefineParam.put(ExtraCommonConstant.IBMS_SCENE_CODE, jsonObject.getString(ExtraCommonConstant.IBMS_SCENE_CODE));
			alarmDefineParam.put(ExtraCommonConstant.IBMS_CLASS_CODE, jsonObject.getString(ExtraCommonConstant.IBMS_CLASS_CODE));
			String alarmDefineStr = HttpUtil.post(alarmDefineUrl, alarmDefineParam.toJSONString(), 180 * 1000);
			if (StrUtil.isBlank(alarmDefineStr)) {
				log.warn("此产品模块下无报警规则配置");
				continue;
			}
			JSONObject alarmDefineObject = JSONObject.parseObject(alarmDefineStr);
			if (!ExtraCommonConstant.SUCCESS.equals(alarmDefineObject.getString(ExtraCommonConstant.RESULT))) {
				log.warn("远程报警服务异常，无报警规则要更新");
				continue;
			}
			
			String alarmDefines = alarmDefineObject.getString(ExtraCommonConstant.CONTENT);
			if (StrUtil.isBlank(alarmDefines)) {
				log.warn("无报警规则要更新");
				continue;
			}
			List<AlarmDefineVO> alarmDefineList = JSONArray.parseArray(alarmDefines, AlarmDefineVO.class);
            if (CollectionUtil.isNotEmpty(alarmDefineList)) {
				AlarmDefineUtil.listSomeAlarmDefine(alarmDefineList);
            }
		}
		
		log.info("****************** 报警规则信息拉取完毕 ******************");
	}
}