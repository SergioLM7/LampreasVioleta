package dao;
// Paquete donde vive esta clase. Normalmente 'dao' agrupa los Data Access Objects,
// clases dedicadas exclusivamente a hablar con la base de datos.

import db.Db;
// Clase que gestiona la obtención de conexiones JDBC (probablemente un método estático getConnection()).

import model.Cliente;
// Modelo/entidad Cliente. Representa una fila de la tabla 'cliente'.

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// Imports necesarios para el uso del API JDBC de Java.

import java.util.ArrayList;
import java.util.List;
// Usamos listas dinámicas para devolver varios clientes cuando hacemos un SELECT *.

public class ClienteDAO {
    // Clase DAO que contiene la lógica de acceso a datos para la entidad Cliente.
    // To do lo relacionado con INSERT, SELECT, UPDATE y DELETE de clientes se pone aquí.

    // ----------------------------------------------------------
    // SENTENCIAS SQL PREPARADAS COMO CONSTANTES
    // ----------------------------------------------------------

    private static final String INSERT_SQL =
            "INSERT INTO cliente (id, nombre, email) VALUES (?, ?, ?)";
    // Consulta SQL para insertar un cliente.
    // Usamos ? para parámetros → evita SQL injection y mejora rendimiento con sentencias preparadas.

    private static final String SELECT_BY_ID_SQL =
            "SELECT id, nombre, email FROM cliente WHERE id = ?";
    // Consulta SQL para buscar un cliente por su ID.

    private static final String SELECT_ALL_SQL =
            "SELECT id, nombre, email FROM cliente ORDER BY id";
    // Consulta SQL para obtener todos los clientes ordenados por id.


    private static final String SEARCH_SQL = """
                    SELECT id, nombre, email
                    FROM cliente
                    WHERE CAST(id AS TEXT) ILIKE ? 
                        OR nombre ILIKE ?  
                        OR email ILIKE ?
                    ORDER BY id                    
                    """;
    //Consulta SQL de búsqueda universal para todos los campos de todos los clientes

    private static final String UPDATE_SQL = """ 
            UPDATE cliente
            SET nombre = ?, email = ?
            WHERE id = ?
            """;
    //Consulta SQL para actualizar los datos básicos de un Cliente

    private static final String DELETE_SQL = "DELETE FROM cliente WHERE id = ?";
    //Consulta SQL para eliminar un Cliente por su ID.


    // ----------------------------------------------------------
    // MÉTODO: INSERTAR UN CLIENTE
    // ----------------------------------------------------------

    public void insert(Cliente c) throws SQLException {
        // Método público que inserta un cliente en la base de datos.
        // Recibe un objeto Cliente y lanza SQLException si algo sale mal.

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {

            // try-with-resources: la conexión y el PreparedStatement se cerrarán automáticamente
            // al final del bloque, aunque haya errores.

            ps.setInt(1, c.getId());         // Parámetro 1 → columna id
            ps.setString(2, c.getNombre());  // Parámetro 2 → columna nombre
            ps.setString(3, c.getEmail());   // Parámetro 3 → columna email

            ps.executeUpdate();
            // Ejecuta la sentencia. Como es un INSERT, no devuelve ResultSet.

        }
    }

    // ----------------------------------------------------------
    // MÉTODO: INSERTAR UN CLIENTE
    // ----------------------------------------------------------
    public void insert(Cliente c, Connection con) throws SQLException {
        // Método público que inserta un cliente en la base de datos.
        // Recibe un objeto Cliente y una Connection y lanza SQLException si algo sale mal.

        try (PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {

            // try-with-resources: el PreparedStatement se cerrarán automáticamente
            // al final del bloque, aunque haya errores.

            ps.setInt(1, c.getId());         // Parámetro 1 → columna id
            ps.setString(2, c.getNombre());  // Parámetro 2 → columna nombre
            ps.setString(3, c.getEmail());   // Parámetro 3 → columna email

            ps.executeUpdate();
            // Ejecuta la sentencia. Como es un INSERT, no devuelve ResultSet.

        }
    }


    // ----------------------------------------------------------
    // MÉTODO: BUSCAR CLIENTE POR ID
    // ----------------------------------------------------------

    public Cliente findById(int id) throws SQLException {
        // Devuelve el Cliente cuyo id coincida con el parámetro.
        // Si no existe, devuelve null.

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, id);  // Asignamos el id al parámetro ?

            try (ResultSet rs = ps.executeQuery()) {
                // executeQuery() devuelve un ResultSet ↔ una tabla virtual con las filas devueltas.

                if (rs.next()) {
                    // Si rs.next() = true → hay fila. Avanzamos a ella y leemos sus columnas.

                    return new Cliente(
                            rs.getInt("id"),          // Columna 'id'
                            rs.getString("nombre"),   // Columna 'nombre'
                            rs.getString("email")     // Columna 'email'
                    );
                }

                return null;
                // Si no hay resultado, devolvemos null para indicar "no encontrado".
            }
        }
    }


    // ----------------------------------------------------------
    // MÉTODO: LISTAR TODOS LOS CLIENTES
    // ----------------------------------------------------------

    public List<Cliente> findAll() throws SQLException {
        // Devuelve una lista con todos los clientes de la tabla.
        // Nunca devuelve null; si no hay datos, devuelve lista vacía.

        List<Cliente> out = new ArrayList<>();

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Iteramos por cada fila del ResultSet.
                out.add(mapRow(rs));   // Mapeamos el ResultSet a un objeto Cliente y lo añadimos a la lista.
            }
        }

        return out;   // Devolvemos la lista completa.
    }

    // ----------------------------------------------------------
    // MÉTODO: BÚSQUEDA UNIVERSAL EN TODOS LOS CAMPOS DE CLIENTE
    // ----------------------------------------------------------
    public List<Cliente> search(String filtro) throws SQLException {

        String patron = "%" + filtro + "%";

        try (Connection con = Db.getConnection();
           PreparedStatement pst = con.prepareStatement(SEARCH_SQL)) {
            pst.setString(1, patron);
            pst.setString(2, patron);
            pst.setString(3, patron);

            List<Cliente> out = new ArrayList<>();

            try(ResultSet rs = pst.executeQuery()){

                while (rs.next()){
                    out.add(mapRow(rs));
                }
            }
            return out;
        }
    }

    /**
     * Actualiza los datos del cliente.
     * Si id no existe, devuelve 0.
     */
    public int update(Cliente c) throws SQLException {

        try (Connection con = Db.getConnection();
        PreparedStatement ps = con.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getEmail());
            ps.setInt(3, c.getId());

            return ps.executeUpdate();
        }
    }

    /**
     * Actualiza los datos del cliente.
     * Si id no existe, devuelve 0.
     */
    public int update(Cliente c, Connection con) throws SQLException {

        try (PreparedStatement ps = con.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getEmail());
            ps.setInt(3, c.getId());

            return ps.executeUpdate();
        }
    }

    /**
     * Borra un cliente concreto por su ID.
     */
    public int deleteById(int id) throws SQLException  {

        try(Connection con = Db.getConnection();
        PreparedStatement ps = con.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, id);

            return ps.executeUpdate();
        }

    }

    /**
     * Borra un cliente concreto por su ID.
     */
    public int deleteById(int id, Connection con) throws SQLException  {

        try(PreparedStatement ps = con.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, id);

            return ps.executeUpdate();
        }

    }

    private Cliente mapRow(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email")
        );
    }


}