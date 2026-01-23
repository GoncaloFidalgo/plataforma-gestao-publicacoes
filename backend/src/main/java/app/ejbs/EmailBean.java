package app.ejbs;

import app.entities.User;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless(name = "EmailEJB")
public class EmailBean {
    @Resource(name = "java:/jboss/mail/fakeSMTP")
    private Session mailSession;

    private static final Logger logger = Logger.getLogger("EmailBean.logger");

    public void send(String to, String subject, String text) {
        Thread emailJob = new Thread(() -> {
            Message message = new MimeMessage(mailSession);
            Date timestamp = new Date();

            try {
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(to, false));
                message.setSubject(subject);
                message.setText(text);
                message.setSentDate(timestamp);

                Transport.send(message);
            } catch (MessagingException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        });

        emailJob.start();
    }

    public void notifyTagSubscribers(Set<User> subscribers, String tagName, String eventType, String publicationTitle) {
        String subject = String.format("Notificacao: Nova atividade na tag '%s'", tagName);

        for (User subscriber : subscribers) {
            String text = String.format(
                "Ola %s,\n\n" +
                "Houve uma nova atividade na tag '%s' a qual esta subscrito:\n\n" +
                "Tipo: %s\n" +
                "Publicacao: %s \n\n" +
                "Aceda ao sistema para mais detalhes.\n\n" +
                "Cumprimentos,\nEquipa",
                subscriber.getName(),
                tagName,
                eventType,
                publicationTitle
            );

            send(subscriber.getEmail(), subject, text);
        }
    }

    public void notifyTagSubscribersWithUrl(Set<User> subscribers, String tagName, String eventType, String publicationTitle, String publicationUrl) {
        String subject = String.format("Notificacao: Nova atividade na tag '%s'", tagName);

        for (User subscriber : subscribers) {
            String text = String.format(
                "Ola %s,\n\n" +
                "Houve uma nova atividade na tag '%s' a qual esta subscrito:\n\n" +
                "Tipo: %s\n" +
                "Publicacao: %s\n\n" +
                "Aceda a publicacao em: %s\n\n" +
                "Cumprimentos,\nEquipa",
                subscriber.getName(),
                tagName,
                eventType,
                publicationTitle,
                publicationUrl
            );

            send(subscriber.getEmail(), subject, text);
        }
    }
}
