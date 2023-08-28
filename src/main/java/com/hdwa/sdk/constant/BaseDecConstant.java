package com.hdwa.sdk.constant;

import java.time.format.DateTimeFormatter;

/**
 * @author abao
 * @since 2023/7/24
 * 动态接口常量类
 */
public class BaseDecConstant {

    /**
     * 基础String
     */
    public static final String BASE = "基础";

    /**
     * y String
     */
    public static final String Y = "Y";


    /**
     * CBD接口路径 String
     */
    public static final String CBD_API_JSON = "BJCBD";

    /**
     * CBD项目id String
     */
    public static final String CBD_PROJECT_ID = "Pj1101050001";

    /**
     * 增城项目id String
     */
    public static final String ZNEG_CHENG_PROJECT_ID = "Pj4401830001";

    /**
     * 浦西项目id String
     */
    public static final String PU_XI_PROJECT_ID = "Pj3505030030";

    /**
     * 增城接口路径 String
     */
    public static final String ZENG_CHENG_API_JSON = "GZZC";

    /**
     * 浦西接口路径 String
     */
    public static final String PU_XI_API_JSON = "QZPX";

    /**
     * 其它接口路径 String
     */
    public static final String BASE_API_JSON = "BASE";

    /**
     * 接口json路径 String
     */
    public static final String JSON_PATH = "config/";


    /**
     * 产品模块String
     */
    public static final String PRODUCT_MODULE = "产品模块";


    /**
     * 数据字典类型名称 String
     */
    public static final String DATA_DICT_TYPE_NAME = "数据字典类型名称";


    /**
     * 关系信息 String
     */
    public static final String INFO_REPLACE = "info_replace";


    /**
     * 管理对象String
     */
    public static final String MANAGED_OBJECT = "管理对象";

    /**
     * 字典编码 String
     */
    public static final String DICTIONARY_ENCODING = "字典编码";

    /**
     * idString
     */
    public static final String ID = "id";

    /**
     * name String
     */
    public static final String NAME = "name";

    /**
     * aliasName String
     */
    public static final String ALIAS_NAME = "aliasName";
    /**
     * ibms String
     */
    public static final String IBMS = "ibms";
    /**
     * ibmsSceneCode String
     */
    public static final String IBMS_SCENE_CODE = "ibmsSceneCode";
    /**
     * ibmsClassCode String
     */
    public static final String IBMS_CLASS_CODE = "ibmsClassCode";
    /**
     * logicalGroupingId String
     */
    public static final String LOGICAL_GROUPING_ID = "logicalGroupingId";
    /**
     * -id string
     */
    public static final String ID2 = "-id";

    /**
     * code String
     */
    public static final String CODE = "code";

    /**
     * 条件 String
     */
    public static final String CONDITION = "condition";

    /**
     * firstTag String
     */
    public static final String FIRST_TAG = "firstTag";

    /**
     * 类型编码string
     */
    public static final String CLASS_CODE = "classCode";


    /**
     * 图类型编码string
     */
    public static final String GRAPH_CODE = "graphCode";

    /**
     * 关系编码string
     */
    public static final String REL_CODE = "relCode";

    /**
     * 类型string
     */
    public static final String TYPE = "type";

    /**
     * from string
     */
    public static final String FROM = "from";


    /**
     * to string
     */
    public static final String TO = "to";


    /**
     * 对象类型string
     */
    public static final String OBJ_TYPE = "objType";


    /**
     * rwd/class string
     */
    public static final String RWD_CLASS_PATH = "rwd/class";

    /**
     * rwd/info string
     */
    public static final String RWD_INFO_PATH = "rwd/info/";

    /**
     * rwd/object string
     */
    public static final String RWD_OBJECT_PATH = "rwd/object/";

    /**
     * rwd/relation string
     */
    public static final String RWD_RELATION_PATH = "rwd/relation/";


    /**
     * 主对象 string
     */
    public static final String OBJ_FROM = "objFrom";

    /**
     * meter-funcid string
     */
    public static final String METER_FUNGICIDE = "meter-funcid";


    /**
     * 有效状态 string
     */
    public static final String VALID = "valid";

    /**
     * 从对象 string
     */
    public static final String OBJ_TO = "objTo";


    /**
     * secondTag String
     */
    public static final String SECOND_TAG = "secondTag";

    /**
     * 运行参数 String
     */
    public static final String RUN_PARAM = "运行参数";


    /**
     * 事件记录 String
     */
    public static final String EVENT_RECORD = "事件记录";

    /**
     * 报警消息 String
     */
    public static final String ALARM_MES = "报警消息";


    /**
     * 设定参数 String
     */
    public static final String SET_PARAM = "设定参数";

