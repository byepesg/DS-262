# Sistema de Priorización de Atención (SPA)

Proyecto semestral, **Entrega 1** (Propuesta, Diseño e Implementación Inicial).
Asignatura Estructuras de Datos, Universidad Nacional de Colombia, semestre
2026-2. Grupo DS3, equipo 02.

## Integrantes

| Integrante | Usuario GitHub | Correo |
| --- | --- | --- |
| Johan Adrian Torres Robles | jotorresro | jotorresro@unal.edu.co |
| Esteban Mora Lopez | emoritax | emoral@unal.edu.co |
| Miguel Angel Rodriguez Bohorquez | Maracuya4 | mrodriguezbo@unal.edu.co |

### Aportes

| Integrante | Aporte |
| --- | --- |
| Johan Adrian Torres Robles (jotorresro) | Coordinación del proyecto; diseño de la cola de prioridad por niveles; implementación de `LinkedQueue`, `LevelPriorityQueue`, `TriageSystem` y la cancelación; redacción de la metodología y del pseudocódigo; integración y publicación en el repositorio. |
| Esteban Mora Lopez (emoritax) | Participó en la definición del problema, el alcance y los requerimientos; implementación de la pila y el historial (`LinkedStack`, `ServiceHistory`); redacción de la introducción y el trabajo relacionado. |
| Miguel Angel Rodriguez Bohorquez (Maracuya4) | Participó en el diseño del menú de consola (`Main`) y en las pruebas (`BasicTests`) con sus casos borde; redacción del estado de la implementación, la discusión y las conclusiones; revisión general del documento. |

El proyecto se desarrolló en sesiones de trabajo conjunto, por lo que los
commits aparecen desde una sola cuenta; todos los integrantes conocen el
proyecto completo.

**Versión revisada (noviembre de 2026).** La entrega se reestructuró para
integrar los temas del curso (árboles, BST, AVL, heaps, Heap-Sort, árboles
de decisión y grafos): se añadieron el índice AVL, la cola por plazo con
min-heap, deshacer, los reportes con Heap-Sort, el protocolo de triage, la
red de sedes, las pruebas automáticas, los experimentos y el documento
técnico actualizado.

## Descripción del proyecto

Sistema que clasifica, registra, prioriza, atiende, busca, cancela y deriva
solicitudes de atención (por ejemplo, una red de salas de urgencias o de
mesas de ayuda). Cada necesidad se resuelve con la estructura de datos
apropiada:

| Necesidad | Estructura | Clase |
| --- | --- | --- |
| Decidir cuál solicitud sigue (urgencia y luego llegada) | Arreglo de 5 colas FIFO sobre listas enlazadas | `LevelPriorityQueue`, `LinkedQueue` |
| Decidir cuál sigue sin inanición (plazo máximo por nivel) | Min-heap binario sobre arreglo dinámico | `DeadlinePriorityQueue`, `MinHeap` |
| Buscar, cancelar y validar ids en `O(log n)` garantizado | Árbol AVL (BST ordinario como línea base) | `AvlIndex`, `BstIndex` |
| Historial y deshacer la última atención | Pila sobre lista enlazada | `ServiceHistory`, `LinkedStack` |
| Listar pendientes en orden de atención sin vaciar la cola | Heap-Sort in-place | `MinHeap.heapSort` |
| Asignar la urgencia con un protocolo de preguntas | Árbol de decisión binario | `DecisionTree` |
| Derivar a la sede con cupo más cercana | Grafo ponderado con listas de adyacencia, BFS y Dijkstra | `SiteNetwork` |

`PendingQueue` y `TicketIndex` son interfaces: cada una tiene dos
representaciones con el mismo comportamiento y distinto costo. La fachada
`TriageSystem` coordina cola, índice e historial y mantiene su
consistencia; las cancelaciones se descartan de forma perezosa cuando
llegan al frente de la cola.

El documento técnico, con la justificación de cada decisión, el análisis de
complejidad y los experimentos, está en `docs/main.pdf` (fuente en
`docs/main.tex`, compilable con pdfLaTeX).

## Complejidad de las operaciones

`n` es el número de solicitudes pendientes de la sede, `h` el número de
atenciones registradas, `V` el número de sedes y `E` el de vías.

| Operación | Costo | Supuesto |
| --- | --- | --- |
| Registrar solicitud | `O(log n)` | Validación e inserción en el AVL; encolar `Θ(1)` (niveles) o sift-up `O(log n)` (heap). |
| Atender siguiente | `O(log n)` | Eliminar del AVL; descartar canceladas cuesta `Θ(1)` amortizado. |
| Buscar por identificador | `O(log n)` | Un camino raíz-hoja del AVL, cuya altura es `O(log n)`. |
| Cancelar solicitud | `O(log n)` | Eliminar del AVL y marcar el ticket (eliminación perezosa). |
| Deshacer la última atención | `O(log n)` | Desapilar `Θ(1)`, reinsertar en el AVL y en la cola. |
| Consultar historial | `Θ(h)` | Recorre la pila sin modificarla. |
| Pendientes en orden de atención | `Θ(n log n)` | Inorder del AVL y Heap-Sort. |
| Pendientes por identificador | `Θ(n)` | Recorrido inorder del AVL. |
| Clasificar con el protocolo | `O(altura del árbol)` | Un camino raíz-hoja del árbol de decisión. |
| Sede con cupo más cercana / ruta mínima | `O((V + E) log V)` | Dijkstra con min-heap. |
| Saltos entre sedes | `O(V + E)` | BFS con cola. |

