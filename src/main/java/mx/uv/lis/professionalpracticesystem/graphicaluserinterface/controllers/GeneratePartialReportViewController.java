package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ActivityDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ReportDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ReportWeeklyAdvanceDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityRowDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.PartialReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportActivityDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportWeeklyAdvanceDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.PartialTimelineRowAdapterDTO;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.generator.PartialReportPDFGenerator;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.generator.ReportDataMapper;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;


/**
 * 
 * @author cinth
 * @author andre
 */
public class GeneratePartialReportViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            GeneratePartialReportViewController.class.getName());
    private final ObservableList<PartialTimelineRowAdapterDTO> timelineObservableList;
    private final StudentDAO studentDAO;
    private final ReportDAO reportDAO;
    private final ActivityDAO activityDAO;
    private final ReportWeeklyAdvanceDAO advanceDAO;

    private StudentDTO authenticatedStudent;
    private List<ActivityDTO> cachedProjectActivities;
    private int cumulativeHoursCalculated = 0;

    @FXML
    private TableView<PartialTimelineRowAdapterDTO> partialActivitiesTimelineTableView;
    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, String> partialActivityNameColumn;
    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, String> partialTimeTypeColumn;

    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, Boolean> partialWeekOneColumn;
    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, Boolean> partialWeekTwoColumn;
    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, Boolean> partialWeekThreeColumn;
    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, Boolean> partialWeekFourColumn;
    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, Boolean> partialWeekFiveColumn;
    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, Boolean> partialWeekSixColumn;
    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, Boolean> partialWeekSevenColumn;
    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, Boolean> partialWeekEightColumn;
    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, Boolean> partialWeekNineColumn;
    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, Boolean> partialWeekTenColumn;
    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, Boolean> partialWeekElevenColumn;
    @FXML
    private TableColumn<PartialTimelineRowAdapterDTO, Boolean> partialWeekTwelveColumn;

    @FXML
    private TextArea partialResultsTextArea;
    @FXML
    private TextArea partialObservationsTextArea;
    @FXML
    private TextField partialReportHoursTextField;
    @FXML
    private Button generatePartialPdfButton;
    @FXML
    private Button partialBackButton;

    public GeneratePartialReportViewController() {
        this.timelineObservableList = FXCollections.observableArrayList();
        this.cachedProjectActivities = new ArrayList<>();
        this.studentDAO = new StudentDAO();
        this.reportDAO = new ReportDAO();
        this.activityDAO = new ActivityDAO();
        this.advanceDAO = new ReportWeeklyAdvanceDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.partialActivitiesTimelineTableView.setEditable(false);
        this.configureTableColumns();
        this.loadPartialFormContext();
    }

    private void configureTableColumns() {
        this.partialActivityNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("activityName"));
        this.partialTimeTypeColumn.setCellValueFactory(
                new PropertyValueFactory<>("timeType"));

        this.configureStaticCheckboxColumn(this.partialWeekOneColumn, "weekOne");
        this.configureStaticCheckboxColumn(this.partialWeekTwoColumn, "weekTwo");
        this.configureStaticCheckboxColumn(this.partialWeekThreeColumn, "weekThree");
        this.configureStaticCheckboxColumn(this.partialWeekFourColumn, "weekFour");
        this.configureStaticCheckboxColumn(this.partialWeekFiveColumn, "weekFive");
        this.configureStaticCheckboxColumn(this.partialWeekSixColumn, "weekSix");
        this.configureStaticCheckboxColumn(this.partialWeekSevenColumn, "weekSeven");
        this.configureStaticCheckboxColumn(this.partialWeekEightColumn, "weekEight");
        this.configureStaticCheckboxColumn(this.partialWeekNineColumn, "weekNine");
        this.configureStaticCheckboxColumn(this.partialWeekTenColumn, "weekTen");
        this.configureStaticCheckboxColumn(this.partialWeekElevenColumn, "weekEleven");
        this.configureStaticCheckboxColumn(this.partialWeekTwelveColumn, "weekTwelve");
    }

    private void configureStaticCheckboxColumn(
            TableColumn<PartialTimelineRowAdapterDTO, Boolean> column, 
            String propertyName) {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(new StaticCheckboxCellFactory());
    }

    private void loadPartialFormContext() {
        String loggedEmail = UserSession.getInstance().getLoggedUser()
                .getEmail();
        
        try {
            LOGGER.log(Level.INFO, "Synchronously querying student profiles " 
                    + "and historical tracking states from core repositories.");

            this.authenticatedStudent = this.studentDAO
                    .getStudentByEnrollment(loggedEmail);
            if (this.authenticatedStudent == null) {
                this.authenticatedStudent = this.studentDAO
                        .getStudentByEmail(loggedEmail);
            }

            List<PartialTimelineRowAdapterDTO> rows = new ArrayList<>();
            List<ReportDTO> pastReports = new ArrayList<>();

            if (this.authenticatedStudent != null) {
                pastReports = this.reportDAO.getReportsByEnrollment(
                        this.authenticatedStudent.getEnrollmentId());

                for (ReportDTO report : pastReports) {
                    if ("Aprobado".equalsIgnoreCase(report.getReviewStatus()) 
                            && "Mensual".equalsIgnoreCase(report.getReportType())) {
                        this.cumulativeHoursCalculated += report.getHoursCovered();
                    }
                }

                this.cachedProjectActivities = this.activityDAO
                        .getActivitiesByIdProject(this.authenticatedStudent
                        .getIdProject());

                for (ActivityDTO act : this.cachedProjectActivities) {
                    rows.add(new PartialTimelineRowAdapterDTO(
                            act.getActivityName(), "Plan"));
                    rows.add(new PartialTimelineRowAdapterDTO(
                            act.getActivityName(), "Real"));
                }
            }

            this.timelineObservableList.clear();
            this.timelineObservableList.addAll(rows);
            this.partialActivitiesTimelineTableView.setItems(
                    this.timelineObservableList);

            this.partialReportHoursTextField.setText(
                    this.cumulativeHoursCalculated + " Horas Acreditadas");
            this.injectCumulativeAdvances(pastReports);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server infrastructure network layer " 
                    + "failure retrieving partial form records.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudieron " 
                    + "sincronizar los avances desde el servidor central.");
        }
    }

    private void injectCumulativeAdvances(List<ReportDTO> pastReports) throws DatabaseSystemException {
        int monthlyReportIndex = 0;

        for (ReportDTO report : pastReports) {
            if ("Parcial".equalsIgnoreCase(report.getReportType()) 
                    && "Aprobado".equalsIgnoreCase(report.getReviewStatus())) {
                this.generatePartialPdfButton.setDisable(true);
                this.partialResultsTextArea.setEditable(false);
                this.partialObservationsTextArea.setEditable(false);
                this.partialResultsTextArea.setText(report.getObservations());
                this.partialResultsTextArea.setStyle(
                        "-fx-background-color: #e9ecef;");
            }

            if (report.getReportType() != null && report.getReportType()
                    .toLowerCase().contains("mensual") && "Aprobado"
                    .equalsIgnoreCase(report.getReviewStatus())) {

                monthlyReportIndex++;
                List<ReportWeeklyAdvanceDTO> advances = this.advanceDAO
                        .getWeeklyAdvancesByReport(report.getIdReport());

                for (ReportWeeklyAdvanceDTO advance : advances) {
                    String databaseActivityName = this.getActivityNameById(
                            advance.getIdActivity()).trim();

                    for (PartialTimelineRowAdapterDTO row : this
                            .timelineObservableList) {
                        if (row.getActivityName().trim().equalsIgnoreCase(
                                databaseActivityName) && row.getTimeType()
                                .equalsIgnoreCase(advance.getRegistrationType())) {

                            this.mapWeeksByReportIndex(row, advance, 
                                    monthlyReportIndex);
                        }
                    }
                }
            }
        }
        this.partialActivitiesTimelineTableView.refresh();
    }

    private void mapWeeksByReportIndex(PartialTimelineRowAdapterDTO row, 
            ReportWeeklyAdvanceDTO advance, int reportIndex) {
        switch (reportIndex) {
            case 1:
                if (advance.isWeekOne()) { row.setWeekOne(true); }
                if (advance.isWeekTwo()) { row.setWeekTwo(true); }
                if (advance.isWeekThree()) { row.setWeekThree(true); }
                if (advance.isWeekFour()) { row.setWeekFour(true); }
                break;
            case 2:
                if (advance.isWeekOne()) { row.setWeekFive(true); }
                if (advance.isWeekTwo()) { row.setWeekSix(true); }
                if (advance.isWeekThree()) { row.setWeekSeven(true); }
                if (advance.isWeekFour()) { row.setWeekEight(true); }
                break;
            case 3:
                if (advance.isWeekOne()) { row.setWeekNine(true); }
                if (advance.isWeekTwo()) { row.setWeekTen(true); }
                if (advance.isWeekThree()) { row.setWeekEleven(true); }
                if (advance.isWeekFour()) { row.setWeekTwelve(true); }
                break;
            default:
                break;
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

    @FXML
    private void handleGeneratePartialPdf(ActionEvent event) {
        String resultsText = this.partialResultsTextArea.getText();
        String obsText = this.partialObservationsTextArea.getText();

        if (resultsText == null || resultsText.trim().isEmpty()) {
            AlertUtility.showWarningAlert("Campos Obligatorios", "La sección " 
                    + "de resultados parciales obtenidos es obligatoria.");
            return;
        }

        try {
            this.generatePartialPdfButton.setDisable(true);
            LOGGER.log(Level.INFO, "Executing synchronous statement write " 
                    + "transaction for Partial Report tracking milestones.");

            String enrollment = this.authenticatedStudent.getEnrollmentId()
                    .toUpperCase().trim();
            String userHome = System.getProperty("user.home");
            String destDir = userHome + java.io.File.separator + "Downloads" 
                    + java.io.File.separator;

            List<ReportDTO> pastReports = this.reportDAO
                    .getReportsByEnrollment(enrollment);

            ReportDTO existingPartial = null;
            for (ReportDTO report : pastReports) {
                if ("Parcial".equalsIgnoreCase(report.getReportType()) 
                        && report.getReportedHours() == 1) {
                    existingPartial = report;
                    break;
                }
            }

            if (existingPartial != null && "Aprobado"
                    .equalsIgnoreCase(existingPartial.getReviewStatus())) {
                throw new IllegalStateException("Tu Informe Parcial ya fue " 
                        + "revisado y VALIDADO como APROBADO por tu docente.");
            }

            ReportDTO mockReport = new ReportDTO();
            mockReport.setStudentEnrollment(enrollment);
            mockReport.setReportedHours(1);
            mockReport.setDeliveryDate(new java.sql.Date(
                    System.currentTimeMillis()));
            mockReport.setHoursCovered(this.cumulativeHoursCalculated);

            List<ReportActivityDTO> emptyList = new ArrayList<>();
            ReportDataMapper mapper = new ReportDataMapper();
            PartialReportDTO partialData = mapper.compilePartialReportData(
                    mockReport, emptyList, resultsText.trim(), obsText.trim());
            
            List<ActivityRowDTO> compiledActivities = this
                    .extractAllActivitiesFromGrid();
            partialData.setActivitiesList(compiledActivities);

            PartialReportPDFGenerator generator = new PartialReportPDFGenerator();
            generator.generateReport(partialData, destDir);

            java.io.File file = new java.io.File(destDir 
                    + "Reporte_Parcial_Unico.pdf");
            byte[] fileBytes = null;
            if (file.exists()) {
                fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
            }

            ReportDTO partialRecord = new ReportDTO();
            partialRecord.setStudentEnrollment(enrollment);
            partialRecord.setReportType("Parcial");
            partialRecord.setReportedHours(1);
            partialRecord.setDeliveryDate(new java.sql.Date(
                    System.currentTimeMillis()));
            partialRecord.setHoursCovered(this.cumulativeHoursCalculated);
            partialRecord.setDeliveryStatus("Vigente");
            partialRecord.setReviewStatus("Pendiente");
            partialRecord.setObservations(resultsText.trim());
            partialRecord.setFileContent(fileBytes);

            if (existingPartial == null) {
                LOGGER.log(Level.INFO, "Saving initial partial milestone.");
                this.reportDAO.saveReport(partialRecord);
            } else {
                LOGGER.log(Level.INFO, "Overwriting pending partial milestone.");
                this.reportDAO.overwriteReport(partialRecord);
            }

            AlertUtility.showInformationAlert("Informe Parcial Listo", 
                    "Tu reporte parcial acumulativo ha sido procesado.");

        } catch (IllegalStateException exception) {
            AlertUtility.showWarningAlert("Acceso Restringido", 
                    exception.getMessage());
        } catch (IOException | DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server write execution crash during " 
                    + "partial report document generation rules.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "Ocurrió un " 
                    + "error de red al sincronizar el hito parcial.");
        } finally {
            this.generatePartialPdfButton.setDisable(false);
        }
    }

    private List<ActivityRowDTO> extractAllActivitiesFromGrid() {
        List<ActivityRowDTO> list = new ArrayList<>();
        for (int i = 0; i < this.timelineObservableList.size(); i += 2) {
            PartialTimelineRowAdapterDTO planRow = this.timelineObservableList.get(i);
            PartialTimelineRowAdapterDTO realRow = this.timelineObservableList.get(i + 1);

            ActivityRowDTO dto = new ActivityRowDTO();
            dto.setDescription(planRow.getActivityName());

            dto.setPlanWeekOne(planRow.isWeekOne());
            dto.setPlanWeekTwo(planRow.isWeekTwo());
            dto.setPlanWeekThree(planRow.isWeekThree());
            dto.setPlanWeekFour(planRow.isWeekFour());
            dto.setPlanWeekFive(planRow.isWeekFive());
            dto.setPlanWeekSix(planRow.isWeekSix());
            dto.setPlanWeekSeven(planRow.isWeekSeven());
            dto.setPlanWeekEight(planRow.isWeekEight());
            dto.setPlanWeekNine(planRow.isWeekNine());
            dto.setPlanWeekTen(planRow.isWeekTen());
            dto.setPlanWeekEleven(planRow.isWeekEleven());
            dto.setPlanWeekTwelve(planRow.isWeekTwelve());

            dto.setRealWeekOne(realRow.isWeekOne());
            dto.setRealWeekTwo(realRow.isWeekTwo());
            dto.setRealWeekThree(realRow.isWeekThree());
            dto.setRealWeekFour(realRow.isWeekFour());
            dto.setRealWeekFive(realRow.isWeekFive());
            dto.setRealWeekSix(realRow.isWeekSix());
            dto.setRealWeekSeven(realRow.isWeekSeven());
            dto.setRealWeekEight(realRow.isWeekEight());
            dto.setRealWeekNine(realRow.isWeekNine());
            dto.setRealWeekTen(realRow.isWeekTen());
            dto.setRealWeekEleven(realRow.isWeekEleven());
            dto.setRealWeekTwelve(realRow.isWeekTwelve());

            list.add(dto);
        }
        return list;
    }

    @FXML
    private void handlePartialBack(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting current operational timeline form. " 
                + "Routing screen back to choices tracking dashboard catalog.");

        NavigationUtility.navigateTo(this.partialBackButton, 
                ViewConstants.VIEW_ADD_REPORT, 
                SystemConstants.TITLE_REPORTS_AVAILABLE);
    }

    private static class StaticCheckboxCellFactory implements Callback<
            TableColumn<PartialTimelineRowAdapterDTO, Boolean>, TableCell<
            PartialTimelineRowAdapterDTO, Boolean>> {
        @Override
        public TableCell<PartialTimelineRowAdapterDTO, Boolean> call(
                TableColumn<PartialTimelineRowAdapterDTO, Boolean> column) {
            return new FrozenCheckBoxTableCell();
        }
    }

    private static class FrozenCheckBoxTableCell extends CheckBoxTableCell<
            PartialTimelineRowAdapterDTO, Boolean> {
        public FrozenCheckBoxTableCell() {
            this.setDisable(true);
        }
    }
}