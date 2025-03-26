package com.ctsi.system.controller;

import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.$;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.system.entity.SmsSysMsg;
import com.ctsi.system.service.ISmsMsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 短信信息模块 前端控制器
 * </p>
 *
 * @author fxd
 * @date 2022-03-15 16:30:51
 */

@RestController
@AllArgsConstructor
@RequestMapping("/smsmsg")
@Api(value = "短信信息模块", tags = "短信信息模块接口")
public class SmsMsgController extends BaseController {

	private final ISmsMsgService smsMsgService;

    @PreAuth
    @PostMapping("/send")
    @ApiOperation(value = "短信信息模块设置", notes = "短信信息模块设置,支持新增或修改")
    public Result<?> send(@Valid @RequestBody SmsSysMsg smsmsg) {
        return Result.condition(smsMsgService.send(smsmsg));
    }

    /**
     * 分页列表
     *
     * @param search 　搜索关键词
     * @return Result
     */
    @PreAuth
    @GetMapping("/page")
    @ApiOperation(value = "短信信息模块列表", notes = "分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
        @ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
        @ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
    })
    public Result<?> page(Search search) {
		return Result.data(smsMsgService.listPage(search));
    }

    /**
     * 短信信息模块信息
     *
     * @param id Id
     * @return Result
     */
    @PreAuth
    @GetMapping("/get")
    @ApiOperation(value = "短信信息模块信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
    })
    public Result<?> get(@RequestParam String id) {
		return Result.data(smsMsgService.getById(id));
	}

    /**
    * 短信信息模块设置
    *
    * @param smsmsg smsMsg 对象
    * @return Result
    */
    @PreAuth
    @PostMapping("/set")
    @ApiOperation(value = "短信信息模块设置", notes = "短信信息模块设置,支持新增或修改")
    public Result<?> set(@Valid @RequestBody SmsSysMsg smsmsg) {
		return Result.condition(smsMsgService.saveOrUpdate(smsmsg));
	}

    /**
    * 短信信息模块删除
    *
    * @param ids id字符串，根据,号分隔
    * @return Result
    */
    @PreAuth
    @PostMapping("/del")
    @ApiOperation(value = "短信信息模块删除", notes = "短信信息模块删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public Result<?> del(@RequestParam String ids) {
		return Result.condition(smsMsgService.removeByIds($.toLongList(ids)));
	}
}

