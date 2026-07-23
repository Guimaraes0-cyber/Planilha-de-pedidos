package com.intimissimi.controledae.service;

import com.intimissimi.controledae.model.Loja;
import com.intimissimi.controledae.model.Pedido;
import com.intimissimi.controledae.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository repo;
    private final SelagemService selagemService;

    public PedidoService(PedidoRepository repo, SelagemService selagemService) {
        this.repo = repo;
        this.selagemService = selagemService;
    }

    public List<Pedido> listarPorLoja(Loja loja) {
        return repo.findByLojaOrderByDataDesc(loja);
    }

    public Pedido buscar(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
    }

    public Optional<Pedido> buscarPorNf(Loja loja, String nfNumero) {
        return repo.findByLojaAndNfNumero(loja, nfNumero);
    }

    public Pedido salvar(Pedido pedido) {
        if (pedido.getValorDae() == null || pedido.getValorDae().signum() == 0) {
            pedido.setValorDae(selagemService.calcularDaeEsperado(pedido.getValorFaturado()));
        }
        if (pedido.getStatus() == null) {
            pedido.setStatus("pendente");
        }
        return repo.save(pedido);
    }

    public void excluir(Long id) {
        repo.deleteById(id);
    }

    public void marcarPago(Long id, java.time.LocalDate dataPagamento, java.math.BigDecimal valorPago) {
        Pedido p = buscar(id);
        p.setStatus("pago");
        p.setDataPagamento(dataPagamento);
        p.setValorPago(valorPago);
        repo.save(p);
    }
}
