package app.dtos.comments;

import app.entities.Comment;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDTO implements Serializable {

    private Long id;
    private String comment; // Maps to entity.text
    private String username;
    private String name;
    private LocalDateTime date; // Maps to entity.createdAt
    private Boolean hidden;
    private String motive;

    public CommentDTO() {
    }

    public CommentDTO(Long id, String comment, String username, String name, LocalDateTime date, Boolean hidden, String motive) {
        this.id = id;
        this.comment = comment;
        this.username = username;
        this.name = name;
        this.date = date;
        this.hidden = hidden;
        this.motive = motive;
    }

    public static CommentDTO from(Comment c) {
        return new CommentDTO(
                c.getId(),
                c.getText(),
                c.getUser().getUsername(),
                c.getUser().getName(),
                c.getCreatedAt(),
                c.isHidden(),
                c.getMotive()
        );
    }

    public static List<CommentDTO> from(List<Comment> comments) {
        return comments.stream().map(CommentDTO::from).collect(Collectors.toList());
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }

    public String getMotive() { return motive; }
    public void setMotive(String motive) { this.motive = motive; }
}