package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.DeadlineDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DeadlineDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;

/**
 *
 * @author andre
 * @author cinth
 */
public class SelfEvaluationDeadlineConfigurationViewController 
        implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            SelfEvaluationDeadlineConfigurationViewController.class.getName());

    @FXML
    private DatePicker deadlineDatePicker;
    @FXML
    private TextField timeTextField;
    @FXML
    private TableView<DeadlineDTO> deadlinesTableView;
    @FXML
    private TableColumn<DeadlineDTO, String> reportTypeTableColumn;
    @FXML
    private TableColumn<DeadlineDTO, String> deadlineTableColumn;
    @FXML
    private TableColumn<DeadlineDTO, String> statusTableColumn;
    @FXML
    private Button returnButton;
    @FXML
    private Button saveButton;

    private final ObservableList<DeadlineDTO> deadlinesObservableList;
    private DeadlineDAO deadlineDAO;
    private ProfessorDAO professorDAO;
    private String currentProfessorStaffNumber;

    private static final String REPORT_TYPE = "Autoevaluación";

    public SelfEvaluationDeadlineConfigurationViewController() {
        this.deadlinesObservableList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.deadlineDAO = new DeadlineDAO();
        this.professorDAO = new ProfessorDAO();

        this.configureTableColumns();
        this.recoverProfessorSessionAndLoadDeadlines();
    }

    private void configureTableColumns() {
        this.reportTypeTableColumn.setCellValueFactory(
                new ReportTypeCellValueFactory());
        this.deadlineTableColumn.setCellValueFactory(
                new DeadlineDateCellValueFactory());
        this.statusTableColumn.setCellValueFactory(
                new StatusCellValueFactory());

        this.deadlinesTableView.setItems(this.deadlinesObservableList);
    }

    public void recoverProfessorSessionAndLoadDeadlines() {
        String loggedEmail = UserSession.getInstance().getLoggedUser()
                .getEmail();

        try {
            LOGGER.log(Level.INFO, "Recovering professor operational profile " 
                    + "synchronously from the server repository context.");
            
            ProfessorDTO professor = this.professorDAO
                    .getProfessorByEmail(loggedEmail);

            if (professor != null) {
                this.currentProfessorStaffNumber = professor
                        .getProfessorStaffNumber();
                this.loadSelfEvaluationDeadlines();
            } else {
                AlertUtility.showErrorAlert("Error de Contexto", 
                        "No se encontró el registro del profesor.");
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server infrastructure lookup failure " 
                    + "during profile identity context mapping.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "Error de " 
                    + "comunicación con el servidor central.");
        }
    }

    public void loadSelfEvaluationDeadlines() {
        try {
            LOGGER.log(Level.INFO, "Fetching configured system evaluation " 
                    + "deadlines synchronously from server layers.");
            
            List<DeadlineDTO> results = this.deadlineDAO
                    .getDeadlinesByProfessorStaffNumber(
                    this.currentProfessorStaffNumber);

            this.deadlinesObservableList.clear();
            for (DeadlineDTO deadlinedto : results) {
                if (REPORT_TYPE.equals(deadlinedto.getReportType())) {
                    this.deadlinesObservableList.add(deadlinedto);
                }
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication failure checking " 
                    + "existing deadline indicators.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se " 
                    + "pudo actualizar el histórico de plazos.");
        }
    }

    @FXML
    private void handleSaveDeadline(ActionEvent event) {
        LocalDate selectedDate = this.deadlineDatePicker.getValue();
        String rawTime = "";
        if (this.timeTextField.getText() != null) {
            rawTime = this.timeTextField.getText().trim();
        }

        if (selectedDate == null || rawTime.isEmpty()) {
            AlertUtility.showWarningAlert("Campos Obligatorios", 
                    "Por favor, seleccione una fecha e introduzca una hora.");
            return;
        }

        LocalTime parsedTime;
        try {
            parsedTime = LocalTime.parse(rawTime, DateTimeFormatter
                    .ofPattern(SystemConstants.TIME_FORMAT_SHORT));
        } catch (DateTimeParseException exception) {
            AlertUtility.showWarningAlert("Formato de Hora Inválido",
                    "La hora debe cumplir con el formato de 24 horas.");
            return;
        }

        LocalDateTime composedDeadline = LocalDateTime.of(selectedDate, 
                parsedTime);
        if (composedDeadline.isBefore(LocalDateTime.now())) {
            AlertUtility.showWarningAlert("Fecha Extemporánea", 
                    "No es posible establecer un plazo en un tiempo pasado.");
            return;
        }

        DeadlineDTO newDeadline = new DeadlineDTO();
        newDeadline.setReportType(REPORT_TYPE);
        newDeadline.setDeadlineDate(composedDeadline);
        newDeadline.setStaffNumber(this.currentProfessorStaffNumber);

        try {
            this.saveButton.setDisable(true);
            LOGGER.log(Level.INFO, "Submitting new evaluation deadline " 
                    + "synchronously to the server application pipeline.");
            
            this.deadlineDAO.saveOrUpdateDeadline(newDeadline);

            AlertUtility.showInformationAlert("Plazo Registrado", 
                    "La fecha límite se guardó correctamente.");

            this.deadlineDatePicker.setValue(null);
            this.timeTextField.clear();
            this.loadSelfEvaluationDeadlines();
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server writing transaction failure " 
                    + "persisting configuration rules framework.", exception);
            
            AlertUtility.showErrorAlert("Error del Servidor", "No se pudo " 
                    + "guardar el plazo. Verifique la estabilidad de su red.");
        } finally {
            this.saveButton.setDisable(false);
        }
    }

    @FXML
    private void handleReturn(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting execution routine. Routing " 
                + "window clients back to core ProfessorMenuView stage layout.");
        
        NavigationUtility.navigateTo(this.returnButton, 
                ViewConstants.VIEW_PROFESSOR_MENU, 
                SystemConstants.TITLE_PROFESSOR_MENU_SYSTEM);
    }

    private static class ReportTypeCellValueFactory 
            implements Callback<CellDataFeatures<DeadlineDTO, String>, 
            ObservableValue<String>> {

        @Override
        public ObservableValue<String> call(
                CellDataFeatures<DeadlineDTO, String> cellData) {
            return new SimpleStringProperty(cellData.getValue()
                    .getReportType());
        }
    }

    private static class DeadlineDateCellValueFactory 
            implements Callback<CellDataFeatures<DeadlineDTO, String>, 
            ObservableValue<String>> {

        @Override
        public ObservableValue<String> call(
                CellDataFeatures<DeadlineDTO, String> cellData) {
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern(SystemConstants.DATE_PATTERN_DISPLAY);
            LocalDateTime dateTime = cellData.getValue().getDeadlineDate();

            String cellText = SystemConstants.DEADLINE_NO_DATE_LABEL;
            if (dateTime != null) {
                cellText = dateTime.format(formatter);
            }
            return new SimpleStringProperty(cellText);
        }
    }

    private static class StatusCellValueFactory 
            implements Callback<CellDataFeatures<DeadlineDTO, String>, 
            ObservableValue<String>> {

        @Override
        public ObservableValue<String> call(
                CellDataFeatures<DeadlineDTO, String> cellData) {
            LocalDateTime deadline = cellData.getValue().getDeadlineDate();

            if (deadline == null) {
                return new SimpleStringProperty(SystemConstants
                        .DEADLINE_NOT_CONFIGURED_LABEL);
            }

            String status = SystemConstants.DEADLINE_STATUS_ACTIVE;
            if (deadline.isBefore(LocalDateTime.now())) {
                status = SystemConstants.DEADLINE_STATUS_EXPIRED;
            }
            return new SimpleStringProperty(status);
        }
    }
}