package app.dtos.comments;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class CommentVisibilityDTO implements Serializable {

    @NotNull
    private Boolean hidden;

    private String motive;

    public CommentVisibilityDTO() {
    }

    public CommentVisibilityDTO(Boolean hidden, String motive) {
        this.hidden = hidden;
        this.motive = motive;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getMotive() {
        return motive;
    }

    public void setMotive(String motive) {
        this.motive = motive;
    }
}