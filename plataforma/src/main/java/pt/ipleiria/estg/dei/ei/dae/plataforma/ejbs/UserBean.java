package pt.ipleiria.estg.dei.ei.dae.plataforma.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.plataforma.entities.User;

@Stateless
public class UserBean {
    @PersistenceContext
    private EntityManager entityManager;
    public void create(String username, String password, String email, String name) {
        User user = new User(username, password, email, name);
        entityManager.persist(user);
    }
}
