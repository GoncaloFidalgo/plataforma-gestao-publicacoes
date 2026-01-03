package app.dtos;

import app.entities.Publicacao;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

public class PublicacaoDTO implements Serializable {
    private Long id;
    private String titulo;
    private String autor;
    private String areaCientifica;
    private String descricao;
    private String file;
    private String resumo;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;
    private Long createdBy;
    private String createdByName;
    private Boolean hidden;
    private String ratings;
    private String comentarios;
    private String historicoEdicoes;
    private String tags;
    private String tipo;
    private List<String> autores;

    public PublicacaoDTO() {
    }

    public static PublicacaoDTO from(Publicacao publicacao) {
        PublicacaoDTO dto = new PublicacaoDTO();
        dto.setId(publicacao.getId());
        dto.setTitulo(publicacao.getTitulo());
        dto.setAutor(publicacao.getAutor());
        dto.setAreaCientifica(publicacao.getAreaCientifica());
        dto.setDescricao(publicacao.getDescricao());
        dto.setFile(publicacao.getFile());
        dto.setResumo(publicacao.getResumo());
        dto.setCreatedAt(publicacao.getCreatedAt());
        dto.setCreatedByName(publicacao.getCreatedByName());
        dto.setHidden(publicacao.getHidden());
        dto.setRatings(publicacao.getRatings());
        dto.setComentarios(publicacao.getComentarios());
        dto.setHistoricoEdicoes(publicacao.getHistoricoEdicoes());
        dto.setTags(publicacao.getTags());
        return dto;
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<String> getAutores() {
        return autores;
    }

    public void setAutores(List<String> autores) {
        this.autores = autores;
    }
}

