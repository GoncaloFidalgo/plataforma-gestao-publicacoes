package app.dtos.comments;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public class CommentCreateDTO implements Serializable {

    @NotBlank(message = "Comment text cannot be empty")
    private String comment;

    public CommentCreateDTO() {
    }

    public CommentCreateDTO(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}