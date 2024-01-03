package com.hdwa.sdk.constant;

/**
 * @author abao
 * @since 2023/8/10
 * 请求路径常量
 */
public class UrlConstant {

    /**
     * UTF8
     */
    public static final String UTF8 = "UTF-8";

    /**
     * 验证头属性值
     */
    public static final String AUTHORIZATION = "Authorization";

    /**
     * 类型定义数据 接口路径
     */
    public static final String LIST_CLASS_DEFINER_URL = "/openApi/listClassDefiner";


    /**
     * 接口数据路径
     */
    public static final String GET_CONFIG_API_URL = "/openApi/getConfigApi";


    /**
     * post 接口路径
     */
    public static final String POST_URL = "/openApi/post";

    /**
     * 点位定义数据 接口路径
     */
    public static final String LIST_POINT_DEFINER_URL = "/openApi/listPointDefiner";

    /**
     * 对象数据 接口路径
     */
    public static final String LIST_OBJECT_DATA_URL = "/openApi/listObjectData";

    /**
     * 关系数据 接口路径
     */
    public static final String LIST_RELATION_DATA_URL = "/openApi/listRelationData";


    /**
     * 控制指令 接口路径
     */
    public static final String iot_project_control = "/sync_pointsetbatch_post";


    /**
     * 逻辑分组数据 接口路径
     */
    public static final String LOGICAL_GROUP_URL = "/openApi/queryLogicalGrouping";


    /**
     * 验证token接口 接口路径
     */
    public static final String VERIFY_TOKEN_URL = "/monitor/core/staticFiles/checking";


    /**
     * 保存日志 接口路径
     */
    public static final String SAVE_LOG_URL = "/openApi/insert";

    /**
     * 逻辑分组包含的对象数据 接口路径
     */
    public static final String LOGICAL_OBJECT_URL = "/openApi/queryLogicalObject";


    /**
     * 查询报警记录 接口路径
     */
    public static final String ALARM_RECORD_PAGE = "/openApi/alarm/page";

    /**
     * 查询工单接口 接口路径
     */
    public static final String QUERY_ORDER_STATE = "/openApi/alarm/queryOrderStateByAlarmIds";


    /**
     * 类型定义数据 文件名
     */
    public static final String CLASS_ARRAY = "classArray.json";


    /**
     * 基础-产品模块数据 文件名
     */
    public static final String SCENE_ARRAY = "sceneArray.json";

    /**
     * ibms逻辑分组数据 文件名
     */
    public static final String IMBS_GROUP_ARRAY = "ibmsGroupArray.json";

    /**
     * .json 文件格式
     */
    public static final String JSON_FILE = ".json";

    /**
     * 点位配置列表 文件名
     */
    public static final String POINT_LIST = "point-list.json";
    /**
     * 点位关系配置 文件名
     */
    public static final String POINT_RELATION = "point-relation.json";

    /**
     * tmp-dataSource 文件名
     */
    public static final String TMP_DATASOURCE = "temp-dataSource.json";

}
