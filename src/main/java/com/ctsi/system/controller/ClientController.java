package com.ctsi.system.controller;

import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.$;
import com.ctsi.core.database.entity.Search;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.log.annotation.Log;
import com.ctsi.system.entity.Client;
import com.ctsi.system.service.IClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2022/7/5 14:24
 */
@RestController
@AllArgsConstructor
@RequestMapping("/client")
@Api(value = "客户端", tags = "客户端接口")
public class ClientController extends BaseController {

    private final IClientService clientService;

    /**
     * 分页列表
     *
     * @param search 　搜索关键词
     * @return Result
     */
    @PreAuth
    @Log(value = "客户端列表", exception = "客户端列表请求异常")
    @GetMapping("/page")
    @ApiOperation(value = "客户端列表", notes = "分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
            @ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
            @ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
    })
    public Result<?> page(Search search) {
        return Result.data(clientService.listPage(search));
    }

    /**
     * 客户端信息
     *
     * @param id Id
     * @return Result
     */
    @PreAuth
    @Log(value = "客户端信息", exception = "客户端信息请求异常")
    @GetMapping("/get")
    @ApiOperation(value = "客户端信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
    })
    public Result<?> get(@RequestParam String id) {
        return Result.data(clientService.getById(id));
    }

    /**
     * 客户端设置
     *
     * @param client client 对象
     * @return Result
     */
    @PreAuth
    @Log(value = "客户端设置", exception = "客户端设置请求异常")
    @PostMapping("/set")
    @ApiOperation(value = "客户端设置", notes = "客户端设置,支持新增或修改")
    public Result<?> set(@Valid @RequestBody Client client) {
        return Result.condition(clientService.saveOrUpdate(client));
    }

    /**
     * 客户端删除
     *
     * @param ids id字符串，根据,号分隔
     * @return Result
     */
    @PreAuth
    @Log(value = "客户端删除", exception = "客户端删除请求异常")
    @PostMapping("/del")
    @ApiOperation(value = "客户端删除", notes = "客户端删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public Result<?> del(@RequestParam String ids) {
        return Result.condition(clientService.removeByIds($.toLongList(ids)));
    }
}