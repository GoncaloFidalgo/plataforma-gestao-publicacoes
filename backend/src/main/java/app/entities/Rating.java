package app.entities;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ratings")
public class Rating implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int value; // e.g., 1 to 5

    @ManyToOne
    @JoinColumn(name = "user_username", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "publicacao_id", nullable = false)
    private Publicacao publicacao;

    public Rating() {}

    public Rating(int value, User user, Publicacao publicacao) {
        this.value = value;
        this.user = user;
        this.publicacao = publicacao;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Publicacao getPublicacao() { return publicacao; }
    public void setPublicacao(Publicacao publicacao) { this.publicacao = publicacao; }
}