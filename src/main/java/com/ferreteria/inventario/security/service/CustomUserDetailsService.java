/**
 * Nombre del artefacto: CustomUserDetailsService.java
 * Descripción: Servicio de autenticación que gestiona usuarios y roles en memoria con contraseñas encriptadas mediante BCrypt sin dependencia de Lombok.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.security.service;

import com.ferreteria.inventario.model.enums.RoleEnum;
import com.ferreteria.inventario.security.model.UserPrincipal;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final PasswordEncoder passwordEncoder;
    private final Map<String, UserPrincipal> usersMap = new ConcurrentHashMap<>();

    @Autowired
    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initUsers() {
        // Inicialización de usuarios predeterminados para pruebas y demostración de roles RBAC
        usersMap.put("admin", UserPrincipal.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(RoleEnum.ROL_ADMIN)
                .enabled(true)
                .build());

        usersMap.put("bodega", UserPrincipal.builder()
                .username("bodega")
                .password(passwordEncoder.encode("bodega123"))
                .role(RoleEnum.ROL_BODEGA)
                .enabled(true)
                .build());

        usersMap.put("ventas", UserPrincipal.builder()
                .username("ventas")
                .password(passwordEncoder.encode("ventas123"))
                .role(RoleEnum.ROL_VENTAS)
                .enabled(true)
                .build());

        log.info("Usuarios RBAC de prueba inicializados correctamente: admin, bodega, ventas.");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserPrincipal user = usersMap.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado en el sistema: " + username);
        }
        return user;
    }
}
