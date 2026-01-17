package app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllColaboradores",
                query = "SELECT s FROM Colaborador s ORDER BY s.name" // JPQL
        )
})
public class Colaborador extends User{
    public Colaborador() {
        super();
    }
    public Colaborador(String username, String password, String name, String email) {
        super(username, password, name, email);
    }
}
