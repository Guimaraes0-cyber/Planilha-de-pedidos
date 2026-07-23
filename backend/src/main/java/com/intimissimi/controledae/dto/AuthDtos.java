package com.intimissimi.controledae.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public static class LoginRequest {
        @NotBlank
        public String username;
        @NotBlank
        public String senha;
    }

    public static class LoginResponse {
        public String token;
        public String username;
        public String role;

        public LoginResponse(String token, String username, String role) {
            this.token = token;
            this.username = username;
            this.role = role;
        }
    }

    public static class CriarUsuarioRequest {
        @NotBlank
        public String username;
        @NotBlank
        public String senha;
        @NotBlank
        public String role; // ADMIN | DIRETORIA | ADMINISTRATIVO | MARKETING
    }

    public static class UsuarioResponse {
        public Long id;
        public String username;
        public String role;
        public boolean ativo;

        public UsuarioResponse(Long id, String username, String role, boolean ativo) {
            this.id = id;
            this.username = username;
            this.role = role;
            this.ativo = ativo;
        }
    }
}
