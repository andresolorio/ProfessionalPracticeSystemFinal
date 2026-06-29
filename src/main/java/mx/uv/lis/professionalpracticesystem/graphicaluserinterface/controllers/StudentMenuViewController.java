package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;

import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MINIMUM_CREDITS_FOR_PRACTICES;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.REQUIRED_HOURS_FINAL_REPORT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STUDENT_STATUS_ACREDITED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.REQUIRED_HOURS_PARTIAL_REPORT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_PROJECT_SELECTION;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_UPLOAD_FORMATS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_STAGE_REPORT_FORM;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_STAGE_PARTIAL_REPORT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_STAGE_FINAL_REPORT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_REPORTS_AVAILABLE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_LOGIN;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WELCOME_PREFIX;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.SESSION_DATE_PREFIX;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.LOCAL_MODE_LABEL;

/**
 *
 * @author andre
 * @author cinth
 */
public class StudentMenuViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            StudentMenuViewController.class.getName());
    private StudentDTO currentStudent;
    private boolean hasMinimumCredits = false;
    private boolean hasEducativeExperienceAssigned = false;
    private boolean hasProjectAssigned = false;
    private boolean hasHoursForPartialReport = false;
    private boolean hasHoursForFinalReport = false;

    @FXML
    private Button logoutButton;
    @FXML
    private Button selectProjectsButton;
    @FXML
    private Button uploadFormatsButton;
    @FXML
    private Button generateReportButton;
    @FXML
    private Button generatePartialReportButton;
    @FXML
    private Button generateFinalReportButton;
    @FXML
    private Button addReportButton;
    @FXML
    private Button selfEvaluationButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label sessionDetailsLabel;
    @FXML
    private GridPane menuOptionsGridPane;
    @FXML
    private VBox congratulationsVBox;
    @FXML
    private Label congratulationsHeaderLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.showSessionInformation();
    }

    public void initializeStudentMenuContext(StudentDTO student) {
        if (student != null) {
            this.currentStudent = student;
            if (STUDENT_STATUS_ACREDITED.equalsIgnoreCase(student.getStatus())) {
                this.lockSystemForAcreditedStudent(student.getFirstName());
            } else {
                this.welcomeLabel.setText("Bienvenido, " + student.getFirstName());
            }
        }
    }

    private void showSessionInformation() {
        UserDTO loggedUser = UserSession.getInstance().getLoggedUser();

        if (loggedUser != null) {
            try {
                LOGGER.log(Level.INFO, "Synchronously validating student dashboard " 
                        + "session state metadata indicators.");
                
                StudentDAO studentDAO = new StudentDAO();
                this.currentStudent = studentDAO.getStudentByEmail(
                        loggedUser.getEmail());

                if (this.currentStudent != null) {
                    if (STUDENT_STATUS_ACREDITED.equalsIgnoreCase(
                            this.currentStudent.getStatus())) {
                        this.lockSystemForAcreditedStudent(
                                this.currentStudent.getFirstName());
                        return;
                    }

                    String fullName = this.currentStudent.getFirstName() + " "
                            + this.currentStudent.getPaternalLastName() + " "
                            + this.currentStudent.getMaternalLastName();

                    this.welcomeLabel.setText(WELCOME_PREFIX + fullName);

                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter
                            .ofPattern("dd/MM/yyyy HH:mm");
                    this.sessionDetailsLabel.setText(SESSION_DATE_PREFIX 
                            + now.format(formatter));

                    this.evaluateAndStyleButtons();
                }
            } catch (DatabaseSystemException exception) {
                LOGGER.log(Level.SEVERE, "Server infrastructure data pipeline " 
                        + "failure updating workspace elements.", exception);
                
                this.welcomeLabel.setText(WELCOME_PREFIX + loggedUser.getEmail());
                this.sessionDetailsLabel.setText(SESSION_DATE_PREFIX 
                        + LOCAL_MODE_LABEL);
            }
        }
    }

    private void lockSystemForAcreditedStudent(String studentName) {
        LOGGER.log(Level.INFO, "Student process concluded successfully via database " 
                + "status check. Conmutating layouts context into congratulations view.");

        this.welcomeLabel.setText("¡Muchas felicidades, " + studentName + "!");
        this.sessionDetailsLabel.setText("Acreditación de Prácticas UV");
        this.congratulationsHeaderLabel.setText("¡Muchas felicidades, " 
                + studentName + "!");

        this.menuOptionsGridPane.setVisible(false);
        this.menuOptionsGridPane.setManaged(false);
        this.congratulationsVBox.setVisible(true);
        this.congratulationsVBox.setManaged(true);
    }

    private void evaluateAndStyleButtons() {
        if (this.currentStudent != null) {
            this.hasMinimumCredits = this.currentStudent.getCoveredCredits() 
                    >= MINIMUM_CREDITS_FOR_PRACTICES;
            this.hasEducativeExperienceAssigned = this.currentStudent.getNrc() 
                    > RESET;
            this.hasProjectAssigned = this.currentStudent.getIdProject() > RESET;
            this.hasHoursForPartialReport = this.currentStudent.getCoveredHours() 
                    >= REQUIRED_HOURS_PARTIAL_REPORT;
            this.hasHoursForFinalReport = this.currentStudent.getCoveredHours() 
                    >= REQUIRED_HOURS_FINAL_REPORT;

            this.selectProjectsButton.setOpacity(1.0);
            this.uploadFormatsButton.setOpacity(1.0);
            this.generateReportButton.setOpacity(1.0);
            this.generatePartialReportButton.setOpacity(1.0);
            this.generateFinalReportButton.setOpacity(1.0);
            this.addReportButton.setOpacity(1.0);
            this.selfEvaluationButton.setOpacity(1.0);

            if (!this.hasMinimumCredits || !this.hasEducativeExperienceAssigned) {
                this.setAllOperationalButtonsOpacity(0.5);
                return;
            }

            if (this.hasProjectAssigned) {
                this.selectProjectsButton.setOpacity(0.5);
            } else {
                this.uploadFormatsButton.setOpacity(0.5);
                this.generateReportButton.setOpacity(0.5);
                this.generatePartialReportButton.setOpacity(0.5);
                this.generateFinalReportButton.setOpacity(0.5);
                this.addReportButton.setOpacity(0.5);
                this.selfEvaluationButton.setOpacity(0.5);
                return;
            }

            if (!this.hasHoursForPartialReport) {
                this.generatePartialReportButton.setOpacity(0.5);
            }

            if (!this.hasHoursForFinalReport) {
                this.generateFinalReportButton.setOpacity(0.5);
                this.selfEvaluationButton.setOpacity(0.5);
            }
        }
    }

    private void setAllOperationalButtonsOpacity(double opacityValue) {
        this.selectProjectsButton.setOpacity(opacityValue);
        this.uploadFormatsButton.setOpacity(opacityValue);
        this.generateReportButton.setOpacity(opacityValue);
        this.generatePartialReportButton.setOpacity(opacityValue);
        this.generateFinalReportButton.setOpacity(opacityValue);
        this.addReportButton.setOpacity(opacityValue);
        this.selfEvaluationButton.setOpacity(opacityValue);
    }

    @FXML
    private void handleSelectProjects(ActionEvent event) {
        if (!this.hasMinimumCredits) {
            AlertUtility.showWarningAlert("Alumno No Elegible",
                    "No cuentas con el 70% de créditos necesarios.");
        } else if (!this.hasEducativeExperienceAssigned) {
            AlertUtility.showWarningAlert("Inscripción Requerida",
                    "Aún no estás asignado a ninguna sección de la EE.");
        } else if (this.hasProjectAssigned) {
            AlertUtility.showWarningAlert("Proyecto Ya Asignado",
                    "Tu cuenta ya se encuentra vinculada a un proyecto.");
        } else {
            NavigationUtility.navigateTo(this.logoutButton, 
                    ViewConstants.VIEW_PROJECT_SELECTION, 
                    TITLE_PROJECT_SELECTION);
        }
    }

    @FXML
    private void handleUploadFormats(ActionEvent event) {
        if (this.hasProjectAssigned) {
            NavigationUtility.navigateTo(this.logoutButton, 
                    ViewConstants.VIEW_INITIAL_FORMATS_UPLOAD, TITLE_UPLOAD_FORMATS);
        } else {
            AlertUtility.showWarningAlert("Asignación Requerida", 
                    "No puedes subir formatos sin un proyecto asignado.");
        }
    }

    @FXML
    private void handleGenerateReport(ActionEvent event) {
        if (!this.hasEducativeExperienceAssigned || !this.hasProjectAssigned) {
            AlertUtility.showWarningAlert("Acceso Restringido",
                    "Debes contar con una sección de EE inscrita.");
            return;
        }

        LOGGER.log(Level.INFO, "Routing context towards monthly tracking wizard.");
        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_GENERATE_REPORT_FORM, TITLE_STAGE_REPORT_FORM);
    }

    @FXML
    private void handleGeneratePartialReport(ActionEvent event) {
        if (!this.hasProjectAssigned) {
            AlertUtility.showWarningAlert("Asignación Requerida", 
                    "No puedes estructurar informes sin un proyecto.");
            return;
        }

        if (!this.hasHoursForPartialReport) {
            AlertUtility.showWarningAlert("Bloqueo por Horas",
                    "El informe parcial requiere mínimo 210 horas validadas.\n\n"
                    + "Horas actuales: " + this.currentStudent.getCoveredHours());
            return;
        }

        LOGGER.log(Level.INFO, "Routing context towards partial report milestone.");
        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_GENERATE_PARTIAL_REPORT, 
                TITLE_STAGE_PARTIAL_REPORT);
    }

    @FXML
    private void handleGenerateFinalReport(ActionEvent event) {
        if (!this.hasProjectAssigned) {
            AlertUtility.showWarningAlert("Asignación Requerida", 
                    "No puedes estructurar el informe sin un proyecto.");
            return;
        }

        if (!this.hasHoursForFinalReport) {
            AlertUtility.showWarningAlert("Informe Final Bloqueado",
                    "El informe definitivo requiere concluir las 420 horas.\n\n"
                    + "Horas actuales: " + this.currentStudent.getCoveredHours());
            return;
        }

        LOGGER.log(Level.INFO, "Routing context towards definitive closing milestone.");        
        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_GENERATE_FINAL_REPORT, TITLE_STAGE_FINAL_REPORT);
    }

    @FXML
    private void handleNavigateToAddReport(ActionEvent event) {
        if (this.hasProjectAssigned) {
            NavigationUtility.navigateTo(this.logoutButton, 
                    ViewConstants.VIEW_ADD_REPORT, TITLE_REPORTS_AVAILABLE);
        } else {
            AlertUtility.showWarningAlert("Asignación Requerida", 
                    "No puedes entregar reportes sin un proyecto vinculado.");
        }
    }

    @FXML
    private void handleNavigateToSelfEvaluation(ActionEvent event) {
        if (!this.hasHoursForFinalReport) {
            AlertUtility.showWarningAlert("Evaluación Bloqueada",
                    "La autoevaluación requiere concluir las 420 horas.");
            return;
        }

        try {
            LOGGER.log(Level.INFO, "Initiating synchronous scene root replacement " 
                    + "for self evaluation module initialization.");
            
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource(ViewConstants.VIEW_GENERATE_SELF_EVALUATION));
            Parent root = loader.load();
            
            GenerateSelfEvaluationViewController controller = loader.getController();
            controller.initializeSelfEvaluationContext(this.currentStudent);
            
            Stage stage = (Stage) this.logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Server framework failure loading self " 
                    + "evaluation window template context.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "cargar la interfaz de autoevaluación.");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        LOGGER.log(Level.INFO, "Explicit student logout flow requested.");
        UserSession.getInstance().logout();
        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_LOGIN, TITLE_LOGIN);
    }
}