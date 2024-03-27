package com.loktar.dto.rss;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;

import java.util.List;

@Data
public class RssChannel {
    private String title;
    private String link;
    //这个列表直接在channel下，而不是被一个额外的包装元素所包围
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<RssItem> item;
}
