package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.EducativeExperienceDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EducativeExperienceDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IEducativeExperienceDAO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;

/**
 *
 * @author cinth
 * @author andre
 */
public class RegisterEducativeExperienceViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            RegisterEducativeExperienceViewController.class.getName());
    private final IProfessorDAO professorDAO;
    private final IEducativeExperienceDAO educativeExperienceDAO;

    @FXML
    private TextField educativeExperienceNameTextField;
    @FXML
    private TextField nrcTextField;
    @FXML
    private ComboBox<String> sectionComboBox;
    @FXML
    private ComboBox<ProfessorDTO> professorComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    public RegisterEducativeExperienceViewController() {
        this.professorDAO = new ProfessorDAO();
        this.educativeExperienceDAO = new EducativeExperienceDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourcebundle) {
        this.sectionComboBox.setItems(FXCollections.observableArrayList(
                "Matutino", "Vespertino"));
        this.configureComboBoxProperties();
        this.loadProfessorsCatalog();
    }

    private void configureComboBoxProperties() {
        this.professorComboBox.setCellFactory(new ProfessorCellFactory());
        this.professorComboBox.setButtonCell(new ProfessorListCell());
    }

    public void loadProfessorsCatalog() {
        try {
            LOGGER.log(Level.INFO, "Fetching active professors list "
                    + "synchronously from database layers.");
            
            List<ProfessorDTO> professors = this.professorDAO.getAllProfessors();
            this.professorComboBox.setItems(FXCollections
                    .observableArrayList(professors));
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server core pipeline failure "
                    + "retrieving active professors data catalogs.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se " 
                    + "pudieron cargar los profesores desde el servidor central.");
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        String name = this.educativeExperienceNameTextField.getText().trim();
        String nrcStr = this.nrcTextField.getText().trim();
        String section = this.sectionComboBox.getValue();
        ProfessorDTO professor = this.professorComboBox.getValue();

        if (this.validateFields(name, nrcStr, section, professor)) {
            EducativeExperienceDTO educativeExperience = 
                    new EducativeExperienceDTO();
            educativeExperience.setEducativeExperienceName(name);
            educativeExperience.setNrc(Integer.parseInt(nrcStr));
            educativeExperience.setSection(section);
            educativeExperience.setProfessorStaffNumber(professor
                    .getProfessorStaffNumber());

            try {
                LOGGER.log(Level.INFO, "Executing synchronous write " 
                        + "transaction for new educative experience register.");
                
                int updateResult = this.educativeExperienceDAO
                        .registerEducativeExperience(educativeExperience);

                if (updateResult > SystemConstants.SUCCESS) {
                    AlertUtility.showInformationAlert("Éxito", 
                            "Experiencia Educativa registrada correctamente.");
                    this.clearFormFields();
                    this.handleCancel(null);
                }
            } catch (DataIntegrityException exception) {
                LOGGER.log(Level.WARNING, "Data write operation aborted: "
                        + "Uniqueness constraint key violation.");
                AlertUtility.showWarningAlert("Registro Duplicado", 
                        exception.getMessage());
            } catch (DatabaseSystemException exception) {
                LOGGER.log(Level.SEVERE, "Server transaction engineering error "
                        + "persisting academic metadata fields.", exception);
                
                AlertUtility.showErrorAlert("Error de Servidor", "Ocurrió " 
                        + "un error al procesar la solicitud en el servidor.");
            }
        }
    }

    private boolean validateFields(String name, String nrc, String section, 
            ProfessorDTO prof) {
        boolean isValid = true;
        
        if (name.isEmpty() || nrc.isEmpty() || section == null || prof == null) {
            AlertUtility.showWarningAlert("Campos Vacíos", 
                    "Por favor, complete todos los campos obligatorios (*).");
            isValid = false;
        }

        if (isValid && !Validator.isValidName(name)) {
            AlertUtility.showWarningAlert("Dato Inválido", 
                    "El nombre de la materia solo permite caracteres alfabéticos.");
            isValid = false;
        }

        if (isValid && !Validator.isValidNRC(nrc)) {
            AlertUtility.showWarningAlert("Dato Inválido", 
                    "El NRC debe contener exactamente 5 números.");
            isValid = false;
        }

        return isValid;
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting educative experience registration. "
                + "Routing client stage context back to CoordinatorMenuView.");
        
        NavigationUtility.navigateTo(this.saveButton, 
                ViewConstants.VIEW_COORDINATOR_MENU,
                SystemConstants.TITLE_COORDINATOR_MENU_EE);
    }

    private void clearFormFields() {
        this.educativeExperienceNameTextField.clear();
        this.nrcTextField.clear();
        this.sectionComboBox.getSelectionModel().clearSelection();
        this.professorComboBox.getSelectionModel().clearSelection();
    }

    private static class ProfessorCellFactory 
            implements Callback<ListView<ProfessorDTO>, ListCell<ProfessorDTO>> {

        @Override
        public ListCell<ProfessorDTO> call(
                ListView<ProfessorDTO> parentListView) {
            return new ProfessorListCell();
        }
    }

    private static class ProfessorListCell extends ListCell<ProfessorDTO> {

        @Override
        protected void updateItem(ProfessorDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                this.setText(null);
            } else {
                this.setText(item.getProfessorStaffNumber() + " - " 
                        + item.getFirstName() + " " 
                        + item.getPaternalLastName());
            }
        }
    }
}