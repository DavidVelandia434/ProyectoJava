/*
 * Programa : Sistema de gestión de librería
 * Archivo  : VentanaPrincipalAWT.java
 * Paquete  : libreria.gui (Carpeta independiente para la Fachada y la Interfaz Gráfica AWT)
 * Fecha    : 06/11/2026
 */
package libreria.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import libreria.dao.PersistenciaException;
import libreria.service.ReglaNegocioException;

/**
 * INTERFAZ GRÁFICA DE ESCRITORIO EN JAVA AWT PURO
 * =========================================================================
 * Diseñada estrictamente como vista pasiva sobre LibreriaFacade (Patrón Facade).
 * - NO contiene consultas SQL, ni cálculos de negocio, ni validaciones de dominio.
 * - Toda acción del usuario es delegada directamente a LibreriaFacade.
 * - Incluye diálogo modal de confirmación (RF10) antes de eliminar registros.
 */
public class VentanaPrincipalAWT extends Frame {

    // Paleta visual moderna y agradable a la vista
    private static final Color COLOR_HEADER = new Color(15, 23, 42);       // Slate 900
    private static final Color COLOR_SIDEBAR = new Color(30, 41, 59);      // Slate 800
    private static final Color COLOR_BTN_NAV = new Color(51, 65, 85);      // Slate 700
    private static final Color COLOR_BTN_ACTIVO = new Color(37, 99, 235);  // Blue 600
    private static final Color COLOR_FONDO = new Color(241, 245, 249);     // Slate 100
    private static final Color COLOR_TARJETA = new Color(255, 255, 255);   // Blanco puro
    private static final Color COLOR_PRIMARIO = new Color(22, 163, 74);    // Emerald 600
    private static final Color COLOR_SECUNDARIO = new Color(217, 119, 6);  // Amber 600
    private static final Color COLOR_PELIGRO = new Color(220, 38, 38);     // Red 600
    private static final Color COLOR_TEXTO = new Color(15, 23, 42);

    private static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FUENTE_SUBTITULO = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FUENTE_NORMAL = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FUENTE_BOTON = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FUENTE_MONO = new Font("Monospaced", Font.PLAIN, 12);

    private final LibreriaFacade facade;

    // Contenedores globales
    private final CardLayout cardLayout = new CardLayout();
    private final Panel panelCentral = new Panel(cardLayout);
    private final Label lblEstadoConexion = new Label();
    private final Label lblBarraEstado = new Label("Listo. Seleccione una operación en el panel izquierdo.");

    // Componentes Módulo 1: Libros
    private final java.awt.List listaLibrosUI = new java.awt.List(12, false);
    private final TextField txtIsbnLibro = new TextField(12);
    private final TextField txtTituloLibro = new TextField(18);
    private final TextField txtPrecioLibro = new TextField(8);
    private final TextField txtStockLibro = new TextField(5);
    private final Choice choiceCategoriaLibro = new Choice();
    private final Choice choiceProveedorLibro = new Choice();
    private final TextField txtIdsAutoresLibro = new TextField("1", 8);
    private final TextField txtIdLibroAccion = new TextField(5);
    private final TextField txtValorAccionLibro = new TextField(14);
    private final TextField txtMotivoStock = new TextField("Ajuste de inventario", 14);
    private final TextField txtFiltroMinPrecio = new TextField("30000", 7);
    private final TextField txtFiltroMaxPrecio = new TextField("70000", 7);
    private final TextField txtFiltroUmbralStock = new TextField("5", 4);

    // Componentes Módulo 2: Ventas (POS)
    private final Choice choiceClienteVenta = new Choice();
    private final Choice choiceLibroVenta = new Choice();
    private final TextField txtCantidadVenta = new TextField("1", 4);
    private final java.awt.List listaCarritoUI = new java.awt.List(6, false);
    private final java.awt.List listaHistorialVentasUI = new java.awt.List(9, false);
    private final Label lblPoliticaActiva = new Label();
    private final TextField txtFechaDesde = new TextField("2026-09-01", 10);
    private final TextField txtFechaHasta = new TextField("2026-12-31", 10);

    // Componentes Módulo 3: Clientes
    private final java.awt.List listaClientesUI = new java.awt.List(12, false);
    private final TextField txtIdCliente = new TextField(4);
    private final TextField txtNombreCliente = new TextField(16);
    private final TextField txtDocCliente = new TextField(12);
    private final TextField txtTelCliente = new TextField(12);
    private final TextField txtCorreoCliente = new TextField(18);

    // Componentes Módulo 4: Catálogo (Autores, Categorías, Proveedores)
    private final java.awt.List listaAutoresUI = new java.awt.List(7, false);
    private final TextField txtIdAutor = new TextField(4);
    private final TextField txtNombreAutor = new TextField(14);
    private final TextField txtNacAutor = new TextField(12);

    private final java.awt.List listaCategoriasUI = new java.awt.List(7, false);
    private final TextField txtIdCategoria = new TextField(4);
    private final TextField txtNombreCategoria = new TextField(14);

