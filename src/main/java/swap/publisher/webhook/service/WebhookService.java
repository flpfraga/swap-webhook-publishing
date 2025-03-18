package swap.publisher.webhook.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swap.producer.info.RepositoryInfo;
import swap.publisher.webhook.client.WebhookClient;
import swap.publisher.webhook.client.WebhookClientResponse;
import swap.publisher.webhook.client.WebhookClientException;

/**
 * Serviço responsável por enviar mensagens recebidas do Kafka para o webhook.
 */
@Service
public class WebhookService {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookService.class);

    @Value("${app.client.webhook.repository-info.path}")
    private String ENDPOINT;

    private final WebhookClient webhookClient;

    public WebhookService(WebhookClient webhookClient) {
        this.webhookClient = webhookClient;
    }

    /**
     * Envia informações de repositório para o webhook configurado
     * 
     * @param repositoryInfo Informações do repositório recebidas do Kafka
     * @throws WebhookClientException em caso de falha no envio
     */
    public void sendByWebhookClient(RepositoryInfo repositoryInfo) {
        if (repositoryInfo == null) {
            LOG.error("Não é possível enviar mensagem nula para o webhook");
            throw new WebhookClientException("Dados de repositório nulos");
        }
        
        LOG.info("Preparando para enviar informações do repositório {}/{} para webhook", 
                repositoryInfo.getUser(), repositoryInfo.getRepository());
        
        WebhookClientResponse webhookClientResponse = convertRepositoryInfoToWebhookClientResponse(repositoryInfo);
        
        try {
            webhookClient.sendWebhookClient(ENDPOINT, webhookClientResponse);
            LOG.info("Mensagem enviada com sucesso para o webhook");
        } catch (WebhookClientException e) {
            LOG.error("Falha ao enviar para o webhook: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Converte o objeto RepositoryInfo do Avro para o formato WebhookClientResponse
     * 
     * @param repositoryInfo Informações do repositório no formato Avro
     * @return Resposta formatada para o webhook
     */
    private WebhookClientResponse convertRepositoryInfoToWebhookClientResponse(RepositoryInfo repositoryInfo) {
        LOG.debug("Convertendo RepositoryInfo para WebhookClientResponse");
        return new WebhookClientResponse(
                repositoryInfo.getUser().toString(),
                repositoryInfo.getRepository().toString(),
                repositoryInfo.getIssues(),
                repositoryInfo.getContributors());
    }
}
