package app.ejbs;

import app.entities.PublicationType;
import app.entities.ScientificArea;
import app.exceptions.MyEntityExistsException;
import app.exceptions.MyEntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ReferenceBean {

    @PersistenceContext
    private EntityManager em;

    // --- PUBLICATION TYPES ---
    public void createType(String name) throws MyEntityExistsException {
        long count = em.createQuery("SELECT COUNT(t) FROM PublicationType t WHERE t.name = :name", Long.class)
                .setParameter("name", name).getSingleResult();

        if (count > 0) {
            throw new MyEntityExistsException("Type '" + name + "' already exists.");
        }
        em.persist(new PublicationType(name));
    }

    public List<PublicationType> findAllTypes() {
        return em.createNamedQuery("getAllPublicationTypes", PublicationType.class).getResultList();
    }

    public void deleteType(Long id) throws MyEntityNotFoundException {
        PublicationType t = em.find(PublicationType.class, id);
        if (t == null) throw new MyEntityNotFoundException("Type not found.");
        em.remove(t);
    }

    // --- SCIENTIFIC AREAS ---

    public void createArea(String name) throws MyEntityExistsException {
        long count = em.createQuery("SELECT COUNT(s) FROM ScientificArea s WHERE s.name = :name", Long.class)
                .setParameter("name", name).getSingleResult();

        if (count > 0) {
            throw new MyEntityExistsException("Area '" + name + "' already exists.");
        }
        em.persist(new ScientificArea(name));
    }

    public List<ScientificArea> findAllAreas() {
        return em.createNamedQuery("getAllScientificAreas", ScientificArea.class).getResultList();
    }

    public void deleteArea(Long id) throws MyEntityNotFoundException {
        ScientificArea a = em.find(ScientificArea.class, id);
        if (a == null) throw new MyEntityNotFoundException("Area not found.");
        em.remove(a);
    }
}