package com.ctsi.system.controller;

import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.$;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.system.entity.SmsSysSign;
import com.ctsi.system.service.ISmsSignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * smssign 前端控制器
 * </p>
 *
 * @author fxd
 * @date 2022-03-17 09:29:06
 */

@RestController
@AllArgsConstructor
@RequestMapping("/smssign")
@Api(value = "smssign", tags = "smssign接口")
public class SmsSignController extends BaseController {

	private final ISmsSignService smsSignService;

    /**
     * 分页列表
     *
     * @param search 　搜索关键词
     * @return Result
     */
    @PreAuth
    @GetMapping("/page")
    @ApiOperation(value = "smssign列表", notes = "分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
        @ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
        @ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
    })
    public Result<?> page(Search search) {
		return Result.data(smsSignService.listPage(search));
    }

    /**
     * smssign信息
     *
     * @param id Id
     * @return Result
     */
    @PreAuth
    @GetMapping("/get")
    @ApiOperation(value = "smssign信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
    })
    public Result<?> get(@RequestParam String id) {
		return Result.data(smsSignService.getById(id));
	}

    /**
    * smssign设置
    *
    * @param smssign smsSign 对象
    * @return Result
    */
    @PreAuth
    @PostMapping("/set")
    @ApiOperation(value = "smssign设置", notes = "smssign设置,支持新增或修改")
    public Result<?> set(@Valid @RequestBody SmsSysSign smssign) {
		return Result.condition(smsSignService.saveOrUpdate(smssign));
	}

    /**
    * smssign删除
    *
    * @param ids id字符串，根据,号分隔
    * @return Result
    */
    @PreAuth
    @PostMapping("/del")
    @ApiOperation(value = "smssign删除", notes = "smssign删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public Result<?> del(@RequestParam String ids) {
		return Result.condition(smsSignService.removeByIds($.toLongList(ids)));
	}
}

