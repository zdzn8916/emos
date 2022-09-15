package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "查询线上会议")
public class searchOnlineMeetingByPageForm {
    @Schema(description = "日期")
    private String date;

    @Schema(description = "模式")
    private String mold;

    @NotNull(message = "页数不能为空")
    @Min(value = 1,message = "页数不能小于1")
    @Schema(description = "页数")
    private Integer page;

    @NotNull(message = "长度不能为空")
    @Min(value = 1,message = "长度必须在10-50之间")
    @Schema(description = "每页记录数")
    private Integer length;
}
