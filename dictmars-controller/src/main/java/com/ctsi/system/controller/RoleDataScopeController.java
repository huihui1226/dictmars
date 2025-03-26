package com.ctsi.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.$;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.system.entity.RoleDataScope;
import com.ctsi.system.service.IRoleDataScopeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 系统角色数据范围表 前端控制器
 * </p>
 *
 * @author wang xiao xiang
 */
@RestController
@AllArgsConstructor
@RequestMapping("/role-data-scope")
@Api(value = "系统角色数据范围表", tags = "系统角色数据范围表接口")
public class RoleDataScopeController extends BaseController {

    private final IRoleDataScopeService roleDataScopeService;

    /**
     * 分页列表
     *
     * @param search 　搜索关键词
     * @return Result
     */
    @PreAuth
    @GetMapping("/page")
    @ApiOperation(value = "系统角色数据范围表列表", notes = "分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
        @ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
        @ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
        @ApiImplicitParam(name = "startDate", required = true, value = "创建开始日期", paramType = "form"),
        @ApiImplicitParam(name = "endDate", required = true, value = "创建结束日期", paramType = "form"),
    })
    public Result<?> page(Search search) {
        Page page = new Page(search.getCurrent(), search.getSize());
		return Result.data(roleDataScopeService.listPage(page, search));
    }

    /**
     * 系统角色数据范围表信息
     *
     * @param id Id
     * @return Result
     */
    @PreAuth
    @GetMapping("/get")
    @ApiOperation(value = "系统角色数据范围表信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
    })
    public Result<?> get(@RequestParam String id) {
		return Result.data(roleDataScopeService.getById(id));
	}

    /**
    * 系统角色数据范围表设置
    *
    * @param roleDataScope RoleDataScope 对象
    * @return Result
    */
    @PreAuth
    @PostMapping("/set")
    @ApiOperation(value = "系统角色数据范围表设置", notes = "系统角色数据范围表设置,支持新增或修改")
    public Result<?> set(@Valid @RequestBody RoleDataScope roleDataScope) {
		return Result.condition(roleDataScopeService.saveOrUpdate(roleDataScope));
	}

    /**
    * 系统角色数据范围表删除
    *
    * @param ids id字符串，根据,号分隔
    * @return Result
    */
    @PreAuth
    @PostMapping("/del")
    @ApiOperation(value = "系统角色数据范围表删除", notes = "系统角色数据范围表删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public Result<?> del(@RequestParam String ids) {
		return Result.condition(roleDataScopeService.removeByIds($.toLongList(ids)));
	}
}

