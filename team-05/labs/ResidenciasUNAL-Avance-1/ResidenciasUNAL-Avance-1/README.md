# Residencias UNAL para Apache NetBeans 30

Proyecto Maven preparado para abrir directamente en Apache NetBeans 30 y compilar con JDK 21.

## Abrir en NetBeans
1. Descomprima el ZIP.
2. Abra Apache NetBeans 30.
3. Seleccione **File > Open Project**.
4. Elija la carpeta `ResidenciasUNAL-NetBeans30`.
5. Espere a que Maven resuelva JUnit.
6. Use **Run Project (F6)** para abrir el menú.
7. Use **Test Project (Alt+F6)** para ejecutar las pruebas.

## Requisitos
- Apache NetBeans 30.
- JDK 21 configurado como plataforma del proyecto.
- Conexión a Internet la primera vez para descargar dependencias Maven.

## Comandos equivalentes
```bash
mvn clean test
mvn exec:java
mvn clean package
java -jar target/residencias-unal-1.0-SNAPSHOT.jar
```

## Estructura
- `src/main/java`: código del sistema.
- `src/test/java`: pruebas JUnit 5.
- `pom.xml`: compilación, pruebas y JAR ejecutable.
- `nbactions.xml`: acciones Run, Test, Clean y Build de NetBeans.

## Integrantes
Laura Flores, Juan Manrique y Jhon Berdugo.
