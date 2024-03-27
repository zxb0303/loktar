package com.loktar.dto.land;

import lombok.Data;

import java.util.List;

@Data
public class LandResultDTO {
    private List<LandDTO> data;
    private String message;
    private String success;
}
