package swap.publisher.webhook.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swap.producer.info.RepositoryInfo;
import swap.publisher.webhook.client.WebhookClient;
import swap.publisher.webhook.client.WebhookClientResponse;

@Service
public class WebhookService {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookService.class);

    @Value("${app.client.webhook.repository-info.path}")
    private String ENDPOINT;

    private final WebhookClient webhookClient;

    public WebhookService(WebhookClient webhookClient) {
        this.webhookClient = webhookClient;
    }

    public void sendByWebhookClient(RepositoryInfo repositoryInfo){
        WebhookClientResponse webhookClientResponse = convertRepositoryInfoToWebhookClientResponse(repositoryInfo);
        webhookClient.sendWebhookClient(ENDPOINT, webhookClientResponse);
        LOG.warn("Message send with sucess");
    }

    private WebhookClientResponse convertRepositoryInfoToWebhookClientResponse(RepositoryInfo repositoryInfo){
        return new WebhookClientResponse(repositoryInfo.getUser().toString(),
                repositoryInfo.getRepository().toString(),
                repositoryInfo.getIssues(),
                repositoryInfo.getContributors());
    }
}
