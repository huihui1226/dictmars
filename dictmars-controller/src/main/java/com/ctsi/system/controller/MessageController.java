package com.ctsi.system.controller;

import com.ctsi.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.$;
import com.ctsi.core.database.entity.Search;


import org.springframework.web.bind.annotation.RestController;
import com.ctsi.system.service.IMessageService;
import com.ctsi.system.entity.Message;
import com.ctsi.core.web.controller.BaseController;
import javax.validation.Valid;

/**
 * <p>
 * 站内消息 前端控制器
 * </p>
 *
 * @author chenfei
 * @date 2022-07-08 15:29:00
 */

@RestController
@AllArgsConstructor
@RequestMapping("/message")
@Api(value = "站内消息", tags = "站内消息接口")
public class MessageController extends BaseController {

	private final IMessageService messageService;

    /**
     * 分页列表
     *
     * @param search 　搜索关键词
     * @return Result
     */
    @PreAuth
    @GetMapping("/page")
    @ApiOperation(value = "站内消息列表", notes = "分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
        @ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
        @ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
    })
    public Result<?> page(Search search) {
		return Result.data(messageService.listPage(search));
    }

    /**
     * 站内消息信息
     *
     * @param id Id
     * @return Result
     */
    @PreAuth
    @GetMapping("/get")
    @ApiOperation(value = "站内消息信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
    })
    public Result<?> get(@RequestParam String id) {
		return Result.data(messageService.getById(id));
	}

    /**
     * 站内消息查询根据类型和接收人
     *
     * @param type 消息类型
     * @param receiveId 接收人id
     * @return Result
     */
    @PreAuth
    @GetMapping("/type")
    @ApiOperation(value = "站内消息信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", required = true, value = "type", paramType = "form"),
            @ApiImplicitParam(name = "receiveId", required = true, value = "receiveId", paramType = "form"),
    })
    public Result<?> type(@RequestParam String type,@RequestParam String receiveId) {
        return Result.data(messageService.typeList(type,receiveId));
    }

    /**
    * 站内消息设置
    *
    * @param message message 对象
    * @return Result
    */
    @PreAuth
    @PostMapping("/set")
    @ApiOperation(value = "站内消息设置", notes = "站内消息设置,支持新增或修改")
    public Result<?> set(@Valid @RequestBody Message message) {
        message.setPublishId(0L);
		return Result.condition(messageService.saveOrUpdate(message));
	}

    /**
    * 站内消息删除
    *
    * @param ids id字符串，根据,号分隔
    * @return Result
    */
    @PreAuth
    @PostMapping("/del")
    @ApiOperation(value = "站内消息删除", notes = "站内消息删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public Result<?> del(@RequestParam String ids) {
		return Result.condition(messageService.removeByIds($.toLongList(ids)));
	}

    @PreAuth
    @Log(value = "标记已读")
    @PostMapping("/mark")
    @ApiOperation(value = "标记已读")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "messageId", required = true, value = "我的消息id", paramType = "form")
    })
    public Result<?> mark(@RequestParam Long messageId) {
        Message message = new Message();
        message.setId(messageId);
        message.setMark("1");
        this.messageService.mark(message);
        return Result.condition(Boolean.TRUE);
    }
}

