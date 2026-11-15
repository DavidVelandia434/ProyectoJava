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
    private static final Font FUENTE_SUBTITULO = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FUENTE_NORMAL = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FUENTE_BOTON = new Font("SansSerif", Font.BOLD, 11);
    private static final Font FUENTE_MONO = new Font("Monospaced", Font.PLAIN, 12);

    private final LibreriaFacade facade;

    // Contenedores globales
    private final CardLayout cardLayout = new CardLayout();
    private final Panel panelCentral = new Panel(cardLayout);
    private final Label lblEstadoConexion = new Label();
    private final Label lblBarraEstado = new Label("Listo. Seleccione una opción en el panel izquierdo.");

    // Componentes Módulo 1: Libros (CRUD + Consultas 1 a 4 + UPDATE 1, 2 y 3)
    private final java.awt.List listaLibrosUI = new java.awt.List(13, false);
    private final TextField txtIdLibroAccion = new TextField(3);
    private final TextField txtIsbnLibro = new TextField(8);
    private final TextField txtTituloLibro = new TextField(12);
    private final TextField txtPrecioLibro = new TextField(6);
    private final TextField txtStockLibro = new TextField(3);
    private final Choice choiceCategoriaLibro = new Choice();
    private final Choice choiceProveedorLibro = new Choice();
    private final TextField txtIdsAutoresLibro = new TextField("1", 4);
    private final TextField txtFiltroMinPrecio = new TextField("30000", 6);
    private final TextField txtFiltroMaxPrecio = new TextField("70000", 6);
    private final TextField txtFiltroUmbralStock = new TextField("5", 3);

    // Componentes Módulo 2: Ventas (POS - RF03 a RF07, Consultas 11 y 12)
    private final Choice choiceClienteVenta = new Choice();
    private final Choice choiceLibroVenta = new Choice();
    private final TextField txtCantidadVenta = new TextField("1", 3);
    private final java.awt.List listaCarritoUI = new java.awt.List(5, false);
    private final java.awt.List listaHistorialVentasUI = new java.awt.List(7, false);
    private final Label lblPoliticaActiva = new Label();
    private final TextField txtFechaDesde = new TextField("2026-09-01", 8);
    private final TextField txtFechaHasta = new TextField("2026-12-31", 8);

    // Componentes Módulo 3: Clientes (CRUD + UPDATE 4)
    private final java.awt.List listaClientesUI = new java.awt.List(10, false);
    private final TextField txtIdCliente = new TextField(3);
    private final TextField txtNombreCliente = new TextField(13);
    private final TextField txtDocCliente = new TextField(9);
    private final TextField txtTelCliente = new TextField(9);
    private final TextField txtCorreoCliente = new TextField(14);

    // Componentes Módulo 4: Catálogo (Autores, Categorías, Proveedores + UPDATE 5)
    private final java.awt.List listaAutoresUI = new java.awt.List(5, false);
    private final TextField txtIdAutor = new TextField(3);
    private final TextField txtNombreAutor = new TextField(11);
    private final TextField txtNacAutor = new TextField(9);

    private final java.awt.List listaCategoriasUI = new java.awt.List(5, false);
    private final TextField txtIdCategoria = new TextField(3);
    private final TextField txtNombreCategoria = new TextField(12);

    private final java.awt.List listaProveedoresUI = new java.awt.List(5, false);
    private final TextField txtIdProveedor = new TextField(3);
    private final TextField txtNombreProveedor = new TextField(11);
    private final TextField txtTelProveedor = new TextField(8);
    private final TextField txtCorreoProveedor = new TextField(11);

    // Componentes Módulo 5: Estadísticas y datos (Consultas analíticas 5 a 15)
    private final TextArea areaReportes = new TextArea("", 22, 90, TextArea.SCROLLBARS_VERTICAL_ONLY);
    private final TextField txtParametroHaving = new TextField("2", 4);

    // Componentes Módulo 6: Estado Base de Datos (Supabase JDBC)
    private final TextArea areaDiagnosticoBD = new TextArea("", 14, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);

    public VentanaPrincipalAWT(LibreriaFacade facade) {
        super("Sistema de Gestión de Librería");
        this.facade = facade;
        configurarVentana();
        construirInterfaz();
        refrescarTodosLosDatos();
    }

    private void configurarVentana() {
        setSize(1160, 740);
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
        header.setPreferredSize(new Dimension(1160, 54));

        Label titulo = new Label("  LIBRERÍA DE BARRIO - SISTEMA INTEGRAL DE GESTIÓN");
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
        sidebar.setPreferredSize(new Dimension(210, 600));

        Label lblMenu = new Label("  MÓDULOS DEL SISTEMA", Label.LEFT);
        lblMenu.setForeground(new Color(148, 163, 184));
        lblMenu.setFont(FUENTE_SUBTITULO);
        sidebar.add(lblMenu);

        Button btnNavLibros = crearBotonMenu("1. Inventario de Libros");
        Button btnNavVentas = crearBotonMenu("2. Registrar y Ver Ventas");
        Button btnNavClientes = crearBotonMenu("3. Gestión de Clientes");
        Button btnNavCatalogo = crearBotonMenu("4. Autores / Cat. / Prov.");
        Button btnNavReportes = crearBotonMenu("5. Estadisticas y datos");
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

        // Eventos de navegación delegados a LibreriaFacade
        btnNavLibros.addActionListener(e -> mostrarSeccion("LIBROS", "Módulo de Inventario de Libros."));
        btnNavVentas.addActionListener(e -> mostrarSeccion("VENTAS", "Módulo de Punto de Venta e Historial de Ventas."));
        btnNavClientes.addActionListener(e -> mostrarSeccion("CLIENTES", "Módulo de Gestión de Clientes."));
        btnNavCatalogo.addActionListener(e -> mostrarSeccion("CATALOGO", "Módulo de Autores, Categorías y Proveedores."));
        btnNavReportes.addActionListener(e -> {
            ejecutarEnFachada(() -> areaReportes.setText(facade.generarReporteCompletoConsultas(txtParametroHaving.getText())));
            mostrarSeccion("REPORTES", "Módulo de Estadísticas y Datos del Negocio.");
        });
        btnNavBD.addActionListener(e -> {
            ejecutarEnFachada(() -> areaDiagnosticoBD.setText(facade.probarConexionSupabase()));
            mostrarSeccion("BD", "Diagnóstico de conexión hacia PostgreSQL (Supabase).");
        });
        btnRefrescar.addActionListener(e -> {
            refrescarTodosLosDatos();
            mostrarExito("Datos sincronizados correctamente desde la base de datos.");
        });

        // 4. BARRA DE ESTADO INFERIOR
        Panel footer = new Panel(new BorderLayout());
        footer.setBackground(new Color(226, 232, 240));
        footer.setPreferredSize(new Dimension(1160, 32));
        lblBarraEstado.setFont(FUENTE_SUBTITULO);
        lblBarraEstado.setForeground(COLOR_TEXTO);
        footer.add(lblBarraEstado, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    // =========================================================================
    // CONSTRUCCIÓN DE CADA SECCIÓN VISUAL (DELEGACIÓN A LIBRERIAFACADE)
    // =========================================================================

    private Panel crearPanelLibros() {
        Panel contenedor = new Panel(new BorderLayout(6, 6));
        contenedor.setBackground(COLOR_FONDO);

        // Parte superior: listado y barra de filtros (Consultas 1, 2, 3 y 4)
        Panel panelArriba = new Panel(new BorderLayout(4, 4));
        panelArriba.setBackground(COLOR_TARJETA);

        Panel barraFiltros = new Panel(new GridLayout(2, 1, 2, 2));
        barraFiltros.setBackground(COLOR_TARJETA);

        // Fila 1 de filtros: Consulta 1 (Ordenar por título), Consulta 2 (Por categoría) y Consulta 4 (Stock bajo - RF08)
        Panel filaFiltro1 = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        filaFiltro1.setBackground(COLOR_TARJETA);
        Button btnVerTodos = crearBotonAccion("Ordenar por Título", COLOR_BTN_ACTIVO);
        Button btnFiltrarCat = crearBotonAccion("Filtrar Categoría Seleccionada", COLOR_BTN_NAV);
        Button btnFiltrarStock = crearBotonAccion("Ver Stock Bajo", COLOR_SECUNDARIO);
        filaFiltro1.add(btnVerTodos);
        filaFiltro1.add(btnFiltrarCat);
        filaFiltro1.add(new Label(" | Stock <"));
        filaFiltro1.add(txtFiltroUmbralStock);
        filaFiltro1.add(btnFiltrarStock);

        // Fila 2 de filtros: Consulta 3 (Rango de precio)
        Panel filaFiltro2 = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        filaFiltro2.setBackground(COLOR_TARJETA);
        Button btnFiltrarPrecio = crearBotonAccion("Filtrar Rango de Precio", COLOR_BTN_NAV);
        filaFiltro2.add(crearEtiquetaSeccion("Filtro Precio:"));
        filaFiltro2.add(new Label("Min:"));
        filaFiltro2.add(txtFiltroMinPrecio);
        filaFiltro2.add(new Label("Max:"));
        filaFiltro2.add(txtFiltroMaxPrecio);
        filaFiltro2.add(btnFiltrarPrecio);

        barraFiltros.add(filaFiltro1);
        barraFiltros.add(filaFiltro2);

        listaLibrosUI.setFont(FUENTE_MONO);
        listaLibrosUI.addItemListener(e -> {
            String id = extraerIdSeleccionado(listaLibrosUI.getSelectedItem());
            if (id != null) {
                txtIdLibroAccion.setText(id);
                ejecutarEnFachada(() -> {
                    String[] datos = facade.obtenerDatosLibroPorId(id);
                    if (datos != null) {
                        txtIsbnLibro.setText(datos[1]);
                        txtTituloLibro.setText(datos[2]);
                        txtPrecioLibro.setText(datos[3]);
                        txtStockLibro.setText(datos[4]);
                        seleccionarItemEnChoicePorId(choiceCategoriaLibro, datos[5]);
                        seleccionarItemEnChoicePorId(choiceProveedorLibro, datos[6]);
                        txtIdsAutoresLibro.setText(datos[7]);
                        mostrarExito("Libro #" + id + " seleccionado en el formulario.");
                    }
                });
            }
        });

        panelArriba.add(barraFiltros, BorderLayout.NORTH);
        panelArriba.add(listaLibrosUI, BorderLayout.CENTER);

        // Parte inferior: Formulario único para Registrar, Actualizar (UPDATE 1, 2, 3) y Eliminar (RF10)
        Panel cardRegistro = new Panel(new GridLayout(2, 1, 2, 2));
        cardRegistro.setBackground(COLOR_TARJETA);

        Panel filaReg1 = new Panel(new FlowLayout(FlowLayout.LEFT, 5, 4));
        filaReg1.setBackground(COLOR_TARJETA);
        filaReg1.add(crearEtiquetaSeccion("Registrar Libro:"));
        filaReg1.add(new Label("ISBN:"));
        filaReg1.add(txtIsbnLibro);
        filaReg1.add(new Label("Título:"));
        filaReg1.add(txtTituloLibro);
        filaReg1.add(new Label("Precio:"));
        filaReg1.add(txtPrecioLibro);
        filaReg1.add(new Label("Stock:"));
        filaReg1.add(txtStockLibro);
        filaReg1.add(new Label("Autores:"));
        filaReg1.add(txtIdsAutoresLibro);

        Panel filaReg2 = new Panel(new FlowLayout(FlowLayout.LEFT, 5, 4));
        filaReg2.setBackground(COLOR_TARJETA);
        filaReg2.add(new Label("Cat:"));
        filaReg2.add(choiceCategoriaLibro);
        filaReg2.add(new Label("Prov:"));
        filaReg2.add(choiceProveedorLibro);
        Button btnGuardarLibro = crearBotonAccion("Registrar Libro", COLOR_PRIMARIO);
        Button btnActualizarLibro = crearBotonAccion("Actualizar Libro", COLOR_SECUNDARIO);
        Button btnEliminarLibroTop = crearBotonAccion("Eliminar Libro", COLOR_PELIGRO);
        Button btnLimpiarLibro = crearBotonAccion("Limpiar", COLOR_BTN_NAV);
        filaReg2.add(btnGuardarLibro);
        filaReg2.add(btnActualizarLibro);
        filaReg2.add(btnEliminarLibroTop);
        filaReg2.add(btnLimpiarLibro);

        cardRegistro.add(filaReg1);
        cardRegistro.add(filaReg2);

        // Delegación de eventos a LibreriaFacade
        btnVerTodos.addActionListener(e -> ejecutarEnFachada(() -> {
            cargarEnLista(listaLibrosUI, facade.obtenerLibrosFormateados());
            mostrarExito("Libros ordenados alfabéticamente por título.");
        }));

        btnFiltrarCat.addActionListener(e -> ejecutarEnFachada(() -> {
            String itemCat = choiceCategoriaLibro.getSelectedItem();
            cargarEnLista(listaLibrosUI, facade.filtrarLibrosPorCategoria(itemCat));
            mostrarExito("Libros filtrados por categoría " + itemCat);
        }));

        btnFiltrarPrecio.addActionListener(e -> ejecutarEnFachada(() -> {
            cargarEnLista(listaLibrosUI, facade.filtrarLibrosPorRangoPrecio(
                    txtFiltroMinPrecio.getText(), txtFiltroMaxPrecio.getText()));
            mostrarExito("Libros filtrados por rango de precio.");
        }));

        btnFiltrarStock.addActionListener(e -> ejecutarEnFachada(() -> {
            cargarEnLista(listaLibrosUI, facade.filtrarLibrosStockBajo(txtFiltroUmbralStock.getText()));
            mostrarExito("Mostrando libros con pocas unidades en inventario.");
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
            limpiarCamposLibro();
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));

        btnActualizarLibro.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.actualizarLibroCompleto(
                txtIdLibroAccion.getText(),
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

        btnEliminarLibroTop.addActionListener(e -> {
            String id = txtIdLibroAccion.getText().trim();
            if (id.isEmpty()) {
                mostrarError("Seleccione primero un libro de la lista para eliminarlo.");
                return;
            }
            if (confirmarAccion("¿Confirma que desea eliminar el libro seleccionado (#" + id + ")?")) {
                ejecutarEnFachada(() -> {
                    String msg = facade.eliminarLibro(id);
                    limpiarCamposLibro();
                    refrescarTodosLosDatos();
                    mostrarExito(msg);
                });
            }
        });

        btnLimpiarLibro.addActionListener(e -> {
            limpiarCamposLibro();
            mostrarExito("Cuadros de texto limpiados.");
        });

        contenedor.add(panelArriba, BorderLayout.CENTER);
        contenedor.add(cardRegistro, BorderLayout.SOUTH);
        return contenedor;
    }

    private Panel crearPanelVentas() {
        Panel contenedor = new Panel(new GridLayout(2, 1, 8, 8));
        contenedor.setBackground(COLOR_FONDO);

        // Mitad superior: Carrito y registro de venta (RF03 a RF06)
        Panel panelCaja = new Panel(new BorderLayout(4, 4));
        panelCaja.setBackground(COLOR_TARJETA);

        Panel formCaja = new Panel(new GridLayout(2, 1, 2, 2));
        formCaja.setBackground(COLOR_TARJETA);

        Panel filaCaja1 = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        filaCaja1.setBackground(COLOR_TARJETA);
        filaCaja1.add(crearEtiquetaSeccion("Nueva Venta:"));
        filaCaja1.add(new Label("Cliente:"));
        filaCaja1.add(choiceClienteVenta);
        filaCaja1.add(new Label("Libro:"));
        filaCaja1.add(choiceLibroVenta);
        filaCaja1.add(new Label("Cantidad:"));
        filaCaja1.add(txtCantidadVenta);

        Panel filaCaja2 = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        filaCaja2.setBackground(COLOR_TARJETA);
        filaCaja2.add(crearEtiquetaSeccion("Opciones:"));
        Button btnAgregarItem = crearBotonAccion("+ Agregar al Carrito", COLOR_BTN_ACTIVO);
        Button btnLimpiarCarrito = crearBotonAccion("Vaciar Carrito", COLOR_SECUNDARIO);
        Button btnConfirmarVenta = crearBotonAccion("Confirmar y Facturar Venta", COLOR_PRIMARIO);
        filaCaja2.add(btnAgregarItem);
        filaCaja2.add(btnLimpiarCarrito);
        filaCaja2.add(btnConfirmarVenta);

        formCaja.add(filaCaja1);
        formCaja.add(filaCaja2);

        listaCarritoUI.setFont(FUENTE_MONO);
        lblPoliticaActiva.setFont(FUENTE_SUBTITULO);
        lblPoliticaActiva.setForeground(COLOR_PRIMARIO);

        panelCaja.add(formCaja, BorderLayout.NORTH);
        panelCaja.add(listaCarritoUI, BorderLayout.CENTER);
        panelCaja.add(lblPoliticaActiva, BorderLayout.SOUTH);

        // Mitad inferior: Historial de ventas y filtros (RF07, Consultas 11 y 12)
        Panel panelHistorial = new Panel(new BorderLayout(4, 4));
        panelHistorial.setBackground(COLOR_TARJETA);

        Panel barraHistorial = new Panel(new GridLayout(2, 1, 2, 2));
        barraHistorial.setBackground(COLOR_TARJETA);

        Panel filaHist1 = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        filaHist1.setBackground(COLOR_TARJETA);
        filaHist1.add(crearEtiquetaSeccion("Historial de Ventas:"));
        Button btnTodasVentas = crearBotonAccion("Ver Todas las Ventas", COLOR_BTN_ACTIVO);
        Button btnPorCliente = crearBotonAccion("Filtrar por Cliente Seleccionado", COLOR_BTN_NAV);
        filaHist1.add(btnTodasVentas);
        filaHist1.add(btnPorCliente);

        Panel filaHist2 = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        filaHist2.setBackground(COLOR_TARJETA);
        filaHist2.add(crearEtiquetaSeccion("Filtro Fechas:"));
        filaHist2.add(new Label("Desde (YYYY-MM-DD):"));
        filaHist2.add(txtFechaDesde);
        filaHist2.add(new Label("Hasta:"));
        filaHist2.add(txtFechaHasta);
        Button btnPorFechas = crearBotonAccion("Filtrar por Fechas", COLOR_SECUNDARIO);
        filaHist2.add(btnPorFechas);

        barraHistorial.add(filaHist1);
        barraHistorial.add(filaHist2);

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
        Panel contenedor = new Panel(new BorderLayout(8, 8));
        contenedor.setBackground(COLOR_FONDO);

        listaClientesUI.setFont(FUENTE_MONO);
        listaClientesUI.addItemListener(e -> {
            String id = extraerIdSeleccionado(listaClientesUI.getSelectedItem());
            if (id != null) {
                txtIdCliente.setText(id);
                ejecutarEnFachada(() -> {
                    String[] datos = facade.obtenerDatosClientePorId(id);
                    if (datos != null) {
                        txtNombreCliente.setText(datos[1]);
                        txtDocCliente.setText(datos[2]);
                        txtTelCliente.setText(datos[3]);
                        txtCorreoCliente.setText(datos[4]);
                        mostrarExito("Cliente #" + id + " cargado en el formulario.");
                    }
                });
            }
        });

        // Formulario de Clientes (CRUD + UPDATE 4: Actualizar contacto)
        Panel formCliente = new Panel(new GridLayout(3, 1, 2, 2));
        formCliente.setBackground(COLOR_TARJETA);

        Panel filaCli1 = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        filaCli1.setBackground(COLOR_TARJETA);
        filaCli1.add(crearEtiquetaSeccion("Datos Cliente:"));
        filaCli1.add(new Label("ID:"));
        filaCli1.add(txtIdCliente);
        filaCli1.add(new Label("Nombre:"));
        filaCli1.add(txtNombreCliente);
        filaCli1.add(new Label("Documento:"));
        filaCli1.add(txtDocCliente);

        Panel filaCli2 = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        filaCli2.setBackground(COLOR_TARJETA);
        filaCli2.add(crearEtiquetaSeccion("Contacto Cliente:"));
        filaCli2.add(new Label("Teléfono:"));
        filaCli2.add(txtTelCliente);
        filaCli2.add(new Label("Correo:"));
        filaCli2.add(txtCorreoCliente);

        Panel filaCli3 = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaCli3.setBackground(COLOR_TARJETA);
        filaCli3.add(crearEtiquetaSeccion("Opciones:"));
        Button btnRegistrar = crearBotonAccion("Registrar Cliente", COLOR_PRIMARIO);
        Button btnActualizar = crearBotonAccion("Actualizar Cliente", COLOR_SECUNDARIO);
        Button btnEliminar = crearBotonAccion("Eliminar Cliente", COLOR_PELIGRO);
        Button btnLimpiar = crearBotonAccion("Limpiar Campos", COLOR_BTN_NAV);

        filaCli3.add(btnRegistrar);
        filaCli3.add(btnActualizar);
        filaCli3.add(btnEliminar);
        filaCli3.add(btnLimpiar);

        formCliente.add(filaCli1);
        formCliente.add(filaCli2);
        formCliente.add(filaCli3);

        btnRegistrar.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.registrarCliente(
                    txtNombreCliente.getText(), txtDocCliente.getText(),
                    txtTelCliente.getText(), txtCorreoCliente.getText());
            limpiarCamposCliente();
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));

        btnActualizar.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.actualizarClienteCompleto(
                    txtIdCliente.getText(), txtNombreCliente.getText(),
                    txtDocCliente.getText(), txtTelCliente.getText(), txtCorreoCliente.getText());
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));

        btnEliminar.addActionListener(e -> {
            String id = txtIdCliente.getText().trim();
            if (id.isEmpty()) {
                mostrarError("Seleccione un cliente de la lista o digite su ID para eliminarlo.");
                return;
            }
            if (confirmarAccion("¿Confirma que desea eliminar el cliente con ID " + id + "?")) {
                ejecutarEnFachada(() -> {
                    String msg = facade.eliminarCliente(id);
                    limpiarCamposCliente();
                    refrescarTodosLosDatos();
                    mostrarExito(msg);
                });
            }
        });

        btnLimpiar.addActionListener(e -> {
            limpiarCamposCliente();
            mostrarExito("Cuadros de texto de cliente limpiados.");
        });

        contenedor.add(listaClientesUI, BorderLayout.CENTER);
        contenedor.add(formCliente, BorderLayout.SOUTH);
        return contenedor;
    }

    private Panel crearPanelCatalogo() {
        Panel contenedor = new Panel(new GridLayout(3, 1, 6, 6));
        contenedor.setBackground(COLOR_FONDO);

        // 1. Subpanel Autores
        Panel pAutores = new Panel(new BorderLayout(4, 4));
        pAutores.setBackground(COLOR_TARJETA);
        listaAutoresUI.setFont(FUENTE_MONO);
        listaAutoresUI.addItemListener(e -> {
            String id = extraerIdSeleccionado(listaAutoresUI.getSelectedItem());
            if (id != null) {
                txtIdAutor.setText(id);
                ejecutarEnFachada(() -> {
                    String[] d = facade.obtenerDatosAutorPorId(id);
                    if (d != null) {
                        txtNombreAutor.setText(d[1]);
                        txtNacAutor.setText(d[2]);
                    }
                });
            }
        });

        Panel fAutor = new Panel(new FlowLayout(FlowLayout.LEFT, 5, 4));
        fAutor.setBackground(COLOR_TARJETA);
        fAutor.add(crearEtiquetaSeccion("Autores:"));
        fAutor.add(new Label("Nombre:"));
        fAutor.add(txtNombreAutor);
        fAutor.add(new Label("Nacionalidad:"));
        fAutor.add(txtNacAutor);
        Button btnCrearAutor = crearBotonAccion("Registrar Autor", COLOR_PRIMARIO);
        fAutor.add(btnCrearAutor);
        fAutor.add(new Label(" | ID:"));
        fAutor.add(txtIdAutor);
        Button btnActualizarAutor = crearBotonAccion("Actualizar", COLOR_SECUNDARIO);
        Button btnBorrarAutor = crearBotonAccion("Eliminar Autor", COLOR_PELIGRO);
        fAutor.add(btnActualizarAutor);
        fAutor.add(btnBorrarAutor);
        pAutores.add(listaAutoresUI, BorderLayout.CENTER);
        pAutores.add(fAutor, BorderLayout.SOUTH);

        btnCrearAutor.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.registrarAutor(txtNombreAutor.getText(), txtNacAutor.getText());
            txtIdAutor.setText("");
            txtNombreAutor.setText("");
            txtNacAutor.setText("");
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));
        btnActualizarAutor.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.actualizarAutor(txtIdAutor.getText(), txtNombreAutor.getText(), txtNacAutor.getText());
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));
        btnBorrarAutor.addActionListener(e -> {
            String id = txtIdAutor.getText().trim();
            if (confirmarAccion("¿Confirma eliminar el autor #" + id + "?")) {
                ejecutarEnFachada(() -> {
                    String msg = facade.eliminarAutor(id);
                    txtIdAutor.setText("");
                    txtNombreAutor.setText("");
                    txtNacAutor.setText("");
                    refrescarTodosLosDatos();
                    mostrarExito(msg);
                });
            }
        });

        // 2. Subpanel Categorías
        Panel pCategorias = new Panel(new BorderLayout(4, 4));
        pCategorias.setBackground(COLOR_TARJETA);
        listaCategoriasUI.setFont(FUENTE_MONO);
        listaCategoriasUI.addItemListener(e -> {
            String id = extraerIdSeleccionado(listaCategoriasUI.getSelectedItem());
            if (id != null) {
                txtIdCategoria.setText(id);
                ejecutarEnFachada(() -> {
                    String[] d = facade.obtenerDatosCategoriaPorId(id);
                    if (d != null) {
                        txtNombreCategoria.setText(d[1]);
                    }
                });
            }
        });

        Panel fCat = new Panel(new FlowLayout(FlowLayout.LEFT, 5, 4));
        fCat.setBackground(COLOR_TARJETA);
        fCat.add(crearEtiquetaSeccion("Categorías:"));
        fCat.add(new Label("Nombre Categoría:"));
        fCat.add(txtNombreCategoria);
        Button btnCrearCat = crearBotonAccion("Registrar Categoría", COLOR_PRIMARIO);
        fCat.add(btnCrearCat);
        fCat.add(new Label(" | ID:"));
        fCat.add(txtIdCategoria);
        Button btnActualizarCat = crearBotonAccion("Actualizar", COLOR_SECUNDARIO);
        Button btnBorrarCat = crearBotonAccion("Eliminar Categoría", COLOR_PELIGRO);
        fCat.add(btnActualizarCat);
        fCat.add(btnBorrarCat);
        pCategorias.add(listaCategoriasUI, BorderLayout.CENTER);
        pCategorias.add(fCat, BorderLayout.SOUTH);

        btnCrearCat.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.registrarCategoria(txtNombreCategoria.getText());
            txtIdCategoria.setText("");
            txtNombreCategoria.setText("");
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));
        btnActualizarCat.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.actualizarCategoria(txtIdCategoria.getText(), txtNombreCategoria.getText());
            refrescarTodosLosDatos();
            mostrarExito(msg);
        }));
        btnBorrarCat.addActionListener(e -> {
            String id = txtIdCategoria.getText().trim();
            if (confirmarAccion("¿Confirma eliminar la categoría #" + id + "?")) {
                ejecutarEnFachada(() -> {
                    String msg = facade.eliminarCategoria(id);
                    txtIdCategoria.setText("");
                    txtNombreCategoria.setText("");
                    refrescarTodosLosDatos();
                    mostrarExito(msg);
                });
            }
        });

        // 3. Subpanel Proveedores (incluye UPDATE 5: Actualizar proveedor)
        Panel pProveedores = new Panel(new BorderLayout(4, 4));
        pProveedores.setBackground(COLOR_TARJETA);
        listaProveedoresUI.setFont(FUENTE_MONO);
        listaProveedoresUI.addItemListener(e -> {
            String id = extraerIdSeleccionado(listaProveedoresUI.getSelectedItem());
            if (id != null) {
                txtIdProveedor.setText(id);
                ejecutarEnFachada(() -> {
                    String[] d = facade.obtenerDatosProveedorPorId(id);
                    if (d != null) {
                        txtNombreProveedor.setText(d[1]);
                        txtTelProveedor.setText(d[2]);
                        txtCorreoProveedor.setText(d[3]);
                    }
                });
            }
        });

        Panel fProv = new Panel(new GridLayout(2, 1, 2, 2));
        fProv.setBackground(COLOR_TARJETA);

        Panel filaProv1 = new Panel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        filaProv1.setBackground(COLOR_TARJETA);
        filaProv1.add(crearEtiquetaSeccion("Proveedores:"));
        filaProv1.add(new Label("ID:"));
        filaProv1.add(txtIdProveedor);
        filaProv1.add(new Label("Nombre:"));
        filaProv1.add(txtNombreProveedor);
        filaProv1.add(new Label("Teléfono:"));
        filaProv1.add(txtTelProveedor);
        filaProv1.add(new Label("Correo:"));
        filaProv1.add(txtCorreoProveedor);

        Panel filaProv2 = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        filaProv2.setBackground(COLOR_TARJETA);
        filaProv2.add(crearEtiquetaSeccion("Opciones:"));
        Button btnCrearProv = crearBotonAccion("Registrar Proveedor", COLOR_PRIMARIO);
        Button btnUpdateProv = crearBotonAccion("Actualizar Proveedor", COLOR_SECUNDARIO);
        Button btnBorrarProv = crearBotonAccion("Eliminar Proveedor", COLOR_PELIGRO);
        filaProv2.add(btnCrearProv);
        filaProv2.add(btnUpdateProv);
        filaProv2.add(btnBorrarProv);

        fProv.add(filaProv1);
        fProv.add(filaProv2);
        pProveedores.add(listaProveedoresUI, BorderLayout.CENTER);
        pProveedores.add(fProv, BorderLayout.SOUTH);

        btnCrearProv.addActionListener(e -> ejecutarEnFachada(() -> {
            String msg = facade.registrarProveedor(
                    txtNombreProveedor.getText(), txtTelProveedor.getText(), txtCorreoProveedor.getText());
            txtIdProveedor.setText("");
            txtNombreProveedor.setText("");
            txtTelProveedor.setText("");
            txtCorreoProveedor.setText("");
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
                    txtIdProveedor.setText("");
                    txtNombreProveedor.setText("");
                    txtTelProveedor.setText("");
                    txtCorreoProveedor.setText("");
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
        // Módulo 5: Estadísticas y datos (ejecuta internamente las Consultas analíticas del proyecto)
        Panel contenedor = new Panel(new BorderLayout(8, 8));
        contenedor.setBackground(COLOR_TARJETA);

        Panel barra = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        barra.setBackground(COLOR_TARJETA);
        barra.add(crearEtiquetaSeccion("Estadisticas y datos:"));
        barra.add(new Label("Mínimo de libros por categoría:"));
        barra.add(txtParametroHaving);
        Button btnEjecutar = crearBotonAccion("Actualizar Estadísticas", COLOR_PRIMARIO);
        barra.add(btnEjecutar);

        areaReportes.setEditable(false);
        areaReportes.setFont(FUENTE_MONO);
        areaReportes.setBackground(new Color(248, 250, 252));

        btnEjecutar.addActionListener(e -> ejecutarEnFachada(() -> {
            areaReportes.setText(facade.generarReporteCompletoConsultas(txtParametroHaving.getText()));
            mostrarExito("Estadísticas y datos actualizados.");
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
        Button btnActivarMemoria = crearBotonAccion("Usar Memoria Local", COLOR_SECUNDARIO);

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
            areaDiagnosticoBD.setText("Modo en memoria local activo.\n" + msg);
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
            cargarEnChoice(choiceProveedorLibro, facade.obtenerProveedoresCompactosParaChoice());
            cargarEnChoice(choiceClienteVenta, facade.obtenerClientesCompactosParaChoice());
            cargarEnChoice(choiceLibroVenta, facade.obtenerLibrosCompactosParaChoice());

            // Muestra la descripción de la política de descuento (Patrón Strategy) sin texto técnico
            lblPoliticaActiva.setText("  Promoción activa: "
                    + facade.obtenerDescripcionPoliticaDescuento());
            actualizarEtiquetaModo();
        });
    }

    private void limpiarCamposLibro() {
        txtIdLibroAccion.setText("");
        txtIsbnLibro.setText("");
        txtTituloLibro.setText("");
        txtPrecioLibro.setText("");
        txtStockLibro.setText("");
        txtIdsAutoresLibro.setText("1");
    }

    private void limpiarCamposCliente() {
        txtIdCliente.setText("");
        txtNombreCliente.setText("");
        txtDocCliente.setText("");
        txtTelCliente.setText("");
        txtCorreoCliente.setText("");
    }

    private String extraerIdSeleccionado(String item) {
        if (item != null && item.startsWith("[")) {
            int fin = item.indexOf(']');
            if (fin > 1) {
                return item.substring(1, fin).trim();
            }
        }
        return null;
    }

    private void seleccionarItemEnChoicePorId(Choice choice, String idBuscado) {
        if (idBuscado == null) return;
        String prefijo = "[" + idBuscado.trim() + "]";
        for (int i = 0; i < choice.getItemCount(); i++) {
            if (choice.getItem(i).startsWith(prefijo)) {
                choice.select(i);
                return;
            }
        }
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
            mostrarError("Ocurrió un error al procesar la solicitud: " + ex.getMessage());
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
        Dialog dlg = new Dialog(this, "Confirmar Operación", true);
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
