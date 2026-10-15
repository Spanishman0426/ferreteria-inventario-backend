/**
 * Nombre del artefacto: ProductoRepository.java
 * Descripción: Repositorio Spring Data JPA para operaciones de persistencia de la entidad Producto.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.repository;

import com.ferreteria.inventario.model.entity.Producto;
import com.ferreteria.inventario.model.enums.EstadoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    List<Producto> findByCategoriaIgnoreCase(String categoria);

    List<Producto> findByEstado(EstadoProducto estado);
}
