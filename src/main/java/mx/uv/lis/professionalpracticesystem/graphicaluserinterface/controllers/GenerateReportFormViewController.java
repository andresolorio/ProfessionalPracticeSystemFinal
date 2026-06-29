package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ActivityDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.DeadlineDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ReportDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ReportWeeklyAdvanceDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityRowDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DeadlineDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.PartialReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportActivityDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportWeeklyAdvanceDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.TimelineRowAdapterDTO;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.generator.MonthlyReportPDFGenerator;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.generator.ReportDataMapper;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;

/**
 *
 * @author cinth
 * @author andre
 */
public class GenerateReportFormViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            GenerateReportFormViewController.class.getName());
    private final ObservableList<TimelineRowAdapterDTO> timelineObservableList;
    private final StudentDAO studentDAO;
    private final DeadlineDAO deadlineDAO;
    private final ActivityDAO activityDAO;
    private final ReportDAO reportDAO;
    private final ReportWeeklyAdvanceDAO advanceDAO;
    
    private StudentDTO authenticatedStudent;
    private List<ActivityDTO> cachedProjectActivities;

    @FXML
    private TableView<TimelineRowAdapterDTO> activitiesTimelineTableView;
    @FXML
    private TableColumn<TimelineRowAdapterDTO, String> activityNameColumn;
    @FXML
    private TableColumn<TimelineRowAdapterDTO, String> timeTypeColumn;
    @FXML
    private TableColumn<TimelineRowAdapterDTO, Boolean> weekOneColumn;
    @FXML
    private TableColumn<TimelineRowAdapterDTO, Boolean> weekTwoColumn;
    @FXML
    private TableColumn<TimelineRowAdapterDTO, Boolean> weekThreeColumn;
    @FXML
    private TableColumn<TimelineRowAdapterDTO, Boolean> weekFourColumn;
    @FXML
    private TableColumn<TimelineRowAdapterDTO, Boolean> weekFiveColumn;
    @FXML
    private TableColumn<TimelineRowAdapterDTO, Boolean> weekSixColumn;
    @FXML
    private TableColumn<TimelineRowAdapterDTO, Boolean> weekSevenColumn;
    @FXML
    private TableColumn<TimelineRowAdapterDTO, Boolean> weekEightColumn;
    @FXML
    private TextArea resultsTextArea;
    @FXML
    private TextArea observationsTextArea;
    @FXML
    private Button generatePdfButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField reportHoursTextField;
    @FXML
    private ComboBox<Integer> reportNumberComboBox;

    public GenerateReportFormViewController() {
        this.timelineObservableList = FXCollections.observableArrayList();
        this.cachedProjectActivities = new ArrayList<>();
        this.studentDAO = new StudentDAO();
        this.deadlineDAO = new DeadlineDAO();
        this.activityDAO = new ActivityDAO();
        this.reportDAO = new ReportDAO();
        this.advanceDAO = new ReportWeeklyAdvanceDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.activitiesTimelineTableView.setEditable(true);
        this.configureTableColumns();
        this.loadAuthenticatedStudentAndActivities();
        
        this.reportNumberComboBox.getSelectionModel().selectedItemProperty()
                .addListener(new ReportNumberChangeListener(this));
    }

    private void configureTableColumns() {
        this.activityNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("activityName"));
        this.timeTypeColumn.setCellValueFactory(
                new PropertyValueFactory<>("timeType"));

        this.configureCheckboxColumn(this.weekOneColumn, "weekOne");
        this.configureCheckboxColumn(this.weekTwoColumn, "weekTwo");
        this.configureCheckboxColumn(this.weekThreeColumn, "weekThree");
        this.configureCheckboxColumn(this.weekFourColumn, "weekFour");
        this.configureCheckboxColumn(this.weekFiveColumn, "weekFive");
        this.configureCheckboxColumn(this.weekSixColumn, "weekSix");
        this.configureCheckboxColumn(this.weekSevenColumn, "weekSeven");
        this.configureCheckboxColumn(this.weekEightColumn, "weekEight");
    }

    private void configureCheckboxColumn(TableColumn<TimelineRowAdapterDTO, 
            Boolean> column, String propertyName) {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(new CheckboxCellFactory());
    }

    private void loadAuthenticatedStudentAndActivities() {
        String loggedEmail = UserSession.getInstance().getLoggedUser()
                .getEmail();
        
        try {
            LOGGER.log(Level.INFO, "Synchronously loading initial form track " 
                    + "contexts framework maps from core server layers.");

            this.authenticatedStudent = this.studentDAO
                    .getStudentByEnrollment(loggedEmail);
            if (this.authenticatedStudent == null) {
                this.authenticatedStudent = this.studentDAO
                        .getStudentByEmail(loggedEmail);
            }

            List<TimelineRowAdapterDTO> gridRows = new ArrayList<>();
            List<Integer> allowedDeliveries = new ArrayList<>();

            if (this.authenticatedStudent != null) {
                List<DeadlineDTO> totalDeadlines = this.deadlineDAO
                        .getDeadlinesByStudentEnrollment(this
                        .authenticatedStudent.getEnrollmentId());

                for (DeadlineDTO deadline : totalDeadlines) {
                    if (deadline.getReportType() != null && deadline
                            .getReportType().toLowerCase().contains(
                            SystemConstants.MONTHLY_REPORT_KEYWORD)) {
                        allowedDeliveries.add(deadline.getReportedNumber());
                    }
                }

                this.cachedProjectActivities = this.activityDAO
                        .getActivitiesByIdProject(this.authenticatedStudent
                        .getIdProject());

                for (ActivityDTO activity : this.cachedProjectActivities) {
                    gridRows.add(new TimelineRowAdapterDTO(
                            activity.getActivityName(), 
                            SystemConstants.TIMELINE_PLAN_LABEL));
                    gridRows.add(new TimelineRowAdapterDTO(
                            activity.getActivityName(), 
                            SystemConstants.TIMELINE_REAL_LABEL));
                }
            }

            this.handleLoadContextSuccess(gridRows, this.authenticatedStudent, 
                    allowedDeliveries);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server architecture context validation " 
                    + "failure loading student initial datasets.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se " 
                    + "pudieron precargar las actividades desde el servidor.");
        }
    }

    public void handleLoadContextSuccess(List<TimelineRowAdapterDTO> gridRows, 
            StudentDTO student, List<Integer> allowedDeliveries) {
        this.authenticatedStudent = student;
        this.timelineObservableList.clear();
        this.timelineObservableList.addAll(gridRows);
        this.activitiesTimelineTableView.setItems(this.timelineObservableList);

        this.reportNumberComboBox.setItems(FXCollections
                .observableArrayList(allowedDeliveries));
        if (!allowedDeliveries.isEmpty()) {
            this.reportNumberComboBox.getSelectionModel().selectFirst();
            this.checkAndCongealIfApproved(this.reportNumberComboBox
                    .getSelectionModel().getSelectedItem());
        }
    }

    public void checkAndCongealIfApproved(int reportNumber) {
        try {
            String enrollment = this.authenticatedStudent.getEnrollmentId()
                    .toUpperCase().trim();
            List<ReportDTO> pastReports = this.reportDAO
                    .getReportsByEnrollment(enrollment);
            
            this.releaseInterfaceControls();
            this.activitiesTimelineTableView.setEditable(true);
            this.activitiesTimelineTableView.setDisable(false);
            
            for (TimelineRowAdapterDTO row : this.timelineObservableList) {
                row.setEditable(true);
                row.setWeekOne(false);
                row.setWeekTwo(false);
                row.setWeekThree(false);
                row.setWeekFour(false);
            }

            if (reportNumber > 1) {
                boolean previousApproved = false;
                for (ReportDTO report : pastReports) {
                    if (report.getReportType() != null && report.getReportType()
                            .toLowerCase().contains("mensual") && report
                            .getReportedHours() == (reportNumber - 1)) {
                        
                        if ("Aprobado".equalsIgnoreCase(report
                                .getReviewStatus())) {
                            previousApproved = true;
                            break;
                        }
                    }
                }
                
                if (!previousApproved) {
                    this.lockInterfaceDueToSequence();
                    AlertUtility.showWarningAlert("Hito Bloqueado", 
                            "No tienes permitido generar el Reporte Mensual " 
                            + reportNumber + " todavía.\n\nMotivo: Tu reporte " 
                            + "anterior debe estar previamente aprobado.");
                    return;
                }
            }

            ReportDTO currentReport = null;
            for (ReportDTO report : pastReports) {
                if (report.getReportType() != null && report.getReportType()
                        .toLowerCase().contains("mensual") && report
                        .getReportedHours() == reportNumber) {
                    currentReport = report;
                    break;
                }
            }

            if (currentReport != null && "Aprobado".equalsIgnoreCase(
                    currentReport.getReviewStatus())) {
                this.activitiesTimelineTableView.setEditable(false);
                this.activitiesTimelineTableView.setDisable(true);
                this.reportHoursTextField.setDisable(true);
                this.resultsTextArea.setDisable(true);
                this.observationsTextArea.setDisable(true);
                this.generatePdfButton.setDisable(true);
                this.reportHoursTextField.setText("Hito resguardado");
                this.resultsTextArea.setText("Este reporte mensual ya fue " 
                        + "validado y aprobado por su docente.");
                this.observationsTextArea.setText("Expediente soldado.");
                
                for (TimelineRowAdapterDTO row : this.timelineObservableList) {
                    row.setEditable(false);
                }
                this.loadSavedWeeklyAdvances(currentReport.getIdReport(), true);
            } else {
                if (currentReport != null) {
                    this.reportHoursTextField.setText(String.valueOf(
                            currentReport.getHoursCovered()));
                    this.resultsTextArea.setText(currentReport
                            .getObservations());
                    this.loadSavedWeeklyAdvances(currentReport.getIdReport(), 
                            false);
                }

                for (ReportDTO pastReport : pastReports) {
                    if ("Aprobado".equalsIgnoreCase(pastReport.getReviewStatus()) 
                            && pastReport.getReportedHours() < reportNumber) {
                        this.loadSavedWeeklyAdvances(pastReport.getIdReport(), 
                                true);
                    }
                }
                
                this.activitiesTimelineTableView.refresh();
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server pipeline technical exception " 
                    + "evaluating interface barrier congeal rule.", exception);
        }
    }

    private void loadSavedWeeklyAdvances(int idReport, boolean freezeRows) 
            throws DatabaseSystemException {
        List<ReportWeeklyAdvanceDTO> advances = this.advanceDAO
                .getWeeklyAdvancesByReport(idReport);

        for (ReportWeeklyAdvanceDTO advance : advances) {
            for (TimelineRowAdapterDTO row : this.timelineObservableList) {
                if (row.getActivityName().equalsIgnoreCase(this
                        .getActivityNameById(advance.getIdActivity())) && row
                        .getTimeType().equalsIgnoreCase(advance
                        .getRegistrationType())) {

                    if (advance.isWeekOne()) {
                        row.setWeekOne(true);
                    }
                    if (advance.isWeekTwo()) {
                        row.setWeekTwo(true);
                    }
                    if (advance.isWeekThree()) {
                        row.setWeekThree(true);
                    }
                    if (advance.isWeekFour()) {
                        row.setWeekFour(true);
                    }

                    if (freezeRows && (advance.isWeekOne() || advance
                            .isWeekTwo() || advance.isWeekThree() || advance
                            .isWeekFour())) {
                        row.setEditable(false);
                    }
                }
            }
        }
    }

    private String getActivityNameById(int idActivity) {
        if (this.cachedProjectActivities != null) {
            for (ActivityDTO act : this.cachedProjectActivities) {
                if (act.getIdActivity() == idActivity) {
                    return act.getActivityName();
                }
            }
        }
        return "Actividad";
    }

    private void lockInterfaceDueToSequence() {
        this.activitiesTimelineTableView.setEditable(false);
        this.activitiesTimelineTableView.setDisable(true);
        this.reportHoursTextField.setDisable(true);
        this.resultsTextArea.setDisable(true);
        this.observationsTextArea.setDisable(true);
        this.generatePdfButton.setDisable(true);
        this.reportHoursTextField.setText("Hito bloqueado");
        this.resultsTextArea.setText("Por favor, acredite y espere la " 
                + "aprobación de la entrega mensual inmediata anterior.");
        this.observationsTextArea.setText("Control secuencial activo.");
    }

    private void releaseInterfaceControls() {
        this.activitiesTimelineTableView.setEditable(true);
        this.activitiesTimelineTableView.setDisable(false);
        this.reportHoursTextField.setDisable(false);
        this.resultsTextArea.setDisable(false);
        this.observationsTextArea.setDisable(false);
        this.generatePdfButton.setDisable(false);
    }

    @FXML
    private void handleGeneratePdf(ActionEvent event) throws SQLException {
        String resultsText = this.resultsTextArea.getText();
        String obsText = this.observationsTextArea.getText();
        String hoursText = this.reportHoursTextField.getText();
        Integer selectedReportNumber = this.reportNumberComboBox
                .getSelectionModel().getSelectedItem();

        if (selectedReportNumber == null) {
            AlertUtility.showWarningAlert("Selección Requerida", "Debe " 
                    + "seleccionar un número de reporte válido.");
            return;
        }

        if (resultsText == null || resultsText.trim().isEmpty()) {
            AlertUtility.showWarningAlert("Campos Obligatorios", "La sección " 
                    + "'Resultados obtenidos' es obligatoria.");
            return;
        }

        if (hoursText == null || hoursText.trim().isEmpty()) {
            AlertUtility.showWarningAlert("Campos Obligatorios", "Por favor, " 
                    + "especifique la cantidad de horas cubiertas.");
            return;
        }

        int parsedHours = 0;
        try {
            parsedHours = Integer.parseInt(hoursText.trim());
            if (parsedHours <= 0 || parsedHours > SystemConstants
                    .MAX_CREDITABLE_HOURS) {
                AlertUtility.showWarningAlert("Formato Inválido", "Por favor " 
                        + "ingrese una cantidad coherente de horas cubiertas.");
                return;
            }
        } catch (NumberFormatException exception) {
            AlertUtility.showWarningAlert("Formato Inválido", "El campo de " 
                    + "horas debe ser un valor numérico entero.");
            return;
        }

        try {
            this.generatePdfButton.setDisable(true);
            LOGGER.log(Level.INFO, "Executing synchronous PDF processing " 
                    + "and reports batch persistent registration pipeline.");

            List<ActivityRowDTO> compiledActivities = this
                    .extractActivitiesFromGrid();
            
            String enrollment = this.authenticatedStudent.getEnrollmentId()
                    .toUpperCase().trim();
            String userHome = System.getProperty("user.home");
            String destDir = userHome + java.io.File.separator + "Downloads" 
                    + java.io.File.separator;
            String fileName = "Reporte_Mensual_" + selectedReportNumber 
                    + ".pdf";
            java.io.File generatedFile = new java.io.File(destDir + fileName);

            ReportDTO mockReport = new ReportDTO();
            mockReport.setStudentEnrollment(enrollment);
            mockReport.setReportedHours(selectedReportNumber);
            mockReport.setDeliveryDate(new java.sql.Date(
                    System.currentTimeMillis()));
            mockReport.setHoursCovered(parsedHours);

            List<ReportActivityDTO> emptyActivities = new ArrayList<>();
            ReportDataMapper mapper = new ReportDataMapper();
            PartialReportDTO partialData = mapper.compilePartialReportData(
                    mockReport, emptyActivities, resultsText.trim(), 
                    obsText.trim());
            partialData.setActivitiesList(compiledActivities);

            MonthlyReportPDFGenerator generator = new MonthlyReportPDFGenerator();
            generator.generateReport(partialData, destDir);

            byte[] fileBytes = null;
            if (generatedFile.exists()) {
                fileBytes = java.nio.file.Files.readAllBytes(
                        generatedFile.toPath());
            }

            List<ReportDTO> pastReports = this.reportDAO
                    .getReportsByEnrollment(enrollment);

            int idReportTarget = SystemConstants.RESET;
            for (ReportDTO report : pastReports) {
                if (report.getReportType() != null && report.getReportType()
                        .toLowerCase().contains("mensual") && report
                        .getReportedHours() == selectedReportNumber) {
                    idReportTarget = report.getIdReport();
                    break;
                }
            }

            if (idReportTarget == SystemConstants.RESET) {
                ReportDTO newReport = new ReportDTO();
                newReport.setStudentEnrollment(enrollment);
                newReport.setReportType("Mensual");
                newReport.setReportedHours(selectedReportNumber);
                newReport.setDeliveryDate(new java.sql.Date(
                        System.currentTimeMillis()));
                newReport.setHoursCovered(parsedHours);
                newReport.setDeliveryStatus("Vigente");
                newReport.setReviewStatus("Pendiente");
                newReport.setObservations(resultsText.trim());
                newReport.setFileContent(fileBytes);

                this.reportDAO.saveReport(newReport);

                pastReports = this.reportDAO.getReportsByEnrollment(enrollment);
                for (ReportDTO report : pastReports) {
                    if (report.getReportedHours() == selectedReportNumber) {
                        idReportTarget = report.getIdReport();
                        break;
                    }
                }
            }

            if (idReportTarget != SystemConstants.RESET) {
                List<ReportWeeklyAdvanceDTO> advancesBatch = new ArrayList<>();

                for (ActivityRowDTO row : compiledActivities) {
                    int idActivityTarget = SystemConstants.RESET;
                    if (this.cachedProjectActivities != null) {
                        for (ActivityDTO act : this.cachedProjectActivities) {
                            if (act.getActivityName().equalsIgnoreCase(
                                    row.getDescription())) {
                                idActivityTarget = act.getIdActivity();
                                break;
                            }
                        }
                    }

                    if (idActivityTarget != SystemConstants.RESET) {
                        ReportWeeklyAdvanceDTO planDTO = 
                                new ReportWeeklyAdvanceDTO();
                        planDTO.setIdReport(idReportTarget);
                        planDTO.setIdActivity(idActivityTarget);
                        planDTO.setRegistrationType("Plan");
                        planDTO.setWeekOne(row.isPlanWeekOne());
                        planDTO.setWeekTwo(row.isPlanWeekTwo());
                        planDTO.setWeekThree(row.isPlanWeekThree());
                        planDTO.setWeekFour(row.isPlanWeekFour());
                        advancesBatch.add(planDTO);

                        ReportWeeklyAdvanceDTO realDTO = 
                                new ReportWeeklyAdvanceDTO();
                        realDTO.setIdReport(idReportTarget);
                        realDTO.setIdActivity(idActivityTarget);
                        realDTO.setRegistrationType("Real");
                        realDTO.setWeekOne(row.isRealWeekOne());
                        realDTO.setWeekTwo(row.isRealWeekTwo());
                        realDTO.setWeekThree(row.isRealWeekThree());
                        realDTO.setWeekFour(row.isRealWeekFour());
                        advancesBatch.add(realDTO);
                    }
                }

                try (Connection connection = new DatabaseConnection().getConnection()) {
                    this.advanceDAO.saveWeeklyAdvances(advancesBatch, 
                            connection);
                }
            }

            AlertUtility.showInformationAlert("PDF Generado", "Tu reporte " 
                    + "mensual oficial ha sido maquetado con éxito.");
            this.clearFormFields();
            this.loadExistingDeadlines();

        } catch (IOException | DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server processing transaction pipeline " 
                    + "engineering failure deploying final report asset.", 
                    exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "Ocurrió un error " 
                    + "al compilar la estructura interna de la entrega.");
        } finally {
            this.generatePdfButton.setDisable(false);
        }
    }

    private void clearFormFields() {
        this.resultsTextArea.clear();
        this.observationsTextArea.clear();
        this.reportHoursTextField.clear();
    }

    private void loadExistingDeadlines() {
        if (this.reportNumberComboBox.getSelectionModel().getSelectedItem() 
                != null) {
            this.checkAndCongealIfApproved(this.reportNumberComboBox
                    .getSelectionModel().getSelectedItem());
        }
    }

    private List<ActivityRowDTO> extractActivitiesFromGrid() {
        List<ActivityRowDTO> list = new ArrayList<>();
        for (int i = 0; i < this.timelineObservableList.size(); i += 2) {
            TimelineRowAdapterDTO planRow = this.timelineObservableList.get(i);
            TimelineRowAdapterDTO realRow = this.timelineObservableList.get(i + 1);

            ActivityRowDTO dto = new ActivityRowDTO();
            dto.setDescription(planRow.getActivityName());

            if (planRow.isEditable()) {
                dto.setPlanWeekOne(planRow.isWeekOne());
                dto.setPlanWeekTwo(planRow.isWeekTwo());
                dto.setPlanWeekThree(planRow.isWeekThree());
                dto.setPlanWeekFour(planRow.isWeekFour());
            } else {
                dto.setPlanWeekOne(false);
                dto.setPlanWeekTwo(false);
                dto.setPlanWeekThree(false);
                dto.setPlanWeekFour(false);
            }

            if (realRow.isEditable()) {
                dto.setRealWeekOne(realRow.isWeekOne());
                dto.setRealWeekTwo(realRow.isWeekTwo());
                dto.setRealWeekThree(realRow.isWeekThree());
                dto.setRealWeekFour(realRow.isWeekFour());
            } else {
                dto.setRealWeekOne(false);
                dto.setRealWeekTwo(false);
                dto.setRealWeekThree(false);
                dto.setRealWeekFour(false);
            }

            list.add(dto);
        }
        return list;
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting current operational workspace form. " 
                + "Routing client stage context back to available choices view.");

        NavigationUtility.navigateTo(this.backButton, 
                ViewConstants.VIEW_ADD_REPORT, 
                SystemConstants.TITLE_REPORTS_AVAILABLE);
    }

    private static class CheckboxCellFactory implements Callback<TableColumn<
            TimelineRowAdapterDTO, Boolean>, TableCell<
            TimelineRowAdapterDTO, Boolean>> {
        @Override
        public TableCell<TimelineRowAdapterDTO, Boolean> call(TableColumn<
                TimelineRowAdapterDTO, Boolean> column) {
            return new FrozenCheckBoxTableCell();
        }
    }

    private static class FrozenCheckBoxTableCell extends CheckBoxTableCell<
            TimelineRowAdapterDTO, Boolean> {
        @Override
        public void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || getTableRow() == null || getTableRow().getItem() 
                    == null) {
                setDisable(false);
            } else {
                TimelineRowAdapterDTO rowItem = getTableRow().getItem();
                setDisable(!rowItem.isEditable());
            }
        }
    }

    private static class ReportNumberChangeListener 
            implements ChangeListener<Integer> {
        private final GenerateReportFormViewController controllerReference;

        public ReportNumberChangeListener(
                GenerateReportFormViewController controller) {
            this.controllerReference = controller;
        }

        @Override
        public void changed(ObservableValue<? extends Integer> observable, 
                Integer oldValue, Integer newValue) {
            if (newValue != null) {
                this.controllerReference.checkAndCongealIfApproved(newValue);
            }
        }
    }
}