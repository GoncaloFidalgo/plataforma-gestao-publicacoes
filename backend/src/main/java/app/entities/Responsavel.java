package app.entities;

import jakarta.persistence.Entity;

@Entity
public class Responsavel extends  User{
    public Responsavel(String username, String password, String name, String email) {
        super(username, password, name, email);
    }

    public Responsavel() {
    }
}
