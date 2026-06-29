package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.EducativeExperienceDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EducativeExperienceDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_STRING;

/**
 *
 * @author andre
 * @author cinth
 */
public class EducativeExperienceAssignmentViewController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(
            EducativeExperienceAssignmentViewController.class.getName());

    @FXML
    private ComboBox<StudentDTO> studentComboBox;
    @FXML
    private ComboBox<EducativeExperienceDTO> eeComboBox;
    @FXML
    private Label professorNameDynamicLabel;
    @FXML
    private Label sectionNameDynamicLabel;
    @FXML
    private Button assignButton;
    @FXML
    private Button cancelButton;

    private final StudentDAO studentDAO = new StudentDAO();
    private final EducativeExperienceDAO educativeExperienceDAO = 
            new EducativeExperienceDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.setupComboBoxConverters();
        this.setupEducativeExperienceComboBoxCellFactory();
        this.loadInitialData();

        this.eeComboBox.getSelectionModel().selectedItemProperty()
                .addListener(new EducativeExperienceSelectionListener(this));
    }

    public void loadInitialData() {
        try {
            LOGGER.log(Level.INFO, "Fetching background academic catalogs " 
                    + "synchronously from the server database context.");

            List<StudentDTO> allStudents = this.studentDAO.getAllActiveStudents();
            List<StudentDTO> unassignedStudents = new ArrayList<>();

            for (StudentDTO student : allStudents) {
                if (student.getNrc() <= SystemConstants.RESET) {
                    unassignedStudents.add(student);
                }
            }

            List<EducativeExperienceDTO> availableEducativeExperiences = this
                    .educativeExperienceDAO
                    .getAllEducativeExperiencesWithProfessors();

            this.handleLoadCatalogsSuccess(unassignedStudents, 
                    availableEducativeExperiences);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication pipeline failure " 
                    + "fetching background academic catalogs context.", 
                    exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se " 
                    + "pudieron recuperar los catálogos académicos desde " 
                    + "el servidor.");
        }
    }

    public void handleLoadCatalogsSuccess(List<StudentDTO> unassignedStudents, 
            List<EducativeExperienceDTO> availableEducativeExperiences) {
        this.studentComboBox.setItems(FXCollections
                .observableArrayList(unassignedStudents));
        this.eeComboBox.setItems(FXCollections
                .observableArrayList(availableEducativeExperiences));
        LOGGER.log(Level.INFO, "Initial academic catalogs successfully " 
                + "loaded and synchronized in core view context.");
    }

    public void updateAcademicDetailsPanel(
            EducativeExperienceDTO selectedEducativeExperince) {
        if (selectedEducativeExperince != null) {
            if (selectedEducativeExperince.getProfessorName() != null) {
                this.professorNameDynamicLabel.setText(
                        selectedEducativeExperince.getProfessorName());
            } else {
                this.professorNameDynamicLabel.setText(
                        SystemConstants.LABEL_NOT_ASSIGNED);
            }

            if (selectedEducativeExperince.getSection() != null) {
                this.sectionNameDynamicLabel.setText(
                        selectedEducativeExperince.getSection());
            } else {
                this.sectionNameDynamicLabel.setText(
                        SystemConstants.LABEL_NOT_DEFINED);
            }
        } else {
            this.professorNameDynamicLabel.setText(
                    SystemConstants.LABEL_LOADING_PREFIX);
            this.sectionNameDynamicLabel.setText(
                    SystemConstants.LABEL_LOADING_PREFIX);
        }
    }

    private void setupComboBoxConverters() {
        this.studentComboBox.setConverter(new StudentStringConverter());
        this.eeComboBox.setConverter(new EducativeExperienceStringConverter());
    }

    private void setupEducativeExperienceComboBoxCellFactory() {
        this.eeComboBox.setCellFactory(new EducativeExperienceCellFactory());
        this.eeComboBox.setButtonCell(new EducativeExperienceListCell());
    }

    @FXML
    private void handleAssign(ActionEvent event) {
        StudentDTO selectedStudent = this.studentComboBox.getSelectionModel()
                .getSelectedItem();
        EducativeExperienceDTO selectedEducativeExperince = this.eeComboBox
                .getSelectionModel().getSelectedItem();

        if (selectedStudent == null || selectedEducativeExperince == null) {
            AlertUtility.showWarningAlert("Selección Requerida", "Debe " 
                    + "seleccionar un alumno y un NRC válido para proceder.");
            return;
        }

        if (!SystemConstants.STATUS_ACTIVE.equalsIgnoreCase(
                selectedStudent.getStatus())) {
            AlertUtility.showWarningAlert("Restricción Administrativa", 
                    "No se puede inscribir al alumno " 
                    + selectedStudent.getFirstName() + " " 
                    + selectedStudent.getPaternalLastName() 
                    + " debido a que su estado actual en el sistema es: " 
                    + selectedStudent.getStatus() + ".");
            return;
        }

        try {
            this.assignButton.setDisable(true);
            LOGGER.log(Level.INFO, "Executing synchronous assignment write " 
                    + "transaction for student enrollment link pipeline.");

            int rowsAffected = this.studentDAO
                    .assignExperienciaEducativaToStudent(
                    selectedStudent.getEnrollmentId(), 
                    selectedEducativeExperince.getNrc());
            
            boolean isSuccess = rowsAffected > SystemConstants.RESET;
            this.handleAssignSuccess(isSuccess, selectedStudent, 
                    selectedEducativeExperince);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server write transaction failure " 
                    + "inside EE student allocation wizard context.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "registrar la inscripción en el servidor central.");
        } finally {
            this.assignButton.setDisable(false);
        }
    }

    public void handleAssignSuccess(boolean isSuccess, StudentDTO selectedStudent, 
            EducativeExperienceDTO selectedEducativeExperince) {
        if (isSuccess) {
            AlertUtility.showInformationAlert("Asignación Académica Exitosa",
                    "El alumno " + selectedStudent.getFirstName() 
                    + " ha sido inscrito oficialmente en el NRC " 
                    + selectedEducativeExperince.getNrc() + ".");
            this.handleCancel(null);
        } else {
            AlertUtility.showWarningAlert("Fallo en Operación", "No se pudo " 
                    + "actualizar el registro académico del estudiante.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting assignment rules configuration. " 
                + "Routing view context screen back to CoordinatorMenuView.");

        NavigationUtility.navigateTo(this.cancelButton, 
                ViewConstants.VIEW_COORDINATOR_MENU, 
                SystemConstants.TITLE_COORDINATOR_MENU_ASSIGNMENT);
    }

    private static class EducativeExperienceSelectionListener 
            implements ChangeListener<EducativeExperienceDTO> {

        private final EducativeExperienceAssignmentViewController 
                controllerReference;

        public EducativeExperienceSelectionListener(
                EducativeExperienceAssignmentViewController controller) {
            this.controllerReference = controller;
        }

        @Override
        public void changed(ObservableValue<? extends EducativeExperienceDTO> 
                observable, EducativeExperienceDTO oldEducativeExperience, 
                EducativeExperienceDTO selectedEducativeExperience) {
            this.controllerReference.updateAcademicDetailsPanel(
                    selectedEducativeExperience);
        }
    }

    private static class StudentStringConverter 
            extends StringConverter<StudentDTO> {

        @Override
        public String toString(StudentDTO student) {
            String output = EMPTY_STRING;
            if (student != null) {
                output = student.getEnrollmentId() 
                        + SystemConstants.SEPARATOR_LABEL
                        + student.getFirstName() + " " 
                        + student.getPaternalLastName();
            }
            return output;
        }

        @Override
        public StudentDTO fromString(String string) {
            return null;
        }
    }

    private static class EducativeExperienceStringConverter 
            extends StringConverter<EducativeExperienceDTO> {

        @Override
        public String toString(EducativeExperienceDTO educativeexperience) {
            String output = EMPTY_STRING;
            if (educativeexperience != null) {
                output = SystemConstants.NRC_PREFIX_LABEL 
                        + educativeexperience.getNrc()
                        + SystemConstants.SEPARATOR_LABEL 
                        + educativeexperience.getEducativeExperienceName();
            }
            return output;
        }

        @Override
        public EducativeExperienceDTO fromString(String string) {
            return null;
        }
    }

    private static class EducativeExperienceCellFactory 
            implements Callback<ListView<EducativeExperienceDTO>, 
            ListCell<EducativeExperienceDTO>> {

        @Override
        public ListCell<EducativeExperienceDTO> call(
                ListView<EducativeExperienceDTO> parentListView) {
            return new EducativeExperienceListCell();
        }
    }

    private static class EducativeExperienceListCell 
            extends ListCell<EducativeExperienceDTO> {

        @Override
        protected void updateItem(EducativeExperienceDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                this.setText(null);
                this.setDisable(false);
                this.setStyle(EMPTY_STRING);
            } else {
                this.setText(SystemConstants.NRC_PREFIX_LABEL + item.getNrc()
                        + SystemConstants.SEPARATOR_LABEL 
                        + item.getEducativeExperienceName());
                this.setDisable(false);
                this.setStyle(EMPTY_STRING);
            }
        }
    }
}