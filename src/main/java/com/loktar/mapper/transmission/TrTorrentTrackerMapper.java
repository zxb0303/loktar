package com.loktar.mapper.transmission;

import com.loktar.domain.transmission.TrTorrentTracker;
import java.util.List;

public interface TrTorrentTrackerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TrTorrentTracker row);

    TrTorrentTracker selectByPrimaryKey(Integer id);

    List<TrTorrentTracker> selectAll();

    int updateByPrimaryKey(TrTorrentTracker row);

    void truncate();

    void insertOrUpdate(TrTorrentTracker trTorrentTracker);

    void deleteByTorrentId(Integer torrentId);
}