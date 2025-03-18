package swap.publisher.webhook.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Cliente responsável por enviar mensagens para o webhook externo.
 * Utiliza Circuit Breaker para tratar falhas de comunicação.
 */
@Component
public class WebhookClient {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookClient.class);
    private final RestTemplate webhookRestTemplate;

    public WebhookClient(RestTemplate webhookRestTemplate) {
        this.webhookRestTemplate = webhookRestTemplate;
    }

    /**
     * Envia mensagem para o webhook configurado com proteção de Circuit Breaker
     * 
     * @param url URL do webhook
     * @param message Mensagem a ser enviada
     * @return Resposta opcional do webhook
     * @throws WebhookClientException em caso de falha no envio
     */
    @CircuitBreaker(name = "Webhook", fallbackMethod = "callClientFallback")
    public <T> Optional<?> sendWebhookClient(String url, T message) {
        try {
            LOG.info("Enviando mensagem para webhook: {}", url);
            final ResponseEntity<Object> response = webhookRestTemplate.exchange(
                    buildRepositoryInfoRequest(url, message), Object.class);

            if (isResponseFail(response)) {
                LOG.error("Falha ao enviar para webhook. Status: {}", 
                        response != null ? response.getStatusCode() : "null");
                throw new WebhookClientException("Webhook Client não teve sucesso. URL: " + url);
            }

            LOG.info("Mensagem enviada com sucesso para webhook. Status: {}", response.getStatusCode());
            return Optional.ofNullable(response);
        } catch (final Exception ex) {
            LOG.error("Erro ao enviar mensagem para webhook: {}", ex.getMessage());
            throw new WebhookClientException("Erro ao enviar mensagem para webhook: " + ex.getMessage(), ex);
        }
    }

    /**
     * Método de fallback para quando o Circuit Breaker é acionado
     * 
     * @param url URL do webhook
     * @param message Mensagem a ser enviada
     * @param ex Exceção que causou a falha
     * @return Empty Optional
     */
    public <T> Optional<?> callClientFallback(String url, T message, Exception ex) {
        LOG.warn("Circuit Breaker acionado para webhook: {}. Erro: {}", url, ex.getMessage());
        return Optional.empty();
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
