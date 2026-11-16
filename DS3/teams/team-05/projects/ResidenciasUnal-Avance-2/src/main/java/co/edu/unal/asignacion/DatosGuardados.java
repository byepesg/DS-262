package co.edu.unal.asignacion;
import java.util.List;

public record DatosGuardados(int cupos, List<Candidato> candidatos) {
    public DatosGuardados {
        if (cupos < 0) throw new IllegalArgumentException("Los cupos no pueden ser negativos.");
        candidatos = candidatos.stream().map(Candidato::copiar).toList();
    }
}