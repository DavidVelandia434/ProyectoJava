package libreria.database;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import libreria.dao.AutorDAO;
import libreria.dao.CategoriaDAO;
import libreria.dao.ClienteDAO;
import libreria.dao.LibroDAO;
import libreria.dao.ProveedorDAO;
import libreria.dao.VentaRepository;
import libreria.model.Autor;
import libreria.model.Categoria;
import libreria.model.Cliente;
import libreria.model.DetalleVenta;
import libreria.model.Libro;
import libreria.model.Proveedor;
import libreria.model.Venta;

public class DatosPrueba {

    public static void cargar(AutorDAO autorDAO, CategoriaDAO categoriaDAO,
                              ProveedorDAO proveedorDAO, ClienteDAO clienteDAO,
                              LibroDAO libroDAO, VentaRepository ventaRepo) {
        for (String c : new String[]{"Novela", "Ciencia Ficción", "Ensayo", "Infantil", "Programación"}) {
            categoriaDAO.insertar(new Categoria(null, c));
        }

        String[][] autores = {
            {"Gabriel García Márquez", "Colombia"}, {"Isabel Allende", "Chile"},
            {"Julio Cortázar", "Argentina"}, {"Carl Sagan", "EE. UU."},
            {"Yuval Noah Harari", "Israel"}, {"Antoine de Saint-Exupéry", "Francia"},
            {"Joshua Bloch", "EE. UU."}, {"Robert C. Martin", "EE. UU."},
            {"Ana Ruiz", "Colombia"}, {"Luis Peña", "Colombia"}
        };
        for (String[] a : autores) {
            autorDAO.insertar(new Autor(null, a[0], a[1]));
        }

        proveedorDAO.insertar(new Proveedor(null, "Distribuidora Andina", "3001112233", "ventas@andina.test"));
        proveedorDAO.insertar(new Proveedor(null, "Libros del Sur", "3012223344", "contacto@librosdelsur.test"));
        proveedorDAO.insertar(new Proveedor(null, "TecnoEditores", "3023334455", "pedidos@tecnoeditores.test"));

        String[][] clientes = {
            {"Camila Rojas", "1001", "3101000001", "camila@correo.test"},
            {"Andrés Gómez", "1002", "3101000002", "andres@correo.test"},
            {"Laura Pardo", "1003", "3101000003", "laura@correo.test"},
            {"Mateo Silva", "1004", "3101000004", "mateo@correo.test"},
            {"Sofia Torres", "1005", "3101000005", "sofia@correo.test"},
            {"Daniel Cruz", "1006", "3101000006", "daniel@correo.test"}
        };
        for (String[] c : clientes) {
            clienteDAO.insertar(new Cliente(null, c[0], c[1], c[2], c[3]));
        }

        libro(libroDAO, "978-0001", "Cien años de soledad", "45000", 12, 1, 1, 1);
        libro(libroDAO, "978-0002", "El amor en los tiempos del cólera", "42000", 8, 1, 1, 1);
        libro(libroDAO, "978-0003", "La casa de los espíritus", "39000", 6, 1, 2, 2);
        libro(libroDAO, "978-0004", "Rayuela", "48000", 4, 1, 2, 3);
        libro(libroDAO, "978-0005", "Cosmos", "55000", 10, 2, 1, 4);
        libro(libroDAO, "978-0006", "Sapiens", "62000", 9, 3, 1, 5);
        libro(libroDAO, "978-0007", "Homo Deus", "60000", 5, 3, 1, 5);
        libro(libroDAO, "978-0008", "El principito", "28000", 15, 4, 2, 6);
        libro(libroDAO, "978-0009", "Effective Java", "120000", 4, 5, 3, 7);
        libro(libroDAO, "978-0010", "Clean Code", "110000", 6, 5, 3, 8);
        libro(libroDAO, "978-0011", "Clean Architecture", "115000", 4, 5, 3, 8);
        libro(libroDAO, "978-0012", "Pálido punto azul", "58000", 2, 2, 1, 4);
        libro(libroDAO, "978-0013", "Introducción a las bases de datos", "70000", 7, 5, 3, 9, 10);

        venta(clienteDAO, libroDAO, ventaRepo, 1, "2026-09-03", new int[]{1, 2}, new int[]{8, 1});
        venta(clienteDAO, libroDAO, ventaRepo, 2, "2026-09-05", new int[]{6, 1});
        venta(clienteDAO, libroDAO, ventaRepo, 1, "2026-09-12", new int[]{9, 1}, new int[]{10, 1});
        venta(clienteDAO, libroDAO, ventaRepo, 3, "2026-09-15", new int[]{5, 2});
        venta(clienteDAO, libroDAO, ventaRepo, 4, "2026-09-20", new int[]{1, 1}, new int[]{3, 1}, new int[]{8, 2});
        venta(clienteDAO, libroDAO, ventaRepo, 2, "2026-10-01", new int[]{10, 2});
        venta(clienteDAO, libroDAO, ventaRepo, 5, "2026-10-04", new int[]{8, 3});
        venta(clienteDAO, libroDAO, ventaRepo, 1, "2026-10-10", new int[]{6, 1}, new int[]{7, 1});
        venta(clienteDAO, libroDAO, ventaRepo, 3, "2026-10-15", new int[]{1, 3});
        venta(clienteDAO, libroDAO, ventaRepo, 4, "2026-10-20", new int[]{2, 1}, new int[]{4, 1});
    }

    private static void libro(LibroDAO dao, String isbn, String titulo, String precio, int stock,
                              int idCategoria, int idProveedor, Integer... autores) {
        dao.insertar(new Libro(null, isbn, titulo, new BigDecimal(precio), stock, idCategoria, idProveedor,
                Arrays.asList(autores)));
    }

    private static void venta(ClienteDAO clienteDAO, LibroDAO libroDAO, VentaRepository repo,
                              int idCliente, String fecha, int[]... items) {
        Venta v = new Venta(null, idCliente, LocalDate.parse(fecha));
        for (int[] item : items) {
            Libro l = libroDAO.buscarPorId(item[0]).orElseThrow();
            v.agregarDetalle(new DetalleVenta(null, l.getId(), item[1], l.getPrecio()));
        }
        v.setDescuento(BigDecimal.ZERO);
        repo.guardarConDetalles(v);
    }
}
