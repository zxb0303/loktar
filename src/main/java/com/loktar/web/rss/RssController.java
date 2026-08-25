package com.loktar.web.rss;

import com.loktar.domain.transmission.TrRss;
import com.loktar.mapper.transmission.TrRssMapper;
import com.loktar.service.transmission.RssService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rss")
public class RssController {

    private final TrRssMapper trRssMapper;
    private final RssService rssService;

    public RssController(TrRssMapper trRssMapper, RssService rssService) {
        this.trRssMapper = trRssMapper;
        this.rssService = rssService;
    }


    @GetMapping("/refreshTrRssTorrents")
    public void refreshTrRssTorrents(int rssId) {
        TrRss trRss = trRssMapper.selectByPrimaryKey(rssId);
        rssService.refreshTrRssTorrents(trRss);
    }

    @GetMapping("/dealTrRssTorrents")
    public void dealTrRssTorrents(int rssId) {
        TrRss trRss = trRssMapper.selectByPrimaryKey(rssId);
        rssService.dealTrRssTorrents(trRss);
    }
}
