package com.intimissimi.controledae.config;

import com.intimissimi.controledae.model.Role;
import com.intimissimi.controledae.model.Usuario;
import com.intimissimi.controledae.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.senha-inicial:Adm@2026}")
    private String adminSenhaInicial;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername(adminUsername);
            admin.setSenhaHash(passwordEncoder.encode(adminSenhaInicial));
            admin.setRole(Role.ADMIN);
            admin.setAtivo(true);
            usuarioRepository.save(admin);
            System.out.println("========================================================");
            System.out.println(" Usuário admin master criado:");
            System.out.println(" usuário: " + adminUsername);
            System.out.println(" senha inicial: " + adminSenhaInicial + "  (troque depois do primeiro login)");
            System.out.println("========================================================");
        }
    }
}
