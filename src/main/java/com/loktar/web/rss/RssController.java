package com.loktar.web.rss;

import com.loktar.domain.transmission.TrRss;
import com.loktar.mapper.transmission.TrRssMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rss")
public class RssController {

    private final TrRssMapper trRssMapper;

    public RssController(TrRssMapper trRssMapper) {
        this.trRssMapper = trRssMapper;
    }


    @RequestMapping("/test.do")
    public void test() {
        TrRss trRss = trRssMapper.selectByPrimaryKey(7);
        //rssService.refreshTrRssTorrents(trRss);
        //rssService.dealTrRssTorrents(trRss);
    }
}
