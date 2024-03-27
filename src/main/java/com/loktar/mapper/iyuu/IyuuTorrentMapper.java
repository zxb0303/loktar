package com.loktar.mapper.iyuu;

import com.loktar.domain.iyuu.IyuuTorrent;
import java.util.List;

public interface IyuuTorrentMapper {
    int deleteByPrimaryKey(String infoHash);

    int insert(IyuuTorrent row);

    IyuuTorrent selectByPrimaryKey(String infoHash);

    List<IyuuTorrent> selectAll();

    int updateByPrimaryKey(IyuuTorrent row);
}