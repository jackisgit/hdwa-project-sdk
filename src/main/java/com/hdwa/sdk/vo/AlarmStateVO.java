package com.hdwa.sdk.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 报警时间状态
 **/
@Data
public class AlarmStateVO {
    /**
     * 报警定义ID
     */
    private String definitionId;
    /**
     * 报警状态（ 0-正常 1-报警）
     */
    private String state = "0";
    /**
     * 最近下一次数据正常状态
     */
    private boolean latestDataNormalstate = true;
    /**
     * 是否过期（0-未过期，1-已过期）
     */
    private String expire = "0";
    /**
     * 报警开始时间
     */
    private String alarmStartTime = "";
    /**
     * 报警恢复开始时间
     */
    private String alarmEndTime = "";

    public AlarmStateVO(String definitionId) {
        this.definitionId = definitionId;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    /**
     * 重置除了实时数据正常状态外的数据为默认值
     * (不报警，不过期，报警开始时间和结束时间为空）
     *
     * @return
     */
    public String reset() {
        this.setState("0");
        this.setExpire("0");
        this.setAlarmStartTime("");
        this.setExpire("");
        return JSONObject.toJSONString(this);
    }

    public enum Overdue {

        NORMAL("0", "正常"),
        OVERDUE("1", "已过期");

        private String type;
        private String name;

        Overdue(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public static Overdue getEnumByType(Integer type) {
            for (Overdue status : Overdue.values()) {
                if (status.getType().equals(type)) {
                    return status;
                }
            }
            return null;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum State {

        NORMAL("0", "正常、已恢复"),
        NOT_DEAL("1", "异常、未处理");

        private String type;
        private String name;

        State(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public static Overdue getEnumByType(Integer type) {
            for (Overdue status : Overdue.values()) {
                if (status.getType().equals(type)) {
                    return status;
                }
            }
            return null;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
