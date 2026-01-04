package app.ejbs;

import app.entities.Publicacao;
import app.entities.Tag;
import app.exceptions.MyEntityExistsException;
import app.exceptions.MyEntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

        // Remove relationships
        for (Publicacao p : tag.getPublicacoes()) {
            p.getTags().remove(tag);
        }
        tag.getSubscribers().clear();

        em.remove(tag);
    }
}