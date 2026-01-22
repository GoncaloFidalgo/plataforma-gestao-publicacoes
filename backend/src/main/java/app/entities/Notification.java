package app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@NamedQueries({
    @NamedQuery(
        name = "getAllNotificationsByUser",
        query = "SELECT n FROM Notification n WHERE n.user.username = :username ORDER BY n.createdAt DESC"
    ),
    @NamedQuery(
        name = "getUnreadNotificationsByUser",
        query = "SELECT n FROM Notification n WHERE n.user.username = :username AND n.isRead = false ORDER BY n.createdAt DESC"
    )
})
public class Notification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String message;

    @NotNull
    private Boolean isRead = false;

    @NotNull
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_username")
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    private Publicacao publicacao;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public Notification() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String message, User user, Publicacao publicacao, Comment comment) {
        this();
        this.message = message;
        this.user = user;
        this.publicacao = publicacao;
        this.comment = comment;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Publicacao getPublicacao() { return publicacao; }
    public void setPublicacao(Publicacao publicacao) { this.publicacao = publicacao; }

    public Comment getComment() { return comment; }
    public void setComment(Comment comment) { this.comment = comment; }
}