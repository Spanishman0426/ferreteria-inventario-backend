/**
 * Nombre del artefacto: RoleEnum.java
 * Descripción: Enumeración para los roles del sistema de seguridad (RBAC).
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Roles de usuario del sistema")
public enum RoleEnum {
    ROL_ADMIN,
    ROL_BODEGA,
    ROL_VENTAS
}
