package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.LinkedOrganizationDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ResponsibleProjectDAO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;

/**
 *
 * @author cinth
 * @author andre
 */
public class CoordinatorMenuViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            CoordinatorMenuViewController.class.getName());

    @FXML
    private Button logoutButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label sessionDetailsLabel;    
    @FXML
    private Button registerResponsibleButton;
    @FXML
    private Button registerProjectButton;
    @FXML
    private Button switchPerformanceRoleButton;

    private final LinkedOrganizationDAO organizationDAO;
    private final ProfessorDAO professorDAO;
    private final StudentDAO studentDAO;
    private final ResponsibleProjectDAO responsibleDAO;
    private final ProjectDAO projectDAO;

    public CoordinatorMenuViewController() {
        this.organizationDAO = new LinkedOrganizationDAO();
        this.professorDAO = new ProfessorDAO();
        this.studentDAO = new StudentDAO();
        this.responsibleDAO = new ResponsibleProjectDAO();
        this.projectDAO = new ProjectDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.showSessionInformation();
        this.executePreconditionsCheck();
        this.switchPerformanceRoleButton.setOnAction(
            new SwitchPerformanceRoleButtonEventHandler(this));
    }

    private void showSessionInformation() {
        UserDTO loggedUser = UserSession.getInstance().getLoggedUser();

        if (loggedUser != null) {
            try {
                ProfessorDTO currentProfessor = this.professorDAO
                        .getProfessorByEmail(loggedUser.getEmail());

                if (currentProfessor != null) {
                    String fullName = currentProfessor.getFirstName() + " "
                            + currentProfessor.getPaternalLastName() + " "
                            + currentProfessor.getMaternalLastName();

                    this.welcomeLabel.setText(
                            SystemConstants.WELCOME_PREFIX + fullName);

                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter
                            .ofPattern("dd/MM/yyyy HH:mm");
                    this.sessionDetailsLabel.setText(
                            SystemConstants.SESSION_DATE_PREFIX 
                            + now.format(formatter));
                }
            } catch (DatabaseSystemException exception) {
                LOGGER.log(Level.SEVERE, "Technical server communication " 
                        + "exception catch injecting context mapping data.", 
                        exception);
                this.welcomeLabel.setText(SystemConstants.WELCOME_PREFIX 
                        + loggedUser.getEmail());
                this.sessionDetailsLabel.setText(SystemConstants
                        .SESSION_DATE_PREFIX 
                        + SystemConstants.AUXILIARY_MODE_LABEL);
            }
        }
    }

    private void executePreconditionsCheck() {
        try {
            boolean hasOrganizations = this.organizationDAO.hasOrganizations();
            if (!hasOrganizations) {
                if (this.registerResponsibleButton != null) {
                    this.registerResponsibleButton.setDisable(true);
                }
                if (this.registerProjectButton != null) {
                    this.registerProjectButton.setDisable(true);
                }
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server infrastructure lookup failure " 
                    + "during initialization validation routine.", exception);
        }
    }

    @FXML
    private void handleLogout() {
        UserSession.getInstance().logout();
        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_LOGIN, SystemConstants.TITLE_LOGIN);
    }

    @FXML
    private void handleRegisterStudent() {
        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_STUDENT_REGISTRATION, 
                SystemConstants.TITLE_REGISTER_STUDENT);
    }

    @FXML
    private void handleInactivateStudent() {
        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_STUDENT_LIST, 
                SystemConstants.TITLE_INACTIVATE_STUDENT);
    }

    @FXML
    private void handleRegisterProject() {
        try {
            boolean hasOrganizations = this.organizationDAO.hasOrganizations();
            int responsiblesCount = this.responsibleDAO
                    .getAllResponsibles().size();

            if (!hasOrganizations || responsiblesCount == RESET) {
                AlertUtility.showWarningAlert("Registro Bloqueado", 
                        "No se puede registrar un proyecto hasta que exista " 
                        + "al menos una organización vinculada activa y un " 
                        + "responsable técnico registrado en el servidor.");
                return;
            }

            NavigationUtility.navigateTo(this.logoutButton, 
                    ViewConstants.VIEW_PROJECT_REGISTRATION, 
                    SystemConstants.TITLE_REGISTER_PROJECT);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server pipeline communication failure " 
                    + "during project constraints check.", exception);
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "conectar con el servidor para validar requisitos.");
        }
    }

    @FXML
    private void handleAssignProject() {
        try {
            int studentsCount = this.studentDAO.getAllStudents().size();

            if (studentsCount == RESET) {
                AlertUtility.showWarningAlert("Asignación Bloqueada", 
                        "No existen alumnos registrados en el servidor " 
                        + "para realizar una asignación de proyecto.");
                return;
            }

            NavigationUtility.navigateTo(this.logoutButton, 
                    ViewConstants.VIEW_PROJECT_ASSIGNMENT, 
                    SystemConstants.TITLE_ASSIGN_PROJECT);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server pipeline communication failure " 
                    + "during assignment verification context.", exception);
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "conectar con el servidor para validar requisitos.");
        }
    }

    @FXML
    private void handleViewProjectsList() {
        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_PROJECT_LIST, 
                SystemConstants.TITLE_PROJECTS_MANAGEMENT);
    }

    @FXML
    private void handleRegisterOrganization() {
        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_LINKED_ORGANIZATION_REGISTRATION, 
                SystemConstants.TITLE_REGISTER_ORGANIZATION);
    }

    @FXML
    private void handleViewOrganizations() {
        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_LINKED_ORGANIZATION_LIST, 
                SystemConstants.TITLE_CONSULT_ORGANIZATIONS);
    }

    @FXML
    private void handleRegisterResponsible() {
        try {
            boolean hasOrganizations = this.organizationDAO.hasOrganizations();

            if (!hasOrganizations) {
                AlertUtility.showWarningAlert("Acceso Bloqueado", 
                        "No se puede registrar un responsable técnico hasta " 
                        + "que exista al menos una organización vinculada " 
                        + "activa en el sistema.");
                return;
            }

            NavigationUtility.navigateTo(this.logoutButton, 
                    ViewConstants.VIEW_RESPONSIBLE_REGISTRATION, 
                    SystemConstants.TITLE_REGISTER_RESPONSIBLE);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server pipeline communication failure " 
                    + "during responsible criteria verification.");
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "conectar con el servidor para validar requisitos.");
        }
    }

    @FXML
    private void handleConsultEE() {
        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_EDUCATIVE_EXPERIENCE_LIST, 
                SystemConstants.TITLE_MANAGE_EE);
    }

    @FXML
    private void handleViewResponsibles() {
        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_RESPONSIBLE_LIST, 
                SystemConstants.TITLE_RESPONSIBLES_LIST);
    }

    @FXML
    private void handleRegisterEducativeExperience() {
        try {
            int professorsCount = this.professorDAO.getAllProfessors().size();

            if (professorsCount == RESET) {
                AlertUtility.showWarningAlert("Acceso Denegado", 
                        "No se pueden registrar Experiencias Educativas " 
                        + "debido a que no existen profesores registrados " 
                        + "y activos actualmente en el servidor.");
                return;
            }

            NavigationUtility.navigateTo(this.logoutButton, 
                    ViewConstants.VIEW_REGISTER_EDUCATIVE_EXPERIENCE, 
                    SystemConstants.TITLE_REGISTER_RESPONSIBLE);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server pipeline communication failure " 
                    + "during educational experience criteria rules.", exception);
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "conectar con el servidor para validar requisitos.");
        }
    }

    @FXML
    private void handleAssignStudentToEE() {
        try {
            int studentsCount = this.studentDAO.getAllStudents().size();

            if (studentsCount == RESET) {
                AlertUtility.showWarningAlert("Asignación Bloqueada", 
                        "No se pueden asignar alumnos a una Experiencia " 
                        + "Educativa si no existe ningún alumno registrado.");
                return;
            }

            NavigationUtility.navigateTo(this.logoutButton, 
                    ViewConstants.VIEW_EDUCATIVE_EXPERIENCE_ASSIGNMENT, 
                    SystemConstants.TITLE_ASSIGN_STUDENT_EE);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server execution context error check " 
                    + "during student experience assignment rules.", exception);
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "conectar con el servidor para validar requisitos.");
        }
    }

    @FXML
    private void handleManageProjectActivities() {
        try {
            int projectsCount = this.projectDAO.getAllProjects().size();

            if (projectsCount == RESET) {
                AlertUtility.showWarningAlert("Gestión Bloqueada", 
                        "No es posible administrar actividades ya que no existe " 
                        + "ningún proyecto registrado en el servidor.");
                return;
            }

            NavigationUtility.navigateTo(this.logoutButton, 
                    ViewConstants.VIEW_PROJECT_ACTIVITIES_MANAGEMENT, 
                    SystemConstants.TITLE_ACTIVITIES_MANAGEMENT);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server infrastructure check rejection " 
                    + "during project activity pipeline lookup.", exception);
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "conectar con el servidor para validar requisitos.");
        }
    }
    
    public void executeRoleSwitchSequence() {
        LOGGER.log(Level.INFO, "Coordinating context switch: Transferring " 
                + "coordinator workspace layout into ProfessorMenuView.");
        
        NavigationUtility.navigateTo(this.switchPerformanceRoleButton, 
                ViewConstants.VIEW_PROFESSOR_MENU, 
                SystemConstants.TITLE_STAGE_ASSIGNED_EE);
    }

    private static class SwitchPerformanceRoleButtonEventHandler 
            implements EventHandler<ActionEvent> {
        private final CoordinatorMenuViewController controller;

        public SwitchPerformanceRoleButtonEventHandler(
                CoordinatorMenuViewController controller) {
            this.controller = controller;
        }

        @Override
        public void handle(javafx.event.ActionEvent event) {
            this.controller.executeRoleSwitchSequence();
        }
    }
}