# SISO-UNAL — Inscripción de Asignaturas UNAL

Proyecto semestral de Estructuras de Datos (DS3, grupo 03, 2026-2) — **team-07**. Entrega 1: propuesta, diseño e implementación inicial.

## Descripción

Sistema por consola, en Java, que simula la inscripción de asignaturas por citas de la Universidad Nacional (sede Bogotá, pregrado) según los grupos de prelación de la Circular 13 de 2026.

Hoy, cuando alguien cancela, el cupo liberado se lo queda quien recargue primero el SIA. SISO-UNAL agrega una **lista de espera por prioridad** a cada grupo. Cuando el grupo está lleno, el estudiante puede confirmar que acepta la asignación automática; si la acepta, queda en espera según su prioridad oficial, y cada cupo liberado se asigna automáticamente al de mayor prioridad. Si no la acepta, no entra a la lista. La lista de espera reasigna cupos existentes; no crea cupos adicionales, como sí lo hacen los sobrecupos. En las siguientes entregas se agregan la sugerencia de grupos sin cruce de horario y los reportes de demanda insatisfecha.

**Prioridad.** `Student` ubica a cada estudiante en uno de los cuatro grupos de prelación:

1. discapacidad;
2. vulnerabilidad académica (PAPA ≤ 3.1 o programas como PEAMA);
3. avance ≥ 80 %;
4. el resto.

Dentro de cada grupo el orden es: grupo 1, PAPA de mayor a menor; grupo 2, PAPA de menor a mayor (más riesgo primero); grupo 3, avance de mayor a menor y luego PAPA; grupo 4, IPA de mayor a menor. El último desempate es el código. **El IPA es un dato oficial de entrada; el sistema no lo calcula.**

## Integrantes

