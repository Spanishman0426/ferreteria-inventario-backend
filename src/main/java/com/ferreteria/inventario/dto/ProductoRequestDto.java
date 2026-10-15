/**
 * Nombre del artefacto: ProductoRequestDto.java
 * Descripción: DTO para las solicitudes de creación y actualización de productos con validaciones sin dependencia de Lombok.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.dto;

import com.ferreteria.inventario.model.enums.EstadoProducto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para el registro o actualización de un producto en el inventario")
public class ProductoRequestDto {

    @NotBlank(message = "{validation.producto.nombre.notblank}")
    @Size(min = 3, max = 100, message = "{validation.producto.nombre.size}")
    @Schema(description = "Nombre comercial del producto", example = "Martillo de Uña Curva 16oz", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "{validation.producto.categoria.notblank}")
    @Size(min = 2, max = 50, message = "{validation.producto.categoria.size}")
    @Schema(description = "Categoría o departamento del producto", example = "Herramientas Manuales", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoria;

    @Size(max = 500, message = "{validation.producto.descripcion.size}")
    @Schema(description = "Descripción detallada del producto y sus especificaciones", example = "Martillo de acero al carbono con mango ergonómico de fibra de vidrio anti-deslizante.")
    private String descripcion;

    @NotNull(message = "{validation.producto.estado.notnull}")
    @Schema(description = "Estado actual del producto en el inventario", example = "DISPONIBLE", requiredMode = Schema.RequiredMode.REQUIRED)
    private EstadoProducto estado;

    public ProductoRequestDto() {
    }

    public ProductoRequestDto(String nombre, String categoria, String descripcion, EstadoProducto estado) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoProducto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProducto estado) {
        this.estado = estado;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nombre;
        private String categoria;
        private String descripcion;
        private EstadoProducto estado;

        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder categoria(String categoria) {
            this.categoria = categoria;
            return this;
        }

        public Builder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public Builder estado(EstadoProducto estado) {
            this.estado = estado;
            return this;
        }

        public ProductoRequestDto build() {
            return new ProductoRequestDto(nombre, categoria, descripcion, estado);
        }
    }
}
