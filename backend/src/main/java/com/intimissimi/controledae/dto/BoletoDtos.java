package com.intimissimi.controledae.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BoletoDtos {

    public static class NotaRequest {
        public String nfNumero;
        public BigDecimal valorDae;
    }

    public static class BoletoRequest {
        public LocalDate data;
        public BigDecimal valorPago;
        public List<NotaRequest> notas;
    }
}
