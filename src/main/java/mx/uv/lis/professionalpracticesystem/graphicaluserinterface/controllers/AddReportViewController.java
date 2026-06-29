package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.DeadlineDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ReportDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DeadlineDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DeadlineRowAdapterDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IDeadlineDAO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IReportDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;

/**
 *
 * @author andre
 * @author cinth
 */
public class AddReportViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            AddReportViewController.class.getName());
    private final IDeadlineDAO deadlineDAO;
    private final IReportDAO reportDAO;
    private final StudentDAO studentDAO;
    private ObservableList<DeadlineRowAdapterDTO> deadlineRowsList;
    public static String targetFilterType = "Todos";
    private StudentDTO currentStudent;

    @FXML
    private TableView<DeadlineRowAdapterDTO> deadlinesTableView;
    @FXML
    private TableColumn<DeadlineRowAdapterDTO, String> reportTypeTableColumn;
    @FXML
    private TableColumn<DeadlineRowAdapterDTO, String> endDateTableColumn;
    @FXML
    private TableColumn<DeadlineRowAdapterDTO, String> endTimeTableColumn;
    @FXML
    private TableColumn<DeadlineRowAdapterDTO, String> statusTableColumn;
    @FXML
    private TableColumn<DeadlineRowAdapterDTO, String> reviewStatusTableColumn;

    @FXML
    private Button uploadPdfButton;
    @FXML
    private Button backButton;

    public AddReportViewController() {
        this.deadlineDAO = new DeadlineDAO();
        this.reportDAO = new ReportDAO();
        this.studentDAO = new StudentDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.configureTableColumns();
        this.loadDeadlinesAndTrackingData();
    }

    private void configureTableColumns() {
        this.reportTypeTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("reportType"));
        this.endDateTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("formattedDate"));
        this.endTimeTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("formattedTime"));
        this.statusTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("deliveryStatus"));
        this.reviewStatusTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("revisionStatus"));
    }

    private void loadDeadlinesAndTrackingData() {
        String studentEmail = UserSession.getInstance().getLoggedUser()
                .getEmail();

        try {
            LOGGER.log(Level.INFO, "Executing synchronous data tracking " 
                    + "retrieval flow for students catalog details.");
            
            this.currentStudent = this.studentDAO
                    .getStudentByEmail(studentEmail);
            
            String enrollment = studentEmail;
            if (this.currentStudent != null) {
                enrollment = this.currentStudent.getEnrollmentId();
            }

            List<DeadlineDTO> deadlines = this.deadlineDAO
                    .getDeadlinesByStudentEnrollment(enrollment);
            List<ReportDTO> studentReports = this.reportDAO
                    .getReportsByEnrollment(enrollment);
            List<DeadlineRowAdapterDTO> adapters = new ArrayList<>();

            for (DeadlineDTO deadline : deadlines) {
                String reportType = deadline.getReportType();
                boolean includeFile = false;

                if (SystemConstants.FILTER_ALL.equalsIgnoreCase(
                        targetFilterType)) {
                    
                    if (SystemConstants.MONTHLY_REPORT_KEYWORD
                            .equalsIgnoreCase(reportType) 
                            || SystemConstants.PARTIAL_REPORT_KEYWORD
                            .equalsIgnoreCase(reportType) 
                            || SystemConstants.FINAL_REPORT_KEYWORD
                            .equalsIgnoreCase(reportType)
                            || "Autoevaluación".equalsIgnoreCase(reportType)) {
                        includeFile = true;
                    }
                } else {
                    if (reportType.equalsIgnoreCase(targetFilterType)) {
                        includeFile = true;
                    }
                }

                if (includeFile) {
                    ReportDTO matchedReport = null;
                    for (ReportDTO report : studentReports) {
                        if (report.getReportType().equalsIgnoreCase(reportType) 
                                && report.getReportedHours() == deadline
                                .getReportedNumber()) {
                            matchedReport = report;
                            break;
                        }
                    }
                    adapters.add(new DeadlineRowAdapterDTO(
                            deadline, matchedReport));
                }
            }

            this.handleLoadCatalogSuccess(adapters);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server infrastructure network layer " 
                    + "failure within student catalog monitoring.");
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar el estado de los plazos desde el servidor.");
        }
    }

    public void handleLoadCatalogSuccess(List<DeadlineRowAdapterDTO> adapters) {
        this.deadlineRowsList = FXCollections.observableArrayList(adapters);
        this.deadlinesTableView.setItems(this.deadlineRowsList);
        LOGGER.log(Level.INFO, "Deliveries synchronized successfully in the " 
                + "UI grid context under category: {0}", targetFilterType);
    }

    @FXML
    private void handleUploadPdf(ActionEvent event) {
        DeadlineRowAdapterDTO selectedRow = this.deadlinesTableView
                .getSelectionModel().getSelectedItem();

        if (selectedRow == null) {
            AlertUtility.showWarningAlert("Selección Requerida", "Por favor, " 
                    + "seleccione un plazo de entrega de la tabla.");
            return;
        }

        try {
            if (this.currentStudent == null) {
                AlertUtility.showErrorAlert("Error de conexión", "No se han " 
                        + "podido validar sus credenciales académicas.");
                return;
            }

            String studentEnrollment = this.currentStudent.getEnrollmentId()
                    .toUpperCase().trim();
            String reportType = selectedRow.getPureReportType();
            int reportNumber = selectedRow.getReportNumber();

            if ("Mensual".equalsIgnoreCase(reportType) && reportNumber > 1) {
                int expectedPreviousNumber = reportNumber - 1;
                boolean foundPrevious = false;
                
                List<ReportDTO> studentReports = this.reportDAO
                        .getReportsByEnrollment(studentEnrollment);
                
                for (ReportDTO report : studentReports) {
                    if ("Mensual".equalsIgnoreCase(report.getReportType()) 
                            && report.getReportedHours() 
                            == expectedPreviousNumber) {
                        foundPrevious = true;
                        break;
                    }
                }
                
                if (!foundPrevious) {
                    AlertUtility.showWarningAlert("Entrega Bloqueada", 
                            "No puede subir el Reporte Mensual " + reportNumber 
                            + " debido a que no ha entregado la versión " 
                            + expectedPreviousNumber + " en el sistema.");
                    return;
                }
            }

            ReportDAO checkDAO = new ReportDAO();
            boolean alreadyExists = checkDAO.isReportSubmitted(
                    studentEnrollment, reportType, reportNumber);

            if (alreadyExists) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Aviso de Entrega Existente");
                confirmAlert.setHeaderText("Documento previamente cargado");
                confirmAlert.setContentText("Ya has enviado un archivo PDF " 
                        + "para este periodo.\n\n¿Deseas sobreescribirlo?");

                Optional<ButtonType> response = confirmAlert.showAndWait();
                if (response.isPresent() && response.get() != ButtonType.OK) {
                    LOGGER.log(Level.INFO, "Navigation to upload wizard " 
                            + "aborted by the student statement.");
                    return;
                }
            }

            this.navigateToUploadWizard(reportType, reportNumber, 
                    selectedRow.getDeliveryStatus());

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Technical error checking previous " 
                    + "report submission status data mapping.");
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "validar el estado de las entregas en el servidor.");
        }
    }

    private void navigateToUploadWizard(String reportType, int reportNumber, 
            String deliveryStatus) {
        try {
            LOGGER.log(Level.INFO, "Initiating sequential context transition " 
                    + "to UploadReportView forwarding context metadata token.");
            
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource(ViewConstants.VIEW_UPLOAD_REPORT));
            Parent root = loader.load();
            UploadReportViewController wizardController = loader.getController();

            wizardController.initializeContextMetadata(
                    reportType, reportNumber, deliveryStatus);

            Stage stage = (Stage) this.uploadPdfButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema de Prácticas Profesionales - Carga de " 
                    + reportType);
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "GUI framework lifecycle pipeline crash: " 
                    + "Failed to transfer metadata to target wizard view.", 
                    exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "desplegar el asistente de carga desde el servidor.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting tracking operational flow. Routing " 
                + "client stage context back to StudentMenuView.");
        
        NavigationUtility.navigateTo(this.backButton, 
                ViewConstants.VIEW_STUDENT_MENU, 
                SystemConstants.TITLE_MENU_STUDENT);
    }
}