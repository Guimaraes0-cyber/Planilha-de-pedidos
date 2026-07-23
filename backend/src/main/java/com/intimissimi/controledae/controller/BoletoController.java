package com.intimissimi.controledae.controller;

import com.intimissimi.controledae.dto.BoletoDtos;
import com.intimissimi.controledae.model.Boleto;
import com.intimissimi.controledae.model.Loja;
import com.intimissimi.controledae.service.BoletoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boletos")
public class BoletoController {

    private final BoletoService boletoService;

    public BoletoController(BoletoService boletoService) {
        this.boletoService = boletoService;
    }

    @GetMapping("/{loja}")
    public List<Boleto> listar(@PathVariable Loja loja) {
        return boletoService.listarPorLoja(loja);
    }

    @PostMapping("/{loja}")
    public ResponseEntity<?> criar(@PathVariable Loja loja, @RequestBody BoletoDtos.BoletoRequest req) {
        List<String> naoEncontradas = boletoService.salvarEConciliar(null, loja, req);
        return ResponseEntity.ok(Map.of("naoEncontradas", naoEncontradas));
    }

    @PutMapping("/{loja}/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Loja loja, @PathVariable Long id, @RequestBody BoletoDtos.BoletoRequest req) {
        List<String> naoEncontradas = boletoService.salvarEConciliar(id, loja, req);
        return ResponseEntity.ok(Map.of("naoEncontradas", naoEncontradas));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        boletoService.excluir(id);
        return ResponseEntity.ok().build();
    }
}
