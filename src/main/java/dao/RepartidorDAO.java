package dao;

import db.Db;
import model.Repartidor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepartidorDAO {

    private final String INSERT_SQL = "INSERT INTO repartidor (id, nombre, telefono) VALUES (?, ?, ?, ?)";

    private final String SELECT_BY_ID_SQL = "SELECT id, nombre, telefono FROM repartidor WHERE id = ?";

    private final String SELECT_ALL_SQL = "SELECT id, nombre, telefono FROM repartidor ORDER BY id";

    private final String UPDATE_SQL = """
        UPDATE repartidor
        SET nombre = ?, telefono = ?
        WHERE id = ?""";

    private final String DELETE_SQL = "DELETE FROM repartidor WHERE id = ?";

    /**
     * Inserta un Repartidor en BBDD
     * @param repartidor el model con todos los datos
     * @throws SQLException si ocurre un error durante la ejecución de la consulta.
     */
    public void insert(Repartidor repartidor, Connection con) throws SQLException {

        try(PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {

            ps.setInt(1, repartidor.getId());
            ps.setString(2, repartidor.getNombre());
            if(repartidor.getTelefono() == null || repartidor.getTelefono().isBlank()) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, repartidor.getTelefono());
            }

            ps.executeUpdate();
        }
    }

    /**
     * Busca un repartidor por su ID.
     * @param id el id a buscar
     * @return el model del repartidor encontrado
     * @throws SQLException si ocurre un error durante la ejecución de la consulta.
     */
    public Repartidor findById(int id) throws SQLException {

        try(Connection con = Db.getConnection();
                PreparedStatement ps = con.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return mapRow(rs);
                }
            }

            return null;
        }
    }

    /**
     * Busca todos los registros de la tabla repartidor en base de datos.
     * @return la lista de objetos repartidor encontrada; estará vacía si no se encuentra ninguno
     * @throws SQLException si ocurre un error durante la ejecución de la consulta.
     */
    public List<Repartidor> findAll() throws SQLException {
        List<Repartidor> list = new ArrayList<>();

        try(Connection con = Db.getConnection();
                PreparedStatement ps = con.prepareStatement(SELECT_ALL_SQL);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    /**
     * Actualiza los datos de un Repartidor en la base de datos
     * @param repartidor el objeto que contiene los nuevos datos
     * @param con la conexión activa a la base de datos.
     * @return el número de filas afectadas; 0 si el ID no existe.
     * @throws SQLException si ocurre un error durante la ejecución de la consulta.
     */
    public int update(Repartidor repartidor, Connection con) throws SQLException {

        try(PreparedStatement ps = con.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, repartidor.getNombre());
            if(repartidor.getTelefono() == null || repartidor.getTelefono().isBlank()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, repartidor.getTelefono());
            }
            ps.setInt(3, repartidor.getId());

            return ps.executeUpdate();
        }
    }

    /**
     * Borra un repartidor concreto por su ID.
     * @param id el ID del repartidor a buscar
     * @param con el objeto de la conexión activa con la base de datos
     * @return si el ID no existe, devuelve 0; sino el número de filas afectadas
     * @throws SQLException si ocurre un error durante la ejecución de la consulta.
     */
    public int deleteById(int id, Connection con) throws SQLException  {

        try(PreparedStatement ps = con.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, id);

            return ps.executeUpdate();
        }

    }

    private Repartidor mapRow(ResultSet rs) throws SQLException {
        return new Repartidor(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("telefono")
        );
    }
    
}
