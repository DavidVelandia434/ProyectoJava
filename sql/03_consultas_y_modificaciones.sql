-- =========================================================================
-- Programa : Sistema de gestión de librería
-- Archivo  : 03_consultas_y_modificaciones.sql
-- Contenido: Las 15 consultas SQL obligatorias, 5 operaciones UPDATE y DELETE
-- Motor    : PostgreSQL (Supabase)
-- =========================================================================

-- 1. Listado de libros ordenados por título (SELECT, ORDER BY)
SELECT id_libro, isbn, titulo, precio, stock
FROM libro
ORDER BY LOWER(titulo) ASC;

-- 2. Libros de una categoría determinada con stock disponible (WHERE + AND)
SELECT l.id_libro, l.isbn, l.titulo, l.precio, l.stock, c.nombre AS categoria
FROM libro l
INNER JOIN categoria c ON l.id_categoria = c.id_categoria
WHERE l.id_categoria = 1 AND l.stock > 0;

-- 3. Libros cuyo precio esté dentro de un rango (BETWEEN)
SELECT id_libro, isbn, titulo, precio, stock
FROM libro
WHERE precio BETWEEN 35000 AND 65000
ORDER BY precio ASC;

-- 4. Libros con existencias inferiores a un umbral (Filtro de alerta de stock)
SELECT id_libro, isbn, titulo, stock
FROM libro
WHERE stock < 5
ORDER BY stock ASC;

-- 5. Cantidad de libros registrados por categoría (COUNT + GROUP BY + LEFT JOIN)
SELECT c.id_categoria, c.nombre AS categoria, COUNT(l.id_libro) AS total_libros
FROM categoria c
LEFT JOIN libro l ON c.id_categoria = l.id_categoria
GROUP BY c.id_categoria, c.nombre
ORDER BY total_libros DESC;

-- 6. Precio promedio de los libros por categoría (AVG + GROUP BY)
SELECT c.nombre AS categoria, ROUND(AVG(l.precio), 2) AS precio_promedio
FROM categoria c
INNER JOIN libro l ON c.id_categoria = l.id_categoria
GROUP BY c.id_categoria, c.nombre
ORDER BY precio_promedio DESC;

-- 7. Libro de mayor y menor precio (MIN y MAX)
SELECT 'MAYOR PRECIO' AS tipo, id_libro, titulo, precio FROM libro WHERE precio = (SELECT MAX(precio) FROM libro)
UNION ALL
SELECT 'MENOR PRECIO' AS tipo, id_libro, titulo, precio FROM libro WHERE precio = (SELECT MIN(precio) FROM libro);

-- 8. Número de ventas por cliente incluyendo clientes sin compras (LEFT JOIN + COUNT + GROUP BY)
SELECT cl.id_cliente, cl.nombre, cl.documento, COUNT(v.id_venta) AS numero_ventas
FROM cliente cl
LEFT JOIN venta v ON cl.id_cliente = v.id_cliente
GROUP BY cl.id_cliente, cl.nombre, cl.documento
ORDER BY numero_ventas DESC;

-- 9. Total vendido por día y por mes (SUM + GROUP BY dimensión temporal)
-- 9A: Por día
SELECT fecha, SUM(total) AS total_vendido_dia
FROM venta
GROUP BY fecha
ORDER BY fecha ASC;

-- 9B: Por mes
SELECT TO_CHAR(fecha, 'YYYY-MM') AS mes, SUM(total) AS total_vendido_mes
FROM venta
GROUP BY TO_CHAR(fecha, 'YYYY-MM')
ORDER BY mes ASC;

-- 10. Cantidad total de unidades vendidas por libro (SUM + INNER JOIN + GROUP BY)
SELECT l.id_libro, l.titulo, SUM(dv.cantidad) AS unidades_vendidas, SUM(dv.subtotal) AS ingresos_generados
FROM libro l
INNER JOIN detalle_venta dv ON l.id_libro = dv.id_libro
GROUP BY l.id_libro, l.titulo
ORDER BY unidades_vendidas DESC;

