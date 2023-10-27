package com.hdwa.sdk.entity.scene;

/**
 * @author abao
 * @since 2023/7/25
 * 场景属性对象
 */
public class DataProperty {

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
    public String filterRule;

    /**
     * 查询层级 0表示递归全部、其他正整数表示层次
     */
    public String readLevel;

    /**
     * 偏移量
     */
    public String offsetLevel;

    /**
     * 标识 1表示通过，0表示截断
     */
    public String allowPass;

    /**
     * 自定义对象
     */
    public DataObjectBase customObject;

    /**
     * 静态值
     */
    public String staticValue;

    /**
     * 静态查询
     */
    public DataObjectBase[] staticArray;

    /**
     * 查询语句
     */
    public String querySql;

    /**
     * 附加查询
     */
    public DataProperty[] queryAttached;

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

    public String getFilterRule() {
        return filterRule;
    }

    public void setFilterRule(String filterRule) {
        this.filterRule = filterRule;
    }

    public String getReadLevel() {
        return readLevel;
    }

    public void setReadLevel(String readLevel) {
        this.readLevel = readLevel;
    }

    public String getOffsetLevel() {
        return offsetLevel;
    }

    public void setOffsetLevel(String offsetLevel) {
        this.offsetLevel = offsetLevel;
    }

    public String getAllowPass() {
        return allowPass;
    }

    public void setAllowPass(String allowPass) {
        this.allowPass = allowPass;
    }

    public DataObjectBase getCustomObject() {
        return customObject;
    }

    public void setCustomObject(DataObjectBase customObject) {
        this.customObject = customObject;
    }

    public String getStaticValue() {
        return staticValue;
    }

    public void setStaticValue(String staticValue) {
        this.staticValue = staticValue;
    }

    public DataObjectBase[] getStaticArray() {
        return staticArray;
    }

    public void setStaticArray(DataObjectBase[] staticArray) {
        this.staticArray = staticArray;
    }

    public String getQuerySql() {
        return querySql;
    }

    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }

    public DataProperty[] getQueryAttached() {
        return queryAttached;
    }

    public void setQueryAttached(DataProperty[] queryAttached) {
        this.queryAttached = queryAttached;
    }
}
