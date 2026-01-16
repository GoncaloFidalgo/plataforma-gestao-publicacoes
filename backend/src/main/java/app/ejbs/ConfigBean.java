package app.ejbs;

import app.entities.Administrator;
import app.entities.User;
import app.exceptions.MyConstraintViolationException;
import app.exceptions.MyEntityExistsException;
import app.exceptions.MyEntityNotFoundException;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

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
    @EJB
    private UserBean userBean;

    @EJB
    private TagBean tagBean;

    @EJB
    private PublicacaoBean publicacaoBean;


    private static final Logger logger = Logger.getLogger(ConfigBean.class.getName());

    @PostConstruct
    public void populateDB() {
        // --- 1. USERS ---
        try {
            // admin
            if (userBean.find("admin") == null) {
                userBean.createUser("admin","123","Admin Principal","admin@example.com",1,true
                );
                logger.info("Created Administrator: admin");
            }

            // responsavel
            if (userBean.find("resp") == null) {
                userBean.createUser("resp","123","Responsavel Content","resp@example.com",2,true);
                logger.info("Created Responsavel: resp");
            }

            // colaborador
            if (userBean.find("colab") == null) {
                userBean.createUser("colab","123","Joao Colaborador","colab@example.com",3,true);
                logger.info("Created Colaborador: colab");
            }
            // --- 2. TAGS ---
            try {
                tagBean.create("Java", "Java Programming Language", "Computer Science");
            } catch (Exception e) {  }

            try {
                tagBean.create("AI", "Artificial Intelligence", "Computer Science");
            } catch (Exception e) {  }

            try {
                tagBean.create("History", "World History", "Humanities");
            } catch (Exception e) {  }

            // --- 3. PUBLICATIONS ---

            // Check if publication exists by searching for title to avoid duplicates on restart
            if (publicacaoBean.findAll(null, "Introduction to Java", null, null, null, null).isEmpty()) {
                // Create a dummy stream to mimic a file upload
                InputStream content = getClass().getClassLoader().getResourceAsStream("test.pdf");
                try {
                publicacaoBean.create(
                        "Introduction to Java",
                        "Tutorial",
                        Arrays.asList("colab"), // Author usernames
                        "Computer Science",
                        "A comprehensive beginner's guide to Java development.",
                        content, // Pass stream to trigger save logic
                        ".pdf",  // Pass extension
                        Arrays.asList("Java"), // Tags
                        "colab" // Creator username
                );
                logger.info("Created Publication: Introduction to Java");
            } finally {
                // Good practice to close FileInputStream if we opened it
                if (content instanceof FileInputStream) {
                    try { content.close(); } catch (Exception ignore) {}
                }
            }
            }

            if (publicacaoBean.findAll(null, "The Future of AI", null, null, null, null).isEmpty()) {
                InputStream content = getClass().getClassLoader().getResourceAsStream("test.zip");

                try {
                    publicacaoBean.create(
                            "The Future of AI",
                            "Thesis",
                            Arrays.asList("resp", "colab"), // Multiple authors
                            "Computer Science",
                            "An in-depth analysis of Neural Networks and LLMs.",
                            content,
                            ".zip",
                            Arrays.asList("AI", "Java"),
                            "resp"
                    );
                    logger.info("Created Publication: The Future of AI");
                } finally {
                    // Good practice to close FileInputStream if we opened it
                    if (content instanceof FileInputStream) {
                        try { content.close(); } catch (Exception ignore) {}
                    }
                }
            }

        } catch (MyConstraintViolationException e) {
            logger.severe("Error populating DB (Constraint Violation): " + e.getMessage());
        } catch (Exception e) {
            logger.severe("Error populating DB: " + e.getMessage());
        }
    }
}
