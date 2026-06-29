package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_STAGE_PROFESSOR_REGISTRATION;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_STAGE_LOGIN;

/**
 *
 * @author andre
 * @author cinth
 */
public class AdminMenuViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            AdminMenuViewController.class.getName());

    @FXML
    private Label welcomeLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    private void handleOpenRegistration(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass()
                    .getResource(ViewConstants.VIEW_PROFESSOR_REGISTRATION));
            Parent root = (Parent) loader.load();

            Stage stage = new Stage();
            stage.setTitle(TITLE_STAGE_PROFESSOR_REGISTRATION);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Critical failure: Could not load " 
                    + "professor registration view window context", exception);
            
            AlertUtility.showErrorAlert("Error de Sistema", "No se pudo " 
                    + "desplegar el formulario de registro de docentes.");
        }
    }

    @FXML
    private void handleOpenInactivation(ActionEvent event) {
        NavigationUtility.openProfessorListModal("INACTIVATE", "Inactivar Docente");
    }

    @FXML
    private void handleOpenProfessorManagement(ActionEvent event) {
        NavigationUtility.openProfessorListModal("ROLE", "Gestión de Roles");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            UserSession.getInstance().logout();

            FXMLLoader loader = new FXMLLoader(this.getClass()
                    .getResource(ViewConstants.VIEW_LOGIN));
            Parent root = (Parent) loader.load();

            Stage stage = (Stage) this.welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle(TITLE_STAGE_LOGIN);
            stage.show();
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "System exception catch: Failed to clear " 
                    + "context and redirect to LoginView context", exception);
            
            AlertUtility.showErrorAlert("Error de Navegación", "Ocurrió un " 
                    + "problema de renderizado al intentar cerrar la sesión.");
        }
    }
}