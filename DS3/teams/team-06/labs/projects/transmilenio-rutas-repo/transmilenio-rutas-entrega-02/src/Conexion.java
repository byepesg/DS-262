public final class Conexion {
    private final String destino;
    private final int segundos;
    private final String servicio;

    public Conexion(String destino, int segundos, String servicio) {
        if (segundos <= 0) {
            throw new IllegalArgumentException("El tiempo de una conexión debe ser positivo: " + segundos);
        }
        this.destino = destino;
        this.segundos = segundos;
        this.servicio = servicio;
    }

    public String getDestino() {
        return destino;
    }

    public int getSegundos() {
        return segundos;
    }

    public String getServicio() {
        return servicio;
    }

    @Override
    public String toString() {
        return "-> " + destino + " (" + segundos + " s" + (servicio == null ? "" : ", " + servicio) + ")";
    }
}
