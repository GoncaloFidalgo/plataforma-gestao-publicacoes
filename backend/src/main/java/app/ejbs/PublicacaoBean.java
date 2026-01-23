package app.ejbs;

import app.entities.*;
import app.exceptions.MyConstraintViolationException;
import app.exceptions.MyEntityNotFoundException;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.Hibernate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

@Stateless
public class PublicacaoBean {

    @PersistenceContext
    private EntityManager entityManager;

    @EJB
    private EmailBean emailBean;

    private static final String UPLOAD_DIR = System.getProperty("java.io.tmpdir") + "/publications/";

    public Publicacao create(String titulo, Long tipoId, List<String> authorUsernames, Long areaId, String descricao, InputStream fileContent, String extension, List<String> tagNames, String creatorUsername) throws MyConstraintViolationException, MyEntityNotFoundException, IOException {
        // Guardar ficheiro
        String storedFileName = null;
        if (fileContent != null) {
            String ext = extension != null ? extension : "";
            storedFileName = UUID.randomUUID().toString() + ext;

            java.nio.file.Path path = Paths.get(UPLOAD_DIR);

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            Files.copy(fileContent, path.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);
        }
        try {
            // Encontrar o criador
            User creator = entityManager.find(User.class, creatorUsername);
            if (creator == null) {
                throw new MyEntityNotFoundException("User '" + creatorUsername + "' not found.");
            }

            // Criar a publicação
            PublicationType type = entityManager.find(PublicationType.class, tipoId);
            if (type == null) throw new MyEntityNotFoundException("Type with ID " + tipoId + " not found.");

            ScientificArea area = entityManager.find(ScientificArea.class, areaId);
            if (area == null) throw new MyEntityNotFoundException("Area with ID " + areaId + " not found.");

            // Create
            Publicacao publicacao = new Publicacao(titulo, type, area, descricao, storedFileName, creator);
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
                    Tag tag = entityManager.find(Tag.class, tagName);
                    if (tag != null) {
                        tagsToSet.add(tag);
                    }
                }
                publicacao.setTags(tagsToSet);
            }

            entityManager.persist(publicacao);

            // enviar notificações
            if (tagNames != null && !tagNames.isEmpty()) {
                for (Tag tag : publicacao.getTags()) {
                    if (!tag.getSubscribers().isEmpty()) {
                        String publicationUrl = "http://localhost:4200/publications/" + publicacao.getId();

                        emailBean.notifyTagSubscribersWithUrl(
                                tag.getSubscribers(),
                                tag.getName(),
                                "Nova publicacao",
                                publicacao.getTitulo(),
                                publicationUrl
                        );
                    }
                }
            }

