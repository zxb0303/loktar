package com.loktar.dto.lottery;

import lombok.Data;

import java.util.List;

@Data
public class HZLotteryHouseResultDTO {
    private int pageCnt;
    private int code;
    private int rowCnt;
    private String pageNum;
    private String pageName;
    private int pageSize;
    private List<HZLotteryHouseDTO> dataList;

}
