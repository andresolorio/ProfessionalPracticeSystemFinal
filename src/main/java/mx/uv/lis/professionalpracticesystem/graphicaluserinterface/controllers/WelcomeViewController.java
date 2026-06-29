package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.UserController;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;

/**
 * 
 * @author andre
 * @author cinth
 */
public class WelcomeViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            WelcomeViewController.class.getName());
    private final UserController userController;
    private String targetFxmlPath;
    private String targetStageTitle;

    @FXML
    private Label systemStatusLabel;
    @FXML
    private Button actionButton;

    public WelcomeViewController() {
        this.userController = new UserController();
        this.targetFxmlPath = ViewConstants.VIEW_LOGIN;
        this.targetStageTitle = SystemConstants.TITLE_LOGIN;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.actionButton.setDisable(true);
        this.systemStatusLabel.setText("Verificando integridad " 
                + "del entorno...");
        
        LOGGER.log(Level.INFO, "Initiating verification of system " 
                + "user density in the database.");
        
        this.executeDatabaseVerification();
    }

    private void executeDatabaseVerification() {
        try {
            int totalUsers = this.userController.checkSystemUserDensity();
            
            if (totalUsers == RESET) {
                this.updateInterfaceForEmptyDatabase();
            } else {
                this.updateInterfaceForNormalDatabase();
            }
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Critical failure during initial " 
                    + "contingency check. Cause: " + exception.getMessage());
            
            this.handleVerificationFailure(exception.getMessage());
        }
    }

    @FXML
    private void handleActionTrigger(ActionEvent event) {
        NavigationUtility.navigateTo(this.actionButton, this.targetFxmlPath, 
                this.targetStageTitle);
    }

    public void updateInterfaceForEmptyDatabase() {
        this.systemStatusLabel.setText("Estado: Base de datos vacía. " 
                + "Se requiere configuración.");
        this.systemStatusLabel.setStyle("-fx-text-fill: #dc3545; " 
                + "-fx-font-weight: bold;");
        
        this.actionButton.setText("Configurar Administrador");
        this.actionButton.setStyle("-fx-background-color: #dc3545; " 
                + "-fx-text-fill: white; -fx-background-radius: 5; " 
                + "-fx-font-weight: bold; -fx-cursor: hand;");
        
        this.targetFxmlPath = ViewConstants.VIEW_REGISTER_ADMIN;
        this.targetStageTitle = SystemConstants.TITLE_REGISTER_ADMIN;
        this.actionButton.setDisable(false);
    }

    public void updateInterfaceForNormalDatabase() {
        this.systemStatusLabel.setText("Estado: Sistema listo y " 
                + "conectado de forma segura.");
        this.systemStatusLabel.setStyle("-fx-text-fill: #28a745; " 
                + "-fx-font-weight: bold;");
        
        this.actionButton.setText("Ingresar al Sistema");
        this.actionButton.setDisable(false);
    }

    public void handleVerificationFailure(String errorMessage) {
        this.systemStatusLabel.setText("Estado: Error de " 
                + "conexión de red.");
        this.systemStatusLabel.setStyle("-fx-text-fill: #ffc107; " 
                + "-fx-font-weight: bold;");
        
        AlertUtility.showErrorAlert("Fallo de Conexión", "No se pudo " 
                + "establecer contacto con el servidor. Causa: " 
                + errorMessage);
    }
}