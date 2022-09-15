package com.example.emos.api.task;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.db.dao.TbMeetingDao;
import com.example.emos.api.db.dao.TbUserDao;
import com.example.emos.api.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class MeetingWorkflowTask {
    @Autowired
    private TbUserDao tbUserDao;
    @Autowired
    private TbMeetingDao tbMeetingDao;
    @Value("${emos.recieveNotify}")
    private String recieveNotify;
    @Value("${emos.code}")
    private String code;
    @Value("${emos.tcode}")
    private String tcode;
    @Value("${workflow.url}")
    private String workflow;

    @Async("AsyncTaskExecutor")
    public void startMeetingWorkflow(String uuid,int creatorId,String title,String date,String start,String meetingType){
        HashMap info = tbUserDao.searchUserInfo(creatorId);

        JSONObject json = new JSONObject();
        json.set("url", recieveNotify);
        json.set("uuid" , uuid);
        json.set("creatorId",creatorId);
        json.set("creatorName",info.get("name").toString());
        json.set("code" , code);
        json.set("tcode", tcode);
        json.set("title",title);
        json.set("date",date);
        json.set("start",start);
        json.set("meetingType",meetingType);

        String[] roles = info.get("roles").toString().split(", ");

        //判断用户是否为总经理,总经理是不需要审批滴
        if(!ArrayUtil.contains(roles,"总经理")){
            //查询部门经理的userId
            Integer managerId = tbUserDao.searchDeptManagerId(creatorId);
            json.set("managerId",managerId);

            //查询总经理Id
            Integer gmId = tbUserDao.searchGmId();
            json.set("gmId",gmId);

            //查询参会人是否为同一部门
            boolean bool = tbMeetingDao.searchMeetingMembersInSameDept(uuid);
            json.set("sameDept",bool);
        }
        String url = workflow + "/workflow/startMeetingProcess";
        HttpResponse resp = HttpRequest.post(url).header("Content-Type", "application/json").body(json.toString()).execute();
        if(resp.getStatus()==200){
            json = JSONUtil.parseObj(resp.body());
            String instanceId = json.getStr("instanceId");
            HashMap param = new HashMap();
            param.put("uuid",uuid);
            param.put("instanceId",instanceId);
            //更新会议记录的instanceId字段
            int row = tbMeetingDao.updateMeetingInstanceId(param);
            if(row!=1){
                throw new EmosException("保存会议工作流id失败");
            }
        }else {
            log.error(resp.body());
        }
    }
}
