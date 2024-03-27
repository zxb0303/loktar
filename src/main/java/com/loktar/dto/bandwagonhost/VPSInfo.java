package com.loktar.dto.bandwagonhost;

import lombok.Data;

@Data
public class VPSInfo {
    private String vmType;
    private String hostname;
    private String nodeIp;
    private String nodeAlias;
    private String nodeLocationId;
    private String nodeLocation;
    private String nodeDatacenter;
    private boolean locationIpv6Ready;
    private String plan;
    private long planMonthlyData;
    private int monthlyDataMultiplie;
    private long planDisk;
    private long planRam;
    private int planSwap;
    private int planMaxIpv6s;
    private String os;
    private String email;
    private long dataCounter;
    private long dataNextReset;
    private String[] ipAddresses;
    private String[] privateIpAddresses;
    private String[] ipNullroutes;
    private String iso1;
    private String iso2;
    private String[] availableIsos;
    private boolean planPrivateNetworkAvailable;
    private boolean locationPrivateNetworkAvailable;
    private boolean rdnsApiAvailable;
}
