package com.loktar.mapper.cxy;

import com.loktar.domain.cxy.TiktokAccount;
import java.util.List;

public interface TiktokAccountMapper {
    int deleteByPrimaryKey(Integer accountId);

    int insert(TiktokAccount row);

    TiktokAccount selectByPrimaryKey(Integer accountId);

    List<TiktokAccount> selectAll();

    int updateByPrimaryKey(TiktokAccount row);

    List<TiktokAccount> selectByStatus(int status);
}