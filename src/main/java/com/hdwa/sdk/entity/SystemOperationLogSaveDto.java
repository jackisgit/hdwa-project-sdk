package com.hdwa.sdk.entity;

import com.hdwa.sdk.utils.BaseApiUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;

/**
 * @author szj
 * @createTime 2023/9/7
 * @description
 */

@Data
@NoArgsConstructor
public class SystemOperationLogSaveDto {

    /**
     * 日志来源
     */
    private String logSource;
    /**
     * 集团code
     */
    private String groupCode;
    /**
     * 项目id
     */
    private String projectId;
    /**
     * 类型
     */
    private String systemType;
    /**
     * 用户id，logType为user时需要
     */
    private String userId;
    /**
     * 用户名，logType为user时需要
     */
    private String userName;
    /**
     * 角色名，logType为user时需要
     */
    private String roleName;
    /**
     * 日志详情，详见：https://thoughts.teambition.com/workspaces/60812f6205669300474fb287/docs/60ffcea641cef60001c20610
     */
    private String details;
    /**
     * 系统,产品模块，logType为sys时需要
     */
    private String module;
    /**
     * ibms产品模块编码
     */
    private String ibmsSceneCode;

    /**
     * ibms产品模块名称
     */
    private String ibmsSceneName;

    /**
     * ibms设备类型，logType为sys时需要
     */
    private String ibmsClassCode;
    /**
     * ibms设备类型名称，logType为sys时需要
     */
    private String ibmsClassName;

    /**
     * 对象编码
     */
    private String classCode;

    /**
     * 设备名称，logType为sys时需要
     */
    private String deviceName;
    /**
     * 设备编码，logType为sys时需要
     */
    private String deviceCode;
    /**
     * 空间id，logType为sys时需要
     */
    private String spaceId;
    /**
     * 楼层，logType为sys时需要
     */
    private String floor;
    /**
     * IP地址，logType为sys时需要
     */
    private String ipAddress;
    /**
     * 扩展字段，存储其它信息，json格式，如有
     */
    private String extraData;

    /**
     * 创建时间（yyyy-MM-dd HH:mm:ss）
     */
    private String createTime;


    public SystemOperationLogSaveDto(HttpServletRequest request, String groupCode, String projectId, String userId, String logSource, String systemType, String module, String details) {
        this.groupCode = groupCode;
        this.projectId = projectId;
        this.userId = userId;
        this.logSource = logSource;
        this.systemType = systemType;
        this.module = module;
        this.details = details;
        this.ipAddress = BaseApiUtil.getIpAddress(request);
    }

    public SystemOperationLogSaveDto(String groupCode, String projectId, String userId, String logSource, String systemType, String module, String details) {
        this.groupCode = groupCode;
        this.projectId = projectId;
        this.userId = userId;
        this.logSource = logSource;
        this.systemType = systemType;
        this.module = module;
        this.details = details;
        this.ipAddress = "";
    }

}
