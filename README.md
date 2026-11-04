# Sistema de gestión de librería (Java)

Autor: David Andrés Velandia Mendoza · Fecha: 02/11/2026

## Compilar y ejecutar (JDK 17+)
    mkdir out
    javac -encoding UTF-8 -d out $(find src -name "*.java")
    java -cp out libreria.Main

## Estructura
- `model`   entidades (Libro, Autor, Categoria, Proveedor, Cliente, Venta, DetalleVenta)
- `dao`     interfaces (LecturaDAO / EscrituraDAO, LibroDAO, VentaRepository...)
- `dao.memoria`  implementaciones que EMULAN la base de datos (activas ahora)
- `dao.jdbc`     plantillas JDBC pendientes (LibroDAOJdbc, VentaRepositoryJdbc)
- `service` reglas de negocio, Strategy de descuentos (PoliticaDescuento)
- `database` BaseDatosEnMemoria, DatosPrueba, ConexionBD (credenciales por variables de entorno)
- `ui`      menús de consola
- `sql/`    scripts pendientes (crear tablas y poblar)

## Cómo conectar la base de datos real
1. Completar `sql/01_crear_tablas.sql` y `sql/02_poblar.sql`.
2. Agregar el driver JDBC (MySQL o PostgreSQL) al classpath.
3. Definir LIBRERIA_DB_URL, LIBRERIA_DB_USER y LIBRERIA_DB_PASSWORD.
4. Implementar las clases de `dao.jdbc` (y las que falten: autor, categoría, proveedor, cliente).
5. En `Main.java`, cambiar los `new ...DAOMemoria(bd)` por los JDBC y poner `cargarDatosPrueba = false`.
`service` y `ui` no cambian.

---

## API REST con Spring Boot (nueva)

Requiere JDK 17+ y Maven. Reutiliza `service` y `dao` sin cambios; solo añade el paquete `libreria.api`
(`config`, `controller`, `dto`). La versión de consola (`libreria.Main`) sigue funcionando.

    mvn spring-boot:run
    # API en http://localhost:8080

### Endpoints
Todas parten de http://localhost:8080.

Libros (/api/libros)

Llamada	Qué hace
GET /api/libros	Lista ordenada por título
GET /api/libros?categoria=1	Libros de una categoría
GET /api/libros?precioMin=30000&precioMax=60000	Libros en un rango de precio
GET /api/libros?stockMenorA=5	Existencias bajas
GET /api/libros/{id}	Un libro
POST /api/libros	Registrar libro
PUT /api/libros/{id}/precio	Actualizar precio
PUT /api/libros/{id}/titulo	Corregir título
PUT /api/libros/{id}/stock	Ajustar existencias (pide motivo)
DELETE /api/libros/{id}	Eliminar (rechaza si ya fue vendido)

Clientes (/api/clientes) y Proveedores (/api/proveedores)

Llamada	Qué hace
GET /api/clientes o /api/proveedores	Listar
GET .../{id}	Uno solo
POST ...	Registrar
PUT .../{id}	Actualizar datos de contacto
DELETE .../{id}	Eliminar (rechaza si tiene ventas o libros)

Autores (/api/autores) y Categorías (/api/categorias)

Llamada	Qué hace
GET ...	Listar
POST ...	Registrar
DELETE .../{id}	Eliminar (rechaza si tiene libros)

Ventas (/api/ventas): solo se crean y consultan, no se modifican ni se borran.

Llamada	Qué hace
GET /api/ventas	Todas
GET /api/ventas?clienteId=1	Historial por cliente
GET /api/ventas?desde=2026-09-01&hasta=2026-09-30	Historial por fechas
GET /api/ventas/{id}	Una venta
POST /api/ventas	Registrar venta

Reportes (/api/reportes/..., todos GET)

Ruta	Consulta
libros-mas-vendidos?limite=5	Más vendidos
ventas-por-cliente	Número de ventas por cliente
total-por-dia / total-por-mes	Total vendido
libros-por-categoria	Cantidad de libros por categoría
categorias-con-mas-de?n=2	Categorías con más de N libros
precio-promedio-por-categoria	Precio promedio
libro-mas-caro / libro-mas-barato	Extremos de precio
libros-nunca-vendidos	Sin ventas
libros-sobre-promedio	Ventas sobre el promedio

Cuerpos JSON de los POST y PUT

json
// POST /api/libros
{"isbn":"978-0099","titulo":"Nuevo libro","precio":50000,"stock":10,
 "idCategoria":1,"idProveedor":1,"idsAutores":[1,2]}

// PUT /api/libros/{id}/precio
{"precio":47000}

// PUT /api/libros/{id}/titulo
{"titulo":"Título corregido"}

// PUT /api/libros/{id}/stock
{"stock":20,"motivo":"Llegó pedido del proveedor"}

// POST /api/clientes  (en el PUT no se usa "documento")
{"nombre":"Ana Pérez","documento":"1007","telefono":"3100000007","correo":"ana@correo.test"}

// POST /api/proveedores  y  PUT /api/proveedores/{id}
{"nombre":"Distribuidora X","telefono":"3000000000","correo":"x@correo.test"}

// POST /api/autores
{"nombre":"Gabriela Mistral","nacionalidad":"Chile"}

// POST /api/categorias
{"nombre":"Poesía"}

// POST /api/ventas
{"idCliente":1,"items":[{"idLibro":8,"cantidad":6},{"idLibro":5,"cantidad":1}]}

### Ejemplos
    curl http://localhost:8080/api/libros?stockMenorA=5
    curl -X POST http://localhost:8080/api/ventas -H "Content-Type: application/json" \
         -d '{"idCliente":1,"items":[{"idLibro":8,"cantidad":6},{"idLibro":5,"cantidad":1}]}'
    curl -X PUT http://localhost:8080/api/libros/1/precio -H "Content-Type: application/json" -d '{"precio":47000}'
    curl -X DELETE http://localhost:8080/api/libros/1     # 409: ya tiene ventas

### Base de datos real
Solo se cambian los beans de la sección 1 de `api/config/ConfiguracionLibreria.java`
y `libreria.datos-prueba=false` en `src/resources/application.properties`.
