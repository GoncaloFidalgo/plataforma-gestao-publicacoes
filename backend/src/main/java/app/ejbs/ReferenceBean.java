package app.ejbs;

import app.entities.PublicationType;
import app.entities.ScientificArea;
import app.exceptions.MyConstraintViolationException;
import app.exceptions.MyEntityExistsException;
import app.exceptions.MyEntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

import java.util.List;

@Stateless
public class ReferenceBean {

    @PersistenceContext
    private EntityManager em;

    // --- PUBLICATION TYPES ---
    public PublicationType createType(String name) throws MyEntityExistsException {
        long count = em.createQuery("SELECT COUNT(t) FROM PublicationType t WHERE t.name = :name", Long.class)
                .setParameter("name", name).getSingleResult();

        if (count > 0) {
            throw new MyEntityExistsException("Type '" + name + "' already exists.");
        }
        PublicationType type = new PublicationType(name);
        em.persist(type);
        em.flush();
        return type;
    }

    public List<PublicationType> findAllTypes() {
        return em.createNamedQuery("getAllPublicationTypes", PublicationType.class).getResultList();
    }

    public void deleteType(Long id) throws MyEntityNotFoundException, MyConstraintViolationException {
        PublicationType t = em.find(PublicationType.class, id);
        if (t == null) throw new MyEntityNotFoundException("Type not found.");

        try {
            em.remove(t);
            em.flush();
        } catch (PersistenceException e) {
            throw new MyConstraintViolationException("Cannot delete Type '" + t.getName() + "' because it is associated with existing Publications.");
        }
    }

    // --- SCIENTIFIC AREAS ---

    public ScientificArea createArea(String name) throws MyEntityExistsException {
        long count = em.createQuery("SELECT COUNT(s) FROM ScientificArea s WHERE s.name = :name", Long.class)
                .setParameter("name", name).getSingleResult();

        if (count > 0) {
            throw new MyEntityExistsException("Area '" + name + "' already exists.");
        }
        ScientificArea area = new ScientificArea(name);
        em.persist(area);
        em.flush();
        return area;
    }

    public List<ScientificArea> findAllAreas() {
        return em.createNamedQuery("getAllScientificAreas", ScientificArea.class).getResultList();
    }

    public void deleteArea(Long id) throws MyEntityNotFoundException, MyConstraintViolationException {
        ScientificArea a = em.find(ScientificArea.class, id);
        if (a == null) throw new MyEntityNotFoundException("Area not found.");

        try {
            em.remove(a);
            em.flush();
        } catch (PersistenceException e) {
            throw new MyConstraintViolationException("Cannot delete Area '" + a.getName() + "' because it is associated with existing Publications.");
        }
    }
}