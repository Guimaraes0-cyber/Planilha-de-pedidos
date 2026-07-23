package com.intimissimi.controledae.controller;

import com.intimissimi.controledae.dto.AuthDtos;
import com.intimissimi.controledae.model.Usuario;
import com.intimissimi.controledae.security.JwtUtil;
import com.intimissimi.controledae.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDtos.LoginRequest req) {
        return usuarioService.autenticar(req.username, req.senha)
                .map(u -> {
                    String token = jwtUtil.gerarToken(u.getUsername(), u.getRole().name());
                    return ResponseEntity.ok(new AuthDtos.LoginResponse(token, u.getUsername(), u.getRole().name()));
                })
                .orElseGet(() -> ResponseEntity.status(401).body("Usuário ou senha incorretos."));
    }

    @PostMapping("/trocar-senha")
    public ResponseEntity<?> trocarSenha(@RequestBody java.util.Map<String, String> body,
                                          org.springframework.security.core.Authentication authentication) {
        try {
            usuarioService.trocarPropriaSenha(authentication.getName(), body.get("senhaAtual"), body.get("novaSenha"));
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
