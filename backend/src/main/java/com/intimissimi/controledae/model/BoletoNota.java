package com.intimissimi.controledae.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "boleto_notas")
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boleto getBoleto() { return boleto; }
    public void setBoleto(Boleto boleto) { this.boleto = boleto; }

    public String getNfNumero() { return nfNumero; }
    public void setNfNumero(String nfNumero) { this.nfNumero = nfNumero; }

    public BigDecimal getValorDae() { return valorDae; }
    public void setValorDae(BigDecimal valorDae) { this.valorDae = valorDae; }
}
