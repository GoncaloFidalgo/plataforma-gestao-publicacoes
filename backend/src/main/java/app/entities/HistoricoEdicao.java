package app.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_edicoes")
public class HistoricoEdicao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    private String description; // e.g., "Changed title from X to Y"

    @ManyToOne
    @JoinColumn(name = "editor_username")
    private User editor;

    @ManyToOne
    @JoinColumn(name = "publicacao_id", nullable = false)
    private Publicacao publicacao;

    public HistoricoEdicao() {}

    public HistoricoEdicao(String description, User editor, Publicacao publicacao) {
        this.description = description;
        this.editor = editor;
        this.publicacao = publicacao;
        this.modifiedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getEditor() { return editor; }
    public void setEditor(User editor) { this.editor = editor; }

    public Publicacao getPublicacao() { return publicacao; }
    public void setPublicacao(Publicacao publicacao) { this.publicacao = publicacao; }
}