package bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import modelo.estructuras.AVL;

public class UsuariosBD {

    public AVL<Usuario> cargarUsuariosEnAVL() {
        AVL<Usuario> usuarios = new AVL<>();
        ArrayList<Rol> rolesDisponibles = obtenerRoles();

        String sql = "SELECT id, username, password_hash, rol_id FROM usuarios";

        try {
            Connection conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    int rolId = rs.getInt("rol_id");
                    Rol rolAsignado = null;

                    if (!rs.wasNull()) {
                        for (Rol r : rolesDisponibles) {
                            if (r.getId() == rolId) {
                                rolAsignado = r;
                                break;
                            }
                        }
                    }

                    Usuario usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rolAsignado
                    );
                    usuarios.insertar(usuario);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar usuarios en el AVL: " + e.getMessage());
        }

        return usuarios;
    }

    public boolean guardarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (id, username, password_hash, rol_id) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, usuario.getId());
                pstmt.setString(2, usuario.getUsername());
                pstmt.setString(3, usuario.getPasswordHash());
                pstmt.setInt(4, usuario.getRol().getId());

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Rol> obtenerRoles() {
        ArrayList<Rol> roles = new ArrayList<>();
        String sql = """
            SELECT r.id AS rol_id, r.nombre_rol, p.id AS permiso_id, p.nombre AS nombre_permiso
            FROM roles r
            LEFT JOIN rol_permisos rp ON r.id = rp.rol_id
            LEFT JOIN permisos p ON rp.permiso_id = p.id
            ORDER BY r.id
            """;

        try {
            Connection conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    int rolId = rs.getInt("rol_id");
                    Rol rolEncontrado = null;

                    // Buscamos si el rol ya fue agregado a la lista
                    for (Rol r : roles) {
                        if (r.getId() == rolId) {
                            rolEncontrado = r;
                            break;
                        }
                    }

                    // Si no existe en la lista, lo creamos y lo agregamos
                    if (rolEncontrado == null) {
                        rolEncontrado = new Rol(rolId, rs.getString("nombre_rol"));
                        roles.add(rolEncontrado);
                    }

                    // Si la fila trae un permiso asociado, lo añadimos a ese rol
                    int permisoId = rs.getInt("permiso_id");
                    if (!rs.wasNull()) {
                        rolEncontrado.agregarPermiso(new Permiso(permisoId, rs.getString("nombre_permiso")));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener roles y permisos: " + e.getMessage());
        }

        return roles;
    }

    public int guardarRol(String nombreRol) {
        String sql = """
            INSERT INTO roles (nombre_rol) VALUES (?)
            ON CONFLICT (nombre_rol) DO UPDATE SET nombre_rol = EXCLUDED.nombre_rol
            RETURNING id
            """;
        try {
            Connection conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nombreRol);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar rol: " + e.getMessage());
        }
        return -1;
    }

    // Guarda un permiso y devuelve el ID generado por PostgreSQL (-1 si ya existía o falló)
    public int guardarPermiso(String nombrePermiso) {
        String sql = """
            INSERT INTO permisos (nombre) VALUES (?)
            ON CONFLICT (nombre) DO UPDATE SET nombre = EXCLUDED.nombre
            RETURNING id
            """;

        try {
            Connection conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nombrePermiso);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar permiso: " + e.getMessage());
        }
        return -1;
    }

    // Asocia un permiso existente a un rol existente en la tabla rol_permisos
    public boolean asignarPermisoARol(int rolId, int permisoId) {
        String sql = "INSERT INTO rol_permisos (rol_id, permiso_id) VALUES (?, ?) ON CONFLICT DO NOTHING";

        try {
            Connection conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, rolId);
                pstmt.setInt(2, permisoId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al asignar permiso al rol: " + e.getMessage());
            return false;
        }
    }

    // Asigna o actualiza el rol de un usuario usando sus IDs
    public boolean asignarRol(int usuarioId, int rolId) {
        String sql = "UPDATE usuarios SET rol_id = ? WHERE id = ?";

        try {
            Connection conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, rolId);
                pstmt.setInt(2, usuarioId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al asignar rol al usuario: " + e.getMessage());
            return false;
        }
    }
}