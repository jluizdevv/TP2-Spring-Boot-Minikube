package com.tp3.model;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Produto {
    @Id
    @With
    private Long id;
    private String nome;
    private Double preco;
}