package com.hdwa.sdk.enums;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ssz
 * @description 关系
 */
public enum RelationModel {

    /**
     *
     */
    BD2FL("Bd2Fl", "建筑下的楼层", "ArchSubset", "building", "floor", "", "所属建筑", "", "one"),
    /**
     *
     */
    BD2SP("Bd2Sp", "建筑下的空间", "ArchSubset", "building", "space", "", "所属建筑", "", "one"),
    /**
     *
     */
    FL2SP("Fl2Sp", "楼层下的空间", "ArchSubset", "floor", "space", "", "所属楼层", "n", "one"),
    /**
     *
     */
    SY2EQ("Sy2Eq", "系统下的设备", "MechSubset", "system", "equipment", "", "所属系统", "", "one"),
    /**
     *
     */
    EQ2SP("Eq2Sp", "设备所在空间", "MechInArch", "equipment", "space", "所在空间", "", "one", "n"),
    /**
     *
     */
    EQ2BD("Eq2Bd", "设备所在建筑", "MechInArch", "equipment", "building", "所在建筑", "", "one", ""),
    /**
     *
     */
    EQ2FL("Eq2Fl", "设备所在楼层", "MechInArch", "equipment", "floor", "所在楼层", "", "one", ""),
    /**
     *
     */
    SP2SY("Sp2Sy", "空间所属系统", "MechInArch", "space", "system", "所属系统", "被空间服务", "", ""),
    /**
     *
     */
    EQCTRL("EqCtrl", "设备控制设备", "MechCtrl", "equipment", "equipment", "控制设备", "被设备控制", "", ""),
    /**
     *
     */
    EQNORMAL("EqNormal", "照明配电箱供电照明控制模块", "MechPower", "equipment", "equipment", "给设备供电", "被设备供电", "", "");

    /**
     * 关系编码
     */
    private String relCode;
    /**
     * 关系名称
     */
    private String relName;
    /**
     * 图类型
     */
    private String graphCode;
    /**
     * 主对象
     */
    private String objFrom;
    /**
     * 从对象
     */
    private String objTo;
    /**
     * 主对象角度名称
     */
    private String fromName;
    /**
     * 从对象角度名称
     */
    private String toName;
    /**
     * 主对象几对几关系
     */
    private String fromMultiple;
    /**
     * 从对象几对几关系
     */
    private String toMultiple;

    RelationModel(String relCode, String relName, String graphCode, String objFrom, String objTo, String fromName, String toName, String fromMultiple, String toMultiple) {
        this.relCode = relCode;
        this.relName = relName;
        this.graphCode = graphCode;
        this.objFrom = objFrom;
        this.objTo = objTo;
        this.fromName = fromName;
        this.toName = toName;
        this.fromMultiple = fromMultiple;
        this.toMultiple = toMultiple;
    }

    /**
     * @param relCode 关系编码
     * @return
     * @description 根据关系编码获取关系
     */
    public static RelationModel getRelation(String relCode) {
        if (StringUtils.isEmpty(relCode)) {
            return null;
        }
        for (RelationModel relation : RelationModel.values()) {
            if (relation.relCode.equals(relCode)) {
                return relation;
            }
        }
        return null;
    }


    /**
     * @return
     * @description 获取关系枚举
     */
    public static List<Rel> listRelation() {
        List<Rel> list = new ArrayList<>();
        for (RelationModel relation : RelationModel.values()) {
            Rel rel = new Rel();
            BeanUtils.copyProperties(relation, rel);
            list.add(rel);
        }
        return list;
    }

    /**
     * @return
     * @description 获取objType列表
     */
    public static Set<String> listObjType() {
        Set<String> objType = new HashSet<>();
        for (RelationModel relation : RelationModel.values()) {
            objType.add(relation.getObjFrom());
            objType.add(relation.getObjTo());
        }
        return objType;
    }

    /**
     * @return
     * @description 获取图例列表
     */
    public static Set<String> listGraphCode() {
        Set<String> graphCodeList = new HashSet<>();
        for (RelationModel relation : RelationModel.values()) {
            graphCodeList.add(relation.getGraphCode());
        }
        return graphCodeList;
    }


    public String getRelCode() {
        return relCode;
    }

    public void setRelCode(String relCode) {
        this.relCode = relCode;
    }

    public String getRelName() {
        return relName;
    }

    public void setRelName(String relName) {
        this.relName = relName;
    }

    public String getGraphCode() {
        return graphCode;
    }

    public void setGraphCode(String graphCode) {
        this.graphCode = graphCode;
    }

    public String getObjFrom() {
        return objFrom;
    }

    public void setObjFrom(String objFrom) {
        this.objFrom = objFrom;
    }

    public String getObjTo() {
        return objTo;
    }

    public void setObjTo(String objTo) {
        this.objTo = objTo;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getFromMultiple() {
        return fromMultiple;
    }

    public void setFromMultiple(String fromMultiple) {
        this.fromMultiple = fromMultiple;
    }

    public String getToMultiple() {
        return toMultiple;
    }

    public void setToMultiple(String toMultiple) {
        this.toMultiple = toMultiple;
    }


    @Data
    public static class Rel {
        /**
         * 关系编码
         */
        private String relCode;
        /**
         * 关系名称
         */
        private String relName;
        /**
         * 图类型
         */
        private String GraphCode;
        /**
         * 主对象
         */
        private String objFrom;
        /**
         * 从对象
         */
        private String objTo;
        /**
         * 主对象角度名称
         */
        private String fromName;
        /**
         * 从对象角度名称
         */
        private String toName;
        /**
         * 主对象几对几关系
         */
        private String fromMultiple;
        /**
         * 从对象几对几关系
         */
        private String toMultiple;
    }
}
