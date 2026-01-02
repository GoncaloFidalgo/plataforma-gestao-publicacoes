package app.dtos;

import java.io.Serializable;

public class EmailDTO implements Serializable {
    private String subject;
    private String body;

    public EmailDTO() {
    }

    public EmailDTO(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    //region Getters
    public String getSubject() {
        return subject;
    }
    public String getBody() {
        return body;
    }
    //endregion

    //region Setters
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public void setBody(String body) {
        this.body = body;
    }
}
