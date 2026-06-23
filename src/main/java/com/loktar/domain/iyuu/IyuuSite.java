package com.loktar.domain.iyuu;

import lombok.Data;

import java.io.Serializable;

@Data
public class IyuuSite implements Serializable {
    private Integer id;

    private String site;

    private String nickname;

    private String baseUrl;

    private String downloadPage;

    private String reseedCheck;

    private Integer isHttps;

    private String supported;

    private Integer uid;

    private String passkey;

    private String downloadHash;

    private String trackHost;

    private Integer registered;

    private Integer importantLevel;

    private static final long serialVersionUID = 1L;
}