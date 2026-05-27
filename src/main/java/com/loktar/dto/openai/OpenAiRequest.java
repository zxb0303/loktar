package com.loktar.dto.openai;

import lombok.Data;

import java.util.List;

@Data
public class OpenAiRequest {
    public String model;
    //随机性 default 0.8
    public double temperature;
    //一个字一个字的发 default false
    public boolean stream;
    //核采率，与随机性类似，但不要和随机性一起修改 default 1.0
    public double topP;

    public List<OpenAiMessage> messages;
}
