package com.ctsi.system.poi;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用户导出对象
 *
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/9 16:22
 */
@Data
@ColumnWidth(50)
public class UserExcel {

    /**
     * 账号
     */
    @ExcelProperty("账号")
    private String account;
    /**
     * 昵称
     */
    @ExcelProperty("昵称")
    private String nickName;
    /**
     * 姓名
     */
    @ExcelProperty("姓名")
    private String name;
    /**
     * 头像
     */
    @ExcelProperty("头像")
    private String avatar;
    /**
     * 生日
     */
    @ExcelProperty("生日")
    private LocalDate birthday;
    /**
     * 性别(字典 1男 2女 3未知)
     */
    @ExcelProperty("性别")
    private Integer sex;
    /**
     * 邮箱
     */
    @ExcelProperty("邮箱")
    private String email;
    /**
     * 手机
     */
    @ExcelProperty("手机")
    private String phone;
    /**
     * 电话
     */
    @ExcelProperty("电话")
    private String tel;
}
