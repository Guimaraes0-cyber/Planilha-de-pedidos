-- ============================================================
-- Controle de Pedidos & DAE - Intimissimi & Calzedonia
-- Schema inicial (PostgreSQL - usado no deploy do Render)
-- Equivalente ao V1__schema_inicial.sql (SQL Server), só que com a
-- sintaxe do Postgres.
-- ============================================================

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(60) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT (now() at time zone 'utc')
);

CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    loja VARCHAR(10) NOT NULL,
    data DATE NULL,
    pedido VARCHAR(255) NULL,
    nf_numero VARCHAR(40) NULL,
    qt INT NULL,
    valor NUMERIC(14,2) NULL,
    qt_faturado INT NULL,
    valor_faturado NUMERIC(14,2) NOT NULL DEFAULT 0,
    previsao_entrega DATE NULL,
    recebimento DATE NULL,
    vencimento DATE NULL,
    vencimento_fornecedor DATE NULL,
    valor_dae NUMERIC(14,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'pendente',
    data_pagamento DATE NULL,
    valor_pago NUMERIC(14,2) NULL,
    observacoes VARCHAR(500) NULL
);
CREATE INDEX idx_pedidos_loja ON pedidos(loja);
CREATE INDEX idx_pedidos_nf ON pedidos(loja, nf_numero);

CREATE TABLE boletos (
    id BIGSERIAL PRIMARY KEY,
    loja VARCHAR(10) NOT NULL,
    data DATE NULL,
    valor_pago NUMERIC(14,2) NOT NULL DEFAULT 0
);
CREATE INDEX idx_boletos_loja ON boletos(loja);

CREATE TABLE boleto_notas (
    id BIGSERIAL PRIMARY KEY,
    boleto_id BIGINT NOT NULL,
    nf_numero VARCHAR(40) NOT NULL,
    valor_dae NUMERIC(14,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_boleto_notas_boleto FOREIGN KEY (boleto_id) REFERENCES boletos(id) ON DELETE CASCADE
);
CREATE INDEX idx_boleto_notas_nf ON boleto_notas(nf_numero);

CREATE TABLE brinde_movimentos (
    id BIGSERIAL PRIMARY KEY,
    item VARCHAR(60) NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    quantidade INT NOT NULL,
    data DATE NULL,
    destino VARCHAR(255) NULL,
    observacoes VARCHAR(500) NULL
);
CREATE INDEX idx_brinde_item ON brinde_movimentos(item);
