package com.example.plantdiseasedetection.model;

public class GeminiResponse {
    private String resultText;
    private boolean success;

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
