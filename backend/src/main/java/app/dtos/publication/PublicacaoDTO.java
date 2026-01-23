package app.dtos.publication;

import app.entities.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PublicacaoDTO implements Serializable {
    private Long id;
    private String titulo;
    private String tipo;
    private String areaCientifica;
    private String descricao;
    private String file;
    private String fileType;
    private String resumo;
    private Boolean hidden;
    private Double ratingAverage;
    private Long ratingCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;

    private String creatorUsername;
    private String creatorName;

    private List<AuthorInfo> autores;
    private List<String> tags;


    public PublicacaoDTO() {}


    public static PublicacaoDTO from(Publicacao p) {
        PublicacaoDTO dto = new PublicacaoDTO();
        dto.setId(p.getId());
        dto.setTitulo(p.getTitulo());
        if (p.getTipo() != null) dto.setTipo(p.getTipo().getName());
        if (p.getAreaCientifica() != null) dto.setAreaCientifica(p.getAreaCientifica().getName());
        dto.setDescricao(p.getDescricao());
        dto.setFile(p.getFilename());
        dto.setResumo(p.getResumo());
        dto.setHidden(p.getHidden());
        dto.setCreatedAt(p.getCreatedAt());

        if (p.getFilename() != null) {
            if (p.getFilename().toLowerCase().endsWith(".pdf")) dto.setFileType("pdf");
            else if (p.getFilename().toLowerCase().endsWith(".zip")) dto.setFileType("zip");
            else dto.setFileType("unknown");
        }

        if (p.getCreatedBy() != null) {
            dto.setCreatorUsername(p.getCreatedBy().getUsername());
            dto.setCreatorName(p.getCreatedBy().getName());
        }

        if (Hibernate.isInitialized(p.getAutores()) && p.getAutores() != null) {
            dto.setAutores(p.getAutores().stream()
                    .map(AuthorInfo::from)
                    .collect(Collectors.toList()));
        } else {
            dto.setAutores(new ArrayList<>());
        }

        if (Hibernate.isInitialized(p.getTags()) && p.getTags() != null) {
            dto.setTags(p.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList()));
        } else {
            dto.setTags(new ArrayList<>());
        }

        if (Hibernate.isInitialized(p.getRatings()) && p.getRatings() != null) {
            if (!p.getRatings().isEmpty()) {
                double average = p.getRatings().stream()
                        .mapToInt(Rating::getValue)
                        .average()
                        .orElse(0.0);
                dto.setRatingAverage(Math.round(average * 10.0) / 10.0);
                dto.setRatingCount((long) p.getRatings().size());
            } else {
                dto.setRatingAverage(0.0);
            }
        } else {
            dto.setRatingAverage(0.0);
        }

        return dto;
    }


    // --- Inner DTOs ---

    public static class AuthorInfo implements Serializable {
        public String username;
        public String name;

        public static AuthorInfo from(User u) {
            AuthorInfo info = new AuthorInfo();
            info.username = u.getUsername();
            info.name = u.getName();
            return info;
        }
    }

    public static class RatingDTO implements Serializable {
        public Long id;
        public int value;
        public String username;
        public static RatingDTO from(Rating r) {
            RatingDTO d = new RatingDTO();
            d.value = r.getValue();
            if (r.getUser() != null) {
                d.username = r.getUser().getUsername();
            }
            d.id = r.getId();
            return d;
        }
    }

    public static class ComentarioDTO implements Serializable {
        public String text;
        public String username;
        public LocalDateTime createdAt;
        public static ComentarioDTO from(Comment c) {
            ComentarioDTO d = new ComentarioDTO();
            d.text = c.getText();
            if (c.getUser() != null) {
                d.username = c.getUser().getUsername();
            }
            d.createdAt = c.getCreatedAt();
            return d;
        }
    }

    public static class HistoricoEdicaoDTO implements Serializable {
        public String description;
        public String editor;
        @JsonFormat(pattern = "dd/MM/yyyy")
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

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getAreaCientifica() { return areaCientifica; }
    public void setAreaCientifica(String areaCientifica) { this.areaCientifica = areaCientifica; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getResumo() { return resumo; }
    public void setResumo(String resumo) { this.resumo = resumo; }

    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }

    public Double getRatingAverage() { return ratingAverage; }
    public void setRatingAverage(Double ratingAverage) { this.ratingAverage = ratingAverage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatorUsername() { return creatorUsername; }
    public void setCreatorUsername(String creatorUsername) { this.creatorUsername = creatorUsername; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public List<AuthorInfo> getAutores() { return autores; }
    public void setAutores(List<AuthorInfo> autores) { this.autores = autores; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }
}