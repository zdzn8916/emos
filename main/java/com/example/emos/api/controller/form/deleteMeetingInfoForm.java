package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除会议")
public class deleteMeetingInfoForm {
    @Schema(description = "会议id")
    private long id;

    @Schema(description = "删除的原因")
    private String reason;

}
