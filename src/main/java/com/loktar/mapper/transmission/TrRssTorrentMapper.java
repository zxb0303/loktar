package com.loktar.mapper.transmission;

import com.loktar.domain.transmission.TrRssTorrent;
import java.util.List;

public interface TrRssTorrentMapper {
    int deleteByPrimaryKey(Integer rssTorrentId, Integer rssId);

    int insert(TrRssTorrent row);

    TrRssTorrent selectByPrimaryKey(Integer rssTorrentId, Integer rssId);

    List<TrRssTorrent> selectAll();

    int updateByPrimaryKey(TrRssTorrent row);

    List<TrRssTorrent> getTrRssTorrentsByStatusAndTrRssId(int status,int rssId);

}