    private final java.awt.List listaProveedoresUI = new java.awt.List(7, false);
    private final TextField txtIdProveedor = new TextField(4);
    private final TextField txtNombreProveedor = new TextField(14);
    private final TextField txtTelProveedor = new TextField(10);
    private final TextField txtCorreoProveedor = new TextField(14);

    // Componentes Módulo 5: Consultas y Reportes (15 Consultas)
    private final TextArea areaReportes = new TextArea("", 22, 90, TextArea.SCROLLBARS_VERTICAL_ONLY);
    private final TextField txtParametroHaving = new TextField("2", 4);

    // Componentes Módulo 6: Estado Base de Datos (Supabase JDBC)
    private final TextArea areaDiagnosticoBD = new TextArea("", 14, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);

    public VentanaPrincipalAWT(LibreriaFacade facade) {
        super("Sistema de Gestión de Librería - Interfaz AWT (Patrón Facade)");
        this.facade = facade;
        configurarVentana();
        construirInterfaz();
        refrescarTodosLosDatos();
    }

    private void configurarVentana() {
        setSize(1120, 720);
        setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(0, 0));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
    }

    private void construirInterfaz() {
        // 1. ENCABEZADO SUPERIOR
        Panel header = new Panel(new BorderLayout(15, 0));
        header.setBackground(COLOR_HEADER);
        header.setPreferredSize(new Dimension(1120, 56));

        Label titulo = new Label("  LIBRERÍA DE BARRIO — SISTEMA INTEGRAL DE GESTIÓN (PATRÓN FACADE)");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(FUENTE_TITULO);

        lblEstadoConexion.setForeground(new Color(134, 239, 172));
        lblEstadoConexion.setFont(FUENTE_SUBTITULO);
        actualizarEtiquetaModo();

        header.add(titulo, BorderLayout.WEST);
        header.add(lblEstadoConexion, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 2. MENÚ LATERAL IZQUIERDO (SIDEBAR)
        Panel sidebar = new Panel(new GridLayout(10, 1, 0, 8));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(215, 600));

        Label lblMenu = new Label("  MÓDULOS DEL SISTEMA", Label.LEFT);
        lblMenu.setForeground(new Color(148, 163, 184));
        lblMenu.setFont(FUENTE_SUBTITULO);
        sidebar.add(lblMenu);

        Button btnNavLibros = crearBotonMenu("1. Inventario de Libros");
        Button btnNavVentas = crearBotonMenu("2. Registrar y Ver Ventas");
        Button btnNavClientes = crearBotonMenu("3. Gestión de Clientes");
        Button btnNavCatalogo = crearBotonMenu("4. Autores / Cat. / Prov.");
        Button btnNavReportes = crearBotonMenu("5. Las 15 Consultas SQL");
        Button btnNavBD = crearBotonMenu("6. Conexión BD Supabase");
        Button btnRefrescar = crearBotonAccion("Actualizar Todo", COLOR_BTN_ACTIVO);

        sidebar.add(btnNavLibros);
        sidebar.add(btnNavVentas);
        sidebar.add(btnNavClientes);
        sidebar.add(btnNavCatalogo);
        sidebar.add(btnNavReportes);
        sidebar.add(btnNavBD);
        sidebar.add(new Label(""));
        sidebar.add(btnRefrescar);
        add(sidebar, BorderLayout.WEST);

        // 3. PANELES CENTRALES (CARDLAYOUT)
        panelCentral.setBackground(COLOR_FONDO);
        panelCentral.add(crearPanelLibros(), "LIBROS");
        panelCentral.add(crearPanelVentas(), "VENTAS");
        panelCentral.add(crearPanelClientes(), "CLIENTES");
        panelCentral.add(crearPanelCatalogo(), "CATALOGO");
        panelCentral.add(crearPanelReportes(), "REPORTES");
        panelCentral.add(crearPanelConexionBD(), "BD");
        add(panelCentral, BorderLayout.CENTER);

        // Eventos de navegación
        btnNavLibros.addActionListener(e -> mostrarSeccion("LIBROS", "Módulo de Inventario de Libros (CRUD + Consultas 1 a 4 + UPDATE 1, 2 y 3)."));
        btnNavVentas.addActionListener(e -> mostrarSeccion("VENTAS", "Módulo de Punto de Venta e Historial Transaccional (RF03 a RF07)."));
        btnNavClientes.addActionListener(e -> mostrarSeccion("CLIENTES", "Módulo de Clientes (CRUD + UPDATE 4: Actualizar contacto)."));
        btnNavCatalogo.addActionListener(e -> mostrarSeccion("CATALOGO", "Módulo de Autores, Categorías y Proveedores (UPDATE 5)."));
        btnNavReportes.addActionListener(e -> {
            ejecutarEnFachada(() -> areaReportes.setText(facade.generarReporteCompletoConsultas(txtParametroHaving.getText())));
            mostrarSeccion("REPORTES", "Reporte analítico en tiempo real de las 15 consultas del proyecto.");
        });
        btnNavBD.addActionListener(e -> {
            ejecutarEnFachada(() -> areaDiagnosticoBD.setText(facade.probarConexionSupabase()));
            mostrarSeccion("BD", "Diagnóstico de conexión JDBC hacia PostgreSQL (Supabase).");
        });
        btnRefrescar.addActionListener(e -> {
            refrescarTodosLosDatos();
            mostrarExito("Datos sincronizados correctamente desde la fachada.");
        });

        // 4. BARRA DE ESTADO INFERIOR
        Panel footer = new Panel(new BorderLayout());
        footer.setBackground(new Color(226, 232, 240));
        footer.setPreferredSize(new Dimension(1120, 32));
        lblBarraEstado.setFont(FUENTE_SUBTITULO);
        lblBarraEstado.setForeground(COLOR_TEXTO);
        footer.add(lblBarraEstado, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    // =========================================================================
    // CONSTRUCCIÓN DE CADA SECCIÓN VISUAL (SOLO COMPONENTES AWT Y DELEGACIÓN)
    // =========================================================================

    private Panel crearPanelLibros() {
        Panel contenedor = new Panel(new BorderLayout(10, 10));
        contenedor.setBackground(COLOR_FONDO);

        // Parte superior: listado y barra de filtros (Consultas 1, 2, 3 y 4)
        Panel panelArriba = new Panel(new BorderLayout(6, 6));
        panelArriba.setBackground(COLOR_TARJETA);

        Panel barraFiltros = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        barraFiltros.setBackground(COLOR_TARJETA);
        Button btnVerTodos = crearBotonAccion("Ordenar por Título (C1)", COLOR_BTN_ACTIVO);
        Button btnFiltrarCat = crearBotonAccion("Filtrar CategoríaSel (C2)", COLOR_BTN_NAV);
        Button btnFiltrarPrecio = crearBotonAccion("Rango Precio (C3)", COLOR_BTN_NAV);
        Button btnFiltrarStock = crearBotonAccion("Stock < Umbral (C4)", COLOR_SECUNDARIO);

        barraFiltros.add(btnVerTodos);
        barraFiltros.add(btnFiltrarCat);
        barraFiltros.add(new Label("Precio Min:"));
        barraFiltros.add(txtFiltroMinPrecio);
        barraFiltros.add(new Label("Max:"));
        barraFiltros.add(txtFiltroMaxPrecio);
        barraFiltros.add(btnFiltrarPrecio);
        barraFiltros.add(new Label("Stock <"));
        barraFiltros.add(txtFiltroUmbralStock);
        barraFiltros.add(btnFiltrarStock);

        listaLibrosUI.setFont(FUENTE_MONO);
        listaLibrosUI.addItemListener(e -> {
            String sel = listaLibrosUI.getSelectedItem();
            if (sel != null && sel.startsWith("[")) {
                int fin = sel.indexOf(']');
                if (fin > 1) txtIdLibroAccion.setText(sel.substring(1, fin));
            }
        });

        panelArriba.add(barraFiltros, BorderLayout.NORTH);
        panelArriba.add(listaLibrosUI, BorderLayout.CENTER);

        // Parte inferior: Formularios de Registro y Actualizaciones (UPDATE 1, 2, 3 + DELETE)
        Panel panelAbajo = new Panel(new GridLayout(2, 1, 8, 8));
        panelAbajo.setBackground(COLOR_FONDO);

        // Tarjeta 1: Registrar Nuevo Libro
        Panel cardRegistro = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        cardRegistro.setBackground(COLOR_TARJETA);
        cardRegistro.add(crearEtiquetaSeccion("Registrar Libro:"));
        cardRegistro.add(new Label("ISBN:"));
        cardRegistro.add(txtIsbnLibro);
        cardRegistro.add(new Label("Título:"));
        cardRegistro.add(txtTituloLibro);
        cardRegistro.add(new Label("Precio:"));
        cardRegistro.add(txtPrecioLibro);
        cardRegistro.add(new Label("Stock:"));
        cardRegistro.add(txtStockLibro);
        cardRegistro.add(new Label("Cat:"));
        cardRegistro.add(choiceCategoriaLibro);
        cardRegistro.add(new Label("Prov:"));
        cardRegistro.add(choiceProveedorLibro);
        cardRegistro.add(new Label("IDs Autor (ej. 1,2):"));
        cardRegistro.add(txtIdsAutoresLibro);
        Button btnGuardarLibro = crearBotonAccion("Guardar Libro", COLOR_PRIMARIO);
        cardRegistro.add(btnGuardarLibro);

        // Tarjeta 2: Operaciones UPDATE 1, UPDATE 2, UPDATE 3 y DELETE con Confirmación (RF10)
        Panel cardAcciones = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        cardAcciones.setBackground(COLOR_TARJETA);
        cardAcciones.add(crearEtiquetaSeccion("Modificar / Eliminar:"));
        cardAcciones.add(new Label("ID Libro:"));
        cardAcciones.add(txtIdLibroAccion);
        cardAcciones.add(new Label("Nuevo Valor (Precio/Título/Stock):"));
        cardAcciones.add(txtValorAccionLibro);
        cardAcciones.add(new Label("Motivo Ajuste:"));
        cardAcciones.add(txtMotivoStock);

        Button btnUpdatePrecio = crearBotonAccion("UPDATE 1: Precio", COLOR_SECUNDARIO);
        Button btnUpdateTitulo = crearBotonAccion("UPDATE 2: Título", COLOR_SECUNDARIO);
        Button btnUpdateStock = crearBotonAccion("UPDATE 3: Stock", COLOR_SECUNDARIO);
        Button btnEliminarLibro = crearBotonAccion("Eliminar Libro", COLOR_PELIGRO);

        cardAcciones.add(btnUpdatePrecio);
        cardAcciones.add(btnUpdateTitulo);
        cardAcciones.add(btnUpdateStock);
        cardAcciones.add(btnEliminarLibro);

        panelAbajo.add(cardRegistro);
        panelAbajo.add(cardAcciones);

        // Delegación de eventos a LibreriaFacade
        btnVerTodos.addActionListener(e -> ejecutarEnFachada(() -> {
            cargarEnLista(listaLibrosUI, facade.obtenerLibrosFormateados());
            mostrarExito("Consulta 1: Libros ordenados alfabéticamente por título.");
        }));

        btnFiltrarCat.addActionListener(e -> ejecutarEnFachada(() -> {
            String itemCat = choiceCategoriaLibro.getSelectedItem();
            int ini = itemCat.indexOf('[');
            int fin = itemCat.indexOf(']');
            String idCat = (ini >= 0 && fin > ini) ? itemCat.substring(ini + 1, fin) : itemCat;
            cargarEnLista(listaLibrosUI, facade.filtrarLibrosPorCategoria(idCat));
            mostrarExito("Consulta 2: Libros filtrados por categoría " + itemCat);
        }));

        btnFiltrarPrecio.addActionListener(e -> ejecutarEnFachada(() -> {
            cargarEnLista(listaLibrosUI, facade.filtrarLibrosPorRangoPrecio(
                    txtFiltroMinPrecio.getText(), txtFiltroMaxPrecio.getText()));
            mostrarExito("Consulta 3: Libros filtrados por rango de precio.");
        }));

        btnFiltrarStock.addActionListener(e -> ejecutarEnFachada(() -> {
            cargarEnLista(listaLibrosUI, facade.filtrarLibrosStockBajo(txtFiltroUmbralStock.getText()));
            mostrarExito("Consulta 4 (RF08): Libros con existencias inferiores al umbral.");
        }));

        btnGuardarLibro.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.registrarLibro(
                txtIsbnLibro.getText(),
                txtTituloLibro.getText(),
                txtPrecioLibro.getText(),
                txtStockLibro.getText(),
                choiceCategoriaLibro.getSelectedItem(),
                choiceProveedorLibro.getSelectedItem(),
                txtIdsAutoresLibro.getText()
            );
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));

        btnUpdatePrecio.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.actualizarPrecioLibro(txtIdLibroAccion.getText(), txtValorAccionLibro.getText());
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));

        btnUpdateTitulo.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.corregirTituloLibro(txtIdLibroAccion.getText(), txtValorAccionLibro.getText());
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));

        btnUpdateStock.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.ajustarStockLibro(
                    txtIdLibroAccion.getText(), txtValorAccionLibro.getText(), txtMotivoStock.getText());
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));

        btnEliminarLibro.addActionListener(e -> {
            String id = txtIdLibroAccion.getText().trim();
            if (confirmarAccion("¿Confirma que desea eliminar el libro con ID " + id + "?")) {
                ejecutarEnFachada(() -> {
                    String msg = facade.eliminarLibro(id);
                    refrescarTodosLosDatos();
                    mostrarExito(msg);
                });
            }
        });

        contenedor.add(panelArriba, BorderLayout.CENTER);
        contenedor.add(panelAbajo, BorderLayout.SOUTH);
        return contenedor;
    }

    private Panel crearPanelVentas() {
        Panel contenedor = new Panel(new GridLayout(2, 1, 10, 10));
        contenedor.setBackground(COLOR_FONDO);

        // Mitad superior: Carrito y registro de venta (RF03 a RF06)
        Panel panelCaja = new Panel(new BorderLayout(8, 8));
        panelCaja.setBackground(COLOR_TARJETA);

        Panel formCaja = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        formCaja.setBackground(COLOR_TARJETA);
        formCaja.add(crearEtiquetaSeccion("Nueva Venta:"));
        formCaja.add(new Label("Cliente:"));
        formCaja.add(choiceClienteVenta);
        formCaja.add(new Label("Libro:"));
        formCaja.add(choiceLibroVenta);
        formCaja.add(new Label("Cantidad:"));
        formCaja.add(txtCantidadVenta);

        Button btnAgregarItem = crearBotonAccion("+ Agregar al Carrito", COLOR_BTN_ACTIVO);
        Button btnLimpiarCarrito = crearBotonAccion("Vaciar Carrito", COLOR_SECUNDARIO);
        Button btnConfirmarVenta = crearBotonAccion("Confirmar y Facturar Venta", COLOR_PRIMARIO);

        formCaja.add(btnAgregarItem);
        formCaja.add(btnLimpiarCarrito);
        formCaja.add(btnConfirmarVenta);

        listaCarritoUI.setFont(FUENTE_MONO);
        lblPoliticaActiva.setFont(FUENTE_SUBTITULO);
        lblPoliticaActiva.setForeground(COLOR_PRIMARIO);

        panelCaja.add(formCaja, BorderLayout.NORTH);
        panelCaja.add(listaCarritoUI, BorderLayout.CENTER);
        panelCaja.add(lblPoliticaActiva, BorderLayout.SOUTH);

        // Mitad inferior: Historial de ventas y filtros (RF07, Consultas 11 y 12)
        Panel panelHistorial = new Panel(new BorderLayout(8, 8));
        panelHistorial.setBackground(COLOR_TARJETA);

        Panel barraHistorial = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        barraHistorial.setBackground(COLOR_TARJETA);
        barraHistorial.add(crearEtiquetaSeccion("Historial de Ventas:"));
        Button btnTodasVentas = crearBotonAccion("Ver Todas (C11/C12)", COLOR_BTN_ACTIVO);
        Button btnPorCliente = crearBotonAccion("Filtrar por Cliente Seleccionado", COLOR_BTN_NAV);
        barraHistorial.add(btnTodasVentas);
        barraHistorial.add(btnPorCliente);
        barraHistorial.add(new Label("Desde (YYYY-MM-DD):"));
        barraHistorial.add(txtFechaDesde);
        barraHistorial.add(new Label("Hasta:"));
        barraHistorial.add(txtFechaHasta);
        Button btnPorFechas = crearBotonAccion("Filtrar por Fechas (RF07)", COLOR_SECUNDARIO);
        barraHistorial.add(btnPorFechas);

        listaHistorialVentasUI.setFont(FUENTE_MONO);
        panelHistorial.add(barraHistorial, BorderLayout.NORTH);
        panelHistorial.add(listaHistorialVentasUI, BorderLayout.CENTER);

        // Eventos delegados a la fachada
        btnAgregarItem.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.agregarItemAlCarrito(choiceLibroVenta.getSelectedItem(), txtCantidadVenta.getText());
            cargarEnLista(listaCarritoUI, facade.obtenerLineasCarritoFormateadas());
            mostrarExito(msg);
        }));

        btnLimpiarCarrito.addActionListener(e -> ejecutarEnFachada(() -> {
            facade.limpiarCarrito();
            cargarEnLista(listaCarritoUI, facade.obtenerLineasCarritoFormateadas());
            mostrarExito("Carrito vaciado.");
        }));

        btnConfirmarVenta.addActionListener(e -> {
            if (confirmarAccion("¿Desea confirmar la venta y descontar las existencias del inventario?")) {
                ejecutarEnFachada(() -> {
                    String msg = facade.confirmarVentaCarrito(choiceClienteVenta.getSelectedItem());
                    refrescarTodosLosDatos();
                    cargarEnLista(listaCarritoUI, facade.obtenerLineasCarritoFormateadas());
                    mostrarExito(msg);
                });
            }
        });

        btnTodasVentas.addActionListener(e -> ejecutarEnFachada(() -> {
            cargarEnLista(listaHistorialVentasUI, facade.obtenerTodasLasVentasFormateadas());
            mostrarExito("Mostrando todas las ventas y sus detalles.");
        }));

        btnPorCliente.addActionListener(e -> ejecutarEnFachada(() -> {
            cargarEnLista(listaHistorialVentasUI, facade.obtenerVentasPorCliente(choiceClienteVenta.getSelectedItem()));
            mostrarExito("Historial filtrado por cliente.");
        }));

        btnPorFechas.addActionListener(e -> ejecutarEnFachada(() -> {
            cargarEnLista(listaHistorialVentasUI, facade.obtenerVentasPorFechas(
                    txtFechaDesde.getText(), txtFechaHasta.getText()));
            mostrarExito("Historial filtrado por rango de fechas.");
        }));

        contenedor.add(panelCaja);
        contenedor.add(panelHistorial);
        return contenedor;
    }

    private Panel crearPanelClientes() {
        Panel contenedor = new Panel(new BorderLayout(10, 10));
        contenedor.setBackground(COLOR_FONDO);

        listaClientesUI.setFont(FUENTE_MONO);
        listaClientesUI.addItemListener(e -> {
            String sel = listaClientesUI.getSelectedItem();
            if (sel != null && sel.startsWith("[")) {
                int fin = sel.indexOf(']');
                if (fin > 1) txtIdCliente.setText(sel.substring(1, fin));
            }
        });

        Panel formCliente = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        formCliente.setBackground(COLOR_TARJETA);
        formCliente.add(crearEtiquetaSeccion("Cliente:"));
        formCliente.add(new Label("ID (para editar/borrar):"));
        formCliente.add(txtIdCliente);
        formCliente.add(new Label("Nombre:"));
        formCliente.add(txtNombreCliente);
        formCliente.add(new Label("Documento:"));
        formCliente.add(txtDocCliente);
        formCliente.add(new Label("Teléfono:"));
        formCliente.add(txtTelCliente);
        formCliente.add(new Label("Correo:"));
        formCliente.add(txtCorreoCliente);

        Button btnRegistrar = crearBotonAccion("Registrar Cliente", COLOR_PRIMARIO);
        Button btnActualizar = crearBotonAccion("UPDATE 4: Actualizar Contacto", COLOR_SECUNDARIO);
        Button btnEliminar = crearBotonAccion("Eliminar Cliente", COLOR_PELIGRO);

        formCliente.add(btnRegistrar);
        formCliente.add(btnActualizar);
        formCliente.add(btnEliminar);

        btnRegistrar.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.registrarCliente(
                    txtNombreCliente.getText(), txtDocCliente.getText(),
                    txtTelCliente.getText(), txtCorreoCliente.getText());
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));

        btnActualizar.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.actualizarContactoCliente(
                    txtIdCliente.getText(), txtNombreCliente.getText(),
                    txtTelCliente.getText(), txtCorreoCliente.getText());
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));

        btnEliminar.addActionListener(e -> {
            String id = txtIdCliente.getText().trim();
            if (confirmarAccion("¿Confirma que desea eliminar el cliente con ID " + id + "?")) {
                ejecutarEnFachada(() -> {
                    String msg = facade.eliminarCliente(id);
                    refrescarTodosLosDatos();
                    mostrarExito(msg);
                });
            }
        });

        contenedor.add(listaClientesUI, BorderLayout.CENTER);
        contenedor.add(formCliente, BorderLayout.SOUTH);
        return contenedor;
    }

    private Panel crearPanelCatalogo() {
        Panel contenedor = new Panel(new GridLayout(3, 1, 8, 8));
        contenedor.setBackground(COLOR_FONDO);

        // 1. Subpanel Autores
        Panel pAutores = new Panel(new BorderLayout(6, 6));
        pAutores.setBackground(COLOR_TARJETA);
        listaAutoresUI.setFont(FUENTE_MONO);
        Panel fAutor = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        fAutor.setBackground(COLOR_TARJETA);
        fAutor.add(crearEtiquetaSeccion("Autores:"));
        fAutor.add(new Label("Nombre:"));
        fAutor.add(txtNombreAutor);
        fAutor.add(new Label("Nacionalidad:"));
        fAutor.add(txtNacAutor);
        Button btnCrearAutor = crearBotonAccion("Registrar Autor", COLOR_PRIMARIO);
        fAutor.add(btnCrearAutor);
        fAutor.add(new Label(" | ID Autor a eliminar:"));
        fAutor.add(txtIdAutor);
        Button btnBorrarAutor = crearBotonAccion("Eliminar Autor", COLOR_PELIGRO);
        fAutor.add(btnBorrarAutor);
        pAutores.add(listaAutoresUI, BorderLayout.CENTER);
        pAutores.add(fAutor, BorderLayout.SOUTH);

        btnCrearAutor.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.registrarAutor(txtNombreAutor.getText(), txtNacAutor.getText());
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));
        btnBorrarAutor.addActionListener(e -> {
            String id = txtIdAutor.getText().trim();
            if (confirmarAccion("¿Confirma eliminar el autor #" + id + "?")) {
                ejecutarEnFachada(() -> {
                    String msg = facade.eliminarAutor(id);
                    refrescarTodosLosDatos();
                    mostrarExito(msg);
                });
            }
        });

        // 2. Subpanel Categorías
        Panel pCategorias = new Panel(new BorderLayout(6, 6));
        pCategorias.setBackground(COLOR_TARJETA);
        listaCategoriasUI.setFont(FUENTE_MONO);
        Panel fCat = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        fCat.setBackground(COLOR_TARJETA);
        fCat.add(crearEtiquetaSeccion("Categorías:"));
        fCat.add(new Label("Nombre Categoría:"));
        fCat.add(txtNombreCategoria);
        Button btnCrearCat = crearBotonAccion("Registrar Categoría", COLOR_PRIMARIO);
        fCat.add(btnCrearCat);
        fCat.add(new Label(" | ID Categoría a eliminar:"));
        fCat.add(txtIdCategoria);
        Button btnBorrarCat = crearBotonAccion("Eliminar Categoría", COLOR_PELIGRO);
        fCat.add(btnBorrarCat);
        pCategorias.add(listaCategoriasUI, BorderLayout.CENTER);
        pCategorias.add(fCat, BorderLayout.SOUTH);

        btnCrearCat.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.registrarCategoria(txtNombreCategoria.getText());
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));
        btnBorrarCat.addActionListener(e -> {
            String id = txtIdCategoria.getText().trim();
            if (confirmarAccion("¿Confirma eliminar la categoría #" + id + "?")) {
                ejecutarEnFachada(() -> {
                    String msg = facade.eliminarCategoria(id);
                    refrescarTodosLosDatos();
                    mostrarExito(msg);
                });
            }
        });

        // 3. Subpanel Proveedores (incluye UPDATE 5)
        Panel pProveedores = new Panel(new BorderLayout(6, 6));
        pProveedores.setBackground(COLOR_TARJETA);
        listaProveedoresUI.setFont(FUENTE_MONO);
        Panel fProv = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        fProv.setBackground(COLOR_TARJETA);
        fProv.add(crearEtiquetaSeccion("Proveedores:"));
        fProv.add(new Label("ID:"));
        fProv.add(txtIdProveedor);
        fProv.add(new Label("Nombre:"));
        fProv.add(txtNombreProveedor);
        fProv.add(new Label("Teléfono:"));
        fProv.add(txtTelProveedor);
        fProv.add(new Label("Correo:"));
        fProv.add(txtCorreoProveedor);
        Button btnCrearProv = crearBotonAccion("Registrar Proveedor", COLOR_PRIMARIO);
        Button btnUpdateProv = crearBotonAccion("UPDATE 5: Actualizar Proveedor", COLOR_SECUNDARIO);
        Button btnBorrarProv = crearBotonAccion("Eliminar Proveedor", COLOR_PELIGRO);
        fProv.add(btnCrearProv);
        fProv.add(btnUpdateProv);
        fProv.add(btnBorrarProv);
        pProveedores.add(listaProveedoresUI, BorderLayout.CENTER);
        pProveedores.add(fProv, BorderLayout.SOUTH);

        btnCrearProv.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.registrarProveedor(
                    txtNombreProveedor.getText(), txtTelProveedor.getText(), txtCorreoProveedor.getText());
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));
        btnUpdateProv.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.actualizarProveedor(
                    txtIdProveedor.getText(), txtNombreProveedor.getText(),
                    txtTelProveedor.getText(), txtCorreoProveedor.getText());
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));
        btnBorrarProv.addActionListener(e -> {
            String id = txtIdProveedor.getText().trim();
            if (confirmarAccion("¿Confirma eliminar el proveedor #" + id + "?")) {
                ejecutarEnFachada(() -> {
                    String msg = facade.eliminarProveedor(id);
                    refrescarTodosLosDatos();
                    mostrarExito(msg);
                });
            }
        });

        contenedor.add(pAutores);
        contenedor.add(pCategorias);
        contenedor.add(pProveedores);
        return contenedor;
    }

    private Panel crearPanelReportes() {
        Panel contenedor = new Panel(new BorderLayout(8, 8));
        contenedor.setBackground(COLOR_TARJETA);

        Panel barra = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        barra.setBackground(COLOR_TARJETA);
        barra.add(crearEtiquetaSeccion("15 Consultas y Reportes Analíticos:"));
        barra.add(new Label("Parámetro N (para Consulta 13 HAVING > N libros):"));
        barra.add(txtParametroHaving);
        Button btnEjecutar = crearBotonAccion("Generar / Actualizar Reporte Completo", COLOR_PRIMARIO);
        barra.add(btnEjecutar);

        areaReportes.setEditable(false);
        areaReportes.setFont(FUENTE_MONO);
        areaReportes.setBackground(new Color(248, 250, 252));

        btnEjecutar.addActionListener(e -> ejecutarEnFachada(() -> {
            areaReportes.setText(facade.generarReporteCompletoConsultas(txtParametroHaving.getText()));
            mostrarExito("Reporte de las 15 consultas generado a través de LibreriaFacade.");
        }));

        contenedor.add(barra, BorderLayout.NORTH);
        contenedor.add(areaReportes, BorderLayout.CENTER);
        return contenedor;
    }

    private Panel crearPanelConexionBD() {
        Panel contenedor = new Panel(new BorderLayout(10, 10));
        contenedor.setBackground(COLOR_TARJETA);

        Panel barra = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        barra.setBackground(COLOR_TARJETA);
        barra.add(crearEtiquetaSeccion("Estado de Conexión PostgreSQL (Supabase):"));
        Button btnProbar = crearBotonAccion("Probar Conexión Supabase", COLOR_BTN_ACTIVO);
        Button btnActivarJdbc = crearBotonAccion("Usar PostgreSQL (JDBC)", COLOR_PRIMARIO);
        Button btnActivarMemoria = crearBotonAccion("Usar Memoria (DatosPrueba)", COLOR_SECUNDARIO);

        barra.add(btnProbar);
        barra.add(btnActivarJdbc);
        barra.add(btnActivarMemoria);

        areaDiagnosticoBD.setEditable(false);
        areaDiagnosticoBD.setFont(FUENTE_MONO);
        areaDiagnosticoBD.setBackground(new Color(248, 250, 252));

        btnProbar.addActionListener(e -> ejecutarEnFachada(() -> {
            areaDiagnosticoBD.setText(facade.probarConexionSupabase());
            mostrarExito("Diagnóstico de conexión ejecutado.");
        }));

        btnActivarJdbc.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.cambiarModoPersistencia(true);
            actualizarEtiquetaModo();
            refrescarTodosLosDatos();
            areaDiagnosticoBD.setText(facade.probarConexionSupabase() + "\n\nResultado: " + msg);
            mostrarExito(msg);
        }));

        btnActivarMemoria.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.cambiarModoPersistencia(false);
            actualizarEtiquetaModo();
            refrescarTodosLosDatos();
            areaDiagnosticoBD.setText("Modo en memoria activo con los registros de DatosPrueba.java.\n" + msg);
            mostrarExito(msg);
        }));

        contenedor.add(barra, BorderLayout.NORTH);
        contenedor.add(areaDiagnosticoBD, BorderLayout.CENTER);
        return contenedor;
    }

    // =========================================================================
    // UTILIDADES DE PRESENTACIÓN Y SINCRONIZACIÓN CON LA FACHADA
    // =========================================================================

    private void refrescarTodosLosDatos() {
        ejecutarEnFachada(() -> {
            List<String> libros = facade.obtenerLibrosFormateados();
            List<String> clientes = facade.obtenerClientesFormateados();
            List<String> autores = facade.obtenerAutoresFormateados();
            List<String> categorias = facade.obtenerCategoriasFormateadas();
            List<String> proveedores = facade.obtenerProveedoresFormateados();
            List<String> ventas = facade.obtenerTodasLasVentasFormateadas();

            cargarEnLista(listaLibrosUI, libros);
            cargarEnLista(listaClientesUI, clientes);
            cargarEnLista(listaAutoresUI, autores);
            cargarEnLista(listaCategoriasUI, categorias);
            cargarEnLista(listaProveedoresUI, proveedores);
            cargarEnLista(listaHistorialVentasUI, ventas);

            cargarEnChoice(choiceCategoriaLibro, categorias);
            cargarEnChoice(choiceProveedorLibro, proveedores);
            cargarEnChoice(choiceClienteVenta, clientes);
            cargarEnChoice(choiceLibroVenta, libros);

            lblPoliticaActiva.setText("  Política de Descuento activa (Strategy): "
                    + facade.obtenerDescripcionPoliticaDescuento());
            actualizarEtiquetaModo();
        });
    }

    private void actualizarEtiquetaModo() {
        lblEstadoConexion.setText(facade.obtenerEstadoModoPersistencia() + "   ");
    }

    private void mostrarSeccion(String nombreCard, String mensajeEstado) {
        cardLayout.show(panelCentral, nombreCard);
        mostrarExito(mensajeEstado);
    }

    private void ejecutarEnFachada(Runnable accion) {
        try {
            accion.run();
        } catch (ReglaNegocioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        } catch (RuntimeException ex) {
            mostrarError("Ocurrió un error al procesar la solicitud.");
        }
    }

    private void mostrarExito(String mensaje) {
        lblBarraEstado.setForeground(new Color(21, 128, 61));
        lblBarraEstado.setText("  [OK] " + mensaje);
    }

    private void mostrarError(String mensaje) {
        lblBarraEstado.setForeground(COLOR_PELIGRO);
        lblBarraEstado.setText("  [AVISO] " + mensaje);
    }

    /** RF10: Solicita confirmación explícita mediante un Dialog modal de AWT antes de eliminar o confirmar. */
    private boolean confirmarAccion(String pregunta) {
        final boolean[] respuesta = {false};
        Dialog dlg = new Dialog(this, "Confirmación Requerida (RF10)", true);
        dlg.setLayout(new BorderLayout(12, 12));
        dlg.setSize(460, 150);
        dlg.setBackground(COLOR_TARJETA);
        dlg.setLocationRelativeTo(this);

        Label lbl = new Label("  " + pregunta, Label.CENTER);
        lbl.setFont(FUENTE_SUBTITULO);
        dlg.add(lbl, BorderLayout.CENTER);

        Panel botones = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        Button btnSi = crearBotonAccion("Sí, Confirmar", COLOR_PRIMARIO);
        Button btnNo = crearBotonAccion("Cancelar", COLOR_PELIGRO);

        btnSi.addActionListener(e -> {
            respuesta[0] = true;
            dlg.dispose();
        });
        btnNo.addActionListener(e -> {
            respuesta[0] = false;
            dlg.dispose();
        });

        botones.add(btnSi);
        botones.add(btnNo);
        dlg.add(botones, BorderLayout.SOUTH);
        dlg.setVisible(true);
        return respuesta[0];
    }

    private void cargarEnLista(java.awt.List componente, List<String> elementos) {
        componente.removeAll();
        for (String item : elementos) {
            componente.add(item);
        }
    }

    private void cargarEnChoice(Choice choice, List<String> elementos) {
        choice.removeAll();
        for (String item : elementos) {
            choice.add(item);
        }
    }

    private Button crearBotonMenu(String texto) {
        Button b = new Button(texto);
        b.setBackground(COLOR_BTN_NAV);
        b.setForeground(Color.WHITE);
        b.setFont(FUENTE_BOTON);
        return b;
    }

    private Button crearBotonAccion(String texto, Color colorFondo) {
        Button b = new Button(texto);
        b.setBackground(colorFondo);
        b.setForeground(Color.WHITE);
        b.setFont(FUENTE_BOTON);
        return b;
    }

    private Label crearEtiquetaSeccion(String texto) {
        Label l = new Label(texto);
        l.setFont(FUENTE_SUBTITULO);
        l.setForeground(COLOR_TEXTO);
        return l;
    }
}
