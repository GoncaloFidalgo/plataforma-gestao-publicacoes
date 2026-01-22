package app.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityDTO {
    private Long id;
    private String tipo; // "upload", "edicao", "comentario", "rating"
    private String descricao;
    private Long publicacaoId;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime data;

    // Campos específicos por tipo
    private List<String> camposEditados; // para edições
    private Long comentarioId; // para comentários
    private Integer rating; // para ratings

    public ActivityDTO() {}

    //region Getters
    public Long getId() { return id; }

    public String getTipo() { return tipo; }

    public String getDescricao() { return descricao; }

    public Long getPublicacaoId() { return publicacaoId; }

    public LocalDateTime getData() { return data; }

    public List<String> getCamposEditados() { return camposEditados; }

    public Long getComentarioId() { return comentarioId; }

    public Integer getRating() { return rating; }
    //endregion

    //region Setters
    public void setId(Long id) { this.id = id; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public void setPublicacaoId(Long publicacaoId) { this.publicacaoId = publicacaoId; }

    public void setData(LocalDateTime data) { this.data = data; }

    public void setCamposEditados(List<String> camposEditados) { this.camposEditados = camposEditados; }

    public void setComentarioId(Long comentarioId) { this.comentarioId = comentarioId; }

    public void setRating(Integer rating) { this.rating = rating; }
    //endregion

}
