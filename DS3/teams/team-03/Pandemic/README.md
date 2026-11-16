# Pandemic — Segunda entrega

Juego cooperativo local para 2 o 3 jugadores. Abre `Pandemic.exe` en Windows de 64 bits, elige la cantidad de jugadores y pulsa **Nueva partida**.

Cada jugador recibe al azar un rol distinto. El encabezado indica el número del jugador activo. El mapa ocupa el área principal. Selecciona una ciudad para consultar sus conexiones o usa **Ampliar mapa**. En **Elige una acción**, selecciona lo que quieres hacer y pulsa **Ejecutar**. El desplegable incluye las habilidades que ofrece el rol en turno. Pulsa **Mano, equipo y registro** para abrir el panel lateral. Al ejecutar **Terminar acciones**, el motor resuelve las fases restantes y ofrece pasar al siguiente jugador.

El menú reproduce desde el segundo 5 la pista proporcionada, `Alexander Perls- STORM - toohappyy.mp3`. La pista queda incluida como `musica_menu.mp3`.

## Código fuente

- `pandemic.py` abre el menú principal y conecta la interfaz Tkinter con el juego.
- `musica.py` controla el audio del menú.
- `tablero.py` dibuja el mapa con los datos de ciudades y conexiones enviados por el motor. Sus coordenadas se usan para ubicar los nodos en pantalla.
- `src/` contiene el motor Java del proyecto y la adaptación de sus entradas y salidas para la interfaz gráfica.
- `Compilar.ps1` prepara el JAR, el entorno Java y el ejecutable.

## Compilar en Windows

Usa Python 3.11 de 64 bits con Tkinter y un JDK 21 de 64 bits. Abre PowerShell en la carpeta del proyecto y ejecuta lo siguiente con la ruta de tu JDK.

```powershell
python -m pip install pyinstaller==6.17.0
.\Compilar.ps1 -Jdk 'C:\ruta\al\jdk-21'
```

El ejecutable generado queda en `dist\Pandemic.exe`. Para probar cambios en Python desde el código fuente, añade `-SoloMotor` al comando de compilación y ejecuta `python .\pandemic.py`. Para abrir la variante de consola después de compilar el motor, ejecuta `.\runtime\bin\java.exe -cp motor.jar juego.Main`.

El ejecutable de la entrega incluye un entorno creado con Eclipse Temurin 21.0.13+9. Los avisos de ese entorno se incluyen en `runtime/legal` dentro del ejecutable. [Código fuente de OpenJDK correspondiente](https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.13%2B9/OpenJDK21U-jdk-sources_21.0.13_9.tar.gz).

## Pruebas reproducibles

Desde la carpeta que contiene este README, usa el mismo JDK que para compilar el juego. Sustituye la ruta de ejemplo por la de tu instalación.

~~~powershell
$Jdk = 'C:\ruta\al\jdk-21'
.\Compilar.ps1 -Jdk $Jdk -SoloMotor
& "$Jdk\bin\javac.exe" --release 17 -encoding UTF-8 -cp motor.jar -d build/tests tests/pruebas/PruebasEntrega2.java
& .\runtime\bin\java.exe '-Dstdout.encoding=UTF-8' -cp 'motor.jar;build/tests' pruebas.PruebasEntrega2
~~~

El resultado esperado es **12/12**. Los casos comprueban la pila, la cola, el AVL, el mapa, el directorio, el reparto inicial, el límite de jugadores, los descartes, Pronóstico, los brotes, los centros y las curas. La guía de sustentación incluida en el ZIP presenta los pasos para probar el flujo gráfico.

## Operaciones y alcance

- El AVL busca por nombre con O(log V) comparaciones y lista las V ciudades en O(V).
- El arreglo de vecinos permite comprobar una conexión en O(d), con d igual al grado de la ciudad. La cola enlazada organiza los brotes en orden FIFO.
- El descarte de infección da acceso por índice en O(1). Retirar una carta puede desplazar hasta m descartes, con costo O(m).
- El motor mantiene un contador agregado de cubos por ciudad. La erradicación, el agotamiento de cubos, la dificultad configurable y la pista original de infección quedan para la entrega final.

## Repositorio del curso

Grupo DS3, equipo 03. La Entrega 1 está registrada en la carpeta del proyecto. Para la siguiente contribución, usa esa misma ruta en la rama del equipo y prepara un pull request hacia main. Publica las fuentes, los casos de prueba y su documentación. Usa el ejecutable del ZIP para la demostración local. [Versión de la Entrega 1](https://github.com/byepesg/DS-262/commit/b290af1e6eb475b02d07d6c552118b20a64f5297) · [Guía de contribución](https://github.com/byepesg/DS-262/blob/main/CONTRIBUTING.md).


conocidoComo https://github.com/conocidoComo
Allobatess https://github.com/Allobatess
edmuc https://github.com/edmuc
