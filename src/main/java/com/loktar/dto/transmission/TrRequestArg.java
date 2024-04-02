package com.loktar.dto.transmission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TrRequestArg {
    private String filename;
    private Boolean paused;
    private String downloadDir;
    private Integer[] ids;
    @JsonProperty("delete-local-data")
    private Boolean deleteLocalData;
    @JsonProperty("alt-speed-enabled")
    private Boolean altSpeedEnabled;
    private String path;
    private String[] fields;
}
