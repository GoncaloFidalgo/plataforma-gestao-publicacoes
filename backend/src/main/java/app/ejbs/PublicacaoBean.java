package app.ejbs;

import app.entities.*;
import app.exceptions.MyConstraintViolationException;
import app.exceptions.MyEntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Stateless
public class PublicacaoBean {

    @PersistenceContext
    private EntityManager entityManager;

    public Publicacao create(String titulo, String tipo, List<String> authorUsernames, String areaCientifica, String descricao, String file, List<String> tagNames, String creatorUsername) throws MyConstraintViolationException, MyEntityNotFoundException {
        try {
            // Encontrar o criador
            User creator = entityManager.find(User.class, creatorUsername);
            if (creator == null) {
                throw new MyEntityNotFoundException("User '" + creatorUsername + "' not found.");
            }

            // Criar a publicação
            Publicacao publicacao = new Publicacao(titulo, tipo, areaCientifica, descricao, file, creator);

            if (authorUsernames != null) {
                for (String username : authorUsernames) {
                    Colaborador autor = entityManager.find(Colaborador.class, username);
                    if (autor != null) {
                        publicacao.addAutor(autor);
                    }
                }
            }

            if (tagNames != null && !tagNames.isEmpty()) {
                List<Tag> tagsToSet = new ArrayList<>();
                for (String tagName : tagNames) {
                    // Since Tag ID is the name, we try to find it
                    Tag tag = entityManager.find(Tag.class, tagName);
                    if (tag != null) {
                        tagsToSet.add(tag);
                    }
                }
                publicacao.setTags(tagsToSet);
            }
            entityManager.persist(publicacao);
            return publicacao;
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }


    public Publicacao find(Long id) {
        Publicacao p = entityManager.find(Publicacao.class, id);
        if (p != null) {
            // Initializar as coleções
            Hibernate.initialize(p.getTags());
            Hibernate.initialize(p.getComentarios());
            Hibernate.initialize(p.getRatings());
            Hibernate.initialize(p.getHistoricoEdicoes());
            Hibernate.initialize(p.getAutores());
        }
        return p;
    }

    public List<Publicacao> findAll(String tag, String titulo, String autorName, String areaCientifica, String uploaderName, Boolean hidden) {
        StringBuilder jpql = new StringBuilder("SELECT DISTINCT p FROM Publicacao p LEFT JOIN p.tags t LEFT JOIN p.autores a WHERE 1=1");

        if (tag != null && !tag.isBlank()) {
            jpql.append(" AND LOWER(t.name) LIKE LOWER(:tag)");
        }
        if (titulo != null && !titulo.isBlank()) {
            jpql.append(" AND LOWER(p.titulo) LIKE LOWER(:titulo)");
        }
        if (autorName != null && !autorName.isBlank()) {
            jpql.append(" AND LOWER(a.name) LIKE LOWER(:autorName)");
        }
        if (areaCientifica != null && !areaCientifica.isBlank()) {
            jpql.append(" AND LOWER(p.areaCientifica) LIKE LOWER(:areaCientifica)");
        }
        if (uploaderName != null && !uploaderName.isBlank()) {
            jpql.append(" AND LOWER(p.createdBy.name) LIKE LOWER(:uploaderName)");
        }
        if (hidden != null) {
            jpql.append(" AND p.hidden = :hidden");
        }

        jpql.append(" ORDER BY p.createdAt DESC");

        var query = entityManager.createQuery(jpql.toString(), Publicacao.class);

        if (tag != null && !tag.isBlank()) query.setParameter("tag", "%" + tag + "%");
        if (titulo != null && !titulo.isBlank()) query.setParameter("titulo", "%" + titulo + "%");
        if (autorName != null && !autorName.isBlank()) query.setParameter("autorName", "%" + autorName + "%");
        if (areaCientifica != null && !areaCientifica.isBlank()) query.setParameter("areaCientifica", "%" + areaCientifica + "%");
        if (uploaderName != null && !uploaderName.isBlank()) query.setParameter("uploaderName", "%" + uploaderName + "%");
        if (hidden != null) query.setParameter("hidden", hidden);

        List<Publicacao> results = query.getResultList();

        for (Publicacao p : results) {
            Hibernate.initialize(p.getTags());
            Hibernate.initialize(p.getAutores());
            Hibernate.initialize(p.getRatings());
            Hibernate.initialize(p.getComentarios());
        }

        return results;
    }

    public Publicacao update(Long id, String titulo, String autorUsernameToAdd, String areaCientifica, String descricao, String resumo, List<String> tagNames, Boolean hidden, String editorUsername) throws MyEntityNotFoundException {
        var publicacao = entityManager.find(Publicacao.class, id);
        if (publicacao == null) return null;

        User editor = entityManager.find(User.class, editorUsername);
        if (editor == null) throw new MyEntityNotFoundException("Editor not found");

        if (titulo != null) publicacao.setTitulo(titulo);

        // Adicionar novo autor
        if (autorUsernameToAdd != null && !autorUsernameToAdd.isBlank()) {
            Colaborador newAutor = entityManager.find(Colaborador.class, autorUsernameToAdd);
            if (newAutor != null && !publicacao.getAutores().contains(newAutor)) {
                publicacao.addAutor(newAutor);
            }
        }

        if (areaCientifica != null) publicacao.setAreaCientifica(areaCientifica);
        if (descricao != null) publicacao.setDescricao(descricao);
        if (resumo != null) publicacao.setResumo(resumo);
        if (hidden != null) publicacao.setHidden(hidden);

        // Tags
        if (tagNames != null) {
            List<Tag> tagsToSet = new ArrayList<>();
            for (String tagName : tagNames) {
                // Encontrar a tag
                Tag tag = entityManager.find(Tag.class, tagName);

                if (tag != null) {
                    tagsToSet.add(tag);
                }
            }
            publicacao.setTags(tagsToSet);
        }


        HistoricoEdicao history = new HistoricoEdicao("Update details", editor, publicacao);
        publicacao.getHistoricoEdicoes().add(history);
        entityManager.persist(history);

        // Para guardar as alterações
        entityManager.flush();

        // Atualizar as coleções
        Hibernate.initialize(publicacao.getTags());
        Hibernate.initialize(publicacao.getAutores());

        return publicacao;
    }



    public void delete(Long id) {
        var publicacao = entityManager.find(Publicacao.class, id);
        if (publicacao != null) entityManager.remove(publicacao);
    }
}