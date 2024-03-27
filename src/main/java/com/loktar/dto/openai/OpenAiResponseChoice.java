package com.loktar.dto.openai;

import lombok.Data;

@Data
public class OpenAiResponseChoice {
    public int index;
    public OpenAiMessage message;
    public String finishReason;
}
