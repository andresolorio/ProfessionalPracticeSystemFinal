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
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.EducativeExperienceDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EducativeExperienceDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;

/**
 *
 * @author andre
 * @author cinth
 */
public class AssignedEducationalExperiencesViewController 
        implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            AssignedEducationalExperiencesViewController.class.getName());

    @FXML
    private TableView<EducativeExperienceDTO> eeTableView;
    @FXML
    private TableColumn<EducativeExperienceDTO, Integer> nrcTableColumn;
    @FXML
    private TableColumn<EducativeExperienceDTO, String> nameTableColumn;
    @FXML
    private TableColumn<EducativeExperienceDTO, String> sectionTableColumn;
    @FXML
    private Button backButton;

    private final EducativeExperienceDAO educativeExperienceDAO;
    private ObservableList<EducativeExperienceDTO> 
            educativeExperienceObservableList;

    public AssignedEducationalExperiencesViewController() {
        this.educativeExperienceDAO = new EducativeExperienceDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.configureTableViewColumns();
        this.loadAssignedEducationalExperiences();
    }

    private void configureTableViewColumns() {
        this.nrcTableColumn.setCellValueFactory(
                new PropertyValueFactory<>(SystemConstants.PROP_EE_NRC_SHORT));
        this.nameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>(SystemConstants.PROP_EE_NAME_LONG));
        this.sectionTableColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                SystemConstants.PROP_EE_SECTION_SHORT));
    }

    public void loadAssignedEducationalExperiences() {
        UserDTO loggedUser = UserSession.getInstance().getLoggedUser();

        if (loggedUser == null) {
            LOGGER.log(Level.SEVERE, "Security anomaly: Unauthorized access " 
                    + "attempt in AssignedEEView");
            return;
        }
        
        try {
            LOGGER.log(Level.INFO, "Executing synchronous data retrieval " 
                    + "for assigned educational experiences from the server.");

            List<EducativeExperienceDTO> resultList = this
                    .educativeExperienceDAO
                    .getEducativeExperiencesWithProfessorsByProfessorEmail(
                    loggedUser.getEmail());

            this.handleLoadExperiencesSuccess(resultList);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication pipeline failure " 
                    + "while loading assigned educational experiences.", 
                    exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar las experiencias educativas desde el servidor.");
        }
    }

    public void handleLoadExperiencesSuccess(
            List<EducativeExperienceDTO> resultList) {
        this.educativeExperienceObservableList = FXCollections
                .observableArrayList(resultList);
        this.eeTableView.setItems(this.educativeExperienceObservableList);
        
        LOGGER.log(Level.INFO, "TableView populated successfully with {0} " 
                + "experiences.", resultList.size());
    }

    @FXML
    private void handleBack(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting assigned educational experiences " 
                + "session view. Routing back to ProfessorMenuView screen.");

        NavigationUtility.navigateTo(this.backButton, 
                ViewConstants.VIEW_PROFESSOR_MENU, 
                SystemConstants.TITLE_PROFESSOR_MENU);
    }
}