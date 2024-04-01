package com.loktar.dto.transmission;

import lombok.Data;

@Data
public class TrRequestArg {
    private String filename;
    private Boolean paused;
    private String downloadDir;
    private Integer[] ids;
    private Boolean deleteLocalData;
    private Boolean altSpeedEnabled;
    private String path;
    private String[] fields;
}
