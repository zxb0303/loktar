package com.loktar.dto.openai;

import lombok.Data;

import java.util.List;

@Data
public class OpenAiResponse {
    public String id;
    public String object;
    public long created;
    public String model;
    public List<OpenAiResponseChoice> choices;
    public OpenAiResponseUsage usage;
}
