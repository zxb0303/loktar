package com.loktar.dto.openai;

import lombok.Data;

@Data
public class OpenAiResponseUsage {
    public int promptTokens;
    public int completionTokens;
    public int totalTokens;
}
