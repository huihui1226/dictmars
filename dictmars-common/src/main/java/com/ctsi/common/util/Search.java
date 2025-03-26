package com.ctsi.common.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2022/6/22 17:35
 */
@Data
@ApiModel(description = "搜索条件")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Search implements Serializable {


    private static final long serialVersionUID = 9163037146504408923L;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页")
    private Integer current = 1;

    /**
     * 每页的数量
     */
    @ApiModelProperty(value = "每页的数量")
    private Integer size = 10;

    /**
     * 关键词
     */
    @ApiModelProperty(value = "关键词")
    private String keyword;

    /**
     * 开始日期
     */
    @ApiModelProperty(hidden = true)
    private String startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(hidden = true)
    private String endDate;

    /**
     * 排序属性
     */
    @ApiModelProperty(hidden = true)
    private String prop;

    /**
     * 排序方式：asc,desc
     */
    @ApiModelProperty(hidden = true)
    private String order;

    /**
     * 数据权限
     */
    @ApiModelProperty(hidden = true)
    private List<Long> dataScope;

    /**
     * 通用批量设置的Id集
     */
    private List<Long> ids;

    /**
     * 通用ID查询
     */
    private String id;

    /**
     * 组织机构ID
     */
    private Long orgId;
}

