package datosObjetos;

import mundo.roles.*;

/**
 * Fábrica de objetos para la creación de los 7 roles disponibles en Pandemic.
 */
public class Roles {

    public static Rol[] crearRoles() {
        return new Rol[]{
            new PlanificadorContingencias(),
            new Despachador(),
            new Medico(),
            new ExpertoOperaciones(),
            new EspecialistaCuarentena(),
            new Investigadora(),
            new Cientifica()
        };
    }
}