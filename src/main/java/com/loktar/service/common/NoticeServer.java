package com.loktar.service.common;


import com.loktar.domain.common.Notice;

import java.util.List;

public interface NoticeServer {
    List<Notice> selectAll();

    int insert(Notice notice);

    List<Notice> getUnsendNotices();

    List<Notice> getUnsendNoticesByNoticeUser(String noticeUser);

    int updateByPrimaryKey(Notice notice);
}
