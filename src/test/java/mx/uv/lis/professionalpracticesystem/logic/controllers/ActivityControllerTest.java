package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityDTO;

import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_STRING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EXPECTED_SINGLE_ROW_AFFECTED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.FIRST_ELEMENT_INDEX;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_NAME_LENGTH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MIN_VALID_ID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_ACTIVITY_DESC_NEW;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_ACTIVITY_DESC_TARGET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_ACTIVITY_DESC_UPDATED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_ACTIVITY_NAME_NEW;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_ACTIVITY_NAME_TARGET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_ACTIVITY_NAME_UPDATED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_DAY_THIRTY;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_MAX_HOUR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_MAX_MINUTE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_MAX_SECOND;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_MONTH_JUNE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_NAME_EXCEEDING_LENGTH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_ID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_EVAL_ORG_ID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_YEAR;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @autor andre
 * @author cinth
 */

public class ActivityControllerTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private ActivityController activityController;

@BeforeEach
    public void setUp() throws SQLException {
        this.activityController = new ActivityController();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.executeUpdate("DELETE FROM actividad WHERE idProyecto = " + TEST_PROJECT_ID);
            statement.executeUpdate("DELETE FROM proyecto WHERE idProyecto = " + TEST_PROJECT_ID);
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = " + TEST_EVAL_ORG_ID);
            
            statement.executeUpdate("INSERT INTO OrganizacionVinculada (idOrganizacionVinculada, nombreEmpresa, direccion, telefono, ciudad, email, sector) " +
                                    "VALUES (" + TEST_EVAL_ORG_ID + ", 'JUnit Org', 'Av. Test 123', '2281234567', 'Xalapa', 'junit@test.mx', 'Privado')");
            
            statement.executeUpdate("INSERT INTO Proyecto (idProyecto, nombreProyecto, descripcion, objetivoGeneral, duracion, estado, idOrganizacionVinculada) " +
                                    "VALUES (" + TEST_PROJECT_ID + ", 'Project JUnit', 'Desc', 'Obj', '3 meses', 'Activo', " + TEST_EVAL_ORG_ID + ")");
            statement.executeUpdate("INSERT INTO actividad (nombreActividad, descripcion, fechaEntrega, idProyecto) " +
                                    "VALUES ('" + TEST_ACTIVITY_NAME_TARGET + "', '" + TEST_ACTIVITY_DESC_TARGET + "', '2026-06-30 23:59:59', " + TEST_PROJECT_ID + ")");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM actividad WHERE idProyecto = " + TEST_PROJECT_ID);
            statement.executeUpdate("DELETE FROM proyecto WHERE idProyecto = " + TEST_PROJECT_ID);
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = " + TEST_EVAL_ORG_ID);
        }
    }

    private int fetchGeneratedActivityId() throws SQLException {
        int generatedId = 0;
        String query = "SELECT idActividad FROM actividad WHERE nombreActividad = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, TEST_ACTIVITY_NAME_TARGET);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    generatedId = resultSet.getInt("idActividad");
                }
            }
        }
        return generatedId;
    }

    @Test
    public void testRegisterActivityValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException {
        ActivityDTO activity = new ActivityDTO();
        activity.setActivityName(TEST_ACTIVITY_NAME_NEW);
        activity.setDescription(TEST_ACTIVITY_DESC_NEW);
        activity.setDeliveryDate(Timestamp.valueOf(LocalDateTime.of(TEST_YEAR, TEST_MONTH_JUNE, TEST_DAY_THIRTY, TEST_MAX_HOUR, TEST_MAX_MINUTE, TEST_MAX_SECOND)));
        activity.setIdProject(TEST_PROJECT_ID);
        
        int rowsAffected = activityController.registerActivity(activity);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testRegisterActivityEmptyNameUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ActivityDTO activity = new ActivityDTO();
        activity.setActivityName(EMPTY_STRING);
        activity.setDescription(TEST_ACTIVITY_DESC_NEW);
        activity.setDeliveryDate(Timestamp.valueOf(LocalDateTime.now()));
        activity.setIdProject(TEST_PROJECT_ID);
        
        try {
            activityController.registerActivity(activity);
            fail("Expected ValidationException due to empty activity name");
        } catch (ValidationException exception) {
            assertEquals("Error: El nombre de la actividad es obligatorio.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterActivityExceededNameLengthUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ActivityDTO activity = new ActivityDTO();
        activity.setActivityName(TEST_NAME_EXCEEDING_LENGTH);
        activity.setDescription(TEST_ACTIVITY_DESC_NEW);
        activity.setDeliveryDate(Timestamp.valueOf(LocalDateTime.now()));
        activity.setIdProject(TEST_PROJECT_ID);
        
        try {
            activityController.registerActivity(activity);
            fail("Expected ValidationException due to name length boundary");
        } catch (ValidationException exception) {
            assertEquals("Error: El nombre no puede exceder los " + MAX_NAME_LENGTH + " caracteres.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterActivityEmptyDescriptionUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ActivityDTO activity = new ActivityDTO();
        activity.setActivityName(TEST_ACTIVITY_NAME_NEW);
        activity.setDescription(EMPTY_STRING);
        activity.setDeliveryDate(Timestamp.valueOf(LocalDateTime.now()));
        activity.setIdProject(TEST_PROJECT_ID);
        
        try {
            activityController.registerActivity(activity);
            fail("Expected ValidationException due to empty description");
        } catch (ValidationException exception) {
            assertEquals("Error: La descripcion de la actividad es obligatoria.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterActivityNullDateUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ActivityDTO activity = new ActivityDTO();
        activity.setActivityName(TEST_ACTIVITY_NAME_NEW);
        activity.setDescription(TEST_ACTIVITY_DESC_NEW);
        activity.setDeliveryDate(null);
        activity.setIdProject(TEST_PROJECT_ID);
        
        try {
            activityController.registerActivity(activity);
            fail("Expected ValidationException due to null delivery date");
        } catch (ValidationException exception) {
            assertEquals("Error: La fecha de entrega es obligatoria.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterActivityInvalidProjectIdUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ActivityDTO activity = new ActivityDTO();
        activity.setActivityName(TEST_ACTIVITY_NAME_NEW);
        activity.setDescription(TEST_ACTIVITY_DESC_NEW);
        activity.setDeliveryDate(Timestamp.valueOf(LocalDateTime.now()));
        activity.setIdProject(MIN_VALID_ID);
        
        try {
            activityController.registerActivity(activity);
            fail("Expected ValidationException due to invalid project identifier bound");
        } catch (ValidationException exception) {
            assertEquals("Error: La actividad debe estar asociada a un proyecto valido.", exception.getMessage());
        }
    }

    @Test
    public void testGetActivitiesByProjectValidIdSuccessful() throws DatabaseSystemException {
        List<ActivityDTO> activities = activityController.getActivitiesByProject(TEST_PROJECT_ID);
        assertEquals(TEST_PROJECT_ID, activities.get(FIRST_ELEMENT_INDEX).getIdProject());
    }

    @Test
    public void testGetActivitiesByProjectInvalidProjectIdUnsuccessful() throws DatabaseSystemException {
        try {
            activityController.getActivitiesByProject(MIN_VALID_ID);
            fail("Expected ValidationException due to invalid project identifier boundary");
        } catch (ValidationException exception) {
            assertEquals("Error: El ID del proyecto no es valido.", exception.getMessage());
        }
    }

    @Test
    public void testUpdateActivityInvalidActivityIdUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ActivityDTO activity = new ActivityDTO();
        activity.setIdActivity(MIN_VALID_ID);
        activity.setActivityName(TEST_ACTIVITY_NAME_UPDATED);
        activity.setDescription(TEST_ACTIVITY_DESC_UPDATED);
        activity.setDeliveryDate(Timestamp.valueOf(LocalDateTime.now()));
        activity.setIdProject(TEST_PROJECT_ID);
        
        try {
            activityController.updateActivity(activity);
            fail("Expected ValidationException due to invalid activity identifier bound");
        } catch (ValidationException exception) {
            assertEquals("Error: El ID de la actividad es invalido.", exception.getMessage());
        }
    }

    @Test
    public void testGetActivityByIdExistingIdSuccessful() throws EntityNotFoundException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedActivityId();
        ActivityDTO activity = activityController.getActivityById(targetId);
        assertEquals(TEST_ACTIVITY_NAME_TARGET, activity.getActivityName());
    }

    @Test
    public void testGetActivityByIdInvalidActivityIdUnsuccessful() throws EntityNotFoundException, DatabaseSystemException {
        try {
            activityController.getActivityById(MIN_VALID_ID);
            fail("Expected ValidationException due to invalid activity identifier boundary");
        } catch (ValidationException exception) {
            assertEquals("Error: El ID de la actividad no es valido.", exception.getMessage());
        }
    }
}