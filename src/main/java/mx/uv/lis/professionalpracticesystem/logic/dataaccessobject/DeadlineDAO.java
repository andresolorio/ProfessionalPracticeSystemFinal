package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DeadlineDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IDeadlineDAO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.SUCCESS;

/**
 * 
 * @author cinth
 * @author andre
 */
public class DeadlineDAO implements IDeadlineDAO {

    private static final Logger LOGGER = Logger.getLogger(DeadlineDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public DeadlineDAO() {
        this.databaseConnection = new DatabaseConnection();
    }
    

    @Override
    public int saveOrUpdateDeadline(DeadlineDTO deadline)
            throws DatabaseSystemException {
        int rowsAffected = SUCCESS;
        String query = "INSERT INTO plazoentrega (nrc, tipoReporte, numeroInforme, "
                + "fechaLimite, fechaActualizacion, numeroPersonalProfesor) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = this.databaseConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, deadline.getNrc());
            preparedStatement.setString(2, deadline.getReportType());

            int reportNumber = deadline.getReportedNumber();
            if (reportNumber <= SUCCESS) {
                reportNumber = 1;
            }
            preparedStatement.setInt(3, reportNumber);
            preparedStatement.setTimestamp(4, Timestamp.valueOf(deadline.getDeadlineDate()));
            preparedStatement.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));
            preparedStatement.setString(6, deadline.getStaffNumber());

            rowsAffected = preparedStatement.executeUpdate();
            LOGGER.log(Level.INFO, "SQL insert execution context finalized. Rows: {0}", rowsAffected);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database transaction failure inside clean insert pipeline", exception);
            throw new DatabaseSystemException("Error técnico: El plazo de entrega "
                    + "ya se encuentra registrado o el servidor rechazó la solicitud.", exception);
        }
        return rowsAffected;
    }
 
    @Override
    public DeadlineDTO getDeadlineByReportType(String reportType) throws DatabaseSystemException {
        DeadlineDTO deadline = null;
        String query = "SELECT idPlazo, nrc, tipoReporte, numeroInforme, fechaLimite, "
                + "fechaActualizacion FROM plazoentrega WHERE tipoReporte = ?";

        try (Connection connection = this.databaseConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, reportType);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    deadline = this.mapResultSetToDTO(resultSet);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database execution failure matching query token: " + reportType, exception);
            throw new DatabaseSystemException("Error técnico al recuperar la fecha límite del reporte.", exception);
        }
        return deadline;
    }

    @Override
    public List<DeadlineDTO> getDeadlinesByStudentEnrollment(String enrollmentId) throws DatabaseSystemException {
        List<DeadlineDTO> deadlines = new ArrayList<>();
        String query = "SELECT p.idPlazo, p.nrc, p.tipoReporte, p.numeroInforme, "
                + "p.fechaLimite, p.fechaActualizacion FROM plazoentrega p "
                + "INNER JOIN Alumno a ON a.nrc = p.nrc WHERE a.matricula = ?";

        try (Connection connection = this.databaseConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, enrollmentId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    deadlines.add(this.mapResultSetToDTO(resultSet));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database extraction failure for target student enrollment: " + enrollmentId, exception);
            throw new DatabaseSystemException("Error técnico al recuperar los plazos de entrega "
                    + "asignados a su Experiencia Educativa.", exception);
        }
        return deadlines;
    }

    @Override
    public List<DeadlineDTO> getDeadlinesByProfessorStaffNumber(String staffNumber)
            throws DatabaseSystemException {
        List<DeadlineDTO> deadlines = new ArrayList<>();
        String query = "SELECT idPlazo, nrc, tipoReporte, numeroInforme, fechaLimite, "
                + "fechaActualizacion FROM plazoentrega WHERE numeroPersonalProfesor = ? "
                + "ORDER BY nrc ASC, numeroInforme ASC";

        try (Connection connection = this.databaseConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, staffNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    deadlines.add(this.mapResultSetToDTO(resultSet));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database extraction failure for professor staff key token: " + staffNumber, exception);
            throw new DatabaseSystemException("Error técnico al recuperar sus plazos de entrega configurados.", exception);
        }
        return deadlines;
    }

    private DeadlineDTO mapResultSetToDTO(ResultSet resultSet) throws SQLException {
        DeadlineDTO deadline = new DeadlineDTO();
        deadline.setIdDeadline(resultSet.getInt("idPlazo"));
        deadline.setNrc(resultSet.getInt("nrc"));
        deadline.setReportType(resultSet.getString("tipoReporte"));
        deadline.setReportedNumber(resultSet.getInt("numeroInforme"));

        Timestamp sqlDeadlineDate = resultSet.getTimestamp("fechaLimite");
        if (sqlDeadlineDate != null) {
            deadline.setDeadlineDate(sqlDeadlineDate.toLocalDateTime());
        }

        Timestamp sqlLastUpdate = resultSet.getTimestamp("fechaActualizacion");
        if (sqlLastUpdate != null) {
            deadline.setLastUpdate(sqlLastUpdate.toLocalDateTime());
        }

        return deadline;
    }
}
