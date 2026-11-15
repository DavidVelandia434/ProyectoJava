Sistema de Gestión de Librería

Aplicación en **Java 17** para administrar una librería: catálogo de libros, autores, categorías, proveedores, clientes, ventas y reportes. El mismo sistema se puede usar de tres formas:

| Interfaz | Descripción |
|---|---|
| **API REST** | Servicio web con Spring Boot (`/api/...`) para consumir desde Postman, `curl` o cualquier cliente. |
| **Interfaz gráfica (AWT)** | Ventanas de escritorio construidas con AWT, conectadas al sistema mediante el patrón **Facade**. |
| **Consola** | Menú interactivo por terminal. |

Los datos se guardan en **PostgreSQL (Supabase)**. Si la base de datos no está disponible, el sistema arranca automáticamente en **modo memoria** con datos de prueba, así que se puede probar sin configurar nada.

---

## Características

- CRUD de **libros, autores, categorías, proveedores y clientes**.
- Libros con **varios autores** (relación muchos a muchos).
- **Registro de ventas** con varios libros por venta, control de existencias y **descuento por volumen** (10 % desde 5 unidades).
- Las ventas se guardan en una **transacción**: si falta stock, no se registra nada.
- El precio de cada libro se guarda en el detalle de la venta, de modo que el **historial no cambia** aunque el precio se actualice después.
- **11 reportes**: libros más vendidos, ventas por cliente, totales por día y por mes, precio promedio por categoría, libro más caro y más barato, libros nunca vendidos, entre otros.
- **Validaciones** de datos y mensajes de error claros.
- No permite eliminar registros que tengan información relacionada (por ejemplo, una categoría con libros o un cliente con ventas).

---

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 17 | Lenguaje |
| Spring Boot 3.3.5 | API REST y servidor embebido |
| Spring Validation | Validación de las peticiones |
| JDBC + PostgreSQL | Acceso a datos (sin ORM) |
| Supabase | Base de datos PostgreSQL en la nube |
| AWT | Interfaz gráfica de escritorio |
| Maven | Compilación y dependencias |

---

## Arquitectura

El proyecto está organizado por capas:

```
ProyectoJava/
├── sql/                         # Scripts de base de datos
│   ├── 01_crear_tablas.sql      # Crea las tablas
│   └── 02_poblar.sql            # Inserta datos de ejemplo
├── src/
│   ├── libreria/
│   │   ├── api/                 # API REST
│   │   │   ├── LibreriaApiApplication.java   # Clase principal de Spring Boot
│   │   │   ├── Respuestas.java               # Respuestas HTTP para eliminaciones
│   │   │   ├── config/                       # Configuración de beans (ConfiguracionLibreria)
│   │   │   ├── controller/                   # Controladores REST y manejo de errores
│   │   │   └── dto/                          # Objetos de petición con validaciones
│   │   ├── model/               # Entidades: Libro, Autor, Categoria, Proveedor, Cliente, Venta, DetalleVenta
│   │   ├── dao/                 # Interfaces de acceso a datos
│   │   │   ├── jdbc/            # Implementación con PostgreSQL
│   │   │   └── memoria/         # Implementación en memoria (respaldo)
│   │   ├── service/             # Lógica de negocio y reglas de validación
│   │   ├── database/            # ConexionBD, BaseDatosEnMemoria, DatosPrueba
│   │   └── gui/                 # Interfaz AWT: MainAWT, LibreriaFacade, VentanaPrincipalAWT
│   └── resources/               # Archivos de configuración
├── target/classes/              # Compilados (generado por Maven)
├── pom.xml
└── README.md
```

**Patrones de diseño utilizados:**

- **DAO / Repository**: separan el acceso a datos de la lógica de negocio. Cada entidad tiene una versión JDBC y una en memoria.
- **Facade**: `LibreriaFacade` ofrece a la interfaz gráfica un único punto de acceso al sistema.
- **Strategy**: `PoliticaDescuento` permite cambiar la regla de descuento (`DescuentoPorVolumen`, `SinDescuento`) sin tocar el resto del código.

---

## Base de datos

Tablas del sistema:

| Tabla | Columnas principales |
|---|---|
| `autor` | `id_autor`, `nombre`, `nacionalidad` |
| `categoria` | `id_categoria`, `nombre` |
| `proveedor` | `id_proveedor`, `nombre`, `telefono`, `correo` |
| `cliente` | `id_cliente`, `nombre`, `documento`, `telefono`, `correo` |
| `libro` | `id_libro`, `isbn`, `titulo`, `precio`, `stock`, `id_categoria`, `id_proveedor` |
| `libro_autor` | `id_libro`, `id_autor` (tabla intermedia muchos a muchos) |
| `venta` | `id_venta`, `id_cliente`, `fecha`, `descuento`, `total` |
| `detalle_venta` | `id_detalle`, `id_venta`, `id_libro`, `cantidad`, `precio_unitario`, `subtotal` |

