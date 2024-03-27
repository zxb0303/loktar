package com.loktar.mapper.cxy;

import com.loktar.domain.cxy.Contract;
import java.util.List;

public interface ContractMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Contract row);

    Contract selectByPrimaryKey(Integer id);

    List<Contract> selectAll();

    int updateByPrimaryKey(Contract row);

    List<Contract> getNeedNoticeContracts();

}