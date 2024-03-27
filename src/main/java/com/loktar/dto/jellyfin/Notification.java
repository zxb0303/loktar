package com.loktar.dto.jellyfin;


import lombok.Data;

@Data
public class Notification {
    private String title;
    private String url;
    private String notificationType;
    private String message;
    private String notificationUsername;
    private String itemType;
    private String seriesName;
    private String year;
    private String seasonNumber00;
    private String episodeNumber00;
    private String name;
    private String playbackPosition;
    private String deviceName;
    private String deviceId;
    private String clientName;
    private String overview;
    private String runTime;

}
