package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询某个会议室周会议表")
public class SearchOfflineMeetingInWeekForm {
    @Schema(description = "日期")
    private String date;

    @Schema(description = "模式")
    private String mold;

    @Schema(description = "会议室名称")
    private String name;
}
