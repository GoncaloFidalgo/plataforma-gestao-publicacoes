package app.dtos;

import app.entities.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private String createdByName;
    private Boolean hidden;

    private List<RatingDTO> ratings;
    private List<ComentarioDTO> comentarios;
    private List<HistoricoEdicaoDTO> historicoEdicoes;
    private List<String> tags;

    public PublicacaoDTO() {}

    public static PublicacaoDTO from(Publicacao p) {
        PublicacaoDTO dto = new PublicacaoDTO();
        dto.setId(p.getId());
        dto.setTitulo(p.getTitulo());
        dto.setAutor(p.getAutor());
        dto.setAreaCientifica(p.getAreaCientifica());
        dto.setDescricao(p.getDescricao());
        dto.setFile(p.getFile());
        dto.setResumo(p.getResumo());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setCreatedByName(p.getCreatedByName());
        dto.setHidden(p.getHidden());

        dto.setRatings(p.getRatings().stream().map(RatingDTO::from).collect(Collectors.toList()));
        dto.setComentarios(p.getComentarios().stream().map(ComentarioDTO::from).collect(Collectors.toList()));
        dto.setHistoricoEdicoes(p.getHistoricoEdicoes().stream().map(HistoricoEdicaoDTO::from).collect(Collectors.toList()));
        dto.setTags(p.getTags().stream().map(Tag::getName).collect(Collectors.toList()));

        return dto;
    }

    // --- Inner DTOs ---

    public static class RatingDTO implements Serializable {
        public int value;
        public String username;
        public static RatingDTO from(Rating r) {
            RatingDTO d = new RatingDTO();
            d.value = r.getValue();
            d.username = r.getUser().getUsername();
            return d;
        }
    }

    public static class ComentarioDTO implements Serializable {
        public String text;
        public String username;
        public LocalDateTime createdAt;
        public static ComentarioDTO from(Comentario c) {
            ComentarioDTO d = new ComentarioDTO();
            d.text = c.getText();
            d.username = c.getUser().getUsername();
            d.createdAt = c.getCreatedAt();
            return d;
        }
    }

    public static class HistoricoEdicaoDTO implements Serializable {
        public String description;
        public String editor;
        public LocalDateTime modifiedAt;
        public static HistoricoEdicaoDTO from(HistoricoEdicao h) {
            HistoricoEdicaoDTO d = new HistoricoEdicaoDTO();
            d.description = h.getDescription();
            d.editor = h.getEditor() != null ? h.getEditor().getUsername() : "System";
            d.modifiedAt = h.getModifiedAt();
            return d;
        }
    }

    // --- Getters & Setters ---
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
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }

    public List<RatingDTO> getRatings() { return ratings; }
    public void setRatings(List<RatingDTO> ratings) { this.ratings = ratings; }
    public List<ComentarioDTO> getComentarios() { return comentarios; }
    public void setComentarios(List<ComentarioDTO> comentarios) { this.comentarios = comentarios; }
    public List<HistoricoEdicaoDTO> getHistoricoEdicoes() { return historicoEdicoes; }
    public void setHistoricoEdicoes(List<HistoricoEdicaoDTO> historicoEdicoes) { this.historicoEdicoes = historicoEdicoes; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}