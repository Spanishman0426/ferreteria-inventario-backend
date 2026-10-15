/**
 * Nombre del artefacto: ProductoServiceImplTest.java
 * Descripción: Pruebas unitarias para la lógica de negocio de ProductoServiceImpl utilizando JUnit 5 y Mockito.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.service;

import com.ferreteria.inventario.dto.ProductoRequestDto;
import com.ferreteria.inventario.dto.ProductoResponseDto;
import com.ferreteria.inventario.exception.BadRequestException;
import com.ferreteria.inventario.exception.ResourceNotFoundException;
import com.ferreteria.inventario.model.entity.Producto;
import com.ferreteria.inventario.model.enums.EstadoProducto;
import com.ferreteria.inventario.repository.ProductoRepository;
import com.ferreteria.inventario.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - ProductoServiceImpl")
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto productoSample;
    private ProductoRequestDto requestDtoSample;

    @BeforeEach
    void setUp() {
        productoSample = Producto.builder()
                .id(1L)
                .nombre("Taladro Percutor 750W")
                .categoria("Herramientas Eléctricas")
                .descripcion("Taladro de velocidad variable reversible.")
                .estado(EstadoProducto.DISPONIBLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestDtoSample = ProductoRequestDto.builder()
                .nombre("Taladro Percutor 750W")
                .categoria("Herramientas Eléctricas")
                .descripcion("Taladro de velocidad variable reversible.")
                .estado(EstadoProducto.DISPONIBLE)
                .build();
    }

    @Test
    @DisplayName("Debe crear un producto exitosamente cuando los datos son válidos")
    void createProducto_WhenValid_ReturnsCreatedDto() {
        when(productoRepository.existsByNombreIgnoreCase(anyString())).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoSample);

        ProductoResponseDto result = productoService.createProducto(requestDtoSample);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Taladro Percutor 750W");
        assertThat(result.getEstado()).isEqualTo(EstadoProducto.DISPONIBLE);

        verify(productoRepository, times(1)).existsByNombreIgnoreCase("Taladro Percutor 750W");
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe lanzar BadRequestException al crear si el nombre ya existe")
    void createProducto_WhenDuplicateName_ThrowsBadRequestException() {
        when(productoRepository.existsByNombreIgnoreCase("Taladro Percutor 750W")).thenReturn(true);

        assertThatThrownBy(() -> productoService.createProducto(requestDtoSample))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("producto.name.exists");

        verify(productoRepository, times(1)).existsByNombreIgnoreCase("Taladro Percutor 750W");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe retornar el producto cuando existe el ID solicitado")
    void getProductoById_WhenExists_ReturnsProductoDto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoSample));

        ProductoResponseDto result = productoService.getProductoById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Taladro Percutor 750W");
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el ID no existe")
    void getProductoById_WhenNotExists_ThrowsResourceNotFoundException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.getProductoById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("producto.notfound");

        verify(productoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Debe listar todos los productos sin filtros aplicados")
    void getAllProductos_WithoutFilters_ReturnsList() {
        when(productoRepository.findAll()).thenReturn(List.of(productoSample));

        List<ProductoResponseDto> result = productoService.getAllProductos(null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Taladro Percutor 750W");
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe listar productos filtrando por categoría y estado")
    void getAllProductos_WithCategoryAndStatus_ReturnsFilteredList() {
        when(productoRepository.findByCategoriaIgnoreCase("Herramientas Eléctricas"))
                .thenReturn(List.of(productoSample));

        List<ProductoResponseDto> result = productoService.getAllProductos("Herramientas Eléctricas", EstadoProducto.DISPONIBLE);

        assertThat(result).hasSize(1);
        verify(productoRepository, times(1)).findByCategoriaIgnoreCase("Herramientas Eléctricas");
    }

    @Test
    @DisplayName("Debe actualizar un producto exitosamente cuando los datos son válidos")
    void updateProducto_WhenValid_ReturnsUpdatedDto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoSample));
        when(productoRepository.existsByNombreIgnoreCaseAndIdNot("Taladro Percutor 750W", 1L)).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoSample);

        ProductoResponseDto result = productoService.updateProducto(1L, requestDtoSample);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe lanzar BadRequestException al actualizar si el nuevo nombre ya está ocupado")
    void updateProducto_WhenDuplicateName_ThrowsBadRequestException() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoSample));
        when(productoRepository.existsByNombreIgnoreCaseAndIdNot("Taladro Percutor 750W", 1L)).thenReturn(true);

        assertThatThrownBy(() -> productoService.updateProducto(1L, requestDtoSample))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("producto.name.exists");

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe eliminar el producto exitosamente cuando el ID existe")
    void deleteProducto_WhenExists_DeletesEntity() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoSample));
        doNothing().when(productoRepository).delete(productoSample);

        productoService.deleteProducto(1L);

        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).delete(productoSample);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException al eliminar si el ID no existe")
    void deleteProducto_WhenNotExists_ThrowsResourceNotFoundException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.deleteProducto(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("producto.notfound");

        verify(productoRepository, never()).delete(any(Producto.class));
    }
}
