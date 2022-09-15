package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data

public class SearchApprovalContentForm {
    private String id;

    private String type;

    private String status;
}