---

## Requisitos previos

- **JDK 17** o superior → `java -version`
- **Apache Maven 3.8+** → `mvn -version`
- **Git** → `git --version`
- *(Opcional)* Una cuenta gratuita en [Supabase](https://supabase.com) para usar la base de datos en la nube. Sin ella, el sistema funciona en modo memoria.
- *(Opcional)* Visual Studio Code con el *Extension Pack for Java*, o cualquier IDE de Java, para ejecutar la interfaz gráfica.

---

##  Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/DavidVelandia434/ProyectoJava.git
cd ProyectoJava
```

### 2. Compilar

```bash
mvn clean package
```

### 3. Crear la base de datos (opcional)

Si quieres guardar los datos en Supabase:

1. Crea un proyecto en [Supabase](https://supabase.com).
2. Abre el **SQL Editor**.
3. Ejecuta primero `sql/01_crear_tablas.sql` y luego `sql/02_poblar.sql`.

> Si omites este paso, el sistema usará el **modo memoria**: todo funciona, pero los datos se pierden al cerrar la aplicación.

### 4. Configurar la conexión (opcional)

Las credenciales se leen de **variables de entorno** o del archivo de configuración de `src/resources/`. Nunca las escribas directamente en el código.

| Variable de entorno | Propiedad equivalente | Descripción |
|---|---|---|
| `LIBRERIA_DB_URL` | `libreria.db.url` | URL JDBC, por ejemplo `jdbc:postgresql://<host>:5432/postgres?sslmode=require` |
| `LIBRERIA_DB_USER` | `libreria.db.user` | Usuario de la base de datos |
| `LIBRERIA_DB_PASSWORD` | `libreria.db.password` | Contraseña de la base de datos |
| `LIBRERIA_DB_MODO` | `libreria.db.modo` | `auto` (por defecto), `jdbc` o `memoria` |
| — | `libreria.datos-prueba` | `true` (por defecto): carga datos de ejemplo en modo memoria |

Los datos de conexión los encuentras en Supabase, en **Project Settings → Database → Connection string**. Usa el *Session Pooler*, que funciona con IPv4.

Ejemplo en Linux o macOS:

```bash
export LIBRERIA_DB_USER="tu_usuario"
export LIBRERIA_DB_PASSWORD="tu_contraseña"
```

Ejemplo en Windows (PowerShell):

```powershell
$env:LIBRERIA_DB_USER = "tu_usuario"
$env:LIBRERIA_DB_PASSWORD = "tu_contraseña"
```

**Modos de funcionamiento (`libreria.db.modo`):**

| Modo | Comportamiento |
|---|---|
| `auto` | Intenta conectar a PostgreSQL y verifica que las tablas existan. Si falla, usa memoria. |
| `jdbc` | Obliga a usar PostgreSQL. Si no hay conexión, las operaciones devuelven error. |
| `memoria` | Usa solo memoria, sin base de datos. |

---

## Instructivo de uso

### Opción A: API REST

1. Inicia el servidor desde la carpeta del proyecto:

   ```bash
   mvn spring-boot:run
   ```

   o, ya compilado:

   ```bash
   java -jar target/libreria-api-1.0.0.jar
   ```

2. Espera el mensaje de que Spring Boot arrancó. Por defecto el servidor queda en `http://localhost:8080`.
3. Prueba que funciona:

   ```bash
   curl http://localhost:8080/api/libros
   ```

4. Usa **Postman**, **Insomnia** o `curl` para consumir los endpoints de la tabla siguiente.
5. Para detener el servidor, presiona `Ctrl + C`.

#### Endpoints disponibles

**📖 Libros** (`/api/libros`)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/libros` | Lista los libros ordenados por título. Filtros opcionales: `?categoria=1`, `?precioMin=30000&precioMax=60000`, `?stockMenorA=5` |
| `GET` | `/api/libros/{id}` | Consulta un libro |
| `POST` | `/api/libros` | Registra un libro |
| `PUT` | `/api/libros/{id}/precio` | Actualiza el precio |
| `PUT` | `/api/libros/{id}/titulo` | Corrige el título |
| `PUT` | `/api/libros/{id}/stock` | Ajusta las existencias (requiere un motivo) |
| `DELETE` | `/api/libros/{id}` | Elimina un libro (no permitido si tiene ventas) |

**✍️ Autores** (`/api/autores`)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/autores` | Lista los autores |
| `POST` | `/api/autores` | Registra un autor |
| `DELETE` | `/api/autores/{id}` | Elimina un autor (no permitido si tiene libros) |

**🏷️ Categorías** (`/api/categorias`)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/categorias` | Lista las categorías |
| `POST` | `/api/categorias` | Registra una categoría |
| `DELETE` | `/api/categorias/{id}` | Elimina una categoría (no permitido si tiene libros) |

**🚚 Proveedores** (`/api/proveedores`)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/proveedores` | Lista los proveedores |
| `GET` | `/api/proveedores/{id}` | Consulta un proveedor |
| `POST` | `/api/proveedores` | Registra un proveedor |
| `PUT` | `/api/proveedores/{id}` | Actualiza los datos |
| `DELETE` | `/api/proveedores/{id}` | Elimina un proveedor (no permitido si tiene libros) |

**👤 Clientes** (`/api/clientes`)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/clientes` | Lista los clientes |
| `GET` | `/api/clientes/{id}` | Consulta un cliente |
| `POST` | `/api/clientes` | Registra un cliente (el documento no se puede repetir) |
| `PUT` | `/api/clientes/{id}` | Actualiza nombre, teléfono y correo |
| `DELETE` | `/api/clientes/{id}` | Elimina un cliente (no permitido si tiene ventas) |

**🧾 Ventas** (`/api/ventas`)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/ventas` | Lista las ventas. Filtros opcionales: `?clienteId=1`, `?desde=2026-09-01&hasta=2026-09-30` |
| `GET` | `/api/ventas/{id}` | Consulta una venta con sus detalles |
| `POST` | `/api/ventas` | Registra una venta |

Las ventas no se pueden modificar ni eliminar una vez registradas.

**📊 Reportes** (`/api/reportes`)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/reportes/libros-mas-vendidos?limite=5` | Libros más vendidos |
| `GET` | `/api/reportes/ventas-por-cliente` | Número de ventas por cliente |
| `GET` | `/api/reportes/total-por-dia` | Total vendido por día |
| `GET` | `/api/reportes/total-por-mes` | Total vendido por mes |
| `GET` | `/api/reportes/libros-por-categoria` | Cantidad de libros por categoría |
| `GET` | `/api/reportes/categorias-con-mas-de?n=2` | Categorías con más de *n* libros |
| `GET` | `/api/reportes/precio-promedio-por-categoria` | Precio promedio por categoría |
| `GET` | `/api/reportes/libro-mas-caro` | Libro de mayor precio |
| `GET` | `/api/reportes/libro-mas-barato` | Libro de menor precio |
| `GET` | `/api/reportes/libros-nunca-vendidos` | Libros sin ventas |
| `GET` | `/api/reportes/libros-sobre-promedio` | Libros que venden por encima del promedio |

#### Ejemplos de peticiones

**Registrar un libro**

```bash
curl -X POST http://localhost:8080/api/libros \
  -H "Content-Type: application/json" \
  -d '{
        "isbn": "978-0100",
        "titulo": "El coronel no tiene quien le escriba",
        "precio": 35000,
        "stock": 10,
        "idCategoria": 1,
        "idProveedor": 1,
        "idsAutores": [1]
      }'
