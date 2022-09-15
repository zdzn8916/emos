package com.example.emos.api.db.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface ApprovalDao {
    public ArrayList<HashMap> searchApprovalByPage(HashMap param);

    public long searchByCount(HashMap param);

    public HashMap searchApprovalContent(HashMap param);

    public int updateStatus(HashMap param);

    public ArrayList<HashMap> searchApprovalByPage1(HashMap param);
}
