package app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

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

    private String areaCientifica;

    private String descricao;

    private String tipo;

    private String file;

    @Column(length = 10000)
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
    private List<Comentario> comentarios = new ArrayList<>();

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

    public Publicacao(String titulo,  String tipo, String areaCientifica, String descricao, String file,  User createdBy) {
        this.titulo = titulo;
        this.tipo = tipo;
        this.areaCientifica = areaCientifica;
        this.descricao = descricao;
        this.file = file;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.hidden = false;
    }

    // Helper methods

    public void addComentario(Comentario comentario) {
        comentarios.add(comentario);
        comentario.setPublicacao(this);
    }

    public void removeCometario(Comentario comentario) {
        comentarios.remove(comentario);
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

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public List<Colaborador> getAutores() { return autores; }
    public void setAutores(List<Colaborador> autores) { this.autores = autores; }

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

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }

    public List<Rating> getRatings() { return ratings; }
    public void setRatings(List<Rating> ratings) { this.ratings = ratings; }

    public List<Comentario> getComentarios() { return comentarios; }
    public void setComentarios(List<Comentario> comentarios) { this.comentarios = comentarios; }

    public List<HistoricoEdicao> getHistoricoEdicoes() { return historicoEdicoes; }
    public void setHistoricoEdicoes(List<HistoricoEdicao> historicoEdicoes) { this.historicoEdicoes = historicoEdicoes; }

    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}