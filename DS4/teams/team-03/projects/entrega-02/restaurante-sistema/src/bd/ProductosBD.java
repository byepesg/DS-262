package bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.entidades.Plato;

public class ProductosBD {

    public ArrayList<Plato> cargarProductos() {
        ArrayList<Plato> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, precio FROM productos ORDER BY id";

        try {
            Connection conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Plato plato = new Plato(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio")
                    );
                    productos.add(plato);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }

        return productos;
    }

    public int guardarProducto(Plato plato) {
        String sql = "INSERT INTO productos (nombre, precio) VALUES (?, ?) RETURNING id";
        try {
            Connection conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, plato.getNombre());
                pstmt.setDouble(2, plato.getPrecio());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
        }
        return -1;
    }

    public boolean eliminarProducto(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";

        try {
            Connection conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
}