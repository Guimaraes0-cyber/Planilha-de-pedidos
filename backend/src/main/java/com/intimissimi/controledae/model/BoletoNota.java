package com.intimissimi.controledae.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "boleto_notas")
@Getter
@Setter
@NoArgsConstructor
public class BoletoNota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boleto_id", nullable = false)
    @JsonBackReference
    private Boleto boleto;

    @Column(length = 40, nullable = false)
    private String nfNumero;

    @Column(precision = 14, scale = 2)
    private BigDecimal valorDae = BigDecimal.ZERO;
}
