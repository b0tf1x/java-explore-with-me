package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class StatClient {
    private final WebClient webClient;

    public StatClient(@Value("${ewm-stats-server.url}") String serverUrl) {
        webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void saveEndpointHit(CreateHit createEndpointHitDto) {
        webClient.post()
                .uri("/hit")
                .body(Mono.just(createEndpointHitDto), CreateHit.class)
                .retrieve();
    }

    public Mono<List<ViewStatsDto>> getStatistics() {
        return webClient.get()
                .uri("/stats")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDto>>() {
                });
    }
}