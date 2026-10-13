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
- **Pila** (lista enlazada de encabezado único): registrará el historial de
  atenciones y permitirá deshacer la más reciente en `Θ(1)`.

## Estructura del repositorio

```
.
├── README.md
└── src/
    └── triage/
        ├── Ticket.java
        ├── LinkedQueue.java
        └── LevelPriorityQueue.java
```

## Cómo compilar

Requiere **Java 17** o superior (JDK). No tiene dependencias externas.

Desde la carpeta raíz del proyecto:

```bash
javac -d out src/triage/*.java
```

## Estado de la implementación

- **Implementado:** clase `Ticket` (solicitud con identificador, nombre,
  urgencia y orden de llegada); cola genérica `LinkedQueue` sobre lista
  enlazada; y cola de prioridad `LevelPriorityQueue`, con una cola por nivel
  de urgencia que atiende primero el nivel más urgente y, dentro de cada
  nivel, la solicitud que llegó primero.
- **Pendiente:** registro y atención de solicitudes, búsqueda por
  identificador, cancelación, historial de atenciones con deshacer, interfaz
  de consola, pruebas y documento técnico.
