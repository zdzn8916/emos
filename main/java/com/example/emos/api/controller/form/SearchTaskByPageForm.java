package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询任务分页列表表单")
public class SearchTaskByPageForm {
    private String creatorName;

    private String type;

    private String status;

    private Integer page;

    private Integer length;
}
