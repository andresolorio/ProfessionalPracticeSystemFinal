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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ResponsibleProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ResponsibleProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.LinkedOrganizationDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.LinkedOrganizationDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.ILinkedOrganizationDAO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IResponsibleProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;

/**
 *
 * @author cinth
 * @author andre
 */
public class ResponsibleListViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            ResponsibleListViewController.class.getName());
    private final IResponsibleProjectDAO responsibleProjectDAO;
    private final ILinkedOrganizationDAO organizationDAO;
    private List<LinkedOrganizationDTO> organizationsList;
    private ObservableList<ResponsibleProjectDTO> responsibleList;

    @FXML
    private TableView<ResponsibleProjectDTO> responsibleTableView;
    @FXML
    private TableColumn<ResponsibleProjectDTO, Integer> idTableColumn;
    @FXML
    private TableColumn<ResponsibleProjectDTO, Integer> 
            linkedOrganizationIdTableColumn;
    @FXML
    private TableColumn<ResponsibleProjectDTO, Integer> 
            linkedOrganizationNameTableColumn;
    @FXML
    private TableColumn<ResponsibleProjectDTO, String> nameTableColumn;
    @FXML
    private TableColumn<ResponsibleProjectDTO, String> firstLastNameTableColumn;
    @FXML
    private TableColumn<ResponsibleProjectDTO, String> secondLastNameTableColumn;
    @FXML
    private TableColumn<ResponsibleProjectDTO, String> positionTableColumn;
    @FXML
    private Button closeButton;

    public ResponsibleListViewController() {
        this.responsibleProjectDAO = new ResponsibleProjectDAO();
        this.organizationDAO = new LinkedOrganizationDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourcebundle) {
        this.configureTableColumns();
        this.loadResponsiblesData();
    }

    private void configureTableColumns() {
        this.idTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("idResponsible"));
        this.linkedOrganizationIdTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("idLinkedOrganization"));
        this.linkedOrganizationNameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("idLinkedOrganization"));
        this.linkedOrganizationNameTableColumn.setCellFactory(
                new OrganizationNameCellFactory(this));
        this.nameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("firstName"));
        this.firstLastNameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("lastName"));
        this.secondLastNameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("secondLastName"));
        this.positionTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("position"));
    }

    public String findOrganizationName(int id) {
        if (this.organizationsList != null) {
            for (LinkedOrganizationDTO org : this.organizationsList) {
                if (org.getIdLinkedOrganization() == id) {
                    return org.getLinkedOrganizationName();
                }
            }
        }
        return "Desconocida (" + id + ")";
    }

    private void loadResponsiblesData() {
        try {
            LOGGER.log(Level.INFO, "Executing sequential query pipeline " 
                    + "for combined responsibles and organizations datasets.");

            List<LinkedOrganizationDTO> organizations = this.organizationDAO
                    .getAllLinkedOrganizations();
            List<ResponsibleProjectDTO> allResponsibles = new ArrayList<>();

            for (LinkedOrganizationDTO organization : organizations) {
                List<ResponsibleProjectDTO> partialList = this
                        .responsibleProjectDAO.getResponsiblesByOrganization(
                        organization.getIdLinkedOrganization());
                allResponsibles.addAll(partialList);
            }

            this.handleLoadDataSuccess(allResponsibles, organizations);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server infrastructure network layer " 
                    + "crash: Failed to fetch organization responsibles.", 
                    exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar la lista de responsables desde el servidor.");
        }
    }

    public void handleLoadDataSuccess(List<ResponsibleProjectDTO> responsibles, 
            List<LinkedOrganizationDTO> organizations) {
        this.organizationsList = organizations;
        this.responsibleList = FXCollections.observableArrayList(responsibles);
        this.responsibleTableView.setItems(this.responsibleList);
    }

    @FXML
    private void handleClose(ActionEvent event) {
        LOGGER.log(Level.INFO, "Closing technical responsible catalog panel " 
                + "workspace. Routing context back to coordinator menu view.");
        
        NavigationUtility.navigateTo(this.closeButton, 
                ViewConstants.VIEW_COORDINATOR_MENU, 
                SystemConstants.TITLE_COORDINATOR_MENU);
    }

    private static class OrganizationNameCellFactory implements Callback<
            TableColumn<ResponsibleProjectDTO, Integer>, 
            TableCell<ResponsibleProjectDTO, Integer>> {

        private final ResponsibleListViewController controllerReference;

        public OrganizationNameCellFactory(
                ResponsibleListViewController controller) {
            this.controllerReference = controller;
        }

        @Override
        public TableCell<ResponsibleProjectDTO, Integer> call(
                TableColumn<ResponsibleProjectDTO, Integer> column) {
            return new OrganizationNameTableCell(this.controllerReference);
        }
    }

    private static class OrganizationNameTableCell 
            extends TableCell<ResponsibleProjectDTO, Integer> {

        private final ResponsibleListViewController controllerReference;

        public OrganizationNameTableCell(
                ResponsibleListViewController controller) {
            this.controllerReference = controller;
        }

        @Override
        protected void updateItem(Integer id, boolean empty) {
            super.updateItem(id, empty);
            if (empty || id == null) {
                this.setText(null);
            } else {
                this.setText(this.controllerReference.findOrganizationName(id));
            }
        }
    }
}