## Estructura del repositorio

```
.
├── README.md
├── docs/
│   ├── main.tex
│   ├── main.pdf
│   └── sections/
│       ├── 01-introduccion.tex
│       ├── 02-trabajo-relacionado.tex
│       ├── 03-metodologia.tex
│       ├── 04-resultados.tex
│       ├── 05-discusion.tex
│       └── 06-conclusiones.tex
└── src/
    └── triage/
        ├── Ticket.java
        ├── LinkedQueue.java
        ├── LinkedStack.java
        ├── PendingQueue.java
        ├── LevelPriorityQueue.java
        ├── MinHeap.java
        ├── DeadlinePriorityQueue.java
        ├── TicketIndex.java
        ├── BstIndex.java
        ├── AvlIndex.java
        ├── ServiceHistory.java
        ├── DecisionTree.java
        ├── SiteNetwork.java
        ├── TriageSystem.java
        ├── Main.java
        ├── BasicTests.java
        └── Experiments.java
```

## Cómo compilar y ejecutar

Requiere **Java 17** o superior (JDK). No tiene dependencias externas.

Desde la carpeta raíz del proyecto, compilar:

```bash
javac -d out src/triage/*.java
```

Ejecutar el menú de consola (al iniciar se elige la política de atención):

```bash
java -cp out triage.Main
```

## Pruebas

```bash
java -cp out triage.BasicTests
```

Ejecuta 69 verificaciones automáticas en ocho bloques (escenario principal,
casos borde, deshacer, índices AVL y BST, heap y Heap-Sort, política por
plazo, árbol de decisión y grafo). Cada línea imprime `[OK]` o `[FALLA]` y
el programa termina con código 1 si alguna falla. Final esperado:

```
Resultado: 69 correctas, 0 fallidas
```

## Experimentos

```bash
java -cp out triage.Experiments
```

Usa semillas fijas. Compara BST y AVL con identificadores ordenados y
aleatorios, mide la cola por niveles frente al heap y simula un turno con
sobrecarga para cada política. Los tiempos varían entre equipos; las
alturas y la simulación no. Resultados en la sección IV del documento.

## Ejemplo básico de uso

```
Politica de atencion:
  1) Por niveles: siempre primero la urgencia mayor
  2) Por plazo maximo: la que vence primero (evita esperas infinitas)
Opcion [1]: 2

Opcion: 2
ID: A2
Nombre: Ana
La persona esta inconsciente o no respira? (s/n): n
Tiene dificultad respiratoria grave o dolor en el pecho? (s/n): s
Nivel asignado por el protocolo: 2
Solicitud registrada: Ticket{id=A2, nombre=Ana, urgencia=2, llegada=0, minuto=0, plazo=10}

Opcion: 13
Sede Chapinero a 15 min. Camino: Central -> Chapinero
```

## Estado de la implementación

- **Implementado:** los doce requerimientos funcionales (registrar, atender
  con dos políticas, buscar, cancelar, deshacer, historial, pendientes en
  orden y por identificador, vencidas, protocolo de triage, derivación,
  rutas y saltos entre sedes), menú de consola, pruebas automáticas y
  experimentos.
- **Pendiente:** cargar el protocolo y la red de sedes desde archivos y
  cambiar la urgencia de una solicitud ya registrada.

## Limitaciones conocidas

- El protocolo de triage y la red de seis sedes son ejemplos ilustrativos
  fijados en el código; el protocolo no es un protocolo clínico real.
- El tiempo es un reloj lógico en minutos que avanza el operador.
- Las canceladas permanecen en la cola hasta llegar al frente: memoria
  `O(n + c)`, con `c` cancelaciones aún no descartadas.
- Los datos existen solo durante la ejecución; no hay persistencia.

## Referencias y declaración de ayudas

- T. H. Cormen, C. E. Leiserson, R. L. Rivest y C. Stein, *Introduction to
  Algorithms*, 4th ed. MIT Press, 2022.
- M. T. Goodrich, R. Tamassia y M. H. Goldwasser, *Data Structures and
  Algorithms in Java*, 6th ed. Wiley, 2014.
- G. M. Adelson-Velsky y E. M. Landis, "An algorithm for the organization of
  information," *Soviet Mathematics Doklady*, 1962.
- E. W. Dijkstra, "A note on two problems in connexion with graphs,"
  *Numerische Mathematik*, 1959.
- Manchester Triage Group, *Emergency Triage*, 3rd ed. Wiley-Blackwell, 2014.
- Todas las estructuras principales son implementación propia; de la
  biblioteca estándar de Java solo se usan `ArrayList`, `Comparator`,
  `Random` y `Scanner`.
- **Declaración de ayudas:** se utilizó Claude (Anthropic), un asistente de
  inteligencia artificial, como apoyo en la implementación del código y en la
  redacción del documento técnico. El equipo revisó, probó y comprende todo
  el código y las decisiones de diseño presentadas.
