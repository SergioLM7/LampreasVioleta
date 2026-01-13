package app.ui.comerciales;

import dao.ComercialDAO;
import db.Db;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.Comercial;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static utils.AlertsUtils.*;

/**
 * Vista JavaFX para gestionar comerciales.
 *
 * Versión completa que permite
 *  - Usa ComercialDAO con su CRUD.
 *  - La tabla muestra datos de los comerciales.
 */
public class ComercialesView {

    private final BorderPane root = new BorderPane();

    // Tabla y datos
    private final TableView<Comercial> tabla = new TableView<>();
    private final ObservableList<Comercial> datos = FXCollections.observableArrayList();

    // Campos de formulario (Comercial)
    private final TextField txtId = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtEmail = new TextField();

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
    private final ComercialDAO comercialDAO = new ComercialDAO();

    public ComercialesView() {
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
        TableColumn<Comercial, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()));

        TableColumn<Comercial, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));

        TableColumn<Comercial, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));

        tabla.getColumns().addAll(colId, colNombre, colEmail);
        tabla.setItems(datos);

        root.setCenter(tabla);
    }

    private void configurarFormulario() {
        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(10);
        form.setVgap(10);

        // ----- Comercial -----
        txtId.setPromptText("ID (entero)");
        txtNombre.setPromptText("Nombre");
        txtEmail.setPromptText("Email");

        form.add(new Label("ID:"), 0, 0);
        form.add(txtId, 1, 0);
        form.add(new Label("Nombre:"), 0, 1);
        form.add(txtNombre, 1, 1);
        form.add(new Label("Email:"), 0, 2);
        form.add(txtEmail, 1, 2);

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
                // Comercial
                txtId.setText(String.valueOf(newSel.getId()));
                txtNombre.setText(newSel.getNombre());
                txtEmail.setText(newSel.getEmail());
                txtId.setDisable(true);
            }
        });

        btnNuevo.setOnAction(e -> limpiarFormulario());

        btnGuardar.setOnAction(e -> guardarComercial());

        btnBorrar.setOnAction(e -> borrarComercialSeleccionado());

        btnRecargar.setOnAction(e -> {
            txtBuscar.clear();
            recargarDatos();
        });

        //btnBuscar.setOnAction(e -> buscarComercialBBDD());

        btnLimpiarBusqueda.setOnAction(e -> {
            txtBuscar.clear();
            recargarDatos();
        });
    }

    /**
     * Carga todos los comerciales desde la BD usando ComercialDAO.findAll()
     */
    private void recargarDatos() {
        try {
            List<Comercial> comerciales = comercialDAO.findAll();

            datos.setAll(comerciales);
        } catch (SQLException e) {
            mostrarError("Error al cargar comerciales", e);
        }
    }

    /**
     * Búsqueda de comerciales en BBD.
     *
     * Se busca en todos los campos de cada Comercial en base a si contienen el texto
     * que haya en el campo txtBuscar de la interfaz gráfica
     */
    //    private void buscarComercialBBDD(){
    //        String filtro = txtBuscar.getText().trim();
    //
    //        if ((filtro.isEmpty())){
    //            recargarDatos();
    //            return;
    //        }
    //
    //        try {
    //            List<Comercial> lista = comercialDAO.search(filtro);
    //            datos.setAll(lista);
    //        } catch (SQLException e){
    //            mostrarError("Error al buscar", e);
    //        }
    //
    //    }
    private void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        txtEmail.clear();
        txtId.setDisable(false);
        tabla.getSelectionModel().clearSelection();
    }

    /**
     * Guardar comercial:
     *  - Si no existe en la BD → INSERT
     *  - Si existe → actualizar los datos con los nuevos inputs
     */
    private void guardarComercial() {
        // Validación rápida
        if (txtId.getText().isBlank() || txtNombre.getText().isBlank() || txtEmail.getText().isBlank()) {

            mostrarAlerta("Campos obligatorios", "Debes rellenar ID, nombre y email.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarAlerta("ID inválido", "El ID debe ser un número entero.");
            return;
        }

        // Comercial con ID escrito por el usuario
        Comercial c = new Comercial(id, txtNombre.getText().trim(), txtEmail.getText().trim());

        try (Connection con = Db.getConnection()) {
            Comercial existente = comercialDAO.findById(id);

            if (existente == null) {
                // No existe → INSERT
                comercialDAO.insert(c, con);

                mostrarInfo("Insertado", "Comercial creado correctamente.");
            } else {
                // Ya existe → UPDATE
                comercialDAO.update(c, con);
                mostrarInfo("Actualizado", "El comercial " + c.getNombre() + " se ha actualizado correctamente.");
            }

            recargarDatos();
            limpiarFormulario();

        } catch (SQLException e) {
            mostrarError("Error al guardar comercial", e);
        }
    }

    /**
     * Borrar comercial seleccionado.
     *
     * Borra un comercial por su ID.
     */
    private void borrarComercialSeleccionado() {
        Comercial sel = tabla.getSelectionModel().getSelectedItem();
        int borrado;

        if (sel == null) {
            mostrarAlerta("Sin selección", "Selecciona un comercial en la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar borrado");
        confirm.setHeaderText("¿Eliminar comercial?");
        confirm.setContentText("Se borrará el comercial con ID " + sel.getId());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try (Connection con = Db.getConnection()) {
            borrado = comercialDAO.deleteById(sel.getId(), con);

            if (borrado > 0) {
                mostrarInfo("Borrado correcto", "El comercial " + sel.getNombre() + " se ha borrado correctamente.");
                recargarDatos();
                limpiarFormulario();
            } else {
                mostrarAlerta("Borrado incorrecto", "No se encontró el comercial en la BBDD.");
            }
        } catch (SQLException e) {
            mostrarError("Error al borrar comercial", e);
        }

    }

}
