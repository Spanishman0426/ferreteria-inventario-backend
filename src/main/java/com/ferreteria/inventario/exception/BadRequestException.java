/**
 * Nombre del artefacto: BadRequestException.java
 * Descripción: Excepción lanzada cuando una operación viola reglas de negocio del inventario.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.exception;

public class BadRequestException extends RuntimeException {

    private final String messageKey;
    private final Object[] args;

    public BadRequestException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }

    public BadRequestException(String message) {
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
