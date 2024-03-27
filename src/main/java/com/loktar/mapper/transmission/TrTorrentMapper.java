package com.loktar.mapper.transmission;

import com.loktar.domain.transmission.TrTorrent;
import java.util.List;

public interface TrTorrentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TrTorrent row);

    TrTorrent selectByPrimaryKey(Integer id);

    List<TrTorrent> selectAll();

    int updateByPrimaryKey(TrTorrent row);

    TrTorrent getworstTorrent(int days, String downloadDir);

    List<TrTorrent> getTorrentsByNameAndSize(String name,Long totalSize);

    void truncate();

    List<TrTorrent> getNeedStartTrTorrents();

    List<TrTorrent> getTrTorrentsByStatus(int status);

    List<String> getNotRegisteredErrorName();

    List<TrTorrent> getTorrentsByName(String name);

    int insertOrUpdate(TrTorrent record);

}