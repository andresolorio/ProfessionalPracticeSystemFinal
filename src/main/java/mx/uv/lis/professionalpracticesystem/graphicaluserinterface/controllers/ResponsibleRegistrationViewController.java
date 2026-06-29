package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TextField;
import javafx.util.Callback;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.LinkedOrganizationDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ResponsibleProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.LinkedOrganizationDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ResponsibleProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.ILinkedOrganizationDAO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IResponsibleProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.SUCCESS;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;

/**
 *
 * @author andre
 * @author cinth
 */
public class ResponsibleRegistrationViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            ResponsibleRegistrationViewController.class.getName());
    private final ILinkedOrganizationDAO organizationDAO;
    private final IResponsibleProjectDAO responsibleProjectDAO;

    @FXML
    private TextField nameResponsibleTextField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField secondLastNameTextField;
    @FXML
    private TextField positionTextField;
    @FXML
    private ComboBox<LinkedOrganizationDTO> linkedOrganizationVinculatedComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    public ResponsibleRegistrationViewController() {
        this.organizationDAO = new LinkedOrganizationDAO();
        this.responsibleProjectDAO = new ResponsibleProjectDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourcebundle) {
        if (this.linkedOrganizationVinculatedComboBox != null) {
            this.configureComboBoxProperties();
            this.loadLinkedOrganizationsCatalog();
            
            this.saveButton.setOnAction(new SaveButtonEventHandler(this));
            this.cancelButton.setOnAction(new CancelButtonEventHandler(this));
        } else {
            LOGGER.log(Level.SEVERE, "Critical initialization error: The FXML " 
                    + "reference injection for ComboBox element is NULL.");
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "cargar el formulario de registro.");
        }
    }

    private void configureComboBoxProperties() {
        this.linkedOrganizationVinculatedComboBox.setCellFactory(
                new OrganizationCellFactory());
        this.linkedOrganizationVinculatedComboBox.setButtonCell(
                new OrganizationListCell());
    }

    public void loadLinkedOrganizationsCatalog() {
        try {
            LOGGER.log(Level.INFO, "Fetching active linked organizations " 
                    + "synchronously from the server repository layer.");
            
            List<LinkedOrganizationDTO> organizations = this.organizationDAO
                    .getAllLinkedOrganizations();
            
            ObservableList<LinkedOrganizationDTO> organizationObservableList = 
                    FXCollections.observableArrayList(organizations);
            this.linkedOrganizationVinculatedComboBox.setItems(
                    organizationObservableList);
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication data pipeline " 
                    + "failure retrieving linked organization catalogs.");
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudieron " 
                    + "cargar las organizaciones vinculadas desde el servidor.");
        }
    }

    public void executeStudentPersistenceFlow() {
        if (this.validateFields()) {
            ResponsibleProjectDTO responsible = new ResponsibleProjectDTO();
            responsible.setFirstName(this.nameResponsibleTextField.getText()
                    .trim());
            responsible.setLastName(this.firstNameTextField.getText().trim());
            responsible.setSecondLastName(this.secondLastNameTextField.getText()
                    .trim());
            responsible.setPosition(this.positionTextField.getText().trim());

            LinkedOrganizationDTO selectedOrg = this
                    .linkedOrganizationVinculatedComboBox.getValue();
            responsible.setIdLinkedOrganization(selectedOrg
                    .getIdLinkedOrganization());

            try {
                LOGGER.log(Level.INFO, "Executing synchronous statement write " 
                        + "transaction for new project responsible profile.");
                
                int updateResult = this.responsibleProjectDAO
                        .registerResponsible(responsible);

                if (updateResult > SUCCESS) {
                    AlertUtility.showInformationAlert("Éxito", 
                            "Responsable registrado correctamente.");
                    this.clearFormFields();
                    this.executeCancelSequence();
                }
            } catch (DatabaseSystemException exception) {
                LOGGER.log(Level.SEVERE, "Persistence transaction write layout " 
                        + "failure inside project responsible scope.");
                
                AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                        + "completar el registro del responsable en el servidor.");
            }
        }
    }
    
    @FXML
    private void handleSave(javafx.event.ActionEvent event) {
        this.executeStudentPersistenceFlow();
    }

    @FXML
    private void handleCancel(javafx.event.ActionEvent event) {
        this.executeCancelSequence();
    }

    private boolean validateFields() {
        boolean isValid = true;
        if (this.nameResponsibleTextField.getText().trim().isEmpty()
                || this.firstNameTextField.getText().trim().isEmpty()
                || this.positionTextField.getText().trim().isEmpty()
                || this.linkedOrganizationVinculatedComboBox.getValue() == null) {

            AlertUtility.showWarningAlert("Campos vacíos", "Por favor llena " 
                    + "los campos obligatorios marcados con (*).");
            isValid = false;
        }

        if (isValid && !Validator.isValidName(this.nameResponsibleTextField
                .getText().trim())) {
            AlertUtility.showWarningAlert("Nombre Inválido", "El nombre solo " 
                    + "debe contener letras y tener al menos 2 caracteres.");
            isValid = false;
        }

        if (isValid && !Validator.isValidName(this.firstNameTextField.getText()
                .trim())) {
            AlertUtility.showWarningAlert("Apellido Inválido", "El primer " 
                    + "apellido solo debe contener letras.");
            isValid = false;
        }

        String secondLastName = this.secondLastNameTextField.getText().trim();
        if (isValid && (!secondLastName.isEmpty() && !Validator
                .isValidName(secondLastName))) {
            AlertUtility.showWarningAlert("Apellido Inválido", "El segundo " 
                    + "apellido solo debe contener letras.");
            isValid = false;
        }

        if (isValid && !Validator.isValidName(this.positionTextField.getText()
                .trim())) {
            AlertUtility.showWarningAlert("Cargo Inválido", "El cargo solo " 
                    + "debe contener letras y espacios.");
            isValid = false;
        }
        return isValid;
    }

    public void executeCancelSequence() {
        LOGGER.log(Level.INFO, "Aborting project responsible registration. " 
                + "Routing workspace screen stage back to CoordinatorMenuView.");
        
        NavigationUtility.navigateTo(this.cancelButton, 
                ViewConstants.VIEW_COORDINATOR_MENU, 
                SystemConstants.TITLE_REGISTER_RESPONSIBLE);
    }

    private void clearFormFields() {
        this.nameResponsibleTextField.clear();
        this.firstNameTextField.clear();
        this.secondLastNameTextField.clear();
        this.positionTextField.clear();
        this.linkedOrganizationVinculatedComboBox.getSelectionModel()
                .clearSelection();
    }

    private static class OrganizationCellFactory implements Callback<ListView<
            LinkedOrganizationDTO>, ListCell<LinkedOrganizationDTO>> {

        @Override
        public ListCell<LinkedOrganizationDTO> call(
                ListView<LinkedOrganizationDTO> parentListView) {
            return new OrganizationListCell();
        }
    }

    private static class OrganizationListCell 
            extends ListCell<LinkedOrganizationDTO> {

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

    private static class SaveButtonEventHandler 
            implements EventHandler<ActionEvent> {
        private final ResponsibleRegistrationViewController controller;

        public SaveButtonEventHandler(
                ResponsibleRegistrationViewController controller) {
            this.controller = controller;
        }

        @Override
        public void handle(ActionEvent event) {
            this.controller.executeStudentPersistenceFlow();
        }
    }

    private static class CancelButtonEventHandler 
            implements EventHandler<ActionEvent> {
        private final ResponsibleRegistrationViewController controller;

        public CancelButtonEventHandler(
                ResponsibleRegistrationViewController controller) {
            this.controller = controller;
        }

        @Override
        public void handle(ActionEvent event) {
            this.controller.executeCancelSequence();
        }
    }
}