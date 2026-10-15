/**
 * Nombre del artefacto: ProductoIntegrationTest.java
 * Descripción: Pruebas de integración de extremo a extremo (E2E) con Spring Boot Test, H2 y seguridad JWT.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferreteria.inventario.dto.AuthRequestDto;
import com.ferreteria.inventario.dto.ProductoRequestDto;
import com.ferreteria.inventario.model.enums.EstadoProducto;
import com.ferreteria.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Pruebas de Integración E2E - Flujo Completo Producto")
class ProductoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoRepository productoRepository;

    private String adminToken;
    private String ventasToken;

    @BeforeEach
    void setUp() throws Exception {
        productoRepository.deleteAll();

        // Obtener token JWT para ROL_ADMIN
        AuthRequestDto adminLogin = new AuthRequestDto("admin", "admin123");
        MvcResult adminResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode adminJson = objectMapper.readTree(adminResult.getResponse().getContentAsString());
        adminToken = adminJson.get("data").get("token").asText();

        // Obtener token JWT para ROL_VENTAS
        AuthRequestDto ventasLogin = new AuthRequestDto("ventas", "ventas123");
        MvcResult ventasResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ventasLogin)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode ventasJson = objectMapper.readTree(ventasResult.getResponse().getContentAsString());
        ventasToken = ventasJson.get("data").get("token").asText();
    }

    @Test
    @DisplayName("Flujo E2E: Autenticar -> Crear Producto -> Consultar con i18n -> Actualizar -> Eliminar")
    void fullProductLifecycle_E2E() throws Exception {
        // 1. Crear Producto como ADMIN
        ProductoRequestDto createDto = ProductoRequestDto.builder()
                .nombre("Cinta Métrica 5 Metros")
                .categoria("Medición")
                .descripcion("Cinta métrica de alta durabilidad con traba automática.")
                .estado(EstadoProducto.DISPONIBLE)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/productos")
                        .header("Authorization", "Bearer " + adminToken)
                        .header("Accept-Language", "es")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producto creado exitosamente"))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andReturn();

        JsonNode createdNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long productoId = createdNode.get("data").get("id").asLong();

        // 2. Consultar Producto como VENTAS con cabecera i18n en Inglés ('en')
        mockMvc.perform(get("/api/v1/productos/" + productoId)
                        .header("Authorization", "Bearer " + ventasToken)
                        .header("Accept-Language", "en")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product successfully retrieved"))
                .andExpect(jsonPath("$.data.nombre").value("Cinta Métrica 5 Metros"));

        // 3. Ventas intenta eliminar -> Espera 403 Forbidden
        mockMvc.perform(delete("/api/v1/productos/" + productoId)
                        .header("Authorization", "Bearer " + ventasToken))
                .andExpect(status().isForbidden());

        // 4. Actualizar Producto como ADMIN
        ProductoRequestDto updateDto = ProductoRequestDto.builder()
                .nombre("Cinta Métrica Profesional 5 Metros")
                .categoria("Medición")
                .descripcion("Cinta métrica mejorada con gancho magnético.")
                .estado(EstadoProducto.RESERVADA)
                .build();

        mockMvc.perform(put("/api/v1/productos/" + productoId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Cinta Métrica Profesional 5 Metros"))
                .andExpect(jsonPath("$.data.estado").value("RESERVADA"));

        // 5. Eliminar Producto como ADMIN
        mockMvc.perform(delete("/api/v1/productos/" + productoId)
                        .header("Authorization", "Bearer " + adminToken)
                        .header("Accept-Language", "es"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 6. Verificar que ya no existe (404)
        mockMvc.perform(get("/api/v1/productos/" + productoId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        assertThat(productoRepository.findById(productoId)).isEmpty();
    }
}
