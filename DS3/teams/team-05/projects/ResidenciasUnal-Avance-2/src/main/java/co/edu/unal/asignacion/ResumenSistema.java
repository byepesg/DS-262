package co.edu.unal.asignacion;

public record ResumenSistema(int totalSolicitudes, int cuposConfigurados,
        int asignados, int enEspera, int cuposLibres, double puntajePromedio) { }