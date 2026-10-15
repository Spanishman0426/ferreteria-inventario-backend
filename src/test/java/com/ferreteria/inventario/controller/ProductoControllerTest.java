/**
 * Nombre del artefacto: ProductoControllerTest.java
 * Descripción: Pruebas unitarias de la capa web para ProductoController con MockMvc, validaciones y roles RBAC.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferreteria.inventario.config.I18nConfig;
import com.ferreteria.inventario.config.SecurityConfig;
import com.ferreteria.inventario.dto.ProductoRequestDto;
import com.ferreteria.inventario.dto.ProductoResponseDto;
import com.ferreteria.inventario.exception.ResourceNotFoundException;
import com.ferreteria.inventario.model.enums.EstadoProducto;
import com.ferreteria.inventario.security.jwt.JwtAccessDeniedHandler;
import com.ferreteria.inventario.security.jwt.JwtAuthenticationEntryPoint;
import com.ferreteria.inventario.security.jwt.JwtAuthenticationFilter;
import com.ferreteria.inventario.security.jwt.JwtTokenProvider;
import com.ferreteria.inventario.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@Import({SecurityConfig.class, I18nConfig.class})
@DisplayName("Pruebas de Controlador Web - ProductoController")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @MockBean
    private JwtAccessDeniedHandler accessDeniedHandler;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    private ProductoResponseDto responseDto;
    private ProductoRequestDto requestDto;

    @BeforeEach
    void setUp() {
        responseDto = ProductoResponseDto.builder()
                .id(1L)
                .nombre("Juego de Llaves Allen 9 Pzas")
                .categoria("Herramientas Manuales")
                .descripcion("Juego de llaves hexagonales milimétricas.")
                .estado(EstadoProducto.DISPONIBLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestDto = ProductoRequestDto.builder()
                .nombre("Juego de Llaves Allen 9 Pzas")
                .categoria("Herramientas Manuales")
                .descripcion("Juego de llaves hexagonales milimétricas.")
                .estado(EstadoProducto.DISPONIBLE)
                .build();
    }

    @Test
    @WithMockUser(authorities = {"ROL_ADMIN"})
    @DisplayName("POST /api/v1/productos - Debe retornar 201 Created cuando usuario tiene ROL_ADMIN")
    void createProducto_WithAdminRole_ReturnsCreated() throws Exception {
        when(productoService.createProducto(any(ProductoRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/productos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.nombre").value("Juego de Llaves Allen 9 Pzas"));
    }

    @Test
    @WithMockUser(authorities = {"ROL_VENTAS"})
    @DisplayName("POST /api/v1/productos - Debe retornar 403 Forbidden cuando usuario tiene solo ROL_VENTAS")
    void createProducto_WithVentasRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());

        verify(productoService, never()).createProducto(any());
    }

    @Test
    @WithMockUser(authorities = {"ROL_ADMIN"})
    @DisplayName("POST /api/v1/productos - Debe retornar 400 Bad Request cuando el DTO falla validaciones (@NotBlank/@NotNull)")
    void createProducto_WithInvalidData_ReturnsBadRequest() throws Exception {
        ProductoRequestDto invalidDto = ProductoRequestDto.builder()
                .nombre("") // Blanco inválido
                .categoria("") // Blanco inválido
                .estado(null) // Nulo inválido
                .build();

        mockMvc.perform(post("/api/v1/productos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray());

        verify(productoService, never()).createProducto(any());
    }

    @Test
    @WithMockUser(authorities = {"ROL_VENTAS"})
    @DisplayName("GET /api/v1/productos/{id} - Debe retornar 200 OK cuando existe el producto para ROL_VENTAS")
    void getProductoById_WhenExists_ReturnsOk() throws Exception {
        when(productoService.getProductoById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/productos/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.nombre").value("Juego de Llaves Allen 9 Pzas"));
    }

    @Test
    @WithMockUser(authorities = {"ROL_BODEGA"})
    @DisplayName("GET /api/v1/productos/{id} - Debe retornar 404 Not Found cuando no existe")
    void getProductoById_WhenNotFound_ReturnsNotFound() throws Exception {
        when(productoService.getProductoById(99L)).thenThrow(new ResourceNotFoundException("producto.notfound", 99L));

        mockMvc.perform(get("/api/v1/productos/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(authorities = {"ROL_BODEGA"})
    @DisplayName("GET /api/v1/productos - Debe retornar 200 OK con lista de productos")
    void getAllProductos_ReturnsOkList() throws Exception {
        when(productoService.getAllProductos(null, null)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/productos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    @WithMockUser(authorities = {"ROL_BODEGA"})
    @DisplayName("PUT /api/v1/productos/{id} - Debe retornar 200 OK cuando ROL_BODEGA actualiza")
    void updateProducto_WithBodegaRole_ReturnsOk() throws Exception {
        when(productoService.updateProducto(eq(1L), any(ProductoRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/productos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Juego de Llaves Allen 9 Pzas"));
    }

    @Test
    @WithMockUser(authorities = {"ROL_BODEGA"})
    @DisplayName("DELETE /api/v1/productos/{id} - Debe retornar 403 Forbidden cuando ROL_BODEGA intenta eliminar")
    void deleteProducto_WithBodegaRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/productos/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(productoService, never()).deleteProducto(any());
    }

    @Test
    @WithMockUser(authorities = {"ROL_ADMIN"})
    @DisplayName("DELETE /api/v1/productos/{id} - Debe retornar 200 OK cuando ROL_ADMIN elimina")
    void deleteProducto_WithAdminRole_ReturnsOk() throws Exception {
        doNothing().when(productoService).deleteProducto(1L);

        mockMvc.perform(delete("/api/v1/productos/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(productoService, times(1)).deleteProducto(1L);
    }
}
