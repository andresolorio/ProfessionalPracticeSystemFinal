package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.UserController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.BusinessException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.InvalidPasswordException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;

/**
 * 
 * @author andre
 * @author cinth
 */
public class LoginViewController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(
            LoginViewController.class.getName());
    private UserController userController;

    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.userController = new UserController();
    }    

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = this.emailTextField.getText().trim();
        String password = this.passwordTextField.getText();

        if (!this.validateFields(email, password)) {
            return;
        }

        try {
            LOGGER.log(Level.INFO, "Executing synchronous login transaction "
                    + "for user email payload registry verification.");
            
            UserDTO user = this.userController.login(email, password);
            this.handleLoginSuccess(email, user);
            
        } catch (InvalidPasswordException exception) {
            LOGGER.log(Level.WARNING, "Authentication failure: Invalid "
                    + "password attempt caught for input credentials.");
            
            AlertUtility.showErrorAlert("Credenciales Incorrectas", 
                    "El correo o la contraseña son inválidos.");
            this.passwordTextField.clear();
        } catch (BusinessException exception) {
            LOGGER.log(Level.WARNING, "Business rule constraint violation "
                    + "during login validation processing: " 
                    + exception.getMessage());
            
            AlertUtility.showWarningAlert("Acceso Restringido", 
                    exception.getMessage());    
            this.passwordTextField.clear();
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Database technical connection failure "
                    + "captured during login transaction sequence.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo "
                    + "establecer contacto con el servidor de la aplicación.");
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Unexpected runtime exception captured "
                    + "within processing data pipeline.", exception);
            
            AlertUtility.showErrorAlert("Error de Sistema", "Ocurrió un "
                    + "error inesperado al procesar el acceso.");
        }
    }

    private void handleLoginSuccess(String email, UserDTO user) {
        if (user != null) {
            UserSession.getInstance().login(user);
            LOGGER.log(Level.INFO, "Successful login transaction executed "
                    + "for user: {0}", email);
            
            AlertUtility.showInformationAlert("Acceso Exitoso", 
                    "Bienvenido al sistema.");
            this.redirectByRole(user.getRole());
        } else {
            AlertUtility.showErrorAlert("Credenciales Incorrectas", 
                    "El correo o la contraseña son inválidos.");
        }
    }

    private void redirectByRole(String role) {
        switch (role) {
            case SystemConstants.ROLE_ADMIN:
                NavigationUtility.navigateTo(this.emailTextField, 
                        ViewConstants.VIEW_ADMIN_MENU, 
                        SystemConstants.TITLE_MENU_ADMIN);
                break;
            case SystemConstants.ROLE_PROFESSOR:
                NavigationUtility.navigateTo(this.emailTextField, 
                        ViewConstants.VIEW_PROFESSOR_MENU, 
                        SystemConstants.TITLE_MENU_PROFESSOR);
                break;
            case SystemConstants.ROLE_STUDENT:
                NavigationUtility.navigateTo(this.emailTextField, 
                        ViewConstants.VIEW_STUDENT_MENU, 
                        SystemConstants.TITLE_MENU_STUDENT);
                break;
            case SystemConstants.ROLE_COORDINATOR:
                NavigationUtility.navigateTo(this.emailTextField, 
                        ViewConstants.VIEW_COORDINATOR_MENU, 
                        SystemConstants.TITLE_MENU_COORDINATOR);
                break;
            default:
                LOGGER.log(Level.WARNING, "Security access warning: Login "
                        + "attempted with unrecognized role context: {0}", 
                        role);
                
                AlertUtility.showErrorAlert("Error de Rol", "El usuario no "
                        + "tiene un rol válido asignado.");
                break;
        }
    }

    private boolean validateFields(String email, String password) {
        boolean isValid = true;
        if (!Validator.isNotEmpty(email) || !Validator.isNotEmpty(password)) {
            AlertUtility.showErrorAlert("Campos Vacíos", "Por favor, "
                    + "complete todos los campos de texto.");
            isValid = false;
        }
        return isValid;
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        LOGGER.log(Level.INFO, "Password recovery request trigger: "
                + "Redirecting to ForgotPasswordView interface context.");
        
        NavigationUtility.navigateTo(this.emailTextField, 
                ViewConstants.VIEW_FORGOT_PASSWORD, 
                SystemConstants.TITLE_FORGOT_PASSWORD);
    }
}