package com.example.plantdiseasedetection.model;

import java.util.List;

public class GeminiRequest {
    private List<Content> messages;

    public GeminiRequest(String b64Img, String prompt) {
        this.messages = List.of(new Content(b64Img,prompt));
    }

    static class Content {
        private String role = "user";
        private String content;

        public Content(String b64Img, String prompt) {
            this.content = b64Img + "\n" + prompt;
        }
    }
}
