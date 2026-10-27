# Sistema de Priorización de Atención (SPA)

Proyecto semestral, **Entrega 1** (Propuesta, Diseño e Implementación Inicial).
Asignatura Estructuras de Datos, Universidad Nacional de Colombia, semestre
2026-2. Grupo DS3, equipo 02.

## Integrantes

| Integrante | Usuario GitHub | Correo |
| --- | --- | --- |
| Johan Adrian Torres Robles | jotorresro | jotorresro@unal.edu.co |
| Esteban Mora Lopez | emoritax | emoral@unal.edu.co |
| Miguel Angel Rodriguez Bohorquez | Maracuya4263 | mrodriguezbo@unal.edu.co |

### Aportes

| Integrante | Aporte |
| --- | --- |
| Johan Adrian Torres Robles (jotorresro) | Coordinación del proyecto; diseño de la cola de prioridad por niveles; implementación de `LinkedQueue`, `LevelPriorityQueue`, `TriageSystem` y la cancelación; redacción de la metodología y del pseudocódigo; integración y publicación en el repositorio. |
| Esteban Mora Lopez (emoritax) | Participó en la definición del problema, el alcance y los requerimientos; implementación de la pila y el historial (`LinkedStack`, `ServiceHistory`); redacción de la introducción y el trabajo relacionado. |
| Miguel Angel Rodriguez Bohorquez (Maracuya4263) | Participó en el diseño del menú de consola (`Main`) y en las pruebas (`BasicTests`) con sus casos borde; redacción del estado de la implementación, la discusión y las conclusiones; revisión general del documento. |

El proyecto se desarrolló en sesiones de trabajo conjunto, por lo que los
commits aparecen desde una sola cuenta; todos los integrantes conocen el
proyecto completo.

## Descripción del proyecto

Sistema que registra, prioriza, atiende, busca, cancela y consulta
solicitudes de atención (por ejemplo, una fila de urgencias o una mesa de
ayuda técnica), atendiendo siempre primero la solicitud de mayor urgencia en
lugar de la que llegó primero.

- **Cola de prioridad por niveles**: un arreglo con una cola FIFO por nivel
  de urgencia (1 a 5), cada una implementada con una lista enlazada con
  referencias a la cabeza y a la cola. Decide el orden de atención y resuelve
  el desempate por orden de llegada.
- **Búsqueda y cancelación por identificador**: recorren las listas de los
  cinco niveles; la cancelación elimina el nodo de su lista.
- **Pila** (lista enlazada de encabezado único): registra el historial de
  atenciones y deja preparada la operación de deshacer.

El documento técnico, con la justificación de cada decisión de diseño, está
en `docs/main.pdf` (fuente en `docs/main.tex`, compilable con pdfLaTeX).

## Complejidad de las operaciones

`n` es el número de solicitudes pendientes, `k = 5` el número de niveles de
urgencia y `h` el número de atenciones registradas.

| Operación | Costo | Supuesto |
| --- | --- | --- |
| Registrar solicitud | `Θ(n)` | Encolar cuesta `Θ(1)` gracias a la referencia a la cola de la lista; la verificación de identificador repetido recorre las pendientes. |
| Atender siguiente | `Θ(1)` | Se revisan como máximo `k = 5` niveles, una constante, y desencolar cuesta `Θ(1)`. |
| Buscar por identificador | `Θ(n)` peor caso | Recorrido lineal de las listas de los cinco niveles. |
| Cancelar solicitud | `Θ(n)` peor caso | Búsqueda lineal más desenlace del nodo en `Θ(1)`. |
| Consultar historial | `Θ(h)` | Recorre la pila completa sin modificarla. |
| Cantidad de pendientes | `Θ(1)` | Se mantiene un contador. |

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
        ├── LevelPriorityQueue.java
        ├── LinkedStack.java
        ├── ServiceHistory.java
        ├── TriageSystem.java
        ├── Main.java
        └── BasicTests.java
