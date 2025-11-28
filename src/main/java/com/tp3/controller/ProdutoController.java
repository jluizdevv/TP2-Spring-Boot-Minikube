package com.tp3.controller;

import com.tp3.model.Produto;
import com.tp3.repository.ProdutoRepository;
import com.tp3.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private ExternalApiService externalApiService;


    @GetMapping
    public Flux<Produto> listar() {
        return Mono.fromCallable(() -> repository.findAll())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable); // Converte o Iterable do JDBC para Flux
    }


    @GetMapping("/{id}")
    public Mono<Produto> buscar(@PathVariable Long id) {
        return Mono.fromCallable(() -> repository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Produto> criar(@RequestBody Produto produto) {
        return externalApiService.verificarStatusExterno()
                .doOnNext(status -> System.out.println("Status Externo: " + status))
                .flatMap(status ->
                        Mono.fromCallable(() -> repository.save(produto))
                                .subscribeOn(Schedulers.boundedElastic())
                );
    }

    @PutMapping("/{id}")
    public Mono<Produto> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        return Mono.fromCallable(() -> {
                    produto.setId(id);
                    return repository.save(produto);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deletar(@PathVariable Long id) {
        return Mono.fromRunnable(() -> repository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}