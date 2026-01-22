package app.ejbs;

import app.dtos.tags.TagDTO;
import app.entities.Publicacao;
import app.entities.Tag;
import app.entities.User;
import app.exceptions.MyEntityExistsException;
import app.exceptions.MyEntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.hibernate.Hibernate;

import java.util.List;

@Stateless
public class TagBean {

    @PersistenceContext
    private EntityManager em;

    public void create(String name, String description, String scientificArea) throws MyEntityExistsException {
        Tag existing = em.find(Tag.class, name);
        if (existing != null) {
            throw new MyEntityExistsException("Tag com o nome '" + name + "' já existe.");
        }

        Tag tag = new Tag(name, description, scientificArea);
        em.persist(tag);
    }

    public List<Tag> findAll(Boolean hidden) {
        List<Tag> tags;
        if (hidden != null) {
            tags = em.createQuery("SELECT t FROM Tag t WHERE t.hidden = :hidden ORDER BY t.name", Tag.class)
                    .setParameter("hidden", hidden)
                    .getResultList();
        } else {
            tags = em.createNamedQuery("getAllTags", Tag.class).getResultList();
        }

        // Initialize lazy collections while Session is still open.
        // This allows tag.getPublicacoes().size() to work in the DTO.
        for (Tag t : tags) {
            Hibernate.initialize(t.getPublicacoes());
            Hibernate.initialize(t.getSubscribers());
        }

        return tags;
    }

    public Tag find(String name) throws MyEntityNotFoundException {
        Tag tag = em.find(Tag.class, name);
        if (tag == null) {
            throw new MyEntityNotFoundException("Tag com nome '" + name + "' não encontrada.");
        }

        Hibernate.initialize(tag.getPublicacoes());
        Hibernate.initialize(tag.getSubscribers());

        return tag;
    }

    public void update(String name, String description, String scientificArea, Boolean hidden) throws MyEntityNotFoundException {
        Tag tag = find(name);

        if (description != null) tag.setDescription(description);
        if (scientificArea != null) tag.setScientificArea(scientificArea);
        if (hidden != null) tag.setHidden(hidden);
    }

    public void delete(String name) throws MyEntityNotFoundException {
        Tag tag = find(name);

        for (Publicacao p : tag.getPublicacoes()) {
            p.getTags().remove(tag);
        }
        tag.getSubscribers().clear();

        em.remove(tag);
    }

    public void subscribeTags(String username, List<String> tagNames)
            throws MyEntityNotFoundException, MyEntityExistsException {

        if (tagNames == null || tagNames.isEmpty()) {
            throw new IllegalArgumentException("Lista de tags vazia.");
        }

        User user = em.find(User.class, username);
        if (user == null) throw new MyEntityNotFoundException("Utilizador não existe.");

        for (String tagName : tagNames) {
            Tag tag = em.find(Tag.class, tagName);
            if (tag == null) throw new MyEntityNotFoundException("Tag '" + tagName + "' não existe.");

            if (tag.getSubscribers().contains(user)) {
                throw new MyEntityExistsException("Já estás subscrito na tag '" + tagName + "'.");
            }

            tag.getSubscribers().add(user);
            user.getTagsSubscritas().add(tag);
        }

        em.merge(user);
    }

    public void unsubscribeTags(String username, List<String> tagNames)
            throws MyEntityNotFoundException {

        if (tagNames == null || tagNames.isEmpty()) {
            throw new IllegalArgumentException("Lista de tags vazia.");
        }

        User user = em.find(User.class, username);
        if (user == null) throw new MyEntityNotFoundException("Utilizador não existe.");

        for (String tagName : tagNames) {
            Tag tag = em.find(Tag.class, tagName);
            if (tag == null) throw new MyEntityNotFoundException("Tag '" + tagName + "' não existe.");

            tag.getSubscribers().remove(user);
            user.getTagsSubscritas().remove(tag);
        }

        em.merge(user);
    }

    public List<TagDTO> getMySubscribedTags(String username)
            throws MyEntityNotFoundException {

        User user = em.find(User.class, username);
        if (user == null) {
            throw new MyEntityNotFoundException("Utilizador '" + username + "' não existe.");
        }

        var rows = em.createQuery(
                        "SELECT t, COUNT(sAll) " +
                                "FROM Tag t " +
                                "JOIN t.subscribers sMe " +
                                "JOIN t.subscribers sAll " +
                                "WHERE sMe.username = :username " +
                                "GROUP BY t",
                        Object[].class
                )
                .setParameter("username", username)
                .getResultList();

        return rows.stream().map(r -> {
            Tag tag = (Tag) r[0];
            Long count = (Long) r[1];

            TagDTO dto = TagDTO.from(tag);
            dto.setSubscritores_count(count.intValue()); // evita lazy
            return dto;
        }).toList();
    }

    public List<TagDTO> getUserSubscribedTags(String username)
            throws MyEntityNotFoundException {

        User user = em.find(User.class, username);
        if (user == null) {
            throw new MyEntityNotFoundException("Utilizador '" + username + "' não existe.");
        }

        var rows = em.createQuery(
                        "SELECT t, COUNT(sAll) " +
                                "FROM Tag t " +
                                "JOIN t.subscribers sMe " +
                                "JOIN t.subscribers sAll " +
                                "WHERE sMe.username = :username " +
                                "GROUP BY t",
                        Object[].class
                ).setParameter("username", username)
                .getResultList();

        return rows.stream().map(r -> {
            Tag tag = (Tag) r[0];
            Long count = (Long) r[1];

            TagDTO dto = TagDTO.from(tag);
            dto.setSubscritores_count(count.intValue()); // evita lazy
            return dto;
        }).toList();
    }

    public List<TagDTO> findAllWithCounts(Boolean hidden) {

        String jpql =
                "SELECT t, COUNT(DISTINCT p), COUNT(DISTINCT s) " +
                        "FROM Tag t " +
                        "LEFT JOIN t.publicacoes p " +
                        "LEFT JOIN t.subscribers s " +
                        (hidden != null ? "WHERE t.hidden = :hidden " : "") +
                        "GROUP BY t " +
                        "ORDER BY t.name";

        var q = em.createQuery(jpql, Object[].class);
        if (hidden != null) {
            q.setParameter("hidden", hidden);
        }

        return q.getResultList().stream().map(row -> {
            Tag tag = (Tag) row[0];
            Long pubCount = (Long) row[1];
            Long subCount = (Long) row[2];

            return new TagDTO(
                    tag.getName(),
                    tag.getDescription(),
                    tag.getScientificArea(),
                    tag.getHidden(),
                    tag.getCreatedAt(),
                    pubCount.intValue(),
                    subCount.intValue()
            );
        }).toList();
    }




}