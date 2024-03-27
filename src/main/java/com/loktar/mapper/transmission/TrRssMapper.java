package com.loktar.mapper.transmission;

import com.loktar.domain.transmission.TrRss;
import java.util.List;

public interface TrRssMapper {
    int deleteByPrimaryKey(Integer rssId);

    int insert(TrRss row);

    TrRss selectByPrimaryKey(Integer rssId);

    List<TrRss> selectAll();

    int updateByPrimaryKey(TrRss row);

    List<TrRss> getTrRsssByStatus(int status);
}