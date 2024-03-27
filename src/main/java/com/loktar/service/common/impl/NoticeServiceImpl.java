package com.loktar.service.common.impl;

import com.loktar.domain.common.Notice;
import com.loktar.mapper.common.NoticeMapper;
import com.loktar.service.common.NoticeServer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeServer {

    private final NoticeMapper noticeMapper;

    public NoticeServiceImpl(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    @Override
    public List<Notice> selectAll() {
        return noticeMapper.selectAll();
    }

    @Override
    public int insert(Notice notice) {
        return  noticeMapper.insert(notice);
    }

    @Override
    public List<Notice> getUnsendNotices() {
        return noticeMapper.getUnsendNotices();
    }

    @Override
    public List<Notice> getUnsendNoticesByNoticeUser(String noticeUser) {
        return noticeMapper.getUnsendNoticesByNoticeUser(noticeUser);
    }
}
