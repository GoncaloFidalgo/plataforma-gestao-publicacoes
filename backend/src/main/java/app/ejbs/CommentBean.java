package app.ejbs;

import app.entities.Comment;
import app.entities.Publicacao;
import app.entities.User;
import app.exceptions.MyEntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CommentBean {

    @PersistenceContext
    private EntityManager em;

    public Comment create(Long publicacaoId, String username, String text) throws MyEntityNotFoundException {
        User user = em.find(User.class, username);
        if (user == null) throw new MyEntityNotFoundException("User not found");

        Publicacao publicacao = em.find(Publicacao.class, publicacaoId);
        if (publicacao == null) throw new MyEntityNotFoundException("Publicacao not found");

        Comment comment = new Comment(text, user, publicacao);
        publicacao.addComentario(comment); // Maintain relationship

        em.persist(comment);
        em.flush();
        return comment;
    }

    public List<Comment> findAll(Long publicacaoId, Boolean hidden) {
        // hidden = null, obtem tudo para os resps e admins.
        // hidden = true || false, serve para os resps e admins filtrarem e para mostrar apenas
        // os comentarios visiveis para os colaboradores

        StringBuilder jpql = new StringBuilder("SELECT c FROM Comment c WHERE c.publicacao.id = :pubId");
        if (hidden != null) {
            jpql.append(" AND c.hidden = :hidden");
        }
        jpql.append(" ORDER BY c.createdAt DESC");

        var query = em.createQuery(jpql.toString(), Comment.class)
                .setParameter("pubId", publicacaoId);

        if (hidden != null) {
            query.setParameter("hidden", hidden);
        }

        return query.getResultList();
    }

    public Comment find(Long id) throws MyEntityNotFoundException {
        Comment comment = em.find(Comment.class, id);
        if (comment == null) throw new MyEntityNotFoundException("Comment not found");
        return comment;
    }

    public Comment update(Long publicacaoId, Long commentId, String text, String username) throws MyEntityNotFoundException {
        Comment comment = find(commentId);

        if (!comment.getPublicacao().getId().equals(publicacaoId)) {
            throw new IllegalArgumentException("Comment does not belong to this publication");
        }

        if (!comment.getUser().getUsername().equals(username)) {
            throw new SecurityException("You can only update your own comments");
        }

        comment.setText(text);
        return comment;
    }

    public void updateVisibility(Long publicacaoId, Long commentId, boolean hidden, String motive) throws MyEntityNotFoundException {
        Comment comment = find(commentId);

        if (!comment.getPublicacao().getId().equals(publicacaoId)) {
            throw new IllegalArgumentException("Comment does not belong to this publication");
        }

        comment.setHidden(hidden);
        comment.setMotive(motive);
    }

    public void delete(Long publicacaoId, Long commentId, String username) throws MyEntityNotFoundException {
        Comment comment = find(commentId);

        if (!comment.getPublicacao().getId().equals(publicacaoId)) {
            throw new IllegalArgumentException("Comment does not belong to this publication");
        }

        // Apenas o próprio autor pode apagar
        if (!comment.getUser().getUsername().equals(username)) {
            throw new SecurityException("You can only delete your own comments");
        }

        Publicacao p = comment.getPublicacao();
        p.getComentarios().remove(comment);
        em.remove(comment);
    }

    public List<Comment> findByUser(String username) throws MyEntityNotFoundException {
        User user = em.find(User.class, username);
        if (user == null) throw new MyEntityNotFoundException("User not found");

        return em.createQuery("SELECT c FROM Comment c WHERE c.user.username = :username ORDER BY c.createdAt DESC", Comment.class)
                .setParameter("username", username)
                .getResultList();
    }
}