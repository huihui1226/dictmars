package com.ctsi.system.controller;

import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.$;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.core.web.tree.ForestNodeMerger;
import com.ctsi.system.entity.Dict;
import com.ctsi.system.service.IDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author wang xiao xiang
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dict")
@Api(value = "字典表", tags = "字典表接口")
public class DictController extends BaseController {

	private final IDictService dictService;

	/**
	 * 分页列表
	 *
	 * @param search 　搜索关键词
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/page")
	@ApiOperation(value = "字典表列表", notes = "分页查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
			@ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
			@ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
	})
	public Result<?> page(Search search) {
		return Result.data(dictService.listPage(search));
	}

	/**
	 * 字典表信息
	 *
	 * @param id Id
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/get")
	@ApiOperation(value = "字典表信息", notes = "根据ID查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
	})
	public Result<?> get(@RequestParam String id) {
		return Result.data(dictService.getById(id));
	}

	/**
	 * 字典表设置
	 *
	 * @param dict Dict 对象
	 * @return Result
	 */
	@PreAuth
	@PostMapping("/set")
	@ApiOperation(value = "字典表设置", notes = "字典表设置,支持新增或修改")
	public Result<?> set(@Valid @RequestBody Dict dict) {
		return Result.condition(dictService.saveOrUpdate(dict));
	}

	/**
	 * 字典表编码检查
	 *
	 * @param code 编码
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/check")
	@ApiOperation(value = "字典表编码校验", notes = "字典表编码校验")
	public Result<?> check(@RequestParam String code,@RequestParam Long parentId) {
		Dict dict = new Dict();
		dict.setCode(code);
		dict.setParentId(parentId);
		if(dictService.checkCodeExist(dict)>0){
			return Result.fail("该编码已存在");
		}
		return Result.success("编码校验成功");
	}

	/**
	 * 字典表删除
	 *
	 * @param ids id字符串，根据,号分隔
	 * @return Result
	 */
	@PreAuth
	@PostMapping("/del")
	@ApiOperation(value = "字典表删除", notes = "字典表删除")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
	})
	public Result<?> del(@RequestParam String ids) {
		return Result.condition(dictService.delDict($.toLongList(ids)));
	}

	/**
	 * 查询code下的子属性列表
	 *
	 * @param code code
	 * @return 列表
	 */
	@PreAuth
	@GetMapping("/get-sub-list")
	@ApiOperation(value = "查询code下的子属性列表", notes = "查询code下的子属性列表")
	public Result<?> getDictSubList(String code) {
		return Result.data(dictService.getDictSubList(code));
	}

	/**
	 * 根据code和key获取字典value
	 *
	 * @param code    字典编码
	 * @param dictKey 字典键
	 * @return 字典值
	 */
	@PreAuth
	@GetMapping("/get-dict-value")
	@ApiOperation(value = "字典列表key查询", notes = "字典列表key查询")
	public Result<?> getDictValue(String code, String dictKey) {
		return Result.data(dictService.getValue(code, dictKey));
	}

	/**
	 * 查询字典树状态列表
	 *
	 * @return 字典树状结构
	 */
	@PreAuth
	@GetMapping("/tree")
	@ApiOperation(value = "字典树列表", notes = "字典树列表")
	public Result<?> tree() {
		return Result.data(ForestNodeMerger.merge(dictService.tree()));
	}

}

