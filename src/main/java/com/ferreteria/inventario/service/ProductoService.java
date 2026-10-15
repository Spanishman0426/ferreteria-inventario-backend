/**
 * Nombre del artefacto: ProductoService.java
 * Descripción: Contrato de servicios de negocio para las operaciones CRUD y gestión de productos.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.service;

import com.ferreteria.inventario.dto.ProductoRequestDto;
import com.ferreteria.inventario.dto.ProductoResponseDto;
import com.ferreteria.inventario.model.enums.EstadoProducto;

import java.util.List;

public interface ProductoService {

    /**
     * Registra un nuevo producto en el catálogo de inventario.
     *
     * @param requestDto Datos de creación del producto
     * @return Producto registrado con identificador asignado
     */
    ProductoResponseDto createProducto(ProductoRequestDto requestDto);

    /**
     * Obtiene el detalle de un producto por su ID único.
     *
     * @param id Identificador numérico del producto
     * @return Detalle del producto
     */
    ProductoResponseDto getProductoById(Long id);

    /**
     * Consulta el catálogo de productos con filtros opcionales.
     *
     * @param categoria Filtro opcional por categoría
     * @param estado    Filtro opcional por estado
     * @return Listado de productos encontrados
     */
    List<ProductoResponseDto> getAllProductos(String categoria, EstadoProducto estado);

    /**
     * Actualiza la información de un producto existente.
     *
     * @param id         Identificador numérico del producto a modificar
     * @param requestDto Datos actualizados del producto
     * @return Producto con la información actualizada
     */
    ProductoResponseDto updateProducto(Long id, ProductoRequestDto requestDto);

    /**
     * Elimina físicamente un producto del inventario por su ID.
     *
     * @param id Identificador numérico del producto a eliminar
     */
    void deleteProducto(Long id);
}
