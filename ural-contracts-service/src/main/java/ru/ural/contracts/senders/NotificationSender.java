package ru.ural.contracts.senders;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.ural.contracts.properties.KafkaProperty;
import ru.ural.contracts.services.ProducerService;
import ru.ural.notifications.dto.contract.NotificationContractRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationSender {

    private final KafkaProperty kafkaProperty;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ProducerService producerService;

    public void sendContractNotification(NotificationContractRequest request) {
        String body = producerService.objectToString(request);

        KafkaProperty.Topic topic = kafkaProperty.getNotificationContractTopic();

        String correlationId = UUID.randomUUID().toString();

        ProducerRecord<String, String> record = new ProducerRecord<>(
                topic.getName(),
                topic.getPartitions(),
                correlationId,
                body
        );

        kafkaTemplate.send(record);
    }

}
