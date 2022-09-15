package com.example.emos.api.service.impl;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.dao.ApprovalDao;
import com.example.emos.api.service.ApprovalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
@Service
@Slf4j
public class ApprovalServiceImpl implements ApprovalService {
    @Autowired
    private ApprovalDao approvalDao;
    @Override
    public PageUtils searchApprovalByPage(HashMap param) {
        if(param.containsKey("type")){
        if (param.get("type").equals("会议申请")) {
            param.put("type", 2);
        } else {
            param.put("type", 1);
        }
    }

        ArrayList<HashMap> list = approvalDao.searchApprovalByPage(param);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).put("status","审批中");
        }
        long count = approvalDao.searchByCount(param);
        int start = (Integer) param.get("start");
        int length = (Integer)param.get("length");

        PageUtils pageUtils = new PageUtils(list,count,start,length);
        return pageUtils;
    }

    @Override
    public HashMap searchApprovalContent(HashMap param) {
        HashMap content = approvalDao.searchApprovalContent(param);
        return content;
    }

    @Override
    public int updateStatus(HashMap param) {
        if(param.get("approval").equals("同意")){
            param.put("approval",3);
        }else {
            param.put("approval",2);
        }
        int rows = approvalDao.updateStatus(param);
        return rows;
    }

    @Override
    public PageUtils searchApprovalByPage1(HashMap param) {
        if(param.containsKey("type")){
            if (param.get("type").equals("会议申请")) {
                param.put("type", 2);
            } else {
                param.put("type", 1);
            }
        }

        ArrayList<HashMap> list = approvalDao.searchApprovalByPage1(param);
        for (int i = 0; i < list.size(); i++) {
            if((int)list.get(i).get("status")==2){
                list.get(i).put("status","已结束");
            }else {
                list.get(i).put("status","未开始");
            }

        }
        long count = approvalDao.searchByCount(param);
        int start = (Integer) param.get("start");
        int length = (Integer)param.get("length");

        PageUtils pageUtils = new PageUtils(list,count,start,length);
        return pageUtils;
    }
}
