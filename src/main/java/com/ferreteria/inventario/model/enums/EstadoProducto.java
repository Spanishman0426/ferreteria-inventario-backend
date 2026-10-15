/**
 * Nombre del artefacto: EstadoProducto.java
 * Descripción: Enumeración que define los estados válidos de un producto en el inventario.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estado operativo del producto dentro del inventario de ferretería")
public enum EstadoProducto {
    DISPONIBLE,
    RESERVADA,
    DANADA
}
