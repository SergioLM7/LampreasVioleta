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

    /**
     * Función para gestinar la actualización de cualquier campo del Cliente junto a su DetalleCliente simultáneamente
     **/
    public void updateClienteCompleto(Cliente c, DetalleCliente dc) throws SQLException {

        try(Connection con = Db.getConnection()) {

            con.setAutoCommit(false);

            try {
                clienteDAO.update(c, con);

                if(detalleClienteDAO.findById(c.getId()) == null) {
                    detalleClienteDAO.insert(dc, con);
                } else {
                    detalleClienteDAO.update(dc, con);
                }

                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw  e;
            } finally {
                con.setAutoCommit(true);
            }

        }

    }

    public int deleteClienteCompleto(int id) throws SQLException {
        int deleteOperationResult;
        try(Connection con = Db.getConnection()) {
            con.setAutoCommit(false);

            try {
                int deleteDetalleClienteResult = detalleClienteDAO.deleteById(id, con);
                int deleteClienteResult = clienteDAO.deleteById(id);

                if (deleteDetalleClienteResult == 0 && deleteClienteResult == 0) {
                    deleteOperationResult = 0;
                } else {
                    deleteOperationResult = deleteClienteResult;
                }
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }

        return deleteOperationResult;
    }


}
