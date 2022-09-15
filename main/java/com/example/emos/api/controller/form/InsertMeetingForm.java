package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "添加会议表单")
public class InsertMeetingForm {
    @NotBlank(message = "title不能为空")
    @Schema(description = "主题")
    private String title;

    @NotBlank(message = "date不能为空")
    @Schema(description = "日期")
    private String date;

    @Schema(description = "会议地点")
    private String place;

    @NotBlank(message = "起始时间")
    @Schema(description = "起始时间")
    private String start;

    @NotBlank(message = "结束时间不能为空")
    @Schema(description = "结束时间")
    private String end;

    @NotNull(message = "会议类型不能为空")
    @Schema(description = "会议类型")
    private Byte type;

    @NotBlank(message = "参会人不能为空")
    @Schema(description = "参会人")
    private String members;

    @NotBlank(message = "内容不能为空")
    @Schema(description = "内容")
    private String desc;
}
