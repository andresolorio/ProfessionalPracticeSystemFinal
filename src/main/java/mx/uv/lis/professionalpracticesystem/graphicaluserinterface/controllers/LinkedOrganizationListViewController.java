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
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.LinkedOrganizationDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.LinkedOrganizationDTO;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;

/**
 *
 * @author andre
 * @author cinth
 */
public class LinkedOrganizationListViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            LinkedOrganizationListViewController.class.getName());

    @FXML
    private TableView<LinkedOrganizationDTO> linkedOrganizationTableView;
    @FXML
    private TableColumn<LinkedOrganizationDTO, Integer> idTableColumn;
    @FXML
    private TableColumn<LinkedOrganizationDTO, String> nameTableColumn;
    @FXML
    private TableColumn<LinkedOrganizationDTO, String> addressTableColumn;
    @FXML
    private TableColumn<LinkedOrganizationDTO, String> cityTableColumn;
    @FXML
    private TableColumn<LinkedOrganizationDTO, String> phoneTableColumn;
    @FXML
    private TableColumn<LinkedOrganizationDTO, String> mailTableColumn;
    @FXML
    private TableColumn<LinkedOrganizationDTO, String> sectorTableColumn;
    @FXML
    private TableColumn<LinkedOrganizationDTO, Integer> directUserTableColumn;
    @FXML
    private TableColumn<LinkedOrganizationDTO, Integer> indirectUserTableColumn;
    @FXML
    private Button closeButton;

    private ObservableList<LinkedOrganizationDTO> organizationsList;
    private final LinkedOrganizationDAO organizationDAO;

    public LinkedOrganizationListViewController() {
        this.organizationDAO = new LinkedOrganizationDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourcebundle) {
        this.configureTableColumns();
        this.loadOrganizationsData();
    }

    private void configureTableColumns() {
        this.idTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("idLinkedOrganization"));
        this.nameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("linkedOrganizationName"));              
        this.addressTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("address"));
        this.cityTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("city"));
        this.phoneTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("phoneNumber"));
        this.mailTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("email"));
        this.sectorTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("sector"));
        this.directUserTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("directUsers"));
        this.indirectUserTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("indirectUsers"));
    }

    private void loadOrganizationsData() {
        try {
            LOGGER.log(Level.INFO, "Executing synchronous data retrieval " 
                    + "for linked organizations from the server.");
            
            List<LinkedOrganizationDTO> allOrganizations = this.organizationDAO
                    .getAllLinkedOrganizations();
            
            this.organizationsList = FXCollections
                    .observableArrayList(allOrganizations);
            this.linkedOrganizationTableView.setItems(this.organizationsList);
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication pipeline failure " 
                    + "encountered while loading organizations catalog context.", 
                    exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar la lista de organizaciones vinculadas " 
                    + "desde el servidor.");
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting organization list auditing view. " 
                + "Routing workspace screen stage back to CoordinatorMenuView.");

        NavigationUtility.navigateTo(this.closeButton, 
                ViewConstants.VIEW_COORDINATOR_MENU, 
                SystemConstants.TITLE_COORDINATOR_MENU);
    }
}