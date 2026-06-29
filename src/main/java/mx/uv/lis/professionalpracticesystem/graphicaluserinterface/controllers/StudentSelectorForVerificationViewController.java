package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.StudentController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;

/**
 *
 * @author cinth
 * @author andre
 */
public class StudentSelectorForVerificationViewController 
        implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            StudentSelectorForVerificationViewController.class.getName());
    private final StudentController studentController;
    private ObservableList<StudentDTO> studentObservableList;

    @FXML
    private TableView<StudentDTO> studentsTableView;
    @FXML
    private TableColumn<StudentDTO, String> enrollmentTableColumn;
    @FXML
    private TableColumn<StudentDTO, String> nameTableColumn;
    @FXML
    private TableColumn<StudentDTO, String> hoursTableColumn;
    @FXML
    private TableColumn<StudentDTO, String> statusTableColumn;
    @FXML
    private Button backButton;
    @FXML
    private Button verifyButton;

    public StudentSelectorForVerificationViewController() {
        this.studentController = new StudentController();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.configureTableColumns();
        this.loadAssignedStudentsData();
    }

    private void configureTableColumns() {
        this.enrollmentTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("enrollmentId"));
        
        this.nameTableColumn.setCellValueFactory(
                new StudentNameCellValueFactory());

        this.hoursTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("coveredCredits"));
        this.statusTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("status"));
    }

    private void loadAssignedStudentsData() {
        String activeProfessorEmail = UserSession.getInstance()
                .getLoggedUser().getEmail();
        
        try {
            LOGGER.log(Level.INFO, "Executing synchronous data retrieval " 
                    + "for assigned students metrics from the server.");
            
            List<StudentDTO> resultList = this.studentController
                    .getAssignedStudents(activeProfessorEmail);
            
            this.studentObservableList = FXCollections
                    .observableArrayList(resultList);
            this.studentsTableView.setItems(this.studentObservableList);
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication pipeline failure " 
                    + "encountered while loading student profiles.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar la lista de alumnos desde el servidor.");
        }
    }

    @FXML
    private void handleVerifyStudent(ActionEvent event) {
        StudentDTO selectedStudent = this.studentsTableView
                .getSelectionModel().getSelectedItem();

        if (selectedStudent != null) {
            LOGGER.log(Level.INFO, "Routing system focus to specific " 
                    + "document validations for student: {0}", 
                    selectedStudent.getEnrollmentId());
            this.navigateToVerificationWorkspace(selectedStudent);
        } else {
            AlertUtility.showWarningAlert("Selección Requerida", 
                    "Por favor, seleccione un alumno de la lista " 
                    + "para proceder con la revisión.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        LOGGER.log(Level.INFO, "User requested return context path back " 
                + "to main Professor dashboard menu.");
        
        NavigationUtility.navigateTo(this.backButton, 
                ViewConstants.VIEW_PROFESSOR_MENU, 
                SystemConstants.TITLE_MAIN_MENU_PROFESSOR);
    }

    private void navigateToVerificationWorkspace(StudentDTO targetStudent) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass()
                    .getResource(ViewConstants.VIEW_INITIAL_DOCUMENTS_VERIFICATION));
            Parent root = (Parent) loader.load();

            InitialDocumentsVerificationViewController downstreamController 
                    = (InitialDocumentsVerificationViewController) loader
                    .getController();
            downstreamController.setSelectedStudentContext(targetStudent);

            Stage stage = (Stage) this.verifyButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Navigation failure: Could not " 
                    + "orchestrate change to Verification panel due to " 
                    + "FXML streams error.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "cargar la ventana de verificación de documentos.");
        }
    }
    
    private static class StudentNameCellValueFactory 
            implements Callback<TableColumn.CellDataFeatures<StudentDTO, String>, 
            ObservableValue<String>> {

        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<
                StudentDTO, String> cellData) {
            
            String fullName = "";
            if (cellData != null && cellData.getValue() != null) {
                StudentDTO student = cellData.getValue();
                fullName = student.getFirstName() + " " 
                        + student.getPaternalLastName() + " " 
                        + student.getMaternalLastName();
            }
            return new ReadOnlyStringWrapper(fullName);
        }
    }
}