package com.loktar.dto.audiobookshelf;

import lombok.Data;

import java.util.List;

@Data
public class AbsOpenSessionsRsp {
    private List<AbsPlaybackSession> sessions;
}
