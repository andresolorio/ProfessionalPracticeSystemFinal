package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.util.ArrayList;
import static java.util.Collections.sort;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentMonitorDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;

/**
 *
 * @author cinth
 * @author andre
 */
public class StudentHoursMonitorViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            StudentHoursMonitorViewController.class.getName());

    @FXML
    private Label totalStudentsLabel;
    @FXML
    private Label finishedStudentsLabel;
    @FXML
    private TableView<StudentMonitorDTO> monitorTableView;
    @FXML
    private TableColumn<StudentMonitorDTO, String> enrollmentTableColumn;
    @FXML
    private TableColumn<StudentMonitorDTO, String> nameTableColumn;
    @FXML
    private TableColumn<StudentMonitorDTO, String> projectTableColumn;
    @FXML
    private TableColumn<StudentMonitorDTO, Integer> hoursCoveredTableColumn;
    @FXML
    private TableColumn<StudentMonitorDTO, Integer> hoursRemainingTableColumn;
    @FXML
    private ComboBox<String> eeFilterComboBox;
    @FXML
    private Button backButton;

    private final IProfessorDAO professorDAO;
    private final StudentDAO alumnoDAO;
    private ObservableList<StudentMonitorDTO> monitorObservableList;
    private List<StudentMonitorDTO> masterProgressList = new ArrayList<>();

    public StudentHoursMonitorViewController() {
        this.professorDAO = new ProfessorDAO();
        this.alumnoDAO = new StudentDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.configureTableViewColumns();
        
        this.eeFilterComboBox.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> 
                this.applyCourseFilter(newVal));
                
        this.loadStudentsProgress();
    }

    private void configureTableViewColumns() {
        this.enrollmentTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("enrollment"));
        this.nameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("fullName"));
        this.projectTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("projectName"));
        this.hoursCoveredTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("hoursCovered"));
        this.hoursRemainingTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("hoursRemaining"));
    }

    public void loadStudentsProgress() {
        String professorEmail = UserSession.getInstance().getLoggedUser()
                .getEmail();
        
        try {
            LOGGER.log(Level.INFO, "Initiating synchronous retrieval flow " 
                    + "for dashboard monitoring progress indicators.");

            ProfessorDTO professor = this.professorDAO
                    .getProfessorByEmail(professorEmail);
            
            if (professor == null) {
                throw new DatabaseSystemException("No se encontró información " 
                        + "del docente activo en el contexto de la sesión.");
            }

            List<StudentMonitorDTO> progressList = this.alumnoDAO
                    .getStudentsProgressByProfessor(professor
                    .getProfessorStaffNumber());

            this.handleFetchProgressSuccess(progressList);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication pipeline failure " 
                    + "while loading monitoring context data grids.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar la información de progreso desde el servidor.");
        }
    }

    public void handleFetchProgressSuccess(
            List<StudentMonitorDTO> resultList) {
        this.masterProgressList = resultList;
        
        for (StudentMonitorDTO dto : this.masterProgressList) {
            try {
                StudentDTO studentDetails = this.alumnoDAO
                        .getStudentByEnrollment(dto.getEnrollment());
                if (studentDetails != null) {
                    dto.setNrc(String.valueOf(studentDetails.getNrc()));
                }
            } catch (DatabaseSystemException exception) {
                LOGGER.log(Level.WARNING, "Failed to resolve student " 
                        + "NRC mapping links dynamically.");
            }
        }
        
        String professorEmail = UserSession.getInstance().getLoggedUser()
                .getEmail();
        Set<String> uniqueCourses = new HashSet<>();
        uniqueCourses.add("Todos");
        
        try {
            ProfessorDAO professorDAOImpl = new ProfessorDAO();
            List<String> professorCourses = professorDAOImpl
                    .getNrcsByProfessorEmail(professorEmail);
            if (professorCourses != null) {
                uniqueCourses.addAll(professorCourses);
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.WARNING, "Failed to compile professor courses.");
        }
        
        List<String> sortedCourses = new ArrayList<>(uniqueCourses);
        sort(sortedCourses);
        this.eeFilterComboBox.setItems(FXCollections
                .observableArrayList(sortedCourses));
        this.eeFilterComboBox.getSelectionModel().select("Todos");
        
        this.applyCourseFilter("Todos");
    }

    private void applyCourseFilter(String selectedCourseToken) {
        if (this.masterProgressList == null) {
            return;
        }

        String targetNrc = "Todos";
        if (selectedCourseToken != null && !"Todos".equals(selectedCourseToken)) {
            targetNrc = selectedCourseToken.split(" - ")[0].trim();
        }

        List<StudentMonitorDTO> filteredList = new ArrayList<>();
        int filteredTotal = 0;
        int filteredFinished = 0;

        for (StudentMonitorDTO student : this.masterProgressList) {
            String studentNrc = student.getNrc();
            boolean matchesNrc = "Todos".equals(targetNrc) 
                    || (studentNrc != null && studentNrc.equals(targetNrc));

            if (matchesNrc) {
                filteredList.add(student);
                filteredTotal++;
                
                if (student.getHoursCovered() >= SystemConstants
                        .REQUIRED_HOURS_FINAL_REPORT) {
                    filteredFinished++;
                }
            }
        }

        this.monitorObservableList = FXCollections
                .observableArrayList(filteredList);
        this.monitorTableView.setItems(this.monitorObservableList);
        
        this.totalStudentsLabel.setText(String.valueOf(filteredTotal));
        this.finishedStudentsLabel.setText(String.valueOf(filteredFinished));
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        LOGGER.log(Level.INFO, "Explicit back routing triggered. Returning " 
                + "window context back to ProfessorMenuView stage layout.");

        NavigationUtility.navigateTo(this.backButton, 
                ViewConstants.VIEW_PROFESSOR_MENU,
                SystemConstants.TITLE_PROFESSOR_DASHBOARD);
    }
}