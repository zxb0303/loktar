package com.loktar.dto.common;

import lombok.Data;

@Data
public class IntentResult {

    /**
     * 是否为添加提醒意图
     */
    private boolean notice;

    /**
     * 是否需要用户补充信息
     */
    private boolean needConfirm;

    /**
     * 需要补充的信息说明
     */
    private String uncertainField;

    /**
     * 提醒标题
     */
    private String title;

    /**
     * 提醒内容
     */
    private String content;

    /**
     * 提醒时间，格式 yyyy-MM-dd HH:mm
     */
    private String noticeTime;

    /**
     * 数据库写入是否成功
     */
    private boolean insertSuccess;

    /**
     * 是否重置会话
     */
    private boolean resetSession;
}
