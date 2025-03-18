package swap.publisher.webhook.client;

/**
 * Exceção específica para problemas de comunicação com o webhook
 */
public class WebhookClientException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WebhookClientException(String message) {
        super(message);
    }

    public WebhookClientException(String message, Throwable cause) {
        super(message, cause);
    }
} 