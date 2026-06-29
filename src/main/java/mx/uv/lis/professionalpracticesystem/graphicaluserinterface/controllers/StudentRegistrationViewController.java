package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.UserController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.PeriodDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.EmailManager;
import mx.uv.lis.professionalpracticesystem.logic.utils.PasswordManager;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.ENROLLMENT_PREFIX_S;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.ROLE_STUDENT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATUS_ACTIVE;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;

/**
 *
 * @author cinth
 * @author andre
 */
public class StudentRegistrationViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            StudentRegistrationViewController.class.getName());

    @FXML
    private TextField enrollmentTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField paternalLastNameTextField;
    @FXML
    private TextField maternalLastNameTextField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField periodTextField;
    @FXML
    private TextField coveredCreditsTextField;
    @FXML
    private TextField mailTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private TextField roleTextField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final UserController userController;

    public StudentRegistrationViewController() {
        this.userController = new UserController();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourcebundle) {
        this.genderComboBox.setItems(FXCollections.observableArrayList(
                "Masculino", "Femenino", "Otro"));
        this.roleTextField.setText(ROLE_STUDENT);
        
        this.saveButton.setOnAction(new SaveButtonEventHandler(this));
        this.cancelButton.setOnAction(new CancelButtonEventHandler(this));
        
        this.enrollmentTextField.setText(ENROLLMENT_PREFIX_S);

        try {
            LOGGER.log(Level.INFO, "Fetching current school period "
                    + "synchronously from the database repository server.");
            PeriodDAO periodDAO = new PeriodDAO();
            String periodCode = periodDAO.getCurrentPeriodFromServer();
            this.periodTextField.setText(periodCode);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server infrastructure data lookup "
                    + "failure. Deploying default fallback period.", exception);
            this.periodTextField.setText(
                    SystemConstants.DEFAULT_FALLBACK_PERIOD);
        }
    }

    public void executeStudentPersistenceFlow() {
        if (this.validateFields()) {
            try {
                String passwordPlana = this.passwordPasswordField.getText();
                
                UserDTO user = new UserDTO();
                user.setEmail(this.mailTextField.getText().trim());             
                user.setPassword(passwordPlana); 
                user.setRole(ROLE_STUDENT);

                StudentDTO student = new StudentDTO();
                student.setEnrollmentId(this.enrollmentTextField.getText()
                        .trim());
                student.setFirstName(this.nameTextField.getText().trim());
                student.setPaternalLastName(this.paternalLastNameTextField
                        .getText().trim());

                String maternalLastNameInput = this.maternalLastNameTextField
                        .getText().trim();
                if (maternalLastNameInput.isEmpty()) {
                    student.setMaternalLastName("");
                } else {
                    student.setMaternalLastName(maternalLastNameInput);
                }

                student.setGender(this.genderComboBox.getValue());
                student.setPeriod(this.periodTextField.getText());
                student.setEmail(user.getEmail());
                student.setStatus(STATUS_ACTIVE);

                if (!this.coveredCreditsTextField.getText().trim().isEmpty()) {
                    student.setCoveredCredits(Integer.parseInt(this
                            .coveredCreditsTextField.getText().trim()));
                } else {
                    student.setCoveredCredits(RESET);
                }

                student.setCoordinatorPersonalNumber(null);
                
                LOGGER.log(Level.INFO, "Initiating synchronous database write "
                        + "transaction for student and user profile link.");
                boolean success = this.userController
                        .registerStudent(student, user);

                if (success) {
                    EmailManager.sendWelcomeEmail(
                            this.mailTextField.getText().trim(),
                            this.nameTextField.getText().trim(),
                            passwordPlana
                    );

                    AlertUtility.showInformationAlert("Éxito", 
                            "¡Alumno registrado correctamente!");
                    this.executeCancelSequence();
                }
            } catch (DataIntegrityException exception) {
                LOGGER.log(Level.WARNING, "School registration write operation "
                        + "interrupted: Uniqueness constraint key violation.");
                AlertUtility.showWarningAlert("Registro Duplicado", 
                        exception.getMessage());
            } catch (DatabaseSystemException exception) {
                LOGGER.log(Level.SEVERE, "Server transaction engineering error "
                        + "persisting student academic profile metadata.", 
                        exception);
                AlertUtility.showErrorAlert("Error de Servidor", 
                        "No se pudo completar el registro en el servidor: " 
                        + exception.getMessage());
            }
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (this.enrollmentTextField.getText().trim().isEmpty()
                || this.nameTextField.getText().trim().isEmpty()
                || this.paternalLastNameTextField.getText().trim().isEmpty()
                || this.mailTextField.getText().trim().isEmpty()
                || this.passwordPasswordField.getText().isEmpty()
                || this.genderComboBox.getValue() == null) {

            AlertUtility.showWarningAlert("Campos Obligatorios", 
                    "Por favor, llene todos los campos marcados con (*).");
            isValid = false;
        }

        if (isValid && !Validator.isValidName(this.nameTextField
                .getText().trim())) {
            AlertUtility.showWarningAlert("Nombre inválido", 
                    "El nombre debe contener al menos 3 letras.");
            isValid = false;
        }

        if (isValid && !Validator.isValidName(this.paternalLastNameTextField
                .getText().trim())) {
            AlertUtility.showWarningAlert("Apellido Paterno Inválido", 
                    "Debe contener al menos 3 letras.");
            isValid = false;
        }

        if (isValid && (!this.maternalLastNameTextField.getText().trim()
                .isEmpty() && !Validator.isValidName(this
                .maternalLastNameTextField.getText().trim()))) {
            AlertUtility.showWarningAlert("Apellido Materno Inválido", 
                    "Solo debe contener letras.");
            isValid = false;
        }

        if (isValid && this.coveredCreditsTextField.getText().trim()
                .isEmpty()) {
            AlertUtility.showWarningAlert("Campo Obligatorio", 
                    "Por favor, ingrese los créditos cubiertos del alumno.");
            isValid = false;
        }

        if (isValid) {
            try {
                int credits = Integer.parseInt(this.coveredCreditsTextField
                        .getText().trim());

                if (!Validator.isValidCreditRange(credits)) {
                    AlertUtility.showWarningAlert("Créditos Fuera de Rango",
                            "Los créditos ingresados están fuera del rango.");
                    isValid = false;
                }
            } catch (NumberFormatException exception) {
                AlertUtility.showWarningAlert("Formato Incorrecto", 
                        "El campo de créditos solo permite números enteros.");
                isValid = false;
            }
        }

        if (isValid && !Validator.isValidEnrollment(this.enrollmentTextField
                .getText().trim())) {
            AlertUtility.showWarningAlert("Matrícula inválida", 
                    "Debe cumplir formato SXXXXXXXX");
            isValid = false;
        }

        if (isValid && !Validator.isValidEmail(this.mailTextField
                .getText().trim())) {
            AlertUtility.showWarningAlert("Email inválido", 
                    "Ingresa una dirección válida de email");
            isValid = false;
        }

        if (isValid && !PasswordManager.isStrongPassword(this
                .passwordPasswordField.getText())) {
            AlertUtility.showWarningAlert("Contraseña Débil", 
                    "La contraseña no cumple con los requisitos mínimos.");
            isValid = false;
        }

        return isValid;
    }

    public void executeCancelSequence() {
        this.enrollmentTextField.clear();
        this.nameTextField.clear();
        this.paternalLastNameTextField.clear();
        this.maternalLastNameTextField.clear();
        this.mailTextField.clear();
        this.passwordPasswordField.clear();
        this.genderComboBox.getSelectionModel().clearSelection();
        this.coveredCreditsTextField.clear();

        NavigationUtility.navigateTo(this.saveButton, 
                ViewConstants.VIEW_COORDINATOR_MENU, 
                SystemConstants.TITLE_COORDINATOR_MENU);
    }

    private static class SaveButtonEventHandler 
            implements EventHandler<ActionEvent> {
        private final StudentRegistrationViewController controller;

        public SaveButtonEventHandler(
                StudentRegistrationViewController controller) {
            this.controller = controller;
        }

        @Override
        public void handle(ActionEvent event) {
            this.controller.executeStudentPersistenceFlow();
        }
    }

    private static class CancelButtonEventHandler 
            implements EventHandler<ActionEvent> {
        private final StudentRegistrationViewController controller;

        public CancelButtonEventHandler(
                StudentRegistrationViewController controller) {
            this.controller = controller;
        }

        @Override
        public void handle(ActionEvent event) {
            this.controller.executeCancelSequence();
        }
    }
}