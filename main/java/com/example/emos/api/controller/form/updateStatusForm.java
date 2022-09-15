package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "审批任务")
@Data
public class updateStatusForm {
    private long id;

    @NotNull
    private String approval;
}
