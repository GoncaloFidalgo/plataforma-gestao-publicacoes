package app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users",   uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)

public class User extends Versionable implements Serializable {
    @Id
    @NotBlank
    private String username;
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    @NotBlank
    private String name;
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    @Email
    @NotBlank
    private String email;
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    @NotBlank
    private String password;
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    private Boolean active;
    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }

    private String resetToken;
    public String getResetToken() {
        return resetToken;
    }
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    private LocalDateTime resetTokenExpiry;
    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    @OneToMany(mappedBy = "user")
    private List<Comentario> comentarios;
    public List<Comentario> getComentarios() { return comentarios; }
    public void setComentarios(List<Comentario> comentarios) { this.comentarios = comentarios; }

    @OneToMany(mappedBy = "user")
    private List<Rating> ratings;
    public List<Rating> getRatings() { return ratings; }
    public void setRatings(List<Rating> ratings) { this.ratings = ratings; }

    @ManyToMany(mappedBy = "subscribers")
    private Set<Tag> tagsSubscritas;
    public Set<Tag> getTagsSubscritas() { return tagsSubscritas; }
    public void setTagsSubscritas(Set<Tag> tagsSubscritas) { this.tagsSubscritas = tagsSubscritas; }

    public User() {}

    public User(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.active = true;

        comentarios = new ArrayList<>();
        ratings = new ArrayList<>();
        tagsSubscritas = new HashSet<>();

    }

}