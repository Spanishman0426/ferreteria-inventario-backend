/**
 * Nombre del artefacto: ApiResponseDto.java
 * Descripción: Envoltorio genérico estándar para las respuestas exitosas de la API REST sin dependencia de Lombok.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Estructura estándar de respuesta para operaciones exitosas")
public class ApiResponseDto<T> {

    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private boolean success;

    @Schema(description = "Mensaje informativo o de estado localizado (i18n)", example = "Producto creado exitosamente")
    private String message;

    @Schema(description = "Carga útil con los datos de respuesta de la operación")
    private T data;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Marca de tiempo de la respuesta", example = "2026-10-13T10:15:30")
    private LocalDateTime timestamp;

    public ApiResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponseDto(boolean success, String message, T data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = (timestamp != null) ? timestamp : LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public static <T> ApiResponseDto<T> success(String message, T data) {
        return new ApiResponseDto<>(true, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponseDto<T> success(String message) {
        return new ApiResponseDto<>(true, message, null, LocalDateTime.now());
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private boolean success;
        private String message;
        private T data;
        private LocalDateTime timestamp = LocalDateTime.now();

        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ApiResponseDto<T> build() {
            return new ApiResponseDto<>(success, message, data, timestamp);
        }
    }
}