```

Reglas: `isbn` y `titulo` no pueden estar vacíos, `precio` debe ser mayor que 0, `stock` no puede ser negativo y se necesita al menos un autor. La categoría, el proveedor y los autores deben existir.

**Cambiar el precio**

```bash
curl -X PUT http://localhost:8080/api/libros/1/precio \
  -H "Content-Type: application/json" \
  -d '{"precio": 47000}'
```

**Ajustar el stock**

```bash
curl -X PUT http://localhost:8080/api/libros/1/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 20, "motivo": "Llegada de nuevo pedido"}'
```

**Registrar un autor**

```bash
curl -X POST http://localhost:8080/api/autores \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Mario Vargas Llosa", "nacionalidad": "Perú"}'
```

**Registrar un cliente**

```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
        "nombre": "Valentina Mora",
        "documento": "2001",
        "telefono": "3109998888",
        "correo": "valentina@correo.test"
      }'
```

**Registrar una venta** (cliente 1 compra 5 unidades del libro 8 y 1 del libro 1)

```bash
curl -X POST http://localhost:8080/api/ventas \
  -H "Content-Type: application/json" \
  -d '{
        "idCliente": 1,
        "items": [
          {"idLibro": 8, "cantidad": 5},
          {"idLibro": 1, "cantidad": 1}
        ]
      }'
