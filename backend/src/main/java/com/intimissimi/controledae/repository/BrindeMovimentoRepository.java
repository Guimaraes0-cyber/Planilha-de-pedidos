package com.intimissimi.controledae.repository;

import com.intimissimi.controledae.model.BrindeMovimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrindeMovimentoRepository extends JpaRepository<BrindeMovimento, Long> {
    List<BrindeMovimento> findByItemOrderByDataDesc(String item);
    List<BrindeMovimento> findAllByOrderByDataDesc();
}
