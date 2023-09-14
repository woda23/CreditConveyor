package com.example.deal.config;

import com.example.deal.dto.EmailMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Producer {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public Producer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public String sendMessage(EmailMessage message, String topic) {
        try {
            String orderAsMessage = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, orderAsMessage);

            log.info("food order produced {}", orderAsMessage);
            return "message sent";
        }
        catch (Exception e) {
             log.info(e.getMessage());
             throw new IllegalArgumentException(e.getMessage());
        }
    }
}
