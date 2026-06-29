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

/**
 *
 * @author cinth
 * @author andre
 */
public class StudentDeliveriesMonitorViewController implements Initializable {

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
    private Button backButton;

    private final IProfessorDAO professorDAO;
    private final StudentDAO alumnoDAO;
    private ObservableList<StudentMonitorDTO> monitorObservableList;

    public StudentDeliveriesMonitorViewController() {
        this.professorDAO = new ProfessorDAO();
        this.alumnoDAO = new StudentDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.configureTableViewColumns();
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
        this.monitorObservableList = FXCollections
                .observableArrayList(resultList);
        this.monitorTableView.setItems(this.monitorObservableList);
        this.calculateSummaryMetrics(resultList);
    }

    private void calculateSummaryMetrics(List<StudentMonitorDTO> students) {
        if (students == null) {
            return;
        }

        int totalStudents = students.size();
        int finishedStudentsCount = 0;

        for (StudentMonitorDTO student : students) {
            if (student.getHoursCovered() >= SystemConstants
                    .REQUIRED_HOURS_FINAL_REPORT) {
                finishedStudentsCount++;
            }
        }

        this.totalStudentsLabel.setText(String.valueOf(totalStudents));
        this.finishedStudentsLabel.setText(String.valueOf(
                finishedStudentsCount));
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