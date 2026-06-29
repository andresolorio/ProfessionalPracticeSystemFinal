package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.UserController;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.EvaluationCriterionDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.LinkedOrganizationDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.SelfEvaluationDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EvaluationCriterionDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.LinkedOrganizationDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.SelfEvaluationDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.generator.SelfEvaluationPDFGenerator;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.*;

/**
 *
 * @author cinth
 * @author andre
 */
public class GenerateSelfEvaluationViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            GenerateSelfEvaluationViewController.class.getName());

    @FXML
    private TableView<EvaluationCriterionDTO> criteriaTable; 
    @FXML
    private TableColumn<EvaluationCriterionDTO, String> statementColumn; 
    @FXML
    private TableColumn<EvaluationCriterionDTO, Integer> scoreColumn; 
    @FXML
    private Button generateButton;

    private StudentDTO authenticatedStudent;
    private final EvaluationCriterionDAO evaluationCriterionDAO;
    private final SelfEvaluationDAO selfEvaluationDAO;
    private final StudentDAO studentDAO;
    private final ProjectDAO projectDAO;
    private final LinkedOrganizationDAO organizationDAO;
    private ObservableList<EvaluationCriterionDTO> criteriaObservableList;

    public GenerateSelfEvaluationViewController() {
        this.evaluationCriterionDAO = new EvaluationCriterionDAO();
        this.selfEvaluationDAO = new SelfEvaluationDAO();
        this.studentDAO = new StudentDAO();
        this.projectDAO = new ProjectDAO();
        this.organizationDAO = new LinkedOrganizationDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.configureTableColumns();
        this.loadEvaluationCriteria();
        this.configureButtonEventHandlers();
    }
    
    public void initializeSelfEvaluationContext(StudentDTO student) {
        this.authenticatedStudent = student;
    }

    private void configureTableColumns() {
        this.statementColumn.setCellValueFactory(
                new PropertyValueFactory<>(SystemConstants.PROP_CRITERIA_STATEMENT));
        this.scoreColumn.setCellFactory(new ScoreCellFactory(this));
    }

    private void configureButtonEventHandlers() {
        this.generateButton.setOnAction(new GenerateButtonEventHandler(this));
    }

    public void handleScoreSelection(TableRow<EvaluationCriterionDTO> tableRow, 
            Integer newScore) {
        if (tableRow != null && tableRow.getItem() != null) {
            tableRow.getItem().setScore(newScore);
            this.validateCompletion();
        }
    }

    private void loadEvaluationCriteria() {
        try {
            LOGGER.log(Level.INFO, "Fetching evaluation criteria " 
                    + "synchronously from database core data layers.");
            
            List<EvaluationCriterionDTO> criteriaList = this
                    .evaluationCriterionDAO.getAllCriteria();
            
            this.criteriaObservableList = FXCollections
                    .observableArrayList(criteriaList);
            this.criteriaTable.setItems(this.criteriaObservableList);
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Technical error fetching criteria " 
                    + "collection from repository context.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se " 
                    + "pudieron obtener los criterios desde el servidor.");
        }
    }

    public void validateCompletion() {
        boolean isFormComplete = true;

        for (EvaluationCriterionDTO criterion : this.criteriaObservableList) {
            if (criterion.getScore() <= SCORE_THRESHOLD) {
                isFormComplete = false;
            }
        }

        this.generateButton.setDisable(!isFormComplete);
    }

    @FXML
    public void handleGenerate() {
        if (AlertUtility.showConfirmationAlert("Confirmar Generación", 
                SystemConstants.CONFIRM_ALERT_TITLE)) {
            try {
                this.generateButton.setDisable(true);
                String enrollmentId = UserController.currentStudentEnrollment;

                LOGGER.log(Level.INFO, "Initiating synchronous write " 
                        + "transaction for self evaluation document generation.");

                StudentDTO student = this.studentDAO
                        .getStudentByEnrollment(enrollmentId);
                ProjectDTO project = this.projectDAO
                        .getProjectById(student.getIdProject());
                
                LinkedOrganizationDTO organization = this.organizationDAO
                        .getLinkedOrganizationById(project
                        .getIdLinkedOrganization());

                SelfEvaluationPDFGenerator.generatePdfFile(student, project, 
                        organization, this.criteriaObservableList);

                String fullPath = SystemConstants
                        .PATH_REPORTS_SELFEVALUATIONS_PREFIX + enrollmentId 
                        + SystemConstants.PATH_REPORTS_EXTENSION_PDF;

                int totalScore = 0;
                for (EvaluationCriterionDTO criterion : this
                        .criteriaObservableList) {
                    totalScore += criterion.getScore();
                }

                SelfEvaluationDTO selfEvaluationData = new SelfEvaluationDTO();
                selfEvaluationData.setEnrollment(enrollmentId);
                selfEvaluationData.setTotalScore(totalScore);
                selfEvaluationData.setFilePath(fullPath);

                this.selfEvaluationDAO.saveSelfEvaluation(selfEvaluationData);

                AlertUtility.showInformationAlert(
                        SystemConstants.ALERT_TITLE_SUCCESS_PROCESS,
                        "La autoevaluación se ha generado correctamente.");
                this.handleCancel();

            } catch (DatabaseSystemException exception) {
                LOGGER.log(Level.SEVERE, "Database core transaction failure " 
                        + "processing self evaluation state data.", exception);
                
                AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                        + "registrar la autoevaluación en el servidor.");
            } catch (IOException exception) {
                LOGGER.log(Level.SEVERE, "Critical physical IO stream crash " 
                        + "generating disk report asset resource.", exception);
                
                AlertUtility.showErrorAlert("Error de Servidor", "Ocurrió un " 
                        + "problema técnico al escribir el archivo en el disco.");
            } finally {
                this.validateCompletion();
            }
        }
    }

    @FXML
    public void handleCancel() {
        LOGGER.log(Level.INFO, "Aborting evaluation configuration session. " 
                + "Routing screen context back to StudentMenuView template.");

        NavigationUtility.navigateTo(this.generateButton, 
                ViewConstants.VIEW_STUDENT_MENU, TITLE_MENU_STUDENT);
    }

    private static class ScoreCellFactory implements Callback<TableColumn<
            EvaluationCriterionDTO, Integer>, TableCell<
            EvaluationCriterionDTO, Integer>> {

        private final GenerateSelfEvaluationViewController controllerReference;

        public ScoreCellFactory(GenerateSelfEvaluationViewController controller) {
            this.controllerReference = controller;
        }

        @Override
        public TableCell<EvaluationCriterionDTO, Integer> call(
                TableColumn<EvaluationCriterionDTO, Integer> column) {
            return new ScoreTableCell(this.controllerReference);
        }
    }

    private static class ScoreTableCell extends TableCell<
            EvaluationCriterionDTO, Integer> {

        private final ChoiceBox<Integer> scoreSelector;

        public ScoreTableCell(GenerateSelfEvaluationViewController controller) {
            this.scoreSelector = new ChoiceBox<>(
                    FXCollections.observableArrayList(
                            SCORE_ONE, SCORE_TWO, SCORE_THREE, 
                            SCORE_FOUR, SCORE_FIVE
                    )
            );
            this.scoreSelector.getSelectionModel().selectedItemProperty()
                    .addListener(new ScoreChangeListener(this, controller));
        }

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                this.setGraphic(null);
            } else {
                this.setGraphic(this.scoreSelector);
            }
        }

        public TableRow<EvaluationCriterionDTO> getCellTableRow() {
            return this.getTableRow();
        }
    }

    private static class ScoreChangeListener implements ChangeListener<Integer> {
        private final ScoreTableCell targetCell;
        private final GenerateSelfEvaluationViewController controllerReference;

        public ScoreChangeListener(ScoreTableCell targetCell, 
                GenerateSelfEvaluationViewController controller) {
            this.targetCell = targetCell;
            this.controllerReference = controller;
        }

        @Override
        public void changed(ObservableValue<? extends Integer> observable, 
                Integer oldValue, Integer newValue) {
            TableRow<EvaluationCriterionDTO> row = this.targetCell
                    .getCellTableRow();
            
            if (row != null && row.getItem() != null) {
                row.getItem().setScore(newValue);
                this.controllerReference.validateCompletion();
            }
        }
    }

    private static class GenerateButtonEventHandler 
            implements EventHandler<ActionEvent> {

        private final GenerateSelfEvaluationViewController controllerReference;

        public GenerateButtonEventHandler(
                GenerateSelfEvaluationViewController controller) {
            this.controllerReference = controller;
        }

        @Override
        public void handle(ActionEvent event) {
            this.controllerReference.handleGenerate();
        }
    }
}