-- 11. Listado de ventas con datos del cliente (INNER JOIN de 2 tablas)
SELECT v.id_venta, v.fecha, cl.nombre AS cliente, cl.documento, v.descuento, v.total
FROM venta v
INNER JOIN cliente cl ON v.id_cliente = cl.id_cliente
ORDER BY v.fecha DESC, v.id_venta DESC;

-- 12. Detalle de ventas con información de libros y clientes (INNER JOIN múltiple de 4 tablas)
SELECT v.id_venta, v.fecha, cl.nombre AS cliente, l.titulo AS libro,
       dv.cantidad, dv.precio_unitario, dv.subtotal
FROM detalle_venta dv
INNER JOIN venta v ON dv.id_venta = v.id_venta
INNER JOIN cliente cl ON v.id_cliente = cl.id_cliente
INNER JOIN libro l ON dv.id_libro = l.id_libro
ORDER BY v.id_venta ASC, dv.id_detalle ASC;

-- 13. Categorías que superen un número determinado de libros (GROUP BY + HAVING)
SELECT c.nombre AS categoria, COUNT(l.id_libro) AS cantidad_libros
FROM categoria c
INNER JOIN libro l ON c.id_categoria = l.id_categoria
GROUP BY c.id_categoria, c.nombre
HAVING COUNT(l.id_libro) > 2
ORDER BY cantidad_libros DESC;

-- 14. Libros que nunca han sido vendidos (LEFT JOIN + IS NULL)
SELECT l.id_libro, l.isbn, l.titulo, l.precio, l.stock
FROM libro l
LEFT JOIN detalle_venta dv ON l.id_libro = dv.id_libro
WHERE dv.id_detalle IS NULL
ORDER BY l.id_libro;

-- 15. Libros cuyas unidades vendidas superan el promedio de unidades vendidas por libro (Subconsulta comparativa)
SELECT l.id_libro, l.titulo, SUM(dv.cantidad) AS total_unidades
FROM libro l
INNER JOIN detalle_venta dv ON l.id_libro = dv.id_libro
GROUP BY l.id_libro, l.titulo
HAVING SUM(dv.cantidad) > (
    SELECT AVG(unidades_por_libro)
    FROM (
        SELECT SUM(cantidad) AS unidades_por_libro
        FROM detalle_venta
        GROUP BY id_libro
    ) sub
)
ORDER BY total_unidades DESC;

-- =========================================================================
-- LAS 5 OPERACIONES UPDATE DIFERENCIADAS
-- =========================================================================

-- UPDATE 1: Actualización del precio de un libro
UPDATE libro SET precio = 47000.00 WHERE id_libro = 1;

-- UPDATE 2: Corrección del título de un libro
UPDATE libro SET titulo = 'Cien años de soledad (Edición Conmemorativa)' WHERE id_libro = 1;

-- UPDATE 3: Ajuste autorizado de existencias en inventario
UPDATE libro SET stock = 15 WHERE id_libro = 12;

-- UPDATE 4: Modificación de los datos de contacto de un cliente
UPDATE cliente SET nombre = 'Camila Rojas Pineda', telefono = '3109998877', correo = 'camila.rojas@nuevo.test' WHERE id_cliente = 1;

-- UPDATE 5: Actualización de los datos de contacto de un proveedor
UPDATE proveedor SET nombre = 'Distribuidora Andina S.A.S.', telefono = '6017778899', correo = 'comercial@andina.test' WHERE id_proveedor = 1;

-- =========================================================================
-- OPERACIONES DELETE CONTROLADAS (Respetando integridad referencial)
-- =========================================================================

-- Eliminación exitosa de un cliente sin compras registradas (id_cliente = 6: Daniel Cruz)
DELETE FROM cliente WHERE id_cliente = 6;
