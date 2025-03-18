package swap.publisher.webhook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import swap.producer.info.Contributor;
import swap.producer.info.Issue;
import swap.producer.info.RepositoryInfo;
import swap.publisher.webhook.client.WebhookClient;
import swap.publisher.webhook.client.WebhookClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    private WebhookClient webhookClient;

    @InjectMocks
    private WebhookService webhookService;

    private RepositoryInfo repositoryInfo;
    private final String ENDPOINT = "https://webhook.test/endpoint";

    @BeforeEach
    void setUp() {
        // Configurar o endpoint através de reflexão (simulando o @Value)
        ReflectionTestUtils.setField(webhookService, "ENDPOINT", ENDPOINT);

        // Criar um exemplo de RepositoryInfo
        List<Issue> issues = new ArrayList<>();
        issues.add(Issue.newBuilder()
                .setTitle("Issue 1")
                .setAuthor("Autor 1")
                .setLabels("bug")
                .build());

        List<Contributor> contributors = new ArrayList<>();
        contributors.add(Contributor.newBuilder()
                .setName("Contribuidor 1")
                .setUser("user1")
                .setQtdCommits(10)
                .build());

        repositoryInfo = RepositoryInfo.newBuilder()
                .setUser("testUser")
                .setRepository("testRepo")
                .setIssues(issues)
                .setContributors(contributors)
                .build();
    }

    @Test
    void shouldSendWebhookSuccessfully() {
        // Arrange
        when(webhookClient.sendWebhookClient(anyString(), any())).thenReturn(Optional.empty());

        // Act
        webhookService.sendByWebhookClient(repositoryInfo);

        // Assert
        verify(webhookClient, times(1)).sendWebhookClient(eq(ENDPOINT), any());
    }

    @Test
    void shouldThrowExceptionWhenRepositoryInfoIsNull() {
        // Act & Assert
        assertThrows(WebhookClientException.class, () -> webhookService.sendByWebhookClient(null));
        verify(webhookClient, never()).sendWebhookClient(anyString(), any());
    }

    @Test
    void shouldPropagateExceptionWhenWebhookClientFails() {
        // Arrange
        when(webhookClient.sendWebhookClient(anyString(), any()))
                .thenThrow(new WebhookClientException("Falha no webhook"));

        // Act & Assert
        assertThrows(WebhookClientException.class, () -> webhookService.sendByWebhookClient(repositoryInfo));
    }
} 