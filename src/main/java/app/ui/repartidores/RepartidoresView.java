package app.ui.repartidores;

import dao.RepartidorDAO;
import db.Db;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.Repartidor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static utils.AlertsUtils.mostrarAlerta;
import static utils.AlertsUtils.mostrarError;
import static utils.AlertsUtils.mostrarInfo;

/**
 * Vista JavaFX para gestionar repartidores.
 *
 * Versión completa que permite
 *  - Usa RepartidorDAO con su CRUD.
 *  - La tabla muestra datos de los repartidores.
 */
public class RepartidoresView {

    private final BorderPane root = new BorderPane();

    // Tabla y datos
    private final TableView<Repartidor> tabla = new TableView<>();
    private final ObservableList<Repartidor> datos = FXCollections.observableArrayList();

    // Campos de formulario (Repartidor)
    private final TextField txtId = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtTelefono = new TextField();

    // Botones CRUD
    private final Button btnNuevo = new Button("Nuevo");
    private final Button btnGuardar = new Button("Guardar");
    private final Button btnBorrar = new Button("Borrar");
    private final Button btnRecargar = new Button("Recargar");

    // Búsqueda
    private final TextField txtBuscar = new TextField();
    private final Button btnBuscar = new Button("Buscar");
    private final Button btnLimpiarBusqueda = new Button("Limpiar");

    // DAO (acceso a BD)
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    public RepartidoresView() {
        configurarTabla();
        configurarFormulario();
        configurarEventos();
        recargarDatos();
    }

    public BorderPane getRoot() {
        return root;
    }

    //Configuración Interfaz
    private void configurarTabla() {
        TableColumn<Repartidor, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()));

        TableColumn<Repartidor, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));

        TableColumn<Repartidor, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTelefono()));

        tabla.getColumns().addAll(colId, colNombre, colTelefono);
        tabla.setItems(datos);

        root.setCenter(tabla);
    }

    private void configurarFormulario() {
        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(10);
        form.setVgap(10);

        // ----- Repartidor -----
        txtId.setPromptText("ID (entero)");
        txtNombre.setPromptText("Nombre");
        txtTelefono.setPromptText("Teléfono");

        form.add(new Label("ID:"), 0, 0);
        form.add(txtId, 1, 0);
        form.add(new Label("Nombre:"), 0, 1);
        form.add(txtNombre, 1, 1);
        form.add(new Label("Teléfono:"), 0, 2);
        form.add(txtTelefono, 1, 2);

        // Zona botones CRUD
        HBox botonesCrud = new HBox(10, btnNuevo, btnGuardar, btnBorrar, btnRecargar);
        botonesCrud.setPadding(new Insets(10, 0, 0, 0));

        // Zona de búsqueda
        HBox zonaBusqueda = new HBox(10, new Label("Buscar:"), txtBuscar, btnBuscar, btnLimpiarBusqueda);
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
                // Repartidor
                txtId.setText(String.valueOf(newSel.getId()));
                txtNombre.setText(newSel.getNombre());
                txtTelefono.setText(newSel.getTelefono());
                txtId.setDisable(true);
            }
        });

        btnNuevo.setOnAction(e -> limpiarFormulario());

        btnGuardar.setOnAction(e -> guardarRepartidor());

        btnBorrar.setOnAction(e -> borrarRepartidorSeleccionado());

        btnRecargar.setOnAction(e -> {
            txtBuscar.clear();
            recargarDatos();
        });

        btnBuscar.setOnAction(e -> buscarRepartidorBBDD());

        btnLimpiarBusqueda.setOnAction(e -> {
            txtBuscar.clear();
            recargarDatos();
        });
    }

    /**
     * Carga todos los repartidores desde la BBDD usando RepartidorDAO.findAll()
     */
    private void recargarDatos() {
        try {
            List<Repartidor> repartidores = repartidorDAO.findAll();

            datos.setAll(repartidores);
        } catch (SQLException e) {
            mostrarError("Error al cargar repartidores", e);
        }
    }

    /**
     * Búsqueda de repartidores en BBDD.
     *
     * Se busca en todos los campos de cada Repartidor en base a si contienen el texto
     * que haya en el campo txtBuscar de la interfaz gráfica
     */
    private void buscarRepartidorBBDD() {
        String filtro = txtBuscar.getText().trim();

        if ((filtro.isEmpty())){
            recargarDatos();
            return;
        }

        try {
            List<Repartidor> lista = repartidorDAO.search(filtro);
            datos.setAll(lista);
        } catch (SQLException e){
            mostrarError("Error al buscar", e);
        }
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtId.setDisable(false);
        tabla.getSelectionModel().clearSelection();
    }

    /**
     * Guardar repartidor:
     *  - Si no existe en la BD → INSERT
     *  - Si existe → actualizar los datos con los nuevos inputs
     */
    private void guardarRepartidor() {
        // Validación rápida
        if (txtId.getText().isBlank() || txtNombre.getText().isBlank() || txtTelefono.getText().isBlank()) {

            mostrarAlerta("Campos obligatorios", "Debes rellenar ID, nombre y teléfono.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarAlerta("ID inválido", "El ID debe ser un número entero.");
            return;
        }

        // Repartidor con ID escrito por el usuario
        Repartidor c = new Repartidor(id, txtNombre.getText().trim(), txtTelefono.getText().trim());

        try (Connection con = Db.getConnection()) {
            Repartidor existente = repartidorDAO.findById(id);

            if (existente == null) {
                // No existe → INSERT
                repartidorDAO.insert(c, con);

                mostrarInfo("Insertado", "Repartidor creado correctamente.");
            } else {
                // Ya existe → UPDATE
                repartidorDAO.update(c, con);
                mostrarInfo("Actualizado", "El repartidor " + c.getNombre() + " se ha actualizado correctamente.");
            }

            recargarDatos();
            limpiarFormulario();

        } catch (SQLException e) {
            mostrarError("Error al guardar repartidor", e);
        }
    }

    /**
     * Borrar repartidor seleccionado.
     *
     * Borra un repartidor por su ID.
     */
    private void borrarRepartidorSeleccionado() {
        Repartidor sel = tabla.getSelectionModel().getSelectedItem();
        int borrado;

        if (sel == null) {
            mostrarAlerta("Sin selección", "Selecciona un repartidor en la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar borrado");
        confirm.setHeaderText("¿Eliminar repartidor?");
        confirm.setContentText("Se borrará el repartidor con ID " + sel.getId());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try (Connection con = Db.getConnection()) {
            borrado = repartidorDAO.deleteById(sel.getId(), con);

            if (borrado > 0) {
                mostrarInfo("Borrado correcto", "El repartidor " + sel.getNombre() + " se ha borrado correctamente.");
                recargarDatos();
                limpiarFormulario();
            } else {
                mostrarAlerta("Borrado incorrecto", "No se encontró el repartidor en la BBDD.");
            }
        } catch (SQLException e) {
            mostrarError("Error al borrar repartidor", e);
        }

    }

}

