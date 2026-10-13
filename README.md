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

- **Cola de prioridad** (heap binario mínimo sobre arreglo dinámico): decide
  el orden de atención. `O(log n)` para insertar y atender.
- **Tabla hash** (`HashMap<String, Ticket>`): permite buscar una solicitud
  por su identificador en tiempo esperado `O(1)`.
- **Pila** (lista enlazada de encabezado único): registra el historial de
  atenciones y permite deshacer la más reciente. `O(1)` por operación.

El documento técnico completo, con la justificación de cada decisión de
diseño, está en `docs/main.tex` (compilar con pdfLaTeX, por ejemplo en
Overleaf).

## Estructura del repositorio

```
.
├── README.md
├── docs/
│   ├── main.tex
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
        ├── LinkedStack.java
        ├── HeapPriorityQueue.java
        ├── ServiceHistory.java
        ├── TriageSystem.java
        ├── Main.java
        └── BasicTests.java
```

## Cómo compilar y ejecutar

Compilar:

```bash
cd src
javac triage/*.java
```

Ejecutar la demostración interactiva por consola:

```bash
java triage.Main
```

Ejecutar el escenario de prueba automático (sin interacción, imprime el
resultado esperado de cada operación junto al obtenido):

```bash
java triage.BasicTests
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

Ver `docs/main.tex`, sección "Estado de la implementación".
