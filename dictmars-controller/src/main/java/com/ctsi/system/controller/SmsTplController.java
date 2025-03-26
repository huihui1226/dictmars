package com.ctsi.system.controller;

import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.$;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.system.entity.SmsSysTpl;
import com.ctsi.system.service.ISmsTplService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * smstpl 前端控制器
 * </p>
 *
 * @author fxd
 * @date 2022-03-16 10:18:50
 */

@RestController
@AllArgsConstructor
@RequestMapping("/smstpl")
@Api(value = "smstpl", tags = "smstpl接口")
public class SmsTplController extends BaseController {

	private final ISmsTplService smsTplService;

    /**
     * 分页列表
     *
     * @param search 　搜索关键词
     * @return Result
     */
    @PreAuth
    @GetMapping("/page")
    @ApiOperation(value = "smstpl列表", notes = "分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
        @ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
        @ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
    })
    public Result<?> page(Search search) {
		return Result.data(smsTplService.listPage(search));
    }

    /**
     * smstpl信息
     *
     * @param id Id
     * @return Result
     */
    @PreAuth
    @GetMapping("/get")
    @ApiOperation(value = "smstpl信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
    })
    public Result<?> get(@RequestParam String id) {
		return Result.data(smsTplService.getById(id));
	}

    /**
    * smstpl设置
    *
    * @param smstpl smsTpl 对象
    * @return Result
    */
    @PreAuth
    @PostMapping("/set")
    @ApiOperation(value = "smstpl设置", notes = "smstpl设置,支持新增或修改")
    public Result<?> set(@Valid @RequestBody SmsSysTpl smstpl) {
		return Result.condition(smsTplService.saveOrUpdate(smstpl));
	}

    /**
    * smstpl删除
    *
    * @param ids id字符串，根据,号分隔
    * @return Result
    */
    @PreAuth
    @PostMapping("/del")
    @ApiOperation(value = "smstpl删除", notes = "smstpl删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public Result<?> del(@RequestParam String ids) {
		return Result.condition(smsTplService.removeByIds($.toLongList(ids)));
	}
}

