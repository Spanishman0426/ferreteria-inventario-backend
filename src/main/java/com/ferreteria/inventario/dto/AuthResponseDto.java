/**
 * Nombre del artefacto: AuthResponseDto.java
 * Descripción: DTO que transporta el token JWT emitido y la información de la sesión sin dependencia de Lombok.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Respuesta de autenticación exitosa con token JWT")
public class AuthResponseDto {

    @Schema(description = "Token de acceso en formato JWT", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Tipo de token para la cabecera Authorization", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Nombre de usuario autenticado", example = "admin")
    private String username;

    @Schema(description = "Roles asignados al usuario en el sistema", example = "[\"ROL_ADMIN\"]")
    private List<String> roles;

    @Schema(description = "Tiempo de expiración en milisegundos", example = "86400000")
    private long expiresInMs;

    public AuthResponseDto() {
        this.tokenType = "Bearer";
    }

    public AuthResponseDto(String token, String tokenType, String username, List<String> roles, long expiresInMs) {
        this.token = token;
        this.tokenType = (tokenType != null) ? tokenType : "Bearer";
        this.username = username;
        this.roles = roles;
        this.expiresInMs = expiresInMs;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public long getExpiresInMs() {
        return expiresInMs;
    }

    public void setExpiresInMs(long expiresInMs) {
        this.expiresInMs = expiresInMs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String tokenType = "Bearer";
        private String username;
        private List<String> roles;
        private long expiresInMs;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder expiresInMs(long expiresInMs) {
            this.expiresInMs = expiresInMs;
            return this;
        }

        public AuthResponseDto build() {
            return new AuthResponseDto(token, tokenType, username, roles, expiresInMs);
        }
    }
}
