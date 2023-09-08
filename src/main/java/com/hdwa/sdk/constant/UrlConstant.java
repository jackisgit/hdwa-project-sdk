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
     * 测试token
     */
    public static final String TOKEN = "Bearer 1e25db16-123d-4743-b253-13ddac3d5c46";


    /**
     * 类型定义数据 接口路径
     */
    public static final String LIST_CLASS_DEFINER_URL = "/classDefiner/listClassDefiner";


    /**
     * post 接口路径
     */
    public static final String POST_URL = "/pathApi/post";

    /**
     * 点位定义数据 接口路径
     */
    public static final String LIST_POINT_DEFINER_URL = "/pointDefiner/listPointDefiner";

    /**
     * 对象数据 接口路径
     */
    public static final String LIST_OBJECT_DATA_URL = "/objectData/listObjectData";

    /**
     * 关系数据 接口路径
     */
    public static final String LIST_RELATION_DATA_URL = "/relation/listRelationData";

    /**
     * 逻辑分组数据 接口路径
     */
    public static final String LOGICAL_GROUP_URL = "/logicalGrouping/list";

    /**
     * 逻辑分组包含的对象数据 接口路径
     */
    public static final String LOGICAL_OBJECT_URL = "/logicalObject/listPage";


    /**
     * 查询报警记录 接口路径
     */
    public static final String ALARM_RECORD_PAGE = "/alarm-record/page";

    /**
     * 查询工单接口 接口路径
     */
    public static final String QUERY_ORDER_STATE = "/alarmToWorkOrder/queryOrderStateByAlarmIds";

    /**
     * 控制指令 接口路径
     */
    public static final String iot_project_control = "/sync_pointsetbatch_post";


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
