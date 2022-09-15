package com.example.emos.api.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.*;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.*;
import com.example.emos.api.db.pojo.TbMeeting;
import com.example.emos.api.service.MeetingService;
import com.example.emos.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


@RestController
@RequestMapping("/meeting")
@Tag(name="SearchOfflineMeetingByPageController",description = "会议web接口")
public class SearchOfflineMeetingByPageController {
    @Autowired
    private MeetingService meetingService;

    @PostMapping("/SearchOfflineMeetingByPage")
    @Operation(summary = "查询线下会议web接口")
    @SaCheckLogin
    public R SearchOfflineMeetingByPage(@Valid @RequestBody SearchOfflineMeetingByPageForm from){
        int page = from.getPage();
        int length = from.getLength();
        int start = (page-1)*length;
        HashMap param = new HashMap(){{
            put("date",from.getDate());
            put("mold",from.getMold());
            put("userId", StpUtil.getLoginId());
            put("start",start);
            put("length",length);
        }};
        PageUtils pageUtils = meetingService.searchOfflineMeetingByPage(param);
        return R.ok().put("page",pageUtils);
    }
    @PostMapping("/insert")
    @Operation(summary = "添加会议")
    @SaCheckLogin
    public R insert(@Valid @RequestBody InsertMeetingForm form){
        DateTime start = DateUtil.parse(form.getDate()+" "+form.getStart());
        DateTime end = DateUtil.parse(form.getDate()+" "+form.getEnd());
        if(start.isAfterOrEquals(end)){
            return R.error("结束时间必须大于开始时间");
        }else if(new DateTime().isAfterOrEquals(start)){
            return R.error("开始时间要晚于当前时间");
        }
        TbMeeting tbMeeting = JSONUtil.parse(form).toBean(TbMeeting.class);
        tbMeeting.setUuid(UUID.randomUUID(true).toString());
        tbMeeting.setCreatorId(StpUtil.getLoginIdAsInt());
        tbMeeting.setStatus((short)1);
        if(form.getPlace()==null||form.getPlace()==""){
            int rows = meetingService.insertOn(tbMeeting);
            return R.ok().put("rows",rows);
        }
        int rows = meetingService.insert(tbMeeting);
        return R.ok().put("rows",rows);
    }
    @PostMapping("/searchOfflineMeetingInfoInWeek")
    @Operation(summary = "查询某个会议室周会议表")
    @SaCheckLogin
    public R searchOfflineMeetingInfoInWeek(@Valid @RequestBody SearchOfflineMeetingInWeekForm form){
        String date = form.getDate();
        DateTime startDate,endDate;
        if(date!=null&&date.length()>0){
            startDate=DateUtil.parseDate(date);
            endDate = startDate.offsetNew(DateField.DAY_OF_WEEK,6);
        }else {
            startDate = DateUtil.beginOfWeek(new Date());
            endDate = startDate.offsetNew(DateField.DAY_OF_WEEK,6);
        }
        HashMap param = new HashMap(){{
            put("place",form.getName());
            put("startDate",startDate.toDateStr());
            put("endDate",endDate.toDateStr());
            put("mold",form.getMold());
            put("userId",StpUtil.getLoginIdAsLong());
        }};
        ArrayList<HashMap> list = meetingService.searchOfflineMeetingInWeek(param);
        ArrayList days = new ArrayList();
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_WEEK);
        range.forEach(one->{
            JSONObject json = new JSONObject();
            json.set("date",one.toString("MM/dd"));
            json.set("day",one.dayOfWeekEnum().toChinese("周"));
            days.add(json);
        });
        return R.ok().put("list",list).put("days",days);
    }
    @PostMapping("/searchMeetingInfo")
    @Operation(summary = "查询某个会议室的信息")
    @SaCheckLogin
    public R searchOfflineMeetingInfoInWeek(@Valid @RequestBody SearchMeetingInfoForm form){
        HashMap hashMap = meetingService.searchMeetingInfo(form.getStatus(), form.getId());
        return R.ok(hashMap);
    }
    @PostMapping("/deleteMeetingInfo")
    @Operation(summary = "删除会议信息")
    @SaCheckLogin
    public R deleteMeetingInfo(@Valid @RequestBody deleteMeetingInfoForm form){

        HashMap hashMap = JSONUtil.parse(form).toBean(HashMap.class);
        hashMap.put("creatorId",StpUtil.getLoginIdAsLong());
        hashMap.put("userId",StpUtil.getLoginIdAsLong());
        int rows = meetingService.deleteMeetingInfo(hashMap);
        if(rows==1){
            return R.ok().put("rows",rows);
        }else {
            return R.error("您无法删除他人创建的会议");
        }
    }
    @PostMapping("/SearchOnlineMeetingByPage")
    @Operation(summary = "查询线上会议web接口")
    @SaCheckLogin
    public R SearchOnlineMeetingByPage(@Valid @RequestBody searchOnlineMeetingByPageForm from){
        int page = from.getPage();
        int length = from.getLength();
        int start = (page-1)*length;
        HashMap param = new HashMap(){{
            put("date",from.getDate());
            put("mold",from.getMold());
            put("userId", StpUtil.getLoginId());
            put("start",start);
            put("length",length);
        }};
        PageUtils pageUtils = meetingService.searchOnlineMeetingByPage(param);
        if(pageUtils.getList().get(0)==null){
            return R.ok();
        }
        return R.ok().put("page",pageUtils);
    }
}
