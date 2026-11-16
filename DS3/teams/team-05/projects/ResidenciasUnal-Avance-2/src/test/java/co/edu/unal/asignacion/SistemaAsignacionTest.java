package co.edu.unal.asignacion;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

class SistemaAsignacionTest {
    private record Beca(int codigo, int merito) { }

    private static final class MotorBecas extends SistemaAsignacion<Beca> {
        MotorBecas() {
            super(Comparator.comparingInt(Beca::merito).reversed().thenComparingInt(Beca::codigo));
        }
        @Override protected int obtenerId(Beca beca) { return beca.codigo(); }
        @Override protected Beca copiarSolicitud(Beca beca) { return new Beca(beca.codigo(), beca.merito()); }
        
        void registrar(int id, int merito) { registrarSolicitud(new Beca(id, merito)); }
        List<Integer> orden() { return listarSolicitudesOrdenadas().stream().map(Beca::codigo).toList(); }
        
        List<Integer> asignar(int cupos) {
            configurarCupos(cupos);
            return distribuirCupos().asignados().stream().map(Beca::codigo).toList();
        }
    }

    @Test
    void motorFuncionaConOtroTipoDeSolicitudYOtraRegla() {
        MotorBecas motor = new MotorBecas();
        motor.registrar(3, 80);
        motor.registrar(1, 90);
        motor.registrar(2, 90);
        assertEquals(List.of(1, 2, 3), motor.orden());
        assertEquals(List.of(1, 2), motor.asignar(2));
    }
}