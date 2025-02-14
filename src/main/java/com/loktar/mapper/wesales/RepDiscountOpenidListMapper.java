package com.loktar.mapper.wesales;

import com.loktar.domain.wesales.RepDiscountOpenidList;

import java.util.List;

public interface RepDiscountOpenidListMapper {
    int insert(RepDiscountOpenidList row);

    int updateByPrimaryKey(RepDiscountOpenidList row);

    List<RepDiscountOpenidList> selectAll();

    List<RepDiscountOpenidList> getNeedSendOpenids(String province, int count);

    int updateBatchStatus(List<RepDiscountOpenidList> repDiscountOpenidLists);

}