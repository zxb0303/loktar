package com.loktar.mapper.qywx;

import com.loktar.domain.qywx.QywxMenu;
import java.util.List;

public interface QywxMenuMapper {
    int deleteByPrimaryKey(Integer menuId);

    int insert(QywxMenu row);

    QywxMenu selectByPrimaryKey(Integer menuId);

    List<QywxMenu> selectAll();

    int updateByPrimaryKey(QywxMenu row);

    List<QywxMenu> selectAllByAgentId(String agentId);
}