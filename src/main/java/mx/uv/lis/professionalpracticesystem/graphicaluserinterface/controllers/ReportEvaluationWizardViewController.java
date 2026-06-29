package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.logic.controllers.ProfessorController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ReportActivityDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityChecklistItemDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportActivityDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;

/**
 * 
 * @author andre
 * @author cinth
 */
public class ReportEvaluationWizardViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            ReportEvaluationWizardViewController.class.getName());
    private EvaluateReportsListViewController parentController;

    @FXML
    private Label enrollmentLabel;
    @FXML
    private Label reportTypeLabel;
    @FXML
    private Label deliveryStatusLabel;
    @FXML
    private Button downloadPdfButton;
    @FXML
    private ComboBox<String> evaluationComboBox;
    @FXML
    private VBox hoursVBox;
    @FXML
    private TextField hoursTextField;
    @FXML
    private TextArea observationsTextArea;
    @FXML
    private Button cancelButton;
    @FXML
    private Button submitButton;
    @FXML
    private TableView<ActivityChecklistItemDTO> checklistTableView;
    @FXML
    private TableColumn<ActivityChecklistItemDTO, Boolean> checkboxTableColumn;
    @FXML
    private TableColumn<ActivityChecklistItemDTO, String> activityNameTableColumn;

    private final ProfessorController professorController;
    private final ReportActivityDAO reportActivityDAO;
    private final ObservableList<ActivityChecklistItemDTO> checklistObservableList;
    private ReportDTO currentReport;

    public ReportEvaluationWizardViewController() {
        this.professorController = new ProfessorController();
        this.reportActivityDAO = new ReportActivityDAO();
        this.checklistObservableList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.evaluationComboBox.getItems().addAll(
                SystemConstants.REVIEW_STATUS_APPROVED, 
                SystemConstants.REVIEW_STATUS_REJECTED);
        this.hoursVBox.setVisible(false);
        this.hoursVBox.setManaged(false);

        this.configureChecklistTable();
    }

    private void configureChecklistTable() {
        this.checklistTableView.setEditable(true);
        
        this.activityNameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("name"));
        this.checkboxTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("completed"));
        
        this.checkboxTableColumn.setCellFactory(new CheckboxCellFactory());
        this.checklistTableView.setItems(this.checklistObservableList);
    }

    public void setParentController(
            EvaluateReportsListViewController parentController) {
        this.parentController = parentController;
    }

    public void setReportData(ReportDTO report) {
        this.currentReport = report;

        if (this.currentReport != null) {
            String decoratedType = this.currentReport.getReportType();
            if ("Mensual".equalsIgnoreCase(decoratedType)) {
                decoratedType = decoratedType + " (Entrega " 
                        + this.currentReport.getReportedHours() + ")";
            }

            this.enrollmentLabel.setText(this.currentReport
                    .getStudentEnrollment());
            this.reportTypeLabel.setText(decoratedType);
            this.deliveryStatusLabel.setText(this.currentReport
                    .getDeliveryStatus());

            this.loadProjectChecklist();

            if (!SystemConstants.REVIEW_STATUS_PENDING.equalsIgnoreCase(
                    this.currentReport.getReviewStatus())) {
                this.evaluationComboBox.getSelectionModel().select(
                        this.currentReport.getReviewStatus());
                this.observationsTextArea.setText(this.currentReport
                        .getObservations());
                this.handleEvaluationSelection(null);

                if (SystemConstants.REVIEW_STATUS_APPROVED.equalsIgnoreCase(
                        this.currentReport.getReviewStatus())) {
                    this.hoursTextField.setText(String.valueOf(
                            this.currentReport.getHoursCovered()));
                    this.congealEvaluationInterface();
                }
            }
        }
    }

    private void loadProjectChecklist() {
        try {
            LOGGER.log(Level.INFO, "Fetching project activities checklist " 
                    + "synchronously from the server repository.");
            
            int projectId = this.professorController.getStudentProjectId(
                    this.currentReport.getStudentEnrollment());
            
            List<ReportActivityDTO> baseChecklist = this.reportActivityDAO
                    .getChecklistByReport(this.currentReport.getIdReport(), 
                    projectId);
            
            this.handleLoadChecklistSuccess(baseChecklist);
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server infrastructure data failure " 
                    + "loading project activities checklist.", exception);
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar el catálogo de actividades del servidor.");
        }
    }

    public void handleLoadChecklistSuccess(
            List<ReportActivityDTO> baseChecklist) {
        this.checklistObservableList.clear();
        for (ReportActivityDTO reportactivitydto : baseChecklist) {
            this.checklistObservableList.add(new ActivityChecklistItemDTO(reportactivitydto));
        }

        if (this.currentReport != null && SystemConstants.REVIEW_STATUS_APPROVED
                .equalsIgnoreCase(this.currentReport.getReviewStatus())) {
            this.checklistTableView.setEditable(false);
        }
    }

    private void congealEvaluationInterface() {
        this.evaluationComboBox.setDisable(true);
        this.hoursTextField.setEditable(false);
        this.observationsTextArea.setEditable(false);
        this.submitButton.setVisible(false);
        this.submitButton.setManaged(false);
        this.checklistTableView.setEditable(false);

        LOGGER.log(Level.INFO, "Interface switched to read-only mode for " 
                + "already audited and approved report ID: {0}", 
                this.currentReport.getIdReport());
    }

    @FXML
    private void handleEvaluationSelection(ActionEvent event) {
        if (this.evaluationComboBox.isDisable()) {
            return;
        }

        String selectedStatus = this.evaluationComboBox.getSelectionModel()
                .getSelectedItem();
        boolean isApproved = SystemConstants.REVIEW_STATUS_APPROVED
                .equals(selectedStatus);

        this.hoursVBox.setVisible(isApproved);
        this.hoursVBox.setManaged(isApproved);

        if (!isApproved) {
            this.hoursTextField.clear();
            this.checklistTableView.setEditable(false);

            for (ActivityChecklistItemDTO item : this.checklistObservableList) {
                if (!item.isPermanentlyFrozen()) {
                    item.setCompleted(false);
                }
            }
            LOGGER.log(Level.INFO, "Checklist UI components disabled " 
                    + "dynamically due to REJECTED evaluation status.");
        } else {
            this.checklistTableView.setEditable(true);
        }
    }

    @FXML
    private void handleDownloadPdf(ActionEvent event) {
        if (this.currentReport == null 
                || this.currentReport.getFileContent() == null) {
            AlertUtility.showWarningAlert("Archivo Ausente", "El reporte " 
                    + "seleccionado no contiene un archivo válido.");
            return;
        }

        try {
            this.downloadPdfButton.setDisable(true);
            LOGGER.log(Level.INFO, "Writing PDF payload synchronously " 
                    + "into disk storage temp space.");

            byte[] pdfBytes = this.currentReport.getFileContent();
            String matricula = this.currentReport.getStudentEnrollment();

            if (pdfBytes != null && pdfBytes.length > 0) {
                String safePrefix = "SPP_Reporte_" + matricula + "_";
                String safeSuffix = ".pdf";

                Path tempFile = Files.createTempFile(safePrefix, safeSuffix);
                Files.write(tempFile, pdfBytes);

                File fileResult = tempFile.toFile();
                fileResult.deleteOnExit();

                if (Desktop.isDesktopSupported() && fileResult != null) {
                    Desktop.getDesktop().open(fileResult);
                } else {
                    AlertUtility.showWarningAlert("No Soportado", "Su entorno " 
                            + "gráfico no permite abrir archivos externos.");
                }
            } else {
                throw new DatabaseSystemException("El archivo digital " 
                        + "del reporte se encuentra vacío.");
            }
        } catch (IOException | DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "OS Native desktop handler or disk IO " 
                    + "failed processing file stream cache.", exception);
            AlertUtility.showErrorAlert("Error de Servidor", "Ocurrió un " 
                    + "problema técnico al procesar el archivo del reporte.");
        } finally {
            this.downloadPdfButton.setDisable(false);
        }
    }

    @FXML
    private void handleSubmitEvaluation(ActionEvent event) {
        String dictamen = this.evaluationComboBox.getSelectionModel()
                .getSelectedItem();
        String observaciones = this.observationsTextArea.getText();
        String rawHoras = this.hoursTextField.getText();

        if (dictamen == null) {
            AlertUtility.showWarningAlert("Campos Incompletos", "Debe " 
                    + "seleccionar un dictamen para el reporte.");
            return;
        }

        if (observaciones == null || observaciones.trim().isEmpty()) {
            AlertUtility.showWarningAlert("Campos Incompletos", "Por favor, " 
                    + "añada observaciones para el estudiante.");
            return;
        }

        int horasAcreditar = 0;
        if (SystemConstants.REVIEW_STATUS_APPROVED.equals(dictamen)) {
            if (rawHoras == null || rawHoras.trim().isEmpty()) {
                AlertUtility.showWarningAlert("Horas Requeridas", "Debe " 
                        + "especificar las horas a acreditar al alumno.");
                return;
            }
            try {
                horasAcreditar = parseInt(rawHoras.trim());
                if (horasAcreditar < 0 || horasAcreditar > SystemConstants
                        .MAX_CREDITABLE_HOURS) {
                    AlertUtility.showWarningAlert("Rango Inválido", "Las horas " 
                            + "deben estar en el rango de 0 a 420.");
                    return;
                }
            } catch (NumberFormatException exception) {
                AlertUtility.showWarningAlert("Formato Inválido", "Por favor, " 
                        + "introduzca caracteres numéricos en las horas.");
                return;
            }
        }

        this.currentReport.setReviewStatus(dictamen);
        this.currentReport.setObservations(observaciones.trim());
        this.currentReport.setHoursCovered(horasAcreditar);

        try {
            this.submitButton.setDisable(true);
            LOGGER.log(Level.INFO, "Submitting evaluation and checklist " 
                    + "synchronously to the core server repository.");

            this.professorController.evaluateStudentReport(this.currentReport);

            if (SystemConstants.REVIEW_STATUS_APPROVED
                    .equalsIgnoreCase(dictamen)) {
                List<ReportActivityDTO> completedActivities = new ArrayList<>();

                for (ActivityChecklistItemDTO item : this
                        .checklistObservableList) {
                    if (item.isCompleted()) {
                        ReportActivityDTO activityRelation = 
                                new ReportActivityDTO();
                        activityRelation.setReportId(this.currentReport
                                .getIdReport());
                        activityRelation.setActivityId(item.getActivityId());
                        activityRelation.setCompleted(true);

                        completedActivities.add(activityRelation);
                    }
                }

                if (!completedActivities.isEmpty()) {
                    LOGGER.log(Level.INFO, "Sending batch processing of {0} " 
                            + "activities to the server.", 
                            completedActivities.size());
                    this.reportActivityDAO.registerCompletedActivities(
                            completedActivities);
                }
            }

            AlertUtility.showInformationAlert("Dictamen Registrado", "La " 
                    + "evaluación se guardó exitosamente en el servidor.");
            this.closeWindow();

            if (this.parentController != null) {
                this.parentController.loadAssignedReports();
                LOGGER.log(Level.INFO, "Parent table view refreshed " 
                        + "successfully via sequential synchronization.");
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Critical server transaction failure " 
                    + "during report evaluation execution phase.", exception);
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "registrar la evaluación en el servidor central.");
        } finally {
            this.submitButton.setDisable(false);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        this.closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) this.cancelButton.getScene().getWindow();
        stage.close();
    }

    private static class CheckboxCellFactory implements Callback<TableColumn<
            ActivityChecklistItemDTO, Boolean>, TableCell<
            ActivityChecklistItemDTO, Boolean>> {

        @Override
        public TableCell<ActivityChecklistItemDTO, Boolean> 
                call(TableColumn<ActivityChecklistItemDTO, Boolean> column) {
            return new FrozenCheckBoxTableCell();
        }
    }

    private static class FrozenCheckBoxTableCell extends CheckBoxTableCell<
            ActivityChecklistItemDTO, Boolean> {

        @Override
        public void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || this.getTableRow() == null || this.getTableRow()
                    .getItem() == null) {
                this.setDisable(false);
            } else {
                ActivityChecklistItemDTO checklistItem = (ActivityChecklistItemDTO) 
                        this.getTableRow().getItem();
                this.setDisable(checklistItem.isPermanentlyFrozen());
            }
        }
    }
}