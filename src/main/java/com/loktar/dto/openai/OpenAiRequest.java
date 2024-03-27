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
    //单次回复限制 default:2000
    public int maxTokens;
    //话题新鲜度 -2.0~2.0 default:1.0
    public double presencePenalty;
    //核采率，与随机性类似，但不要和随机性一起修改 default 1.0
    public double topP;

    public List<OpenAiMessage> messages;
}
