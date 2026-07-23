package com.intimissimi.controledae.repository;

import com.intimissimi.controledae.model.Loja;
import com.intimissimi.controledae.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByLojaOrderByDataDesc(Loja loja);
    Optional<Pedido> findByLojaAndNfNumero(Loja loja, String nfNumero);
    List<Pedido> findAllByLojaIn(List<Loja> lojas);
}
