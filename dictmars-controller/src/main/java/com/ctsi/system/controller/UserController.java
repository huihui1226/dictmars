package com.ctsi.system.controller;

import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.entity.MarsDS;
import com.ctsi.core.common.util.$;
import com.ctsi.core.excel.annotation.ExportExcel;
import com.ctsi.core.web.annotation.EnableDS;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.log.annotation.Log;
import com.ctsi.system.dto.BatchReqDTO;
import com.ctsi.system.dto.UserPwdReqDTO;
import com.ctsi.system.dto.UserReqDTO;
import com.ctsi.system.entity.MarsDsReq;
import com.ctsi.system.entity.User;
import com.ctsi.system.poi.UserExcel;
import com.ctsi.system.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户表 前端控制器
 * </p>
 *
 * @author wang xiao xiang
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/user")
@Api(value = "系统用户表", tags = "系统用户表接口")
public class UserController extends BaseController {

	private final IUserService userService;

	/**
	 * 分页列表
	 *
	 * @param search 　搜索关键词
	 * @return Result
	 */
	@Log(value = "系统用户分页", exception = "系统用户分页请求异常")
	@PreAuth
	@GetMapping("/page")
	@ApiOperation(value = "系统用户分页", notes = "分页查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
			@ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
			@ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
	})
	public Result<?> page(@EnableDS MarsDsReq marsDsReq, User user, Search search) {
	    MarsDS marsDS = $.copy(marsDsReq, MarsDS.class);
		return Result.data(userService.listPage(marsDS, user, search));
	}

	/**
	 * 系统用户表信息
	 *
	 * @param id Id
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/get")
	@ApiOperation(value = "系统用户信息", notes = "根据ID查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
	})
	public Result<?> get(@RequestParam String id) {
		return Result.data(userService.detail($.toLong(id)));
	}

	/**
	 * 系统用户表设置
	 *
	 * @param user User 对象
	 * @return Result
	 */
	@PreAuth
	@PostMapping("/set")
	@ApiOperation(value = "系统用户表设置", notes = "系统用户表设置,支持新增或修改")
	public Result<?> set(@Valid @RequestBody UserReqDTO user) {
		return Result.condition(userService.setUser(user));
	}

	/**
	 * 系统用户表删除
	 *
	 * @param ids id字符串，根据,号分隔
	 * @return Result
	 */
	@PreAuth
	@PostMapping("/del")
	@ApiOperation(value = "系统用户表删除", notes = "系统用户表删除")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
	})
	public Result<?> del(@RequestParam String ids) {
		return Result.condition(userService.delUser($.toLongList(ids)));
	}

	/**
	 * 系统用户获取角色
	 *
	 * @param user 用户id
	 * @return 角色列表
	 */
	@PreAuth
	@GetMapping("/get-role")
	@ApiOperation(value = "系统用户获取角色", notes = "系统用户获取角色")
	public Result<?> getRole(User user) {
		return Result.data(userService.getRole(user));
	}


	/**
	 * 设置用户状态
	 *
	 * @param user 用户
	 * @return 用户状态
	 */
	@PreAuth
	@PostMapping("/set-status")
	@ApiOperation(value = "设置用户状态", notes = "设置用户状态")
	public Result<?> setStatus(@RequestBody User user) {
		return Result.condition(userService.setStatus(user));
	}


	/**
	 * 设置用户角色
	 *
	 * @param batchReqDTO 批量操作信息
	 * @return boolean
	 */
	@PreAuth
	@PostMapping("/set-role")
	@ApiOperation(value = "设置用户角色", notes = "设置用户角色")
	public Result<?> setRole(@RequestBody BatchReqDTO batchReqDTO) {
		return Result.condition(userService.setRole(batchReqDTO.getId(), batchReqDTO.getIds()));
	}

	@PreAuth
	@PostMapping("/set-password")
	@ApiOperation(value = "设置用户密码", notes = "设置用户密码")
	public Result<?> updatePassword(@RequestBody UserPwdReqDTO userPwdReq) {
		return Result.condition(userService.setPassword(userPwdReq));
	}

	@PreAuth
	@PostMapping("/set-avatar")
	@ApiOperation(value = "设置用户头像", notes = "设置用户头像")
	public Result<?> setAvatar(@RequestBody User user) {
		return Result.condition(userService.setAvatar(user));
	}

	/**
	 * 用户数据导出
	 *
	 * @return List<UserExcel> 用户数据列表
	 */
//	@PreAuth
	@PostMapping("/export")
	@ExportExcel(name = "用户管理", sheet = "userSheet", password = "123456")
	@ApiOperation(value = "用户导出", notes = "用户导出")
	public List<UserExcel> export() {
		return userService.list().stream().map(user -> $.copy(user, UserExcel.class))
				.collect(Collectors.toList());
	}

	@PreAuth
	@GetMapping("/flow-user-list")
	@ApiOperation(value = "用户列表-工作流", notes = "用户列表-工作流")
	public Result<?> flowUserList() {
		return Result.data(userService.getFlowUserList());
	}
}

