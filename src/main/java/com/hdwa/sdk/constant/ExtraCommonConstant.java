package com.hdwa.sdk.constant;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExtraCommonConstant {

	public static final Object sync = new Object();
	
	public static final String RESULT = "Result";
	public static final String CONTENT = "Content";
	
	public static final String SUCCESS = "success";
	public static final String CRITERIA = "criteria";
	public static final String USER_ID = "userId";
	public static final String GROUP_CODE = "groupCode";
	public static final String PROJECT_ID = "projectId";
    public static final String IBMS_SCENE_CODE = "ibmsSceneCode";
    public static final String IBMS_CLASS_CODE = "ibmsClassCode";
	public static final String IBMS_ALARM_ALL_SCENE_CODE = "/alarm-config/queryAllIbmsScene";
	public static final String IBMS_ALARM_ALL_ALARM_DEFINE = "/alarm-config/queryAlarmDefine";
	public static final String RWD_INSTANCE_OBJECT_QUERY = "/rwd/instance/object/query?projectId={}&groupCode={}";
	
	public static String getObjectQueryUrl() {
		return CommonConst.zktDMP + StrUtil.format(RWD_INSTANCE_OBJECT_QUERY, CommonConst.projectId, CommonConst.groupCode);
	}
	
	/**
	 * 查询对应设备类下的所有设备数据
	 */
	public static JSONArray getObjectArray(String ibmsSceneCode, String ibmsClassCode) {
		try {
			JSONObject params = new JSONObject();
			JSONObject criteria = new JSONObject();
	        criteria.put(IBMS_SCENE_CODE, ibmsSceneCode);
	        criteria.put(IBMS_CLASS_CODE, ibmsClassCode);
	        params.put(CRITERIA, criteria);
			String post = HttpUtil.post(getObjectQueryUrl(), params.toJSONString(), 600 * 1000);
			if (StrUtil.isBlank(post)) {
				return null;
			}
			JSONObject result = JSONObject.parseObject(post);
			if (!SUCCESS.equals(result.getString("result"))) {
				return null;
			}
			JSONArray datas = result.getJSONArray("data");
			if (datas == null || datas.size() == 0) {
				return null;
			}
			
			return datas;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("查询管理范围设备信息失败", e);
		}
		return null;
	}
	
	/**
	 * key 拼接
	 */
    public static String getKey(String value1, String value2) {
        return value1 + "-" + value2;
    }
}