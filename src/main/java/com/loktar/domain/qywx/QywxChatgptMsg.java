package com.loktar.domain.qywx;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class QywxChatgptMsg implements Serializable {
    private Integer id;

    private String fromUserName;

    private String role;

    private String filename;

    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totaltokens;

    private LocalDateTime createTime;

    private String text;

    private static final long serialVersionUID = 1L;
}