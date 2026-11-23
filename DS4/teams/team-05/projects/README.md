# Sistema de Procesamiento de Señales sEMG y Simulación Cinemática 3D para una Prótesis Transradial

**Asignatura:** Estructuras de Datos (Semestre 2026-2) — Universidad Nacional de Colombia, Sede Bogotá  
**Docente:** Sebastián Yepes García  
**Grupo:** `DS4` | **Equipo:** `team-05`  
**Ruta en el repositorio:** `DS4/teams/team-05/projects/`

## Integrantes

| Integrante | Correo Institucional |
| :--- | :--- |
| Franklin Orlando Delgado Ramírez | `frdelgado@unal.edu.co` |
| Yoseth Santiago Cárdenas Clavijo | `yocardenasc@unal.edu.co` |
| Camilo Andrés Triana Ortiz | `ctrianao@unal.edu.co` |

---

## Descripción General

Sistema computacional determinista para procesar señales electromiográficas de superficie (sEMG) almacenadas en archivos `.fif` (*Gallo EMG Dataset* / *Nature Scientific Data*), extraer la envolvente de activación muscular mediante ventanas temporales deslizantes (RMS), asociar canales con grados de libertad mediante una Tabla Hash propia y propagar transformaciones espaciales $4 \times 4$ sobre un modelo 3D articulado de 17 nodos (muñeca, palma y cinco cadenas falángicas).

**Restricción fundamental cumplida:** Todas las estructuras de datos nucleares (**Cola Circular**, **Tabla Hash con encadenamiento separado y rehashing**, **Árbol N-ario con arreglo dinámico propio**, **Cola enlazada BFS** y **Matriz Homogénea $4 \times 4$**) están implementadas desde cero, libres de `dict` o `set` en la lógica algorítmica central.

---

## Evolución del Proyecto: Entrega 1 (MVP) $\rightarrow$ Entrega 2 (Sistema Funcional)

| Componente | Entrega 1 (MVP Inicial) | Entrega 2 (Sistema Funcional Actual) |
| :--- | :--- | :--- |
| **Lenguaje y ecosistema** | Prototipo base en Java (JDK 17+) por consola. | Migración justificada a **Python 3.10+** para lectura nativa de archivos `.fif` (`mne`) y renderizado OpenGL ligero (`vispy`), conservando implementación propia de las estructuras de datos. |
| **Ingesta y Señal (RF-01 / RF-02)** | Lectura simulada con arreglos de prueba y RMS básico. | `LectorFIF.py` multicanal sobre `.fif`, `ColaCircular.py` en $\Theta(1)$ y `ProcesadorEMG.py` con filtrado Notch (60 Hz) + Butterworth pasa-banda y extracción RMS/MAV. |
| **Mapeo Lógico (RF-03)** | Tabla Hash estática 1-a-1 sin redimensionamiento. | `mapeador_hash.py` con encadenamiento separado, **rehashing dinámico a números primos** ($\alpha \le 0.75$) y **sinergias musculares 1-a-$k$**. |
| **Cinemática (RF-04)** | Acumulación escalar de ángulos en preorden. | Composición completa de **matrices homogéneas $4 \times 4$ zero-allocation** (`matriz4x4.py`), `nodo_articulacion.py` (prevención de ciclos) y `arbol_cinematico.py` (17 articulaciones, preorden $\Theta(V)$ y recorrido BFS). |
| **Simulación 3D (RF-05)** | Pendiente (solo trazas de texto en consola). | **Visor 3D interactivo en VisPy/OpenGL** (`visor_protesis_3d.py`) con concurrencia **Productor–Consumidor por doble búfer** ($\Theta(1)$ en *swap*), tríadas de orientación global y fallback automático *headless*. |
| **Integración y Pruebas** | Ejecución manual de prueba. | Orquestador multicanal con autocalibración RMS (`src/main.py`) y suite automatizada `pytest` (`tests/test_visor_3d.py`). |

---

## Estructura del Repositorio

```text
DS4/teams/team-05/projects/
├── README.md                        # Documentación general, instalación y uso
├── requirements.txt                 # Dependencias del entorno virtual Python
├── .gitignore                       # Exclusión de .venv/, __pycache__/ y archivos .fif pesados
├── data/                            # Carpeta local para archivos de prueba (ej. Subject_01.fif)
├── scripts/
│   └── inspeccionar_fif.py          # Inspección de metadatos y rango dinámico de canales .fif
├── src/
│   ├── ColaCircular.py              # Módulo 1 (RF-02): Ring Buffer en Theta(1)
│   ├── LectorFIF.py                 # Módulo 1 (RF-01): Lectura multicanal de archivos .fif
│   ├── ProcesadorEMG.py             # Módulo 2 (RF-01): Filtrado digital Notch + Pasa-Banda y RMS
│   ├── mapeador_hash.py             # Módulo 3 (RF-03): Tabla Hash propia con rehashing y sinergias
│   ├── matriz4x4.py                 # Módulo 4 (RF-04): Álgebra matricial homogénea 4x4 in-place
│   ├── nodo_articulacion.py         # Módulo 4 (RF-04): Nodo del Árbol N-ario con cotas angulares
│   ├── arbol_cinematico.py          # Módulo 4 (RF-04): Árbol N-ario (17 DoF), Preorden Theta(V) y BFS
│   ├── visor_protesis_3d.py         # Módulo 5 (RF-05): Renderizado 3D VisPy y doble búfer concurrente
│   └── main.py                      # Controlador principal integrador (M1 -> M5)
└── tests/
    └── test_visor_3d.py             # Pruebas unitarias, de concurrencia e integración M1-M5
└── docs/
    └── Proyecto_Estructura_de_Datos.pdf