package com.loktar.mapper.newhouse;

import com.loktar.domain.newhouse.NewHouseHangzhouV3PresellBuild;
import java.util.List;

public interface NewHouseHangzhouV3PresellBuildMapper {
    int deleteByPrimaryKey(String buildId);

    int insert(NewHouseHangzhouV3PresellBuild row);

    NewHouseHangzhouV3PresellBuild selectByPrimaryKey(String buildId);

    List<NewHouseHangzhouV3PresellBuild> selectAll();

    int updateByPrimaryKey(NewHouseHangzhouV3PresellBuild row);

    void insertBatch(List<NewHouseHangzhouV3PresellBuild> newHouseHangzhouV3PresellBuilds);
}