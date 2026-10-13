# Sistema de Priorización de Atención (SPA)

Proyecto semestral, Entrega 1. Asignatura Estructuras de Datos, Universidad
Nacional de Colombia, semestre 2026-2.

## Integrantes

- [Nombre completo], usuario GitHub: [usuario], [correo]@unal.edu.co
- [Nombre completo], usuario GitHub: [usuario], [correo]@unal.edu.co
- [Nombre completo], usuario GitHub: [usuario], [correo]@unal.edu.co

## Descripción del proyecto

Sistema que registra, prioriza, atiende, cancela y consulta solicitudes de
atención (por ejemplo, una fila de urgencias o una mesa de ayuda técnica),
atendiendo siempre primero la solicitud de mayor urgencia en lugar de la que
llegó primero.

- **Cola de prioridad por niveles** (un arreglo con una cola FIFO por nivel
  de urgencia, cada una implementada con una lista enlazada con referencias a
  la cabeza y a la cola): decide el orden de atención. Como la urgencia solo
  toma 5 valores, registrar y atender cuestan `Θ(1)`.
- **Búsqueda por identificador**: recorre las colas de todos los niveles,
  con costo `Θ(n)` en el peor caso. También se usa para rechazar
  identificadores repetidos al registrar.
- **Cancelación**: localiza la solicitud con la misma búsqueda y elimina su
  nodo de la lista enlazada de su nivel, en `Θ(n)` en el peor caso. Una
  solicitud cancelada sale de la cola y nunca es atendida.
- **Pila** (lista enlazada de encabezado único): registra el historial de
  atenciones en `Θ(1)` por atención. Al ser LIFO, deja preparada la
  operación de deshacer la atención más reciente.

## Estructura del repositorio

```
.
├── README.md
└── src/
    └── triage/
        ├── Ticket.java
        ├── LinkedQueue.java
        ├── LevelPriorityQueue.java
        ├── LinkedStack.java
        ├── ServiceHistory.java
        ├── TriageSystem.java
        └── Main.java
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

- **Implementado:** clase `Ticket` (solicitud con identificador, nombre,
  urgencia y orden de llegada); cola genérica `LinkedQueue` sobre lista
  enlazada, recorrible sin modificarla y con eliminación de un elemento
  arbitrario; cola de prioridad `LevelPriorityQueue`, con una cola por nivel
  de urgencia que atiende primero el nivel más urgente y, dentro de cada
  nivel, la solicitud que llegó primero; pila genérica `LinkedStack` sobre
  lista enlazada; historial de atenciones `ServiceHistory`; y `TriageSystem`,
  que registra solicitudes (validando identificador, identificadores
  repetidos y urgencia entre 1 y 5), atiende siempre la de mayor prioridad,
  busca y cancela solicitudes pendientes y consulta el historial de
  atenciones; y un menú de consola (`Main`) que expone todas estas
  operaciones.
- **Pendiente:** deshacer la última atención, pruebas y documento técnico.
