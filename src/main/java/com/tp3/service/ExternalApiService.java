package com.tp3.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ExternalApiService {

    private final WebClient webClient;

    public ExternalApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://httpbin.org").build();
    }

    public Mono<String> verificarStatusExterno() {
        return this.webClient.get()
                .uri("/status/200")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("Serviço Indisponível");
    }
}