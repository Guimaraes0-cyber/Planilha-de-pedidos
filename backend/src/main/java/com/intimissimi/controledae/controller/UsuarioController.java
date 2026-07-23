package com.intimissimi.controledae.controller;

import com.intimissimi.controledae.dto.AuthDtos;
import com.intimissimi.controledae.model.Role;
import com.intimissimi.controledae.model.Usuario;
import com.intimissimi.controledae.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<AuthDtos.UsuarioResponse> listar() {
        return usuarioService.listar().stream()
                .map(u -> new AuthDtos.UsuarioResponse(u.getId(), u.getUsername(), u.getRole().name(), u.isAtivo()))
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody AuthDtos.CriarUsuarioRequest req) {
        try {
            Usuario u = usuarioService.criar(req.username, req.senha, Role.valueOf(req.role));
            return ResponseEntity.ok(new AuthDtos.UsuarioResponse(u.getId(), u.getUsername(), u.getRole().name(), u.isAtivo()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Papel inválido. Use ADMIN, DIRETORIA, ADMINISTRATIVO ou MARKETING.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/desativar")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        usuarioService.desativar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/senha")
    public ResponseEntity<?> redefinirSenha(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        usuarioService.redefinirSenha(id, body.get("novaSenha"));
        return ResponseEntity.ok().build();
    }
}
