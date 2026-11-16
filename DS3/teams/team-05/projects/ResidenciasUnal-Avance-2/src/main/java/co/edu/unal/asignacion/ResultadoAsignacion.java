package co.edu.unal.asignacion;
import java.util.List;

public record ResultadoAsignacion(
        int cuposConfigurados,
        List<Candidato> asignados,
        List<Candidato> enEspera) {

    public ResultadoAsignacion {
        if (cuposConfigurados < 0) throw new IllegalArgumentException("Los cupos no pueden ser negativos.");
        asignados = asignados.stream().map(Candidato::copiar).toList();
        enEspera = enEspera.stream().map(Candidato::copiar).toList();
    }
}