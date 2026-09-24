package com.loktar.domain.qywx;

import lombok.Data;

import java.io.Serializable;

@Data
public class QywxChatgptMsg implements Serializable {
    private Integer id;

    private String fromUserName;

    private String agentId;

    private String role;

    private String filename;

    private String text;

    private static final long serialVersionUID = 1L;
}