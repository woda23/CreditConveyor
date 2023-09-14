package com.example.dossier.service;

import com.example.dossier.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSender {
    public void sendEmail(EmailMessage emailMessage) {
        String host = "smtp.mail.ru";
        int port = 587;
        String username = "test.nf.09@mail.ru";
        String password = "5K30zE1m7rR7jjbw67Sm";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailMessage.getAddress()));
            message.setSubject(emailMessage.getTheme().name());
            message.setText("С уважением, Пупыркин Велемир Святославович.");
            Transport.send(message);
            log.info("EmailSender, sendEmail(), Письмо {} успешно отправлено.", message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
