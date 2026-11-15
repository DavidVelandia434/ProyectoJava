# Sistema de Gestión de Librería (Java 17 + AWT Facade + PostgreSQL Supabase)

## Cómo abrir y probar en Visual Studio Code
1. Descomprime este archivo ZIP y abre la carpeta raíz en **Visual Studio Code** (`File -> Open Folder...`).
2. Para abrir la **Interfaz Gráfica en Java AWT (Patrón Facade)**:
   - Ve al archivo `src/libreria/gui/MainAWT.java`.
   - Haz clic en **Run** encima de `public static void main` o presiona **`F5`**.
3. Para conectar con **Supabase (PostgreSQL)**:
   - Abre el **SQL Editor** en tu proyecto de Supabase y ejecuta primero `sql/01_crear_tablas.sql` y luego `sql/02_poblar.sql`.
   - En la ventana gráfica AWT, entra a la sección **6. Conexión BD Supabase** y haz clic en **Probar Conexión Supabase** o **Usar PostgreSQL (JDBC)**.
