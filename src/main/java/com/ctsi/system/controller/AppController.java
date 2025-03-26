package com.ctsi.system.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.enums.StatusType;
import com.ctsi.core.common.util.$;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.system.entity.App;
import com.ctsi.system.service.IAppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 系统应用表 前端控制器
 * </p>
 *
 * @author wang xiao xiang
 */
@RestController
@AllArgsConstructor
@RequestMapping("/app")
@Api(value = "系统应用表", tags = "系统应用表接口")
public class AppController extends BaseController {

    private final IAppService appService;

    /**
     * 分页列表
     *
     * @param search 　搜索关键词
     * @return Result
     */
    @PreAuth
    @GetMapping("/page")
    @ApiOperation(value = "系统应用表列表", notes = "分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
            @ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
            @ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
    })
    public Result<?> page(Search search) {
        return Result.data(appService.listPage(search));
    }


    /**
     * 系统应用列表
     *
     * @return 应用列表
     */
    @PreAuth
    @GetMapping("/list")
    @ApiOperation(value = "系统应用表列表", notes = "系统应用表列表")
    public Result<?> list() {
        return Result.data(appService.list(Wrappers.<App>query().lambda()
                .eq(App::getStatus, StatusType.ENABLE.getCode())));
    }

    /**
     * 系统应用表信息
     *
     * @param id Id
     * @return Result
     */
    @PreAuth
    @GetMapping("/get")
    @ApiOperation(value = "系统应用表信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
    })
    public Result<?> get(@RequestParam String id) {
        return Result.data(appService.getById(id));
    }

    /**
     * 系统应用表设置
     *
     * @param app App 对象
     * @return Result
     */
    @PreAuth
    @GetMapping("/set")
    @ApiOperation(value = "系统应用表设置", notes = "系统应用表设置,支持新增或修改")
    public Result<?> set(@Valid @RequestBody App app) {
        if(appService.checkCodeExist(app.getCode())>0){
            return Result.fail("该编码已存在");
        }
        return Result.condition(appService.saveOrUpdate(app));
    }

    /**
     * 系统应用编码检查
     *
     * @param code 编码
     * @return Result
     */
    @PreAuth
    @GetMapping("/check")
    @ApiOperation(value = "系统应用表编码校验", notes = "系统应用表编码校验")
    public Result<?> check(@RequestParam String code) {
        if(appService.checkCodeExist(code)>0){
            return Result.fail("该编码已存在");
        }
        return Result.success("编码校验成功");
    }


    /**
     * 系统应用表删除
     *
     * @param ids id字符串，根据,号分隔
     * @return Result
     */
    @PreAuth
    @PostMapping("/del")
    @ApiOperation(value = "系统应用表删除", notes = "系统应用表删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public Result<?> del(@RequestParam String ids) {
        return Result.condition(appService.removeByIds($.toLongList(ids)));
    }

    @PreAuth
    @PostMapping("/set-default")
    @ApiOperation(value = "系统应用设置默认", notes = "系统应用设置默认")
    public Result<?> setDefault(@RequestBody App app) {
        return Result.condition(appService.setDefault(app));
    }
}