    /**
     * 设定反馈值 String
     */
    public static final String SET_FEEDBACK_VALUE = "设定反馈值";

    /**
     * 清单String
     */
    public static final String DETAILED_LIST = "清单";

    /**
     * 逻辑运算符String
     */
    public static final String LOGIC_OPERATOR = "LogicOperator";


    /**
     * and String
     */
    public static final String AND = "and";

    /**
     * 标准 String
     */
    public static final String CRITERIA = "Criteria";
    /**
     * criteria String
     */
    public static final String CRITERIA_2 = "criteria";

    /**
     * 标准复数 String
     */
    public static final String CRITERIAS = "Criterias";

    /**
     * 静态的 String
     */
    public static final String STATIC = "static";

    /**
     * JSONArray String
     */
    public static final String JSONARRAY = "JSONArray";

    /**
     * JSONObject String
     */
    public static final String JSONOBJECT = "JSONObject";

    /**
     * QUERY String
     */
    public static final String QUERY = "query";

    /**
     * DEAMON String
     */
    public static final String DEAMON = "deamon";

    /**
     * 自定义 String
     */
    public static final String CUSTOM = "custom";

    /**
     * 手自动点位 String
     */
    public static final String MANUAL_AUTO_SET = "manualAutoSet";

    /**
     * one String
     */
    public static final String ONE = "one";

    /**
     * QueryType String
     */
    public static final String QUERY_TYPE = "QueryType";

    /**
     * select String
     */
    public static final String SELECT = "select";
    /**
     * expression String
     */
    public static final String EXPRESSION = "expression";
    /**
     * trend String
     */
    public static final String TREND = "trend";

    /**
     * curve String
     */
    public static final String CURVE = "curve";
    /**
     * ref String
     */
    public static final String REF = "ref";

    /**
     * int String
     */
    public static final String INT = "int";
    /**
     * double String
     */
    public static final String DOUBLE = "double";


    /**
     * rwd String
     */
    public static final String RWD = "rwd";

    /**
     * WD String
     */
    public static final String WD = "WD";

    /**
     * class String
     */
    public static final String CLASS = "class";


    /**
     * object String
     */
    public static final String OBJECT = "object";


    /**
     * equipment String
     */
    public static final String EQUIPMENT = "equipment";

    /**
     * system String
     */
    public static final String SYSTEM = "system";

    /**
     * space String
     */
    public static final String SPACE = "space";

    /**
     * info String
     */
    public static final String INFO = "info";

    /**
     * point String
     */
    public static final String POINT = "point";


    /**
     * relation String
     */
    public static final String RELATION = "relation";


    /**
     * groupCode String
     */
    public static final String GROUP_CODE = "groupCode";

    /**
     * projectId String
     */
    public static final String PROJECT_ID = "projectId";


    /**
     * read_level String
     */
    public static final String READ_LEVEL = "read_level";


    /**
     * result String
     */
    public static final String RESULT = "result";


    /**
     * data String
     */
    public static final String DATA = "data";

    /**
     * dataSource String
     */
    public static final String DATA_SOURCE = "dataSource";


    /**
     * Content String
     */
    public static final String CONTENT = "Content";

    /**
     * path String
     */
    public static final String PATH = "path";


    /**
     * count String
     */
    public static final String COUNT = "count";

    /**
     * Source String
     */
    public static final String SOURCE = "Source";


    /**
     * Target String
     */
    public static final String TARGET = "Target";


    /**
     * success String
     */
    public static final String SUCCESS = "success";


    /**
     * onlyCount String
     */
    public static final String ONLY_COUNT = "onlyCount";


    /**
     * sequenceNo String
     */
    public static final String SEQUENCE_NO = "sequenceNo";

    /**
     * infoCode String
     */
    public static final String INFO_CODE = "infoCode";

    /**
     * infoType String
     */
    public static final String INFO_TYPE = "infoType";

    /**
     * infoName String
     */
    public static final String INFO_NAME = "infoName";


    /**
     * infoAlias String
     */
    public static final String INFO_ALIAS = "infoAlias";

    /**
     * isKeyPoint String
     */
    public static final String IS_KEY_POINT = "isKeyPoint";


    /**
     * isBatchControlParam String
     */
    public static final String IS_BATCH_CONTROL_PARAM = "isBatchControlParam";


    /**
     * isVisible String
     */
    public static final String IS_VISIBLE = "isVisible";

    /**
     * 点位配置表格名称 String
     */
    public static final String POINT_FILE_NAME = "point.xlsx";

    /**
     * 配置文件目录名称 String
     */
    public static final String CONFIG_DIR = "config";

    /**
     * jar同级目录标识 String
     */
    public static final String USER_DIR = "user.dir";

    /**
     * 日期格式化
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
}
