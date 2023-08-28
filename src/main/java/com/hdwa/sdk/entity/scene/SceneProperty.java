package com.hdwa.sdk.entity.scene;

public class SceneProperty {
    public String propertyName;
    public boolean propertyVisible;

    public String propertyValueSchema;// JSONObject、JSONArray、string、double、int、boolean
    public String propertyValueType;// static、query、custom、deamon

    public String filter_rule;// 筛选规则
    public String read_level;// 0表示递归全部、其他正整数表示层次
    public String offset_level;
    public String allow_pass;// 1表示通过，0表示截断

    // type=custom
    public SceneObject custom_object;

    // type=static
    public String static_value;
    public SceneObject[] static_array;

    // type=query
    public String query_sql;
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
