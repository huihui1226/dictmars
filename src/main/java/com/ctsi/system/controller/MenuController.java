package com.ctsi.system.controller;

import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.entity.LoginUser;
import com.ctsi.core.common.util.$;
import com.ctsi.core.common.util.SecurityUtil;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.core.web.tree.ForestNodeMerger;
import com.ctsi.system.entity.Menu;
import com.ctsi.system.service.IMenuService;
import com.ctsi.system.vo.MenuTreeResVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统菜单表 前端控制器
 * </p>
 *
 * @author wang xiao xiang
 */
@RestController
@AllArgsConstructor
@RequestMapping("/menu")
@Api(value = "系统菜单表", tags = "系统菜单表接口")
public class MenuController extends BaseController {

	private final IMenuService menuService;


	/**
	 * 查询菜单树
	 *
	 * @param menu 查询条件
	 * @return 菜单树列表
	 */
	@PreAuth
	@GetMapping("/list")
	@ApiOperation(value = "菜单树", notes = "菜单树")
	public Result<?> list(Menu menu) {
		return Result.data(ForestNodeMerger.merge(menuService.listAll(menu)));
	}

	/**
	 * 应用菜单树树
	 *
	 * @param menu 应用名称
	 * @return 菜单树列表
	 * @see MenuTreeResVO
	 */
	@PreAuth
	@GetMapping("/tree")
	@ApiOperation(value = "应用菜单树树", notes = "应用菜单树树")
	public Result<?> tree(Menu menu) {
		return Result.data(ForestNodeMerger.merge(menuService.tree(menu)));
	}


	/**
	 * 授权菜单树
	 *
	 * @param menu 查询条件，根据应用
	 * @return 授权菜单树列表
	 */
	@PreAuth
	@GetMapping("/grant-tree")
	@ApiOperation(value = "授权菜单树", notes = "授权菜单树")
	public Result<?> grantTree(HttpServletRequest request, Menu menu) {
		LoginUser user = SecurityUtil.getUsername(request);
		// 拼装新的返回格式，后续多种应用场景下，再封装新的对象
		Map<String, Object> mapData = new HashMap<>(2);
		List<MenuTreeResVO> menuTreeResList = menuService.grantTree(user, menu);
		// 组织所有ids串
		List<Long> ids = menuTreeResList.stream().map(MenuTreeResVO::getId).collect(Collectors.toList());
		mapData.put("ids", ids);
		mapData.put("tree", ForestNodeMerger.merge(menuTreeResList));
		return Result.data(mapData);
	}


	/**
	 * 切换菜单树
	 *
	 * @param menu 应用信息
	 * @return 菜单树列表
	 */
	@PreAuth
	@PostMapping("/change-menu")
	@ApiOperation(value = "切换菜单树", notes = "切换菜单树")
	public Result<?> exchangeMenu(HttpServletRequest request, @RequestBody Menu menu) {
		LoginUser user = SecurityUtil.getUsername(request);
		return Result.data(menuService.getLoginMenusAntDesign(user.getId(), menu.getApplication(),user.getUserType()));
	}

	/**
	 * 系统菜单表信息
	 *
	 * @param id Id
	 * @return Result
	 */
	@PreAuth
	@GetMapping("/get")
	@ApiOperation(value = "系统菜单表信息", notes = "根据ID查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
	})
	public Result<?> get(@RequestParam String id) {
		return Result.data(menuService.getById(id));
	}

	/**
	 * 系统菜单表设置
	 *
	 * @param menu Menu 对象
	 * @return Result
	 */
	@PreAuth
	@PostMapping("/set")
	@ApiOperation(value = "系统菜单表设置", notes = "系统菜单表设置,支持新增或修改")
	public Result<?> set(@Valid @RequestBody Menu menu) {
		return Result.condition(menuService.setMenu(menu));
	}

	/**
	 * 系统菜单表删除
	 *
	 * @param ids id字符串，根据,号分隔
	 * @return Result
	 */
	@PreAuth
	@PostMapping("/del")
	@ApiOperation(value = "系统菜单表删除", notes = "系统菜单表删除")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
	})
	public Result<?> del(@RequestParam String ids) {
		return Result.condition(menuService.delMenu($.toLongList(ids)));
	}
}

