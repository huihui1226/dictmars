package com.ctsi.system.controller;

import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.entity.LoginUser;
import com.ctsi.core.common.util.$;
import com.ctsi.core.common.util.SecurityUtil;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.system.dto.BatchReqDTO;
import com.ctsi.system.dto.RoleDsReqDTO;
import com.ctsi.system.entity.Role;
import com.ctsi.system.service.IRoleMenuService;
import com.ctsi.system.service.IRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>
 * 系统角色表 前端控制器
 * </p>
 *
 * @author wang xiao xiang
 */
@RestController
@AllArgsConstructor
@RequestMapping("/role")
@Api(value = "系统角色表", tags = "系统角色表接口")
public class RoleController extends BaseController {

	private final IRoleService roleService;
	private final IRoleMenuService roleMenuService;

	/**
	 * 分页列表
	 *
	 * @param search 搜索关键词
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/page")
	@ApiOperation(value = "系统角色表列表", notes = "分页查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
			@ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
			@ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
	})
	public Result<?> page(Search search) {
		return Result.data(roleService.listPage(search));
	}

	/**
	 * 系统角色表信息
	 *
	 * @param id Id
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/get")
	@ApiOperation(value = "系统角色表信息", notes = "根据ID查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
	})
	public Result<?> get(@RequestParam String id) {
		return Result.data(roleService.getById(id));
	}

	/**
	 * 系统角色表设置
	 *
	 * @param role Role 对象
	 * @return Result
	 */
	@PreAuth
	@PostMapping("/set")

	@ApiOperation(value = "系统角色表设置", notes = "系统角色表设置,支持新增或修改")
	public Result<?> set(@Valid @RequestBody Role role) {
		if(roleService.checkCodeExist(role.getCode())>0){
			return Result.fail("该编码已存在");
		}
		return Result.condition(roleService.saveOrUpdate(role));
	}

	/**
	 * 系统角色表编码检查
	 *
	 * @param code 编码
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/check")
	@ApiOperation(value = "系统角色表编码校验", notes = "系统角色表编码校验")
	public Result<?> check(@RequestParam String code) {
		if(roleService.checkCodeExist(code)>0){
			return Result.fail("该编码已存在");
		}
		return Result.success("编码校验成功");
	}

	/**
	 * 系统角色表删除
	 *
	 * @param ids id字符串，根据,号分隔
	 * @return Result
	 */
	@PreAuth
	@PostMapping("/del")
	@ApiOperation(value = "系统角色表删除", notes = "系统角色表删除")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
	})
	public Result<?> del(@RequestParam String ids) {
		return Result.condition(roleService.removeByIds($.toLongList(ids)));
	}

	/**
	 * 角色菜单
	 *
	 * @param role 角色
	 * @return 角色菜单树
	 */
	@PreAuth
	@GetMapping("/role-menu")
	@ApiOperation(value = "角色菜单", notes = "角色菜单")
	public Result<?> roleMenu(Role role) {
		return Result.data(roleMenuService.getRoleMenuIdList($.toLongList($.toStr(role.getId()))));
	}

	/**
	 * 设置授权菜单
	 *
	 * @param batchReqDTO 批量请求DTO
	 * @return boolean
	 */
	@PreAuth
	@PostMapping("/set-grant-menu")
	@ApiOperation(value = "设置授权菜单", notes = "设置授权菜单")
	public Result<?> grantMenu(@RequestBody BatchReqDTO batchReqDTO) {
		return Result.data(roleMenuService.grantMenu(batchReqDTO.getId(), batchReqDTO.getIds()));
	}

}

