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

    public java.util.List<Publicacao> findAll(String tags, String titulo, String autor, String areaCientifica, String uploader, Boolean hidden) {
        StringBuilder jpql = new StringBuilder("SELECT p FROM Publicacao p WHERE 1=1");

        if (tags != null && !tags.isBlank()) {
            jpql.append(" AND LOWER(p.tags) LIKE LOWER(:tags)");
        }
        if (titulo != null && !titulo.isBlank()) {
            jpql.append(" AND LOWER(p.titulo) LIKE LOWER(:titulo)");
        }
        if (autor != null && !autor.isBlank()) {
            jpql.append(" AND LOWER(p.autor) LIKE LOWER(:autor)");
        }
        if (areaCientifica != null && !areaCientifica.isBlank()) {
            jpql.append(" AND LOWER(p.areaCientifica) LIKE LOWER(:areaCientifica)");
        }
        if (uploader != null && !uploader.isBlank()) {
            jpql.append(" AND LOWER(p.createdByName) LIKE LOWER(:uploader)");
        }
        if (hidden != null) {
            jpql.append(" AND p.hidden = :hidden");
        }

        var query = entityManager.createQuery(jpql.toString(), Publicacao.class);

        if (tags != null && !tags.isBlank()) query.setParameter("tags", "%" + tags + "%");
        if (titulo != null && !titulo.isBlank()) query.setParameter("titulo", "%" + titulo + "%");
        if (autor != null && !autor.isBlank()) query.setParameter("autor", "%" + autor + "%");
        if (areaCientifica != null && !areaCientifica.isBlank()) query.setParameter("areaCientifica", "%" + areaCientifica + "%");
        if (uploader != null && !uploader.isBlank()) query.setParameter("uploader", "%" + uploader + "%");
        if (hidden != null) query.setParameter("hidden", hidden);

        return query.getResultList();
    }

    public Publicacao update(Long id, String titulo, String autor, String areaCientifica, String descricao, String resumo, String tags, Boolean hidden) {
        var publicacao = entityManager.find(Publicacao.class, id);

        if (publicacao == null) {
            return null;
        }

        // Verifica cada campo. Se não for nulo, atualiza.
        if (titulo != null && !titulo.isBlank()) {
            publicacao.setTitulo(titulo);
        }
        if (autor != null && !autor.isBlank()) {
            publicacao.setAutor(autor);
        }
        if (areaCientifica != null && !areaCientifica.isBlank()) {
            publicacao.setAreaCientifica(areaCientifica);
        }
        if (descricao != null && !descricao.isBlank()) {
            publicacao.setDescricao(descricao);
        }
        if (resumo != null && !resumo.isBlank()) {
            publicacao.setResumo(resumo);
        }
        if (tags != null && !tags.isBlank()) {
            publicacao.setTags(tags);
        }
        if (hidden != null) {
            publicacao.setHidden(hidden);
        }


        entityManager.flush();
        return publicacao;
    }

    public void delete(Long id) {
        var publicacao = entityManager.find(Publicacao.class, id);
        if (publicacao != null) {
            entityManager.remove(publicacao);
        } else {
            throw new RuntimeException("Publicação não encontrada");
        }
    }
}

