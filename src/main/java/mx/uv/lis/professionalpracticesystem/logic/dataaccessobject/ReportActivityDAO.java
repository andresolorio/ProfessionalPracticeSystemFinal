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
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportActivityDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IReportActivityDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.REPORT_STATUS_APPROVED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.FOREIGN_KEY_CHECKS_DISABLE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.FOREIGN_KEY_CHECKS_ENABLE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.VALUE_CONVERSION_TRUE_INT;

/**
 * 
 * @author cinth
 * @author andre
 */
public class ReportActivityDAO implements IReportActivityDAO {
    private static final Logger LOGGER = Logger.getLogger(ReportActivityDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public ReportActivityDAO() {
        this.databaseConnection = new DatabaseConnection();
    }
    
    public ReportActivityDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
 
    private void validateIdentifier(int identifier) {
        if (identifier <= RESET) {
            throw new ValidationException("El identificador numérico proporcionado no puede ser menor o igual a cero.");
        }
    }

    private void validateActivityListFields(
            List<ReportActivityDTO> completedActivities) {
        if (completedActivities == null || completedActivities.isEmpty()) {
            throw new ValidationException("La lista de actividades completadas no puede ser nula o vacía.");
        }
        for (ReportActivityDTO activity : completedActivities) {
            if (activity == null) {
                throw new ValidationException("La lista contiene elementos nulos inconsistentes.");
            }
            validateIdentifier(activity.getReportId());
            validateIdentifier(activity.getActivityId());
        }
    }
    
    private void toggleForeignKeyChecks(Connection connection, int state) 
            throws SQLException {
        String query = "SET FOREIGN_KEY_CHECKS = " + state;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
        }
    }
  
    private int executeBatchRegistration(Connection connection, 
            List<ReportActivityDTO> completedActivities) throws SQLException {
        int rowsAffected = RESET;
        String query = "INSERT INTO reporte_actividad (idReporte, idActividad, completado) VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE completado = 1";
                     
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (ReportActivityDTO activity : completedActivities) {
                statement.setInt(1, activity.getReportId());
                statement.setInt(2, activity.getActivityId());
                statement.addBatch();
            }
            int[] batchResults = statement.executeBatch();
            rowsAffected = batchResults.length;
        }
        return rowsAffected;
    }
    
    @Override
    public int registerCompletedActivities(List<ReportActivityDTO> completedActivities) throws DatabaseSystemException {
        validateActivityListFields(completedActivities);

        int rowsAffected = SystemConstants.RESET;

        try (Connection connection = databaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            
            toggleForeignKeyChecks(connection, FOREIGN_KEY_CHECKS_DISABLE);
            rowsAffected = executeBatchRegistration(connection, completedActivities);
            toggleForeignKeyChecks(connection, FOREIGN_KEY_CHECKS_ENABLE);

            connection.commit();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Critical SQLException crash processing transactional activity batch", exception);
            throw new DatabaseSystemException("Error técnico al guardar el progreso de actividades del reporte.", exception);
        }
        return rowsAffected;
    }
    
    @Override
    public List<ReportActivityDTO> getChecklistByReport(int reportId, int projectId) throws DatabaseSystemException {
        this.validateIdentifier(reportId);
        this.validateIdentifier(projectId);

        List<ReportActivityDTO> checklist = new ArrayList<>();
        
        String query = "SELECT a.idActividad, a.nombreActividad, "
                + "IF(ra.idReporte IS NOT NULL, 1, 0) AS estaCompletado, "
                + "IF((SELECT COUNT(*) FROM reporte_actividad ra2 "
                + "INNER JOIN reporte r2 ON ra2.idReporte = r2.idReporte "
                + "WHERE ra2.idActividad = a.idActividad "
                + "AND r2.estadoRevision = '" + REPORT_STATUS_APPROVED + "' "
                + "AND r2.idReporte != ?) > 0, 1, 0) AS yaAprobadoPasado "
                + "FROM actividad a "
                + "LEFT JOIN reporte_actividad ra ON a.idActividad = ra.idActividad "
                + "AND ra.idReporte = ? WHERE a.idProyecto = ?";

        try (Connection connection = this.databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, reportId);
            statement.setInt(2, reportId);
            statement.setInt(3, projectId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ReportActivityDTO reportActivity = new ReportActivityDTO();
                    reportActivity.setReportId(reportId);
                    reportActivity.setActivityId(resultSet.getInt("idActividad"));
                    reportActivity.setActivityName(
                            resultSet.getString("nombreActividad"));

                    boolean isCurrentCompleted = resultSet.getInt("estaCompletado") == VALUE_CONVERSION_TRUE_INT;
                    boolean isPastApproved = resultSet.getInt("yaAprobadoPasado") == VALUE_CONVERSION_TRUE_INT;

                    reportActivity.setCompleted(isCurrentCompleted || isPastApproved);
                    reportActivity.setAlreadyApprovedPast(isPastApproved);

                    checklist.add(reportActivity);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed context extraction querying cumulative dataset checklist for reportId: " + reportId, exception);
            throw new DatabaseSystemException("Error al mantener la consistencia del catálogo de actividades.", exception);
        }
        return checklist;
    }
}