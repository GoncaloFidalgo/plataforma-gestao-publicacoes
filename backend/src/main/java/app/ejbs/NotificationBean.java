package app.ejbs;

import app.entities.Comment;
import app.entities.Notification;
import app.entities.Publicacao;
import app.entities.User;
import app.exceptions.MyEntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class NotificationBean {

    @PersistenceContext
    private EntityManager em;

    public void createNotification(String message, User user, Publicacao publicacao, Comment comment) {
        Notification notification = new Notification(message, user, publicacao, comment);
        em.persist(notification);
    }

    public List<Notification> findAllByUser(String username) {
        return em.createNamedQuery("getAllNotificationsByUser", Notification.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<Notification> findUnreadByUser(String username) {
        return em.createNamedQuery("getUnreadNotificationsByUser", Notification.class)
                .setParameter("username", username)
                .getResultList();
    }

    public Notification find(Long id) throws MyEntityNotFoundException {
        Notification notification = em.find(Notification.class, id);
        if (notification == null) {
            throw new MyEntityNotFoundException("Notificação não encontrada");
        }
        return notification;
    }

    public void markAsRead(Long id, String username) throws MyEntityNotFoundException {
        Notification notification = find(id);

        if (!notification.getUser().getUsername().equals(username)) {
            throw new SecurityException("Não pode marcar notificações de outros utilizadores");
        }

        notification.setIsRead(true);
    }

    public void markAllAsRead(String username) {
        em.createQuery("UPDATE Notification n SET n.isRead = true WHERE n.user.username = :username AND n.isRead = false")
                .setParameter("username", username)
                .executeUpdate();
    }

    public long countUnread(String username) {
        return em.createQuery("SELECT COUNT(n) FROM Notification n WHERE n.user.username = :username AND n.isRead = false", Long.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    public void delete(Long id, String username) throws MyEntityNotFoundException {
        Notification notification = find(id);

        if (!notification.getUser().getUsername().equals(username)) {
            throw new SecurityException("Não pode apagar notificações de outros utilizadores");
        }

        em.remove(notification);
    }
}