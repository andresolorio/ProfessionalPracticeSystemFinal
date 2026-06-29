package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.ReportController;
import mx.uv.lis.professionalpracticesystem.logic.controllers.StudentController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.DocumentDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ReportDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DocumentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_ACCEPTANCE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_SCHEDULE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_INSURANCE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_ACTIVITY;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;

/**
 * 
 * @author cinth
 * @author andre
 */
public class UploadReportViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            UploadReportViewController.class.getName());
    private final ReportController reportController;
    private final StudentController studentController;
    private final StudentDAO studentDAO;
    private final ReportDAO reportDAO;
    private final DocumentDAO documentDAO;
    
    private String reportTypeMetadata;
    private int reportNumberMetadata;
    private String deliveryStatusMetadata;
    private File selectedFile;
    private StudentDTO currentStudent;

    @FXML
    private Label reportTypeLabel;
    @FXML
    private Label deliveryStatusLabel;
    @FXML
    private TextField filePathTextField;
    @FXML
    private Button browseButton;
    @FXML
    private Button submitButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button viewServerPdfButton;

    public UploadReportViewController() {
        this.reportController = new ReportController();
        this.studentController = new StudentController();
        this.studentDAO = new StudentDAO();
        this.reportDAO = new ReportDAO();
        this.documentDAO = new DocumentDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.filePathTextField.setEditable(false);
        this.viewServerPdfButton.setVisible(false);
        this.viewServerPdfButton.setManaged(false);
    }

    public void initializeContextMetadata(String reportType, int reportNumber, 
            String deliveryStatus) {
        this.reportTypeMetadata = reportType;
        this.reportNumberMetadata = reportNumber;
        this.deliveryStatusMetadata = deliveryStatus;
        
        try {
            String loggedEmail = UserSession.getInstance().getLoggedUser()
                    .getEmail();
            this.currentStudent = this.studentDAO.getStudentByEmail(loggedEmail);
            
            if (this.currentStudent != null) {
                String enrollment = this.currentStudent.getEnrollmentId()
                        .toUpperCase().trim();
                
                Map<String, String> statusSummary = this.studentController
                        .getStudentDocumentStatusSummary(enrollment);
                
                if (this.isExpedientIncomplete(statusSummary)) {
                    this.browseButton.setDisable(true);
                    this.submitButton.setVisible(false);
                    this.submitButton.setManaged(false);
                    this.filePathTextField.setPromptText("Expediente " 
                            + "bloqueado por falta de aprobación");
                    
                    AlertUtility.showWarningAlert("Expediente Bloqueado", 
                            "No tienes permitido subir entregas ni reportes " 
                            + "en este momento.\n\nMotivo: Tus 4 formatos " 
                            + "iniciales obligatorios deben ser previamente " 
                            + "revisados y aprobados por tu profesor.");
                    return;
                }

                List<ReportDTO> pastReports = this.reportDAO
                        .getReportsByEnrollment(enrollment);
                ReportDTO activeReport = null;
                
                for (ReportDTO report : pastReports) {
                    if (report.getReportType().equalsIgnoreCase(this
                            .reportTypeMetadata) && report.getReportedHours() 
                            == this.reportNumberMetadata) {
                        activeReport = report;
                        break;
                    }
                }

                if (activeReport != null) {
                    this.viewServerPdfButton.setVisible(true);
                    this.viewServerPdfButton.setManaged(true);

                    if ("Aprobado".equalsIgnoreCase(activeReport
                            .getReviewStatus())) {
                        this.congealInterfaceForApprovedReport();
                        return;
                    } else {
                        this.filePathTextField.setPromptText("Visualiza tu " 
                                + "entrega actual o carga una nueva versión");
                    }
                }
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server failure retrieving student " 
                    + "credentials backup context alignment.", exception);
        }

        if ("Mensual".equalsIgnoreCase(this.reportTypeMetadata)) {
            this.reportTypeLabel.setText("Reporte Mensual (Entrega " 
                    + this.reportNumberMetadata + ")");
        } else if ("Parcial".equalsIgnoreCase(this.reportTypeMetadata)) {
            this.reportTypeLabel.setText("Reporte Parcial");
        } else if ("Final".equalsIgnoreCase(this.reportTypeMetadata)) {
            this.reportTypeLabel.setText("Informe Final");
        } else {
            this.reportTypeLabel.setText(this.reportTypeMetadata);
        }

        this.deliveryStatusLabel.setText(this.deliveryStatusMetadata);
    }

    private boolean isExpedientIncomplete(Map<String, String> summary) {
        return summary == null || summary.isEmpty()
                || !"Aprobado".equals(summary.get(DOC_TYPE_ACCEPTANCE))
                || !"Aprobado".equals(summary.get(DOC_TYPE_SCHEDULE))
                || !"Aprobado".equals(summary.get(DOC_TYPE_INSURANCE))
                || !"Aprobado".equals(summary.get(DOC_TYPE_ACTIVITY));
    }

    private void congealInterfaceForApprovedReport() {
        this.browseButton.setDisable(true);
        this.submitButton.setVisible(false);
        this.submitButton.setManaged(false);
        this.filePathTextField.setPromptText("Archivo resguardado y " 
                + "aprobado por el profesor");     
        this.viewServerPdfButton.setVisible(true);
        this.viewServerPdfButton.setManaged(true);
        
        AlertUtility.showInformationAlert("Reporte Aprobado", 
                "Este reporte ya cuenta con la validación de APROBADO por " 
                + "parte de tu profesor.\n\nNo es posible realizar " 
                + "modificaciones.");
    }

    @FXML
    private void handleBrowseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Reporte Firmado (Formato PDF)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Archivos PDF (*.pdf)", "*.pdf"));

        Stage stage = (Stage) this.browseButton.getScene().getWindow();
        this.selectedFile = fileChooser.showOpenDialog(stage);

        if (this.selectedFile != null) {
            this.filePathTextField.setText(this.selectedFile.getAbsolutePath());
            LOGGER.log(Level.INFO, "Student selected local PDF file target " 
                    + "for upload: {0}", this.selectedFile.getName());
        }
    }

    @FXML
    private void handleViewServerPdf(ActionEvent event) {
        try {
            this.viewServerPdfButton.setDisable(true);
            String enrollment = this.currentStudent.getEnrollmentId()
                    .toUpperCase().trim();
            
            LOGGER.log(Level.INFO, "Initiating synchronous retrieval flow " 
                    + "for approved report file payload stream from server.");
            
            List<ReportDTO> reports = this.reportDAO.getReportsByEnrollment(
                    enrollment);
            ReportDTO targetReport = null;
            
            for (ReportDTO report : reports) {
                if (report.getReportType().equalsIgnoreCase(this
                        .reportTypeMetadata) && report.getReportedHours() 
                        == this.reportNumberMetadata) {
                    targetReport = report;
                    break;
                }
            }
            
            if (targetReport != null && targetReport.getFileContent() != null) {
                String prefix = "SPP_Servidor_" + enrollment + "_";
                java.nio.file.Path tempPath = Files.createTempFile(
                        prefix, ".pdf");
                Files.write(tempPath, targetReport.getFileContent());
                
                File resultFile = tempPath.toFile();
                resultFile.deleteOnExit();
                
                if (Desktop.isDesktopSupported() && resultFile != null) {
                    Desktop.getDesktop().open(resultFile);
                } else {
                    AlertUtility.showWarningAlert("Entorno No Soportado", 
                            "Su sistema operativo no soporta la apertura " 
                            + "nativa de archivos externos.");
                }
            } else {
                throw new DatabaseSystemException("No se encontró el contenido " 
                        + "del documento solicitado en el servidor.");
            }
        } catch (IOException | DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server file payload recovery or OS native " 
                    + "desktop handler execution crash.", exception);
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudieron " 
                    + "recuperar o abrir los datos del reporte desde el servidor.");
        } finally {
            this.viewServerPdfButton.setDisable(false);
        }
    }

    @FXML
    private void handleSubmitDelivery(ActionEvent event) {
        if (this.selectedFile == null) {
            AlertUtility.showWarningAlert("Archivo Requerido", "Por favor " 
                    + "seleccione un archivo PDF desde su equipo.");
            return;
        }

        if (this.currentStudent == null) {
            AlertUtility.showErrorAlert("Error de Contexto", "No se han " 
                    + "podido validar las credenciales del estudiante.");
            return;
        }

        try {
            this.submitButton.setDisable(true);
            String cleanEnrollment = this.currentStudent.getEnrollmentId()
                    .toUpperCase().trim(); 
            
            LOGGER.log(Level.INFO, "Executing synchronous processing transaction " 
                    + "for student report payload submission stream.");
            
            boolean alreadyExists = this.reportDAO.isReportSubmitted(
                    cleanEnrollment, this.reportTypeMetadata, 
                    this.reportNumberMetadata);

            byte[] fileBytes = Files.readAllBytes(this.selectedFile.toPath());

            ReportDTO report = new ReportDTO();
            report.setStudentEnrollment(cleanEnrollment); 
            report.setReportType(this.reportTypeMetadata);
            report.setReportedHours(this.reportNumberMetadata);
            report.setHoursCovered(SystemConstants.RESET);
            report.setDeliveryDate(new java.sql.Date(System.currentTimeMillis()));
            report.setDeliveryStatus(this.deliveryStatusMetadata);
            report.setReviewStatus("Pendiente");
            report.setFileContent(fileBytes);

            if (alreadyExists) {
                this.reportDAO.overwriteReport(report);
            } else {
                this.reportController.registerReport(report);
            }

            if ("Autoevaluación".equalsIgnoreCase(this.reportTypeMetadata) 
                    || "Evaluación OV".equalsIgnoreCase(this
                    .reportTypeMetadata)) {
                
                DocumentDTO docCheck = new DocumentDTO();
                docCheck.setFileName(this.selectedFile.getName());
                docCheck.setFileData(fileBytes);
                docCheck.setDocumentType(this.reportTypeMetadata);
                docCheck.setEnrollment(cleanEnrollment);
                docCheck.setDeliveryDate(new java.sql.Date(
                        System.currentTimeMillis()));
                docCheck.setReviewStatus("Pendiente");
                
                this.documentDAO.saveDocument(docCheck);
            }

            AlertUtility.showInformationAlert("Entrega Exitosa", "Su reporte " 
                    + "ha sido enviado y guardado correctamente en el servidor.");
            this.navigateToCatalog();

        } catch (IOException | DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server writing transaction engineering " 
                    + "failure inside report upload pipeline.", exception);
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "completar la entrega en el servidor central.");
        } finally {
            this.submitButton.setDisable(false);
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        this.navigateToCatalog();
    }

    private void navigateToCatalog() {
        LOGGER.log(Level.INFO, "Routing view screen layout context " 
                + "back to available options catalog view.");
        
        NavigationUtility.navigateTo(this.cancelButton, 
                ViewConstants.VIEW_ADD_REPORT, 
                SystemConstants.TITLE_REPORTS_AVAILABLE);
    }
}