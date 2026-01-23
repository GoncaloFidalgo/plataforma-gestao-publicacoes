package app.dtos.rating;

import app.entities.Rating;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class UserRatingsResponseDTO implements Serializable {
    private Double ratingMedio;
    private List<RatingDetailDTO> ratings;

    public UserRatingsResponseDTO() {}

    public UserRatingsResponseDTO(Double ratingMedio, List<RatingDetailDTO> ratings) {
        this.ratingMedio = ratingMedio;
        this.ratings = ratings;
    }

    public static UserRatingsResponseDTO from(List<Rating> ratingsList) {
        double average = ratingsList.isEmpty() ? 0.0 :
            ratingsList.stream()
                .mapToInt(Rating::getValue)
                .average()
                .orElse(0.0);

        return new UserRatingsResponseDTO(
            Math.round(average * 10.0) / 10.0,
            ratingsList.stream()
                .map(RatingDetailDTO::from)
                .collect(Collectors.toList())
        );
    }

    public static class RatingDetailDTO implements Serializable {
        private Long id;
        private Long publicacaoId;
        private String tituloPublicacao;
        private int rating;
        @JsonFormat(pattern = "dd/MM/yyyy")
        private LocalDateTime data;

        public static RatingDetailDTO from(Rating r) {
            RatingDetailDTO dto = new RatingDetailDTO();
            dto.id = r.getId();
            dto.publicacaoId = r.getPublicacao().getId();
            dto.tituloPublicacao = r.getPublicacao().getTitulo();
            dto.rating = r.getValue();
            // Assuming Rating has a createdAt field, otherwise use publication's createdAt
            dto.data = r.getPublicacao().getCreatedAt();
            return dto;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getPublicacaoId() { return publicacaoId; }
        public void setPublicacaoId(Long publicacaoId) { this.publicacaoId = publicacaoId; }
        public String getTituloPublicacao() { return tituloPublicacao; }
        public void setTituloPublicacao(String tituloPublicacao) { this.tituloPublicacao = tituloPublicacao; }
        public int getRating() { return rating; }
        public void setRating(int rating) { this.rating = rating; }
        public LocalDateTime getData() { return data; }
        public void setData(LocalDateTime data) { this.data = data; }
    }

    // Getters and Setters
    public Double getRatingMedio() { return ratingMedio; }
    public void setRatingMedio(Double ratingMedio) { this.ratingMedio = ratingMedio; }
    public List<RatingDetailDTO> getRatings() { return ratings; }
    public void setRatings(List<RatingDetailDTO> ratings) { this.ratings = ratings; }
}
