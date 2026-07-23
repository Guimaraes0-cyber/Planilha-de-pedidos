package com.intimissimi.controledae.service;

import com.intimissimi.controledae.dto.BoletoDtos;
import com.intimissimi.controledae.model.Boleto;
import com.intimissimi.controledae.model.BoletoNota;
import com.intimissimi.controledae.model.Loja;
import com.intimissimi.controledae.model.Pedido;
import com.intimissimi.controledae.repository.BoletoRepository;
import com.intimissimi.controledae.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BoletoService {

    private final BoletoRepository boletoRepo;
    private final PedidoRepository pedidoRepo;

    public BoletoService(BoletoRepository boletoRepo, PedidoRepository pedidoRepo) {
        this.boletoRepo = boletoRepo;
        this.pedidoRepo = pedidoRepo;
    }

    public List<Boleto> listarPorLoja(Loja loja) {
        return boletoRepo.findByLojaOrderByDataDesc(loja);
    }

    /**
     * Cria (ou atualiza, se boletoId != null) um boleto de DAE para uma loja,
     * e concilia automaticamente cada NF listada com o pedido correspondente
     * NESSA MESMA LOJA — marcando como pago.
     *
     * Retorna a lista de números de NF que não foram encontrados como pedido
     * lançado, pra o front avisar o usuário.
     */
    @Transactional
    public List<String> salvarEConciliar(Long boletoId, Loja loja, BoletoDtos.BoletoRequest req) {
        Boleto boleto = (boletoId != null)
                ? boletoRepo.findById(boletoId).orElseThrow(() -> new RuntimeException("Boleto não encontrado"))
                : new Boleto();

        boleto.setLoja(loja);
        boleto.setData(req.data);
        boleto.setValorPago(req.valorPago);

        boleto.getNotas().clear();
        List<String> naoEncontradas = new ArrayList<>();

        for (BoletoDtos.NotaRequest n : req.notas) {
            if (n.nfNumero == null || n.nfNumero.isBlank()) continue;
            BoletoNota nota = new BoletoNota();
            nota.setBoleto(boleto);
            nota.setNfNumero(n.nfNumero.trim());
            nota.setValorDae(n.valorDae);
            boleto.getNotas().add(nota);

            Optional<Pedido> pedidoOpt = pedidoRepo.findByLojaAndNfNumero(loja, n.nfNumero.trim());
            if (pedidoOpt.isPresent()) {
                Pedido p = pedidoOpt.get();
                p.setStatus("pago");
                p.setDataPagamento(req.data);
                p.setValorPago(n.valorDae);
                pedidoRepo.save(p);
            } else {
                naoEncontradas.add(n.nfNumero.trim());
            }
        }

        boletoRepo.save(boleto);
        return naoEncontradas;
    }

    public void excluir(Long id) {
        boletoRepo.deleteById(id);
    }
}
