package swap.publisher.webhook.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class WebhookClient {

    private final RestTemplate webhookRestTemplate;

    public WebhookClient(RestTemplate webhookRestTemplate) {
        this.webhookRestTemplate = webhookRestTemplate;
    }

    @CircuitBreaker(name = "Webhook", fallbackMethod = "callClientFallback")
    public <T> Optional<?> sendWebhookClient(String url, T message) {
        try {
            final ResponseEntity<Object> response = webhookRestTemplate.exchange(
                    buildRepositoryInfoRequest(url, message), Object.class);

            if (isResponseFail(response)) {
                throw new RuntimeException("Webhook Client not sucesso." + url);
            }

            return Optional.ofNullable(response);
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private <T> RequestEntity<T> buildRepositoryInfoRequest(String url, T message) {
        return RequestEntity
                .post(url)
                .header("accept", "*/*")
                .accept(MediaType.APPLICATION_JSON)
                .body(message);
    }

    private boolean isResponseFail(final ResponseEntity<?> response) {
        return response == null || !response.getStatusCode().is2xxSuccessful();
    }


}
