package app.ejbs;

import app.entities.Administrator;
import app.entities.Colaborador;
import app.entities.Responsavel;
import app.entities.User;
import app.exceptions.MyConstraintViolationException;
import app.security.Hasher;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class UserBean {
    @PersistenceContext
    private EntityManager entityManager;

    @EJB
    private EmailBean emailBean;

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

    public void recoverPassword(String email) {
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

        String subject = "Recuperacao de Palavra-passe";
        String text = String.format(
                "Ola %s,\n\n" +
                        "Recebemos um pedido para redefinir a sua palavra-passe.\n\n" +
                        "Token de recuperacao: %s\n\n" +
                        "Este token e valido por 1 hora.\n\n" +
                        "Se nao solicitou esta alteracao, ignore este email.\n\n" +
                        "Cumprimentos,\nEquipa de Suporte",
                user.getName(),
                token
        );

        emailBean.send(email, subject, text);
    }

    public void resetPassword(String token, String newPassword) {
        // Validar formato do token
        try {
            java.util.UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Token inválido");
        }

        var user = entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.resetToken = :token", User.class)
                .setParameter("token", token)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado"));

        // Verificar se o token ainda está válido
        if (user.getResetTokenExpiry() == null || LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw new RuntimeException("Token expirado");
        }

        user.setPassword(Hasher.hash(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
    }


    public void updateUser(String username, String newName, String email, Boolean active) {
        User user = entityManager.find(User.class, username);
        if (user == null) {
            throw new IllegalArgumentException("User " + username + " not found");
        }
        entityManager.lock(user, jakarta.persistence.LockModeType.OPTIMISTIC);

        if (newName != null) {
            user.setName(newName);
        }

        if (email != null) {
            user.setEmail(email);
        }

        if (active != null) {
            user.setActive(active);
        }
    }

    public void updatePersonalData(String username, String newName, String email) {
        User user = entityManager.find(User.class, username);
        if (user == null) {
            throw new IllegalArgumentException("User " + username + " not found");
        }
        entityManager.lock(user, jakarta.persistence.LockModeType.OPTIMISTIC);

        if (newName != null) {
            user.setName(newName);
        }

        if (email != null) {
            user.setEmail(email);
        }
    }

    public void deleteUser(String username) {
        User user = entityManager.find(User.class, username);
        if (user == null) {
            throw new RuntimeException("Utilizador não encontrado");
        }
        entityManager.remove(user);
    }

    public void updateUserStatus(String username, Boolean active, String motive) {
        User user = entityManager.find(User.class, username);
        if (user == null) {
            throw new RuntimeException("Utilizador não encontrado");
        }
        user.setActive(active);
    }

    public void updateUserRole(String username, Integer role) {
        User user = entityManager.find(User.class, username);
        if (user == null) {
            throw new EntityNotFoundException("Utilizador não encontrado");
        }

        // Validate role
        if (role == null || role < 1 || role > 3) {
            throw new IllegalArgumentException("Role inválida. Deve ser 1 (Administrator), 2 (Responsavel) ou 3 (Colaborador)");
        }

        String currentType = Hibernate.getClass(user).getSimpleName();
        String targetType = switch (role) {
            case 1 -> "Administrator";
            case 2 -> "Responsavel";
            case 3 -> "Colaborador";
            default -> throw new IllegalArgumentException("Role inválida");
        };

        if (currentType.equals(targetType)) {
            return;
        }

        entityManager.flush();
        // Remove o objeto da cache do Hibernate para não haver problemas com a versão antiga do user depois de atualizar a role.
        entityManager.detach(user);
        entityManager.createNativeQuery("UPDATE users SET DTYPE = ? WHERE username = ?")
                .setParameter(1, targetType)
                .setParameter(2, username)
                .executeUpdate();
    }

    public List<User> findAll() {
        return entityManager.createNamedQuery("getAllUsers", User.class).getResultList();
    }

    public List<Administrator> findAllAdministrators() {
        return entityManager.createNamedQuery("getAllAdministrators", Administrator.class).getResultList();
    }

    public List<Responsavel> findAllResponsaveis() {
        return entityManager.createNamedQuery("getAllResponsaveis", Responsavel.class).getResultList();
    }

    public List<Colaborador> findAllColaboradores() {
        return entityManager.createNamedQuery("getAllColaboradores", Colaborador.class).getResultList();
    }
}
