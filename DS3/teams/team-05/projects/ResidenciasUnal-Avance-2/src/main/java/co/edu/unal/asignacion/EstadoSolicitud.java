package co.edu.unal.asignacion;

public record EstadoSolicitud(Candidato candidato, int posicion, boolean asignado) {
    public EstadoSolicitud { candidato = candidato.copiar(); }
    public String descripcion() { return asignado ? "ASIGNADO" : "EN ESPERA"; }
}