package com.loktar.dto.minecraft;

import lombok.Data;

import java.util.List;

@Data
public class BedrockVersionsDTO {

    private Release release;

    private Preview preview;

    @Data
    public static class Release {
        private String latest;
        private List<String> versions;
    }

    @Data
    public static class Preview {
        private String latest;
        private List<String> versions;
    }
}
