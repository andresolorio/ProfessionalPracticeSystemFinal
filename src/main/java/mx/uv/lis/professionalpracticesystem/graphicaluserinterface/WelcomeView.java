package mx.uv.lis.professionalpracticesystem.graphicaluserinterface;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author andre
 */
public class WelcomeView extends Application {

    private static final Logger LOGGER = Logger.getLogger(WelcomeView.class.getName());

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/fxml/WelcomeView.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Sistema de Prácticas Profesionales - Bienvenido");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Fallo técnico al compilar y renderizar la vista de presentación FXML", exception);
        }
    }
}