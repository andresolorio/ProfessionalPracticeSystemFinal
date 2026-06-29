package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.ProjectController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.SUCCESS;

/**
 *
 * @author andre
 * @author cinth
 */
public class ProjectListViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            ProjectListViewController.class.getName());

    @FXML
    private TableView<ProjectDTO> projectsTable;
    @FXML
    private TableColumn<ProjectDTO, String> nameTableColumn;
    @FXML
    private TableColumn<ProjectDTO, String> organizationTableColumn;   
    @FXML
    private TableColumn<ProjectDTO, String> responsibleTableColumn;   
    @FXML
    private Button cancelButton;
    @FXML
    private Button updateButton;

    private final ProjectController projectController = new ProjectController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.nameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("projectName"));
        this.organizationTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("organizationName"));     
        this.responsibleTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("technicalResponsibleName"));
        
        this.loadProjects();
    }

    private void loadProjects() {
        try {
            LOGGER.log(Level.INFO, "Executing synchronous data retrieval " 
                    + "for available projects dataset from the server.");

            List<ProjectDTO> projects = this.projectController
                    .getAvailableProjects();
            this.projectsTable.setItems(FXCollections
                    .observableArrayList(projects));
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Infrastructure connectivity failure " 
                    + "while pulling available projects dataset.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "conectar con el servidor para cargar los proyectos.");
        }
    }

    @FXML
    private void handleUpdateProject(ActionEvent event) {
        ProjectDTO selectedProject = this.projectsTable.getSelectionModel()
                .getSelectedItem();

        if (selectedProject == null) {
            AlertUtility.showWarningAlert("Selección Requerida", 
                    "Por favor, selecciona un proyecto para actualizar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(this.getClass()
                    .getResource(ViewConstants.VIEW_PROJECT_REGISTRATION));
            Parent root = (Parent) loader.load();
            
            ProjectRegistrationViewController registrationController = loader
                    .getController();
            registrationController.loadProjectData(selectedProject);
            
            Stage stage = (Stage) this.updateButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Proyecto - Sistema de Prácticas");
            stage.show();
            
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Critical failure loading project " 
                    + "registration view layout content for editing.", 
                    exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "desplegar el formulario de edición de proyectos.");
        }
    }

    @FXML
    private void handleDeactivateProject(ActionEvent event) {
        ProjectDTO selectedProject = this.projectsTable.getSelectionModel()
                .getSelectedItem();

        if (selectedProject == null) {
            AlertUtility.showWarningAlert("Selección Requerida", 
                    "Por favor, selecciona un proyecto de la tabla.");
            return;
        }

        try {
            boolean isDeactivatable = this.projectController
                    .isProjectDeactivatable(selectedProject.getIdProject());

            if (isDeactivatable) {
                String confirmMessage = "¿Deseas inactivar el proyecto: " 
                        + selectedProject.getProjectName() + "?";
                
                if (AlertUtility.showConfirmationAlert("Confirmar Acción", 
                        confirmMessage)) {
                    
                    int result = this.projectController
                            .deactivateProject(selectedProject.getIdProject());

                    if (result > SUCCESS) {
                        AlertUtility.showInformationAlert("Éxito", 
                                "El proyecto ha sido inactivado correctamente.");
                        this.loadProjects();
                    }
                }
            } else {
                AlertUtility.showErrorAlert("Acción Denegada", "No se puede " 
                        + "inactivar: El proyecto tiene alumnos asignados.");
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Transactional persistence error " 
                    + "execution phase during project status update.", 
                    exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "Ocurrió un " 
                    + "error al conectar con el servidor central.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting projects overview session. " 
                + "Routing screen context back to coordinator menu.");

        NavigationUtility.navigateTo(this.cancelButton, 
                ViewConstants.VIEW_COORDINATOR_MENU, 
                SystemConstants.TITLE_COORDINATOR_MENU);
    }
}