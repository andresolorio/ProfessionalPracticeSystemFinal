package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.logic.controllers.UserController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.EmailManager;
import mx.uv.lis.professionalpracticesystem.logic.utils.PasswordManager;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.ROLE_PROFESSOR;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;

/**
 * 
 * @author cinth
 * @author andre
 */
public class ProfessorRegistrationViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            ProfessorRegistrationViewController.class.getName());
    private final UserController userController;

    @FXML
    private TextField staffNumberTextField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField paternalLastNameTextField;
    @FXML
    private TextField maternalLastNameTextField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private CheckBox isCoordinatorCheckBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    public ProfessorRegistrationViewController() {
        this.userController = new UserController();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.genderComboBox.setItems(FXCollections.observableArrayList(
                "Masculino", "Femenino", "Otro"));
    }

    @FXML
    private void handleSave() {
        if (!this.validateFields()) {
            return;
        }

        this.saveButton.setDisable(true);
        String plainPassword = this.passwordPasswordField.getText();

        try {
            if (this.isCoordinatorCheckBox.isSelected()) {
                LOGGER.log(Level.INFO, "Checking coordinator monopoly constraint " 
                        + "synchronously in the server.");
                
                boolean hasCoordinator = this.userController
                        .isCoordinatorAlreadyRegistered();
                
                if (hasCoordinator) {
                    AlertUtility.showWarningAlert("Restricción de Sistema",
                            "No se puede registrar al docente con el rol de " 
                            + "Coordinador debido a que ya existe un usuario " 
                            + "asignado a dicho puesto.");
                    this.isCoordinatorCheckBox.setSelected(false);
                    this.saveButton.setDisable(false);
                    return;
                }
            }

            this.executeProfessorRegistration(plainPassword);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server transaction exception encountered " 
                    + "during coordinator validation pipeline.", exception);
            AlertUtility.showErrorAlert("Error de Conexión", "No se pudo " 
                    + "comprobar la disponibilidad del rol con el servidor.");
            this.saveButton.setDisable(false);
        }
    }

    private void executeProfessorRegistration(String plainPassword) {
        UserDTO user = new UserDTO();
        user.setEmail(this.emailTextField.getText().trim());
        user.setPassword(plainPassword);
        user.setRole(ROLE_PROFESSOR);

        ProfessorDTO professor = new ProfessorDTO();
        professor.setProfessorStaffNumber(this.staffNumberTextField
                .getText().trim());
        professor.setFirstName(this.firstNameTextField.getText().trim());
        professor.setPaternalLastName(this.paternalLastNameTextField
                .getText().trim());
        professor.setMaternalLastName(this.maternalLastNameTextField
                .getText().trim());
        professor.setGender(this.genderComboBox.getValue());
        professor.setIsCoordinator(this.isCoordinatorCheckBox.isSelected());
        professor.setEmail(user.getEmail());
        professor.setStatus(SystemConstants.STATUS_ACTIVE);

        try {
            LOGGER.log(Level.INFO, "Executing synchronous professor insertion " 
                    + "routine into the core server repository.");
            
            boolean success = this.userController
                    .registerProfessor(professor, user);

            if (success) {
                LOGGER.log(Level.INFO, "Server transaction committed. " 
                        + "Dispatching welcome email invitation to: {0}", 
                        user.getEmail());
                
                EmailManager.sendWelcomeEmail(user.getEmail(),
                        professor.getFirstName(), plainPassword);

                AlertUtility.showInformationAlert("Éxito", 
                        "¡Profesor registrado correctamente en el servidor!");
                this.handleCancel();
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.WARNING, "The server transaction aborted professor " 
                    + "insertion due to duplicate constraint rules.");
            AlertUtility.showErrorAlert("Error de Registro", 
                    exception.getMessage());
        } finally {
            this.saveButton.setDisable(false);
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (!Validator.isNotEmpty(this.staffNumberTextField.getText())
                || !Validator.isNotEmpty(this.firstNameTextField.getText())
                || !Validator.isNotEmpty(this.paternalLastNameTextField.getText())
                || !Validator.isNotEmpty(this.emailTextField.getText())
                || !Validator.isNotEmpty(this.passwordPasswordField.getText())
                || this.genderComboBox.getValue() == null) {

            AlertUtility.showWarningAlert("Campos Obligatorios", 
                    "Por favor, llene todos los campos marcados con (*).");
            isValid = false;
        }

        if (isValid) {
            String firstName = this.firstNameTextField.getText().trim();
            String paternalLastName = this.paternalLastNameTextField
                    .getText().trim();
            String maternalLastName = this.maternalLastNameTextField
                    .getText().trim();

            if (!Validator.isValidName(firstName)) {
                AlertUtility.showWarningAlert("Nombre Inválido",
                        "El nombre del docente debe tener entre 3 y 50 " 
                        + "caracteres y contener solo letras.");
                isValid = false;
            } else if (!Validator.isValidName(paternalLastName)) {
                AlertUtility.showWarningAlert("Apellido Paterno Inválido",
                        "El apellido paterno debe tener entre 3 y 50 " 
                        + "caracteres y contener solo letras.");
                isValid = false;
            } else if (!maternalLastName.isEmpty() 
                    && !Validator.isValidName(maternalLastName)) {
                AlertUtility.showWarningAlert("Apellido Materno Inválido",
                        "El apellido materno debe tener entre 3 y 50 " 
                        + "caracteres y contener solo letras.");
                isValid = false;
            }
        }

        if (isValid && !Validator.isValidStaffNumber(this.staffNumberTextField
                .getText())) {
            AlertUtility.showWarningAlert("Número de Personal Inválido",
                    "El número de personal debe estar compuesto por " 
                    + "6 dígitos numéricos.");
            isValid = false;
        }

        if (isValid && !Validator.isValidEmail(this.emailTextField
                .getText().trim())) {
            AlertUtility.showWarningAlert("Correo Inválido",
                    "El formato del correo electrónico no es correcto.");
            isValid = false;
        }

        if (isValid && !PasswordManager.isStrongPassword(this
                .passwordPasswordField.getText())) {
            AlertUtility.showWarningAlert("Contraseña Débil",
                    "La contraseña no cumple con los requisitos mínimos de " 
                    + "seguridad definidos en el servidor.");
            isValid = false;
        }
        return isValid;
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) this.staffNumberTextField.getScene().getWindow();
        stage.close();
    }
}