package com.loktar.domain.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class Notice implements Serializable {
    private Integer noticeId;

    private String noticeTitle;

    private String noticeContent;

    private String noticeTime;

    private String noticeUser;

    private Integer status;

    private static final long serialVersionUID = 1L;
}