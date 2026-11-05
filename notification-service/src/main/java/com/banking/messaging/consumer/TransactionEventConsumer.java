package com.banking.messaging.consumer;

import com.banking.messaging.event.TransactionEvent;
import com.banking.messaging.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionEventConsumer {

    private final JavaMailSender mailSender;

    @KafkaListener(
            topics = KafkaTopics.TRANSACTION_CREATED_TOPIC,
            groupId = "transaction-notify-group-v2"
    )
    public void consumeTransactionEvent(TransactionEvent message) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(message.getEmail());
            simpleMailMessage.setSubject("Transaction alert!");
            simpleMailMessage.setText(message.getMessage());
            mailSender.send(simpleMailMessage);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

}
