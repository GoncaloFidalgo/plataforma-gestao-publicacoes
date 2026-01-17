package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // <--- Importante
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaResponse implements Serializable {

    private String model;
    private String response;
    private boolean done;

    public OllamaResponse() {}

    // --- GETTERS E SETTERS ---


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}