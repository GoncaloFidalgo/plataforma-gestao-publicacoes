package pt.ipleiria.estg.dei.ei.dae.plataforma.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users",   uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
public class User {
    @Id
    @NotBlank
    private String username;
    public void setUsername(String _username) {
        this.username = _username;
    }
    public String getUsername() {
        return username;
    }

    @NotBlank
    private String password;
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
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
    private String name;
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public User() {
    }
    public User(String username, String password, String email, String name) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
    }
}
