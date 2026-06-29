package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.SelfEvaluationDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.ISelfEvaluationDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;

/**
 * 
 * @author andre
 * @author cinth
 */
public class SelfEvaluationDAO implements ISelfEvaluationDAO {
    private static final Logger LOGGER = Logger.getLogger(SelfEvaluationDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public SelfEvaluationDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

    public SelfEvaluationDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    private int executeTransactionalStatements(Connection connection, 
            SelfEvaluationDTO selfEvaluationInformation) throws SQLException {
        int rowsAffected = RESET;
        String insertQuery = "INSERT INTO Autoevaluacion (matricula, "
                + "puntajeTotal, archivoAutoevaluacionPDF) VALUES (?, ?, ?)";

        try (PreparedStatement insertStatement = connection
                .prepareStatement(insertQuery)) {

            insertStatement.setString(1, selfEvaluationInformation
                    .getEnrollment());
            insertStatement.setInt(2, selfEvaluationInformation
                    .getTotalScore());
            insertStatement.setString(3, selfEvaluationInformation
                    .getFilePath());
            
            rowsAffected = insertStatement.executeUpdate();
        }
        return rowsAffected;
    }

    @Override
    public void saveSelfEvaluation(SelfEvaluationDTO selfEvaluationData) throws DatabaseSystemException {
        if (selfEvaluationData != null) {
            Validator.checkEnrollmentFormat(selfEvaluationData.getEnrollment());
        }

        try (Connection connection = this.databaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            
            try {
                this.executeTransactionalStatements(connection, selfEvaluationData);
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.log(Level.SEVERE, "Transaction failure executing nested self evaluation batch statements", exception);
                throw new DatabaseSystemException("Error técnico al procesar la transacción de la autoevaluación.", exception);
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database connection link dropped during self evaluation persistence sequence", exception);
            throw new DatabaseSystemException("Error de red: No se pudo conectar al repositorio para guardar.", exception);
        }
    }

    @Override
    public SelfEvaluationDTO getSelfEvaluationByEnrollment(String enrollmentId) throws DatabaseSystemException {
        Validator.checkEnrollmentFormat(enrollmentId);
        SelfEvaluationDTO selfEvaluationResult = new SelfEvaluationDTO();
        String query = "SELECT * FROM Autoevaluacion WHERE matricula = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, enrollmentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    selfEvaluationResult = mapResultSetToDTO(resultSet);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error al recuperar autoevaluación por matrícula: " + enrollmentId, exception);
            throw new DatabaseSystemException("Error técnico al recuperar la información de la autoevaluación.", exception);
        }
        return selfEvaluationResult;
    }

    private SelfEvaluationDTO mapResultSetToDTO(ResultSet resultSet) throws SQLException {
        SelfEvaluationDTO selfEvaluation = new SelfEvaluationDTO();
        selfEvaluation.setIdSelfEvaluation(resultSet.getInt("idAutoevaluacion"));
        selfEvaluation.setEnrollment(resultSet.getString("matricula"));
        selfEvaluation.setTotalScore(resultSet.getInt("puntajeTotal"));
        selfEvaluation.setFilePath(resultSet.getString("archivoAutoevaluacionPDF"));    
        if (resultSet.getTimestamp("fechaSubida") != null) {
            selfEvaluation.setUploadDate(resultSet.getTimestamp("fechaSubida").toLocalDateTime());
        }
        return selfEvaluation;
    }
}