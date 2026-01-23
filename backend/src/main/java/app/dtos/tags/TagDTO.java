package app.dtos.tags;

import app.entities.Tag;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TagDTO implements Serializable {
    private String name;
    private String description;
    private String scientific_area;
    private Boolean hidden;
    private String created_at;

    private int publicacoes_count;
    private int subscritores_count;

    public TagDTO() {
    }

    public TagDTO(String name, String description, String scientific_area, Boolean hidden, LocalDateTime createdAt, int pubCount, int subCount) {
        this.name = name;
        this.description = description;
        this.scientific_area = scientific_area;
        this.hidden = hidden;
        this.created_at = createdAt != null ? createdAt.toString() : null;
        this.publicacoes_count = pubCount;
        this.subscritores_count = subCount;
    }

    public static TagDTO from(Tag tag) {
        return new TagDTO(
                tag.getName(),
                tag.getDescription(),
                tag.getScientificArea(),
                tag.getHidden(),
                tag.getCreatedAt(),
                tag.getPublicacoes().size(),
                tag.getSubscribers().size()
        );
    }

    public static List<TagDTO> from(List<Tag> tags) {
        return tags.stream().map(TagDTO::from).collect(Collectors.toList());
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getScientific_area() { return scientific_area; }
    public void setScientific_area(String scientific_area) { this.scientific_area = scientific_area; }
    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }
    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
    public int getPublicacoes_count() { return publicacoes_count; }
    public void setPublicacoes_count(int publicacoes_count) { this.publicacoes_count = publicacoes_count; }
    public int getSubscritores_count() { return subscritores_count; }
    public void setSubscritores_count(int subscritores_count) { this.subscritores_count = subscritores_count; }
}