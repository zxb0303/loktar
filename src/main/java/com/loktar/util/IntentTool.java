package com.loktar.util;

import com.loktar.domain.common.Notice;
import com.loktar.dto.common.IntentResult;
import com.loktar.service.common.NoticeServer;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

public class IntentTool {

    private final NoticeServer noticeServer;

    private final String noticeUser;

    private IntentResult result;

    public IntentTool(NoticeServer noticeServer, String noticeUser) {
        this.noticeServer = noticeServer;
        this.noticeUser = noticeUser;
    }

    @Tool("当用户明确想添加一条提醒/备忘/待办，并且已经提供了具体提醒时间时调用")
    public String addNotice(
            @P("提醒标题，简洁，不超过40字") String title,
            @P("提醒内容，不超过255字") String content,
            @P("提醒时间，格式必须是 yyyy-MM-dd HH:mm") String noticeTime) {
        Notice notice = new Notice();
        notice.setNoticeTitle(title);
        notice.setNoticeContent(content);
        notice.setNoticeTime(noticeTime);
        notice.setNoticeUser(noticeUser);
        notice.setStatus(0);
        int insertResult = noticeServer.insert(notice);

        result = new IntentResult();
        result.setNotice(true);
        result.setNeedConfirm(false);
        result.setTitle(title);
        result.setContent(content);
        result.setNoticeTime(noticeTime);
        result.setInsertSuccess(insertResult == 1);
        return insertResult == 1 ? "添加成功" : "添加失败";
    }

    @Tool("当用户想添加提醒但缺少必要信息（特别是具体提醒时间）时调用")
    public String askForMissingInfo(@P("说明具体缺少什么信息，例如缺少提醒时间") String missingInfo) {
        result = new IntentResult();
        result.setNotice(true);
        result.setNeedConfirm(true);
        result.setUncertainField(missingInfo);
        return "已记录缺失信息";
    }

    @Tool("当用户明确说'重置会话'、'清空会话'、'清空上下文'、'重置聊天'等想要清空当前对话上下文时调用")
    public String resetSession() {
        result = new IntentResult();
        result.setResetSession(true);
        return "已记录重置会话请求";
    }

    public IntentResult getResult() {
        return result;
    }
}
