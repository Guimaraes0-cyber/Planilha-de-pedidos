package com.intimissimi.controledae.controller;

import com.intimissimi.controledae.model.BrindeMovimento;
import com.intimissimi.controledae.service.BrindeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/brindes")
public class BrindeController {

    private final BrindeService brindeService;

    public BrindeController(BrindeService brindeService) {
        this.brindeService = brindeService;
    }

    @GetMapping("/itens")
    public List<String> itens() {
        return BrindeService.ITENS;
    }

    @GetMapping("/estoque")
    public Map<String, Integer> estoque() {
        return brindeService.estoquePorItem();
    }

    @GetMapping("/movimentos")
    public List<BrindeMovimento> movimentos() {
        return brindeService.listarTodos();
    }

    @GetMapping("/movimentos/{item}")
    public List<BrindeMovimento> historico(@PathVariable String item) {
        return brindeService.historico(item);
    }

    @PostMapping("/movimentos")
    public BrindeMovimento registrar(@RequestBody BrindeMovimento mov) {
        return brindeService.registrar(mov);
    }

    @DeleteMapping("/movimentos/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        brindeService.excluir(id);
        return ResponseEntity.ok().build();
    }
}
