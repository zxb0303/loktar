package com.loktar.service.newhouse;

import com.loktar.domain.newhouse.NewHouseHangzhouV3;
import com.loktar.domain.newhouse.NewHouseHangzhouV3Presell;
import com.loktar.domain.newhouse.NewHouseHangzhouV3PresellBuild;

import java.util.List;

public interface NewHouseHangzhouV3Service {
    NewHouseHangzhouV3 getNewHouseData(String houseName);

    List<NewHouseHangzhouV3Presell> getNewHousePresellDataByHouseId(String houseId);

    List<NewHouseHangzhouV3PresellBuild> getNewHousePresellBuildDataByHouseId(String houseId);

    void memberLogin();
}
