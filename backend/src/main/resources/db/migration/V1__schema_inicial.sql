-- ============================================================
-- Controle de Pedidos & DAE - Intimissimi & Calzedonia
-- Schema inicial (SQL Server)
-- ============================================================

CREATE TABLE usuarios (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(60) NOT NULL UNIQUE,
    senha_hash NVARCHAR(255) NOT NULL,
    role NVARCHAR(30) NOT NULL,
    ativo BIT NOT NULL DEFAULT 1,
    criado_em DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE pedidos (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    loja NVARCHAR(10) NOT NULL,
    data DATE NULL,
    pedido NVARCHAR(255) NULL,
    nf_numero NVARCHAR(40) NULL,
    qt INT NULL,
    valor DECIMAL(14,2) NULL,
    qt_faturado INT NULL,
    valor_faturado DECIMAL(14,2) NOT NULL DEFAULT 0,
    previsao_entrega DATE NULL,
    recebimento DATE NULL,
    vencimento DATE NULL,
    vencimento_fornecedor DATE NULL,
    valor_dae DECIMAL(14,2) NOT NULL DEFAULT 0,
    status NVARCHAR(20) NOT NULL DEFAULT 'pendente',
    data_pagamento DATE NULL,
    valor_pago DECIMAL(14,2) NULL,
    observacoes NVARCHAR(500) NULL
);
CREATE INDEX idx_pedidos_loja ON pedidos(loja);
CREATE INDEX idx_pedidos_nf ON pedidos(loja, nf_numero);

CREATE TABLE boletos (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    loja NVARCHAR(10) NOT NULL,
    data DATE NULL,
    valor_pago DECIMAL(14,2) NOT NULL DEFAULT 0
);
CREATE INDEX idx_boletos_loja ON boletos(loja);

CREATE TABLE boleto_notas (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    boleto_id BIGINT NOT NULL,
    nf_numero NVARCHAR(40) NOT NULL,
    valor_dae DECIMAL(14,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_boleto_notas_boleto FOREIGN KEY (boleto_id) REFERENCES boletos(id) ON DELETE CASCADE
);
CREATE INDEX idx_boleto_notas_nf ON boleto_notas(nf_numero);

CREATE TABLE brinde_movimentos (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    item NVARCHAR(60) NOT NULL,
    tipo NVARCHAR(10) NOT NULL,
    quantidade INT NOT NULL,
    data DATE NULL,
    destino NVARCHAR(255) NULL,
    observacoes NVARCHAR(500) NULL
);
CREATE INDEX idx_brinde_item ON brinde_movimentos(item);
