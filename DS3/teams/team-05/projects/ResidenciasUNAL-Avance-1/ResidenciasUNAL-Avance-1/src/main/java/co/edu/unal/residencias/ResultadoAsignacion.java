package co.edu.unal.residencias;

import java.util.List;

public record ResultadoAsignacion(
        int cuposConfigurados,
        List<Estudiante> beneficiarios,
        List<Estudiante> noBeneficiarios) {

    public ResultadoAsignacion {
        beneficiarios = List.copyOf(beneficiarios);
        noBeneficiarios = List.copyOf(noBeneficiarios);
    }
}
