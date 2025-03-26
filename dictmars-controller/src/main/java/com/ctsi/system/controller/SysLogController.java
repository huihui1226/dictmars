package com.ctsi.system.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.$;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.system.entity.SysLog;
import com.ctsi.system.service.ISysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统日志表 前端控制器
 * </p>
 *
 * @author wang xiao xiang
 */
@RestController
@AllArgsConstructor
@RequestMapping("/log")
@Api(tags = "日志管理")
public class SysLogController extends BaseController {

	private final ISysLogService sysLogService;

	/**
	 * 日志分页列表
	 *
	 * @param search 搜索关键词
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/page")
	@ApiOperation(value = "日志列表", notes = "日志列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
			@ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
			@ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
	})
	public Result<?> page(SysLog sysLog, Search search) {
		return Result.data(sysLogService.listPage(sysLog, search));
	}

	/**
	 * 日志删除
	 *
	 * @param ids 　多个id采用逗号分隔
	 * @return Result
	 */
	@PreAuth(hasPerm = "sys:log:delete")
	@PostMapping("/del")
	@ApiOperation(value = "日志删除", notes = "日志删除")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
	})
	public Result<?> del(@RequestParam String ids) {
		return Result.condition(sysLogService.removeByIds($.toLongList(ids)));
	}

	@PreAuth
	@PostMapping("/empty")
	@ApiOperation(value = "清空日志", notes = "清空日志")
	public Result<?> empty(@RequestParam String type) {
		return Result.condition(sysLogService.remove(Wrappers.<SysLog>lambdaQuery().eq(SysLog::getType, type)));
	}

}

