package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.SearchApprovalContentForm;
import com.example.emos.api.controller.form.SearchTaskByPageForm;
import com.example.emos.api.controller.form.updateStatusForm;
import com.example.emos.api.service.ApprovalService;
import com.example.emos.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/approval")
@Tag(name="approvalController",description = "任务审批Web接口")
public class approvalController {
    @Autowired
    private ApprovalService approvalService;
    @Autowired
    private UserService userService;
    @PostMapping("/searchTaskByPage")
    @Operation(summary = "查询任务分页列表")
    @SaCheckPermission(value = {"WORKFLOW:APPROVAL","FILE:ARCHIVE","ROOT"},mode = SaMode.OR)
    public R searchTaskByPage(@Valid @RequestBody SearchTaskByPageForm form){
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        int status;
        if(form.getStatus().equals("待审批")){
            int userId = StpUtil.getLoginIdAsInt();
            int start = (form.getPage()-1)* form.getLength();
            param.put("start",start);
            param.put("userId",userId);
            param.put("role",userService.searchUserRoles(userId));
            PageUtils pageUtils = approvalService.searchApprovalByPage(param);
            return R.ok().put("page",pageUtils);
        }else{
            int userId = StpUtil.getLoginIdAsInt();
            int start = (form.getPage()-1)* form.getLength();
            param.put("start",start);
            param.put("userId",userId);
            param.put("role",userService.searchUserRoles(userId));
            PageUtils pageUtils = approvalService.searchApprovalByPage1(param);
            return R.ok().put("page",pageUtils);
        }


    }
    @PostMapping("/searchApprovalContent")
    @Operation(summary = "查询任务详情")
    @SaCheckPermission(value = {"ROOT","WORKFLOW:APPROVAL","FILE:ARCHIVE"},mode = SaMode.OR)
    public R searchApprovalContent(@Valid @RequestBody SearchApprovalContentForm form){
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        int userId = StpUtil.getLoginIdAsInt();
        param.put("userId",userId);
        param.put("role",userService.searchUserRoles(userId));
        HashMap content = approvalService.searchApprovalContent(param);
        return R.ok().put("content",content);

    }
    @PostMapping("/updateStatus")
    @Operation(summary = "审批任务")
    @SaCheckPermission(value = {"ROOT","WORKFLOW:APPROVAL","FILE:ARCHIVE"},mode = SaMode.OR)
    public R updateStatus(@Valid @RequestBody updateStatusForm form){
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);

        int rows = approvalService.updateStatus(param);
        if(rows == 1){
            return R.ok().put("rows",rows);
        }
        return R.error("审批失败");
    }
}
