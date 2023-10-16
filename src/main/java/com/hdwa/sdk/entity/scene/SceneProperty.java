package com.hdwa.sdk.entity.scene;

/**
 * @author abao
 * @since 2023/7/25
 * 场景属性对象
 */
public class SceneProperty {

    /**
     * 属性名称
     */
    public String propertyName;

    /**
     * 使用状态 0不使用，1启用
     */
    public boolean propertyVisible;

    /**
     * 查询后的返回数据值类型
     * JSONObject、JSONArray、string、double、int、boolean
     */
    public String propertyValueSchema;

    /**
     * 属性值种类
     * static、query、custom、deamon
     */
    public String propertyValueType;

    /**
     * 筛选规则
     */
    public String filter_rule;

    /**
     * 查询层级 0表示递归全部、其他正整数表示层次
     */
    public String read_level;

    /**
     * 偏移量
     */
    public String offset_level;

    /**
     * 标识 1表示通过，0表示截断
     */
    public String allow_pass;

    /**
     * 自定义对象
     */
    public SceneObject custom_object;

    /**
     * 静态值
     */
    public String static_value;

    /**
     * 静态查询
     */
    public SceneObject[] static_array;

    /**
     * 查询语句
     */
    public String query_sql;

    /**
     * 附加查询
     */
    public SceneProperty[] query_attached;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean isPropertyVisible() {
        return propertyVisible;
    }

    public void setPropertyVisible(boolean propertyVisible) {
        this.propertyVisible = propertyVisible;
    }

    public String getPropertyValueSchema() {
        return propertyValueSchema;
    }

    public void setPropertyValueSchema(String propertyValueSchema) {
        this.propertyValueSchema = propertyValueSchema;
    }

    public String getPropertyValueType() {
        return propertyValueType;
    }

    public void setPropertyValueType(String propertyValueType) {
        this.propertyValueType = propertyValueType;
    }

    public String getFilter_rule() {
        return filter_rule;
    }

    public void setFilter_rule(String filter_rule) {
        this.filter_rule = filter_rule;
    }

    public String getRead_level() {
        return read_level;
    }

    public void setRead_level(String read_level) {
        this.read_level = read_level;
    }

    public String getOffset_level() {
        return offset_level;
    }

    public void setOffset_level(String offset_level) {
        this.offset_level = offset_level;
    }

    public String getAllow_pass() {
        return allow_pass;
    }

    public void setAllow_pass(String allow_pass) {
        this.allow_pass = allow_pass;
    }

    public SceneObject getCustom_object() {
        return custom_object;
    }

    public void setCustom_object(SceneObject custom_object) {
        this.custom_object = custom_object;
    }

    public String getStatic_value() {
        return static_value;
    }

    public void setStatic_value(String static_value) {
        this.static_value = static_value;
    }

    public SceneObject[] getStatic_array() {
        return static_array;
    }

    public void setStatic_array(SceneObject[] static_array) {
        this.static_array = static_array;
    }

    public String getQuery_sql() {
        return query_sql;
    }

    public void setQuery_sql(String query_sql) {
        this.query_sql = query_sql;
    }

    public SceneProperty[] getQuery_attached() {
        return query_attached;
    }

    public void setQuery_attached(SceneProperty[] query_attached) {
        this.query_attached = query_attached;
    }
}
