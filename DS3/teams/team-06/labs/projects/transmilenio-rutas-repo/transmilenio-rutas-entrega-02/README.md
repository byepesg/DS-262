# Gestor de Rutas TransMilenio · Calle 26, NQS y Autopista Norte

**Proyecto semestral, Entrega 2** · Estructuras de Datos 2026-2 · DS3 · `team-06`
Universidad Nacional de Colombia, Sede Bogotá · Docente: Sebastián Yepes García

Sistema en Java para planear viajes en **60 estaciones** de TransMilenio: la troncal **Calle 26** (Portal Eldorado a Universidades, Entrega 1) y el tramo **NQS + Autopista Norte de San Mateo a Terminal** (Entrega 2), con los **31 servicios** que paran en ellas según la guía oficial de TransMilenio (agosto 2026):

- Calle 26: K86-M86, K54-H54, K53-G53, K43-S43, K23-B23, K16-B16, K10-L10, 3-3, 1-1.
- NQS y Autopista (nuevos en E2): B12-G12, B11-G11, E42-S42, E48-S48, B46-S46, D22-G22 y S45 (solo un sentido). K16-B16, K43-S43 y K53-G53 ahora llegan hasta Terminal, San Mateo y Portal Sur.

Responde:

- ¿Qué rutas paran en una estación?
- ¿Qué recorrido hace una ruta (K16, B23, 1, ...)?
- ¿Qué rutas van **directo** entre dos estaciones?
- ¿Cuál es la ruta **más rápida**, la de **menos transbordos** y la de **menos paradas**?

Documento técnico: [`entrega-02.pdf`](entrega-02.pdf) (la versión anterior sigue en [`entrega-01.pdf`](entrega-01.pdf)).

## Qué cambió respecto a la Entrega 1

| Aspecto | Entrega 1 | Entrega 2 |
| --- | --- | --- |
| Troncales | Calle 26 | Calle 26, NQS y Autopista Norte |
| Estaciones / tramos | 14 / 13 | 60 / 61 (incluye empalmes Corferias - Av. El Dorado y C. Universitaria - CAD) |
| Servicios / arcos | 18 / 124 | 31 / 315 |
| Pruebas | 39 | 56 |
| Rendimiento | No se medía | `Rendimiento.java`: redes de 60 a 5000 estaciones |

Las estructuras de datos y los algoritmos son los mismos de la Entrega 1: la ampliación solo cambió los CSV.

## Integrantes

| Integrante | Documento | Correo | Usuario GitHub | Aporte principal |
| --- | --- | --- | --- | --- |
| Jeicob Stiven Murillo Ramos | 1000334659 | jemurillor@unal.edu.co | `JeicobMurill` | Modelo de grafos, `MinHeap`, Dijkstra por estados, cargador CSV |
| Andres Santiago Morales Robayo | 1011080401 | andmoralesro@unal.edu.co | `AndresMoris` | Datos de servicios, consultas de rutas, documento técnico |

Ambos integrantes revisaron el código completo y pueden explicar cualquier componente.

## Estaciones