            return publicacao;
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
    }

    public File getFile(Long id) {
        Publicacao p = find(id);
        if (p == null || p.getFilename() == null) return null;

        File file = new File(UPLOAD_DIR, p.getFilename());
        return file.exists() ? file : null;
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
        if (areaCientifica != null && !areaCientifica.isBlank()) {
            jpql.append(" AND LOWER(p.areaCientifica.name) LIKE LOWER(:areaCientifica)");
        }
        if (tag != null && !tag.isBlank()) {
            jpql.append(" AND LOWER(t.name) LIKE LOWER(:tag)");
        }
        if (titulo != null && !titulo.isBlank()) {
            jpql.append(" AND LOWER(p.titulo) LIKE LOWER(:titulo)");
        }
        if (autorName != null && !autorName.isBlank()) {
            jpql.append(" AND LOWER(a.name) LIKE LOWER(:autorName)");
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
        if (areaCientifica != null && !areaCientifica.isBlank())
            query.setParameter("areaCientifica", "%" + areaCientifica + "%");
        if (uploaderName != null && !uploaderName.isBlank())
            query.setParameter("uploaderName", "%" + uploaderName + "%");
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

    public Publicacao update(Long id, String titulo, Long tipoId, Long areaId, String descricao,
                             List<String> authorUsernames, List<String> tagNames, Boolean hidden,
                             InputStream fileContent, String extension, String editorUsername)
            throws MyEntityNotFoundException, IOException {

        Publicacao p = entityManager.find(Publicacao.class, id);
        if (p == null) throw new MyEntityNotFoundException("Publicacao not found.");

        User editor = entityManager.find(User.class, editorUsername);
        if (editor == null) throw new MyEntityNotFoundException("Editor not found.");

        // Metadata Updates
        if (titulo != null) p.setTitulo(titulo);
        if (descricao != null) p.setDescricao(descricao);
        if (hidden != null) p.setHidden(hidden);

        if (tipoId != null) {
            PublicationType type = entityManager.find(PublicationType.class, tipoId);
            if (type != null) p.setTipo(type);
        }
        if (areaId != null) {
            ScientificArea area = entityManager.find(ScientificArea.class, areaId);
            if (area != null) p.setAreaCientifica(area);
        }
        if (authorUsernames != null) {
            p.getAutores().clear();
            for (String username : authorUsernames) {
                Colaborador autor = entityManager.find(Colaborador.class, username);
                if (autor != null) p.addAutor(autor);
            }
        }
        if (tagNames != null) {
            p.getTags().clear();
            for (String tagName : tagNames) {
                Tag tag = entityManager.find(Tag.class, tagName);
                if (tag != null) p.addTag(tag);
            }
        }

        if (fileContent != null) {
            // 1. Delete old file if exists
            if (this.getFile(id) != null) {
                File oldFile = this.getFile(id);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            // 2. Save new file
            String ext = extension != null ? extension : "";
            String newFileName = UUID.randomUUID().toString() + ext;

            java.nio.file.Path path = Paths.get(UPLOAD_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Files.copy(fileContent, path.resolve(newFileName), StandardCopyOption.REPLACE_EXISTING);

            // 3. Update entity reference
            p.setFilename(newFileName);
        }

        for (Tag tag : p.getTags()) {
            if (!tag.getSubscribers().isEmpty()) {
                emailBean.notifyTagSubscribers(
                    tag.getSubscribers(),
                    tag.getName(),
                    "Publicacao editada",
                    p.getTitulo()
                );
            }
        }

        HistoricoEdicao history = new HistoricoEdicao("Update details", editor, p);
        p.getHistoricoEdicoes().add(history);
        entityManager.persist(history);
        entityManager.flush();

        Hibernate.initialize(p.getTags());
        Hibernate.initialize(p.getAutores());
        Hibernate.initialize(p.getRatings());
        Hibernate.initialize(p.getComentarios());
        Hibernate.initialize(p.getHistoricoEdicoes());

        return p;
    }
    public void updateVisibility(Long id, boolean hidden) throws MyEntityNotFoundException {
        Publicacao p = entityManager.find(Publicacao.class, id);
        if (p == null) throw new MyEntityNotFoundException("Publicacao not found.");
        p.setHidden(hidden);
        entityManager.flush();
    }

    public void delete(Long id) throws MyEntityNotFoundException {
        Publicacao p = entityManager.find(Publicacao.class, id);
        if (p == null) throw new MyEntityNotFoundException("Publicacao not found.");


        if (this.getFile(id) != null) {
            File file = this.getFile(id);
            if (file.exists()) {
                file.delete();
            }
        }

        entityManager.remove(p);
    }


    public List<Publicacao> findPublicationsForUser(String username) {
        List<Publicacao> results = entityManager.createQuery(
                        "SELECT p FROM Publicacao p WHERE p.createdBy.username = :username ORDER BY p.createdAt DESC",
                        Publicacao.class)
                .setParameter("username", username)
                .getResultList();

        for (Publicacao p : results) {
            Hibernate.initialize(p.getTags());
            Hibernate.initialize(p.getAutores());
            Hibernate.initialize(p.getRatings());
        }
        return results;
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateResumo(Long id, String novoResumo) {
        Publicacao p = entityManager.find(Publicacao.class, id);
        if (p != null) {
            p.setResumo(novoResumo);
        }
    }
}