package app.dtos.publication;

import java.io.Serializable;
import java.util.List;

public class PublicacaoCreateDTO implements Serializable {
    private String titulo;
    private Long tipoId;
    private Long areaId;
    private String descricao;
    private List<String> autores;
    private List<String> tags;
    private Boolean hidden;

    public PublicacaoCreateDTO() {}

    // Getters and Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Long getTipoId() { return tipoId; }
    public void setTipoId(Long tipoId) { this.tipoId = tipoId; }

    public Long getAreaId() { return areaId; }
    public void setAreaId(Long areaId) { this.areaId = areaId; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<String> getAutores() { return autores; }
    public void setAutores(List<String> autores) { this.autores = autores; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }
}