import java.util.List;

public final class Ruta {
    public static final int ESPERA_TRANSBORDO_SEG = 240;

    private final List<Estacion> paradas;
    private final List<String> servicioPorTramo;
    private final int segundosEnVehiculo;

    public Ruta(List<Estacion> paradas, List<String> servicioPorTramo, int segundosEnVehiculo) {
        if (paradas.size() != servicioPorTramo.size() + 1) {
            throw new IllegalArgumentException("Debe haber un servicio por cada tramo");
        }
        this.paradas = List.copyOf(paradas);
        this.servicioPorTramo = List.copyOf(servicioPorTramo);
        this.segundosEnVehiculo = segundosEnVehiculo;
    }

    public List<Estacion> getParadas() {
        return paradas;
    }

    public List<String> getServicioPorTramo() {
        return servicioPorTramo;
    }

    public int getSegundosEnVehiculo() {
        return segundosEnVehiculo;
    }

    public int getNumeroParadas() {
        return servicioPorTramo.size();
    }

    // cuenta los cambios de ruta
    public int getTransbordos() {
        int cambios = 0;
        for (int i = 1; i < servicioPorTramo.size(); i++) {
            if (!servicioPorTramo.get(i).equals(servicioPorTramo.get(i - 1))) cambios++;
        }
        return cambios;
    }

    public int getSegundosTotales() {
        return segundosEnVehiculo + getTransbordos() * ESPERA_TRANSBORDO_SEG;
    }

    public static String formatear(int segundos) {
        return String.format("%d min %02d s", segundos / 60, segundos % 60);
    }

    public String describir() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Tiempo total: %s (en bus %s + %d transbordo(s)) | Paradas: %d%n",
                formatear(getSegundosTotales()), formatear(segundosEnVehiculo),
                getTransbordos(), getNumeroParadas()));
        if (servicioPorTramo.isEmpty()) {
            sb.append("  Origen y destino son la misma estación: ").append(paradas.get(0).getNombre());
            return sb.toString();
        }
        int inicio = 0;
        int paso = 1;
        for (int i = 1; i <= servicioPorTramo.size(); i++) {
            boolean fin = i == servicioPorTramo.size()
                    || !servicioPorTramo.get(i).equals(servicioPorTramo.get(i - 1));
            if (fin) {
                sb.append(String.format("  %d. Tome %-4s en %-22s y bájese en %s (%d parada%s)%n",
                        paso++, Servicio.codigoDe(servicioPorTramo.get(inicio)),
                        paradas.get(inicio).getNombre(),
                        paradas.get(i).getNombre(),
                        i - inicio, i - inicio == 1 ? "" : "s"));
                inicio = i;
            }
        }
        sb.append("  Recorrido: ");
        for (int i = 0; i < paradas.size(); i++) {
            if (i > 0) sb.append(" > ");
            sb.append(paradas.get(i).getNombre());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return describir();
    }
}
