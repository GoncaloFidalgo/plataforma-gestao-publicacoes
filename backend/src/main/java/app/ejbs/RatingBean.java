package app.ejbs;

import app.entities.Publicacao;
import app.entities.Rating;
import app.entities.User;
import app.exceptions.MyEntityExistsException;
import app.exceptions.MyEntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class RatingBean {

    @PersistenceContext
    private EntityManager em;

    public double create(Long publicacaoId, String username, int value) throws MyEntityNotFoundException, MyEntityExistsException {
        User user = em.find(User.class, username);
        if (user == null) throw new MyEntityNotFoundException("User not found");

        Publicacao publicacao = em.find(Publicacao.class, publicacaoId);
        if (publicacao == null) throw new MyEntityNotFoundException("Publicacao not found");

        try {
            Rating existing = em.createQuery("SELECT r FROM Rating r WHERE r.user.username = :user AND r.publicacao.id = :pub", Rating.class)
                    .setParameter("user", username)
                    .setParameter("pub", publicacaoId)
                    .getSingleResult();
            if (existing != null) {
                throw new MyEntityExistsException("Utilizador já avaliou esta publicação.");
            }
        } catch (NoResultException e) {
        }

        Rating rating = new Rating(value, user, publicacao);
        publicacao.addRating(rating);
        em.persist(rating);
        em.flush();

        return calculateAverage(publicacao);
    }

    public double update(Long publicacaoId, Long ratingId, int newValue, String username) throws MyEntityNotFoundException {
        Rating rating = em.find(Rating.class, ratingId);
        if (rating == null) throw new MyEntityNotFoundException("Rating not found");

        // Verifica se o rating pertence mesmo àquela publicação
        if (!rating.getPublicacao().getId().equals(publicacaoId)) {
            throw new IllegalArgumentException("Rating does not belong to this publication");
        }

        // Apenas o dono pode editar
        if (!rating.getUser().getUsername().equals(username)) {
            throw new SecurityException("You can only edit your own rating");
        }

        rating.setValue(newValue);
        em.flush();

        return calculateAverage(rating.getPublicacao());
    }

    public double delete(Long publicacaoId, Long ratingId) throws MyEntityNotFoundException {
        Rating rating = em.find(Rating.class, ratingId);
        if (rating == null) throw new MyEntityNotFoundException("Rating not found");

        if (!rating.getPublicacao().getId().equals(publicacaoId)) {
            throw new IllegalArgumentException("Rating does not belong to this publication");
        }

        Publicacao p = rating.getPublicacao();
        p.getRatings().remove(rating);
        em.remove(rating);
        em.flush();

        return calculateAverage(p);
    }

    public double calculateAverage(Publicacao p) {
        if (p.getRatings().isEmpty()) return 0.0;

        // Refresh para garantir que temos os dados mais recentes
        em.refresh(p);

        double avg = p.getRatings().stream()
                .mapToInt(Rating::getValue)
                .average()
                .orElse(0.0);

        // Arredondar a 1 casa decimal
        return Math.round(avg * 10.0) / 10.0;
    }

    public Rating findRatingByUser(Long pubId, String username) {
        try {
            return em.createQuery(
                            "SELECT r FROM Rating r WHERE r.publicacao.id = :pubId AND r.user.username = :username",
                            Rating.class)
                    .setParameter("pubId", pubId)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public int getCount(Long publicacaoId) {
        Publicacao p = em.find(Publicacao.class, publicacaoId);
        return p != null ? p.getRatings().size() : 0;
    }

    public List<Rating> findRatingsWithPublicationsByUser(String username) throws MyEntityNotFoundException {
        User user = em.find(User.class, username);
        if (user == null) {
            throw new MyEntityNotFoundException("User not found");
        }

        List<Rating> ratings = em.createQuery(
            "SELECT r FROM Rating r JOIN FETCH r.publicacao WHERE r.user.username = :username ORDER BY r.id DESC",
            Rating.class)
            .setParameter("username", username)
            .getResultList();

        return ratings;
    }
}