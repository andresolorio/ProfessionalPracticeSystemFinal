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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.util.Callback;
import javafx.util.StringConverter;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.EmailManager;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_STRING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MINIMUM_CREDITS_FOR_PRACTICES;

/**
 *
 * @author andre
 * @author cinth
 */
public class ProjectAssignmentViewController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(
            ProjectAssignmentViewController.class.getName());

    @FXML
    private ComboBox<StudentDTO> studentComboBox;
    @FXML
    private ComboBox<ProjectDTO> projectComboBox;
    @FXML
    private TextArea reasonTextArea;
    @FXML
    private Button assignButton;
    @FXML
    private Button cancelButton;

    private final ProjectDAO projectDAO = new ProjectDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private List<ProjectDTO> studentRequestedProjects;

    public ProjectAssignmentViewController() {
        this.studentRequestedProjects = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.setupComboBoxes();
        this.loadStudents();
        
        this.studentComboBox.getSelectionModel().selectedItemProperty()
                .addListener(new StudentSelectionChangeListener(this));
    }

    public void handleStudentSelection(StudentDTO selectedStudent) {
        if (selectedStudent != null) {
            if (this.isEligibleForAssignment(selectedStudent)) {
                this.validateStudentRequestsPrecondition(selectedStudent
                        .getEnrollmentId());
            } else {
                this.clearAndDisableAssignmentFields();
            }
        }
    }

    private boolean isEligibleForAssignment(StudentDTO student) {
        if (student.getCoveredCredits() < MINIMUM_CREDITS_FOR_PRACTICES) {
            AlertUtility.showWarningAlert("Alumno No Elegible", 
                    "El estudiante no cuenta con el 70% de créditos " 
                    + "necesarios (" + MINIMUM_CREDITS_FOR_PRACTICES + ").");
            return false;
        }
        return true;
    }

    public void clearAndDisableAssignmentFields() {
        this.projectComboBox.setItems(FXCollections.emptyObservableList());
        this.reasonTextArea.clear();
        this.projectComboBox.setDisable(true);
        this.reasonTextArea.setDisable(true);
        this.assignButton.setDisable(true);
    }

    private void loadStudents() {
        try {
            LOGGER.log(Level.INFO, "Fetching active students catalog list " 
                    + "synchronously from server repository layers.");
            
            ObservableList<StudentDTO> students = FXCollections
                    .observableArrayList(this.studentDAO.getAllActiveStudents());
            this.studentComboBox.setItems(students);
            
        } catch (DatabaseSystemException exception) { 
            LOGGER.log(Level.SEVERE, "Server communication failure captured " 
                    + "while loading students records catalog.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "obtener la lista de alumnos desde el servidor central.");
        }
    }

    private void validateStudentRequestsPrecondition(String enrollmentId) {
        try {
            LOGGER.log(Level.INFO, "Verifying project options precondition " 
                    + "synchronously for enrollment: {0}", enrollmentId);
            
            boolean hasRequests = this.projectDAO
                    .isStudentAlreadyRegisteredInRequest(enrollmentId);
            
            if (hasRequests) {
                this.loadSmartProjectCatalog(enrollmentId);
            } else {
                this.clearAndDisableAssignmentFields();
                
                AlertUtility.showWarningAlert("Alumno Sin Solicitudes", 
                        "El estudiante con matrícula " + enrollmentId 
                        + " no es elegible para una asignación oficial "
                        + "debido a que no ha completado el registro de " 
                        + "sus opciones de proyecto obligatorias.");
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server pipeline query failure check " 
                    + "verifying student choices metrics.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "verificar el estado de las opciones en el servidor.");
        }
    }

    public void loadSmartProjectCatalog(String enrollmentId) {
        try {
            LOGGER.log(Level.INFO, "Compiling smart project choices catalog " 
                    + "synchronously for enrollment ID: {0}", enrollmentId);

            this.studentRequestedProjects = this.projectDAO
                    .getRequestedProjectsByStudent(enrollmentId);
            
            List<ProjectDTO> allProjects = this.projectDAO
                    .getAllAvailableProjects();
            List<ProjectDTO> smartCatalog = new ArrayList<>();
            
            smartCatalog.addAll(this.studentRequestedProjects);
            
            ProjectDTO separator = new ProjectDTO();
            separator.setIdProject(-99);
            separator.setProjectName("--- OTROS PROYECTOS DISPONIBLES ---");
            smartCatalog.add(separator);
            
            for (ProjectDTO generalProject : allProjects) {
                boolean isAlreadyRequested = false;
                for (ProjectDTO reqProject : this.studentRequestedProjects) {
                    if (reqProject.getIdProject() == generalProject
                            .getIdProject()) {
                        isAlreadyRequested = true;
                        break;
                    }
                }
                if (!isAlreadyRequested) {
                    smartCatalog.add(generalProject);
                }
            }
            
            this.handleCatalogSuccess(smartCatalog);
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server pipeline communication failure " 
                    + "compiling smart project choices mapping payload.", 
                    exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se " 
                    + "pudieron compilar las opciones desde el servidor.");
        }
    }
    
    private void setupComboBoxes() {
        this.studentComboBox.setConverter(new StudentStringConverter());
        this.projectComboBox.setCellFactory(new ProjectCellFactory(
                this.studentRequestedProjects));
        this.projectComboBox.setButtonCell(new ProjectListCell(
                this.studentRequestedProjects));
    }

    public void handleCatalogSuccess(List<ProjectDTO> smartCatalog) {
        this.projectComboBox.setItems(FXCollections
                .observableArrayList(smartCatalog));
        this.projectComboBox.setDisable(false);
        this.reasonTextArea.setDisable(false);
        this.assignButton.setDisable(false);
    }

    @FXML
    private void handleAssign(ActionEvent event) {
        StudentDTO selectedStudent = this.studentComboBox.getSelectionModel()
                .getSelectedItem();
        ProjectDTO selectedProject = this.projectComboBox.getSelectionModel()
                .getSelectedItem();
        String assignmentReason = EMPTY_STRING;
        if (this.reasonTextArea.getText() != null) {
            assignmentReason = this.reasonTextArea.getText().trim();
        }

        if (selectedStudent == null || selectedProject == null 
                || selectedProject.getIdProject() == -99) {
            
            AlertUtility.showWarningAlert("Selección Requerida", "Debe " 
                    + "seleccionar un alumno y un proyecto válido.");
            return;
        }

        if (assignmentReason.isEmpty()) {
            AlertUtility.showWarningAlert("Motivos Obligatorios", "Por favor " 
                    + "introduzca la exposición de motivos académicos.");
            return;
        }

        this.executeAssignmentTransaction(selectedStudent, selectedProject, 
                assignmentReason);
    }

    private void executeAssignmentTransaction(StudentDTO student, 
            ProjectDTO project, String reason) {
        
        try {
            this.assignButton.setDisable(true);
            LOGGER.log(Level.INFO, "Initiating synchronous database write " 
                    + "transaction for project assignment profile link stream.");

            if (this.projectDAO.isStudentAlreadyAssigned(student
                    .getEnrollmentId())) {
                AlertUtility.showWarningAlert("Alumno ya asignado", 
                        "El alumno " + student.getEnrollmentId() + " ya cuenta " 
                        + "con un proyecto asignado previamente.");
                return;
            }
            
            this.studentDAO.assignProjectToStudent(student.getEnrollmentId(), 
                    project.getIdProject(), reason);
            
            ProjectDTO detailedProject = this.projectDAO.getProjectById(
                    project.getIdProject());
            
            String responsibleName = "Por asignar (Contactar a la organización)";
            if (detailedProject.getTechnicalResponsibleName() != null) {
                responsibleName = detailedProject.getTechnicalResponsibleName();
            }
            
            EmailManager.sendProjectAssignmentNotification(
                    student.getEmail(),
                    student.getFirstName() + " " + student.getPaternalLastName(),
                    detailedProject.getProjectName(),
                    detailedProject.getOrganizationName(),
                    responsibleName,
                    reason
            );
            
            AlertUtility.showInformationAlert("Asignación Exitosa", "El " 
                    + "proyecto ha sido vinculado oficialmente, el cupo ha " 
                    + "disminuido y se envió la notificación al alumno.");
            this.handleCancel(null);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server transaction execution failure " 
                    + "during student project assignment pipeline processing.", 
                    exception);
            
            String errorMessage = "No se pudo registrar la asignación en el " 
                    + "servidor central de la aplicación.";
            
            if (exception.getMessage() != null) {
                String message = exception.getMessage();
                if (message.contains("chk_vacantes_disponibles") 
                        || message.contains("Integridad")) {
                    errorMessage = "Error: El proyecto seleccionado se ha " 
                            + "quedado sin cupos disponibles en este momento.";
                }
            }
            AlertUtility.showErrorAlert("Error de Asignación", errorMessage);
        } finally {
            this.assignButton.setDisable(false);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting assignment configurations wizard. " 
                + "Routing screen context back to coordinator menu.");

        NavigationUtility.navigateTo(this.cancelButton, 
                ViewConstants.VIEW_COORDINATOR_MENU, 
                SystemConstants.TITLE_COORDINATOR_MENU);
    }

    public void updateStudentRequestedProjects(List<ProjectDTO> requested) {
        this.studentRequestedProjects = requested;
    }

    private static class StudentSelectionChangeListener 
            implements ChangeListener<StudentDTO> {
        private final ProjectAssignmentViewController controller;

        public StudentSelectionChangeListener(
                ProjectAssignmentViewController controller) {
            this.controller = controller;
        }

        @Override
        public void changed(ObservableValue<? extends StudentDTO> observable, 
                StudentDTO oldStudent, StudentDTO selectedStudent) {
            this.controller.handleStudentSelection(selectedStudent);
        }
    }

    private static class StudentStringConverter 
            extends StringConverter<StudentDTO> {
        @Override
        public String toString(StudentDTO student) {
            String result = EMPTY_STRING;
            if (student != null) {
                result = student.getEnrollmentId() + " - " 
                        + student.getFirstName() + " " 
                        + student.getPaternalLastName();
            }
            return result;
        }
        @Override 
        public StudentDTO fromString(String string) { 
            return null; 
        }
    }

    private static class ProjectCellFactory 
            implements Callback<ListView<ProjectDTO>, ListCell<ProjectDTO>> {
        private final List<ProjectDTO> requestedProjects;

        public ProjectCellFactory(List<ProjectDTO> requestedProjects) {
            this.requestedProjects = requestedProjects;
        }

        @Override
        public ListCell<ProjectDTO> call(ListView<ProjectDTO> listView) {
            return new ProjectListCell(this.requestedProjects);
        }
    }

    private static class ProjectListCell extends ListCell<ProjectDTO> {
        private final List<ProjectDTO> requestedProjects;

        public ProjectListCell(List<ProjectDTO> requestedProjects) {
            this.requestedProjects = requestedProjects;
        }

        @Override
        protected void updateItem(ProjectDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                this.setText(null);
                this.setDisable(false);
                this.setStyle(EMPTY_STRING);
            } else if (item.getIdProject() == -99) {
                this.setText(item.getProjectName());
                this.setDisable(true); 
                this.setStyle("-fx-text-fill: #888888; -fx-font-weight: bold; " 
                        + "-fx-background-color: #eeeeee;");
            } else {
                int priorityIndex = -1;
                for (int i = 0; i < this.requestedProjects.size(); i++) {
                    if (this.requestedProjects.get(i).getIdProject() 
                            == item.getIdProject()) {
                        priorityIndex = i + 1;
                        break;
                    }
                }

                String prefix = "";
                if (priorityIndex != -1) {
                    prefix = "[Opción " + priorityIndex + "] ";
                }
                String vacanciesSuffix = " (Lugares: " 
                        + item.getAvailableVacancies() + ")";

                if (item.getAvailableVacancies() <= 0) {
                    this.setText(prefix + item.getProjectName() 
                            + " - [CUPO LLENO]");
                    this.setDisable(true);
                    this.setStyle("-fx-text-fill: #bbbbbb;");
                } else {
                    this.setText(prefix + item.getProjectName() 
                            + vacanciesSuffix);
                    this.setDisable(false);
                    this.setStyle(EMPTY_STRING);
                }
            }
        }
    }
}