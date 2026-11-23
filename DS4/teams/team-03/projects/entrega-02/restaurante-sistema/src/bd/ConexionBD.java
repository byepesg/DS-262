package bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {
    private static final String URL = "jdbc:postgresql://ep-ancient-feather-b8mebb1f.c-14.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require";
    private static final String USUARIO = "neondb_owner";
    private static final String CLAVE = "npg_yR23kCmHJKcn";
    private static Connection conexion = null;

    // Constructor privado para evitar que instancien la clase con 'new ConexionBD()'
    private ConexionBD() {
    }

    // Método público y estático (+ subrayado en UML)
    public static Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
            inicializarTablas();
        }
        return conexion;
    }

    // Método privado y estático (- subrayado en UML)
    private static void inicializarTablas() {
        String sqlRoles = """
            CREATE TABLE IF NOT EXISTS roles (
                id SERIAL PRIMARY KEY,
                nombre_rol VARCHAR(50) UNIQUE NOT NULL
            );
            """;

        String sqlPermisos = """
            CREATE TABLE IF NOT EXISTS permisos (
                id SERIAL PRIMARY KEY,
                nombre VARCHAR(50) UNIQUE NOT NULL
            );
            """;

        String sqlRolPermisos = """
            CREATE TABLE IF NOT EXISTS rol_permisos (
                rol_id INT REFERENCES roles(id) ON DELETE CASCADE,
                permiso_id INT REFERENCES permisos(id) ON DELETE CASCADE,
                PRIMARY KEY (rol_id, permiso_id)
            );
            """;

        String sqlUsuarios = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                rol_id INT REFERENCES roles(id)
            );
            """;

        String sqlProductos = """
            CREATE TABLE IF NOT EXISTS productos (
                id SERIAL PRIMARY KEY,
                nombre VARCHAR(100) NOT NULL,
                precio NUMERIC(10, 2) NOT NULL
            );
            """;

        // Datos semilla iniciales para poder iniciar sesión la primera vez, para quitar si funciona sin esto
        

        try (Statement stmt = conexion.createStatement()) {
            stmt.execute(sqlRoles);
            stmt.execute(sqlPermisos);
            stmt.execute(sqlRolPermisos);
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlProductos);
        } catch (SQLException e) {
            System.err.println("Error al inicializar las tablas en la BD: " + e.getMessage());
        }
    }
}