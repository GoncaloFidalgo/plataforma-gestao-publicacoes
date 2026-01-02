package app.ejbs;

import app.entities.Administrator;
import app.entities.User;
import app.exceptions.MyConstraintViolationException;
import app.security.Hasher;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;

import java.util.List;

@Stateless
public class AdministratorBean {
    @PersistenceContext
    private EntityManager entityManager;

    public Administrator create(String username, String password, String name, String email) throws MyConstraintViolationException {
        if (entityManager.find(User.class, username) != null) {
            throw new IllegalArgumentException("Username " + username + " already exists");
        }

        Administrator administrator = new Administrator();

        try{
            administrator = new Administrator(username, Hasher.hash(password), name, email);
            entityManager.persist(administrator);
            entityManager.flush();
        }catch (ConstraintViolationException e){
            throw new MyConstraintViolationException(e);
        }

        return administrator;
    }

    public Administrator find(String username) {
        var administrator = entityManager.find(Administrator.class, username);
        if (administrator == null) {
            throw new RuntimeException("Administrator " + username + " not found");
        }
        return administrator;
    }

    public List<Administrator> findAll() {
        return entityManager.createQuery("SELECT a FROM Administrator a ORDER BY a.name", Administrator.class).getResultList();
    }

    public void update(String username, String password, String name, String email) {
        Administrator admin = entityManager.find(Administrator.class, username);
        if (admin == null) {
            System.err.println("ERROR_ADMIN_NOT_FOUND: " + username);
            return;
        }
        entityManager.lock(admin, LockModeType.OPTIMISTIC);

        if(password != null && !password.isBlank()){
            if(!Hasher.verify(password, admin.getPassword())){
                admin.setPassword(Hasher.hash(password));

            }
        }

        admin.setName(name);
        admin.setEmail(email);
    }

    public void delete(String username) {
        Administrator admin = entityManager.find(Administrator.class, username);
        if (admin == null) {
            throw new RuntimeException("Administrator " + username + " not found");
        }
        entityManager.remove(admin);
    }
}
