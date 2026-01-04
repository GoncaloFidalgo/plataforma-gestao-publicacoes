package app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@NamedQueries({
        @NamedQuery(
                name = "getAllTags",
                query = "SELECT t FROM Tag t ORDER BY t.name"
        )
})
public class Tag implements Serializable {

    @Id
    @NotBlank
    private String name;

    private String description;

    @Column(name = "scientific_area")
    private String scientificArea;

    private Boolean hidden = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Relação inversa com Publicações
    @ManyToMany(mappedBy = "tags")
    private Set<Publicacao> publicacoes = new HashSet<>();

    // Relação com Utilizadores (Subscritores)
    @ManyToMany
    @JoinTable(
            name = "tag_subscribers",
            joinColumns = @JoinColumn(name = "tag_name", referencedColumnName = "name"),
            inverseJoinColumns = @JoinColumn(name = "user_username", referencedColumnName = "username")
    )
    private Set<User> subscribers = new HashSet<>();

    public Tag() {
    }

    public Tag(String name, String description, String scientificArea) {
        this.name = name;
        this.description = description;
        this.scientificArea = scientificArea;
        this.createdAt = LocalDateTime.now();
        this.hidden = false;
    }

    // Getters e Setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getScientificArea() { return scientificArea; }
    public void setScientificArea(String scientificArea) { this.scientificArea = scientificArea; }

    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<Publicacao> getPublicacoes() { return publicacoes; }
    public void setPublicacoes(Set<Publicacao> publicacoes) { this.publicacoes = publicacoes; }

    public Set<User> getSubscribers() { return subscribers; }
    public void setSubscribers(Set<User> subscribers) { this.subscribers = subscribers; }
}