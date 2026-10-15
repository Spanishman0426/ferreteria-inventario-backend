/**
 * Nombre del artefacto: ProductoResponseDto.java
 * Descripción: DTO de salida que contiene la información completa de un producto sin dependencia de Lombok.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ferreteria.inventario.model.enums.EstadoProducto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Datos de respuesta que representan un producto en el inventario")
public class ProductoResponseDto {

    @Schema(description = "Identificador único secuencial del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre comercial del producto", example = "Martillo de Uña Curva 16oz")
    private String nombre;

    @Schema(description = "Categoría del producto", example = "Herramientas Manuales")
    private String categoria;

    @Schema(description = "Descripción detallada del producto", example = "Martillo de acero al carbono con mango ergonómico de fibra de vidrio.")
    private String descripcion;

    @Schema(description = "Estado actual del producto en el inventario", example = "DISPONIBLE")
    private EstadoProducto estado;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Fecha y hora de creación del registro", example = "2026-10-13T10:15:30")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Fecha y hora de la última actualización", example = "2026-10-13T10:30:00")
    private LocalDateTime updatedAt;

    public ProductoResponseDto() {
    }

    public ProductoResponseDto(Long id, String nombre, String categoria, String descripcion, EstadoProducto estado, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.estado = estado;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nombre;
        private String categoria;
        private String descripcion;
        private EstadoProducto estado;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ProductoResponseDto build() {
            return new ProductoResponseDto(id, nombre, categoria, descripcion, estado, createdAt, updatedAt);
        }
    }
}
