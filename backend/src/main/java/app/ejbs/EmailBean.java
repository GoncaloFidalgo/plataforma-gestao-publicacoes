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

    public void notifyTagSubscribers(Set<User> subscribers, String tagName, String eventType, String publicationTitle, Long publicationId) {
        String subject = String.format("Notificação: Nova atividade na tag '%s'", tagName);

        for (User subscriber : subscribers) {
            String text = String.format(
                "Olá %s,\n\n" +
                "Houve uma nova atividade na tag '%s' à qual está subscrito:\n\n" +
                "Tipo: %s\n" +
                "Publicação: %s (ID: %d)\n\n" +
                "Aceda ao sistema para mais detalhes.\n\n" +
                "Cumprimentos,\nEquipa",
                subscriber.getName(),
                tagName,
                eventType,
                publicationTitle,
                publicationId
            );

            send(subscriber.getEmail(), subject, text);
        }
    }
}
