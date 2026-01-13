package app.ui.menu;

import app.ui.clientes.ClientesView;
import app.ui.comerciales.ComercialesView;
import app.ui.repartidores.RepartidoresView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuPrincipalView {

    private final VBox root = new VBox(20); // Espaciado de 20px

    public MenuPrincipalView(Stage stage) {
        configurarLayout(stage);
    }

    private void configurarLayout(Stage stage) {
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f4f4;");

        Label titulo = new Label("SISTEMA DE GESTIÓN - LAMPREAS VIOLETA");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Botones
        Button btnClientes = new Button("Gestión de Clientes");
        Button btnComerciales = new Button("Gestión de Comerciales");
        Button btnRepartidores = new Button("Gestión de Repartidores");

        String estiloBoton = "-fx-min-width: 250px; -fx-padding: 10px; -fx-font-size: 14px;";
        btnClientes.setStyle(estiloBoton);
        btnComerciales.setStyle(estiloBoton);
        btnRepartidores.setStyle(estiloBoton);

        // Eventos de navegación
        btnClientes.setOnAction(e -> {
            ClientesView vistaClientes = new ClientesView();
            Button btnVolver = new Button("← Volver al Menú");
            vistaClientes.getRoot().setTop(btnVolver);

            btnVolver.setOnAction(ev -> stage.getScene().setRoot(this.root));

            stage.getScene().setRoot(vistaClientes.getRoot());
        });

        btnComerciales.setOnAction(e -> {
            ComercialesView vistaComerciales = new ComercialesView();
            Button btnVolver = new Button("← Volver al Menú");
            vistaComerciales.getRoot().setTop(btnVolver);

            btnVolver.setOnAction(ev -> stage.getScene().setRoot(this.root));

            stage.getScene().setRoot(vistaComerciales.getRoot());
        });

        btnRepartidores.setOnAction(e -> {
            RepartidoresView vistaRepartidores = new RepartidoresView();
            Button btnVolver = new Button("← Volver al Menú");
            vistaRepartidores.getRoot().setTop(btnVolver);

            btnVolver.setOnAction(ev -> stage.getScene().setRoot(this.root));

            stage.getScene().setRoot(vistaRepartidores.getRoot());
        });

        root.getChildren().addAll(titulo, btnClientes, btnComerciales, btnRepartidores);
    }

    public VBox getRoot() {
        return root;
    }
}
