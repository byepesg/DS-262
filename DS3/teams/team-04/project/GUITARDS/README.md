# FretGraph Engine — Motor Computacional de Teoría Musical y Voicings para Guitarra

## Descripción

FretGraph Engine es un motor de software (Developer Tooling) desarrollado en Java que modela el mástil de la guitarra como una estructura de datos procesable. Permite consultar notas en tiempo O(1), generar escalas y acordes mediante conjuntos, y calcular las mejores digitaciones (voicings) de un acorde aplicando restricciones físicas (ventanas de trastes) y heurísticas musicales.

A diferencia de las aplicaciones comerciales que operan como "cajas negras", este proyecto expone una API abierta diseñada para que otros desarrolladores puedan construir herramientas de análisis musical, generadores de tablaturas o sistemas de recomendación sobre él.

Este proyecto fue desarrollado como parte de la asignatura **Estructuras de Datos** de la Universidad Nacional de Colombia, semestre 2026-2.

## Integrantes

- Samuel Stiven Escobar Rodriguez — sescobarro@unal.edu.co
- Cristian Daniel Silva Rodriguez — crsilva@unal.edu.co
- Santiago Cedeño Sabogal — scedeno@unal.edu.co

## Estructuras de datos utilizadas

Todas las estructuras principales utilizan la API estándar de Java (Java Collections Framework), seleccionadas estratégicamente según la complejidad algorítmica requerida para cada operación.

| Estructura | Uso en el Proyecto | Representación / Implementación | Complejidad Principal |
|------------|--------------------|--------------------------------|-----------------------|
| `Note[][]` (Matriz) | Precomputación de las notas en el mástil (cuerda x traste) | Array bidimensional | Acceso (`noteAt`): O(1) |
| `HashSet<Note>` | Almacenamiento de escalas y acordes sin duplicados | Tabla Hash (Conjunto) | Búsqueda (`contains`): O(1) promedio |
| `PriorityQueue<VoicingScore>` | Ranking dinámico de las mejores digitaciones encontradas | Min-Heap (Cola de prioridad) | Inserción / Extracción: O(log N) |
| `ArrayList<int[]>` | Almacenamiento temporal de las posiciones de un voicing durante el backtracking | Array dinámico | Inserción/Remoción al final: O(1) amortizado |
| `HashMap<Integer, String>` | Mapeo de semitonos a nombres técnicos de intervalos | Tabla Hash (Diccionario) | Búsqueda (`get`): O(1) promedio |

## Algoritmos Destacados

- **Aritmética Modular:** Utilizada en la clase `Note` para la transposición de notas (lista circular de 12 semitonos).
- **Backtracking con Poda:** Utilizado en `VoicingFinder` para explorar el espacio de estados de las posiciones en el mástil, descartando ramas que superen el estiramiento máximo de dedos (`maxFretSpan`).
- **Heurística de Puntuación:** Sistema de pesos y castigos para rankear los voicings según su factibilidad física (cuerdas al aire, estiramientos) y teoría musical (presencia de la nota raíz y la tercera).

## Requisitos

- **Java JDK 17** o superior.
- No se requieren dependencias externas para la lógica central.
- JUnit 5 (solo para ejecutar la suite de pruebas).

## Estructura del proyecto

```text
FretGraphEngine/
├── README.md
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       ├── Main_proyect.java
    │       ├── model/
    │       │   ├── Note.java
    │       │   ├── Tuning.java
    │       │   ├── Fretboard.java
    │       │   ├── Interval.java
    │       │   ├── Scale.java
    │       │   ├── Chord.java
    │       │   ├── VoicingFinder.java
    │       │   └── VoicingScore.java
    │       └── util/
    │           └── MusicConstants.java
    └── test/
        └── java/
            └── model/
                ├── NoteTest.java
                ├── IntervalTest.java
                ├── FretboardTest.java
                ├── ScaleTest.java
                ├── ChordTest.java
                └── VoicingFinderTest.java

Como compilar y ejecutar

bash
mvn clean compile
mvn exec:java -Dexec.mainClass="Main_proyect"

Para ejecutar la suite de pruebas unitarias:
bash
mvn clean test
