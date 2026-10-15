/**
 * Nombre del artefacto: ResourceNotFoundException.java
 * Descripción: Excepción lanzada cuando un recurso solicitado no existe en la base de datos.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String messageKey;
    private final Object[] args;

    public ResourceNotFoundException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.messageKey = message;
        this.args = null;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }
}
