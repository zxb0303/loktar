package com.loktar.dto.audiobookshelf;

import lombok.Data;

import java.util.List;

@Data
public class AbsListeningStats {
    private Long totalTime;
    private Long today;
    private List<AbsPlaybackSession> recentSessions;
}