| Usuario de GitHub | Aporte |
|---|---|
| [@BLlontopSec](https://github.com/BLlontopSec) | Versión inicial de `Node`, `LinkedQueue`, `LinkedStack`, `Student`, `CourseGroup` y `Main`; documento base de la propuesta. |
| [@DfCastilloa](https://github.com/DfCastilloa) | Estructuras genéricas, comparación por prioridad en `Student`, `LinkedList`, `PriorityQueueADT`, `SortedLinkedPriorityQueue`, lista de espera y cancelación en `CourseGroup`, demo y README. |
| [@hoverwars](https://github.com/hoverwars) | Por definir. |

## Estructuras de datos

| Estructura | Uso en el proyecto | Operaciones y costo |
|---|---|---|
| `SortedLinkedPriorityQueue<T>` (implementa `PriorityQueueADT<T>`) | Lista de espera de cada grupo; ordenar a los estudiantes para las citas | `insert` O(w), `removeHighest` O(1), `peekHighest` O(1), `remove`/`contains` O(w) |
| `LinkedQueue<T>` (cola FIFO) | Estudiantes de una cita, en orden de llegada | `enqueue`, `dequeue`, `peek` O(1) |
| `LinkedList<T>` (con referencia al último nodo) | Inscritos de un grupo | `addLast` O(1), `remove`/`contains` O(c) |
| `LinkedStack<T>` (pila) | Carrito de selección con deshacer | `push`, `pop`, `peek` O(1) |

w = tamaño de la lista de espera; c = cupo del grupo.

La lista de espera se programa contra la interfaz `PriorityQueueADT`, para que en la Entrega 2 la lista ordenada se pueda reemplazar por un heap (O(log w) en inserción y extracción) sin cambiar `CourseGroup`.

Operaciones de `CourseGroup`:

- `request(s, aceptaAsignacion)`: inscribe si hay cupo (`ENROLLED`). Si no hay cupo y el estudiante acepta la asignación automática, entra a la lista de espera (`WAITLISTED`); si no la acepta, no entra (`REJECTED`). Cuesta O(c + w), porque primero verifica que el estudiante no esté ya inscrito ni en espera.
- `leaveWaitlist(s)`: el estudiante retira su aceptación y sale de la lista, O(w).
- `cancel(s)`: retira al estudiante, O(c), y pasa el cupo al primero de la lista de espera, O(1).

## Documento técnico

La fuente LaTeX del documento de la Entrega 1 está en [`docs/SISO-UNAL_Entrega1.tex`](docs/SISO-UNAL_Entrega1.tex) (plantilla IEEE; se compila con pdfLaTeX, por ejemplo en Overleaf). Es una versión preliminar y puede cambiar antes de la entrega.

## Requisitos

- Java 17 o superior. Sin dependencias externas.

## Compilar y ejecutar

Desde esta carpeta (`DS3/teams/team-07/projects/entrega-01/`):

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

En la consola de Windows, si las tildes salen mal, ejecutar antes `chcp 65001`.

## Salida esperada

```text
Orden de prioridad para las citas: [Ana (G1), Carla (G3), Elena (G4), Felipe (G4)]

--- 1. Citas: todos piden Cálculo Diferencial Gr-1 (cupo 2) ---
Ana (G1) -> inscrito
Carla (G3) -> inscrito
Elena (G4) -> sin cupo; aceptó la asignación automática, entra a lista de espera
Felipe (G4) -> sin cupo; no aceptó la asignación automática, no entra a la lista
   Inscritos en Cálculo Diferencial Gr-1: [Ana (G1), Carla (G3)] | Cupos libres: 0
   Lista de espera: [Elena (G4)]

--- 2. Adiciones: Daniel pide Cálculo Diferencial Gr-1 ---
Daniel (G3) -> sin cupo; aceptó la asignación automática, entra a lista de espera
   Inscritos en Cálculo Diferencial Gr-1: [Ana (G1), Carla (G3)] | Cupos libres: 0
   Lista de espera: [Daniel (G3), Elena (G4)]

--- 3. Carla cancela ---
El cupo liberado se asignó automáticamente a: Daniel (G3)
   Inscritos en Cálculo Diferencial Gr-1: [Ana (G1), Daniel (G3)] | Cupos libres: 0
   Lista de espera: [Elena (G4)]

--- 4. Carrito de Felipe con deshacer ---
Último grupo añadido: Cálculo Diferencial Gr-2
Deshacer -> se quita: Cálculo Diferencial Gr-2
Queda en el tope del carrito: Cálculo Diferencial Gr-1
```

Lo que muestra: Felipe no aceptó la asignación automática, así que no entra a la lista. Daniel llegó **después** que Elena, pero queda primero en la lista por su prioridad (grupo 3); por eso recibe el cupo que libera Carla.

## Estado

- [x] Estructuras propias genéricas: `Node`, `LinkedList`, `LinkedQueue`, `LinkedStack`, `PriorityQueueADT`, `SortedLinkedPriorityQueue`
- [x] Grupos de prelación y orden de prioridad (IPA como dato)
- [x] Inscripción con cupo y lista de espera por prioridad, con confirmación de la asignación automática
- [x] Cancelación con reasignación automática del cupo
- [x] Carrito con deshacer
- [ ] Sacar al estudiante de las listas de espera de otros grupos de la misma asignatura
- [ ] Franjas de citas (arreglo de colas) y catálogo con varios grupos
- [ ] Heap, tabla hash, datos sintéticos a escala real, cruces de horario (Entrega 2)
- [ ] Prerrequisitos con grafos, sugerencia de grupos y reportes (Entrega 3)

## Limitaciones

- Demostración guiada, sin menú interactivo.
- Un estudiante puede quedar en espera en dos grupos de la misma asignatura; todavía no se le retira del otro cuando obtiene cupo.
- Insertar en la lista ordenada cuesta O(w); a escala real se reemplazará por un heap.
- La aceptación se da al pedir el grupo y la asignación es inmediata; una oferta con plazo para aceptar o rechazar el cupo queda para la Entrega 2.
- Los sobrecupos (cupos adicionales aprobados por la unidad académica) no se modelan.

## Declaración de uso de IA

Se usó Claude (Anthropic) para revisar el código, proponer correcciones e implementar la lista de espera por prioridad. Los integrantes verificaron el código y responden por él.
