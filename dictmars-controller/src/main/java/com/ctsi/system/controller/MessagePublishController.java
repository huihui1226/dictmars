package com.ctsi.system.controller;

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
import com.ctsi.system.service.IMessagePublishService;
import com.ctsi.system.entity.MessagePublish;
import com.ctsi.core.web.controller.BaseController;
import javax.validation.Valid;

/**
 * <p>
 * 站内消息发布 前端控制器
 * </p>
 *
 * @author chenfei
 * @date 2022-07-08 10:09:01
 */

@RestController
@AllArgsConstructor
@RequestMapping("/messagePublish")
@Api(value = "站内消息发布", tags = "站内消息发布接口")
public class MessagePublishController extends BaseController {

	private final IMessagePublishService messagePublishService;

    /**
     * 分页列表
     *
     * @param search 　搜索关键词
     * @return Result
     */
    @PreAuth
    @GetMapping("/page")
    @ApiOperation(value = "站内消息发布列表", notes = "分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
        @ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
        @ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
    })
    public Result<?> page(Search search) {
		return Result.data(messagePublishService.listPage(search));
    }


    /**
    * 站内消息发布设置
    *
    * @param messagepublish messagePublish 对象
    * @return Result
    */
    @PreAuth
    @PostMapping("/set")
    @ApiOperation(value = "站内消息发布设置", notes = "站内消息发布设置,支持新增或修改")
    public Result<?> set(@Valid @RequestBody MessagePublish messagepublish) {
		return Result.condition(messagePublishService.saveOrUpdate(messagepublish));
	}

    /**
    * 站内消息发布删除
    *
    * @param ids id字符串，根据,号分隔
    * @return Result
    */
    @PreAuth
    @PostMapping("/del")
    @ApiOperation(value = "站内消息发布删除", notes = "站内消息发布删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public Result<?> del(@RequestParam String ids) {
		return Result.condition(messagePublishService.removeByIds($.toLongList(ids)));
	}


    /**
     * 消息发布删除
     *
     * @param ids id字符串，根据,号分隔
     * @return Result
     */
    @PreAuth
    @PostMapping("/publish")
    @ApiOperation(value = "站内消息发布删除", notes = "站内消息发布删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public Result<?> publish(@RequestParam String ids) {
        return Result.condition(messagePublishService.publish($.toLongList(ids)));
    }


    /**
     * 站内消息发布信息
     *
     * @param receiverType 消息接收人类型
     * @return Result
     */
    @PreAuth
    @GetMapping("/receiver")
    @ApiOperation(value = "站内消息发布信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "receiverType", required = true, value = "receiverType", paramType = "form"),
    })
    public Result<?> receiver(@RequestParam String receiverType) {
        return Result.data(messagePublishService.getReceiverData(receiverType));
    }

}

