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
import javafx.scene.control.ComboBox;
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
public class FinalDeadlineConfigurationViewController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(
            FinalDeadlineConfigurationViewController.class.getName());

    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private DatePicker deadlineDatePicker;
    @FXML
    private TextField timeTextField;
    @FXML
    private TableView<DeadlineDTO> deadlinesTableView;
    @FXML
    private TableColumn<DeadlineDTO, String> reportTypeColumn;
    @FXML
    private TableColumn<DeadlineDTO, String> deadlineColumn;
    @FXML
    private TableColumn<DeadlineDTO, String> statusColumn;
    @FXML
    private Button returnButton;
    @FXML
    private Button saveButton;

    private final ObservableList<DeadlineDTO> deadlinesObservableList;
    private DeadlineDAO deadlineDAO;
    private ProfessorDAO professorDAO;
    private String currentProfessorStaffNumber;

    public FinalDeadlineConfigurationViewController() {
        this.deadlinesObservableList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.deadlineDAO = new DeadlineDAO();
        this.professorDAO = new ProfessorDAO();       
        this.reportTypeComboBox.getItems().addAll("Informe Final");
        
        this.configureTableColumns();
        this.recoverProfessorSessionAndLoadDeadlines();
    }

    private void configureTableColumns() {
        this.reportTypeColumn.setCellValueFactory(
                new ReportTypeCellValueFactory());
        this.deadlineColumn.setCellValueFactory(
                new DeadlineDateCellValueFactory());
        this.statusColumn.setCellValueFactory(
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
                this.loadProfessorDeadlines();
            } else {
                AlertUtility.showErrorAlert("Error de Sesión", "No se pudo " 
                        + "recuperar la información del profesor.");
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server infrastructure lookup failure " 
                    + "during identity profile query.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "Error técnico " 
                    + "al validar credenciales con el servidor central.");
        }
    }

    public void loadProfessorDeadlines() {
        try {
            LOGGER.log(Level.INFO, "Fetching final administrative deadlines " 
                    + "synchronously from server layers.");
            
            List<DeadlineDTO> results = this.deadlineDAO
                    .getDeadlinesByProfessorStaffNumber(
                    this.currentProfessorStaffNumber);

            this.deadlinesObservableList.clear();
            for (DeadlineDTO dto : results) {
                String type = dto.getReportType();
                if ("Informe Final".equals(type) 
                        || "Autoevaluación".equals(type)) {
                    this.deadlinesObservableList.add(dto);
                }
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication failure checking " 
                    + "configured closing system deadlines.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "actualizar la lista de plazos finales desde el servidor.");
        }
    }

    @FXML
    private void handleSaveDeadline(ActionEvent event) {
        String selectedType = this.reportTypeComboBox.getValue();
        LocalDate selectedDate = this.deadlineDatePicker.getValue();
        String rawTime = "";
        if (this.timeTextField.getText() != null) {
            rawTime = this.timeTextField.getText().trim();
        }

        if (selectedType == null || selectedDate == null || rawTime.isEmpty()) {
            AlertUtility.showWarningAlert("Campos Vacíos", "Por favor, " 
                    + "complete todos los campos requeridos (*).");
            return;
        }

        LocalTime parsedTime;
        try {
            parsedTime = LocalTime.parse(rawTime, DateTimeFormatter
                    .ofPattern("HH:mm"));
        } catch (DateTimeParseException exception) {
            AlertUtility.showWarningAlert("Formato de Hora Incorrecto", 
                    "Introduzca una hora válida en formato de 24 horas.");
            return;
        }

        LocalDateTime composedDeadline = LocalDateTime.of(selectedDate, 
                parsedTime);
        if (composedDeadline.isBefore(LocalDateTime.now())) {
            AlertUtility.showWarningAlert("Fecha Inválida", "La fecha límite " 
                    + "no puede configurarse en el pasado.");
            return;
        }

        DeadlineDTO newDeadline = new DeadlineDTO();
        newDeadline.setReportType(selectedType);
        newDeadline.setDeadlineDate(composedDeadline);
        newDeadline.setStaffNumber(this.currentProfessorStaffNumber);

        try {
            this.saveButton.setDisable(true);
            LOGGER.log(Level.INFO, "Submitting new closing system deadline " 
                    + "rule synchronously to the database pipeline.");
            
            this.deadlineDAO.saveOrUpdateDeadline(newDeadline);

            AlertUtility.showInformationAlert("Configuración Guardada", 
                    "El plazo de entrega final se ha registrado con éxito.");

            this.reportTypeComboBox.setValue(null);
            this.deadlineDatePicker.setValue(null);
            this.timeTextField.clear();
            this.loadProfessorDeadlines();
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server write transaction execution " 
                    + "failure persisting deadline configuration.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "conectar con el servidor para guardar los cambios.");
        } finally {
            this.saveButton.setDisable(false);
        }
    }

    @FXML
    private void handleReturn(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting deadlines configuration session. " 
                + "Routing window view stage back to ProfessorMenuView.");
        
        NavigationUtility.navigateTo(this.returnButton, 
                ViewConstants.VIEW_PROFESSOR_MENU, 
                SystemConstants.TITLE_PROFESSOR_MENU);
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
            String text = "Sin fecha";
            if (dateTime != null) {
                text = dateTime.format(formatter);
            }
            return new SimpleStringProperty(text);
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