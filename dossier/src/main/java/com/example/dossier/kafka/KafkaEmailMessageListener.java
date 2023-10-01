package com.example.dossier.kafka;

import com.example.dossier.dto.EmailMessage;
import com.example.dossier.dto.EmailMessageWithDocuments;
import com.example.dossier.service.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEmailMessageListener {
    private final EmailSender emailSender;
    @KafkaListener(topics = {"finish-registration", "create-documents", "send-ses", "credit-issued",
            "application-denied"}, groupId = "dossier-consumer-group")
    public void receiveEmailMessage(@Payload EmailMessage emailMessage) {
        log.info("receiveEmailMessage(), EmailMessage: {}", emailMessage);
        sendEmail(emailMessage);
    }

    @KafkaListener(topics = {"send-documents"}, groupId = "dossier-consumer-group")
    public void receiveDocuments(@Payload EmailMessageWithDocuments emailMessageWithDocuments) {
        log.info("receiveDocuments(), EmailMessageWithDocuments: {}", emailMessageWithDocuments);
        sendDocuments(emailMessageWithDocuments);
    }
    private void sendEmail(EmailMessage emailMessage) {
        log.info("sendEmail(), EmailMessage: {}", emailMessage);
        emailSender.sendEmail(emailMessage);
    }
    private void sendDocuments(EmailMessageWithDocuments emailMessageWithDocuments) {
        log.info("sendDocuments(), EmailMessageWithDocuments: {}", emailMessageWithDocuments);
        emailSender.sendDocuments(emailMessageWithDocuments);
    }
}
