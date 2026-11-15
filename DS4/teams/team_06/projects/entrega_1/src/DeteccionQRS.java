public class DeteccionQRS {
    // varibles para el algoritmo hastas la ventana movil
    double fs;  // frecuencia de muestreo

    // memorias para cada paso
    CircularQueue x_pb;
    CircularQueue y_pb;
    CircularQueue x_pa;
    CircularQueue y_pa;
    CircularQueue x_der;
    CircularQueue x_int;
    
    double suma_int;
    int window_size;

    boolean tieneOffset = false;
    double offset = 0;

    // variables para la decision

    double spki = 0;
    double npki = 0;
    double theresholdI = 0;
    double theresholdII = 0;

    //  estado de la decisión
    double prev1 = 0;
    double prev2 = 0;
    int n = 0;            // indice de la muestra actual
    int ultimoQRS = -1;   // indice del ultimo QRS aceptado
    int totalLatidos = 0;

    // intervalos RR (en muestras)
    CircularQueue rr1;     // ultimos 8 RR, sin filtrar
    CircularQueue rr2;     // ultimos 8 RR dentro de limites
    int rr1Count = 0;
    int rr2Count = 0;
    int rr2Rechazos = 0;

    // inicializacion de 2 s
    int muestrasInicializacion;
    double maxInicializacion = 0, sumaInicializacion = 0;
    boolean inicializado = false;

    // pendientes (para descartar onda T)
    double pendMax = 0;
    double pendQRS = 0;

    // candidato para searchback
    double candPico = 0, candPend = 0;
    int candIdx = -1;

    CircularLinkedList latidos;

    public DeteccionQRS(double fs) {
        this.fs = fs;
        this.window_size = (int) Math.round(0.15 * fs); // Ventana de 150 ms

        this.x_pb = new CircularQueue(13); // x[n] hasta x[n-12]
        this.y_pb = new CircularQueue(3);  // guarda las ultimas salidas (solo se usan y[n-1] y y[n-2])
        this.x_pa = new CircularQueue(33); // x[n] hasta x[n-32]
        this.y_pa = new CircularQueue(2);  // guarda y[n-2], y[n-1]
        this.x_der = new CircularQueue(5); // x[n] hasta x[n-4]
        this.x_int = new CircularQueue(this.window_size);
        this.suma_int = 0;

        prellenar(x_pb, 13);
        prellenar(y_pb, 3);
        prellenar(x_pa, 33);
        prellenar(y_pa, 2);
        prellenar(x_der, 5);
        prellenar(x_int, window_size);

        this.muestrasInicializacion = (int) Math.round(2 * fs);
        this.rr1 = new CircularQueue(8);
        this.rr2 = new CircularQueue(8);
        prellenar(rr1, 8);
        prellenar(rr2, 8);

        latidos = new CircularLinkedList();
    }

    private void prellenar(CircularQueue queue, int capacity) {
        for (int i = 0; i < capacity; i++) {
            queue.enqueue(0.0);
        }
    }

    // 1. pasa bajos
    public double pasaBajos(double x) {
        x_pb.slide(x);
        
        // El la posicion logica 12 es el posicion actual, 6 es la mitad, 0 es el mas viejo
        double y = 2 * y_pb.get(2) - y_pb.get(1) + x_pb.get(12) - 2 * x_pb.get(6) + x_pb.get(0);

        y_pb.slide(y);
        return y;
    }

    // 2. pasa altos
    public double pasaAltos(double x) {
        x_pa.slide(x);
        
        double y = y_pa.get(1) - (x_pa.get(32)/32.0) + x_pa.get(16) - x_pa.get(15) + (x_pa.get(0)/32.0);

        y_pa.slide(y);
        return y;
    }

    // 3. derivada 
    public double derivada(double x) {
        x_der.slide(x);

        double y = 0.125*(2*x_der.get(4) + x_der.get(3) - x_der.get(1) - 2*x_der.get(0));
        return y;
    }

    // 4. elevar al cuadrado
    public double cuadrado(double x) {
        return x*x;
    }

    // 5. ventana movil
    public double integracionVentana(double x) {
        double ultimoDato = x_int.get(0);
        suma_int = suma_int  + x - ultimoDato; // sumo lo que entra y resto lo que sale
        x_int.slide(x);
        return suma_int/ window_size;
    }

    public double procesarMuestra(double m) {
        if (!tieneOffset) {
            offset = m;
            tieneOffset = true;
        }
        m = m - offset;

        double pb = pasaBajos(m);
        double pa = pasaAltos(pb);
        double derivar = derivada(pa);
        double elevarCuad = cuadrado(derivar);
        double ventana = integracionVentana(elevarCuad);
        
        return ventana;
    }


    // 6. decision
    public void decidir(double v) {
        pendMax = Math.max(pendMax, v - prev1);

        if (!inicializado) { // para los primeros 2s
            maxInicializacion = Math.max(maxInicializacion, v);
            sumaInicializacion += v;
            if (n + 1 >= muestrasInicializacion) {
                spki = 0.25 * maxInicializacion;
                npki = 0.5 * (sumaInicializacion / muestrasInicializacion);
                actualizarUmbrales();
                pendMax = 0;
                inicializado = true;
            }
        } 
        else {
            if (prev1 > prev2 && prev1 >= v) {   // pico en n-1
                clasificar(prev1, n - 1);
                pendMax = 0;
            }
            searchback();
        }

        prev2 = prev1;
        prev1 = v;
        n++;
    }

    private void actualizarUmbrales() {
        theresholdI  = npki + 0.25 * (spki - npki);
        theresholdII = 0.5 * theresholdI;
    }

    private double promedio(CircularQueue q, int c) {
        if (c == 0) return 0;

        double s = 0;
        for (int i = 8 - c; i < 8; i++) {
            s += q.get(i);   // los ultimos count
        }
        return s/c;
    }

    // si los 8 RR de rr1 estan dentro de limites, el ritmo es regular y rrProm2 = rrProm1
    private boolean ritmoRegular() {
        if (rr1Count < 8 || rr2Count == 0) return false;

        double rrProm2 = promedio(rr2, rr2Count);
        
        for (int i = 0; i < 8; i++) {
            double v = rr1.get(i);

            if (v < 0.92 * rrProm2 || v > 1.16 * rrProm2){
                return false;
            } 
        }

        return true;
    }

    private double rrReferencia() {   // RR prom 2
        if (ritmoRegular()) {
            return promedio(rr1, rr1Count);
        }
        return promedio(rr2, rr2Count);
    }

    private void registrarQRS(int idx, double pend) {
        if (ultimoQRS >= 0) {
            double rrNuevo = idx - ultimoQRS;
            double rrProm2 = promedio(rr2, rr2Count);

            // AVERAGE1: siempre entra
            rr1.slide(rrNuevo);
            if (rr1Count < 8) rr1Count++;

            //pasa si esta entre 92% y 116% de RRprom2
            boolean pasa = (rr2Count == 0 || (rrNuevo >= 0.92 * rrProm2 && rrNuevo <= 1.16 * rrProm2));
            if (pasa) {
                rr2Rechazos = 0;
            } 
            else if (++rr2Rechazos >= 4) {  
                rr2Count = 0;
                rr2Rechazos = 0;
                pasa = true;
            }
            
            if (pasa) {
                rr2.slide(rrNuevo);
                if (rr2Count < 8) {
                    rr2Count++;
                }
            }
        }

        ultimoQRS = idx;
        pendQRS = pend;
        totalLatidos++;
        candPico = 0;
        candIdx = -1;

        latidos.pushBack(idx);
    }

    // RR MISSED LIMIT = 166% RR AVERAGE2, evaluado en cada muestra
    private void searchback() {
        if (candIdx >= 0 && rr2Count > 0 && (n - ultimoQRS) > 1.66 * rrReferencia()) {
            spki = 0.25 * candPico + 0.75 * spki;
            registrarQRS(candIdx, candPend);
            actualizarUmbrales();
        }
    }

    private void clasificar(double pico, int idx) {
        int periodoRefactor = (int) Math.round(0.200 * fs); // es para evitar contar el mismo latido
        int ventanaT = (int) Math.round(0.360 * fs);
        int desde = idx - ultimoQRS;

        if (ultimoQRS >= 0 && desde < periodoRefactor) { // revisar si es el mismo latido
            return;
        }

        if (pico > theresholdI) { // si es mayor al thereshold 1
            boolean ondaT = ultimoQRS >= 0 && desde < ventanaT && pendMax < 0.5 * pendQRS; // verifico si es una onda T
            
            if (ondaT) {
                npki = 0.125 * pico + 0.875 * npki; // actualizo el ruido
            } 
            else {
                spki = 0.125 * pico + 0.875 * spki; // actualizo el signal peak
                registrarQRS(idx, pendMax);
            }
        } 
        else {
            npki = 0.125 * pico + 0.875 * npki;
            if (pico > theresholdII && pico > candPico) {   // candidato para searchback
                candPico = pico;
                candIdx = idx;
                candPend = pendMax;
            }
        }

        actualizarUmbrales();
    }
}