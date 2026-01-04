package app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "publicacoes")
public class Publicacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String titulo;

    private String autor; // Kept as original (could be migrated to User later if needed)

    private String areaCientifica;

    private String descricao;

    private String file;

    private String resumo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Kept purely for historical record of who uploaded it originally
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_by_name")
    private String createdByName;

    private Boolean hidden = false;

    // --- New Relationships ---

    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoEdicao> historicoEdicoes = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "publicacao_tags",
            joinColumns = @JoinColumn(name = "publicacao_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Publicacao() {
    }

    public Publicacao(String titulo, String autor, String areaCientifica, String descricao, String file, String resumo, String createdBy, String createdByName) {
        this.titulo = titulo;
        this.autor = autor;
        this.areaCientifica = areaCientifica;
        this.descricao = descricao;
        this.file = file;
        this.resumo = resumo;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.createdAt = LocalDateTime.now();
        this.hidden = false;
    }


    public void addComentario(Comentario comentario) {
        comentarios.add(comentario);
        comentario.setPublicacao(this);
    }

    public void addRating(Rating rating) {
        ratings.add(rating);
        rating.setPublicacao(this);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getPublicacoes().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getPublicacoes().remove(this);
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getAreaCientifica() { return areaCientifica; }
    public void setAreaCientifica(String areaCientifica) { this.areaCientifica = areaCientifica; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }

    public String getResumo() { return resumo; }
    public void setResumo(String resumo) { this.resumo = resumo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }

    public List<Rating> getRatings() { return ratings; }
    public void setRatings(List<Rating> ratings) { this.ratings = ratings; }

    public List<Comentario> getComentarios() { return comentarios; }
    public void setComentarios(List<Comentario> comentarios) { this.comentarios = comentarios; }

    public List<HistoricoEdicao> getHistoricoEdicoes() { return historicoEdicoes; }
    public void setHistoricoEdicoes(List<HistoricoEdicao> historicoEdicoes) { this.historicoEdicoes = historicoEdicoes; }

    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }
}