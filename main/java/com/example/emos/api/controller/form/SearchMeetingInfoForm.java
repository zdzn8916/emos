package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询会议信息表单")
public class SearchMeetingInfoForm {
    @Schema(description = "会议id")
    private Long id;

    @Schema(description = "状态")
    private Short status;
}
