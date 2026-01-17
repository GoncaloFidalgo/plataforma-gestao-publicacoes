package app.dtos;

import java.io.Serializable;

public class OllamaResponse implements Serializable {
    private String model;
    private String response; // O resumo vem aqui
    private boolean done;

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

}