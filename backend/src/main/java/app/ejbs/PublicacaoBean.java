package app.ejbs;

import app.entities.*;
import app.exceptions.MyConstraintViolationException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Stateless
public class PublicacaoBean {

    @PersistenceContext
    private EntityManager entityManager;

    public Publicacao create(String titulo, String autor, String areaCientifica, String descricao, String file, String resumo, String createdBy, String createdByName) throws MyConstraintViolationException {
        try {
            Publicacao publicacao = new Publicacao(titulo, autor, areaCientifica, descricao, file, resumo, createdBy, createdByName);
            entityManager.persist(publicacao);
            return publicacao;
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public Publicacao find(Long id) {
        Publicacao p = entityManager.find(Publicacao.class, id);
        if (p != null) {
            p.getTags().size();
            p.getComentarios().size();
            p.getRatings().size();
            p.getHistoricoEdicoes().size();
        }
        return p;
    }

    public List<Publicacao> findAll(String tag, String titulo, String autor, String areaCientifica, String uploader, Boolean hidden) {
        StringBuilder jpql = new StringBuilder("SELECT DISTINCT p FROM Publicacao p LEFT JOIN p.tags t WHERE 1=1");

        if (tag != null && !tag.isBlank()) {
            jpql.append(" AND LOWER(t.name) LIKE LOWER(:tag)");
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

        if (tag != null && !tag.isBlank()) query.setParameter("tag", "%" + tag + "%");
        if (titulo != null && !titulo.isBlank()) query.setParameter("titulo", "%" + titulo + "%");
        if (autor != null && !autor.isBlank()) query.setParameter("autor", "%" + autor + "%");
        if (areaCientifica != null && !areaCientifica.isBlank()) query.setParameter("areaCientifica", "%" + areaCientifica + "%");
        if (uploader != null && !uploader.isBlank()) query.setParameter("uploader", "%" + uploader + "%");
        if (hidden != null) query.setParameter("hidden", hidden);

        List<Publicacao> results = query.getResultList();

        // FORCE LOAD collections for the search results too
        for (Publicacao p : results) {
            p.getTags().size();
            p.getComentarios().size();
            p.getRatings().size();
            p.getHistoricoEdicoes().size();
        }

        return results;
    }

    public Publicacao update(Long id, String titulo, String autor, String areaCientifica, String descricao, String resumo, String tagsStr, Boolean hidden, String editorUsername) {
        var publicacao = entityManager.find(Publicacao.class, id);
        if (publicacao == null) return null;

        User editor = entityManager.find(User.class, editorUsername);

        if (titulo != null) publicacao.setTitulo(titulo);
        if (autor != null) publicacao.setAutor(autor);
        if (areaCientifica != null) publicacao.setAreaCientifica(areaCientifica);
        if (descricao != null) publicacao.setDescricao(descricao);
        if (resumo != null) publicacao.setResumo(resumo);
        if (hidden != null) publicacao.setHidden(hidden);

        // Handle Tags
        if (tagsStr != null && !tagsStr.isBlank()) {
            Set<Tag> newTags = new HashSet<>();
            String[] tagNames = tagsStr.split(",");
            for (String tagName : tagNames) {
                String trimmedName = tagName.trim();
                if (!trimmedName.isEmpty()) {
                    List<Tag> existingTags = entityManager.createQuery("SELECT t FROM Tag t WHERE t.name = :name", Tag.class)
                            .setParameter("name", trimmedName)
                            .getResultList();

                    if (existingTags.isEmpty()) {
                        Tag newTag = new Tag(trimmedName);
                        entityManager.persist(newTag);
                        newTags.add(newTag);
                    } else {
                        newTags.add(existingTags.get(0));
                    }
                }
            }
            publicacao.setTags(newTags);
        }

        // Add history entry
        HistoricoEdicao history = new HistoricoEdicao("Update details", editor, publicacao);
        publicacao.getHistoricoEdicoes().add(history);

        entityManager.flush();

        publicacao.getTags().size();
        publicacao.getComentarios().size();
        publicacao.getRatings().size();
        publicacao.getHistoricoEdicoes().size();

        return publicacao;
    }

    // Optional: Add Comment method if you implement it later
//    public void addComment(Long publicacaoId, String username, String text) {
//        Publicacao p = entityManager.find(Publicacao.class, publicacaoId);
//        User u = entityManager.find(User.class, username);
//        if (p != null && u != null) {
//            p.addComentario(new Comentario(text, u, p));
//        }
//    }

    public void delete(Long id) {
        var publicacao = entityManager.find(Publicacao.class, id);
        if (publicacao != null) entityManager.remove(publicacao);
    }
}