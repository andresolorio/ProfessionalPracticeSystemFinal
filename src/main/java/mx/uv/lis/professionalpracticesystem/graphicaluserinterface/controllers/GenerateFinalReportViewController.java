package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ActivityDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.FinalReportDeliverableDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ReportDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityRowDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.FinalReportDeliverableDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.PartialReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.generator.FinalReportPDFGenerator;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;

/**
 *
 * @author cinth
 * @author andre
 */
public class GenerateFinalReportViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            GenerateFinalReportViewController.class.getName());

    private final ObservableList<ActivityRowDTO> activitiesObservableList;
    private final ObservableList<ActivityRowDTO> deliverablesObservableList;

    private StudentDTO authenticatedStudent;
    private List<ActivityDTO> cachedProjectActivities;

    @FXML
    private TableView<ActivityRowDTO> finalActivitiesTableView;
    @FXML
    private TableColumn<ActivityRowDTO, String> activityProgrammedColumn;
    @FXML
    private TableColumn<ActivityRowDTO, String> activityAdvanceColumn;
    @FXML
    private TableColumn<ActivityRowDTO, String> activityObservationsColumn;
    @FXML
    private TableView<ActivityRowDTO> finalDeliverablesTableView;
    @FXML
    private TableColumn<ActivityRowDTO, String> deliverableResultColumn;
    @FXML
    private TableColumn<ActivityRowDTO, String> deliverableAdvanceColumn;
    @FXML
    private TableColumn<ActivityRowDTO, String> deliverableObservationsColumn;
    @FXML
    private TextArea finalGeneralObservationsTextArea;
    @FXML
    private Button generateFinalPdfButton;

    public GenerateFinalReportViewController() {
        this.activitiesObservableList = FXCollections.observableArrayList();
        this.deliverablesObservableList = FXCollections.observableArrayList();
        this.cachedProjectActivities = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.configureTableProperties();
        this.loadFinalFormContext();
    }

    private void configureTableProperties() {
        this.finalActivitiesTableView.setEditable(true);
        this.activityProgrammedColumn.setCellValueFactory(
                new PropertyValueFactory<>("description"));
        this.activityAdvanceColumn.setCellValueFactory(
                new PropertyValueFactory<>("advance"));
        this.activityAdvanceColumn.setCellFactory(
                new CellCustomFactory());
        this.activityAdvanceColumn.setOnEditCommit(
                new ActivityAdvanceEditCommitHandler());

        this.activityObservationsColumn.setCellValueFactory(
                new PropertyValueFactory<>("observations"));
        this.activityObservationsColumn.setCellFactory(
                new CellCustomFactory());
        this.activityObservationsColumn.setOnEditCommit(
                new ActivityObservationsEditCommitHandler());
        this.finalDeliverablesTableView.setEditable(true);
        this.deliverableResultColumn.setCellValueFactory(
                new PropertyValueFactory<>("description"));
        this.deliverableResultColumn.setCellFactory(
                new CellCustomFactory());
        this.deliverableResultColumn.setOnEditCommit(
                new DeliverableResultEditCommitHandler());
        this.deliverableAdvanceColumn.setCellValueFactory(
                new PropertyValueFactory<>("advance"));
        this.deliverableAdvanceColumn.setCellFactory(
                new CellCustomFactory());
        this.deliverableAdvanceColumn.setOnEditCommit(
                new DeliverableAdvanceEditCommitHandler());
        this.deliverableObservationsColumn.setCellValueFactory(
                new PropertyValueFactory<>("observations"));
        this.deliverableObservationsColumn.setCellFactory(
                new CellCustomFactory());
        this.deliverableObservationsColumn.setOnEditCommit(
                new DeliverableObservationsEditCommitHandler());
    }

    private void loadFinalFormContext() {
        String loggedEmail = UserSession.getInstance().getLoggedUser()
                .getEmail();

        try {
            LOGGER.log(Level.INFO, "Synchronously loading initial " 
                    + "academic form tracking contexts from database metadata.");

            StudentDAO studentDAO = new StudentDAO();
            this.authenticatedStudent = studentDAO
                    .getStudentByEmail(loggedEmail);
            
            List<ActivityRowDTO> rows = new ArrayList<>();
            if (this.authenticatedStudent != null) {
                ActivityDAO activityDAO = new ActivityDAO();
                this.cachedProjectActivities = activityDAO
                        .getActivitiesByIdProject(this.authenticatedStudent
                        .getIdProject());
                
                for (ActivityDTO act : this.cachedProjectActivities) {
                    ActivityRowDTO row = new ActivityRowDTO();
                    row.setDescription(act.getActivityName());
                    row.setAdvance("");
                    row.setObservations("");
                    rows.add(row);
                }
            }

            this.activitiesObservableList.clear();
            this.activitiesObservableList.addAll(rows);
            this.finalActivitiesTableView.setItems(
                    this.activitiesObservableList);

            ActivityRowDTO emptyRow = new ActivityRowDTO();
            emptyRow.setDescription("");
            emptyRow.setAdvance("");
            emptyRow.setObservations("");
            
            this.deliverablesObservableList.clear();
            this.deliverablesObservableList.add(emptyRow);
            this.finalDeliverablesTableView.setItems(
                    this.deliverablesObservableList);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server core lookup architecture " 
                    + "failure within structural data bindings.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "sincronizar los datos iniciales desde el servidor.");
        }
    }

    @FXML
    private void handleAddNewDeliverableRow(ActionEvent event) {
        ActivityRowDTO newRow = new ActivityRowDTO();
        newRow.setDescription("");
        newRow.setAdvance("");
        newRow.setObservations("");
        this.deliverablesObservableList.add(newRow);
        this.finalDeliverablesTableView.scrollTo(newRow);
    }

    @FXML
    private void handleGenerateFinalReportPdf(ActionEvent event) throws SQLException, Exception {
        String generalObs = this.finalGeneralObservationsTextArea.getText();

        if (generalObs == null || generalObs.trim().isEmpty()) {
            AlertUtility.showWarningAlert("Campos Obligatorios", "La sección " 
                    + "de Observaciones Generales es obligatoria.");
            return;
        }

        try {
            this.generateFinalPdfButton.setDisable(true);
            LOGGER.log(Level.INFO, "Initiating synchronous write sequence " 
                    + "for Final Report PDF data layout compilation.");

            String enrollment = this.authenticatedStudent.getEnrollmentId()
                    .toUpperCase().trim();
            String userHome = System.getProperty("user.home");
            String destDir = userHome + File.separator + "Downloads" 
                    + File.separator;

            ReportDAO reportDAO = new ReportDAO();
            List<ReportDTO> pastReports = reportDAO
                    .getReportsByEnrollment(enrollment);

            ReportDTO existingFinalRecord = null;
            for (ReportDTO report : pastReports) {
                if ("Final".equalsIgnoreCase(report.getReportType()) 
                        && report.getReportedHours() == 1) {
                    existingFinalRecord = report;
                    break;
                }
            }

            if (existingFinalRecord != null && "Aprobado"
                    .equalsIgnoreCase(existingFinalRecord.getReviewStatus())) {
                throw new IllegalStateException("Tu Informe Final ya ha sido " 
                        + "revisado y aprobado de forma definitiva.");
            }

            PartialReportDTO finalData = new PartialReportDTO();
            
            String academicProfessorName = "Coordinador de EE";
            if (this.authenticatedStudent.getProfessorName() != null) {
                academicProfessorName = this.authenticatedStudent
                        .getProfessorName();
            }
            finalData.setProfessorName(academicProfessorName);
            
            finalData.setNrc(String.valueOf(this.authenticatedStudent.getNrc()));
            
            String operationalPeriod = "FEB - JUL 2026";
            if (this.authenticatedStudent.getPeriod() != null) {
                operationalPeriod = this.authenticatedStudent.getPeriod();
            }
            finalData.setSchoolPeriod(operationalPeriod);
            
            finalData.setStudentNames(this.authenticatedStudent.getFirstName() 
                    + " " + this.authenticatedStudent.getPaternalLastName() 
                    + " " + this.authenticatedStudent.getMaternalLastName());
            finalData.setLinkedOrganizationName("Organización Vinculada");
            finalData.setProjectName("Sistema de Prácticas Profesionales");
            finalData.setGeneralObjectives("Desplegar la persistencia.");
            finalData.setMethodology("Cascada.");
            finalData.setReportDate(new SimpleDateFormat("dd/MM/yyyy")
                    .format(new java.util.Date()));
            finalData.setObservations(generalObs.trim());

            finalData.setActivitiesList(new ArrayList<>(
                    this.activitiesObservableList));
            finalData.setDeliverablesList(new ArrayList<>(
                    this.deliverablesObservableList));

            FinalReportPDFGenerator generator = new FinalReportPDFGenerator();
            generator.generateFinalReport(finalData, destDir);

            File file = new File(destDir + "Reporte_Final_Practicas.pdf");
            byte[] fileBytes = null;
            if (file.exists()) {
                fileBytes = Files.readAllBytes(file.toPath());
            }

            ReportDTO finalRecord = new ReportDTO();
            finalRecord.setStudentEnrollment(enrollment);
            finalRecord.setReportType("Final");
            finalRecord.setReportedHours(1);
            finalRecord.setDeliveryDate(new Date(System.currentTimeMillis()));
            finalRecord.setHoursCovered(420);
            finalRecord.setDeliveryStatus("Vigente");
            finalRecord.setReviewStatus("Pendiente");
            finalRecord.setObservations(generalObs.trim());
            finalRecord.setFileContent(fileBytes);

            if (existingFinalRecord == null) {
                reportDAO.saveReport(finalRecord);
            } else {
                reportDAO.overwriteReport(finalRecord);
            }

            List<FinalReportDeliverableDTO> batch = new ArrayList<>();
            for (ActivityRowDTO row : this.deliverablesObservableList) {
                if (row.getDescription() != null && !row.getDescription()
                        .trim().isEmpty()) {
                    FinalReportDeliverableDTO deliverableDTO = 
                            new FinalReportDeliverableDTO();
                    deliverableDTO.setEnrollmentId(enrollment);
                    deliverableDTO.setDeliverableResult(row.getDescription()
                            .trim());
                    
                    int percentageValue = 0;
                    try {
                        percentageValue = Integer.parseInt(row.getAdvance()
                                .replaceAll("[^0-9]", ""));
                    } catch (NumberFormatException exception) {
                        LOGGER.log(Level.WARNING, "Failed to parse advance "
                                + "percentage string token context value.", 
                                exception);
                        percentageValue = 0;
                    }
                    deliverableDTO.setAdvancePercentage(percentageValue);
                    deliverableDTO.setObservations(row.getObservations() 
                            != null ? row.getObservations().trim() : "");
                    batch.add(deliverableDTO);
                }
            }

            if (!batch.isEmpty()) {
                FinalReportDeliverableDAO entregableDAO = 
                        new FinalReportDeliverableDAO();
                try (Connection connection = new DatabaseConnection().getConnection()) {
                    entregableDAO.saveDeliverables(batch, connection);
                }
            }

            AlertUtility.showInformationAlert("Expediente Concluido", 
                    "Tu Informe Final ha sido guardado con éxito.");
            
        } catch (IllegalStateException exception) {
            LOGGER.log(Level.WARNING, "Business rule restriction encountered "
                    + "during final report submission.", exception);
            AlertUtility.showWarningAlert("Acceso Restringido", 
                    exception.getMessage());
        } catch (IOException | DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server transaction pipeline write error " 
                    + "processing closing report files.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "Ocurrió un " 
                    + "error al registrar la entrega en el servidor central.");
        } finally {
            this.generateFinalPdfButton.setDisable(false);
        }
    }

    @FXML
    private void handleFinalBack(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting final milestone compilation view. " 
                + "Routing layout frame back to StudentMenuView context.");

        NavigationUtility.navigateTo(this.generateFinalPdfButton, 
                ViewConstants.VIEW_STUDENT_MENU, 
                SystemConstants.TITLE_MENU_STUDENT);
    }

    private static class CellCustomFactory implements Callback<TableColumn<
            ActivityRowDTO, String>, TableCell<ActivityRowDTO, String>> {
        @Override
        public TableCell<ActivityRowDTO, String> call(TableColumn<
                ActivityRowDTO, String> param) {
            return new CommitOnFocusLossTableCell();
        }
    }

    private static class ActivityAdvanceEditCommitHandler implements 
            EventHandler<TableColumn.CellEditEvent<ActivityRowDTO, String>> {
        @Override
        public void handle(TableColumn.CellEditEvent<ActivityRowDTO, 
                String> event) {
            String input = event.getNewValue().replaceAll("[^0-9]", "").trim();
            if (!input.isEmpty()) {
                int percentage = Integer.parseInt(input);
                if (percentage >= 0 && percentage <= 100) {
                    event.getRowValue().setAdvance(percentage + "%");
                    return;
                }
            }
            AlertUtility.showWarningAlert("Formato Inválido", "Por favor " 
                    + "ingrese un porcentaje numérico entre 0 y 100.");
        }
    }

    private static class ActivityObservationsEditCommitHandler implements 
            EventHandler<TableColumn.CellEditEvent<ActivityRowDTO, String>> {
        @Override
        public void handle(TableColumn.CellEditEvent<ActivityRowDTO, 
                String> event) {
            if (event.getNewValue() != null) {
                event.getRowValue().setObservations(event.getNewValue().trim());
            }
        }
    }

    private static class DeliverableResultEditCommitHandler implements 
            EventHandler<TableColumn.CellEditEvent<ActivityRowDTO, String>> {
        @Override
        public void handle(TableColumn.CellEditEvent<ActivityRowDTO, 
                String> event) {
            if (event.getNewValue() != null) {
                event.getRowValue().setDescription(event.getNewValue().trim());
            }
        }
    }

    private static class DeliverableAdvanceEditCommitHandler implements 
            EventHandler<TableColumn.CellEditEvent<ActivityRowDTO, String>> {
        @Override
        public void handle(TableColumn.CellEditEvent<ActivityRowDTO, 
                String> event) {
            String input = event.getNewValue().replaceAll("[^0-9]", "").trim();
            if (!input.isEmpty()) {
                int percentage = Integer.parseInt(input);
                if (percentage >= 0 && percentage <= 100) {
                    event.getRowValue().setAdvance(percentage + "%");
                    return;
                }
            }
            AlertUtility.showWarningAlert("Formato Inválido", "Por favor " 
                    + "ingrese un porcentaje numérico entre 0 y 100.");
        }
    }

    private static class DeliverableObservationsEditCommitHandler implements 
            EventHandler<TableColumn.CellEditEvent<ActivityRowDTO, String>> {
        @Override
        public void handle(TableColumn.CellEditEvent<ActivityRowDTO, 
                String> event) {
            if (event.getNewValue() != null) {
                event.getRowValue().setObservations(event.getNewValue().trim());
            }
        }
    }

    private static class CommitOnFocusLossTableCell extends TableCell<
            ActivityRowDTO, String> {

        private TextField textField;

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
                textField.requestFocus();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(item);
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(item);
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getItem() == null ? "" : getItem());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnAction(new TextFieldActionHandler(this, textField));
            textField.focusedProperty().addListener(
                    new TextFieldFocusChangeListener(this, textField));
        }
    }

    private static class TextFieldActionHandler implements 
            EventHandler<ActionEvent> {
        private final CommitOnFocusLossTableCell cell;
        private final TextField field;

        public TextFieldActionHandler(CommitOnFocusLossTableCell cell, 
                TextField field) {
            this.cell = cell;
            this.field = field;
        }

        @Override
        public void handle(ActionEvent event) {
            if (this.cell.isEditing()) {
                this.cell.commitEdit(this.field.getText());
            }
        }
    }

    private static class TextFieldFocusChangeListener implements 
            ChangeListener<Boolean> {
        private final CommitOnFocusLossTableCell cell;
        private final TextField field;

        public TextFieldFocusChangeListener(CommitOnFocusLossTableCell cell, 
                TextField field) {
            this.cell = cell;
            this.field = field;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, 
                Boolean oldValue, Boolean newValue) {
            if (!newValue && this.cell.isEditing()) {
                this.cell.commitEdit(this.field.getText());
            }
        }
    }
}