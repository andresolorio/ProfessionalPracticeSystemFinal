package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.ProjectController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.LinkedOrganizationDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ResponsibleProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.LinkedOrganizationDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ResponsibleProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_PROJECT_DURATION_LABEL_LENGTH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_PROJECT_NAME_LENGTH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_PROJECT_OBJECTIVE_LENGTH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATUS_ACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.SUCCESS;

/**
 * 
 * @author andre
 * @author cinth
 */
public class ProjectRegistrationViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            ProjectRegistrationViewController.class.getName());

    @FXML
    private TextField nameProjectTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField durationTextField;
    @FXML
    private TextField statusTextField;
    @FXML
    private TextField vacanciesTextField;
    @FXML
    private TextArea methodologyTextArea;
    @FXML
    private TextArea generalObjectiveTextArea;
    @FXML
    private TextArea immediateObjectiveTextArea;
    @FXML
    private TextArea mediatedObjectiveTextArea;
    @FXML
    private TextArea responsibilitiesTextArea;
    @FXML
    private TextArea resourcesTextArea;
    @FXML
    private ComboBox<LinkedOrganizationDTO> 
            linkedOrganizationVinculatedComboBox;
    @FXML
    private ComboBox<ResponsibleProjectDTO> technicalResponsibleComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final LinkedOrganizationDAO organizationDAO;
    private final ResponsibleProjectDAO responsibleDAO;
    private final ProjectController projectController;
    private ProjectDTO projectToUpdate;

    public ProjectRegistrationViewController() {
        this.organizationDAO = new LinkedOrganizationDAO();
        this.responsibleDAO = new ResponsibleProjectDAO();
        this.projectController = new ProjectController();
        this.projectToUpdate = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourcebundle) {
        LOGGER.log(Level.INFO, "Initializing project registration form " 
                + "and purging context constraints cache.");
        
        this.projectToUpdate = null;
        this.saveButton.setText("Guardar");
        
        this.statusTextField.setText(STATUS_ACTIVE);
        this.configureComboBoxProperties();
        this.loadLinkedOrganizations();

        this.saveButton.setOnAction(new SaveButtonEventHandler(this));
        this.cancelButton.setOnAction(new CancelButtonEventHandler(this));

        this.linkedOrganizationVinculatedComboBox.getSelectionModel()
                .selectedItemProperty().addListener(
                new OrganizationSelectionChangeListener(this));
    }

    public void loadProjectData(ProjectDTO project) {
        this.projectToUpdate = project;
        this.nameProjectTextField.setText(project.getProjectName());
        this.descriptionTextField.setText(project.getDescription());
        this.methodologyTextArea.setText(project.getMethodology());
        this.generalObjectiveTextArea.setText(project.getGeneralObjective());
        this.immediateObjectiveTextArea.setText(project
                .getImmediateObjective());
        this.mediatedObjectiveTextArea.setText(project.getMediatedObjective());
        this.durationTextField.setText(project.getDuration());
        this.responsibilitiesTextArea.setText(project.getResponsibilities());
        this.resourcesTextArea.setText(project.getResources());
        this.vacanciesTextField.setText(String.valueOf(project
                .getTotalVacancies()));
        this.statusTextField.setText(project.getStatus());
        
        this.saveButton.setText("Actualizar Proyecto");
    }

    private void configureComboBoxProperties() {
        this.linkedOrganizationVinculatedComboBox.setCellFactory(
                new OrganizationCellFactory());
        this.linkedOrganizationVinculatedComboBox.setButtonCell(
                new OrganizationListCell());    
        this.technicalResponsibleComboBox.setCellFactory(
                new ResponsibleCellFactory());
        this.technicalResponsibleComboBox.setButtonCell(
                new ResponsibleListCell());
    }

    public void loadLinkedOrganizations() {
        try {
            LOGGER.log(Level.INFO, "Fetching linked organizations list " 
                    + "synchronously from server repository.");
            
            List<LinkedOrganizationDTO> organizations = this.organizationDAO
                    .getAllLinkedOrganizations();
            
            ObservableList<LinkedOrganizationDTO> observableList = 
                    FXCollections.observableArrayList(organizations);
            this.linkedOrganizationVinculatedComboBox.setItems(observableList);
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server infrastructure network layer " 
                    + "failure: Failed to fetch linked organizations", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se " 
                    + "pudieron cargar las organizaciones desde el servidor.");
        }
    }

    private void loadResponsiblesByOrganization(int idOrganization) {
        try {
            LOGGER.log(Level.INFO, "Fetching technical responsibles mapping " 
                    + "synchronously from core server logic layers.");
            
            this.technicalResponsibleComboBox.getSelectionModel().clearSelection();
            
            List<ResponsibleProjectDTO> responsibles = this.responsibleDAO
                    .getResponsiblesByOrganization(idOrganization);
            
            ObservableList<ResponsibleProjectDTO> observableList = 
                    FXCollections.observableArrayList(responsibles);
            this.technicalResponsibleComboBox.setItems(observableList);
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server data pipeline exception fetching " 
                    + "responsibles for organization ID: " + idOrganization);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se " 
                    + "pudieron recuperar los responsables desde el servidor.");
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!this.validateFields()) {
            return;
        }

        LinkedOrganizationDTO selectedOrg = this
                .linkedOrganizationVinculatedComboBox.getValue();
        ResponsibleProjectDTO selectedResponsible = this
                .technicalResponsibleComboBox.getValue();

        ProjectDTO project = new ProjectDTO();
        project.setProjectName(this.nameProjectTextField.getText().trim());
        project.setDescription(this.descriptionTextField.getText().trim());
        project.setMethodology(this.methodologyTextArea.getText().trim());
        project.setGeneralObjective(this.generalObjectiveTextArea.getText()
                .trim());
        project.setImmediateObjective(this.immediateObjectiveTextArea.getText()
                .trim());
        project.setMediatedObjective(this.mediatedObjectiveTextArea.getText()
                .trim());
        project.setDuration(this.durationTextField.getText().trim());
        project.setResponsibilities(this.responsibilitiesTextArea.getText()
                .trim());
        project.setResources(this.resourcesTextArea.getText().trim());
        project.setStatus(STATUS_ACTIVE);
        project.setIdLinkedOrganization(selectedOrg.getIdLinkedOrganization());
        project.setTotalVacancies(Integer.parseInt(this.vacanciesTextField
                .getText().trim()));
        project.setIdTechnicalResponsible(selectedResponsible
                .getIdResponsible());

        try {
            this.saveButton.setDisable(true);
            int result;

            if (this.projectToUpdate == null) {
                LOGGER.log(Level.INFO, "Executing synchronous persistence " 
                        + "pipeline for new project insertion.");
                result = this.projectController.registerProject(project);
            } else {
                LOGGER.log(Level.INFO, "Executing synchronous update statement " 
                        + "for existing dataset context mapping.");
                project.setIdProject(this.projectToUpdate.getIdProject());
                result = this.projectController.updateProject(project);
            }

            if (result > SUCCESS) {
                AlertUtility.showInformationAlert("Éxito", "La información " 
                        + "del proyecto se guardó con éxito en el servidor.");
                this.handleCancel(null);
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Persistence transaction failure processing " 
                    + "project entry statement scope registry.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "completar la transacción en el servidor central.");
        } finally {
            this.saveButton.setDisable(false);
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (this.nameProjectTextField.getText().trim().isEmpty()
                || this.descriptionTextField.getText().trim().isEmpty()
                || this.generalObjectiveTextArea.getText().trim().isEmpty()
                || this.durationTextField.getText().trim().isEmpty()
                || this.vacanciesTextField.getText().trim().isEmpty()
                || this.linkedOrganizationVinculatedComboBox.getValue() == null
                || this.technicalResponsibleComboBox.getValue() == null) {

            AlertUtility.showWarningAlert("Campos Obligatorios Vacíos", 
                    "Por favor llene todos los campos marcados con (*).");
            return false;
        }

        try {
            int vacanciesValue = Integer.parseInt(this.vacanciesTextField
                    .getText().trim());
            if (vacanciesValue <= 0) {
                AlertUtility.showWarningAlert("Lugares Inválidos", "El cupo " 
                        + "de estudiantes debe ser un entero positivo.");
                return false;
            }
        } catch (NumberFormatException exception) {
            AlertUtility.showWarningAlert("Formato Inválido", "Por favor " 
                    + "introduzca un valor numérico entero en las vacantes.");
            return false;
        }

        if (this.nameProjectTextField.getText().trim().length() 
                > MAX_PROJECT_NAME_LENGTH) {
            AlertUtility.showWarningAlert("Nombre Largo", "El nombre no " 
                    + "debe exceder los " + MAX_PROJECT_NAME_LENGTH + " carac.");
            isValid = false;
        }

        if (isValid && (this.generalObjectiveTextArea.getText().trim().length() 
                > MAX_PROJECT_OBJECTIVE_LENGTH
                || this.immediateObjectiveTextArea.getText().trim().length() 
                > MAX_PROJECT_OBJECTIVE_LENGTH
                || this.mediatedObjectiveTextArea.getText().trim().length() 
                > MAX_PROJECT_OBJECTIVE_LENGTH)) {

            AlertUtility.showWarningAlert("Texto Largo", "Los objetivos no " 
                    + "deben exceder los " + MAX_PROJECT_OBJECTIVE_LENGTH 
                    + " caracteres.");
            isValid = false;
        }

        if (isValid && this.durationTextField.getText().trim().length() 
                > MAX_PROJECT_DURATION_LABEL_LENGTH) {
            AlertUtility.showWarningAlert("Duración Inválida", "La duración " 
                    + "excede el límite (máx. " 
                    + MAX_PROJECT_DURATION_LABEL_LENGTH + ").");
            isValid = false;
        }

        return isValid;
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting current operational workflow. " 
                + "Re-routing view stage back to projects list view context.");
        
        NavigationUtility.navigateTo(this.cancelButton, 
                ViewConstants.VIEW_PROJECT_LIST, 
                SystemConstants.TITLE_PROJECTS_MANAGEMENT);
    }

    private static class OrganizationListCell extends 
            ListCell<LinkedOrganizationDTO> {
        @Override
        protected void updateItem(LinkedOrganizationDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                this.setText(null);
            } else {
                this.setText(item.getLinkedOrganizationName());
            }
        }
    }

    private static class ResponsibleListCell extends 
            ListCell<ResponsibleProjectDTO> {
        @Override
        protected void updateItem(ResponsibleProjectDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                this.setText(null);
            } else {
                String fullName = item.getFirstName() + " " + item.getLastName();
                if (item.getSecondLastName() != null && !item
                        .getSecondLastName().trim().isEmpty()) {
                    fullName += " " + item.getSecondLastName();
                }
                this.setText(fullName);
            }
        }
    }
    
    private static class OrganizationSelectionChangeListener 
            implements ChangeListener<LinkedOrganizationDTO> {

        private final ProjectRegistrationViewController controller;

        public OrganizationSelectionChangeListener(
                ProjectRegistrationViewController controller) {
            this.controller = controller;
        }

        @Override
        public void changed(ObservableValue<? extends LinkedOrganizationDTO> observable, 
                LinkedOrganizationDTO oldValue, LinkedOrganizationDTO newValue) {
            
            if (newValue != null) {
                this.controller.loadResponsiblesByOrganization(newValue
                        .getIdLinkedOrganization());
            } else {
                this.controller.technicalResponsibleComboBox.setItems(
                        FXCollections.emptyObservableList());
            }
        }
    }
    
    private static class OrganizationCellFactory 
            implements Callback<ListView<LinkedOrganizationDTO>, 
            ListCell<LinkedOrganizationDTO>> {
        @Override
        public ListCell<LinkedOrganizationDTO> call(
                ListView<LinkedOrganizationDTO> projectListView) {
            return new OrganizationListCell();
        }
    }

    private static class ResponsibleCellFactory 
            implements Callback<ListView<ResponsibleProjectDTO>, 
            ListCell<ResponsibleProjectDTO>> {
        @Override
        public ListCell<ResponsibleProjectDTO> call(
                ListView<ResponsibleProjectDTO> projectListView) {
            return new ResponsibleListCell();
        }
    }
    
    private static class SaveButtonEventHandler 
            implements EventHandler<ActionEvent> {
        private final ProjectRegistrationViewController controller;

        public SaveButtonEventHandler(
                ProjectRegistrationViewController controller) {
            this.controller = controller;
        }

        @Override
        public void handle(ActionEvent event) {
            this.controller.handleSave(event);
        }
    }

    private static class CancelButtonEventHandler 
            implements EventHandler<ActionEvent> {
        private final ProjectRegistrationViewController controller;

        public CancelButtonEventHandler(
                ProjectRegistrationViewController controller) {
            this.controller = controller;
        }

        @Override
        public void handle(ActionEvent event) {
            this.controller.handleCancel(event);
        }
    }
}