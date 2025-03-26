package com.ctsi.system.controller;

import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.entity.LoginUser;
import com.ctsi.core.common.util.$;
import com.ctsi.core.common.util.SecurityUtil;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.core.web.tree.ForestNodeMerger;
import com.ctsi.system.entity.Org;
import com.ctsi.system.service.IOrgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 系统组织机构表 前端控制器
 * </p>
 *
 * @author wang xiao xiang
 */
@RestController
@AllArgsConstructor
@RequestMapping("/org")
@Api(value = "系统组织机构表", tags = "系统组织机构表接口")
public class OrgController extends BaseController {

	private final IOrgService orgService;

	/**
	 * 分页列表
	 *
	 * @param search 　搜索关键词
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/page")
	@ApiOperation(value = "系统组织机构表列表", notes = "分页查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
			@ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
			@ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
	})
	public Result<?> page(Org org, Search search) {
		return Result.data(orgService.listPage(org, search));
	}

	/**
	 * 组织机构列表
	 *
	 * @param org    组织机构对象
	 * @param search 搜索对象
	 * @return 组织机构列表
	 */
	@PreAuth
	@GetMapping("/list")
	@ApiOperation(value = "组织机构列表", notes = "组织机构列表")
	public Result<?> list(HttpServletRequest request, Org org, Search search) {
		LoginUser user = SecurityUtil.getUsername(request);
		return Result.data(orgService.list(user, org, search));
	}

	/**
	 * 系统组织机构表信息
	 *
	 * @param id Id
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/get")
	@ApiOperation(value = "系统组织机构表信息", notes = "根据ID查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
	})
	public Result<?> get(@RequestParam String id) {
		return Result.data(orgService.getById(id));
	}

	/**
	 * 系统组织机构表设置
	 *
	 * @param org Org 对象
	 * @return Result
	 */
	@PreAuth
	@PostMapping("/set")
	@ApiOperation(value = "系统组织机构表设置", notes = "系统组织机构表设置,支持新增或修改")
	public Result<?> set(@Valid @RequestBody Org org) {
		if(orgService.checkCodeExist(org.getCode())>0){
			return Result.fail("该编码已存在");
		}
		return Result.condition(orgService.setOrg(org));
	}

	/**
	 * 系统组织机构编码检查
	 *
	 * @param code 编码
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/check")
	@ApiOperation(value = "系统组织机构编码校验", notes = "系统组织机构编码校验")
	public Result<?> check(@RequestParam String code) {
		if(orgService.checkCodeExist(code)>0){
			return Result.fail("该编码已存在");
		}
		return Result.success("编码校验成功");
	}

	/**
	 * 系统组织机构表删除
	 *
	 * @param ids id字符串，根据,号分隔
	 * @return Result
	 */
	@PreAuth
	@PostMapping("/del")
	@ApiOperation(value = "系统组织机构表删除", notes = "系统组织机构表删除")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
	})
	public Result<?> del(@RequestParam String ids) {
		List<Long>  idsList = $.toLongList(ids);
		if (orgService.getChildIdListWithSelfById(idsList.get(0)).size() > 1)
		{
			return Result.fail("存在下级机构,不允许删除");
		}
		if (orgService.checkOrgExistUser(idsList.get(0)))
		{
			return Result.fail("机构下存在用户,不允许删除");
		}
		return Result.condition(orgService.removeByIds(idsList));
	}

	/**
	 * 系统组织机构树
	 *
	 * @param search 查询对象
	 * @return 组织机构树
	 */
	@PreAuth
	@GetMapping("/tree")
	@ApiOperation(value = "系统组织机构树", notes = "系统组织机构树")
	public Result<?> tree(HttpServletRequest request, Search search) {
		LoginUser user = SecurityUtil.getUsername(request);
		return Result.data(ForestNodeMerger.merge(orgService.tree(user, search)));
	}
}

