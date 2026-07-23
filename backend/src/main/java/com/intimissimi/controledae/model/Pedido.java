package com.intimissimi.controledae.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Loja loja;

    private LocalDate data;

    @Column(length = 255)
    private String pedido; // descrição / tipo (Ressuprimento, Brinde, Tranche, Sem pedido de venda...)

    @Column(length = 40)
    private String nfNumero;

    private Integer qt;
    private BigDecimal valor;
    private Integer qtFaturado;

    @Column(precision = 14, scale = 2)
    private BigDecimal valorFaturado = BigDecimal.ZERO;

    private LocalDate previsaoEntrega;
    private LocalDate recebimento;
    private LocalDate vencimento;              // vencimento do DAE
    private LocalDate vencimentoFornecedor;    // vencimento do pagamento à Calzedonia (fornecedor)

    @Column(precision = 14, scale = 2)
    private BigDecimal valorDae = BigDecimal.ZERO; // esperado (16% do valor faturado)

    @Column(length = 20)
    private String status = "pendente"; // pendente | pago

    private LocalDate dataPagamento;

    @Column(precision = 14, scale = 2)
    private BigDecimal valorPago;

    @Column(length = 500)
    private String observacoes;
}
