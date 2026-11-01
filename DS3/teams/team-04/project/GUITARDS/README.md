# BankSim — Simulador de Atención Bancaria con Múltiples Cajeros

## Descripción

BankSim es un simulador de atención bancaria desarrollado en Java que modela la llegada de clientes a un banco con múltiples cajeros. Los clientes pueden ser **normales** (atendidos en orden FIFO) o **prioritarios** (atendidos antes que los normales). El simulador permite configurar el número de cajeros, la tasa de llegada de clientes, la proporción de clientes prioritarios y la duración de la simulación. Al finalizar, muestra estadísticas como el número de clientes atendidos y el tiempo promedio de espera.

Este proyecto fue desarrollado como parte de la asignatura **Estructuras de Datos** de la Universidad Nacional de Colombia, semestre 2026-2.

## Integrantes

- Samuel Stiven Escobar Rodriguez — sescobarro@unal.edu.co
- Cristian Daniel Silva Rodriguez — crsilva@unal.edu.co
- Santiago Cedeño Sabogal — scedeno@unal.edu.co

## Estructuras de datos utilizadas

Todas las estructuras fueron implementadas desde cero, sin usar librerías externas de Java (no se usan `ArrayList`, `LinkedList`, `Queue` ni `PriorityQueue` de la API estándar).

| Estructura | Uso | Representación | Complejidad principal |
|------------|-----|----------------|----------------------|
| `Queue<T>` | Cola de clientes normales (FIFO) | Circular array | enqueue/dequeue: Θ(1) |
| `PriorityQueue<T>` | Cola de clientes prioritarios | Lista ordenada | insert: Θ(n), extractMin: Θ(1) |
| `DynamicArray<T>` | Lista de cajeros e historial de atenciones | Array dinámico | get/set: Θ(1), addLast: Θ(1) amortizado |

## Requisitos

- **Java JDK 8** o superior.
- No se requieren dependencias externas.

## Estructura del proyecto
Banksim/
├── ProyectPDF.pdf
├── README.md
└── src/
├── BanksimMain.java
├── Simulador.java
├── Cliente.java
├── Cajero.java
├── Atencion.java
├── Queue.java
├── PriorityQueue.java
└── DynamicArray.java

## Cómo compilar y ejecutar
Desde la carpeta `src/`:

```bash
javac *.java

java BanksimMain

