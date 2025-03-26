package com.ctsi.system.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ctsi.common.util.FileTypes;
import com.ctsi.common.util.Search;
import com.ctsi.core.auth.annotation.PreAuth;
import com.ctsi.core.common.api.Result;
import com.ctsi.core.common.util.$;
import com.ctsi.core.web.controller.BaseController;
import com.ctsi.system.entity.Material;
import com.ctsi.system.service.IMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@RestController
@AllArgsConstructor
@RequestMapping("/material")
@Api(value = "素材", tags = "素材表接口")
public class MaterialController extends BaseController {

    private final IMaterialService materialService;

//    private final Preview preview;

    /**
     * 分页列表
     *
     * @param search 　搜索关键词
     * @return Result
     */
    @PreAuth
    @GetMapping("/page")
    @ApiOperation(value = "素材表列表", notes = "分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "form"),
            @ApiImplicitParam(name = "size", required = true, value = "每页显示数据", paramType = "form"),
            @ApiImplicitParam(name = "keyword", required = true, value = "模糊查询关键词", paramType = "form"),
    })
    public Result<?> page(Material material, Search search) {
        return Result.data(materialService.listPage(material, search));
    }

    /**
     * 素材表信息
     *
     * @param id Id
     * @return Result
     */
    @PreAuth
    @GetMapping("/get")
    @ApiOperation(value = "素材表信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
    })
    public Result<?> get(@RequestParam String id) {
        return Result.data(materialService.getById(id));
    }

    /**
     * 素材表设置
     *
     * @param material Material 对象
     * @return Result
     */
    @PreAuth
    @PostMapping("/set")
    @ApiOperation(value = "素材表设置", notes = "素材表设置,支持新增或修改")
    public Result<?> set(@Valid @RequestBody Material material) {
        return Result.condition(materialService.saveOrUpdate(material));
    }

    /**
     * 素材表删除
     *
     * @param ids id字符串，根据,号分隔
     * @return Result
     */
    @PreAuth
    @PostMapping("/del")
    @ApiOperation(value = "素材表删除", notes = "素材表删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public Result<?> del(@RequestParam String ids) {
        return Result.condition(materialService.removeByIds($.toLongList(ids)));
    }

    /**
     * 资源上传
     *
     * @param groupId 分组ID
     * @param file    资源
     * @return Result
     */
    @ApiOperation(value = "素材上传", notes = "素材上传")
    @PostMapping("/upload")
    public Result<?> upload(@RequestParam(required = false) String groupId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("上传文件为空");
        }
        MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
        String mimeType = fileTypeMap.getContentType(file.getOriginalFilename());
        String[] mimeTypeBlackList = {
                "text/html",
                "text/javascript",
                "application/javascript",
                "application/ecmascript",
                "text/xml",
                "application/xml"
        };
        for (String blackMimeType : mimeTypeBlackList) {
            // 用contains是为了防止text/html;charset=UTF-8绕过
            if (mimeType.toUpperCase(Locale.ENGLISH).contains(blackMimeType)) {
                return Result.fail("上传文件方式不合法");
            }
        }
        return Result.data(materialService.upload(groupId, null, file, Boolean.FALSE));
    }

    /**
     * 缩略图上传
     *
     * @param groupId    分组ID
     * @param materialId 素材ID
     * @param file       文件
     * @return 上传结果
     */
    @ApiOperation(value = "素材缩略图上传", notes = "素材缩略图上传")
    @PostMapping("/upload-thumb")
    public Result<?> uploadThumbnail(@RequestParam(required = false) String groupId, @RequestParam String materialId, @RequestParam("file") MultipartFile file) {
        boolean checkFlag = false;
        if (FileTypes.checkType(FileTypes.FileTypeName.JPG, file)) {
            checkFlag = true;
        } else if (FileTypes.checkType(FileTypes.FileTypeName.PNG, file)) {
            checkFlag = true;
        } else if (FileTypes.checkType(FileTypes.FileTypeName.JPG, file)) {
            checkFlag = true;
        }
        if (!checkFlag) {
            return Result.fail("上传文件不符合要求");
        }
        return Result.data(materialService.upload(groupId, materialId, file, Boolean.TRUE));
    }

    /**
     * 素材更名
     *
     * @param id   主键ID
     * @param name 名称
     * @return 更名状态
     */
    @PreAuth
    @PostMapping("/rename")
    @ApiOperation(value = "素材更名", notes = "素材更名")
    public Result<?> rename(@RequestParam String id, @RequestParam String name) {
        return Result.condition(materialService.update(Wrappers.<Material>lambdaUpdate()
                .set(Material::getName, name)
                .eq(Material::getId, id)));
    }

    /**
     * 素材移动到指定组
     *
     * @param ids     数组列表
     * @param groupId 分组ID
     * @return boolean
     */
    @PreAuth
    @PostMapping("/move-group")
    @ApiOperation(value = "素材移动到指定组", notes = "素材移动到指定组")
    public Result<?> moveGroup(@RequestParam List<String> ids, @RequestParam String groupId) {
        return Result.condition(materialService.moveGroup(ids, groupId));
    }

    /**
     * 素材预览
     *
     * @param url
     * @return boolean
     */
//    @PreAuth
//    @GetMapping("/pre")
//    @ApiOperation(value = "文件预览", notes = "文件预览")
//    public Result<?> preview(@RequestParam String url) throws JSONException {
//        JSONObject param  = new JSONObject();
//        try {
//            //获取ACCESS_TOKEN
//            String accessToken = preview.postToken(param);
//            //获取素材URL
//            param.put("fileUri",url);
//            param.put("access_token",accessToken);
//            return Result.data(preview.getPreviewUrl(param));
//        } catch ( JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}

