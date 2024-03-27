package com.loktar.mapper.iyuu;

import com.loktar.domain.iyuu.IyuuSite;
import java.util.List;

public interface IyuuSiteMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IyuuSite row);

    IyuuSite selectByPrimaryKey(Integer id);

    List<IyuuSite> selectAll();

    int updateByPrimaryKey(IyuuSite row);
}