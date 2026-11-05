package com.banking.messaging.producer;


import com.banking.messaging.event.TransactionEvent;
import com.banking.messaging.topic.kafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionEventProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public void publish(TransactionEvent transactionEvent) {

        kafkaTemplate.send(
                kafkaTopics.TRANSACTION_CREATED_TOPIC,
                transactionEvent
        );
    }
}
