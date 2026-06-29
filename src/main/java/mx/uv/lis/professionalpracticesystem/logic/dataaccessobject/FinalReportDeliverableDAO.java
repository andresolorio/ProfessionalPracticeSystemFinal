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
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.FinalReportDeliverableDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IFinalReportDeliverableDAO;

/**
 * 
 * @author cinth
 * @author andre
 */
public class FinalReportDeliverableDAO implements IFinalReportDeliverableDAO {
    private static final Logger LOGGER = Logger.getLogger(FinalReportDeliverableDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public FinalReportDeliverableDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

    @Override
    public int saveDeliverables(List<FinalReportDeliverableDTO> deliverables, Connection connection) throws DatabaseSystemException {
        
        String query = "INSERT INTO informe_final_entregable (matricula, " 
                + "resultadoEntregable, porcentajeAvance, observaciones) " 
                + "VALUES (?, ?, ?, ?)";
        int rowsAffected = 0;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (FinalReportDeliverableDTO deliverable : deliverables) {
                statement.setString(1, deliverable.getEnrollmentId());
                statement.setString(2, deliverable.getDeliverableResult());
                statement.setInt(3, deliverable.getAdvancePercentage());
                statement.setString(4, deliverable.getObservations());
                statement.addBatch();
            }
            int[] results = statement.executeBatch();
            rowsAffected = results.length;
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Critical failure executing batch insertion for final report deliverables", exception);
            throw new DatabaseSystemException("Error al registrar los entregables finales.", exception);
        }
        return rowsAffected;
    }

    @Override
    public List<FinalReportDeliverableDTO> getDeliverablesByStudentEnrollment(String enrollmentId) throws DatabaseSystemException {
        List<FinalReportDeliverableDTO> deliverablesList = new ArrayList<>();
        String query = "SELECT * FROM informe_final_entregable WHERE matricula = ?";
        
        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, enrollmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    deliverablesList.add(mapResultSetToDTO(resultSet));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database transactional fail querying deliverables for enrollment: " 
                    + enrollmentId, exception);
            throw new DatabaseSystemException("Error tecnico al recuperar entregables.", exception);
        }
        return deliverablesList;
    }

    private FinalReportDeliverableDTO mapResultSetToDTO(ResultSet resultSet) throws SQLException {
        FinalReportDeliverableDTO deliverable = new FinalReportDeliverableDTO();
        deliverable.setIdDeliverable(resultSet.getInt("idEntregable"));
        deliverable.setEnrollmentId(resultSet.getString("matricula"));
        deliverable.setDeliverableResult(resultSet.getString("resultadoEntregable"));
        deliverable.setAdvancePercentage(resultSet.getInt("porcentajeAvance"));
        deliverable.setObservations(resultSet.getString("observaciones"));
        return deliverable;
    }
}