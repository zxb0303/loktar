package com.loktar.domain.qywx;

import lombok.Data;

import java.io.Serializable;

@Data
public class QywxMenu implements Serializable {
    private Integer menuId;

    private Integer agentId;

    private String button;

    private Integer buttonLevel;

    private Integer hasSubButton;

    private String name;

    private String type;

    private String key;

    private String url;

    private Integer order;

    private Integer status;

    private static final long serialVersionUID = 1L;
}