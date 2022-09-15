package com.example.emos.api.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.dao.TbMeetingDao;
import com.example.emos.api.db.pojo.TbMeeting;
import com.example.emos.api.exception.EmosException;
import com.example.emos.api.service.MeetingService;
import com.example.emos.api.task.MeetingWorkflowTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
@Service
@Slf4j
public class MeetingServiceImpl implements MeetingService {
    @Autowired
    private TbMeetingDao tbMeetingDao;
    @Autowired
    private MeetingWorkflowTask meetingWorkflowTask;
    @Override
    public PageUtils searchOfflineMeetingByPage(HashMap param) {
        ArrayList<HashMap> list = tbMeetingDao.searchOfflineMeetingByPage(param);
        long count = tbMeetingDao.searchOfflineMeetingCount(param);
        int start = (Integer) param.get("start");
        int length = (Integer)param.get("length");
        //把meeting字段转换为JSON数组对象
        for(HashMap map:list){
            String meeting = (String) map.get("meeting");
            if(meeting!=null&&meeting.length()>0){
                map.replace("meeting", JSONUtil.parseArray(meeting));
            }
        }
        PageUtils pageUtils = new PageUtils(list,count,start,length);
        return pageUtils;
    }

    @Override
    public int insert(TbMeeting meeting) {
        int rows = tbMeetingDao.insert(meeting);
        if(rows!=1){
            throw new EmosException("会议添加失败");
        }
//        meetingWorkflowTask.startMeetingWorkflow(meeting.getUuid(),meeting.getCreatorId(),meeting.getTitle(),
//                meeting.getDate(),meeting.getStart()+":00","线下会议");//没有工作流账号密码无法完成调用任务
        return rows;
    }
    @Override
    public int insertOn(TbMeeting meeting) {
        int rows = tbMeetingDao.insertOn(meeting);
        if(rows!=1){
            throw new EmosException("会议添加失败");
        }
//        meetingWorkflowTask.startMeetingWorkflow(meeting.getUuid(),meeting.getCreatorId(),meeting.getTitle(),
//                meeting.getDate(),meeting.getStart()+":00","线下会议");//没有工作流账号密码无法完成调用任务
        return rows;
    }

    @Override
    public ArrayList<HashMap> searchOfflineMeetingInWeek(HashMap param) {
        ArrayList<HashMap> list = tbMeetingDao.searchOfflineMeetingInWeek(param);
        return list;
    }

    @Override
    public HashMap searchMeetingInfo(short status, long id) {
        //判断会议是否正在进行中
        HashMap map;
        map = tbMeetingDao.searchMeetingInfo(id);
        return map;
    }

    @Override
    public int deleteMeetingInfo(HashMap param) {
        //先查询会议详情,判断是否距离会议开始不足20分钟
        Long id = MapUtil.getLong(param,"id");
        HashMap meeting = tbMeetingDao.searchMeetingById(param);

        String date = MapUtil.getStr(meeting,"date");
        String start = MapUtil.getStr(meeting,"start");
        boolean isCreator = Boolean.parseBoolean(MapUtil.getStr(meeting,"isCreator"));
        DateTime dateTime = DateUtil.parse(date+" "+start);
        DateTime now = DateUtil.date();
        if(now.isAfterOrEquals(dateTime.offset(DateField.MINUTE,-20))){
            throw new EmosException("距离会议开始不到20分钟,无法删除");
        }
        //只能申请人进行删除
        if(!isCreator){
            throw new EmosException("只能申请人删除该会议");
        }
        int rows  = tbMeetingDao.deleteMeetingInfo(param);
        return rows;
    }

    @Override
    public PageUtils searchOnlineMeetingByPage(HashMap param) {
        ArrayList<HashMap> list = tbMeetingDao.searchOnlineMeetingByPage(param);
        long count = tbMeetingDao.searchOnlineMeetingCount(param);
        int start = (Integer) param.get("start");
        int length = (Integer)param.get("length");
        if(list.get(0)!=null){
        for (int i = 0; i < list.size(); i++) {
            if ((Integer) list.get(i).get("status") == 1) {
                list.get(i).put("status", "审批中");
            } else if ((int) list.get(i).get("status") == 2) {
                list.get(i).put("status", "已结束");
            } else if ((int) list.get(i).get("status") == 3) {
                list.get(i).put("status", "未开始");
            }
        }
        }
        //把meeting字段转换为JSON数组对象
        PageUtils pageUtils = new PageUtils(list,count,start,length);
        return pageUtils;
    }
}
