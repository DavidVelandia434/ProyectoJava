/*
 * Programa : Sistema de gestión de librería
 * Archivo  : MenuReportes.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.ui;

import java.util.Map;
import libreria.dao.PersistenciaException;
import libreria.service.ReglaNegocioException;
import libreria.service.ReporteService;

public class MenuReportes {
    private final ConsolaUtil c;
    private final ReporteService reportes;

    public MenuReportes(ConsolaUtil c, ReporteService reportes) {
        this.c = c;
        this.reportes = reportes;
    }

    public void ejecutar() {
        int op;
        do {
            ConsolaUtil.titulo("REPORTES Y CONSULTAS");
            System.out.println("1. Libros más vendidos\n2. Número de ventas por cliente\n"
                    + "3. Total vendido por día\n4. Total vendido por mes\n5. Libros por categoría\n"
                    + "6. Categorías con más de N libros\n7. Precio promedio por categoría\n"
                    + "8. Libro más caro y más barato\n9. Libros nunca vendidos\n"
                    + "10. Libros sobre el promedio de ventas\n0. Volver");
            op = c.leerEntero("Opción:");
            try {
                switch (op) {
                    case 1: imprimirMapa(reportes.librosMasVendidos(c.leerEntero("¿Cuántos mostrar?")), "unidades"); break;
                    case 2: imprimirMapa(reportes.numeroVentasPorCliente(), "ventas"); break;
                    case 3: imprimirMapa(reportes.totalVendidoPorDia(), ""); break;
                    case 4: imprimirMapa(reportes.totalVendidoPorMes(), ""); break;
                    case 5: imprimirMapa(reportes.cantidadLibrosPorCategoria(), "libros"); break;
                    case 6: imprimirMapa(reportes.categoriasConMasDe(c.leerEntero("N:")), "libros"); break;
                    case 7: imprimirMapa(reportes.precioPromedioPorCategoria(), ""); break;
                    case 8:
                        reportes.libroMasCaro().ifPresent(l -> System.out.println("Más caro: " + l));
                        reportes.libroMasBarato().ifPresent(l -> System.out.println("Más barato: " + l));
                        break;
                    case 9: ConsolaUtil.imprimirLista(reportes.librosNuncaVendidos()); break;
                    case 10:
                        System.out.printf("Promedio de unidades por libro vendido: %.2f%n",
                                reportes.promedioUnidadesPorLibroVendido());
                        imprimirMapa(reportes.librosSobrePromedioDeVentas(), "unidades");
                        break;
                    case 0: break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (ReglaNegocioException | PersistenciaException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (op != 0);
    }

    private <K, V> void imprimirMapa(Map<K, V> mapa, String unidad) {
        if (mapa.isEmpty()) {
            System.out.println("(sin resultados)");
            return;
        }
        mapa.forEach((k, v) -> System.out.println("  " + k + " -> " + v + (unidad.isEmpty() ? "" : " " + unidad)));
    }
}
