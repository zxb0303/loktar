package com.loktar.dto.rss;

import lombok.Data;

@Data
public class RssItem {
    private String title;
    private String link;
    private String pubDate;
    private RssEnclosure enclosure;
}
