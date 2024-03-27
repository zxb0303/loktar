package com.loktar.service.transmission;


import com.loktar.domain.transmission.TrRss;

import java.util.List;

public interface RssService {
    List<TrRss> getTrRsssByStatus(int status);

    void refreshTrRssTorrents(TrRss trRss);

    void dealTrRssTorrents(TrRss trRss);

}
