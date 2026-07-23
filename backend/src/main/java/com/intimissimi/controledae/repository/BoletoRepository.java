package com.intimissimi.controledae.repository;

import com.intimissimi.controledae.model.Boleto;
import com.intimissimi.controledae.model.Loja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    List<Boleto> findByLojaOrderByDataDesc(Loja loja);
}
