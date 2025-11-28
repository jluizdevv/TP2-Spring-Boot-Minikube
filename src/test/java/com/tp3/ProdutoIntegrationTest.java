package com.tp3;

import com.tp3.model.Produto;
import com.tp3.repository.ProdutoRepository;
import com.tp3.service.ExternalApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ProdutoIntegrationTest {


    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProdutoRepository repository;

    @MockitoBean
    private ExternalApiService externalApiService;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void deveCriarProdutoComSucesso() {
        Mockito.when(externalApiService.verificarStatusExterno())
                .thenReturn(Mono.just("Status OK (Mock)"));

        Produto novoProduto = new Produto();
        novoProduto.setNome("Teclado Mecânico");
        novoProduto.setPreco(250.00);


        webTestClient.post()
                .uri("/produtos")
                .bodyValue(novoProduto)
                .exchange()
                // Verificações
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nome").isEqualTo("Teclado Mecânico");
    }

    @Test
    void deveListarProdutos() {
        Produto p = new Produto();
        p.setNome("Monitor 24");
        p.setPreco(900.00);
        repository.save(p);
        webTestClient.get()
                .uri("/produtos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].nome").isEqualTo("Monitor 24");
    }
}