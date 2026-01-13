package dao;


import db.Db;
import model.Comercial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComercialDAO {

    private final String INSERT_SQL = "INSERT INTO comercial (id, nombre, email) VALUES (?, ?, ?, ?)";

    private final String SELECT_BY_ID_SQL = "SELECT id, nombre, email FROM comercial WHERE id = ?";

    private final String SELECT_ALL_SQL = "SELECT id, nombre, email FROM comercial ORDER BY id";

    private final String UPDATE_SQL = """
        UPDATE comercial 
        SET nombre = ?, email = ? 
        WHERE id = ?""";

    private final String DELETE_SQL = "DELETE FROM comercial WHERE id = ?";

    /**
     * Inserta un Comercial en BBDD
     * @param comercial el model con todos los datos
     * @throws SQLException si ocurre un error durante la ejecución de la consulta.
     */
    public void insert(Comercial comercial, Connection con) throws SQLException {

        try(PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {

            ps.setInt(1, comercial.getId());
            ps.setString(2, comercial.getNombre());
            ps.setString(3, comercial.getEmail());

            ps.executeUpdate();
        }
    }

    /**
     * Busca un comercial por su ID.
     * @param id el id a buscar
     * @return el model del comercial encontrado
     * @throws SQLException si ocurre un error durante la ejecución de la consulta.
     */
    public Comercial findById(int id) throws SQLException {

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
     * Busca todos los registros de la tabla comercial en base de datos.
     * @return la lista de objetos comercial encontrada; estará vacía si no se encuentra ninguno
     * @throws SQLException si ocurre un error durante la ejecución de la consulta.
     */
    public List<Comercial> findAll() throws SQLException {
        List<Comercial> list = new ArrayList<>();

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
     * Actualiza los datos de un Comercial en la base de datos
     * @param comercial el objeto que contiene los nuevos datos
     * @param con la conexión activa a la base de datos.
     * @return el número de filas afectadas; 0 si el ID no existe.
     * @throws SQLException si ocurre un error durante la ejecución de la consulta.
     */
    public int update(Comercial comercial, Connection con) throws SQLException {

        try(PreparedStatement ps = con.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, comercial.getNombre());
            ps.setString(2, comercial.getEmail());
            ps.setInt(3, comercial.getId());

            return ps.executeUpdate();
        }
    }

    /**
     * Borra un comercial concreto por su ID.
     * @param id el ID del comercial a buscar
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

    private Comercial mapRow(ResultSet rs) throws SQLException {
        return new Comercial(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email")
        );
    }
}
