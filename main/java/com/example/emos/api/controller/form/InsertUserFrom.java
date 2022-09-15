package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Schema(description = "添加用户")
public class InsertUserFrom {
    @NotNull(message = "userId不能为空")
    @Min(value = 1,message = "userId不能小于1")
    @Schema(description ="用户ID")
    private Integer userId;

    @NotBlank(message = "username不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$",message = "username不符合")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$",message = "密码内容不符合")
    @Schema(description = "密码")
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,10}$",message = "name内容不正确")
    @Schema(description = "姓名")
    private String name;

    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^男$|^女$",message = "sex内容不正确")
    @Schema(description = "性别")
    private String sex;

    @NotEmpty(message = "角色不能为空")
    @Schema(description ="角色")
    private Integer[] role;

    @NotNull(message = "deptId不能为空")
    @Min(value = 1,message = "dept不能小于1")
    @Schema(description ="部门")
    private Integer deptId;

    @NotBlank(message = "电话不能为空")
    @Pattern(regexp = "^1\\d{10}$",message = "电话内容不符合")
    @Schema(description = "电话")
    private String tel;

    @NotBlank(message = "入职日期不能为空")//入职日期
    @Schema(description = "入职日期")
    private String hiredate;

    @NotBlank(message = "email不能为空")
    @Schema(description = "email")
    private String email;


}
