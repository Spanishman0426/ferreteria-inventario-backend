-- =============================================================================
-- Script DDL para PostgreSQL: Esquema de Inventario de Ferretería
-- Entidad: Producto
-- Fecha: 2026-10-13
-- =============================================================================

CREATE TABLE IF NOT EXISTS productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    categoria VARCHAR(50) NOT NULL,
    descripcion VARCHAR(500),
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('DISPONIBLE', 'RESERVADA', 'DANADA')),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

-- Índices para optimización de consultas frecuentes
CREATE INDEX IF NOT EXISTS idx_producto_categoria ON productos (categoria);
CREATE INDEX IF NOT EXISTS idx_producto_estado ON productos (estado);

-- Inserción de datos iniciales de prueba (Seed Data)
INSERT INTO productos (nombre, categoria, descripcion, estado, created_at, updated_at)
VALUES 
('Martillo de Uña Curva 16oz', 'Herramientas Manuales', 'Martillo de acero al carbono forjado con mango antideslizante', 'DISPONIBLE', NOW(), NOW()),
('Taladro Percutor 1/2 pulg 750W', 'Herramientas Eléctricas', 'Taladro percutor de velocidad variable y reversa para concreto y metal', 'DISPONIBLE', NOW(), NOW()),
('Sierra Circular 7-1/4 pulg 1400W', 'Herramientas Eléctricas', 'Sierra para cortes de madera con guía láser y disco de carburo', 'RESERVADA', NOW(), NOW()),
('Juego Destornilladores 6 Piezas', 'Herramientas Manuales', 'Punta magnética de acero cromo vanadio (3 planos, 3 phillips)', 'DISPONIBLE', NOW(), NOW()),
('Disco de Corte Diamantado 4-1/2 pulg', 'Accesorios y Consumibles', 'Disco con segmento soldado para corte en seco de mampostería', 'DANADA', NOW(), NOW())
ON CONFLICT (nombre) DO NOTHING;
