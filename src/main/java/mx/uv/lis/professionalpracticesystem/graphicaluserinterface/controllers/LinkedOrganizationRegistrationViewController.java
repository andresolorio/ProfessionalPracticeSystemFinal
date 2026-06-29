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
import javafx.scene.control.TextField;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.CityDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.LinkedOrganizationDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.LinkedOrganizationDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;

/**
 *
 * @author cinth
 * @author andre
 */
public class LinkedOrganizationRegistrationViewController 
        implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            LinkedOrganizationRegistrationViewController.class.getName());

    @FXML
    private TextField nameLinkedOrganizationTextField;
    @FXML
    private TextField directionTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField mailTextField;
    @FXML
    private TextField directUserTextField;
    @FXML
    private TextField indirectUserTextField;
    @FXML
    private ComboBox<String> sectorComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<String> cityComboBox;

    private final LinkedOrganizationDAO organizationDAO;
    private final CityDAO cityDAO;
    private ObservableList<String> operationalCitiesList;

    public LinkedOrganizationRegistrationViewController() {
        this.organizationDAO = new LinkedOrganizationDAO();
        this.cityDAO = new CityDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourcebundle) {
        this.sectorComboBox.setItems(FXCollections.observableArrayList(
                SystemConstants.SECTOR_PUBLIC, SystemConstants.SECTOR_PRIVATE));

        this.saveButton.setOnAction(new SaveButtonEventHandler(this));
        this.cancelButton.setOnAction(new CancelButtonEventHandler(this));

        this.loadCitiesFromDatabase();

        this.cityComboBox.setEditable(true);
        this.cityComboBox.focusedProperty().addListener(
                new CityFocusListener(this));
    }

    private void loadCitiesFromDatabase() {
        try {
            LOGGER.log(Level.INFO, "Requesting available cities catalog " 
                    + "synchronously from the server database context mapping.");
            
            List<String> cities = this.cityDAO.getAllAvailableCities();
            this.operationalCitiesList = FXCollections
                    .observableArrayList(cities);
            this.cityComboBox.setItems(this.operationalCitiesList);
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server write transaction execution " 
                    + "failure retrieving cities entities catalogs.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudieron " 
                    + "cargar las ciudades desde el servidor central de datos.");
        }
    }

    public void validateCityFocus(boolean isFocused) {
        if (!isFocused) {
            String enteredText = this.cityComboBox.getEditor().getText();
            if (enteredText != null && !enteredText.trim().isEmpty()) {
                String normalizedText = this.capitalizeCityName(enteredText);
                String matchedCity = null;

                for (String city : this.operationalCitiesList) {
                    if (city.equalsIgnoreCase(normalizedText)) {
                        matchedCity = city;
                        break;
                    }
                }

                if (matchedCity != null) {
                    this.cityComboBox.getSelectionModel().select(matchedCity);
                    this.cityComboBox.getEditor().setText(matchedCity);
                } else {
                    this.cityComboBox.getSelectionModel().select(normalizedText);
                    this.cityComboBox.getEditor().setText(normalizedText);
                }
            } else {
                this.cityComboBox.getEditor().clear();
                this.cityComboBox.getSelectionModel().clearSelection();
            }
        }
    }

    private String capitalizeCityName(String input) {
        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder capitalized = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                           .append(word.substring(1))
                           .append(" ");
            }
        }
        return capitalized.toString().trim();
    }

    @FXML
    public void handleSave() {
        if (this.validateFields()) {
            LinkedOrganizationDTO organization = new LinkedOrganizationDTO();
            organization.setLinkedOrganizationName(
                    this.nameLinkedOrganizationTextField.getText().trim());
            organization.setAddress(this.directionTextField.getText().trim());
            organization.setCity(this.cityComboBox.getEditor().getText().trim());
            organization.setPhoneNumber(this.phoneTextField.getText().trim());
            organization.setEmail(this.mailTextField.getText().trim());
            organization.setSector(this.sectorComboBox.getValue());

            String directStr = this.directUserTextField.getText().trim();
            int directUsers = 0;
            if (!directStr.isEmpty()) {
                directUsers = Integer.parseInt(directStr);
            }
            organization.setDirectUsers(directUsers);

            String indirectStr = this.indirectUserTextField.getText().trim();
            int indirectUsers = 0;
            if (!indirectStr.isEmpty()) {
                indirectUsers = Integer.parseInt(indirectStr);
            }
            organization.setIndirectUsers(indirectUsers);

            try {
                LOGGER.log(Level.INFO, "Initiating synchronous persistence " 
                        + "transaction for new linked organization register.");
                
                int rowsAffected = this.organizationDAO
                        .saveLinkedOrganization(organization);
                
                if (rowsAffected > SystemConstants.RESET) {
                    AlertUtility.showInformationAlert("Éxito", 
                            "Organización registrada correctamente.");
                    this.handleCancel();
                }
            } catch (DatabaseSystemException exception) {
                LOGGER.log(Level.SEVERE, "Server write transaction failure " 
                        + "registering linked organization profile.");
                
                AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                        + "completar el registro en el servidor central.");
            }
        }
    }

    private boolean validateFields() {
        boolean isValid = true;
        if (this.nameLinkedOrganizationTextField.getText().trim().isEmpty()
                || this.directionTextField.getText().trim().isEmpty()
                || this.cityComboBox.getEditor().getText().trim().isEmpty()
                || this.mailTextField.getText().trim().isEmpty()
                || this.phoneTextField.getText().trim().isEmpty()
                || this.sectorComboBox.getValue() == null) {

            AlertUtility.showWarningAlert("Campos Obligatorios", "Por favor, " 
                    + "llene todos los campos marcados con (*).");
            isValid = false;
        }

        if (isValid && !Validator.isValidName(this
                .nameLinkedOrganizationTextField.getText().trim())) {
            AlertUtility.showWarningAlert("Nombre inválido", "El nombre " 
                    + "debe contener al menos 2 letras.");
            isValid = false;
        }

        if (isValid && !Validator.isValidEmail(this.mailTextField.getText()
                .trim())) {
            AlertUtility.showWarningAlert("Correo Inválido", "El formato " 
                    + "del correo electrónico no es correcto.");
            isValid = false;
        }

        if (isValid && !Validator.isValidPhoneNumber(this.phoneTextField
                .getText().trim())) {
            AlertUtility.showWarningAlert("Teléfono Inválido", "El teléfono " 
                    + "debe contener exactamente 10 dígitos numéricos.");
            isValid = false;
        }

        if (isValid && (!this.directUserTextField.getText().trim().isEmpty() 
                && !this.directUserTextField.getText().trim().matches("[0-9]+"))) {
            AlertUtility.showWarningAlert("Dato Inválido", "El número de " 
                    + "usuarios directos debe ser un valor numérico.");
            isValid = false;
        }

        if (isValid && (!this.indirectUserTextField.getText().trim().isEmpty() 
                && !this.indirectUserTextField.getText().trim().matches("[0-9]+"))) {
            AlertUtility.showWarningAlert("Dato Inválido", "El número de " 
                    + "usuarios indirectos debe ser un valor numérico.");
            isValid = false;
        }
        return isValid;
    }

    @FXML
    public void handleCancel() {
        LOGGER.log(Level.INFO, "Aborting organization register session. " 
                + "Routing workspace screen back to CoordinatorMenuView.");

        NavigationUtility.navigateTo(this.cancelButton, 
                ViewConstants.VIEW_COORDINATOR_MENU, 
                SystemConstants.TITLE_COORDINATOR_MENU);
    }

    private static class CityFocusListener implements ChangeListener<Boolean> {
        private final LinkedOrganizationRegistrationViewController controller;

        public CityFocusListener(
                LinkedOrganizationRegistrationViewController controller) {
            this.controller = controller;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, 
                Boolean oldVal, Boolean newVal) {
            this.controller.validateCityFocus(newVal);
        }
    }

    private static class SaveButtonEventHandler 
            implements EventHandler<ActionEvent> {
        private final LinkedOrganizationRegistrationViewController controller;

        public SaveButtonEventHandler(
                LinkedOrganizationRegistrationViewController controller) {
            this.controller = controller;
        }

        @Override
        public void handle(ActionEvent event) {
            this.controller.handleSave();
        }
    }

    private static class CancelButtonEventHandler 
            implements EventHandler<ActionEvent> {
        private final LinkedOrganizationRegistrationViewController controller;

        public CancelButtonEventHandler(
                LinkedOrganizationRegistrationViewController controller) {
            this.controller = controller;
        }

        @Override
        public void handle(ActionEvent event) {
            this.controller.handleCancel();
        }
    }
}