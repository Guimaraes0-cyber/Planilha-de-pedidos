package com.intimissimi.controledae.model;

public enum Loja {
    RM("Intimissimi Riomar", "Shopping RioMar"),
    IGT("Intimissimi Iguatemi", "Shopping Iguatemi"),
    CLZ("Calzedonia Via Bosque", "Shopping Via Bosque");

    private final String nomeCompleto;
    private final String shopping;

    Loja(String nomeCompleto, String shopping) {
        this.nomeCompleto = nomeCompleto;
        this.shopping = shopping;
    }

    public String getNomeCompleto() { return nomeCompleto; }
    public String getShopping() { return shopping; }
}
