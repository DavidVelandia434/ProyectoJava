-- =========================================================================
-- Programa : Sistema de gestión de librería
-- Archivo  : 02_poblar.sql (Datos de prueba para PostgreSQL / Supabase)
-- Fecha    : 06/11/2026
-- Autor    : David Andrés Velandia Mendoza
-- =========================================================================

INSERT INTO categoria (nombre, descripcion) VALUES
('Novela', 'Obras narrativas de ficción'),
('Ciencia Ficción', 'Relatos especulativos y científicos'),
('Ensayo', 'Textos reflexivos y argumentativos'),
('Infantil', 'Literatura para niños y jóvenes'),
('Programación', 'Ingeniería de software y ciencias de la computación');

INSERT INTO autor (nombre, nacionalidad) VALUES
('Gabriel García Márquez', 'Colombia'),
('Isabel Allende', 'Chile'),
('Julio Cortázar', 'Argentina'),
('Carl Sagan', 'EE. UU.'),
('Yuval Noah Harari', 'Israel'),
('Antoine de Saint-Exupéry', 'Francia'),
('Joshua Bloch', 'EE. UU.'),
('Robert C. Martin', 'EE. UU.'),
('Ana Ruiz', 'Colombia'),
('Luis Peña', 'Colombia');

INSERT INTO proveedor (nombre, telefono, correo) VALUES
('Distribuidora Andina', '3001112233', 'ventas@andina.test'),
('Libros del Sur', '3012223344', 'contacto@librosdelsur.test'),
('TecnoEditores', '3023334455', 'pedidos@tecnoeditores.test');

INSERT INTO cliente (nombre, documento, telefono, correo) VALUES
('Camila Rojas', '1001', '3101000001', 'camila@correo.test'),
('Andrés Gómez', '1002', '3101000002', 'andres@correo.test'),
('Laura Pardo', '1003', '3101000003', 'laura@correo.test'),
('Mateo Silva', '1004', '3101000004', 'mateo@correo.test'),
('Sofia Torres', '1005', '3101000005', 'sofia@correo.test'),
('Daniel Cruz', '1006', '3101000006', 'daniel@correo.test');

INSERT INTO libro (isbn, titulo, precio, stock, id_categoria, id_proveedor) VALUES
('978-0001', 'Cien años de soledad', 45000.00, 6, 1, 1),
('978-0002', 'El amor en los tiempos del cólera', 42000.00, 7, 1, 1),
('978-0003', 'La casa de los espíritus', 39000.00, 5, 1, 2),
('978-0004', 'Rayuela', 48000.00, 3, 1, 2),
('978-0005', 'Cosmos', 55000.00, 8, 2, 1),
('978-0006', 'Sapiens', 62000.00, 7, 3, 1),
('978-0007', 'Homo Deus', 60000.00, 4, 3, 1),
('978-0008', 'El principito', 28000.00, 9, 4, 2),
('978-0009', 'Effective Java', 120000.00, 3, 5, 3),
('978-0010', 'Clean Code', 110000.00, 3, 5, 3),
('978-0011', 'Clean Architecture', 115000.00, 4, 5, 3),
('978-0012', 'Pálido punto azul', 58000.00, 2, 2, 1),
('978-0013', 'Introducción a las bases de datos', 70000.00, 7, 5, 3);

INSERT INTO libro_autor (id_libro, id_autor) VALUES
(1, 1), (2, 1), (3, 2), (4, 3), (5, 4), (6, 5), (7, 5),
(8, 6), (9, 7), (10, 8), (11, 8), (12, 4), (13, 9), (13, 10);

INSERT INTO venta (id_cliente, fecha, descuento, total) VALUES
(1, '2026-09-03', 0.00, 118000.00),
(2, '2026-09-05', 0.00, 62000.00),
(1, '2026-09-12', 0.00, 230000.00),
(3, '2026-09-15', 0.00, 110000.00),
(4, '2026-09-20', 0.00, 140000.00),
(2, '2026-10-01', 0.00, 220000.00),
(5, '2026-10-04', 0.00, 84000.00),
(1, '2026-10-10', 0.00, 122000.00),
(3, '2026-10-15', 0.00, 135000.00),
(4, '2026-10-20', 0.00, 90000.00);

INSERT INTO detalle_venta (id_venta, id_libro, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 2, 45000.00, 90000.00),
(1, 8, 1, 28000.00, 28000.00),
(2, 6, 1, 62000.00, 62000.00),
(3, 9, 1, 120000.00, 120000.00),
(3, 10, 1, 110000.00, 110000.00),
(4, 5, 2, 55000.00, 110000.00),
(5, 1, 1, 45000.00, 45000.00),
(5, 3, 1, 39000.00, 39000.00),
(5, 8, 2, 28000.00, 56000.00),
(6, 10, 2, 110000.00, 220000.00),
(7, 8, 3, 28000.00, 84000.00),
(8, 6, 1, 62000.00, 62000.00),
(8, 7, 1, 60000.00, 60000.00),
(9, 1, 3, 45000.00, 135000.00),
(10, 2, 1, 42000.00, 42000.00),
(10, 4, 1, 48000.00, 48000.00);
