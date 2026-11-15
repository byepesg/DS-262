# Detector de arritmias a partir de ECG (MIT-BIH)
## Descripción

El programa procesa registros ECG almacenados en archivos de la base de datos **MIT-BIH Arrhythmia Database** (PhysioNet). Lee los archivos del registro, decodifica la señal, carga las anotaciones de los cardiólogos y genera una imagen del electrocardiograma con sus metadatos. Es la base para los análisis posteriores: detección de picos R, frecuencia cardíaca y clasificación del ritmo.

Cada registro de MIT-BIH consta de estos archivos:

| Archivo | Contenido |
|---|---|
| `.hea` | Cabecera: frecuencia de muestreo, canales, muestras, formato, ganancia, unidades y comentarios |
| `.dat` | Muestras digitales de cada canal (formato binario 212) |
| `.atr` | Anotaciones: posición y símbolo de cada latido, y marcas especiales |
| `.xws` | Archivo auxiliar |

## Archivos del proyecto

| Archivo | Función |
|---|---|
| `Main.java` | Punto de entrada. Pide el número del registro (000-999), valida que existan sus archivos en `Muestras_Pruebas/REF_XXX/`, invoca los lectores y entrega los datos al graficador. |
| `graficador/CabeceraECG.java` | Modelo de datos de la cabecera: nombre del registro, número de canales, frecuencia de muestreo, número de muestras y lista de canales (`CanalECG`, con formato, ganancia, resolución y cero del ADC, unidades y nombre de la derivación). |
| `graficador/LectorCabecera.java` | Lee y analiza el archivo `.hea` y construye un objeto `CabeceraECG`. Valida los campos y reconoce ganancias del tipo `200(1024)/mV`. |
| `graficador/LectorSen.java` | Decodifica el archivo `.dat` en formato 212 (dos muestras de 12 bits empaquetadas en 3 bytes) y convierte los valores ADC a milivoltios: `(ADC - cero) / ganancia`. Devuelve una matriz `[canal][muestra]`. |
| `graficador/LectorAnotaciones.java` | Lee el archivo `.atr`: obtiene la posición (muestra) y el símbolo de cada anotación (`N`, `A`, `V`, `+`, etc.), incluidos los saltos largos de tiempo y las notas auxiliares. |
| `graficador/LectorAuxiliar.java` | Lee el archivo `.xws` y devuelve su contenido como líneas de texto. |
| `graficador/RegistroECG.java` | Contenedor que agrupa en un solo objeto la cabecera, las señales en mV, las anotaciones (clase interna `Anotacion`) y el contenido auxiliar. |
| `graficador/GraficadorECG.java` | Dibuja el ECG sobre papel milimetrado (25 mm/s y 10 mm/mV), con las anotaciones, el nombre de cada derivación y los metadatos del registro. Guarda la imagen en PNG y la muestra en una ventana si hay pantalla. |

## Estructura de carpetas esperada
```
Muestras_Pruebas/
  REF_100/
    100.hea
    100.dat
    100.atr
    100.xws
  REF_101/
    ...
```
Cada registro usado por el programa está en su propia carpeta `REF_XXX`. La
descarga original y sus archivos auxiliares se conservan en
`Muestras_Pruebas/mit-bih-arrhythmia-database-1.0.0/`.

## Compilar y ejecutar

Requiere JDK 11 o superior.

```bash
cd DS4/teams/team_06/projects/entrega_1/src/
javac -d bin *.java graficador/*.java
java -cp bin Main
# Ingrese el numero del registro (000-999): 100
```

También puede ejecutarse desde la carpeta padre usando:

```bash
mkdir -p AnalizadorECG/bin
javac -d AnalizadorECG/bin AnalizadorECG/*.java AnalizadorECG/graficador/*.java
java -cp AnalizadorECG/bin Main
```

En ambos casos busca los registros en `AnalizadorECG/Muestras_Pruebas`.

El programa genera la imagen PNG del ECG y la muestra en pantalla.

## Pruebas realizadas

Para comprobar que los datos se extrajeron correctamente, se graficó el ECG de los registros **100, 101, 102, 103, 104 y 105** y se comparó visualmente con la gráfica del mismo registro publicada por MIT-BIH/PhysioNet. En cada caso se verificó que coincidieran la forma de la señal, la posición de los picos R y los símbolos de las anotaciones. La comparación permitió detectar y corregir un error en la decodificación del segundo canal del formato 212.

## Estado del proyecto (Entrega 1)

- Implementado: lectura de `.hea`, `.dat` (formato 212), `.atr` y `.xws`; modelo de datos del registro; gráfica del ECG con anotaciones.
- Pendiente: filtrado, detección de picos R, cálculo de intervalos RR y frecuencia cardíaca, validación contra las anotaciones y clasificación del ritmo.

## Referencias

1. G. Moody and R. Mark, "MIT-BIH Arrhythmia Database," *PhysioNet*, version 1.0.0, feb. 2005. doi: [10.13026/C2F305](https://doi.org/10.13026/C2F305)
2. T. Pollard, B. E. Moody, L. H. Lehman, B. J. Gow, C. Fernandes, C. Xie, A. Johnson, R. G. Mark, and T. Heldt, "PhysioNet as a global platform for biomedical research," *Nature Health*, vol. 1, no. 8, pp. 792-795, ago. 2026. doi: [10.1038/s44360-026-00096-z](https://doi.org/10.1038/s44360-026-00096-z)
3. MIT-BIH Arrhythmia Database, PhysioNet: <https://physionet.org/content/mitdb/1.0.0/>
