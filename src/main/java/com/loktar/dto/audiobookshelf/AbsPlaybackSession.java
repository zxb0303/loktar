package com.loktar.dto.audiobookshelf;

import lombok.Data;

@Data
public class AbsPlaybackSession {
    private String id;
    private String userId;
    private String displayTitle;
    private String displayAuthor;
    private Long currentTime;
    private Long duration;
}
