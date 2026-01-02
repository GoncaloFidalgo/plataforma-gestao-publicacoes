package app.entities;

import jakarta.persistence.Entity;

@Entity
public class Colaborador extends  User{
    public Colaborador(String username, String password, String name, String email) {
        super(username, password, name, email);
    }

    public Colaborador() {
    }
}
