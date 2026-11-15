# Gestor de Rutas TransMilenio · Troncal Calle 26

**Proyecto semestral, Entrega 1** · Estructuras de Datos 2026-2 · DS3 · `team-06`
Universidad Nacional de Colombia, Sede Bogotá · Docente: Sebastián Yepes García

Sistema en Java para planear viajes en las **14 estaciones de la troncal Calle 26** (Portal Eldorado a Universidades), con las **18 rutas** que las recorren solamente por el carril exclusivo (K86-M86, K54-H54, K53-G53, K43-S43, K23-B23, K16-B16, K10-L10, 3-3, 1-1), según la guía oficial de TransMilenio (agosto 2026).

Responde:

- ¿Qué rutas paran en una estación?
- ¿Qué recorrido hace una ruta (K16, B23, 1, ...)?
- ¿Qué rutas van **directo** entre dos estaciones?
- ¿Cuál es la ruta **más rápida**, la de **menos transbordos** y la de **menos paradas**?


## Integrantes

| Integrante | Documento | Correo | Usuario GitHub | Aporte principal |
| --- | --- | --- | --- | --- |
| Jeicob Stiven Murillo Ramos | 1000334659 | jemurillor@unal.edu.co | `JeicobMurill` | Modelo de grafos, `MinHeap`, Dijkstra por estados, cargador CSV |
| Andres Santiago Morales Robayo | 1011080401 | andmoralesro@unal.edu.co | `AndresMoris` | Datos de servicios, consultas de rutas, documento técnico |


## Estaciones del tramo

| Id | Estación | Rutas que paran |
| --- | --- | --- |
| E01 | Portal Eldorado | 16 |
| E02 | Modelia | 10 |
| E03 | Normandía | 6 |
| E04 | Avenida Rojas | 12 |
| E05 | El Tiempo - Maloka | 12 |
| E06 | Salitre - El Greco | 12 |
| E07 | CAN | 14 |
| E08 | Gobernación | 10 |
| E09 | Quinta Paredes | 8 |
| E10 | Corferias | 12 |
| E11 | Ciudad Universitaria | 12 |
| E12 | Concejo de Bogotá | 10 |
| E13 | Centro Memoria | 6 |
| E14 | Universidades | 2 |

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
| `java -cp out Main servicio K43` | Recorrido de una ruta en el tramo |
| `java -cp out Main ruta Normandia "Concejo de Bogota"` | Ruta más rápida |
| `java -cp out Main transbordos Normandia "Concejo de Bogota"` | Ruta con menos transbordos |
| `java -cp out Main paradas Normandia "Concejo de Bogota"` | Ruta con menos paradas |

Las estaciones se escriben por **id** (`E07`), **nombre** o **alias** (`Maloka`, `CityU`, `Consejo de Bogota`), sin importar tildes ni mayúsculas.

### Ejemplo de salida

```text
RUTA MÁS RÁPIDA: Normandía -> Concejo de Bogotá
Tiempo total: 20 min 30 s (en bus 20 min 30 s + 0 transbordo(s)) | Paradas: 9
  1. Tome 1    en Normandía              y bájese en Concejo de Bogotá (9 paradas)
```

## Pruebas

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Pruebas
```

Resultado esperado: `Resultado: 39 aprobadas, 0 fallidas`. Cubren:

- `MinHeap`: vacío, un elemento, 1 000 valores aleatorios ordenados.
- Construcción: rechazo de duplicados, tramos inválidos, paradas repetidas y servicios sin vía física (sin dejar arcos a medias); tiempo de arcos expresos.
- Datos reales: rutas que paran en CAN (14, la ruta 1 cuenta ida y regreso) y en Universidades (solo la 1); rutas directas en ambos sentidos; alias.
- Rutas: red pequeña donde cada criterio da una respuesta distinta, origen = destino, estación aislada, sentido de los servicios.
- **Dijkstra por estados = Bellman-Ford** en los 196 pares × 3 penalizaciones (588 consultas), y coherencia entre los tres criterios en todos los pares.

## Estructura

```text
transmilenio-rutas/
├── README.md
├── entrega-01.pdf          # Documento técnico
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
    └── Pruebas.java            # 39 pruebas
```

## Modelo

- **Vía física:** grafo no dirigido, 13 tramos con tiempo estimado (90 a 150 s).
- **Red de servicios:** multigrafo dirigido. Por cada servicio y par de paradas consecutivas hay un arco con tiempo = camino mínimo en la vía física + 30 s de detención. 124 arcos. Así se calcula el tiempo de los expresos.
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

## Estado de la entrega

| Funcionalidad | Estado |
| --- | --- |
| Carga y validación de estaciones, tramos y servicios | Implementado |
| Rutas que paran en una estación / recorrido de una ruta | Implementado |
| Rutas directas entre dos estaciones | Implementado |
| Ruta más rápida y con menos transbordos (Dijkstra por estados) | Implementado |
| Ruta con menos paradas (BFS) | Implementado |
| Búsqueda por nombre, alias o id sin tildes | Implementado |
| Sugerencias por fragmento | Parcial (lineal, se migrará a trie) |
| Horarios de operación de cada ruta | Pendiente (Entrega 2) |
| Autocompletado con trie | Pendiente (Entrega 2) |
| Otras troncales | Pendiente (Entrega 3) |

## Limitaciones conocidas

- Solo se modela el tramo Portal Eldorado - Universidades; de cada servicio se guardan solo sus paradas dentro del tramo.
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
- Documentación de Java SE 21: https://docs.oracle.com/en/java/javase/21/docs/api/
