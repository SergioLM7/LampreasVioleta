package utils;

import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapsUtils {

    public static ClienteCompletoDTO mapClienteDetalleCompleto(Cliente c, DetalleCliente d) {
        return new ClienteCompletoDTO(
                c.getId(),
                c.getNombre(),
                c.getEmail(),
                d.getDireccion(),
                d.getTelefono(),
                d.getNotas());
    }

    public static  Cliente mapClienteRow(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email")
        );
    }

    /**
     * Convierte una fila de ResultSet en un objeto DetalleCliente.
     */
    public static  DetalleCliente mapDetalleClienteRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String direccion = rs.getString("direccion");
        String telefono = rs.getString("telefono");
        String notas = rs.getString("notas");

        return new DetalleCliente(id, direccion, telefono, notas);
    }

    public static Repartidor mapRepartidorRow(ResultSet rs) throws SQLException {
        return new Repartidor(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("telefono")
        );
    }

    public static  Comercial mapComercialRow(ResultSet rs) throws SQLException {
        return new Comercial(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email")
        );
    }
}
