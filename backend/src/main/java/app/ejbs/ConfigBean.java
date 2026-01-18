package app.ejbs;

import app.entities.Administrator;
import app.entities.PublicationType;
import app.entities.ScientificArea;
import app.entities.User;
import app.exceptions.MyConstraintViolationException;
import app.exceptions.MyEntityExistsException;
import app.exceptions.MyEntityNotFoundException;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Startup
@Singleton
public class ConfigBean {
    @PersistenceContext
    private EntityManager em;

    @EJB private UserBean userBean;
    @EJB private TagBean tagBean;
    @EJB private PublicacaoBean publicacaoBean;
    @EJB private ReferenceBean referenceBean;

    private static final Logger logger = Logger.getLogger(ConfigBean.class.getName());

    @PostConstruct
    public void populateDB() {
        try {
            // 1. Users
            if (userBean.find("admin") == null) userBean.createUser("admin", "123", "Admin", "admin@e.com", 1, true);
            if (userBean.find("resp") == null) userBean.createUser("resp", "123", "Resp", "resp@e.com", 2, true);
            if (userBean.find("colab") == null) userBean.createUser("colab", "123", "Colab", "colab@e.com", 3, true);

            // 2. Tags
            try { tagBean.create("Java", "Desc", "CS"); } catch (Exception e) {}
            try { tagBean.create("AI", "Desc", "CS"); } catch (Exception e) {}

            // 3. Types & Areas
            PublicationType tutorialType = findOrCreateType("Tutorial");
            PublicationType thesisType = findOrCreateType("Thesis");
            ScientificArea csArea = findOrCreateArea("Computer Science");

            // 4. Publications
            if (publicacaoBean.findAll(null, "Introduction to Java", null, null, null, null).isEmpty()) {
                InputStream content = new ByteArrayInputStream("Content".getBytes(StandardCharsets.UTF_8));

                publicacaoBean.create(
                        "Introduction to Java",
                        tutorialType.getId(),
                        Arrays.asList("colab"),
                        csArea.getId(),
                        "Desc",
                        content, ".pdf",
                        Arrays.asList("Java"),
                        "colab"
                );
            }

            if (publicacaoBean.findAll(null, "The Future of AI", null, null, null, null).isEmpty()) {
                InputStream content = getClass().getClassLoader().getResourceAsStream("test.pdf");
                if (content == null) content = new ByteArrayInputStream("Dummy PDF".getBytes());

                publicacaoBean.create(
                        "The Future of AI",
                        thesisType.getId(),
                        Arrays.asList("resp"),
                        csArea.getId(),
                        "Desc",
                        content, ".pdf",
                        Arrays.asList("AI"),
                        "resp"
                );
            }

        } catch (Exception e) {
            logger.severe("ConfigBean Error: " + e.getMessage());
        }
    }
    private PublicationType findOrCreateType(String name) {
        List<PublicationType> list = em.createQuery("SELECT t FROM PublicationType t WHERE t.name = :name", PublicationType.class)
                .setParameter("name", name).getResultList();
        if (!list.isEmpty()) return list.get(0);

        PublicationType t = new PublicationType(name);
        em.persist(t);
        return t;
    }

    private ScientificArea findOrCreateArea(String name) {
        List<ScientificArea> list = em.createQuery("SELECT s FROM ScientificArea s WHERE s.name = :name", ScientificArea.class)
                .setParameter("name", name).getResultList();
        if (!list.isEmpty()) return list.get(0);

        ScientificArea a = new ScientificArea(name);
        em.persist(a);
        return a;
    }
}
