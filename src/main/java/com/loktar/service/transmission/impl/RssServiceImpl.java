package com.loktar.service.transmission.impl;


import com.loktar.conf.LokTarConfig;
import com.loktar.domain.transmission.TrRss;
import com.loktar.domain.transmission.TrRssTorrent;
import com.loktar.dto.transmission.TrResponse;
import com.loktar.mapper.transmission.TrRssMapper;
import com.loktar.mapper.transmission.TrRssTorrentMapper;
import com.loktar.service.transmission.RssService;
import com.loktar.util.DelayUtil;
import com.loktar.util.RssUtil;
import com.loktar.util.TransmissionUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RssServiceImpl implements RssService {

    private final TrRssMapper trRssMapper;

    private final TrRssTorrentMapper trRssTorrentMapper;

    private final TransmissionUtil transmissionUtil;

    private final LokTarConfig lokTarConfig;

    public RssServiceImpl(TrRssMapper trRssMapper, TrRssTorrentMapper trRssTorrentMapper, TransmissionUtil transmissionUtil, LokTarConfig lokTarConfig) {
        this.trRssMapper = trRssMapper;
        this.trRssTorrentMapper = trRssTorrentMapper;
        this.transmissionUtil = transmissionUtil;
        this.lokTarConfig = lokTarConfig;
    }

    @Override
    public List<TrRss> getTrRsssByStatus(int status) {
        return trRssMapper.getTrRsssByStatus(status);
    }

    @Override
    @Transactional
    public void refreshTrRssTorrents(TrRss trRss) {
        List<TrRssTorrent> trRssTorrents = RssUtil.getRssData(trRss);
        for (TrRssTorrent trRssTorrent : trRssTorrents) {
            if (ObjectUtils.isEmpty(trRssTorrentMapper.selectByPrimaryKey(trRssTorrent.getRssTorrentId()))) {
                trRssTorrentMapper.insert(trRssTorrent);
            }
        }
    }

    @Override
    public void dealTrRssTorrents(TrRss trRss) {
        List<TrRssTorrent> trRssTorrents = trRssTorrentMapper.getTrRssTorrentsByStatusAndTrRssId(0, trRss.getRssId());
        for (TrRssTorrent trRssTorrent : trRssTorrents) {
            Pattern r = Pattern.compile(trRss.getPattern());
            Matcher m = r.matcher(trRssTorrent.getTitle());
            if (m.matches()) {
                String downloadUrl = trRssTorrent.getDownloadUrl();
                DelayUtil.delaySeconds(3, 5);
                TrResponse trResponse = transmissionUtil.addTorrent(downloadUrl, lokTarConfig.getTransmission().getTempDownloadDir(), false);
                if (!ObjectUtils.isEmpty(trResponse) && trResponse.getResult().equals(TrResponse.RESULT_SUCCESS)) {
                    System.out.println("RSS自动添加-" + trRss.getHostCnName() + ":" + trRssTorrent.getTitle());
                    trRssTorrent.setStatus(1);
                } else {
                    System.out.println("RSS添加失败-" + trRss.getHostCnName() + ":" + trRssTorrent.getTitle());
                    trRssTorrent.setStatus(3);
                }
            } else {
                trRssTorrent.setStatus(2);
            }
            trRssTorrentMapper.updateByPrimaryKey(trRssTorrent);
        }
    }

}
