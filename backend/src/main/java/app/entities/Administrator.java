package app.entities;

import jakarta.persistence.Entity;

@Entity
public class Administrator extends User{
    public Administrator(String username, String password, String name, String email) {
        super(username, password, name, email);
    }

    public Administrator() {
    }
}
