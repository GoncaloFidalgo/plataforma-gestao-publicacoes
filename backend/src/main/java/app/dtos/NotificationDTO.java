package app.dtos;

import app.entities.Notification;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationDTO implements Serializable {

    private Long id;
    private String message;
    private Boolean isRead;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    private Long publicationId;
    private String publicationTitle;
    private Long commentId;

    public NotificationDTO() {}

    public NotificationDTO(Long id, String message, Boolean isRead, LocalDateTime createdAt,
                          Long publicationId, String publicationTitle, Long commentId) {
        this.id = id;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.publicationId = publicationId;
        this.publicationTitle = publicationTitle;
        this.commentId = commentId;
    }

    public static NotificationDTO from(Notification n) {
        return new NotificationDTO(
            n.getId(),
            n.getMessage(),
            n.getIsRead(),
            n.getCreatedAt(),
            n.getPublicacao() != null ? n.getPublicacao().getId() : null,
            n.getPublicacao() != null ? n.getPublicacao().getTitulo() : null,
            n.getComment() != null ? n.getComment().getId() : null
        );
    }

    public static List<NotificationDTO> from(List<Notification> notifications) {
        return notifications.stream().map(NotificationDTO::from).collect(Collectors.toList());
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

    public Long getPublicationId() { return publicationId; }
    public void setPublicationId(Long publicationId) { this.publicationId = publicationId; }

    public String getPublicationTitle() { return publicationTitle; }
    public void setPublicationTitle(String publicationTitle) { this.publicationTitle = publicationTitle; }

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
}