package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.EducativeExperienceController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EducativeExperienceDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;

/**
 *
 * @author cinth
 * @author andre
 */
public class EducativeExperienceListViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            EducativeExperienceListViewController.class.getName());

    @FXML
    private TableView<EducativeExperienceDTO> experienceTableView;
    @FXML
    private TableColumn<EducativeExperienceDTO, Integer> nrcTableColumn;
    @FXML
    private TableColumn<EducativeExperienceDTO, String> nameTableColumn;
    @FXML
    private TableColumn<EducativeExperienceDTO, String> sectionTableColumn;
    @FXML
    private TableColumn<EducativeExperienceDTO, String> professorTableColumn;
    @FXML
    private Button closeButton;

    private EducativeExperienceController experienceController;
    private ObservableList<EducativeExperienceDTO> experiencesObservableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.experienceController = new EducativeExperienceController();
        this.configureTableColumns();
        this.loadExperiencesData();
    }

    private void configureTableColumns() {
        this.nrcTableColumn.setCellValueFactory(
                new PropertyValueFactory<>(SystemConstants.PROP_EE_NRC));
        this.nameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>(SystemConstants.PROP_EE_NAME));
        this.sectionTableColumn.setCellValueFactory(
                new PropertyValueFactory<>(SystemConstants.PROP_EE_SECTION));
        this.professorTableColumn.setCellValueFactory(
                new PropertyValueFactory<>(SystemConstants.PROP_EE_PROFESSOR));
    }

    private void loadExperiencesData() {
        try {
            LOGGER.log(Level.INFO, "Executing synchronous query data flow " 
                    + "for educational experiences from the server.");

            List<EducativeExperienceDTO> experienceList = this
                    .experienceController.getAllExperiences();
            
            this.experiencesObservableList = FXCollections
                    .observableArrayList(experienceList);
            this.experienceTableView.setItems(this.experiencesObservableList);
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server infrastructure data pipeline " 
                    + "failure while populating educative experiences mapping.", 
                    exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar las experiencias educativas desde el servidor.");
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        LOGGER.log(Level.INFO, "Closing educational experiences stage context " 
                + "and routing screen back to coordinator menu view.");

        NavigationUtility.navigateTo(this.closeButton, 
                ViewConstants.VIEW_COORDINATOR_MENU, 
                SystemConstants.TITLE_COORDINATOR_MENU);
    }
}