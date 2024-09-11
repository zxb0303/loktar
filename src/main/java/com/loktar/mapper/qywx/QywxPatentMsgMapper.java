package com.loktar.mapper.qywx;

import com.loktar.domain.qywx.QywxPatentMsg;
import java.util.List;

public interface QywxPatentMsgMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(QywxPatentMsg row);

    QywxPatentMsg selectByPrimaryKey(Integer id);

    List<QywxPatentMsg> selectAll();

    int updateByPrimaryKey(QywxPatentMsg row);

    List<QywxPatentMsg> getQywxPatentMsgsByStatus(String status);

    int updateQywxPatentStatusById(Integer id, String status);

    String getMobileStrByApplyName(String applyName);
}