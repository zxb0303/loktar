package com.loktar.dto.transmission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TrResponseArgDTO {
    private List<TrResponseTorrentDTO> torrents;
    private String path;
    private long sizeBytes;
    @JsonProperty("alt-speed-enabled")
    private boolean altSpeedEnabled;

}
