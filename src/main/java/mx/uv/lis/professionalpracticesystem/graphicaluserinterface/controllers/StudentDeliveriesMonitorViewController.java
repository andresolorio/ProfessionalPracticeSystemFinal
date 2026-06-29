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
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDeliveryMonitorDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;

/**
 *
 * @author cinth
 * @author andre
 */
public class StudentDeliveriesMonitorViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            StudentDeliveriesMonitorViewController.class.getName());

    @FXML
    private Label totalStudentsLabel;
    @FXML
    private Label finishedStudentsLabel;
    @FXML
    private TableView<StudentDeliveryMonitorDTO> monitorTableView;
    @FXML
    private TableColumn<StudentDeliveryMonitorDTO, String> enrollmentTableColumn;
    @FXML
    private TableColumn<StudentDeliveryMonitorDTO, String> nameTableColumn;
    @FXML
    private TableColumn<StudentDeliveryMonitorDTO, String> projectTableColumn;   
    @FXML
    private TableColumn<StudentDeliveryMonitorDTO, Integer> validatedReportsTableColumn;
    @FXML
    private TableColumn<StudentDeliveryMonitorDTO, String> selfEvaluationStatusTableColumn;
    
    @FXML
    private ComboBox<String> eeFilterComboBox;
    @FXML
    private Button backButton;

    private final IProfessorDAO professorDAO;
    private final StudentDAO alumnoDAO;
    private ObservableList<StudentDeliveryMonitorDTO> monitorObservableList;
    private List<StudentDeliveryMonitorDTO> masterProgressList = new ArrayList<>();

    public StudentDeliveriesMonitorViewController() {
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
        this.validatedReportsTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("validatedReportsCount"));
        this.selfEvaluationStatusTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("selfEvaluationStatus"));
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

            List<StudentDeliveryMonitorDTO> progressList = this.alumnoDAO
                    .getStudentsDeliveriesByProfessor(professor
                    .getProfessorStaffNumber());

            this.handleFetchProgressSuccess(progressList);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication pipeline failure.", exception);
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar la información de progreso desde el servidor.");
        }
    }
    
    public void handleFetchProgressSuccess(
            List<StudentDeliveryMonitorDTO> resultList) {
        this.masterProgressList = resultList;
        
        for (StudentDeliveryMonitorDTO dto : this.masterProgressList) {
            try {
                StudentDTO studentDetails = this.alumnoDAO
                        .getStudentByEnrollment(dto.getEnrollment());
                if (studentDetails != null) {
                    dto.setNrc(String.valueOf(studentDetails.getNrc()));
                }
            } catch (DatabaseSystemException exception) {
                LOGGER.log(Level.WARNING, "Failed to resolve student NRC map "
                        + "using sequential enrollment token link.");
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

        List<StudentDeliveryMonitorDTO> filteredList = new ArrayList<>();
        int filteredTotal = 0;

        for (StudentDeliveryMonitorDTO student : this.masterProgressList) {
            String studentNrc = student.getNrc();
            boolean matchesNrc = "Todos".equals(targetNrc) 
                    || (studentNrc != null && studentNrc.equals(targetNrc));

            if (matchesNrc) {
                filteredList.add(student);
                filteredTotal++;
            }
        }

        this.monitorObservableList = FXCollections
                .observableArrayList(filteredList);
        this.monitorTableView.setItems(this.monitorObservableList);
        this.totalStudentsLabel.setText(String.valueOf(filteredTotal));
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        NavigationUtility.navigateTo(this.backButton, 
                ViewConstants.VIEW_PROFESSOR_MENU,
                SystemConstants.TITLE_PROFESSOR_DASHBOARD);
    }
}