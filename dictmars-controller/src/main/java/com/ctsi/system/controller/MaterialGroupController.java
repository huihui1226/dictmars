package com.ctsi.system.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.$;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.system.entity.MaterialGroup;
import com.ctsi.system.service.IMaterialGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 素材组表 前端控制器
 * </p>
 *
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/19 10:25
 */
@RestController
@AllArgsConstructor
@RequestMapping("/material-group")
@Api(value = "素材组表", tags = "素材组表接口")
public class MaterialGroupController extends BaseController {

    private final IMaterialGroupService materialGroupService;

    /**
     * 分页列表
     *
     * @param search 　搜索关键词
     * @return Result
     */
    @PreAuth
    @GetMapping("/page")
    @ApiOperation(value = "素材组表列表", notes = "分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
            @ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
            @ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
            @ApiImplicitParam(name = "startDate", required = true, value = "创建开始日期", paramType = "form"),
            @ApiImplicitParam(name = "endDate", required = true, value = "创建结束日期", paramType = "form"),
    })
    public Result<?> page(Search search) {
        return Result.data(materialGroupService.listPage(search));
    }

    /**
     * 素材组列表
     *
     * @param type 图片类型
     * @return Result
     */
    @PreAuth
    @GetMapping("/list")
    @ApiOperation(value = "素材组表列表", notes = "根据type查询")
    public Result<?> list(@RequestParam String type) {
        return Result.data(materialGroupService.listByType(type));
    }

    /**
     * 素材组表信息
     *
     * @param id Id
     * @return Result
     */
    @PreAuth
    @GetMapping("/get")
    @ApiOperation(value = "素材组表信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
    })
    public Result<?> get(@RequestParam String id) {
        return Result.data(materialGroupService.getById(id));
    }

    /**
     * 素材组表设置
     *
     * @param materialGroup MaterialGroup 对象
     * @return Result
     */
    @PreAuth
    @PostMapping("/set")
    @ApiOperation(value = "素材组表设置", notes = "素材组表设置,支持新增或修改")
    public Result<?> set(@Valid @RequestBody MaterialGroup materialGroup) {
        return Result.condition(materialGroupService.saveOrUpdate(materialGroup));
    }

    /**
     * 素材组表删除
     *
     * @param ids id字符串，根据,号分隔
     * @return Result
     */
    @PreAuth
    @PostMapping("/del")
    @ApiOperation(value = "素材组表删除", notes = "素材组表删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public Result<?> del(@RequestParam String ids) {
        return Result.condition(materialGroupService.removeByIds($.toLongList(ids)));
    }

    /**
     * 素材组更名
     *
     * @param id   主键ID
     * @param name 名称
     * @return 更名状态
     */
    @PreAuth
    @PostMapping("/rename")
    @ApiOperation(value = "素材组更名", notes = "素材组更名")
    public Result<?> rename(@RequestParam String id, @RequestParam String name) {
        return Result.condition(materialGroupService.update(Wrappers.<MaterialGroup>lambdaUpdate()
                .set(MaterialGroup::getName, name)
                .eq(MaterialGroup::getId, id)));
    }
}
