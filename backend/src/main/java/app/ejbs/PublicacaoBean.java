package app.ejbs;

import app.entities.Publicacao;
import app.entities.User;
import app.exceptions.MyConstraintViolationException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;

@Stateless
public class PublicacaoBean {

    @PersistenceContext
    private EntityManager entityManager;

    public Publicacao create(String titulo, String autor, String areaCientifica, String descricao, String file, String resumo, String createdBy, String createdByName) throws MyConstraintViolationException {
        try {
            Publicacao publicacao = new Publicacao(titulo, autor, areaCientifica, descricao, file, resumo, createdBy, createdByName);
            entityManager.persist(publicacao);
            entityManager.flush();
            return publicacao;
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public Publicacao find(Long id) {
        return entityManager.find(Publicacao.class, id);
    }
}

