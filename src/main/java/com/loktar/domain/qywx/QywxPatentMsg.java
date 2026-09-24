package com.loktar.domain.qywx;

import lombok.Data;

import java.io.Serializable;

@Data
public class QywxPatentMsg implements Serializable {
    private Integer id;

    private String agentId;

    private String fromUserName;

    private String content;

    private String type;

    private String applyName;

    private String price;

    private String mobile;

    private String status;

    private static final long serialVersionUID = 1L;
}