package app;

import dao.ClienteDAO;
import dao.DetalleClienteDAO;
import model.Cliente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.DetalleCliente;
import services.ClienteDetalle;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Vista JavaFX para gestionar clientes.
 *
 * Versión completa que permite
 *  - Usa ClienteDAO y DetalleClienteDAO con sus respectivos CRUD.
 *  - La tabla muestra datos de Cliente y los datos de su respectivo DetalleCliente.
 */
public class ClientesView {

    private final BorderPane root = new BorderPane();

    // Tabla y datos
    private final TableView<Cliente> tabla = new TableView<>();
    private final ObservableList<Cliente> datos = FXCollections.observableArrayList();

    // Campos de formulario (Cliente)
    private final TextField txtId = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtEmail = new TextField();

    // Caché en memoria: idCliente -> Detalle
    private final Map<Integer, DetalleCliente> cacheDetalles = new HashMap<>();

    // Campos de formulario (DetalleCliente) – por ahora solo visuales
    private final TextField txtDireccion = new TextField();
    private final TextField txtTelefono  = new TextField();
    private final TextField txtNotas     = new TextField();

    // Botones CRUD
    private final Button btnNuevo    = new Button("Nuevo");
    private final Button btnGuardar  = new Button("Guardar");
    private final Button btnBorrar   = new Button("Borrar");
    private final Button btnRecargar = new Button("Recargar");

    // Búsqueda
    private final TextField txtBuscar          = new TextField();
    private final Button    btnBuscar          = new Button("Buscar");
    private final Button    btnLimpiarBusqueda = new Button("Limpiar");

    // DAO (acceso a BD)
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DetalleClienteDAO detalleClienteDAO = new DetalleClienteDAO();

    //Service
    private final ClienteDetalle clienteService = new ClienteDetalle();

    public ClientesView() {
        configurarTabla();
        configurarFormulario();
        configurarEventos();
        recargarDatos(); // al iniciar la vista cargamos los clientes
    }

    public Parent getRoot() {
        return root;
    }

    /* =========================================================
       CONFIGURACIÓN INTERFAZ
       ========================================================= */

