
package com.intimissimi.controledae.service;

import com.intimissimi.controledae.model.Pedido;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class SelagemService {

    public static final BigDecimal DAE_RATE = new BigDecimal("0.16");

    public BigDecimal calcularDaeEsperado(BigDecimal valorFaturado) {
        if (valorFaturado == null) return BigDecimal.ZERO;
        return valorFaturado.multiply(DAE_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public String categoria(String pedidoTexto) {
        if (pedidoTexto == null) return "Outros";
        String up = pedidoTexto.toUpperCase();
        if (up.contains("RESSUPRIMENTO")) return "Ressuprimento";
        if (up.contains("TRANCHE")) return "Tranche";
        if (up.contains("BRINDE")) return "Brinde";
        if (up.contains("SEM PEDIDO")) return "Sem pedido de venda";
        return "Outros";
    }

    public String statusSelagem(Pedido p) {
        if (!"pago".equals(p.getStatus())) return "aguardando";
        BigDecimal esperado = p.getValorDae() != null ? p.getValorDae() : BigDecimal.ZERO;
        BigDecimal pago = p.getValorPago() != null ? p.getValorPago() : BigDecimal.ZERO;
        BigDecimal diferenca = esperado.subtract(pago).abs();
        return diferenca.compareTo(BigDecimal.ONE) < 0 ? "ok" : "erro";
    }

    public String statusVencimento(Pedido p) {
        if ("pago".equals(p.getStatus())) return "pago";
        if (p.getVencimento() == null) return "pendente";
        long dias = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), p.getVencimento());
        if (dias < 0) return "vencido";
        if (dias <= 7) return "urgente";
        return "pendente";
    }
}
