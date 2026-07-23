package com.intimissimi.controledae.service;

import com.intimissimi.controledae.model.BrindeMovimento;
import com.intimissimi.controledae.repository.BrindeMovimentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BrindeService {

    public static final List<String> ITENS = List.of(
        "Vela Intimissimi", "Vela Calzedonia", "Necessaire", "Toalha", "Ecobag",
        "Cheirinho", "Manta Intimissimi", "Pantufa", "Bolsa",
        "Bombom Intimissimi", "Bombom Calzedonia"
    );

    private final BrindeMovimentoRepository repo;

    public BrindeService(BrindeMovimentoRepository repo) {
        this.repo = repo;
    }

    public List<BrindeMovimento> listarTodos() {
        return repo.findAllByOrderByDataDesc();
    }

    public List<BrindeMovimento> historico(String item) {
        return repo.findByItemOrderByDataDesc(item);
    }

    public BrindeMovimento registrar(BrindeMovimento mov) {
        return repo.save(mov);
    }

    public void excluir(Long id) {
        repo.deleteById(id);
    }

    public Map<String, Integer> estoquePorItem() {
        List<BrindeMovimento> todos = repo.findAll();
        return ITENS.stream().collect(Collectors.toMap(
            item -> item,
            item -> todos.stream()
                .filter(m -> m.getItem().equals(item))
                .mapToInt(m -> "entrada".equals(m.getTipo()) ? m.getQuantidade() : -m.getQuantidade())
                .sum()
        ));
    }
}
