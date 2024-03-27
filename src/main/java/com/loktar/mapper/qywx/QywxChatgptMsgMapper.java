package com.loktar.mapper.qywx;

import com.loktar.domain.qywx.QywxChatgptMsg;
import java.util.List;

public interface QywxChatgptMsgMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(QywxChatgptMsg row);

    QywxChatgptMsg selectByPrimaryKey(Integer id);

    List<QywxChatgptMsg> selectAll();

    int updateByPrimaryKey(QywxChatgptMsg row);
}