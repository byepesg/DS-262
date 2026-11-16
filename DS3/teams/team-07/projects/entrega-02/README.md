# SISO-UNAL — Entrega 2

Proyecto semestral de Estructuras de Datos (DS3, grupo 03, 2026-2), **team-07**. Esta entrega continúa la [Entrega 1](../entrega-01/). El documento técnico está en [`docs/`](docs/).

## Qué hace

Simula la inscripción de asignaturas de la Universidad Nacional (sede Bogotá, pregrado).

- Cada grupo tiene cupo, horario y una lista de espera.
- Si el grupo está lleno y el estudiante acepta la asignación automática, queda en espera según su prioridad oficial (grupo de prelación e IPA, que son datos de entrada).
- Cuando alguien cancela, el cupo pasa solo al primero de la lista que pueda tomarlo.
- No se permite inscribir un grupo que se cruce con el horario del estudiante, ni dos grupos de la misma asignatura.
- Cuando un estudiante consigue cupo, sale de las demás listas de espera de esa asignatura.
- En la fase de solo cancelaciones no se puede inscribir y los cupos liberados no se reasignan.
- Estudiantes y grupos se buscan por su código.
- Si un grupo está lleno, sugiere otros grupos de la misma asignatura con cupo y sin cruce.
- Muestra los grupos con más estudiantes en espera.
- Se puede usar con un menú en la consola.

## Qué cambió frente a la Entrega 1

| Entrega 1 | Entrega 2 |
|---|---|
| Lista de espera con una lista ordenada | Lista de espera con un montículo (heap) |
| Sin búsqueda por código | Búsqueda de estudiantes y grupos con un árbol AVL |
| Sin horarios | Horarios por grupo y control de cruces |
| Un estudiante podía quedar en espera en dos grupos de la misma asignatura | Al recibir cupo sale de las otras listas |
| Sin fases | Fase de solo cancelaciones |
| Sin sugerencias ni reportes | Sugerencia de otro grupo y reporte de los grupos con más demanda |
| Demostración guiada | Demostración guiada y menú interactivo |
| Sin pruebas automáticas | 48 pruebas y una medición de tiempos |

## Qué se usó

| Estructura | Para qué |
|---|---|
| Montículo (`BinaryHeapPriorityQueue`) | Lista de espera de cada grupo: entrega siempre al de mayor prioridad. También arma el reporte de los grupos con más demanda |
| Árbol AVL (`AVLTreeDictionary`) | Encontrar rápido un estudiante o un grupo por su código |
| Cola (`LinkedQueue`) | Orden de llegada en una cita |
| Lista enlazada (`LinkedList`) | Inscritos, horario de cada grupo y grupos de cada estudiante |
| Pila (`LinkedStack`) | Carrito con opción de deshacer |

`RegistrationSystem` reúne todo: recibe las solicitudes, revisa cruces y fases, reasigna los cupos, sugiere grupos y arma el reporte. `Menu` permite usarlo desde la consola.

| Operación | Costo |
|---|---|
| Entrar a la lista de espera / sacar al primero | O(log w) |
| Buscar estudiante o grupo por código | O(log n) |
| Revisar cruces de horario | O(k), k = grupos inscritos del estudiante |
| Salir de una lista de espera | O(w) |
| Sugerir otros grupos | O(log n + a·k), a = grupos de la asignatura |
| Reporte de los k grupos con más demanda | O(g log k), g = grupos |

w = personas en espera; n = estudiantes o grupos registrados.

## Integrantes

- [@BLlontopSec](https://github.com/BLlontopSec)
- [@DfCastilloa](https://github.com/DfCastilloa)
- [@hoverwars](https://github.com/hoverwars)

## Cómo ejecutarlo

Java 17 o superior, sin librerías externas. Desde esta carpeta:

```bash
javac -encoding UTF-8 -d out src/*.java tests/*.java
java -cp out Main
java -cp out Menu
java -cp out TestRunner
java -cp out Benchmark
```

- `Main`: demostración paso a paso.
- `Menu`: menú para probar el sistema a mano con datos de ejemplo (estudiantes 2024101 a 2024105; grupos como `1000004-Gr-1`).
- `TestRunner`: corre las pruebas; debe terminar con `48 pruebas correctas, 0 fallidas`.
- `Benchmark`: compara tiempos (tarda unos 20 segundos).

En Windows, si las tildes salen mal, ejecutar antes `chcp 65001`.

## Resultados de la medición

Tiempo en milisegundos (mediana de 5 corridas; varía según el equipo).

| Estudiantes | Lista ordenada | Heap | Búsqueda en lista | Búsqueda en AVL |
|---|---|---|---|---|
| 1 000 | 2.96 | 0.78 | 1.75 | 0.16 |
| 5 000 | 29.47 | 2.01 | 42.38 | 1.36 |
| 10 000 | 149.89 | 3.69 | 269.10 | 3.12 |
| 20 000 | 968.02 | 8.46 | 1 579.13 | 6.24 |

Con más estudiantes, la lista crece mucho más rápido que el heap y el AVL.

## Pendiente (Entrega 3)

- Tabla hash propia y comparación con el AVL.
- Prerrequisitos con grafos.
- Simulación del calendario con 28.700 estudiantes de prueba.

## Declaración de uso de IA

Se usó Claude (Anthropic) para proponer e implementar parte del código y las pruebas. Los integrantes revisaron el código y responden por él.
