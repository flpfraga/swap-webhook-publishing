package swap.publisher.webhook.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebhookClientTest {

    @Mock
    private RestTemplate webhookRestTemplate;

    @InjectMocks
    private WebhookClient webhookClient;

    private WebhookClientResponse webhookClientResponse;
    private final String WEBHOOK_URL = "https://webhook.test/endpoint";

    @BeforeEach
    void setUp() {
        // Criar um exemplo de WebhookClientResponse
        Issue issue = new Issue("Issue 1", "Autor 1", "bug");
        Contributor contributor = new Contributor("Contribuidor 1", "user1", 10);
        
        webhookClientResponse = new WebhookClientResponse();
        webhookClientResponse.setUser("testUser");
        webhookClientResponse.setRepository("testRepo");
        webhookClientResponse.setIssues(Arrays.asList(issue));
        webhookClientResponse.setContributors(Arrays.asList(contributor));
    }

    @Test
    void shouldSendWebhookSuccessfully() {
        // Arrange
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(webhookRestTemplate.exchange(any(), any())).thenReturn(responseEntity);

        // Act
        Optional<?> result = webhookClient.sendWebhookClient(WEBHOOK_URL, webhookClientResponse);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(responseEntity, result.get());
    }

    @Test
    void shouldThrowExceptionWhenResponseFails() {
        // Arrange
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        when(webhookRestTemplate.exchange(any(), any())).thenReturn(responseEntity);

        // Act & Assert
        assertThrows(WebhookClientException.class, 
                () -> webhookClient.sendWebhookClient(WEBHOOK_URL, webhookClientResponse));
    }

    @Test
    void shouldThrowExceptionWhenRestClientFails() {
        // Arrange
        when(webhookRestTemplate.exchange(any(), any())).thenThrow(new RestClientException("Connection failed"));

        // Act & Assert
        assertThrows(WebhookClientException.class, 
                () -> webhookClient.sendWebhookClient(WEBHOOK_URL, webhookClientResponse));
    }

    @Test
    void shouldHandleNullResponse() {
        // Arrange
        when(webhookRestTemplate.exchange(any(), any())).thenReturn(null);

        // Act & Assert
        assertThrows(WebhookClientException.class, 
                () -> webhookClient.sendWebhookClient(WEBHOOK_URL, webhookClientResponse));
    }

    @Test
    void shouldReturnEmptyOptionalWhenFallbackIsCalled() {
        // Act
        Optional<?> result = webhookClient.callClientFallback(
                WEBHOOK_URL, webhookClientResponse, new RuntimeException("Test"));

        // Assert
        assertFalse(result.isPresent());
    }
} 