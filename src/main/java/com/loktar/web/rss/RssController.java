package com.loktar.web.rss;

import com.loktar.domain.transmission.TrRss;
import com.loktar.mapper.transmission.TrRssMapper;
import com.loktar.service.transmission.RssService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rss")
public class RssController {
    private final RssService rssService;

    private final TrRssMapper trRssMapper;

    public RssController(RssService rssService, TrRssMapper trRssMapper) {
        this.rssService = rssService;
        this.trRssMapper = trRssMapper;
    }


    @RequestMapping("/test.do")
    public void test() {
        TrRss trRss = trRssMapper.selectByPrimaryKey(7);
        //rssService.refreshTrRssTorrents(trRss);
        //rssService.dealTrRssTorrents(trRss);
    }
}
