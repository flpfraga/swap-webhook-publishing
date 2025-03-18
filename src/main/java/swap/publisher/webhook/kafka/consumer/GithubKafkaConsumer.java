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
import swap.publisher.webhook.client.WebhookClientException;
import swap.publisher.webhook.service.WebhookService;

/**
 * Consumidor Kafka responsável por processar mensagens do tópico de informações de repositórios
 */
@Service
@EnableKafka
public class GithubKafkaConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(GithubKafkaConsumer.class);

    @Value("${app.kafka.consumer.github-info.message-key}")
    private String messageKey;
    
    private final WebhookService webhookService;

    public GithubKafkaConsumer(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Consome mensagens do tópico Kafka e encaminha para o serviço de webhook
     *
     * @param consumerRecord Registro recebido do Kafka
     * @param receivedKey Chave da mensagem recebida
     */
    @KafkaListener(topics = "${app.kafka.consumer.github-info.topic}", groupId = "${app.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, GenericRecord> consumerRecord,
                        @Header(KafkaHeaders.RECEIVED_KEY) String receivedKey) {
        
        LOG.info("Mensagem recebida do tópico Kafka com chave: {}", receivedKey);
        
        if (ObjectUtils.isEmpty(consumerRecord)) {
            LOG.warn("Mensagem vazia recebida, chave da mensagem: {}", receivedKey);
            return;
        }
        
        if (consumerRecord.value() == null) {
            LOG.warn("Valor da mensagem é nulo, chave: {}", receivedKey);
            return;
        }
        
        try {
            RepositoryInfo repositoryInfo = (RepositoryInfo) consumerRecord.value();
            LOG.info("Processando informações do repositório {}/{}", 
                    repositoryInfo.getUser(), repositoryInfo.getRepository());
                    
            webhookService.sendByWebhookClient(repositoryInfo);
            
            LOG.info("Mensagem processada com sucesso");
        } catch (ClassCastException e) {
            LOG.error("Erro ao converter mensagem para RepositoryInfo: {}", e.getMessage());
        } catch (WebhookClientException e) {
            LOG.error("Falha ao enviar mensagem para webhook: {}", e.getMessage());
        } catch (Exception e) {
            LOG.error("Erro inesperado ao processar mensagem: {}", e.getMessage(), e);
        }
    }
}
