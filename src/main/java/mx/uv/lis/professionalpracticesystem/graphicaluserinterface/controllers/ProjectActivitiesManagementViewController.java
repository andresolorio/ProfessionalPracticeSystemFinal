package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ActivityDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;

/**
 *
 * @author andre
 * @author cinth
 */
public class ProjectActivitiesManagementViewController 
        implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            ProjectActivitiesManagementViewController.class.getName());

    @FXML
    private ComboBox<ProjectDTO> projectComboBox;
    @FXML
    private TextField activityNameTextField;
    @FXML
    private DatePicker deadlineDatePicker;
    @FXML
    private TableView<ActivityDTO> activitiesTableView;
    @FXML
    private TableColumn<ActivityDTO, String> nameTableColumn;
    @FXML
    private TableColumn<ActivityDTO, LocalDate> dateTableColumn;
    @FXML
    private TableColumn<ActivityDTO, Void> actionTableColumn;
    @FXML
    private Button cancelButton;

    private final ObservableList<ActivityDTO> localActivitiesList;
    private ProjectDAO projectDAO;
    private ActivityDAO activityDAO;

    public ProjectActivitiesManagementViewController() {
        this.localActivitiesList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.projectDAO = new ProjectDAO();
        this.activityDAO = new ActivityDAO();
        
        this.configureTableColumns();
        this.loadAvailableProjects();
    }

    private void configureTableColumns() {
        this.nameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("activityName"));
        this.dateTableColumn.setCellValueFactory(
                new ActivityDateCellValueFactory());
        this.actionTableColumn.setCellFactory(
                new ActionCellFactory(this.localActivitiesList));
        this.activitiesTableView.setItems(this.localActivitiesList);
    }

    private void loadAvailableProjects() {
        try {
            LOGGER.log(Level.INFO, "Fetching available projects synchronously " 
                    + "from the database repository.");
            
            List<ProjectDTO> activeProjects = this.projectDAO
                    .getAllAvailableProjects();
            
            ObservableList<ProjectDTO> projectsList = FXCollections
                    .observableArrayList(activeProjects);
            this.projectComboBox.setItems(projectsList);

            this.projectComboBox.setCellFactory(new ProjectCellFactory());
            this.projectComboBox.setButtonCell(new ProjectListCell());
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server infrastructure data pipeline " 
                    + "failure while populating projects catalog.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar el catálogo de proyectos desde el servidor.");
        }
    }

    @FXML
    private void handleAddActivityToList(ActionEvent event) {
        String activityName = "";
        if (this.activityNameTextField.getText() != null) {
            activityName = this.activityNameTextField.getText().trim();
        }
        LocalDate selectedDate = this.deadlineDatePicker.getValue();

        if (activityName.isEmpty() || selectedDate == null) {
            AlertUtility.showErrorAlert("Campos Incompletos", "Por favor, " 
                    + "defina la descripción de la actividad y su fecha límite.");
            return;
        }

        if (selectedDate.isBefore(SystemConstants.FEB_JUL_START_DATE) 
                || selectedDate.isAfter(SystemConstants.FEB_JUL_END_DATE)) {
            AlertUtility.showErrorAlert("Fecha Fuera de Período", 
                    "La fecha límite de la actividad debe pertenecer " 
                    + "estrictamente al período escolar regular actual " 
                    + "(" + SystemConstants.DEFAULT_PERIOD + ").");
            return;
        }

        ActivityDTO activity = new ActivityDTO();
        activity.setActivityName(activityName);
        activity.setDescription("Actividad específica obligatoria.");
        activity.setDeliveryDate(Timestamp.valueOf(
                selectedDate.atStartOfDay()));

        this.localActivitiesList.add(activity);
        this.activityNameTextField.clear();
        this.deadlineDatePicker.setValue(null);
    }

    @FXML
    private void handleSaveAllActivities(ActionEvent event) {
        ProjectDTO selectedProject = this.projectComboBox.getValue();

        if (selectedProject == null) {
            AlertUtility.showErrorAlert("Proyecto Requerido", "Debe " 
                    + "seleccionar un proyecto de la lista para asociar.");
            return;
        }

        if (this.localActivitiesList.isEmpty()) {
            AlertUtility.showErrorAlert("Tabla Vacía", "Debe añadir al " 
                    + "menos una actividad para poder guardar el registro.");
            return;
        }

        try {
            LOGGER.log(Level.INFO, "Initiating batch processing transaction " 
                    + "for projects activities records submission stream.");
            
            int registeredCount = 0;
            for (ActivityDTO activity : this.localActivitiesList) {
                activity.setIdProject(selectedProject.getIdProject());
                this.activityDAO.registerActivity(activity);
                registeredCount++;
            }

            LOGGER.log(Level.INFO, "Successfully committed {0} activities " 
                    + "for project ID: {1}", new Object[]{registeredCount, 
                    selectedProject.getIdProject()});
            
            AlertUtility.showInformationAlert("Registro Exitoso", "Se han " 
                    + "guardado correctamente las actividades de proyecto.");
            this.navigateToMenuCoordinador();

        } catch (DataIntegrityException exception) {
            LOGGER.log(Level.WARNING, "Uniqueness key constraint violation " 
                    + "inserting project activities dataset batch.", exception);
            AlertUtility.showErrorAlert("Error de Integridad", 
                    exception.getMessage());
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server transaction engineering error " 
                    + "processing batch activity deployment pipeline.", 
                    exception);
            AlertUtility.showErrorAlert("Error de Servidor", "Fallo de " 
                    + "infraestructura al conectar con el servidor central.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        this.navigateToMenuCoordinador();
    }

    private void navigateToMenuCoordinador() {
        LOGGER.log(Level.INFO, "Aborting current operation layout workspace. " 
                + "Routing screen context back to CoordinatorMenuView.");
        
        NavigationUtility.navigateTo(this.cancelButton, 
                ViewConstants.VIEW_COORDINATOR_MENU, 
                SystemConstants.TITLE_COORDINATOR_MENU);
    }

    private static class ActivityDateCellValueFactory 
            implements Callback<CellDataFeatures<ActivityDTO, LocalDate>, 
            ObservableValue<LocalDate>> {
        @Override
        public ObservableValue<LocalDate> call(
                CellDataFeatures<ActivityDTO, LocalDate> cellData) {
            Timestamp timestamp = cellData.getValue().getDeliveryDate();
            LocalDate localDate = null;
            if (timestamp != null) {
                localDate = timestamp.toLocalDateTime().toLocalDate();
            }
            return new SimpleObjectProperty<>(localDate);
        }
    }

    private static class ActionCellFactory 
            implements Callback<TableColumn<ActivityDTO, Void>, 
            TableCell<ActivityDTO, Void>> {
        private final ObservableList<ActivityDTO> localList;

        public ActionCellFactory(ObservableList<ActivityDTO> list) {
            this.localList = list;
        }

        @Override
        public TableCell<ActivityDTO, Void> call(
                TableColumn<ActivityDTO, Void> column) {
            return new ActionTableCell(this.localList);
        }
    }

    private static class ActionTableCell extends TableCell<ActivityDTO, Void> {
        private final Button removeButton = new Button("Quitar");
        private final ObservableList<ActivityDTO> localList;

        public ActionTableCell(ObservableList<ActivityDTO> list) {
            this.localList = list;
            this.removeButton.setStyle("-fx-background-color: #dc3545; " 
                    + "-fx-text-fill: white; -fx-background-radius: 3; " 
                    + "-fx-cursor: hand; -fx-font-size: 11;");
            this.removeButton.setOnAction(
                    new RemoveButtonEventHandler(this, this.localList));
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                this.setGraphic(null);
            } else {
                this.setGraphic(this.removeButton);
            }
        }
    }

    private static class RemoveButtonEventHandler 
            implements EventHandler<ActionEvent> {
        private final ActionTableCell cell;
        private final ObservableList<ActivityDTO> localList;

        public RemoveButtonEventHandler(ActionTableCell cell, 
                ObservableList<ActivityDTO> list) {
            this.cell = cell;
            this.localList = list;
        }

        @Override
        public void handle(ActionEvent event) {
            ActivityDTO activity = this.cell.getTableView().getItems()
                    .get(this.cell.getIndex());
            this.localList.remove(activity);
        }
    }

    private static class ProjectCellFactory 
            implements Callback<ListView<ProjectDTO>, ListCell<ProjectDTO>> {
        @Override
        public ListCell<ProjectDTO> call(ListView<ProjectDTO> projectListView) {
            return new ProjectListCell();
        }
    }

    private static class ProjectListCell extends ListCell<ProjectDTO> {
        @Override
        protected void updateItem(ProjectDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                this.setText(null);
            } else {
                this.setText(item.getProjectName());
            }
        }
    }
}