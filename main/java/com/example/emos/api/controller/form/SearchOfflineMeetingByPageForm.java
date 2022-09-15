package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "查询线下会议分页表单")
public class SearchOfflineMeetingByPageForm {
            @Schema(description ="日期")
            private String date;

            @NotNull(message = "mold不能空")
            @Pattern(regexp = "^全部会议$|^我的会议$", message = "mold内容错误")
            @Schema(description ="模式")
            private String mold;

            @NotNull( message = "page不能为空")
            @Min(value = 1,message = "page不能小于1")
            @Schema(description ="页数")
            private Integer page;

            @NotNull(message = "length不能空")
            @Range(min=10,max=50,message = "lenth必须在10-50之间")
            @Schema(description = "每页记录数")
            private Integer length;

}
