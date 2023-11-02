package com.hdwa.alarm.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.alarm.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NettyMessage<T> extends Packet implements Serializable {
    /*
    唯一标识
     */
    private long streamId;
    private String channelId;
    /**
     * 操作类型：1-请求、2 -响应、3-通知、
     * 4-边缘端获取报警定义、
     * 5-边缘端主动推送报警记录、
     * 6-边缘端主动更新报警记录状态、
     * 7-云端推送修改的报警定义给边缘端(增量新增修改报警定义)、
     * 8-云端把报警记录的id推送到边缘端
     * 9-边缘端取报警定义，云端推送给边缘端的标记(全量报警定义)
     * 10-云端推送删除的报警定义给边缘端(增量删除报警定义)、
     * 11-云端下发隔离系统给边缘端
     * 200 - 建立连接，此时的projectId == 项目id
     * 12-云端更新报警状态
     */
    private int opCode;
    /**
     * 请求来源
     */
    private String projectId;
    private String groupCode;

    /**
     * 传输内容
     */
    private List<T> content;
    /**
     * 备注说明
     */
    private String remark;
    /**
     * 成功标识
     */
    private Boolean success;

    public NettyMessage(String channelId, int opCode, String projectId, String groupCode) {
        this.channelId = channelId;
        this.opCode = opCode;
        this.projectId = projectId;
        this.groupCode = groupCode;
    }

    public List<T> getContent() {
        return content;
    }
    public List<T> getContent(Class<T> t) {
        return JsonUtil.jsonToList(JSON.toJSONString(t), t);
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    /**
     * 获取协议指令
     *
     * @return 返回指令值
     */
    @Override
    public Byte getCommand() {
        return Command.NETTY_MESSAGE;
    }
}
