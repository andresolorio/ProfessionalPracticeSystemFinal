package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.ProfessorController;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportCustomRow;

/**
 *
 * @author cinth
 * @author andre
 */
public class EvaluateReportsListViewController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(
            EvaluateReportsListViewController.class.getName());

    @FXML
    private ComboBox<String> statusFilterComboBox;
    @FXML
    private TableView<ReportCustomRow> reportsTableView;
    @FXML
    private TableColumn<ReportCustomRow, String> enrollmentTableColumn;
    @FXML
    private TableColumn<ReportCustomRow, String> typeTableColumn;
    @FXML
    private TableColumn<ReportCustomRow, String> deliveryDateTableColumn;
    @FXML
    private TableColumn<ReportCustomRow, String> deliveryStatusTableColumn;
    @FXML
    private TableColumn<ReportCustomRow, String> reviewStatusTableColumn;
    @FXML
    private TableColumn<ReportCustomRow, Integer> hoursTableColumn;
    @FXML
    private Button backButton;
    @FXML
    private Button evaluateButton;

    private final ProfessorController professorController;
    private List<ReportDTO> masterReportsList;
    private ObservableList<ReportCustomRow> tableRowsObservableList;

    public EvaluateReportsListViewController() {
        this.professorController = new ProfessorController();
        this.masterReportsList = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.configureTableViewColumns();
        
        this.statusFilterComboBox.setItems(FXCollections.observableArrayList(
            "Todos", "Pendiente", "Aprobado", "Rechazado"
        ));
        this.statusFilterComboBox.getSelectionModel().select("Todos");
        
        this.statusFilterComboBox.getSelectionModel().selectedItemProperty()
                .addListener(new FilterChangeListener(this));
        this.reportsTableView.getSelectionModel().selectedItemProperty()
                .addListener(new RowSelectionChangeListener(this));

        this.loadAssignedReports();
    }

    private void configureTableViewColumns() {
        this.enrollmentTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("enrollment"));
        this.typeTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("reportType"));
        this.deliveryDateTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("formattedDate"));
        this.deliveryStatusTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("deliveryStatus"));
        this.reviewStatusTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("revisionStatus"));
        this.hoursTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("hoursCovered"));
    }

    public void loadAssignedReports() {
        String professorEmail = UserSession.getInstance().getLoggedUser()
                .getEmail();
        try {
            LOGGER.log(Level.INFO, "Requesting assigned student reports " 
                    + "stream synchronously from server layers.");

            List<ReportDTO> reports = this.professorController
                    .getAssignedStudentsReports(professorEmail);
            
            this.handleLoadReportsSuccess(reports);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication pipeline failure " 
                    + "encountered while loading student reports.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar los reportes de los alumnos desde el servidor.");
        }
    }

    public void handleLoadReportsSuccess(List<ReportDTO> reports) {
        this.masterReportsList = reports;
        this.applyStatusFilter(this.statusFilterComboBox.getSelectionModel()
                .getSelectedItem());
        this.evaluateButton.setText("Evaluar Reporte");
    }

    public void applyStatusFilter(String selectedStatus) {
        if (this.masterReportsList == null) {
            return;
        }

        this.tableRowsObservableList = FXCollections.observableArrayList();
        for (ReportDTO dto : this.masterReportsList) {
            if ("Todos".equals(selectedStatus) || dto.getReviewStatus()
                    .equalsIgnoreCase(selectedStatus)) {
                this.tableRowsObservableList.add(new ReportCustomRow(dto));
            }
        }
        this.reportsTableView.setItems(this.tableRowsObservableList);
    }

    public void handleRowSelectionChange(ReportCustomRow selectedRow) {
        if (selectedRow == null) {
            this.evaluateButton.setText("Evaluar Reporte");
            return;
        }

        if ("Aprobado".equalsIgnoreCase(selectedRow.getRevisionStatus())) {
            this.evaluateButton.setText("Consultar Reporte");
        } else {
            this.evaluateButton.setText("Evaluar Reporte");
        }
    }

    @FXML
    private void handleEvaluate(ActionEvent event) {
        ReportCustomRow selectedRow = this.reportsTableView.getSelectionModel()
                .getSelectedItem();
        
        if (selectedRow == null) {
            AlertUtility.showWarningAlert("Selección Requerida", 
                    "Por favor, seleccione el reporte de un alumno " 
                    + "de la lista para proceder.");
            return;
        }

        ReportDTO selectedReportDTO = null;
        for (ReportDTO report : this.masterReportsList) {
            if (report.getIdReport() == selectedRow.getIdReport()) {
                selectedReportDTO = report;
                break;
            }
        }

        if (selectedReportDTO != null) {
            if ("Aprobado".equalsIgnoreCase(selectedReportDTO.getReviewStatus())) {
                boolean proceed = AlertUtility.showConfirmationAlert(
                        "Reporte ya Evaluado", "Este reporte ya se encuentra " 
                        + "APROBADO. Entrarás en modo de consulta.\n\n" 
                        + "¿Deseas continuar?");
                if (!proceed) {
                    return;
                }
            } else if ("Rechazado".equalsIgnoreCase(selectedReportDTO
                    .getReviewStatus())) {
                boolean proceed = AlertUtility.showConfirmationAlert(
                        "Reporte ya Evaluado", "Este reporte ya cuenta con " 
                        + "un dictamen de RECHAZADO.\n\n¿Deseas volver a " 
                        + "evaluarlo?");
                if (!proceed) {
                    return;
                }
            }

            LOGGER.log(Level.INFO, "Launching report evaluation wizard view " 
                    + "as a synchronous modal window constraint context.");

            NavigationUtility.navigateToModal(this.evaluateButton, 
                    ViewConstants.VIEW_REPORT_EVALUATION_WIZARD, 
                    "Sistema de Prácticas Profesionales - Evaluar Reporte", 
                    selectedReportDTO, this);

            LOGGER.log(Level.INFO, "Modal wizard closed. Triggering automatic " 
                    + "sequential synchronization pipeline layout.");
            this.loadAssignedReports();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        LOGGER.log(Level.INFO, "Explicit back routing window execution. " 
                + "Routing client contexts screen back to ProfessorMenuView.");

        NavigationUtility.navigateTo(this.backButton, 
                ViewConstants.VIEW_PROFESSOR_MENU, 
                SystemConstants.TITLE_PROFESSOR_DASHBOARD);
    }

    private static class FilterChangeListener implements ChangeListener<String> {
        private final EvaluateReportsListViewController controller;

        public FilterChangeListener(EvaluateReportsListViewController controller) {
            this.controller = controller;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, 
                String oldValue, String newValue) {
            this.controller.applyStatusFilter(newValue);
        }
    }

    private static class RowSelectionChangeListener implements 
            ChangeListener<ReportCustomRow> {
        private final EvaluateReportsListViewController controller;

        public RowSelectionChangeListener(
                EvaluateReportsListViewController controller) {
            this.controller = controller;
        }

        @Override
        public void changed(ObservableValue<? extends ReportCustomRow> observable, 
                ReportCustomRow oldRow, ReportCustomRow selectedRow) {
            this.controller.handleRowSelectionChange(selectedRow);
        }
    }
}