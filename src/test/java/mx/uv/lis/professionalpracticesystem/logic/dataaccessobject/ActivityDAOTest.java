package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

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
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.*;

/**
 * @autor andre
 * @author cinth
 */
public class ActivityDAOTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private ActivityDAO activityDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        this.activityDAO = new ActivityDAO(databaseConnection);
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.executeUpdate("DELETE FROM actividad WHERE idProyecto = " + TEST_PROJECT_ID);
            statement.executeUpdate("DELETE FROM Proyecto WHERE idProyecto = " + TEST_PROJECT_ID);
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = " + TEST_EVAL_ORG_ID);

            statement.executeUpdate("INSERT INTO OrganizacionVinculada (idOrganizacionVinculada, nombreEmpresa, direccion, telefono, ciudad, email, sector) " +
                                    "VALUES (" + TEST_EVAL_ORG_ID + ", 'JUnit Org', 'Av. Testing 123', '2281234567', 'Xalapa', 'test@test.mx', 'Privado')");
            
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
            statement.executeUpdate("DELETE FROM Proyecto WHERE idProyecto = " + TEST_PROJECT_ID);
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
        
        int rowsAffected = activityDAO.registerActivity(activity);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testGetActivitiesByIdProjectValidIdSuccessful() throws DatabaseSystemException {
        List<ActivityDTO> activities = activityDAO.getActivitiesByIdProject(TEST_PROJECT_ID);
        assertEquals(TEST_PROJECT_ID, activities.get(FIRST_ELEMENT_INDEX).getIdProject());
    }

    @Test
    public void testGetActivityByIdExistingIdSuccessful() throws EntityNotFoundException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedActivityId();
        ActivityDTO activity = activityDAO.getActivityById(targetId);
        assertEquals(TEST_ACTIVITY_NAME_TARGET, activity.getActivityName());
    }

    @Test
    public void testGetActivityByIdNonExistentIdUnsuccessful() throws DatabaseSystemException {
        try {
            activityDAO.getActivityById(NON_EXISTENT_ID);
            fail("Expected EntityNotFoundException was not thrown");
        } catch (EntityNotFoundException exception) {
            assertEquals("No se encontro la actividad con ID: " + NON_EXISTENT_ID, exception.getMessage());
        }
    }

    @Test
    public void testUpdateActivityValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedActivityId();
        ActivityDTO activity = new ActivityDTO();
        activity.setIdActivity(targetId);
        activity.setActivityName(TEST_ACTIVITY_NAME_UPDATED);
        activity.setDescription(TEST_ACTIVITY_DESC_UPDATED);
        activity.setDeliveryDate(Timestamp.valueOf(LocalDateTime.of(TEST_YEAR, TEST_MONTH_JUNE, TEST_DAY_THIRTY, TEST_MAX_HOUR, TEST_MAX_MINUTE, TEST_MAX_SECOND)));
        
        int rowsAffected = activityDAO.updateActivity(activity);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }
}