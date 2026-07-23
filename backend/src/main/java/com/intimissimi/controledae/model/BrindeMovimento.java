package com.intimissimi.controledae.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "brinde_movimentos")
@Getter
@Setter
@NoArgsConstructor
public class BrindeMovimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String item;

    @Column(nullable = false, length = 10)
    private String tipo; // entrada | saida

    @Column(nullable = false)
    private Integer quantidade;

    private LocalDate data;

    @Column(length = 255)
    private String destino;

    @Column(length = 500)
    private String observacoes;
}
