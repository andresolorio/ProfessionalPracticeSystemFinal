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
public class PartialDeadlineConfigurationViewController 
        implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            PartialDeadlineConfigurationViewController.class.getName());

    @FXML
    private TextField reportTypeTextField;
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

    public PartialDeadlineConfigurationViewController() {
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
            LOGGER.log(Level.INFO, "Recovering professor session context " 
                    + "synchronously from the application server database.");
            
            ProfessorDTO professor = this.professorDAO
                    .getProfessorByEmail(loggedEmail);

            if (professor != null) {
                this.currentProfessorStaffNumber = professor
                        .getProfessorStaffNumber();
                this.loadProfessorDeadlines();
            } else {
                AlertUtility.showErrorAlert("Error de Sesión", "No se " 
                        + "encontró el registro del profesor en el sistema.");
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server pipeline query failure looking " 
                    + "up professor operational identity profile.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "Ocurrió un " 
                    + "problema de comunicación con el servidor central.");
        }
    }

    public void loadProfessorDeadlines() {
        try {
            LOGGER.log(Level.INFO, "Fetching active operational deadlines " 
                    + "synchronously from application server logic layers.");

            List<DeadlineDTO> results = this.deadlineDAO
                    .getDeadlinesByProfessorStaffNumber(
                    this.currentProfessorStaffNumber);

            this.deadlinesObservableList.clear();
            for (DeadlineDTO deadline : results) {
                if (deadline.getReportType().equals("Reporte Parcial")) {
                    this.deadlinesObservableList.add(deadline);
                }
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server infrastructure data lookup " 
                    + "failure while updating table timeline data.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "actualizar el historial de fechas límite.");
        }
    }

    @FXML
    private void handleSaveDeadline(ActionEvent event) {
        String selectedType = this.reportTypeTextField.getText().trim();
        LocalDate selectedDate = this.deadlineDatePicker.getValue();
        String rawTime = "";

        if (this.timeTextField.getText() != null) {
            rawTime = this.timeTextField.getText().trim();
        }

        if (selectedDate == null || rawTime.isEmpty()) {
            AlertUtility.showWarningAlert("Campos Incompletos", "Por favor " 
                    + "rellene todos los campos obligatorios (*).");
            return;
        }

        LocalTime parsedTime;
        try {
            parsedTime = LocalTime.parse(rawTime, DateTimeFormatter
                    .ofPattern("HH:mm"));
        } catch (DateTimeParseException exception) {
            AlertUtility.showWarningAlert("Hora Inválida", "Por favor " 
                    + "introduzca la hora en un formato de 24 horas (HH:mm).");
            return;
        }

        LocalDateTime composedDeadline = LocalDateTime.of(selectedDate, 
                parsedTime);
        if (composedDeadline.isBefore(LocalDateTime.now())) {
            AlertUtility.showWarningAlert("Fecha Expirada", "La fecha límite " 
                    + "no puede establecerse en un tiempo pasado.");
            return;
        }

        DeadlineDTO newDeadline = new DeadlineDTO();
        newDeadline.setReportType(selectedType);
        newDeadline.setDeadlineDate(composedDeadline);
        newDeadline.setStaffNumber(this.currentProfessorStaffNumber);

        try {
            this.saveButton.setDisable(true);
            LOGGER.log(Level.INFO, "Submitting new evaluation deadline rule " 
                    + "synchronously to the server context ecosystem.");
            
            this.deadlineDAO.saveOrUpdateDeadline(newDeadline);

            AlertUtility.showInformationAlert("Plazo Registrado", "La fecha " 
                    + "límite se ha guardado y sincronizado con éxito.");

            this.deadlineDatePicker.setValue(null);
            this.timeTextField.clear();
            this.loadProfessorDeadlines();
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server transaction execution failure " 
                    + "writing new deadline parameters data.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "almacenar la configuración en el servidor central.");
        } finally {
            this.saveButton.setDisable(false);
        }
    }

    @FXML
    private void handleReturn(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting operation timeline session. " 
                + "Routing window view stage back to ProfessorMenuView.");
        
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
                    .ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dateTime = cellData.getValue().getDeadlineDate();
            String cellText = "Sin fecha";
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
                return new SimpleStringProperty("No configurado");
            }
            String status = "Vigente / Activo";
            if (deadline.isBefore(LocalDateTime.now())) {
                status = "Expirado / Extemporáneo";
            }
            return new SimpleStringProperty(status);
        }
    }
}