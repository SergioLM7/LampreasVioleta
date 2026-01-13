package app;

import app.ui.menu.MenuPrincipalView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LampreasVioletaApp extends Application {
    @Override
    public void start(Stage stage) {
        MenuPrincipalView menuPrincipal = new MenuPrincipalView(stage);
        Scene scene = new Scene(menuPrincipal.getRoot(), 900, 600);
        stage.setTitle("Lampreas Violeta - Inicio");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
