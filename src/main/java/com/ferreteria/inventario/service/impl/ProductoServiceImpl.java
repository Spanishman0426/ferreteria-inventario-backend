/**
 * Nombre del artefacto: ProductoServiceImpl.java
 * Descripción: Implementación de la lógica de negocio para la gestión de productos con validaciones y mapeo sin dependencia de Lombok.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.service.impl;

import com.ferreteria.inventario.dto.ProductoRequestDto;
import com.ferreteria.inventario.dto.ProductoResponseDto;
import com.ferreteria.inventario.exception.BadRequestException;
import com.ferreteria.inventario.exception.ResourceNotFoundException;
import com.ferreteria.inventario.model.entity.Producto;
import com.ferreteria.inventario.model.enums.EstadoProducto;
import com.ferreteria.inventario.repository.ProductoRepository;
import com.ferreteria.inventario.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoServiceImpl.class);

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public ProductoResponseDto createProducto(ProductoRequestDto requestDto) {
        log.info("Iniciando creación de producto con nombre: {}", requestDto.getNombre());

        if (productoRepository.existsByNombreIgnoreCase(requestDto.getNombre())) {
            throw new BadRequestException("producto.name.exists", requestDto.getNombre());
        }

        Producto producto = Producto.builder()
                .nombre(requestDto.getNombre().trim())
                .categoria(requestDto.getCategoria().trim())
                .descripcion(requestDto.getDescripcion() != null ? requestDto.getDescripcion().trim() : null)
                .estado(requestDto.getEstado())
                .build();

        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado exitosamente con ID: {}", guardado.getId());

        return mapToResponseDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDto getProductoById(Long id) {
        log.info("Consultando producto con ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("producto.notfound", id));

        return mapToResponseDto(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDto> getAllProductos(String categoria, EstadoProducto estado) {
        log.info("Listando productos - Filtros: categoria={}, estado={}", categoria, estado);

        List<Producto> productos;

        if (categoria != null && !categoria.isBlank() && estado != null) {
            productos = productoRepository.findByCategoriaIgnoreCase(categoria.trim()).stream()
                    .filter(p -> p.getEstado() == estado)
                    .collect(Collectors.toList());
        } else if (categoria != null && !categoria.isBlank()) {
            productos = productoRepository.findByCategoriaIgnoreCase(categoria.trim());
        } else if (estado != null) {
            productos = productoRepository.findByEstado(estado);
        } else {
            productos = productoRepository.findAll();
        }

        return productos.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductoResponseDto updateProducto(Long id, ProductoRequestDto requestDto) {
        log.info("Iniciando actualización para el producto con ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("producto.notfound", id));

        if (productoRepository.existsByNombreIgnoreCaseAndIdNot(requestDto.getNombre(), id)) {
            throw new BadRequestException("producto.name.exists", requestDto.getNombre());
        }

        producto.setNombre(requestDto.getNombre().trim());
        producto.setCategoria(requestDto.getCategoria().trim());
        producto.setDescripcion(requestDto.getDescripcion() != null ? requestDto.getDescripcion().trim() : null);
        producto.setEstado(requestDto.getEstado());

        Producto actualizado = productoRepository.save(producto);
        log.info("Producto con ID: {} actualizado exitosamente", actualizado.getId());

        return mapToResponseDto(actualizado);
    }

    @Override
    @Transactional
    public void deleteProducto(Long id) {
        log.info("Iniciando eliminación de producto con ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("producto.notfound", id));

        productoRepository.delete(producto);
        log.info("Producto con ID: {} eliminado exitosamente", id);
    }

    private ProductoResponseDto mapToResponseDto(Producto entity) {
        return ProductoResponseDto.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .categoria(entity.getCategoria())
                .descripcion(entity.getDescripcion())
                .estado(entity.getEstado())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