    private void configurarTabla() {
        TableColumn<Cliente, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()));

        TableColumn<Cliente, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));

        TableColumn<Cliente, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));

        // ===== Columnas “placeholder” para DetalleCliente =====
        TableColumn<Cliente, String> colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(c -> {
            DetalleCliente d = cacheDetalles.get(c.getValue().getId());
            String valor = (d != null) ? d.getDireccion() : "";
            return new javafx.beans.property.SimpleStringProperty(valor);
        });

        TableColumn<Cliente, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(c -> {
            DetalleCliente d = cacheDetalles.get(c.getValue().getId());
            String valor = (d != null) ? d.getTelefono() : "";
            return new javafx.beans.property.SimpleStringProperty(valor);
        });

        TableColumn<Cliente, String> colNotas = new TableColumn<>("Notas");
        colNotas.setCellValueFactory(c -> {
            DetalleCliente d = cacheDetalles.get(c.getValue().getId());
            String valor = (d != null) ? d.getNotas() : "";
            return new javafx.beans.property.SimpleStringProperty(valor);
        });

        tabla.getColumns().addAll(colId, colNombre, colEmail,
                colDireccion, colTelefono, colNotas);
        tabla.setItems(datos);

        root.setCenter(tabla);
    }

    private void configurarFormulario() {
        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(10);
        form.setVgap(10);

        // ----- Cliente -----
        txtId.setPromptText("ID (entero)");
        txtNombre.setPromptText("Nombre");
        txtEmail.setPromptText("Email");

        form.add(new Label("ID:"), 0, 0);
        form.add(txtId, 1, 0);
        form.add(new Label("Nombre:"), 0, 1);
        form.add(txtNombre, 1, 1);
        form.add(new Label("Email:"), 0, 2);
        form.add(txtEmail, 1, 2);

        // ----- DetalleCliente -----
        txtDireccion.setPromptText("Dirección");
        txtTelefono.setPromptText("Teléfono");
        txtNotas.setPromptText("Notas");

        form.add(new Label("Dirección:"), 0, 3);
        form.add(txtDireccion, 1, 3);
        form.add(new Label("Teléfono:"), 0, 4);
        form.add(txtTelefono, 1, 4);
        form.add(new Label("Notas:"), 0, 5);
        form.add(txtNotas, 1, 5);

        // Zona botones CRUD
        HBox botonesCrud = new HBox(10, btnNuevo, btnGuardar, btnBorrar, btnRecargar);
        botonesCrud.setPadding(new Insets(10, 0, 0, 0));

        // Zona de búsqueda
        HBox zonaBusqueda = new HBox(10,
                new Label("Buscar:"), txtBuscar, btnBuscar, btnLimpiarBusqueda);
        zonaBusqueda.setPadding(new Insets(10, 0, 10, 0));

        BorderPane bottom = new BorderPane();
        bottom.setTop(zonaBusqueda);
        bottom.setCenter(form);
        bottom.setBottom(botonesCrud);

        root.setBottom(bottom);
    }

    private void configurarEventos() {
        // Cuando seleccionamos una fila en la tabla, pasamos los datos al formulario
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                // Cliente
                txtId.setText(String.valueOf(newSel.getId()));
                txtNombre.setText(newSel.getNombre());
                txtEmail.setText(newSel.getEmail());
                txtId.setDisable(true); // al editar, de momento, no dejamos cambiar el ID

                // DetalleCliente
                try {
                    DetalleCliente detalleCliente = detalleClienteDAO.findById(newSel.getId());
                    if(detalleCliente != null) {
                        txtDireccion.setText(detalleCliente.getDireccion() != null ? detalleCliente.getDireccion() : "");
                        txtTelefono.setText(detalleCliente.getTelefono() != null ? detalleCliente.getTelefono() : "");
                        txtNotas.setText(detalleCliente.getNotas() != null ? detalleCliente.getNotas() : "");
                    } else {
                        txtDireccion.clear();
                        txtTelefono.clear();
                        txtNotas.clear();
                    }

                } catch (SQLException e) {
                    mostrarError("Error al recuperar detalles", e);
                } catch (Exception ex) {
                    mostrarError("Error ", ex);
                }
            }
        });

        btnNuevo.setOnAction(e -> limpiarFormulario());

        btnGuardar.setOnAction(e -> guardarCliente());

        btnBorrar.setOnAction(e -> borrarClienteSeleccionado());

        btnRecargar.setOnAction(e -> {
            txtBuscar.clear();
            recargarDatos();
        });

        btnBuscar.setOnAction(e -> buscarClientesEnBBDD());

        btnLimpiarBusqueda.setOnAction(e -> {
            txtBuscar.clear();
            recargarDatos();
        });
    }

    /* =========================================================
       LÓGICA DE NEGOCIO (usando ClienteDAO actual)
       ========================================================= */

    /**
     * Carga todos los clientes desde la BD usando ClienteDAO.findAll()
     */
    private void recargarDatos() {
        try {
            //1. Cargar datos de todos los clientes
            List<Cliente> clientes = clienteDAO.findAll();

            //2. Cargar todos los detalles de los clientes
            List<DetalleCliente> detallesClientes = detalleClienteDAO.findAll();

            //3. Rellenar la caché id -> detalle
            cacheDetalles.clear();
            for(DetalleCliente d : detallesClientes) {
                cacheDetalles.put(d.getId(), d);
            }

            //4. Refrescar la tabla
            datos.setAll(clientes);
        } catch (SQLException e) {
            mostrarError("Error al cargar clientes", e);
        }
    }

    /**
     * Búsqueda de momento hecha EN MEMORIA.
     *
     * Se carga toda la lista (findAll) y se filtra con streams.
     * Más adelante se puede cambiar para que use ClienteDAO.search()
     * cuando lo implementéis.
     */
    private void buscarClientesEnMemoria() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) {
            recargarDatos();
            return;
        }

        try {
            List<Cliente> lista = clienteDAO.findAll();
            String f = filtro.toLowerCase();

            List<Cliente> filtrados = lista.stream()
                    .filter(c ->
                            String.valueOf(c.getId()).contains(f) ||
                                    c.getNombre().toLowerCase().contains(f) ||
                                    c.getEmail().toLowerCase().contains(f)
                    )
                    .collect(Collectors.toList());

            datos.setAll(filtrados);
        } catch (SQLException e) {
            mostrarError("Error al buscar clientes", e);
        }
    }

    /**
     * Búsqueda de clientes en BBD.
     *
     * Se busca en todos los campos de cada Cliente en base a si contienen el texto
     * que haya en el campo txtBuscar de la interfaz gráfica
     */
    private void buscarClientesEnBBDD(){
        //Se coge lo que haya en la casilla donde se escribe (txtBuscar en este caso de JavaFX)
        String filtro = txtBuscar.getText().trim();

        if ((filtro.isEmpty())){
            recargarDatos();
            return;
        }

        try {
            List<Cliente> lista = clienteDAO.search(filtro);
            datos.setAll(lista);
        } catch (SQLException e){
            mostrarError("Error al buscar", e);
        }

    }

    private void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        txtEmail.clear();
        txtDireccion.clear();
        txtTelefono.clear();
        txtNotas.clear();
        txtId.setDisable(false);
        tabla.getSelectionModel().clearSelection();
    }

    /**
     * Guardar cliente y sus detalles:
     *  - Si no existe en la BD → INSERT usando el service guardarClienteCompleto
     *  - Si existe → actualizar los datos con los nuevos inputs con el service actualizarClienteCompleto
     */
    private void guardarCliente() {
        // Validación rápida
        if (txtId.getText().isBlank() ||
                txtNombre.getText().isBlank() ||
                txtEmail.getText().isBlank()) {

            mostrarAlerta("Campos obligatorios",
                    "Debes rellenar ID, nombre y email.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarAlerta("ID inválido", "El ID debe ser un número entero.");
            return;
        }

        // Cliente con ID escrito por el usuario
        Cliente c = new Cliente(id,
                txtNombre.getText().trim(),
                txtEmail.getText().trim());

        // DetalleCliente con el mismo ID
        DetalleCliente d = new DetalleCliente(id,
                txtDireccion.getText().trim(),
                txtTelefono.getText().trim(),
                txtNotas.getText().trim());

        try {
            Cliente existente = clienteDAO.findById(id);

            if (existente == null) {
                // No existe → INSERT
                clienteService.guardarClienteCompleto(c, d);

                mostrarInfo("Insertado", "Cliente y detalle creados correctamente (con transacción OK).");
            } else {
                // Ya existe → UPDATE
                clienteService.updateClienteCompleto(c, d);
                mostrarInfo("Actualizado",
                        "El cliente " + c.getNombre() + " se ha actualizado correctamente.");
            }

            recargarDatos();
            limpiarFormulario();

        } catch (SQLException e) {
            mostrarError("Error al guardar cliente", e);
        }
    }

    /**
     * Borrar cliente seleccionado.
     *
     * Borra un cliente por su ID: primero sus DetalleCliente y luego el Cliente.
     */
    private void borrarClienteSeleccionado() {
        Cliente sel = tabla.getSelectionModel().getSelectedItem();
        int borrado;

        if (sel == null) {
            mostrarAlerta("Sin selección", "Selecciona un cliente en la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar borrado");
        confirm.setHeaderText("¿Eliminar cliente?");
        confirm.setContentText("Se borrará el cliente con ID " + sel.getId());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            borrado = clienteService.deleteClienteCompleto(sel.getId());

            if(borrado > 0) {
                mostrarInfo("Borrado correcto", "El cliente " + sel.getNombre() + " se ha borrado correctamente.");
                recargarDatos();
                limpiarFormulario();
            } else {
                mostrarAlerta("Borrado incorrecto", "No se encontró el cliente en la BBDD.");
            }
        } catch (SQLException e) {
            mostrarError("Error al borrar cliente", e);
        }

    }

    /* =========================================================
       DIÁLOGOS AUXILIARES
       ========================================================= */

    private void mostrarError(String titulo, Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