| Id | Estación | Troncal | Servicios que paran |
| --- | --- | --- | --- |
| E01 | Portal Eldorado | Calle 26 | 16 |
| E02 | Modelia | Calle 26 | 10 |
| E03 | Normandía | Calle 26 | 6 |
| E04 | Avenida Rojas | Calle 26 | 12 |
| E05 | El Tiempo - Maloka | Calle 26 | 12 |
| E06 | Salitre - El Greco | Calle 26 | 12 |
| E07 | CAN | Calle 26 | 14 |
| E08 | Gobernación | Calle 26 | 10 |
| E09 | Quinta Paredes | Calle 26 | 8 |
| E10 | Corferias | Calle 26 | 12 |
| E11 | Ciudad Universitaria | Calle 26 | 12 |
| E12 | Concejo de Bogotá | Calle 26 | 10 |
| E13 | Centro Memoria | Calle 26 | 6 |
| E14 | Universidades | Calle 26 | 2 |
| E15 | San Mateo | NQS | 9 |
| E16 | Terreros | NQS | 9 |
| E17 | León XIII | NQS | 9 |
| E18 | La Despensa | NQS | 9 |
| E19 | Bosa | NQS | 7 |
| E20 | Portal Sur | NQS | 9 |
| E21 | Perdomo | NQS | 4 |
| E22 | Madelena | NQS | 4 |
| E23 | Sevillana | NQS | 4 |
| E24 | Venecia | NQS | 4 |
| E25 | Alquería | NQS | 4 |
| E26 | General Santander | NQS | 8 |
| E27 | NQS - Calle 38A Sur | NQS | 2 |
| E28 | NQS - Calle 30 Sur | NQS | 2 |
| E29 | SENA | NQS | 6 |
| E30 | Santa Isabel | NQS | 8 |
| E31 | Comuneros | NQS | 4 |
| E32 | Ricaurte | NQS | 8 |
| E33 | Paloquemao | NQS | 6 |
| E34 | CAD | NQS | 4 |
| E35 | Avenida El Dorado | NQS | 8 |
| E36 | Universidad Nacional | NQS | 8 |
| E37 | Campín - UAN | NQS | 4 |
| E38 | Movistar Arena | NQS | 0 |
| E39 | 7 de Agosto | NQS | 4 |
| E40 | Avenida Chile | NQS | 4 |
| E41 | NQS - Calle 75 | NQS | 4 |
| E42 | La Castellana | NQS | 2 |
| E43 | Escuela Militar | NQS - Héroes | 2 |
| E44 | Héroes | Autopista Norte | 2 |
| E45 | Calle 85 | Autopista Norte | 2 |
| E46 | Virrey | Autopista Norte | 2 |
| E47 | Calle 100 | Autopista Norte | 4 |
| E48 | Calle 106 | Autopista Norte | 2 |
| E49 | Pepe Sierra | Autopista Norte | 2 |
| E50 | Calle 127 | Autopista Norte | 2 |
| E51 | Prado | Autopista Norte | 2 |
| E52 | Alcalá | Autopista Norte | 4 |
| E53 | Calle 142 | Autopista Norte | 2 |
| E54 | Calle 146 | Autopista Norte | 2 |
| E55 | Mazurén | Autopista Norte | 2 |
| E56 | Calle 161 | Autopista Norte | 0 |
| E57 | Toberín | Autopista Norte | 8 |
| E58 | Portal del Norte | Autopista Norte | 4 |
| E59 | Calle 187 | Autopista Norte | 4 |
| E60 | Terminal | Autopista Norte | 4 |

Movistar Arena y Calle 161 están en la vía pero ninguna ruta del alcance para en ellas.

## Requisitos

- Java **17 o superior** (probado con OpenJDK 21). Sin dependencias externas.

## Compilar y ejecutar

Desde esta carpeta (`transmilenio-rutas/`):

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

En Windows (cmd) los comandos son los mismos, usando `src\*.java`.

### Modos de uso

| Comando | Qué hace |
| --- | --- |
| `java -cp out Main` | Menú interactivo (9 opciones) |
| `java -cp out Main demo` | Consultas de ejemplo de todas las funciones |
| `java -cp out Main estacion CAN` | Rutas que paran en una estación |
| `java -cp out Main servicio K43` | Recorrido de una ruta en la red |
| `java -cp out Main ruta Normandia "Concejo de Bogota"` | Ruta más rápida |
| `java -cp out Main transbordos Normandia "Concejo de Bogota"` | Ruta con menos transbordos |
| `java -cp out Main paradas Normandia "Concejo de Bogota"` | Ruta con menos paradas |
| `java -cp out Main ruta "San Mateo" Terminal` | Viaje entre troncales |
| `java -cp out Rendimiento` | Tiempos de consulta con redes de distinto tamaño |

Las estaciones se escriben por **id** (`E07`), **nombre** o **alias** (`Maloka`, `CityU`, `Consejo de Bogota`), sin importar tildes ni mayúsculas.

### Ejemplo de salida

