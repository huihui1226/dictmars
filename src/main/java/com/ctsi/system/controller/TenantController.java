package com.ctsi.system.controller;

import com.ctsi.system.entity.Tenant;
import com.ctsi.system.service.ITenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.$;
import com.ctsi.core.database.entity.Search;


import org.springframework.web.bind.annotation.RestController;

import com.ctsi.core.web.controller.BaseController;
import javax.validation.Valid;

/**
 * <p>
 * 租户表 前端控制器
 * </p>
 *
 * @author chenfei
 * @date 2022-07-07 15:49:50
 */

@RestController
@AllArgsConstructor
@RequestMapping("/tenant")
@Api(value = "租户表", tags = "租户表接口")
public class TenantController extends BaseController {

	private final ITenantService tenantService;

    /**
     * 分页列表
     *
     * @param search 　搜索关键词
     * @return Result
     */
    @PreAuth
    @GetMapping("/page")
    @ApiOperation(value = "租户表列表", notes = "分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
        @ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
        @ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
    })
    public Result<?> page(Search search) {
		return Result.data(tenantService.listPage(search));
    }

    /**
     * 租户表信息
     *
     * @param id Id
     * @return Result
     */
    @PreAuth
    @GetMapping("/get")
    @ApiOperation(value = "租户表信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
    })
    public Result<?> get(@RequestParam String id) {
		return Result.data(tenantService.getById(id));
	}

    /**
    * 租户表设置
    *
    * @param tenant tenant 对象
    * @return Result
    */
    @PreAuth
    @PostMapping("/set")
    @ApiOperation(value = "租户表设置", notes = "租户表设置,支持新增或修改")
    public Result<?> set(@Valid @RequestBody Tenant tenant) {
		return Result.condition(tenantService.saveOrUpdate(tenant));
	}

    /**
    * 租户表删除
    *
    * @param ids id字符串，根据,号分隔
    * @return Result
    */
    @PreAuth
    @PostMapping("/del")
    @ApiOperation(value = "租户表删除", notes = "租户表删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public Result<?> del(@RequestParam String ids) {
		return Result.condition(tenantService.removeByIds($.toLongList(ids)));
	}
}

