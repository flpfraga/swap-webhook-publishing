package swap.publisher.webhook.kafka.consumer;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import swap.producer.info.RepositoryInfo;
import swap.publisher.webhook.service.WebhookService;


@Service
@EnableKafka
public class GithubKafkaConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(GithubKafkaConsumer.class);

    @Value("${app.kafka.consumer.github-info.message-key}")
    private String MESSAGE_KEY;
    public final WebhookService webhookService;

    public GithubKafkaConsumer(WebhookService webhookService) {
        this.webhookService = webhookService;
    }


    @KafkaListener(topics = "${app.kafka.consumer.github-info.topic}", groupId = "consumer-group-id")
    public void consume(ConsumerRecord<String,
            GenericRecord> consumerRecord,
                        @Header(KafkaHeaders.RECEIVED_KEY) String MESSAGE_KEY) {
        if (ObjectUtils.isEmpty(consumerRecord)) {
            LOG.warn("Not message, Key_message: MESSAGE_KEY");
        }
        LOG.warn("New messagem from kafka conumer");
        RepositoryInfo repositoryInfo = (RepositoryInfo) consumerRecord.value();
        webhookService.sendByWebhookClient(repositoryInfo);
    }

}
