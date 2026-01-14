package services;

import dao.ClienteDAO;
import dao.DetalleClienteDAO;
import db.Db;
import model.Cliente;
import model.ClienteCompletoDTO;
import model.DetalleCliente;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static utils.MapsUtils.mapClienteDetalleCompleto;

public class ClienteDetalle {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DetalleClienteDAO detalleClienteDAO = new DetalleClienteDAO();

    /**
     * Función para gestionar el guardado del Cliente con sus DetalleCliente simultáneamente
     * @param c el modelo del cliente a guardar
     * @param dc el modelo del detalle cliente a guardar
     * @throws SQLException si ocurre un error durante la ejecución de la consulta.
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
     * Función para gestionar la actualización de cualquier campo del Cliente junto a su DetalleCliente simultáneamente
     * @param c el modelo del cliente a actualizar
     * @param dc el modelo del detalle cliente a actualizar
     * @throws SQLException si ocurre un error durante la ejecución de la consulta.
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

    /**
     * Función para eliminar por completo un DetalleCliente y su correspondiente Cliente en base al id
     * @param id el identificador del cliente a eliminar
     * @return un valor número; 0 en caso de que no existiera el id a borrar; y 1 si se ha borrado el cliente
     * @throws SQLException si ocurre un error durante la ejecución de la consulta.
     */
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

    /**
     * Busca en base de datos todos los clientes y todos los detalle cliente y los unifica en un DTO
     * @return lista de los clientes encontrados en BBDD con sus detalles cliente
     * @throws SQLException si ocurre un error durante la ejecución de la consulta.
     */
    public List<ClienteCompletoDTO> unirClienteCompleto() throws SQLException {
        List<ClienteCompletoDTO> unirClienteCompleto = new ArrayList<>();

        List<Cliente> clientes = clienteDAO.findAll();
        List<DetalleCliente> detalleClientes = detalleClienteDAO.findAll();

        for(Cliente cc : clientes) {
            for(DetalleCliente dc : detalleClientes) {
                if(cc.getId().equals(dc.getId())) {
                    unirClienteCompleto.add(mapClienteDetalleCompleto(cc, dc));
                }
            }
        }

        return unirClienteCompleto;
    }

}
