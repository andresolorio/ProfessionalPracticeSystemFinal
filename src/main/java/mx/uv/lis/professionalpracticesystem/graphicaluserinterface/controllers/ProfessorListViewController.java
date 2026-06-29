package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.logic.controllers.ProfessorController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;

/**
 * 
 * @author cinth
 * @author andre
 */
public class ProfessorListViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            ProfessorListViewController.class.getName());

    @FXML
    private TableView<ProfessorDTO> professorsTableView;
    @FXML
    private TableColumn<ProfessorDTO, String> staffNumberTableColumn;
    @FXML
    private TableColumn<ProfessorDTO, String> nameTableColumn;
    @FXML
    private TableColumn<ProfessorDTO, String> paternalLastNameTableColumn;
    @FXML
    private TableColumn<ProfessorDTO, String> maternalLastNameTableColumn;
    @FXML
    private TableColumn<ProfessorDTO, String> emailTableColumn;
    @FXML
    private TableColumn<ProfessorDTO, String> roleTableColumn;
    @FXML
    private TableColumn<ProfessorDTO, String> statusTableColumn;
    @FXML
    private Button inactivateButton;
    @FXML
    private Button toggleRoleButton;

    private ObservableList<ProfessorDTO> professorsList;
    private final ProfessorDAO professorDAO = new ProfessorDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourcebundle) {
        this.configureTable();
        this.loadAllProfessorsContext();
    }

    public void setMode(String mode) {
        if ("ROLE".equals(mode)) {
            this.inactivateButton.setVisible(false);
            this.inactivateButton.setManaged(false);
            this.toggleRoleButton.setVisible(true);
            this.toggleRoleButton.setManaged(true);
        } else if ("INACTIVATE".equals(mode)) {
            this.toggleRoleButton.setVisible(false);
            this.toggleRoleButton.setManaged(false);
            this.inactivateButton.setVisible(true);
            this.inactivateButton.setManaged(true);
        }
    }

    private void configureTable() {
        this.staffNumberTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("professorStaffNumber"));
        this.nameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("firstName"));
        this.paternalLastNameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("paternalLastName"));
        this.maternalLastNameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("maternalLastName"));
        this.emailTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("email"));        
        this.statusTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("status"));
        this.roleTableColumn.setCellValueFactory(
                new RoleCellValueFactory());
    }

    public static SimpleStringProperty getRolePropertyRepresentation(
            ProfessorDTO professor) {
        String roleText = "No";
        if (professor.getIsCoordinator()) {
            roleText = "Sí";
        }
        return new SimpleStringProperty(roleText);
    }

    private void loadAllProfessorsContext() {
        try {
            List<ProfessorDTO> allProfessors = this.professorDAO
                    .getAllProfessors();
            this.professorsList = FXCollections
                    .observableArrayList(allProfessors);
            this.professorsTableView.setItems(this.professorsList);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Infrastructure connectivity failure " 
                    + "loading general professors auditing catalog", exception);
            AlertUtility.showWarningAlert("Error de Sistema", "No se pudo " 
                    + "cargar la lista de profesores desde la Base de Datos.");
        }
    }

    @FXML
    private void handleToggleCoordinator(ActionEvent event) {
        ProfessorDTO selectedProfessor = this.professorsTableView
                .getSelectionModel().getSelectedItem();

        if (selectedProfessor != null) {
            boolean isAttemptingToAssign = !selectedProfessor.getIsCoordinator();
            try {
                if (isAttemptingToAssign) {
                    LOGGER.log(Level.INFO, "Validating coordinator role " 
                            + "exclusivity for staff number: {0}",
                            selectedProfessor.getProfessorStaffNumber());

                    boolean isLimitReached = this.professorDAO
                            .isCoordinatorLimitReachedExcluding(
                            selectedProfessor.getProfessorStaffNumber());

                    if (isLimitReached) {
                        LOGGER.log(Level.WARNING, "Assignment rejected: " 
                                + "The coordinator slot is already filled.");
                        AlertUtility.showWarningAlert("Asignación Rechazada", 
                                "No es posible asignar este rol. El sistema ya "
                                + "cuenta con un Coordinador activo.");
                        return;
                    }
                }

                String message = isAttemptingToAssign 
                        ? "¿Desea asignar el rol de Coordinador?" 
                        : "¿Desea quitar el rol de Coordinador?";

                if (AlertUtility.showConfirmationAlert("Cambiar Rol", message)) {
                    this.professorDAO.updateCoordinatorRole(
                            selectedProfessor.getProfessorStaffNumber(), 
                            isAttemptingToAssign);
                    this.loadAllProfessorsContext();
                    AlertUtility.showInformationAlert("Rol Actualizado", 
                            "El rol ha sido modificado con éxito.");
                }

            } catch (DatabaseSystemException exception) {
                LOGGER.log(Level.SEVERE, "Database transactional failure " 
                        + "executing coordinator role modification statement", 
                        exception);
                AlertUtility.showWarningAlert("Error en la Base de Datos", 
                        "No se pudo cambiar el rol del profesor.");
            }
        } else {
            AlertUtility.showWarningAlert("Selección Requerida", 
                    "Por favor, seleccione un profesor de la tabla.");
        }
    }

    @FXML
    private void handleInactivateProfessor(ActionEvent event) {
        ProfessorDTO selectedProfessor = this.professorsTableView
                .getSelectionModel().getSelectedItem();

        if (selectedProfessor == null) {
            AlertUtility.showWarningAlert("Selección Requerida", 
                    "Por favor, seleccione un docente de la tabla activa.");
            return;
        }

        String staffNumber = selectedProfessor.getProfessorStaffNumber();
        
        try {
            LOGGER.log(Level.INFO, "Initiating sequential validation flow " 
                    + "for professor inactivation via logic controller.");

            ProfessorController professorController = new ProfessorController();
            int rowsAffected = professorController.inactivateProfessor(staffNumber);

            if (rowsAffected > SystemConstants.RESET) {
                AlertUtility.showInformationAlert("Profesor Inactivado", 
                        "El docente ha sido dado de baja con éxito.");
                this.refreshProfessorsTable();
            }
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Logic layer system exception handled " 
                    + "safely during professor status modification loop.");
            
            AlertUtility.showErrorAlert("Error de Servidor", 
                    exception.getMessage());
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        try {
            int activeCount = this.professorDAO.getActiveCoordinatorsCount();
            if (activeCount == SystemConstants.MINIMUM_ACTIVE_COORDINATORS) {
                AlertUtility.showWarningAlert("Cierre Bloqueado", 
                        "Debe existir un Coordinador activo antes de salir.");
                return;
            }
            ((Stage) this.professorsTableView.getScene().getWindow()).close();
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Failure validating role management " 
                    + "system exit rules constraint", exception);
            ((Stage) this.professorsTableView.getScene().getWindow()).close();
        }
    }

    private static class RoleCellValueFactory implements Callback<
            CellDataFeatures<ProfessorDTO, String>, ObservableValue<String>> {

        @Override
        public ObservableValue<String> call(
                CellDataFeatures<ProfessorDTO, String> cellData) {
            ProfessorDTO professor = cellData.getValue();
            return ProfessorListViewController
                    .getRolePropertyRepresentation(professor);
        }
    }
    
    private void refreshProfessorsTable() {
        try {
            LOGGER.log(Level.INFO, "Synchronously reloading active " 
                    + "professors dataset catalog from the core server.");

            ProfessorDAO professorDAO = new ProfessorDAO();
            
            List<ProfessorDTO> updatedList = professorDAO.getAllProfessors();

            this.professorsTableView.setItems(FXCollections
                    .observableArrayList(updatedList));
            this.professorsTableView.refresh();

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication failure " 
                    + "encountered while refreshing professors table items.");
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se " 
                    + "pudieron refrescar los datos de los docentes.");
        }
    }
}