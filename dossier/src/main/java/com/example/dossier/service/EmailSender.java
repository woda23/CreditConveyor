package com.example.dossier.service;

import com.example.dossier.dto.EmailMessage;
import com.example.dossier.dto.EmailMessageWithDocuments;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

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
    @Value("${email.username}")
    private String username;
    @Value("${email.password}")
    private String password;
    @Value("${email.port}")
    private String port;
    @Value("${email.host}")
    private String host;
    public void sendEmail(EmailMessage emailMessage) {
        Session session = getSession();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailMessage.getAddress()));
            message.setSubject(emailMessage.getTheme().name());
            switch (emailMessage.getTheme()) {
                case FINISH_REGISTRATION -> message.setText("Завершите оформление");
                case CREATE_DOCUMENTS -> message.setText("Перейти к оформлению документов");
                case SEND_SES -> message.setText("ССЫЛКА");
                case CREDIT_ISSUED -> message.setText("Успешно завершено");
                case APPLICATION_DENIED -> message.setText("Отказ");
            }
            Transport.send(message);
            log.info("EmailSender, sendEmail(), Письмо {} успешно отправлено.", message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendDocuments(EmailMessageWithDocuments emailMessageWithDocuments) {
        Session session = getSession();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailMessageWithDocuments.getAddress()));
            message.setSubject(emailMessageWithDocuments.getTheme().name());
            message.setText(emailMessageWithDocuments.getCredit());
            Transport.send(message);
            log.info("EmailSender, sendDocuments(), Письмо {} успешно отправлено.", message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private Session getSession() {
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
        return session;
    }
}
