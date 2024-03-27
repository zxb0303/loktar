package com.loktar.mapper.transmission;

import com.loktar.domain.transmission.TrRssTorrent;
import java.util.List;

public interface TrRssTorrentMapper {
    int deleteByPrimaryKey(Integer rssTorrentId);

    int insert(TrRssTorrent row);

    TrRssTorrent selectByPrimaryKey(Integer rssTorrentId);

    List<TrRssTorrent> selectAll();

    int updateByPrimaryKey(TrRssTorrent row);

    List<TrRssTorrent> getTrRssTorrentsByStatusAndTrRssId(int status,int rssId);

}