```text
RUTA MÁS RÁPIDA: San Mateo -> Terminal
Tiempo total: 82 min 00 s (en bus 78 min 00 s + 1 transbordo(s)) | Paradas: 9
  1. Tome B46  en San Mateo              y bájese en Toberín (7 paradas)
  2. Tome B16  en Toberín                y bájese en Terminal (2 paradas)
  Recorrido: San Mateo > Terreros > León XIII > La Despensa > Bosa > Calle 100 > Alcalá > Toberín > Calle 187 > Terminal
```

## Pruebas

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Pruebas
```

Resultado esperado: `Resultado: 56 aprobadas, 0 fallidas`. Cubren:

- `MinHeap`: vacío, un elemento, 1 000 valores aleatorios ordenados.
- Construcción: rechazo de duplicados, tramos inválidos, paradas repetidas y servicios sin vía física (sin dejar arcos a medias); tiempo de arcos expresos.
- Datos reales: rutas que paran en CAN (14, la ruta 1 cuenta ida y regreso) y en Universidades (solo la 1); rutas directas en ambos sentidos; alias.
- Rutas: red pequeña donde cada criterio da una respuesta distinta, origen = destino, estación aislada, sentido de los servicios.
- **Dijkstra por estados = Bellman-Ford** en los 3600 pares × 3 penalizaciones (10 800 consultas), y coherencia entre los tres criterios en todos los pares.
- Ampliación (E2): alias nuevos, empalmes entre troncales, viajes San Mateo - Terminal y Universidades - San Mateo, S45 en un solo sentido, estación sin servicios, expreso B46 y una red sintética de 1000 estaciones.

## Rendimiento

```bash
java -cp out Rendimiento
```

Promedio de 300 consultas aleatorias (mejor de 5 rondas, OpenJDK 21, Xeon 2,1 GHz):

| Red | Estaciones | Arcos | Más rápida (ms) | Menos transbordos (ms) | BFS (ms) |
| --- | --- | --- | --- | --- | --- |
| Real | 60 | 315 | 0,104 | 0,060 | 0,007 |
| 4×50 | 200 | 774 | 0,141 | 0,127 | 0,015 |
| 10×100 | 1000 | 4040 | 0,975 | 1,025 | 0,073 |
| 25×200 | 5000 | 20 600 | 6,846 | 7,629 | 0,579 |

Los tiempos cambian un poco en cada equipo.

## Estructura

```text
transmilenio-rutas/
├── README.md
├── entrega-01.pdf          # Documento técnico (Entrega 1)
├── entrega-02.pdf          # Documento técnico (Entrega 2)
├── data/
│   ├── estaciones.csv      # id,nombre,alias (separados por |)
│   ├── tramos.csv          # origen,destino,segundos (vía física)
│   └── servicios.csv       # codigo,origen,destino,paradas (ids en orden, separados por |)
└── src/
    ├── Main.java               # Menú y modos por argumentos
    ├── CargadorDatos.java      # Lectura de los CSV
    ├── RedTransmilenio.java    # Vía física + red de rutas + índices
    ├── CalculadorRutas.java    # Dijkstra y BFS
    ├── MinHeap.java            # Montículo de mínimos
    ├── Estacion.java, Conexion.java, Servicio.java, Ruta.java
    ├── Pruebas.java            # 56 pruebas
    └── Rendimiento.java        # Medición de tiempos
