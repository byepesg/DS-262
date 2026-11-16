# Sistema de Priorización de Atención (SPA)

Proyecto semestral, **Entrega 2** (Desarrollo, Integración y Evaluación
Parcial). Asignatura Estructuras de Datos, Universidad Nacional de Colombia,
semestre 2026-2. Grupo DS3, equipo 02.

Esta entrega es una evolución directa de la Entrega 1: conserva su diseño
(cola de prioridad por niveles y pila de historial) y lo completa con un
índice hash, la operación de deshacer, pruebas automatizadas y mediciones de
desempeño. El resumen de cambios está en la sección
[Cambios respecto de la Entrega 1](#cambios-respecto-de-la-entrega-1).

## Integrantes

| Integrante | Usuario GitHub | Correo |
| --- | --- | --- |
| Johan Adrian Torres Robles | jotorresro | jotorresro@unal.edu.co |
| Esteban Mora Lopez | emoritax | emoral@unal.edu.co |
| Miguel Angel Rodriguez Bohorquez | Maracuya4263 | mrodriguezbo@unal.edu.co |

### Aportes

| Integrante | Entrega 1 | Entrega 2 |
| --- | --- | --- |
| Johan Adrian Torres Robles (jotorresro) | Coordinación; cola de prioridad por niveles; `LinkedQueue`, `LevelPriorityQueue`, `TriageSystem` y cancelación; metodología y pseudocódigo; integración en el repositorio. | _Por completar por el equipo._ |
| Esteban Mora Lopez (emoritax) | Definición del problema, alcance y requerimientos; `LinkedStack` y `ServiceHistory`; introducción y trabajo relacionado. | _Por completar por el equipo._ |
| Miguel Angel Rodriguez Bohorquez (Maracuya4263) | Menú de consola (`Main`) y pruebas (`BasicTests`); estado de la implementación, discusión y conclusiones; revisión del documento. | _Por completar por el equipo._ |

Todos los integrantes conocen el proyecto completo.

## Descripción del proyecto

Sistema que registra, prioriza, atiende, busca, cancela y consulta
solicitudes de atención (por ejemplo, una fila de urgencias o una mesa de
ayuda técnica), atendiendo siempre primero la solicitud de mayor urgencia
(1 = más urgente, 5 = menos urgente) y, entre iguales, la que llegó antes.

| Estructura (propia) | Clase | Para qué se usa |
| --- | --- | --- |
| Cola de prioridad por niveles (_bucket queue_) | `LevelPriorityQueue` | Arreglo de 5 colas FIFO, una por urgencia. Decide el orden de atención en `O(1)`. |
| Lista doblemente enlazada con localizadores | `LinkedQueue<T>` | Cada nivel. `enqueue` devuelve el nodo, que permite retirarlo en `O(1)`. |
| Tabla hash con encadenamiento separado | `HashTable<K, V>` | Índice `id -> nodo` de las pendientes: buscar, cancelar y detectar duplicados en `O(1)` esperado. |
| Pila sobre lista enlazada | `LinkedStack<T>` / `ServiceHistory` | Historial de atenciones y deshacer la más reciente en `O(1)`. |

`TriageSystem` es la fachada que coordina las tres estructuras y mantiene el
invariante: _un id está en el índice si y solo si hay una solicitud
pendiente con ese id, y su valor es el nodo de esa solicitud_.

El documento técnico está en [`docs/main.pdf`](docs/main.pdf) (fuente en
`docs/main.tex` y `docs/sections/`, compilable con pdfLaTeX en Overleaf).

## Cambios respecto de la Entrega 1

| Aspecto | Entrega 1 | Entrega 2 |
| --- | --- | --- |
| Deshacer última atención (RF5) | Pendiente | Implementado, `O(1)` |
| Buscar / cancelar por id | Recorrido lineal, `Θ(n)` | Índice hash, `O(1)` esperado |
| Verificar id repetido al registrar | Recorrido lineal, `Θ(n)` | Índice hash, `O(1)` esperado |
| Cola de cada nivel | Lista simple; quitar un nodo exige buscar el anterior | Lista doble; los nodos son localizadores |
| Leer el historial | Desapilar todo a una pila auxiliar y reapilar | Iterador de solo lectura |
| Validación | No validaba el nombre | Id y nombre no vacíos; ids normalizados con `trim` |
| Listar pendientes en orden (RF7) | No existía | Nuevo, opción 7 del menú |
| Pruebas | `BasicTests` manual (imprimía esperado vs. obtenido) | 42 pruebas automatizadas con aserciones |
| Desempeño | Sin medir | `Benchmark` con `n` hasta 10^6 |

## Complejidad de las operaciones

`n` = solicitudes pendientes, `k = 5` niveles de urgencia, `h` = atenciones
registradas. "Esperado" supone que la función hash reparte bien los ids
(hashing uniforme simple); en el peor caso, con todos los ids en una misma
cubeta, las operaciones del índice cuestan `O(n)`.

| Operación | Método | Entrega 1 | Entrega 2 |
| --- | --- | --- | --- |
| Registrar (RF1) | `registerRequest` | `Θ(n)` | `O(1)` esperado, amortizado (la tabla se duplica al superar factor de carga 0.75) |
| Atender (RF2) | `serveNext` | `Θ(1)` | `O(1)` esperado (revisa ≤ k niveles + borrar del índice) |
| Ver siguiente | `peekNext` | — | `Θ(1)` |
| Buscar por id (RF3) | `findById` | `Θ(n)` | `O(1)` esperado |
| Cancelar (RF4) | `cancelRequest` | `Θ(n)` | `O(1)` esperado (índice + desenlace en lista doble) |
| Deshacer (RF5) | `undoLastService` | — | `O(1)` esperado |
| Historial (RF6) | `getHistory` | `Θ(h)` | `Θ(h)` |
| Pendientes en orden (RF7) | `getPendingInOrder` | — | `Θ(n)` |

Memoria: `Θ(n + h)`; medido, unos 97 bytes por solicitud pendiente.

## Estructura del repositorio

```
.
├── README.md
├── docs/
│   ├── main.tex, main.pdf
│   └── sections/                  01-introduccion ... 09-conclusiones
├── src/triage/                    código del sistema
│   ├── Ticket.java                solicitud (inmutable)
│   ├── LinkedQueue.java           cola sobre lista doble con localizadores
│   ├── LevelPriorityQueue.java    cola de prioridad por niveles
│   ├── HashTable.java             tabla hash con encadenamiento separado
│   ├── LinkedStack.java           pila sobre lista enlazada
│   ├── ServiceHistory.java        historial de atenciones (pila)
│   ├── TriageSystem.java          fachada
│   └── Main.java                  menú de consola
├── test/triage/                   pruebas automatizadas
│   ├── Check.java                 mini marco de aserciones
│   ├── DataStructureTests.java    pruebas unitarias de las estructuras
│   ├── TriageSystemTests.java     integración, casos borde y prueba aleatoria vs. modelo
│   └── AllTests.java              punto de entrada
├── bench/triage/                  mediciones de desempeño
│   ├── Benchmark.java
│   └── SortedListPriorityQueue.java   alternativa descartada, solo para comparar
└── results/                       salida del benchmark usada en el documento
    ├── benchmark.csv
    └── benchmark.txt
```

## Cómo compilar y ejecutar

Requiere **Java 17** o superior (JDK). No tiene dependencias externas
(tampoco JUnit: las pruebas usan un mini marco propio).

Desde la carpeta del proyecto (`DS3/teams/team-02/projects/project-01`):

```bash
javac -d out src/triage/*.java
java -cp out triage.Main
```

Opciones del menú: `1` registrar, `2` atender siguiente, `3` buscar por ID,
`4` cancelar, `5` deshacer última atención, `6` historial, `7` pendientes en
orden de atención, `0` salir.

## Pruebas

```bash
javac -d out src/triage/*.java test/triage/*.java
java -cp out triage.AllTests
```

Cada prueba imprime `[OK]` o `[FALLA]`; el programa termina con código de
salida 1 si alguna falla. Resultado esperado (última línea; el tiempo
varía):

```
Pruebas: 42 correctas, 0 fallidas; 285173 aserciones; 355 ms
```

Qué cubren:

- **Unitarias** de `LinkedQueue`, `LinkedStack`, `HashTable` y
  `LevelPriorityQueue`: estructura vacía, un elemento, eliminar cabeza /
  medio / final, localizadores inválidos, colisiones (`"Aa"` y `"BB"` tienen
  el mismo `hashCode`), redimensionamiento con 10 000 claves.
- **Integración** de `TriageSystem`: el escenario de `BasicTests` de la
  Entrega 1, ahora con aserciones; entradas inválidas (urgencia 0 o 9, id o
  nombre vacíos o `null`, id repetido, cancelar o buscar inexistentes, cola
  vacía); deshacer (posición al frente del nivel, varios en orden LIFO,
  llegadas posteriores, id reutilizado).
- **Aleatorias contra un modelo**: 5 semillas × 20 000 operaciones mezcladas
  comparadas paso a paso con una implementación de referencia hecha con
  `TreeSet`, `HashMap` y `ArrayDeque` de Java.

## Benchmark

```bash
javac -d out src/triage/*.java bench/triage/*.java
java -Xmx2g -cp out triage.Benchmark results/benchmark.csv   # ~5 min
java -cp out triage.Benchmark --quick                        # prueba rápida, ~2 s
```

Resultados de referencia (OpenJDK 21, 2 núcleos Intel Xeon 2.1 GHz; mediana
de 5 repeticiones, ns por operación). Salida completa en
[`results/benchmark.txt`](results/benchmark.txt).

| n | Buscar lineal (E1) | Buscar con índice (E2) | Cola por niveles | Heap binario |
| --- | --- | --- | --- | --- |
| 10^3 | 2 467 | 7 | 47 | 411 |
| 10^4 | 27 770 | 13 | 31 | 193 |
| 10^5 | 1 840 793 | 81 | 55 | 402 |
| 10^6 | 11 991 043 | 189 | 103 | 717 |

## Ejemplo de uso

Registrar tres solicitudes, ver el orden, atender, deshacer y cancelar
(entrada: `1 A1 "Juan Perez" 3`, `1 A2 "Ana Gomez" 1`, `1 A3 "Luis Ruiz" 1`,
`7`, `2`, `5`, `4 A3`, `7`, `0`):

```
Solicitud registrada: Ticket{id=A1, nombre=Juan Perez, urgencia=3, llegada=0}
Solicitud registrada: Ticket{id=A2, nombre=Ana Gomez, urgencia=1, llegada=1}
Solicitud registrada: Ticket{id=A3, nombre=Luis Ruiz, urgencia=1, llegada=2}

Pendientes: 3 (por urgencia: 1=2 2=0 3=1 4=0 5=0)
Orden de atencion:
  1. Ticket{id=A2, nombre=Ana Gomez, urgencia=1, llegada=1}
  2. Ticket{id=A3, nombre=Luis Ruiz, urgencia=1, llegada=2}
  3. Ticket{id=A1, nombre=Juan Perez, urgencia=3, llegada=0}

Atendiendo: Ticket{id=A2, nombre=Ana Gomez, urgencia=1, llegada=1}
Atencion deshecha; vuelve a pendientes: Ticket{id=A2, nombre=Ana Gomez, urgencia=1, llegada=1}
Solicitud cancelada.

Pendientes: 2 (por urgencia: 1=1 2=0 3=1 4=0 5=0)
Orden de atencion:
  1. Ticket{id=A2, nombre=Ana Gomez, urgencia=1, llegada=1}
  2. Ticket{id=A1, nombre=Juan Perez, urgencia=3, llegada=0}
```

Para reproducirlo sin escribir a mano:

```bash
printf '1\nA1\nJuan Perez\n3\n1\nA2\nAna Gomez\n1\n1\nA3\nLuis Ruiz\n1\n7\n2\n5\n4\nA3\n7\n0\n' | java -cp out triage.Main
```

## Estado de la implementación

- **Implementado:** RF1–RF7 (registrar, atender por prioridad, buscar,
  cancelar, deshacer, historial, pendientes en orden), índice hash,
  validación de entradas, menú de consola, pruebas automatizadas y
  benchmark.
- **Preliminar:** el análisis experimental (un solo equipo, urgencias
  uniformes).
- **Pendiente para la entrega final:** reclasificar la urgencia de una
  pendiente (RF8), carga por lotes desde archivo, estadísticas de atención y
  benchmark con distribuciones de urgencia sesgadas.

El detalle está en las secciones IV y VIII de `docs/main.pdf`.

## Limitaciones conocidas

- El costo `O(1)` del índice es esperado; un conjunto de ids diseñado para
  colisionar lo degrada a `O(n)`.
- El costo constante de la cola de prioridad depende de que la urgencia tome
  solo cinco valores.
- Deshacer solo revierte atenciones (no registros ni cancelaciones) y se
  rechaza si el id de la solicitud fue reutilizado por otra pendiente.
- Los datos existen solo durante la ejecución; no hay persistencia.

## Referencias y declaración de ayudas

- T. H. Cormen, C. E. Leiserson, R. L. Rivest y C. Stein, *Introduction to
  Algorithms*, 4th ed. MIT Press, 2022.
- M. T. Goodrich, R. Tamassia y M. H. Goldwasser, *Data Structures and
  Algorithms in Java*, 6th ed. Wiley, 2014.
- R. B. Dial, "Algorithm 360: Shortest-path forest with topological
  ordering," *Communications of the ACM*, 12(11), 1969.
- E. Gamma, R. Helm, R. Johnson y J. Vlissides, *Design Patterns*.
  Addison-Wesley, 1994.
- Ministerio de Salud y Protección Social, Resolución 5596 de 2015 (triage
  de cinco categorías).
- N. Gilboy et al., *Emergency Severity Index (ESI), Version 4*. AHRQ, 2011.
- Las colas, la cola de prioridad, la tabla hash y la pila son
  implementación propia; de la biblioteca estándar de Java solo se usa
  `ArrayList` para devolver listados. Las pruebas y el benchmark usan
  además colecciones de `java.util` como referencia de comparación.
- **Declaración de ayudas:** se utilizó Claude (Anthropic), un asistente de
  inteligencia artificial, como apoyo en la implementación del código, las
  pruebas, el benchmark y la redacción del documento técnico. El equipo
  revisó, probó y comprende todo el código y las decisiones de diseño
  presentadas.
