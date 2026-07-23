package com.intimissimi.controledae.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Loja loja;

    private LocalDate data;

    @Column(length = 255)
    private String pedido;

    @Column(length = 40)
    private String nfNumero;

    private Integer qt;
    private BigDecimal valor;
    private Integer qtFaturado;

    @Column(precision = 14, scale = 2)
    private BigDecimal valorFaturado = BigDecimal.ZERO;

    private LocalDate previsaoEntrega;
    private LocalDate recebimento;
    private LocalDate vencimento;
    private LocalDate vencimentoFornecedor;

    @Column(precision = 14, scale = 2)
    private BigDecimal valorDae = BigDecimal.ZERO;

    @Column(length = 20)
    private String status = "pendente";

    private LocalDate dataPagamento;

    @Column(precision = 14, scale = 2)
    private BigDecimal valorPago;

    @Column(length = 500)
    private String observacoes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Loja getLoja() { return loja; }
    public void setLoja(Loja loja) { this.loja = loja; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getPedido() { return pedido; }
    public void setPedido(String pedido) { this.pedido = pedido; }

    public String getNfNumero() { return nfNumero; }
    public void setNfNumero(String nfNumero) { this.nfNumero = nfNumero; }

    public Integer getQt() { return qt; }
    public void setQt(Integer qt) { this.qt = qt; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public Integer getQtFaturado() { return qtFaturado; }
    public void setQtFaturado(Integer qtFaturado) { this.qtFaturado = qtFaturado; }

    public BigDecimal getValorFaturado() { return valorFaturado; }
    public void setValorFaturado(BigDecimal valorFaturado) { this.valorFaturado = valorFaturado; }

    public LocalDate getPrevisaoEntrega() { return previsaoEntrega; }
    public void setPrevisaoEntrega(LocalDate previsaoEntrega) { this.previsaoEntrega = previsaoEntrega; }

    public LocalDate getRecebimento() { return recebimento; }
    public void setRecebimento(LocalDate recebimento) { this.recebimento = recebimento; }

    public LocalDate getVencimento() { return vencimento; }
    public void setVencimento(LocalDate vencimento) { this.vencimento = vencimento; }

    public LocalDate getVencimentoFornecedor() { return vencimentoFornecedor; }
    public void setVencimentoFornecedor(LocalDate vencimentoFornecedor) { this.vencimentoFornecedor = vencimentoFornecedor; }

    public BigDecimal getValorDae() { return valorDae; }
    public void setValorDae(BigDecimal valorDae) { this.valorDae = valorDae; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }

    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal valorPago) { this.valorPago = valorPago; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
