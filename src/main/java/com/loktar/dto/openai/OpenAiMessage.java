package com.loktar.dto.openai;

import lombok.Data;

@Data
public class OpenAiMessage {
    public String role;
    public String content;

    public OpenAiMessage() {
    }

    public OpenAiMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
