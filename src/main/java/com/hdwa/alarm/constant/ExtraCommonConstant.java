package com.hdwa.alarm.constant;

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
	public static final String IBMS_ALARM_ALL_SCENE_CODE = "/openApi/alarm/queryAllIbmsScene";
	public static final String IBMS_ALARM_ALL_ALARM_DEFINE = "/openApi/alarm/queryAlarmDefine";
	public static final String RWD_INSTANCE_OBJECT_QUERY = "/rwd/instance/object/query?projectId={}&groupCode={}";
}