package com.ctsi.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.$;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.system.entity.Notice;
import com.ctsi.system.service.INoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 通知表 前端控制器
 * </p>
 *
 * @author wang xiao xiang
 */
@RestController
@AllArgsConstructor
@RequestMapping("/notice")
@Api(value = "通知表", tags = "通知表接口")
public class NoticeController extends BaseController {

    private final INoticeService noticeService;

    /**
     * 分页列表
     *
//     * @param page   分页信息
     * @param search 　搜索关键词
     * @return Result
     */
//    @PreAuth
    @GetMapping("/page")
    @ApiOperation(value = "通知表列表", notes = "分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
        @ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
        @ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
        @ApiImplicitParam(name = "startDate", required = true, value = "创建开始日期", paramType = "form"),
        @ApiImplicitParam(name = "endDate", required = true, value = "创建结束日期", paramType = "form"),
    })
    public Result<?> page(Search search) {
        Page page = new Page();
        return Result.data(noticeService.listPage(page, search));
    }

    /**
     * 通知表信息
     *
     * @param id Id
     * @return Result
     */
    @PreAuth
    @GetMapping("/get")
    @ApiOperation(value = "通知表信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
    })
    public Result<?> get(@RequestParam String id) {
		return Result.data(noticeService.getById(id));
	}

    /**
    * 通知表设置
    *
    * @param notice Notice 对象
    * @return Result
    */
    @PreAuth
    @PostMapping("/set")
    @ApiOperation(value = "通知表设置", notes = "通知表设置,支持新增或修改")
    public Result<?> set(@Valid @RequestBody Notice notice) {
		return Result.condition(noticeService.saveOrUpdate(notice));
	}

    /**
    * 通知表删除
    *
    * @param ids id字符串，根据,号分隔
    * @return Result
    */
    @PreAuth
    @PostMapping("/del")
    @ApiOperation(value = "通知表删除", notes = "通知表删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public Result<?> del(@RequestParam String ids) {
		return Result.condition(noticeService.removeByIds($.toLongList(ids)));
	}
}

