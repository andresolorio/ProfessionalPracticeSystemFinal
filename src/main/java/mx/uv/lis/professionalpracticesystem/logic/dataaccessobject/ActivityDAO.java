package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IActivityDAO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MYSQL_DUPLICATE_KEY_ERROR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.SUCCESS;

/**
 * 
 * @author cinth
 * @author andre
 */
public class ActivityDAO implements IActivityDAO {
    private static final Logger LOGGER = Logger.getLogger(ActivityDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public ActivityDAO() {
        this.databaseConnection = new DatabaseConnection();
    }
    
    public ActivityDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public int registerActivity(ActivityDTO activity) throws DataIntegrityException, DatabaseSystemException {
        int result = SUCCESS;
        String query = "INSERT INTO actividad (nombreActividad, descripcion, fechaEntrega, idProyecto) VALUES (?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, activity.getActivityName());
            statement.setString(2, activity.getDescription());
            statement.setTimestamp(3, activity.getDeliveryDate());
            statement.setInt(4, activity.getIdProject());

            result = statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error registering activity context data", exception);
            if (exception.getErrorCode() == MYSQL_DUPLICATE_KEY_ERROR) {
                throw new DataIntegrityException("Error: La actividad ya se encuentra registrada.", exception);
            }
            throw new DatabaseSystemException("Error tecnico al registrar la actividad.", exception);
        }
        return result;
    }

    @Override
    public List<ActivityDTO> getActivitiesByIdProject(int idProject) throws DatabaseSystemException {
        List<ActivityDTO> activities = new ArrayList<>();
        String query = "SELECT * FROM actividad WHERE idProyecto = ?";

        try (Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, idProject);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    activities.add(mapResultSetToDTO(resultSet));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error retrieving activities collections", exception);
            throw new DatabaseSystemException("Error al consultar las actividades del proyecto.", exception);
        }
        return activities;
    }

    @Override
    public ActivityDTO getActivityById(int idActivity) throws EntityNotFoundException, DatabaseSystemException {
        ActivityDTO activity = null;
        String query = "SELECT * FROM actividad WHERE idActividad = ?";

        try (Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, idActivity);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    activity = mapResultSetToDTO(resultSet);
                } else {
                    throw new EntityNotFoundException("No se encontro la actividad con ID: " + idActivity);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error retrieving unique activity context", exception);
            throw new DatabaseSystemException("Error tecnico al recuperar la actividad.", exception);
        }
        return activity;
    }

    @Override
    public int updateActivity(ActivityDTO activity) throws DataIntegrityException, DatabaseSystemException {
        int result = SUCCESS;
        String query = "UPDATE actividad SET nombreActividad = ?, descripcion = ?, fechaEntrega = ? WHERE idActividad = ?";

        try (Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, activity.getActivityName());
            statement.setString(2, activity.getDescription());
            statement.setTimestamp(3, activity.getDeliveryDate());
            statement.setInt(4, activity.getIdActivity());

            result = statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error updating target activity row", exception);
            if (exception.getErrorCode() == MYSQL_DUPLICATE_KEY_ERROR) {
                throw new DataIntegrityException("Error de integridad al actualizar la actividad.", exception);
            }
            throw new DatabaseSystemException("Fallo tecnico al intentar actualizar la actividad.", exception);
        }
        return result;
    }

    private ActivityDTO mapResultSetToDTO(ResultSet resultSet) throws SQLException {
        ActivityDTO activity = new ActivityDTO();
        activity.setIdActivity(resultSet.getInt("idActividad"));
        activity.setActivityName(resultSet.getString("nombreActividad"));
        activity.setDescription(resultSet.getString("descripcion"));
        activity.setDeliveryDate(resultSet.getTimestamp("fechaEntrega"));
        activity.setIdProject(resultSet.getInt("idProyecto"));
        return activity;
    }
}