package com.intimissimi.controledae.controller;

import com.intimissimi.controledae.model.Loja;
import com.intimissimi.controledae.model.Pedido;
import com.intimissimi.controledae.service.PedidoService;
import com.intimissimi.controledae.service.SelagemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final SelagemService selagemService;

    public PedidoController(PedidoService pedidoService, SelagemService selagemService) {
        this.pedidoService = pedidoService;
        this.selagemService = selagemService;
    }

    @GetMapping("/{loja}")
    public List<Pedido> listar(@PathVariable Loja loja) {
        return pedidoService.listarPorLoja(loja);
    }

    @PostMapping
    public Pedido criar(@RequestBody Pedido pedido) {
        return pedidoService.salvar(pedido);
    }

    @PutMapping("/{id}")
    public Pedido atualizar(@PathVariable Long id, @RequestBody Pedido pedido) {
        pedido.setId(id);
        return pedidoService.salvar(pedido);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        pedidoService.excluir(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<?> marcarPago(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        LocalDate data = LocalDate.parse((String) body.get("dataPagamento"));
        BigDecimal valor = new BigDecimal(body.get("valorPago").toString());
        pedidoService.marcarPago(id, data, valor);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/status-selagem")
    public Map<String, String> statusSelagem(@PathVariable Long id) {
        Pedido p = pedidoService.buscar(id);
        return Map.of(
            "selagem", selagemService.statusSelagem(p),
            "vencimento", selagemService.statusVencimento(p),
            "categoria", selagemService.categoria(p.getPedido())
        );
    }
}
