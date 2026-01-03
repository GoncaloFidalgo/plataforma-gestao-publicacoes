package app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "publicacoes")
public class Publicacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String titulo;

    private String autor;

    private String areaCientifica;

    private String descricao;

    private String file;

    private String resumo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_by_name")
    private String createdByName;

    private Boolean hidden = false;

    @Column(columnDefinition = "TEXT")
    private String ratings = "{}";

    @Column(columnDefinition = "TEXT")
    private String comentarios = "{}";

    @Column(name = "historico_edicoes", columnDefinition = "TEXT")
    private String historicoEdicoes = "{}";

    @Column(columnDefinition = "TEXT")
    private String tags = "{}";

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getAreaCientifica() {
        return areaCientifica;
    }

    public void setAreaCientifica(String areaCientifica) {
        this.areaCientifica = areaCientifica;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getHistoricoEdicoes() {
        return historicoEdicoes;
    }

    public void setHistoricoEdicoes(String historicoEdicoes) {
        this.historicoEdicoes = historicoEdicoes;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}

