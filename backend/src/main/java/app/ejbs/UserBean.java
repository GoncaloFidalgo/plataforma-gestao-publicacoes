package app.ejbs;

import app.entities.Administrator;
import app.entities.Colaborador;
import app.entities.Responsavel;
import app.entities.User;
import app.exceptions.MyConstraintViolationException;
import app.security.Hasher;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;

@Stateless
public class UserBean {
    @PersistenceContext
    private EntityManager entityManager;

    public User createUser(String username, String password, String name, String email, Integer role, Boolean active)
            throws MyConstraintViolationException {

        if (entityManager.find(User.class, username) != null) {
            throw new IllegalArgumentException("Username " + username + " already exists");
        }

        User user;
        String hashedPassword = Hasher.hash(password);

        try {
            user = switch (role) {
                case 1 -> new Administrator(username, hashedPassword, name, email);
                case 2 -> new Responsavel(username, hashedPassword, name, email);
                case 3 -> new Colaborador(username, hashedPassword, name, email);
                default -> throw new IllegalArgumentException("Invalid role: " + role);
            };

            if (active != null) {
                user.setActive(active);
            }

            entityManager.persist(user);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }

        return user;
    }

    public User find(String username) {
        return entityManager.find(User.class, username);
    }

    public boolean canLogin(String username, String password) {
        var user = find(username);
        return user != null && Hasher.verify(password, user.getPassword());
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        var user = entityManager.find(User.class, username);
        if (user == null) {
            throw new RuntimeException("User " + username + " not found");
        }
        if (!Hasher.verify(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password incorrect");
        }
        user.setPassword(Hasher.hash(newPassword));
    }

    public String recoverPassword(String email) {
        var user = entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        // Gerar token único
        String token = java.util.UUID.randomUUID().toString();

        // Armazenar token no BD com validade de 1 hora
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));


        return token; // APENAS para testes locais
    }

    public void resetPassword(String token, String newPassword) {

        // Validar formato do token
        try {
            java.util.UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Token inválido");
        }

        var user = entityManager.createQuery(
                        "SELECT u FROM User u", User.class)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado"));

        user.setPassword(Hasher.hash(newPassword));
    }

    public void updateUser(String username, String newUsername, String email, Boolean active) {
        User user = entityManager.find(User.class, username);
        if (user == null) {
            throw new IllegalArgumentException("User " + username + " not found");
        }
        entityManager.lock(user, jakarta.persistence.LockModeType.OPTIMISTIC);

        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(username)) {
            if (entityManager.find(User.class, newUsername) != null) {
                throw new IllegalArgumentException("Username " + newUsername + " already exists");
            }
            user.setUsername(newUsername);
        }

        if (email != null) {
            user.setEmail(email);
        }

        if (active != null) {
            user.setActive(active);
        }
    }





}
