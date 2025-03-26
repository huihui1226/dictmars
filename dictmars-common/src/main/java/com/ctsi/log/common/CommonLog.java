package com.ctsi.log.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2022/7/1 10:44
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "CommonLog对象", description = "普通日志封装")
public class CommonLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志类型
     */
    @ApiModelProperty(value = "日志类型")
    private String type;
    /**
     * 跟踪ID
     */
    @ApiModelProperty(value = "跟踪ID")
    private String traceId;
    /**
     * 日志标题
     */
    @ApiModelProperty(value = "日志标题")
    private String title;
    /**
     * 操作内容
     */
    @ApiModelProperty(value = "操作内容")
    private String operation;
    /**
     * 方法类型
     */
    @ApiModelProperty(value = "方法类型")
    private String methodType;

    /**
     * 方法名
     */
    @ApiModelProperty(value = "方法名")
    private String methodName;

    /**
     * 参数
     */
    @ApiModelProperty(value = "参数")
    private String params;

    /**
     * 类名
     */
    @ApiModelProperty(value = "类名")
    private String classPath;

    /**
     * 请求路径
     */
    @ApiModelProperty(value = "请求路径")
    private String url;

    /**
     * ip地址
     */
    @ApiModelProperty(value = "ip地址")
    private String ip;

    /**
     * 异常内容
     */
    @ApiModelProperty(value = "异常内容")
    private String exception;

    /**
     * 耗时
     */
    @ApiModelProperty(value = "耗时")
    private Long executeTime;
    /**
     * 地区
     */
    @ApiModelProperty(value = "地区")
    private String location;

    /**
     * 浏览器
     */
    @ApiModelProperty(value = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @ApiModelProperty(value = "操作系统")
    private String os;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private String userName;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
    /**
     * 删除标识
     */
    @ApiModelProperty(value = "删除标识")
    private String isDeleted;
    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private Integer tenantId;

    /**
     * 用于计算耗时，起始时间
     */
    private LocalDateTime startTime;
    /**
     * 用于计算耗时，结束时间
     */
    private LocalDateTime finishTime;
}