```

Como la compra suma 6 unidades (5 o más), se aplica automáticamente un **10 % de descuento** sobre el subtotal. Si algún libro no tiene existencias suficientes, la venta se rechaza completa.

#### Códigos de respuesta

| Código | Significado |
|---|---|
| `200 OK` | Consulta o actualización exitosa |
| `201 Created` | Registro creado |
| `204 No Content` | Eliminación exitosa |
| `400 Bad Request` | Datos inválidos, regla de negocio incumplida o formato de petición incorrecto |
| `404 Not Found` | El registro no existe |
| `409 Conflict` | No se puede eliminar porque tiene información relacionada, o falló la base de datos |
| `500 Internal Server Error` | Error inesperado |

Los errores devuelven un JSON con el motivo. Por ejemplo, si falla la validación:

```json
{
  "error": "Los datos enviados no son válidos.",
  "campos": { "precio": "must be greater than 0" }
}
```

---

### Opción B: Interfaz gráfica (AWT)

1. Abre el proyecto en **Visual Studio Code** (con el *Extension Pack for Java*) o en tu IDE de preferencia.
2. Abre el archivo `src/libreria/gui/MainAWT.java`.
3. Haz clic en **Run** sobre el método `main()` o presiona `F5`.
4. Se abrirá la ventana principal. Desde ahí puedes administrar el catálogo, los clientes, las ventas y consultar los reportes.

No necesita ningún servidor aparte. Al iniciar, `LibreriaFacade` intenta conectarse a PostgreSQL (Supabase); si no hay red o las tablas no existen, arranca en **modo memoria** con datos de prueba.

---

### Opción C: Consola

> 📝 **Completar:** escribe aquí el nombre de la clase de consola (la que tiene el `main` con el menú), por ejemplo `libreria.consola.MainConsola`.

1. Abre esa clase en tu IDE.
2. Ejecuta el método `main()`.
3. Sigue el menú numerado que aparece en pantalla e ingresa la opción que necesites.

---

## 🧪 Datos de prueba

En modo memoria el sistema carga automáticamente:

- 5 categorías (Novela, Ciencia Ficción, Ensayo, Infantil, Programación)
- 10 autores
- 3 proveedores
- 6 clientes
- 13 libros
- 10 ventas de ejemplo

Para desactivar esta carga, usa `libreria.datos-prueba=false`. Con PostgreSQL, los datos de ejemplo se cargan ejecutando `sql/02_poblar.sql`.

---

## 📏 Reglas de negocio

- El ISBN de un libro no se puede repetir.
- Un libro necesita al menos un autor, una categoría y un proveedor existentes.
- El documento de un cliente no se puede repetir.
- El nombre de una categoría no se puede repetir.
- El correo debe tener un formato válido.
- No se puede vender más cantidad de la que hay en existencias.
- Descuento por volumen: **10 %** cuando la venta tiene **5 o más unidades**.
- No se pueden eliminar categorías con libros, proveedores con libros, autores con libros, libros con ventas ni clientes con ventas.

---

## 🛠️ Solución de problemas

| Problema | Posible causa y solución |
|---|---|
| Aparece "MODO MEMORIA ACTIVO" | No se pudo conectar a Supabase. Revisa las variables de entorno, tu conexión a internet y que las tablas estén creadas. |
| "CONECTADO SIN TABLAS" | Falta ejecutar `sql/01_crear_tablas.sql` y `sql/02_poblar.sql` en el SQL Editor de Supabase. |
| `release version 17 not supported` | Tienes un JDK anterior. Instala JDK 17 o superior. |
| `Port 8080 was already in use` | Cierra el programa que usa ese puerto o cambia `server.port` en la configuración. |
| No se encuentra el driver `org.postgresql.Driver` | Ejecuta el proyecto con Maven o desde un IDE que cargue las dependencias del `pom.xml`. |
| `400 Bad Request` al crear algo | Revisa que el JSON tenga todos los campos obligatorios y que `Content-Type` sea `application/json`. |

---

## 🔒 Seguridad

- **No subas credenciales a GitHub.** Usa variables de entorno o un archivo de configuración local incluido en `.gitignore`.
- Si alguna vez publicaste una contraseña en el repositorio, cámbiala de inmediato en Supabase (*Project Settings → Database → Reset database password*), porque queda guardada en el historial de commits.
- Se recomienda agregar al `.gitignore`:

  ```
  target/
  src/resources/application-local.properties
  ```

