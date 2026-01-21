package app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllResponsaveis",
                query = "SELECT s FROM Responsavel s ORDER BY s.name" // JPQL
        )
})
public class Responsavel extends Colaborador{
    public Responsavel() {
        super();
    }
    public Responsavel(String username, String password, String name, String email) {
        super(username, password, name, email);
    }
}
