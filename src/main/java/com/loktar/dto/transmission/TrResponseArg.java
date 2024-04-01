package com.loktar.dto.transmission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TrResponseArg {
    private List<TrResponseTorrent> torrents;
    private String path;
    @JsonProperty("size-bytes")
    private Long sizeBytes;
    @JsonProperty("total_size")
    private Long totalSize;
    @JsonProperty("alt-speed-enabled")
    private Boolean altSpeedEnabled;

}
