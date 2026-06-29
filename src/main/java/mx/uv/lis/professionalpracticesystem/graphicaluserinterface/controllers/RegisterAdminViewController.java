package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.UserController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.PasswordManager.isStrongPassword;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;

/**
 *
 * @author andre
 * @author cinth
 */
public class RegisterAdminViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            RegisterAdminViewController.class.getName());
    private final UserController userController;

    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private CheckBox showPasswordCheckBox;
    @FXML
    private Button registerButton;

    public RegisterAdminViewController() {
        this.userController = new UserController();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.passwordTextField.setVisible(false);
        this.passwordTextField.setManaged(false);
    }

    @FXML
    private void handleTogglePasswordVisibility(ActionEvent event) {
        if (this.showPasswordCheckBox.isSelected()) {
            this.passwordTextField.setText(this.passwordField.getText());
            this.passwordField.setVisible(false);
            this.passwordField.setManaged(false);
            this.passwordTextField.setVisible(true);
            this.passwordTextField.setManaged(true);
        } else {
            this.passwordField.setText(this.passwordTextField.getText());
            this.passwordTextField.setVisible(false);
            this.passwordTextField.setManaged(false);
            this.passwordField.setVisible(true);
            this.passwordField.setManaged(true);
        }
    }

    @FXML
    private void handleRegisterAdmin(ActionEvent event) {
        String email = this.emailTextField.getText().trim();
        
        String password;
        if (this.showPasswordCheckBox.isSelected()) {
            password = this.passwordTextField.getText();
        } else {
            password = this.passwordField.getText();
        }

        if (email.isEmpty() || password.isEmpty()) {
            AlertUtility.showWarningAlert("Campos Vacíos", "Por favor, " 
                    + "complete todos los campos marcados como obligatorios.");
            return;
        }

        if (!Validator.isValidEmail(email)) {
            AlertUtility.showWarningAlert("Formato Inválido", "El correo " 
                    + "electrónico ingresado no cuenta con un formato válido.");
            return;
        }

        if (!isStrongPassword(password)) {
            AlertUtility.showWarningAlert("Contraseña Débil", 
                    "La estructura de la contraseña no cumple con los criterios " 
                    + "mínimos de seguridad.\n\nDebe contener al menos 10 " 
                    + "caracteres, una mayúscula, un número y un carácter.");
            return;
        }

        LOGGER.log(Level.INFO, "Initiating root administrator account " 
                + "registration pipeline via logic controller.");

        UserDTO adminDTO = new UserDTO();
        adminDTO.setEmail(email);
        adminDTO.setPassword(password);
        adminDTO.setRole(SystemConstants.ROLE_ADMINISTRATOR);

        this.executeRegistrationFlow(adminDTO);
    }
    
    private void executeRegistrationFlow(UserDTO adminDTO) {
        try {
            this.registerButton.setDisable(true);
            LOGGER.log(Level.INFO, "Requesting root administrator account " 
                    + "activation synchronously from login view controller.");
            
            boolean success = this.userController.registerAdmin(adminDTO);
            
            if (success) {
                AlertUtility.showInformationAlert("Sistema Activado", 
                        "La cuenta del administrador raíz ha sido registrada " 
                        + "y sincronizada con éxito en el servidor.");
                
                NavigationUtility.navigateTo(this.registerButton, 
                        ViewConstants.VIEW_LOGIN, 
                        SystemConstants.TITLE_LOGIN);
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Logic layer system exception during " 
                    + "root administrator activation execution.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", 
                    exception.getMessage());
        } finally {
            this.registerButton.setDisable(false);
        }
    }
}