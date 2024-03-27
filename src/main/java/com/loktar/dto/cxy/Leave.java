package com.loktar.dto.cxy;

import lombok.Data;

@Data
public class Leave {

    private String name;
    private String jobNo;
    private String date;
    //病假、事假、调休假->调休、外出、出差
    private String leaveType;
    //全天、上午、下午
    private String leaveTime;
}
