package app.dtos;

import java.io.Serializable;

public class OllamaRequest implements Serializable {
    private String model;
    private String prompt;
    private boolean stream = false;

    public OllamaRequest() {}

    public OllamaRequest(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
        this.stream = false;
    }

    // Getters e Setters
    public String getModel() {
        return model;
    }

    public String getPrompt() {
        return prompt;
    }

    public boolean isStream() {
        return stream;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }
}