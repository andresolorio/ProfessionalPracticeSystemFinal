package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.UserController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAXIMUM_PROJECT_OPTIONS;

/**
 *
 * @author andre
 * @author cinth
 */
public class ProjectSelectionViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            ProjectSelectionViewController.class.getName());

    @FXML
    private TableView<ProjectDTO> availableProjectsTable;
    @FXML
    private TableColumn<ProjectDTO, String> nameTableColumn;
    @FXML
    private TableColumn<ProjectDTO, String> organizationTableColumn;
    @FXML
    private ListView<ProjectDTO> selectedProjectsListView;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;

    private String currentStudentEnrollment;
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final ObservableList<ProjectDTO> selectedProjects = 
            FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.currentStudentEnrollment = UserController.currentStudentEnrollment;

        if (this.currentStudentEnrollment == null) {
            AlertUtility.showErrorAlert("Error de Acceso", 
                    "No se encontró una sesión de alumno activa.");
            return;
        }
        this.setupTable();
        this.selectedProjectsListView.setItems(this.selectedProjects);
        this.selectedProjectsListView.setCellFactory(
                new SelectedProjectCellFactory());

        this.checkExistingRequests();
        this.loadAvailableProjects();
    }

    private void checkExistingRequests() {
        try {
            LOGGER.log(Level.INFO, "Checking existing project selection " 
                    + "requests synchronously from server for enrollment: {0}", 
                    this.currentStudentEnrollment);

            boolean hasRequests = this.projectDAO
                    .isStudentAlreadyRegisteredInRequest(
                    this.currentStudentEnrollment);

            if (hasRequests) {
                List<ProjectDTO> savedProjects = this.projectDAO
                        .getRequestedProjectsByStudent(
                        this.currentStudentEnrollment);
                
                this.handleCheckRequestsSuccess(hasRequests, savedProjects);
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server pipeline query failure " 
                    + "verifying existing project selection criteria.", 
                    exception);
            AlertUtility.showErrorAlert("Error de Validación", "No se pudo " 
                    + "verificar el estado de tus solicitudes.");
        }
    }

    public void handleCheckRequestsSuccess(boolean hasRequests, 
            List<ProjectDTO> savedProjects) {
        if (hasRequests) {
            AlertUtility.showInformationAlert("Solicitud enviada", 
                    "Ya has registrado tus opciones anteriormente.");

            this.selectedProjects.setAll(savedProjects);

            this.availableProjectsTable.setDisable(true);
            this.selectedProjectsListView.setDisable(true);
            this.disableActionButtons();
        }
    }

    private void disableActionButtons() {
        if (this.addButton != null) {
            this.addButton.setDisable(true);
        }
        if (this.saveButton != null) {
            this.saveButton.setDisable(true);
        }
    }

    private void setupTable() {
        this.nameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("projectName"));
        this.organizationTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("organizationName"));
    }

    private void loadAvailableProjects() {
        try {
            LOGGER.log(Level.INFO, "Fetching available vacancy projects " 
                    + "synchronously from core server repository layers.");

            List<ProjectDTO> availableProjects = this.projectDAO
                    .getAllAvailableProjects();
            
            this.availableProjectsTable.setItems(FXCollections
                    .observableArrayList(availableProjects));

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Technical server data error loading " 
                    + "available vacancy projects collection.", exception);
            AlertUtility.showErrorAlert("Error de Carga", "No se pudo " 
                    + "recuperar el catálogo de proyectos del servidor.");
        }
    }

    @FXML
    private void handleAddSelection(ActionEvent event) {
        ProjectDTO selected = this.availableProjectsTable.getSelectionModel()
                .getSelectedItem();
        if (selected != null) {
            if (this.selectedProjects.size() >= MAXIMUM_PROJECT_OPTIONS) {
                AlertUtility.showWarningAlert("Límite alcanzado", 
                        "Solo puedes seleccionar un máximo de " 
                        + MAXIMUM_PROJECT_OPTIONS + " opciones.");
            } else {
                if (this.selectedProjects.contains(selected)) {
                    AlertUtility.showWarningAlert("Proyecto duplicado", 
                            "Ya has añadido este proyecto.");
                } else {
                    this.selectedProjects.add(selected);
                }
            }
        }
    }

    @FXML
    private void handleRemoveSelection(ActionEvent event) {
        ProjectDTO selected = this.selectedProjectsListView.getSelectionModel()
                .getSelectedItem();
        if (selected != null) {
            this.selectedProjects.remove(selected);
        }
    }

    @FXML
    private void handleSaveSelection(ActionEvent event) {
        if (this.selectedProjects.size() != MAXIMUM_PROJECT_OPTIONS) {
            AlertUtility.showWarningAlert("Selección Incompleta", 
                    "Debes seleccionar exactamente " + MAXIMUM_PROJECT_OPTIONS 
                    + " proyectos.");
            return;
        }

        try {
            this.saveButton.setDisable(true);
            LOGGER.log(Level.INFO, "Submitting project selection metadata " 
                    + "synchronously to the server database architecture.");

            this.projectDAO.saveProjectRequests(this.currentStudentEnrollment, 
                    new ArrayList<>(this.selectedProjects));

            AlertUtility.showInformationAlert("Éxito", 
                    "Tus opciones han sido enviadas correctamente.");
            this.handleCancel(null);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Transactional persistence execution " 
                    + "failure sending project choices metadata.", exception);
            AlertUtility.showErrorAlert("Error de Registro", 
                    exception.getMessage());
        } finally {
            this.saveButton.setDisable(false);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting project choices view session. " 
                + "Routing client context back to StudentMenuView screen.");

        NavigationUtility.navigateTo(this.cancelButton, 
                ViewConstants.VIEW_STUDENT_MENU, 
                SystemConstants.TITLE_MENU_STUDENT);
    }

    private static class SelectedProjectCellFactory 
            implements Callback<ListView<ProjectDTO>, ListCell<ProjectDTO>> {

        @Override
        public ListCell<ProjectDTO> call(ListView<ProjectDTO> listView) {
            return new SelectedProjectListCell();
        }
    }

    private static class SelectedProjectListCell extends ListCell<ProjectDTO> {

        @Override
        protected void updateItem(ProjectDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                this.setText(null);
            } else {
                this.setText(item.getProjectName());
            }
        }
    }
}