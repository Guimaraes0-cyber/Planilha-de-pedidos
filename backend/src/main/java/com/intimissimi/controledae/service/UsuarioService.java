
package com.intimissimi.controledae.service;

import com.intimissimi.controledae.model.Role;
import com.intimissimi.controledae.model.Usuario;
import com.intimissimi.controledae.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public Optional<Usuario> autenticar(String username, String senha) {
        return repo.findByUsernameIgnoreCase(username)
                .filter(Usuario::isAtivo)
                .filter(u -> encoder.matches(senha, u.getSenhaHash()));
    }

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Usuario criar(String username, String senha, Role role) {
        if (repo.existsByUsernameIgnoreCase(username)) {
            throw new RuntimeException("Já existe um usuário com esse nome.");
        }
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setSenhaHash(encoder.encode(senha));
        u.setRole(role);
        u.setAtivo(true);
        return repo.save(u);
    }

    public void desativar(Long id) {
        Usuario u = repo.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        u.setAtivo(false);
        repo.save(u);
    }

    public void redefinirSenha(Long id, String novaSenha) {
        Usuario u = repo.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        u.setSenhaHash(encoder.encode(novaSenha));
        repo.save(u);
    }

    public void trocarPropriaSenha(String username, String senhaAtual, String novaSenha) {
        Usuario u = repo.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!encoder.matches(senhaAtual, u.getSenhaHash())) {
            throw new RuntimeException("Senha atual incorreta.");
        }
        u.setSenhaHash(encoder.encode(novaSenha));
        repo.save(u);
    }
}