```

## Modelo

- **Vía física:** grafo no dirigido, 61 tramos con tiempo estimado (90 a 150 s).
- **Red de servicios:** multigrafo dirigido. Por cada servicio y par de paradas consecutivas hay un arco con tiempo = camino mínimo en la vía física + 30 s de detención. 315 arcos. Así se calcula el tiempo de los expresos.
- **Transbordos:** Dijkstra busca sobre estados *(estación, servicio en que se llegó)*. Cambiar de servicio suma una penalización `P`: `P = 240 s` da la ruta más rápida; `P = 10^6` da la de menos transbordos.

## Estructuras de datos y complejidades

| Operación | Estructura | Costo |
| --- | --- | --- |
| Agregar tramo / arco | `HashMap` + listas de adyacencia | O(1) promedio |
| Arcos salientes de v | Lista de adyacencia | O(grado(v)) |
| Rutas que paran en v | `HashMap<String, TreeSet<String>>` | O(1) + O(k) |
| Rutas directas a → b | Índice parada→posición (`HashMap`) en cada servicio | O(k) |
| Buscar por nombre / alias | `HashMap` de nombres normalizados | O(L) promedio |
| Buscar por fragmento | Recorrido lineal | O(V·L) |
| Insertar / extraer mínimo | `MinHeap` propio | O(log n) |
| Ruta más rápida / menos transbordos | Dijkstra por estados + `MinHeap` | O((X + A') log X) |
| Ruta con menos paradas | BFS + `ArrayDeque` | O(V + A) |
| Reconstruir ruta | Pila (`ArrayDeque`) | O(k) |

k: rutas de la estación o paradas de la ruta; L: longitud del texto; X ≤ V + P estados (P = total de paradas de todos los servicios); A': arcos explorados.

## Estado de la entrega (aprox. 70 % del proyecto)

| Funcionalidad | Estado |
| --- | --- |
| Carga y validación de estaciones, tramos y servicios | Implementado |
| Rutas que paran en una estación / recorrido de una ruta | Implementado |
| Rutas directas entre dos estaciones | Implementado |
| Ruta más rápida y con menos transbordos (Dijkstra por estados) | Implementado |
| Ruta con menos paradas (BFS) | Implementado |
| Búsqueda por nombre, alias o id sin tildes | Implementado |
| Sugerencias por fragmento | Parcial (lineal, se migrará a trie) |
| Ampliación a NQS y Autopista Norte | Implementado (Entrega 2) |
| Medición de rendimiento | Implementado (Entrega 2) |
| Horarios de operación de cada ruta | Pendiente (entrega final) |
| Autocompletado con trie | Pendiente (entrega final) |
| Otras troncales (Caracas, Calle 80, Américas) | Fuera del alcance |

## Limitaciones conocidas

- Solo se modelan la Calle 26 y la NQS + Autopista Norte (San Mateo - Terminal). De las rutas que salen de la red (K23-B23 por la Caracas, D22-G22 hacia el Portal 80, etc.) solo se guardan las paradas dentro de ella.
- Escuela Militar se modela como el punto que une NQS - Calle 75 con Héroes (por donde pasan B11 y G11).
- Servicios y paradas: guía oficial de agosto de 2026. Se excluyen variantes de domingos y festivos (ciclovía).
- Tiempos por tramo, detención (30 s) y espera por transbordo (240 s) son **estimaciones** del equipo.
- No se consideran horarios: algunas rutas (por ejemplo K53 y G53) solo operan en ciertas franjas.
- En consolas Windows antiguas las tildes pueden verse mal; escribir sin tilde funciona igual.

## Nota

Se usó un asistente de IA como apoyo en la programación; el equipo revisó y validó el código.

## Referencias

- TransMilenio S.A., *Guía general de viaje de TransMilenio, corte a agosto de 2026*, https://www.transmilenio.gov.co/
- T. H. Cormen et al., *Introduction to Algorithms*, 4th ed., MIT Press, 2022.
- M. T. Goodrich, R. Tamassia, M. H. Goldwasser, *Data Structures and Algorithms in Java*, 6th ed., Wiley, 2014.
- H. Bast et al., "Route planning in transportation networks", *Algorithm Engineering*, Springer, 2016.
- D. Delling, T. Pajor, R. F. Werneck, "Round-based public transit routing", *Transportation Science*, 2015.
- J. Dibbelt et al., "Connection scan algorithm", *ACM J. Exp. Algorithmics*, 2018.
- GTFS Reference: https://gtfs.org/
- Wikipedia, "List of TransMilenio stations" (orden de las estaciones): https://en.wikipedia.org/wiki/List_of_TransMilenio_stations
- Documentación de Java SE 21: https://docs.oracle.com/en/java/javase/21/docs/api/
