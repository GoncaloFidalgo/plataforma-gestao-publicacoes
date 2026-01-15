package app.dtos;

import java.io.Serializable;
import java.util.List;

public class PublicacaoCreateDTO implements Serializable {
    private String titulo;
    private String tipo;
    private String area_cientifica;
    private String descricao;
    private List<String> autores;
    private List<String> tags;
    private Boolean hidden;

    public PublicacaoCreateDTO() {}

    // Getters and Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getArea_cientifica() { return area_cientifica; }
    public void setArea_cientifica(String area_cientifica) { this.area_cientifica = area_cientifica; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<String> getAutores() { return autores; }
    public void setAutores(List<String> autores) { this.autores = autores; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }
}