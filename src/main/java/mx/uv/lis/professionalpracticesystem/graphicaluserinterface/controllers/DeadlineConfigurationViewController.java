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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.StudentController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.DeadlineDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.EducativeExperienceDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DeadlineDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DeadlineCustomRowDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EducativeExperienceDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_STRING;

/**
 *
 * @author andre
 * @author cinth
 */
public class DeadlineConfigurationViewController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(
            DeadlineConfigurationViewController.class.getName());

    @FXML
    private ComboBox<String> educationalExperienceComboBox;
    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private DatePicker deadlineDatePicker;
    @FXML
    private TextField timeLimitTextField;
    @FXML
    private TableView<DeadlineCustomRowDTO> deadlinesTableView;
    @FXML
    private TableColumn<DeadlineCustomRowDTO, Integer> nrcTableColumn;
    @FXML
    private TableColumn<DeadlineCustomRowDTO, String> reportTypeTableColumn;
    @FXML
    private TableColumn<DeadlineCustomRowDTO, String> dateTableColumn;
    @FXML
    private TableColumn<DeadlineCustomRowDTO, String> statusTableColumn;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final StudentController studentController;
    private final EducativeExperienceDAO experienceDAO;
    private final DeadlineDAO deadlineDAO;
    private ObservableList<DeadlineCustomRowDTO> deadlineRowsObservableList;
    private List<DeadlineDTO> rawDeadlinesCache;
    private List<EducativeExperienceDTO> professorExperiencesCache;

    public DeadlineConfigurationViewController() {
        this.studentController = new StudentController();
        this.experienceDAO = new EducativeExperienceDAO();
        this.deadlineDAO = new DeadlineDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.configureTableViewColumns();

        this.reportTypeComboBox.setItems(FXCollections.observableArrayList(
                "Reporte Mensual", "Reporte Parcial", "Informe Final", 
                "Autoevaluación"
        ));

        this.loadProfessorExperiencesAndDeadlines();
        LOGGER.log(Level.INFO, "Deadline configuration view components " 
                + "initialized successfully.");
    }

    private void configureTableViewColumns() {
        this.nrcTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("nrc"));
        this.reportTypeTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("reportType"));
        this.dateTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("formattedDate"));
        this.statusTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("statusText"));
    }

    private void loadProfessorExperiencesAndDeadlines() {
        String professorEmail = UserSession.getInstance().getLoggedUser()
                .getEmail();
        
        try {
            LOGGER.log(Level.INFO, "Fetching educational experiences " 
                    + "synchronously from server database context.");
            
            List<EducativeExperienceDTO> experiences = this.experienceDAO
                    .getEducativeExperiencesWithProfessorsByProfessorEmail(
                    professorEmail);
            
            this.handleLoadExperiencesSuccess(experiences);
            this.loadExistingDeadlines();
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication pipeline failure " 
                    + "loading professor academic profile catalogs.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se " 
                    + "pudieron cargar las materias asignadas desde el servidor.");
        }
    }

    public void handleLoadExperiencesSuccess(
            List<EducativeExperienceDTO> experiences) {
        this.professorExperiencesCache = experiences;
        ObservableList<String> items = FXCollections.observableArrayList();
        
        for (EducativeExperienceDTO dto : experiences) {
            items.add(dto.getNrc() + " - " + dto.getEducativeExperienceName());
        }
        this.educationalExperienceComboBox.setItems(items);
    }

    public void loadExistingDeadlines() {
        String professorEmail = UserSession.getInstance().getLoggedUser()
                .getEmail();
        
        try {
            LOGGER.log(Level.INFO, "Loading active deadline rosters " 
                    + "synchronously from the server repository layer.");
            
            String staffNumber = this.studentController
                    .getProfessorStaffNumber(professorEmail);
            
            List<DeadlineDTO> results = this.deadlineDAO
                    .getDeadlinesByProfessorStaffNumber(staffNumber);
            
            this.rawDeadlinesCache = results;
            this.deadlineRowsObservableList = FXCollections
                    .observableArrayList();

            for (DeadlineDTO dto : results) {
                this.deadlineRowsObservableList.add(
                        new DeadlineCustomRowDTO(dto));
            }
            this.deadlinesTableView.setItems(this.deadlineRowsObservableList);
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server pipeline error retrieving " 
                    + "configured deadlines framework rules.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudieron " 
                    + "recuperar los plazos vigentes desde el servidor.");
        }
    }

    @FXML
    private void handleSaveDeadline(ActionEvent event) {
        String selectedExperience = this.educationalExperienceComboBox
                .getValue();
        String selectedType = this.reportTypeComboBox.getValue();
        LocalDate selectedDate = this.deadlineDatePicker.getValue();
        String rawTime = EMPTY_STRING;

        if (this.timeLimitTextField.getText() != null) {
            rawTime = this.timeLimitTextField.getText().trim();
        }

        if (selectedExperience == null || selectedType == null 
                || selectedDate == null || rawTime.isEmpty()) {
            
            AlertUtility.showWarningAlert("Campos Incompletos", "Por favor " 
                    + "rellene todos los campos obligatorios (*).");
            return;
        }

        LocalTime parsedTime;
        try {
            parsedTime = LocalTime.parse(rawTime, DateTimeFormatter
                    .ofPattern("HH:mm"));
        } catch (DateTimeParseException exception) {
            AlertUtility.showWarningAlert("Hora Inválida", "Formato de " 
                    + "24hrs incorrecto (HH:mm).");
            return;
        }

        LocalDateTime composedDeadline = LocalDateTime.of(selectedDate, 
                parsedTime);
        if (composedDeadline.isBefore(LocalDateTime.now())) {
            AlertUtility.showWarningAlert("Fecha Inválida", "La fecha límite " 
                    + "no puede ser una fecha pasada.");
            return;
        }

        int targetNrc = Integer.parseInt(selectedExperience.split(" - ")[0]);

        String dbReportType = selectedType;
        if ("Reporte Mensual".equalsIgnoreCase(selectedType)) {
            dbReportType = "Mensual";
        } else if ("Reporte Parcial".equalsIgnoreCase(selectedType)) {
            dbReportType = "Parcial";
        } else if ("Informe Final".equalsIgnoreCase(selectedType)) {
            dbReportType = "Final";
        }

        int nextReportNumber = 1;
        boolean isDuplicateUniqueReport = false;

        if (this.rawDeadlinesCache != null) {
            for (DeadlineDTO existingDeadline : this.rawDeadlinesCache) {
                if (existingDeadline.getNrc() == targetNrc && existingDeadline
                        .getReportType().equalsIgnoreCase(dbReportType)) {
                    if ("Mensual".equalsIgnoreCase(dbReportType)) {
                        nextReportNumber++;
                    } else {
                        isDuplicateUniqueReport = true;
                        break;
                    }
                }
            }
        }

        if (isDuplicateUniqueReport) {
            AlertUtility.showWarningAlert("Plazo Ya Existente", "El plazo " 
                    + "para '" + selectedType + "' ya se encuentra configurado.");
            return;
        }

        if ("Mensual".equalsIgnoreCase(dbReportType) && nextReportNumber > 6) {
            AlertUtility.showWarningAlert("Límite Alcanzado", "Ya se han " 
                    + "configurado los plazos para las 6 entregas mensuales.");
            return;
        }

        this.processSaveDeadline(targetNrc, dbReportType, nextReportNumber, 
                composedDeadline, selectedType);
    }

    private void processSaveDeadline(int nrc, String reportType, 
            int reportNumber, LocalDateTime deadlineDateTime, 
            String originalType) {
        
        String professorEmail = UserSession.getInstance().getLoggedUser()
                .getEmail();

        try {
            this.saveButton.setDisable(true);
            LOGGER.log(Level.INFO, "Executing synchronous statement write " 
                    + "for new academic deadline policy parameters.");

            String staffNumber = this.studentController
                    .getProfessorStaffNumber(professorEmail);

            if (staffNumber == null) {
                throw new DatabaseSystemException("No se encontró número de " 
                        + "personal para el docente activo en la sesión.");
            }

            DeadlineDTO newDeadline = new DeadlineDTO();
            newDeadline.setNrc(nrc);
            newDeadline.setReportType(reportType);
            newDeadline.setReportedNumber(reportNumber);
            newDeadline.setDeadlineDate(deadlineDateTime);
            newDeadline.setStaffNumber(staffNumber);

            this.deadlineDAO.saveOrUpdateDeadline(newDeadline);

            AlertUtility.showInformationAlert(SystemConstants
                    .ALERT_TITLE_DEADLINE_SAVED, "El plazo de entrega para '" 
                    + originalType + "' se guardó exitosamente.");
            
            this.clearFormFields();
            this.loadExistingDeadlines();

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server transaction engineering error " 
                    + "persisting deadline policy data layout.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "completar la transacción en el servidor central.");
        } finally {
            this.saveButton.setDisable(false);
        }
    }

    private void clearFormFields() {
        this.educationalExperienceComboBox.getSelectionModel().clearSelection();
        this.reportTypeComboBox.getSelectionModel().clearSelection();
        this.deadlineDatePicker.setValue(null);
        this.timeLimitTextField.clear();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting current configurations panel. " 
                + "Routing screen view stage context back to user menu.");
        
        String userRole = UserSession.getInstance().getUserRole();

        if (SystemConstants.ROLE_COORDINATOR_MATCH.equalsIgnoreCase(userRole)) {
            NavigationUtility.navigateTo(this.cancelButton, 
                    ViewConstants.VIEW_COORDINATOR_MENU, 
                    SystemConstants.TITLE_COORDINATOR_MENU_DEADLINE);
        } else {
            NavigationUtility.navigateTo(this.cancelButton, 
                    ViewConstants.VIEW_PROFESSOR_MENU, 
                    SystemConstants.TITLE_PROFESSOR_MENU_DEADLINE);
        }
    }
}