```

## Cómo compilar y ejecutar

Requiere **Java 17** o superior (JDK). No tiene dependencias externas.

Desde la carpeta raíz del proyecto, compilar:

```bash
javac -d out src/triage/*.java
```

Ejecutar el menú de consola:

```bash
java -cp out triage.Main
```

## Pruebas

```bash
java -cp out triage.BasicTests
```

Es un programa de prueba manual: cada línea imprime el resultado esperado
junto al obtenido. Salida esperada:

```
--- Escenario principal ---
Pendientes tras registrar 4 solicitudes (esperado 4): 4
1ra atencion (esperado: A2, urgencia 1): Ticket{id=A2, nombre=Ana Gomez, urgencia=1, llegada=1}
2da atencion (esperado: A4, urgencia 1, llego despues de A2): Ticket{id=A4, nombre=Eva Leon, urgencia=1, llegada=3}
Cancelar A3 (esperado true): true
Buscar A3 tras cancelar (esperado null): null
Pendientes tras cancelar A3 (esperado 1): 1
3ra atencion (esperado: A1; A3 ya no esta): Ticket{id=A1, nombre=Juan Perez, urgencia=3, llegada=0}
4to intento (esperado: null, no quedan pendientes): null
Historial (esperado: A1, A4, A2):
  Ticket{id=A1, nombre=Juan Perez, urgencia=3, llegada=0}
  Ticket{id=A4, nombre=Eva Leon, urgencia=1, llegada=3}
  Ticket{id=A2, nombre=Ana Gomez, urgencia=1, llegada=1}

--- Casos borde ---
Urgencia 9 (esperado: error): La urgencia debe estar entre 1 y 5
Id repetido pendiente (esperado: error): Ya existe una solicitud con el id: C1
Cancelar id inexistente (esperado false): false
Reusar id ya atendido (esperado: Sofia Mora): Ticket{id=C1, nombre=Sofia Mora, urgencia=4, llegada=1}
Atender dos veces (esperado: C1 y luego null): C1, null
```

## Ejemplo básico de uso

```
Opcion: 1
ID: A1
Nombre: Juan Perez
Urgencia (1 = mas urgente, 5 = menos urgente): 3
Solicitud registrada.

Opcion: 2
Atendiendo: Ticket{id=A1, nombre=Juan Perez, urgencia=3, llegada=0}
```

## Estado de la implementación

- **Implementado:** registrar, atender por prioridad, buscar por
  identificador, cancelar, consultar el historial de atenciones y menú de
  consola.
- **Parcial:** validación de entradas (el nombre no se valida) y pruebas
  (manuales, sin aserciones automáticas).
- **Pendiente:** deshacer la última atención, índice por identificador en
  tiempo constante, pruebas automatizadas y análisis experimental de
  desempeño.

El detalle está en la sección "Estado de la implementación" de
`docs/main.pdf`.

## Limitaciones conocidas

- La búsqueda, la cancelación y la verificación de identificadores repetidos
  cuestan `Θ(n)`.
- El costo constante de la cola de prioridad depende de que la urgencia tome
  solo cinco valores.
- Los datos existen solo durante la ejecución; no hay persistencia.
- No se valida el nombre de la solicitud.

## Referencias y declaración de ayudas

- T. H. Cormen, C. E. Leiserson, R. L. Rivest y C. Stein, *Introduction to
  Algorithms*, 4th ed. MIT Press, 2022.
- M. T. Goodrich, R. Tamassia y M. H. Goldwasser, *Data Structures and
  Algorithms in Java*, 6th ed. Wiley, 2014.
- Las colas, la cola de prioridad y la pila son implementación propia; de la
  biblioteca estándar de Java solo se usa `ArrayList` para devolver el
  historial como lista.
- **Declaración de ayudas:** se utilizó Claude (Anthropic), un asistente de
  inteligencia artificial, como apoyo en la implementación del código y en la
  redacción del documento técnico. El equipo revisó, probó y comprende todo
  el código y las decisiones de diseño presentadas.
