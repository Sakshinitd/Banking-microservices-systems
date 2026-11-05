package com.banking.messaging.producer;

import com.banking.messaging.event.AccountEvent;
import com.banking.messaging.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountEventProducer {

    private final KafkaTemplate<String, AccountEvent> kafkaTemplate;

    public void produceAccountEvent(AccountEvent accountEvent){
        kafkaTemplate.send(KafkaTopics.ACCOUNT_EVENT_TOPIC,accountEvent);
    }
}
