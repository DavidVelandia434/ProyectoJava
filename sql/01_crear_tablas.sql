-- =========================================================================
-- Programa : Sistema de gestión de librería
-- Archivo  : 01_crear_tablas.sql (PostgreSQL / Supabase)
-- Fecha    : 06/11/2026
-- Autor    : David Andrés Velandia Mendoza
-- =========================================================================

DROP TABLE IF EXISTS detalle_venta CASCADE;
DROP TABLE IF EXISTS venta CASCADE;
DROP TABLE IF EXISTS libro_autor CASCADE;
DROP TABLE IF EXISTS libro CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;
DROP TABLE IF EXISTS proveedor CASCADE;
DROP TABLE IF EXISTS autor CASCADE;
DROP TABLE IF EXISTS categoria CASCADE;

CREATE TABLE categoria (
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE autor (
    id_autor SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    nacionalidad VARCHAR(100) NOT NULL
);

CREATE TABLE proveedor (
    id_proveedor SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(40) NOT NULL,
    correo VARCHAR(150) NOT NULL
);

CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    documento VARCHAR(40) NOT NULL UNIQUE,
    telefono VARCHAR(40) NOT NULL,
    correo VARCHAR(150) NOT NULL
);

CREATE TABLE libro (
    id_libro SERIAL PRIMARY KEY,
    isbn VARCHAR(40) NOT NULL UNIQUE,
    titulo VARCHAR(200) NOT NULL,
    precio NUMERIC(12, 2) NOT NULL CHECK (precio > 0),
    stock INTEGER NOT NULL CHECK (stock >= 0),
    id_categoria INTEGER NOT NULL REFERENCES categoria(id_categoria) ON DELETE RESTRICT ON UPDATE CASCADE,
    id_proveedor INTEGER NOT NULL REFERENCES proveedor(id_proveedor) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE libro_autor (
    id_libro INTEGER NOT NULL REFERENCES libro(id_libro) ON DELETE CASCADE ON UPDATE CASCADE,
    id_autor INTEGER NOT NULL REFERENCES autor(id_autor) ON DELETE RESTRICT ON UPDATE CASCADE,
    PRIMARY KEY (id_libro, id_autor)
);

CREATE TABLE venta (
    id_venta SERIAL PRIMARY KEY,
    id_cliente INTEGER NOT NULL REFERENCES cliente(id_cliente) ON DELETE RESTRICT ON UPDATE CASCADE,
    fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    descuento NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (descuento >= 0),
    total NUMERIC(12, 2) NOT NULL CHECK (total >= 0)
);

CREATE TABLE detalle_venta (
    id_detalle SERIAL PRIMARY KEY,
    id_venta INTEGER NOT NULL REFERENCES venta(id_venta) ON DELETE RESTRICT ON UPDATE CASCADE,
    id_libro INTEGER NOT NULL REFERENCES libro(id_libro) ON DELETE RESTRICT ON UPDATE CASCADE,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(12, 2) NOT NULL CHECK (precio_unitario > 0),
    subtotal NUMERIC(12, 2) NOT NULL CHECK (subtotal >= 0)
);
