package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;

import java.util.ArrayList;
import java.util.HashMap;

public interface ApprovalService {
    public PageUtils searchApprovalByPage(HashMap param);

    public HashMap searchApprovalContent(HashMap param);

    public int updateStatus(HashMap param);

    public PageUtils searchApprovalByPage1(HashMap param);
}
