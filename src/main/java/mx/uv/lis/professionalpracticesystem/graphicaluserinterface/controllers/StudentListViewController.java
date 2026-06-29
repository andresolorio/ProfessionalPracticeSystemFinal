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
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IStudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATUS_INACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.SUCCESS;

/**
 * 
 * @author cinth
 * @author andre
 */
public class StudentListViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            StudentListViewController.class.getName());
    private final IStudentDAO studentDAO;
    private ObservableList<StudentDTO> studentsList;

    @FXML 
    private TableView<StudentDTO> studentTableView;    
    @FXML 
    private TableColumn<StudentDTO, String> enrollmentTableColumn;
    @FXML 
    private TableColumn<StudentDTO, String> nameTableColumn;
    @FXML 
    private TableColumn<StudentDTO, String> paternalLastNameTableColumn;
    @FXML 
    private TableColumn<StudentDTO, String> maternalLastNameTableColumn;
    @FXML 
    private TableColumn<StudentDTO, String> genderTableColumn;
    @FXML 
    private TableColumn<StudentDTO, String> statusTableColumn;
    @FXML 
    private TableColumn<StudentDTO, String> professorTableColumn;
    @FXML 
    private Button closeButton;
    @FXML 
    private Button inactivateButton;

    public StudentListViewController() {
        this.studentDAO = new StudentDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.configureTableColumns();
        this.loadStudentsData();
    }

    private void configureTableColumns() {
        this.enrollmentTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("enrollmentId"));
        this.nameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("firstName"));
        this.paternalLastNameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("paternalLastName"));
        this.maternalLastNameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("maternalLastName"));
        this.genderTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("gender"));
        this.statusTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("status"));
        this.professorTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("coordinatorPersonalNumber"));
    }

    private void loadStudentsData() {
        try {
            LOGGER.log(Level.INFO, "Executing synchronous query data flow " 
                    + "for all existing student record profiles from server.");

            List<StudentDTO> students = this.studentDAO.getAllStudents();
            this.handleLoadDataSuccess(students);
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication pipeline failure " 
                    + "encountered while loading complete student records.");
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar la lista de alumnos desde el servidor.");
        }
    }

    public void handleLoadDataSuccess(List<StudentDTO> students) {
        this.studentsList = FXCollections.observableArrayList(students);
        this.studentTableView.setItems(this.studentsList);
    }

    @FXML
    private void handleInactivate(ActionEvent event) {
        StudentDTO selectedStudent = this.studentTableView.getSelectionModel()
                .getSelectedItem();

        if (selectedStudent == null) {
            AlertUtility.showWarningAlert("Selección Requerida", 
                    "Por favor, selecciona un alumno de la lista " 
                    + "para inactivarlo.");
            return;
        }

        if (selectedStudent.getStatus().equalsIgnoreCase(STATUS_INACTIVE)) {
            AlertUtility.showErrorAlert("Operación Inválida", 
                    "No se puede inactivar al alumno debido a que ya " 
                    + "se encuentra inactivo.");
            return;
        }

        String confirmationMessage = "Al inactivar al alumno, se desvinculará " 
                + "del proyecto, del profesor y de su experiencia educativa.\n"
                + "Las horas cubiertas se mantendrán.\n¿Desea continuar?";
                                    
        if (AlertUtility.showConfirmationAlert(
                "Confirmar Baja Administrativa", confirmationMessage)) {
            
            LOGGER.log(Level.INFO, "Initiating sequential status change routine " 
                    + "for enrollment ID: {0}", selectedStudent.getEnrollmentId());
            
            this.executeInactivationFlow(selectedStudent.getEnrollmentId());
        }
    }

    private void executeInactivationFlow(String enrollmentId) {
        try {
            int updateResult = this.studentDAO.updateStudentStatus(
                    enrollmentId, SystemConstants.STUDENT_INACTIVE_LABEL);
            
            if (updateResult > SUCCESS) {
                AlertUtility.showInformationAlert("Éxito", 
                        "El estudiante se inactivó correctamente.");
                this.loadStudentsData();
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server transaction engineering error " 
                    + "modifying student active status flag.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "Ocurrió un " 
                    + "problema en el servidor al intentar inactivar al alumno.");
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        LOGGER.log(Level.INFO, "Explicit closing routine triggered. " 
                + "Routing screen context back to CoordinatorMenuView.");
        
        NavigationUtility.navigateTo(this.closeButton, 
                ViewConstants.VIEW_COORDINATOR_MENU, 
                SystemConstants.TITLE_COORDINATOR_MENU);
    }
}