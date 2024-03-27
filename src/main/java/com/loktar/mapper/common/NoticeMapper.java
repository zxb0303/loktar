package com.loktar.mapper.common;

import com.loktar.domain.common.Notice;
import java.util.List;

public interface NoticeMapper {
    int deleteByPrimaryKey(Integer noticeId);

    int insert(Notice row);

    Notice selectByPrimaryKey(Integer noticeId);

    List<Notice> selectAll();

    int updateByPrimaryKey(Notice row);

    List<Notice> getUnsendNotices();

    List<Notice> getUnsendNoticesByNoticeUser(String noticeUser);
}