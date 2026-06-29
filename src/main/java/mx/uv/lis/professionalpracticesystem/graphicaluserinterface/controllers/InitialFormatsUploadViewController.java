package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.StudentController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.DocumentDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DocumentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_ACCEPTANCE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_ACTIVITY;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_EVALUATION;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_SCHEDULE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_INSURANCE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_FILE_SIZE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_FILE_SIZE_MB;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PDF_EXTENSION_DESCRIPTION;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PDF_EXTENSION_FILTER;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;

/**
 *
 * @author cinth
 * @author andre
 */
public class InitialFormatsUploadViewController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(
            InitialFormatsUploadViewController.class.getName());

    @FXML
    private Label studentNameLabel;
    @FXML
    private Label enrollmentLabel;
    @FXML
    private TextField assignmentLetterPathTextField;
    @FXML
    private TextField schedulePathTextField;
    @FXML
    private TextField insuranceCertificatePathTextField;
    @FXML
    private TextField organizationEvaluationPathTextField;
    @FXML
    private TextField activitySchedulePathTextField;
    @FXML
    private Button cancelUploadButton;   
    @FXML
    private Button browseAssignmentLetterButton;
    @FXML
    private Button browseScheduleButton;
    @FXML
    private Button browseInsuranceCertificateButton;
    @FXML
    private Button browseOrganizationEvaluationButton;
    @FXML
    private Button browseActivityScheduleButton;
    @FXML
    private Button uploadFilesButton;

    private final StudentController studentController;
    private final StudentDAO studentDAO;
    private final DocumentDAO documentDAO;
    private StudentDTO currentStudent;
    private final Map<String, File> selectedFiles;

    public InitialFormatsUploadViewController() {
        this.studentController = new StudentController();
        this.studentDAO = new StudentDAO();
        this.documentDAO = new DocumentDAO();
        this.selectedFiles = new HashMap<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.filePathTextFieldsConfiguration();
        this.loadStudentData();
    }

    private void filePathTextFieldsConfiguration() {
        this.assignmentLetterPathTextField.setEditable(false);
        this.schedulePathTextField.setEditable(false);
        this.insuranceCertificatePathTextField.setEditable(false);
        this.organizationEvaluationPathTextField.setEditable(false);
        this.activitySchedulePathTextField.setEditable(false);
    }

    private void loadStudentData() {
        try {
            if (UserSession.getInstance().getLoggedUser() != null) {
                String email = UserSession.getInstance().getLoggedUser()
                        .getEmail();
                
                LOGGER.log(Level.INFO, "Fetching student identity profile " 
                        + "synchronously from server session context.");
                this.currentStudent = this.studentDAO.getStudentByEmail(email);

                if (this.currentStudent != null) {
                    String fullName = this.currentStudent.getFirstName() + " "
                            + this.currentStudent.getPaternalLastName() + " "
                            + this.currentStudent.getMaternalLastName();
                    this.studentNameLabel.setText(fullName);
                    this.enrollmentLabel.setText(this.currentStudent
                            .getEnrollmentId());
                    
                    this.checkAndCongealApprovedFields();
                }
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server session identity profile lookup " 
                    + "failure within current view context.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar la información del alumno desde el servidor.");
        }
    }

    private void checkAndCongealApprovedFields() {
        try {
            LOGGER.log(Level.INFO, "Requesting synchronous evaluation state " 
                    + "summary for initial documents expediente validation.");
            
            Map<String, String> currentSummary = this.studentController
                    .getStudentDocumentStatusSummary(this.currentStudent
                    .getEnrollmentId());
            
            if (currentSummary != null && !currentSummary.isEmpty()) {
                String validationText = "Formato validado y aprobado";
                
                if ("Aprobado".equalsIgnoreCase(currentSummary
                        .get(DOC_TYPE_ACCEPTANCE))) {
                    this.assignmentLetterPathTextField.setPromptText(
                            validationText);
                    this.browseAssignmentLetterButton.setDisable(true);
                }
                
                if ("Aprobado".equalsIgnoreCase(currentSummary
                        .get(DOC_TYPE_SCHEDULE))) {
                    this.schedulePathTextField.setPromptText(validationText);
                    this.browseScheduleButton.setDisable(true);
                }
                
                if ("Aprobado".equalsIgnoreCase(currentSummary
                        .get(DOC_TYPE_INSURANCE))) {
                    this.insuranceCertificatePathTextField.setPromptText(
                            validationText);
                    this.browseInsuranceCertificateButton.setDisable(true);
                }
                
                if ("Aprobado".equalsIgnoreCase(currentSummary
                        .get(DOC_TYPE_ACTIVITY))) {
                    this.activitySchedulePathTextField.setPromptText(
                            validationText);
                    this.browseActivityScheduleButton.setDisable(true);
                }
                
                if ("Aprobado".equalsIgnoreCase(currentSummary
                        .get(DOC_TYPE_EVALUATION))) {
                    this.organizationEvaluationPathTextField.setPromptText(
                            validationText);
                    this.browseOrganizationEvaluationButton.setDisable(true);
                }
                
                if (this.isEverythingApproved(currentSummary)) {
                    this.uploadFilesButton.setDisable(true);
                    this.uploadFilesButton.setText("Expediente Completo");
                }
            }

            boolean isEvaluationApproved = currentSummary != null && "Aprobado"
                    .equalsIgnoreCase(currentSummary.get(DOC_TYPE_EVALUATION));

            if (!isEvaluationApproved && this.currentStudent.getCoveredHours() 
                    < SystemConstants.REQUIRED_HOURS_FINAL_REPORT) {
                
                this.organizationEvaluationPathTextField.setText("");
                this.organizationEvaluationPathTextField.setPromptText(
                        "Bloqueado: Requiere un mínimo de 420 horas.");
                this.browseOrganizationEvaluationButton.setDisable(true);
            }
            
            boolean isActivityApproved = currentSummary != null && "Aprobado"
                    .equalsIgnoreCase(currentSummary.get(DOC_TYPE_ACTIVITY));
                    
            if (!isActivityApproved) {
                this.browseActivityScheduleButton.setDisable(false);
            }
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server data pipeline failure handling " 
                    + "preventive GUI congeal state rules context.");
        }
    }

    private boolean isEverythingApproved(Map<String, String> summary) {
        return "Aprobado".equalsIgnoreCase(summary.get(DOC_TYPE_ACCEPTANCE))
                && "Aprobado".equalsIgnoreCase(summary.get(DOC_TYPE_SCHEDULE))
                && "Aprobado".equalsIgnoreCase(summary.get(DOC_TYPE_INSURANCE))
                && "Aprobado".equalsIgnoreCase(summary.get(DOC_TYPE_EVALUATION))
                && "Aprobado".equalsIgnoreCase(summary.get(DOC_TYPE_ACTIVITY));
    }

    @FXML
    private void handleBrowseAcceptance(ActionEvent event) {
        this.selectFile(DOC_TYPE_ACCEPTANCE, 
                this.assignmentLetterPathTextField);
    }

    @FXML
    private void handleBrowseSchedule(ActionEvent event) {
        this.selectFile(DOC_TYPE_SCHEDULE, this.schedulePathTextField);
    }

    @FXML
    private void handleBrowseInsurance(ActionEvent event) {
        this.selectFile(DOC_TYPE_INSURANCE, 
                this.insuranceCertificatePathTextField);
    }

    @FXML
    private void handleBrowseEvaluation(ActionEvent event) {
        this.selectFile(DOC_TYPE_EVALUATION, 
                this.organizationEvaluationPathTextField);
    }

    @FXML
    private void handleBrowseActivity(ActionEvent event) {
        this.selectFile(DOC_TYPE_ACTIVITY, this.activitySchedulePathTextField);
    }

    private void selectFile(String type, TextField targetTextField) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar " + type);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                PDF_EXTENSION_DESCRIPTION, PDF_EXTENSION_FILTER));
        
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            if (file.length() > MAX_FILE_SIZE) {
                AlertUtility.showWarningAlert("Archivo Excedido", "El archivo " 
                        + "supera el límite permitido de " + MAX_FILE_SIZE_MB 
                        + " MB.");
                return;
            }
            targetTextField.setText(file.getAbsolutePath());
            this.selectedFiles.put(type, file);
            
            LOGGER.log(Level.INFO, "Local document file resource mapping selected " 
                    + "for upload: {0}", file.getName());
        }
    }

    @FXML
    private void handleUploadFiles(ActionEvent event) {
        if (this.selectedFiles.isEmpty()) {
            AlertUtility.showWarningAlert("Sin archivos", "Por favor, " 
                    + "seleccione al menos un archivo para subir.");
            return;
        }

        try {
            Map<String, String> currentSummary = this.studentController
                    .getStudentDocumentStatusSummary(this.currentStudent
                    .getEnrollmentId());

            if (currentSummary != null && !currentSummary.isEmpty() 
                    && this.evaluateExistingOverwrites(currentSummary)) {
                
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Advertencia de Entrega");
                alert.setHeaderText("Formatos detectados en el sistema");
                alert.setContentText("Ya cuentas con archivos entregados " 
                        + "previamente. ¿Deseas sobreescribir con las nuevas " 
                        + "versiones? (Regresarán a estado Pendiente).");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK) {
                    return;
                }
            }

            this.processDocumentsPersistence();

        } catch (IOException | DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server writing transaction pipeline failure " 
                    + "processing documents file upload sequence stream.", 
                    exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "Ocurrió un " 
                    + "problema al procesar la entrega en el servidor central.");
        }
    }

    private boolean evaluateExistingOverwrites(
            Map<String, String> statusSummary) {
        return statusSummary.containsKey(DOC_TYPE_ACCEPTANCE)
                || statusSummary.containsKey(DOC_TYPE_SCHEDULE)
                || statusSummary.containsKey(statusSummary.get(
                DOC_TYPE_INSURANCE))
                || statusSummary.containsKey(DOC_TYPE_ACTIVITY)
                || statusSummary.containsKey(DOC_TYPE_EVALUATION);
    }

    private void processDocumentsPersistence() 
            throws IOException, DatabaseSystemException {
        int uploadedCount = RESET;

        for (Map.Entry<String, File> entry : this.selectedFiles.entrySet()) {
            String type = entry.getKey();
            File file = entry.getValue();

            DocumentDTO doc = new DocumentDTO();
            doc.setFileName(file.getName());
            doc.setFileData(Files.readAllBytes(file.toPath()));
            doc.setDocumentType(type);
            doc.setEnrollment(this.currentStudent.getEnrollmentId());
            doc.setDeliveryDate(new java.sql.Date(System.currentTimeMillis()));
            doc.setReviewStatus("Pendiente");

            this.documentDAO.saveDocument(doc);
            uploadedCount++;
        }

        AlertUtility.showInformationAlert("Carga Exitosa", "Se han " 
                + "procesado " + uploadedCount + " archivos correctamente. " 
                + "Quedan a la espera de aprobación.");
        
        this.checkAndCongealApprovedFields();
        this.clearFields();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting document formats deployment session. " 
                + "Routing view context screen back to StudentMenuView.");

        NavigationUtility.navigateTo(this.cancelUploadButton, 
                ViewConstants.VIEW_STUDENT_MENU, 
                SystemConstants.TITLE_MENU_STUDENT);
    }

    private void clearFields() {
        this.assignmentLetterPathTextField.clear();
        this.schedulePathTextField.clear();
        this.insuranceCertificatePathTextField.clear();
        this.organizationEvaluationPathTextField.clear();
        this.activitySchedulePathTextField.clear();
        this.selectedFiles.clear();
    }
}