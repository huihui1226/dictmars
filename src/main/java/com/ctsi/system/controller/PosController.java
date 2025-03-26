package com.ctsi.system.controller;

import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.$;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.system.entity.Pos;
import com.ctsi.system.service.IPosService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 系统职位表 前端控制器
 * </p>
 *
 * @author wang xiao xiang
 */
@RestController
@AllArgsConstructor
@RequestMapping("/pos")
@Api(value = "系统职位表", tags = "系统职位表接口")
public class PosController extends BaseController {

	private final IPosService posService;

	/**
	 * 分页列表
	 *
	 * @param search 　搜索关键词
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/page")
	@ApiOperation(value = "系统职位表列表", notes = "分页查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
			@ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
			@ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
	})
	public Result<?> page(Search search) {
		return Result.data(posService.listPage(search));
	}

	/**
	 * 系统职位表信息
	 *
	 * @param id Id
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/get")
	@ApiOperation(value = "系统职位表信息", notes = "根据ID查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
	})
	public Result<?> get(@RequestParam String id) {
		return Result.data(posService.getById(id));
	}

	/**
	 * 系统职位表设置
	 *
	 * @param pos Pos 对象
	 * @return Result
	 */
	@PreAuth
	@PostMapping("/set")
	@ApiOperation(value = "系统职位表设置", notes = "系统职位表设置,支持新增或修改")
	public Result<?> set(@Valid @RequestBody Pos pos) {
		return Result.condition(posService.saveOrUpdate(pos));
	}

	/**
	 * 系统职位表删除
	 *
	 * @param ids id字符串，根据,号分隔
	 * @return Result
	 */
	@PreAuth
	@PostMapping("/del")
	@ApiOperation(value = "系统职位表删除", notes = "系统职位表删除")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
	})
	public Result<?> del(@RequestParam String ids) {
		return Result.condition(posService.removeByIds($.toLongList(ids)));
	}

	/**
	 * 系统职位列表
	 *
	 * @param pos 根据职位编码模糊查询
	 * @return 职位列表
	 */
	@PreAuth
	@GetMapping("/list")
	@ApiOperation(value = "系统职位列表", notes = "系统职位列表")
	public Result<?> list(Pos pos) {
		return Result.data(posService.list(pos));
	}
}

