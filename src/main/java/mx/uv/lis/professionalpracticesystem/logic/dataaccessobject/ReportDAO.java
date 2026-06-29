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
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IReportDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MYSQL_DUPLICATE_KEY_ERROR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.REPORT_STATUS_APPROVED;

/**
 *
 * @author andre
 * @author cinth
 */
public class ReportDAO implements IReportDAO {

    private static final Logger LOGGER = Logger.getLogger(ReportDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public ReportDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

    public ReportDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public int saveReport(ReportDTO report) throws DataIntegrityException, DatabaseSystemException {
        int result = RESET;
        String query = "INSERT INTO reporte (matricula, tipoReporte, "
                + "numeroInforme, horasCubiertas, fechaReporte, estadoEntrega, "
                + "estadoRevision, observaciones, archivoReportePDF) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = this.databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            int reportNumber = report.getReportedHours();
            if (reportNumber <= RESET) {
                reportNumber = SystemConstants.DEFAULT_REPORT_NUMBER;
            }

            statement.setString(1, report.getStudentEnrollment());
            statement.setString(2, report.getReportType());
            statement.setInt(3, reportNumber);
            statement.setInt(4, report.getHoursCovered());
            statement.setDate(5, report.getDeliveryDate());
            statement.setString(6, report.getDeliveryStatus());
            statement.setString(7, report.getReviewStatus());
            statement.setString(8, report.getObservations());
            statement.setBytes(9, report.getFileContent());

            result = statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database persist infrastructure failure "
                    + "saving unified student report entry", exception);
            if (exception.getErrorCode() == MYSQL_DUPLICATE_KEY_ERROR) {
                throw new DataIntegrityException("Error: Este reporte "
                        + "periódico o hito ya se encuentra registrado "
                        + "en el sistema.", exception);
            }
            throw new DatabaseSystemException("Error técnico al registrar el reporte en la base de datos.", exception);
        }
        return result;
    }

    @Override
    public List<ReportDTO> getReportsByEnrollment(String enrollmentId) throws DatabaseSystemException {
        List<ReportDTO> reports = new ArrayList<>();
        String query = "SELECT idReporte, matricula, tipoReporte, "
                + "numeroInforme, horasCubiertas, fechaReporte, estadoEntrega, "
                + "estadoRevision, observaciones, archivoReportePDF FROM reporte "
                + "WHERE matricula = ? ORDER BY numeroInforme ASC";

        try (Connection connection = this.databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, enrollmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reports.add(this.mapResultSetToDTO(resultSet));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "SQL transactional query crash retrieving "
                    + "reports portfolio for enrollment identifier: "
                    + enrollmentId, exception);
            throw new DatabaseSystemException("Error al consultar el "
                    + "historico de reportes por matrícula.", exception);
        }
        return reports;
    }

    @Override
    public ReportDTO getReportById(int idReport) throws EntityNotFoundException, DatabaseSystemException {
        ReportDTO report = null;
        String query = "SELECT idReporte, matricula, tipoReporte, "
                + "numeroInforme, horasCubiertas, fechaReporte, estadoEntrega, "
                + "estadoRevision, observaciones, archivoReportePDF FROM reporte "
                + "WHERE idReporte = ?";

        try (Connection connection = this.databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, idReport);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    report = this.mapResultSetToDTO(resultSet);
                } else {
                    throw new EntityNotFoundException("No se encontró el reporte solicitado con ID: " + idReport);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "SQL data retrieval crash searching report by primary key token identifier: " + idReport,
                    exception);
            throw new DatabaseSystemException("Error técnico al recuperar el documento desde la base de datos.", exception);
        }
        return report;
    }

    @Override
    public List<ReportDTO> getReportsByProfessorStaffNumber(String staffNumber) throws DatabaseSystemException {
        List<ReportDTO> reports = new ArrayList<>();
        String query = "SELECT r.idReporte, r.matricula, r.tipoReporte, "
                + "r.numeroInforme, r.horasCubiertas, r.fechaReporte, "
                + "r.estadoEntrega, r.estadoRevision, r.observaciones, "
                + "r.archivoReportePDF FROM reporte r INNER JOIN Alumno a "
                + "ON r.matricula = a.matricula WHERE a.numeroPersonalProfesor = ? "
                + "ORDER BY r.fechaReporte DESC, r.numeroInforme DESC";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, staffNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reports.add(this.mapResultSetToDTO(resultSet));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "SQL operational exception pulling "
                    + "report portfolios linked to professor staff validation key: " + staffNumber, exception);
            throw new DatabaseSystemException("Error técnico al recuperar el listado unificado de reportes pendientes.", exception);
        }
        return reports;
    }

    @Override
    public void updateReportEvaluation(ReportDTO report)
            throws DatabaseSystemException {
        String updateReportSQL = "UPDATE reporte SET estadoRevision = ?, horasCubiertas = ?, observaciones = ? WHERE idReporte = ?";
        String updateAlumnoHorasSQL = "UPDATE Alumno SET horasCubiertas = horasCubiertas + ? WHERE matricula = ?";

        try (Connection connection = this.databaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statementUpdate = connection.prepareStatement(updateReportSQL); PreparedStatement statementAlumno = connection.prepareStatement(updateAlumnoHorasSQL)) {

                statementUpdate.setString(1, report.getReviewStatus());
                statementUpdate.setInt(2, report.getHoursCovered());
                statementUpdate.setString(3, report.getObservations());
                statementUpdate.setInt(4, report.getIdReport());
                statementUpdate.executeUpdate();

                boolean isApproved = REPORT_STATUS_APPROVED.equalsIgnoreCase(report.getReviewStatus());

                if (isApproved && report.getHoursCovered() > RESET) {
                    statementAlumno.setInt(1, report.getHoursCovered());
                    statementAlumno.setString(2, report.getStudentEnrollment());
                    statementAlumno.executeUpdate();

                    LOGGER.log(Level.INFO, "Hours successfully aggregated on Alumno records for enrollment token: {0}",
                            report.getStudentEnrollment());
                }

                connection.commit();
                LOGGER.log(Level.INFO, "Evaluation transaction pipeline processed successfully on cloud repository storage.");
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.log(Level.SEVERE, "SQL nested exception inside "
                        + "statement batch pipeline triggering transaction "
                        + "rollback protocol", exception);
                throw new DatabaseSystemException("Error técnico al escribir "
                        + "el dictamen de evaluación en la base de datos.",
                        exception);
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Network connection block linkage "
                    + "failure on updateReportEvaluation architecture pipeline",
                    exception);
            throw new DatabaseSystemException("Error de comunicación de red: "
                    + "No se pudo actualizar los criterios del dictamen en "
                    + "el repositorio.", exception);
        }
    }

    @Override
    public boolean isReportSubmitted(String enrollmentId, String reportType, int reportNumber) throws DatabaseSystemException {
        boolean exists = false;
        String query = "SELECT COUNT(*) FROM reporte WHERE matricula = ? "
                + "AND tipoReporte = ? AND numeroInforme = ?";

        try (Connection connection = this.databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            int evaluatedReportNumber = reportNumber;
            if (evaluatedReportNumber <= RESET) {
                evaluatedReportNumber = SystemConstants.DEFAULT_REPORT_NUMBER;
            }

            statement.setString(1, enrollmentId);
            statement.setString(2, reportType);
            statement.setInt(3, evaluatedReportNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    exists = resultSet.getInt(1) > RESET;
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error checking report dual existence duplication mapping context bounds", exception);
            throw new DatabaseSystemException("Error técnico al verificar duplicidad de entregas.", exception);
        }
        return exists;
    }

    @Override
    public int overwriteReport(ReportDTO report) throws DatabaseSystemException {
        int rowsAffected = RESET;
        String query = "UPDATE reporte SET archivoReportePDF = ?, "
                + "fechaReporte = ?, estadoEntrega = ?, estadoRevision = ? "
                + "WHERE matricula = ? AND tipoReporte = ? AND "
                + "numeroInforme = ?";

        try (Connection connection = this.databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            int reportNumber = report.getReportedHours();
            if (reportNumber <= RESET) {
                reportNumber = SystemConstants.DEFAULT_REPORT_NUMBER;
            }

            statement.setBytes(1, report.getFileContent());
            statement.setDate(2, report.getDeliveryDate());
            statement.setString(3, report.getDeliveryStatus());
            statement.setString(4, report.getReviewStatus());
            statement.setString(5, report.getStudentEnrollment());
            statement.setString(6, report.getReportType());
            statement.setInt(7, reportNumber);

            rowsAffected = statement.executeUpdate();
            LOGGER.log(Level.INFO, "Hot-swap binary stream document "
                    + "overwriting executed successfully for enrollment: {0}",
                    report.getStudentEnrollment());
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Critical technical crash overwriting "
                    + "binary payload context inside database pipeline storage", exception);
            throw new DatabaseSystemException("Error al sobreescribir el reporte en la base de datos.", exception);
        }
        return rowsAffected;
    }

    private ReportDTO mapResultSetToDTO(ResultSet resultSet)
            throws SQLException {
        ReportDTO report = new ReportDTO();
        report.setIdReport(resultSet.getInt("idReporte"));
        report.setStudentEnrollment(resultSet.getString("matricula"));
        report.setReportType(resultSet.getString("tipoReporte"));
        report.setReportedHours(resultSet.getInt("numeroInforme"));
        report.setHoursCovered(resultSet.getInt("horasCubiertas"));
        report.setDeliveryDate(resultSet.getDate("fechaReporte"));
        report.setDeliveryStatus(resultSet.getString("estadoEntrega"));
        report.setReviewStatus(resultSet.getString("estadoRevision"));
        report.setObservations(resultSet.getString("observaciones"));

        byte[] content = resultSet.getBytes("archivoReportePDF");
        if (content != null) {
            report.setFileContent(content);
        }
        return report;
    }
}
