package libreria.service;

public enum ResultadoEliminacion {
    ELIMINADO("El registro fue eliminado correctamente."),
    NO_ENCONTRADO("No se encontró ningún registro con ese identificador."),
    RECHAZADO_POR_RESTRICCION("No se puede eliminar: el registro tiene información relacionada.");

    private final String mensaje;

    ResultadoEliminacion(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
