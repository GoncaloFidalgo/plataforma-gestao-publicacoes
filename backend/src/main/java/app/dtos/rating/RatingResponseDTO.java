package app.dtos.rating;

import java.io.Serializable;

// Usado para devolver { "rating_medio_atualizado": 4.8, ... }
public class RatingResponseDTO implements Serializable {
    private Double ratingAverage;
    private int ratingCount;

    public RatingResponseDTO() {}

    public RatingResponseDTO(double average, int count) {
        this.ratingAverage = average;
        this.ratingCount = count;
    }

    // Getters and Setters

    public Double getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(Double ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}