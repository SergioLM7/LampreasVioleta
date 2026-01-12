package services;

import dao.ClienteDAO;
import dao.DetalleClienteDAO;
import db.Db;
import model.Cliente;
import model.DetalleCliente;

import java.sql.Connection;
import java.sql.SQLException;

public class ClienteDetalle {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DetalleClienteDAO detalleClienteDAO = new DetalleClienteDAO();

    /**
        Función para gestionar el guardado del Cliente con sus DetalleCliente simultáneamente
     **/
    public void guardarClienteCompleto(Cliente c, DetalleCliente dc) throws SQLException {
        try (Connection con = Db.getConnection()) {

            //Desactivar auto-commit del motor de BBDD
            con.setAutoCommit(false);

            try {
                clienteDAO.insert(c, con);
                detalleClienteDAO.insert(dc, con);

                //Consolidamos las inserciones anteriores (por haber desactivado el auto-commit)
                con.commit();

            } catch(SQLException e) {
                con.rollback();
                throw e;
            } finally {
                //Revertimos el cambio de la configuración de AutoCommit, para que se puedan guardar otras transacciones
                //en otros puntos del código de nuestra aplicación
                con.setAutoCommit(true);
            }
        }

    }


}
