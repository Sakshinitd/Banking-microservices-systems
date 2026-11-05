package com.banking.messaging.consumer;

import com.banking.messaging.event.AccountEvent;
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
public class AccountEventConsumer {

    private final JavaMailSender mailSender;

    @KafkaListener(
            topics = KafkaTopics.ACCOUNT_EVENT_TOPIC,
            groupId = "account-notify-group-v2"
    )
    public void consumeAccountEvent(AccountEvent message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(message.getEmail());
        simpleMailMessage.setSubject("Account alerts!");
        simpleMailMessage.setText(message.getMessage());
        mailSender.send(simpleMailMessage);
    }
}
