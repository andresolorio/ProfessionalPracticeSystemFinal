package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.UserController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_FORGOT_PASSWORD;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_LOGIN;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;

/**
 *
 * @author cinth
 * @author andre
 */
public class ForgotPasswordViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            ForgotPasswordViewController.class.getName());

    @FXML
    private TextField emailTextField;
    @FXML
    private Button sendCodeButton;
    @FXML
    private Button cancelButton;

    private UserController userController;

    @Override
    public void initialize(URL url, ResourceBundle resourcebundle) {
        this.userController = new UserController();
    }

    @FXML
    private void handleSendCode(ActionEvent event) {
        String email = this.emailTextField.getText().trim();

        if (!Validator.isNotEmpty(email)) {
            AlertUtility.showWarningAlert("Campo Vacío", "Por favor, " 
                    + "ingrese su correo electrónico institucional.");
            return;
        }

        this.executeTokenGenerationFlow(email);
    }

    private void executeTokenGenerationFlow(String email) {
        try {
            LOGGER.log(Level.INFO, "Initiating synchronous token generation " 
                    + "and transmission sequence for: {0}", email);
            
            this.userController.generateRecoveryToken(email);
            
            this.handleTransferSuccess();
        } catch (EntityNotFoundException exception) {
            LOGGER.log(Level.WARNING, "Token request rejected: Email target " 
                    + "not found in registry: " + email);
            
            AlertUtility.showErrorAlert("Correo No Registrado", 
                    exception.getMessage());
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Persistence failure captured during " 
                    + "token generation routine execution.", exception);
            
            AlertUtility.showErrorAlert("Error de Conexión", "No se pudo " 
                    + "establecer contacto con el servidor de datos.");
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Unexpected runtime exception captured " 
                    + "within processing thread pipeline.", exception);
            
            AlertUtility.showErrorAlert("Error de Sistema", "Ocurrió un " 
                    + "error inesperado al procesar la solicitud.");
        }
    }

    public void handleTransferSuccess() {
        LOGGER.log(Level.INFO, "Token generation and transmission sequence " 
                + "completed successfully.");
        
        AlertUtility.showInformationAlert("Código Enviado", "Se ha enviado " 
                + "un código de verificación a tu correo institucional. " 
                + "Revisa tu bandeja de entrada.");
        
        NavigationUtility.navigateTo(this.emailTextField, 
                ViewConstants.VIEW_RESET_PASSWORD, TITLE_FORGOT_PASSWORD);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting password recovery wizard session. " 
                + "Redirecting back to LoginView context.");
        
        NavigationUtility.navigateTo(this.emailTextField, 
                ViewConstants.VIEW_LOGIN, TITLE_LOGIN);
    }
}