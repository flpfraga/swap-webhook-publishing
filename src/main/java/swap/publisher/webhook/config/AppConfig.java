package swap.publisher.webhook.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuração de beans da aplicação
 */
@Configuration
public class AppConfig {

    @Value("${app.client.webhook.connection-timeout}")
    private int connectionTimeout;

    @Value("${app.client.webhook.read-timeout}")
    private int readTimeout;

    /**
     * Cria um RestTemplate configurado com timeouts para as chamadas de webhook
     * 
     * @return RestTemplate configurado
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(connectionTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }
}
