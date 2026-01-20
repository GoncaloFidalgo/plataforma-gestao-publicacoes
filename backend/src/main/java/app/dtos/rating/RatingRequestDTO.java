package app.dtos.rating;

import java.io.Serializable;

// Usado para receber o { "rating": "5" }
public class RatingRequestDTO implements Serializable {
    private int rating;

    public RatingRequestDTO() {}

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}