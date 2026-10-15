/**
 * Nombre del artefacto: AuthController.java
 * Descripción: Controlador REST para la autenticación de usuarios y emisión de tokens JWT sin dependencia de Lombok.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.controller;

import com.ferreteria.inventario.dto.ApiResponseDto;
import com.ferreteria.inventario.dto.AuthRequestDto;
import com.ferreteria.inventario.dto.AuthResponseDto;
import com.ferreteria.inventario.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints para inicio de sesión y obtención de tokens de acceso JWT")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica credenciales y emite un token JWT con los roles del usuario (ROL_ADMIN, ROL_BODEGA, ROL_VENTAS).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa y emisión del token"),
            @ApiResponse(responseCode = "400", description = "Parámetros de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> authenticateUser(
            @Valid @RequestBody AuthRequestDto loginRequest) {

        log.info("Intento de autenticación para usuario: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        AuthResponseDto authResponse = AuthResponseDto.builder()
                .token(jwt)
                .tokenType("Bearer")
                .username(authentication.getName())
                .roles(roles)
                .expiresInMs(tokenProvider.getExpirationMs())
                .build();

        return ResponseEntity.ok(ApiResponseDto.success("Autenticación exitosa", authResponse));
    }
}
