package app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "publicacoes")
public class Publicacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String titulo;

    @ManyToMany
    @JoinTable(
            name = "publicacao_autores",
            joinColumns = @JoinColumn(name = "publicacao_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_username")
    )
    private List<Colaborador> autores = new ArrayList<>();

    private String descricao;

    @ManyToOne
    @JoinColumn(name = "tipo_id") // Links to PublicationType.id
    private PublicationType tipo;

    @ManyToOne
    @JoinColumn(name = "area_id") // Links to ScientificArea.id
    private ScientificArea areaCientifica;

    private String filename;

    private String resumo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by_username", referencedColumnName = "username")
    private User createdBy;

    private Boolean hidden = false;

    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoEdicao> historicoEdicoes = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "publicacao_tags",
            joinColumns = @JoinColumn(name = "publicacao_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_name")
    )
    private List<Tag> tags = new ArrayList<>();

    public Publicacao() {
    }

    public Publicacao(String titulo, PublicationType tipo, ScientificArea areaCientifica, String descricao, String filename, User createdBy) {
        this.titulo = titulo;
        this.tipo = tipo;
        this.areaCientifica = areaCientifica;
        this.descricao = descricao;
        this.filename = filename;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.hidden = false;
    }

    // Helper methods

    public void addComentario(Comment comment) {
        comments.add(comment);
        comment.setPublicacao(this);
    }

    public void removeCometario(Comment comment) {
        comments.remove(comment);
    }

    public void addRating(Rating rating) {
        ratings.add(rating);
        rating.setPublicacao(this);
    }

    public void removeRating(Rating rating) {
        ratings.remove(rating);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getPublicacoes().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getPublicacoes().remove(this);
    }

    public void addAutor(Colaborador autor) {
        autores.add(autor);
    }

    public void removeAutor(Colaborador autor) {
        autores.remove(autor);
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PublicationType getTipo() { return tipo; }
    public void setTipo(PublicationType tipo) { this.tipo = tipo; }

    public ScientificArea getAreaCientifica() { return areaCientifica; }
    public void setAreaCientifica(ScientificArea areaCientifica) { this.areaCientifica = areaCientifica; }


    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public List<Colaborador> getAutores() { return autores; }
    public void setAutores(List<Colaborador> autores) { this.autores = autores; }


    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getFilename() { return filename; }
    public void setFilename(String file) { this.filename = file; }

    public String getResumo() { return resumo; }
    public void setResumo(String resumo) { this.resumo = resumo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }

    public List<Rating> getRatings() { return ratings; }
    public void setRatings(List<Rating> ratings) { this.ratings = ratings; }

    public List<Comment> getComentarios() { return comments; }
    public void setComentarios(List<Comment> comments) { this.comments = comments; }

    public List<HistoricoEdicao> getHistoricoEdicoes() { return historicoEdicoes; }
    public void setHistoricoEdicoes(List<HistoricoEdicao> historicoEdicoes) { this.historicoEdicoes = historicoEdicoes; }

    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }

}