package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DEFAULT_PERIOD;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_BYTE_ARRAY;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_STRING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EXPECTED_SINGLE_ROW_AFFECTED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_FILE_SIZE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_FILE_SIZE_MB;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.NEGATIVE_HOURS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.ROLE_PROFESSOR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.ROLE_STUDENT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATUS_ACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATUS_APPROVED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_DUMMY_BYTES;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_RC_ENROLLMENT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_RC_INVALID_ENROLLMENT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_RC_INVALID_STATUS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_RC_PROF_EMAIL;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_RC_REPORTED_HOURS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_RC_REPORT_NUMBER;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_RC_REPORT_TYPE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_RC_STAFF_NUMBER;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_RC_STATUS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_RC_STUDENT_EMAIL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
/**
 *
 * @author cinth
 */
public class ReportControllerTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private ReportController reportController;

    @BeforeEach
    public void setUp() throws SQLException {
        this.reportController = new ReportController();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.executeUpdate("DELETE FROM reporte WHERE matricula = '" + TEST_RC_ENROLLMENT + "'");
            statement.executeUpdate("UPDATE Alumno SET numeroPersonalProfesor = NULL WHERE matricula = '" + TEST_RC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_RC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor = '" + TEST_RC_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email IN ('" + TEST_RC_PROF_EMAIL + "', '" + TEST_RC_STUDENT_EMAIL + "')");
            
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_RC_PROF_EMAIL + "', 'pass123', '" + ROLE_PROFESSOR + "')");
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_RC_STUDENT_EMAIL + "', 'pass123', '" + ROLE_STUDENT + "')");
            
            statement.executeUpdate("INSERT INTO Profesor (numeroPersonalProfesor, nombre, apellidoPaterno, genero, estado, email) VALUES ('" + TEST_RC_STAFF_NUMBER + "', 'Teacher', 'JUnit', 'Femenino', '" + STATUS_ACTIVE + "', '" + TEST_RC_PROF_EMAIL + "')");
            statement.executeUpdate("INSERT INTO Alumno (matricula, nombre, apellidoPaterno, genero, estado, periodo, creditosCubiertos, numeroPersonalProfesor, email) VALUES ('" + TEST_RC_ENROLLMENT + "', 'Student', 'JUnit', 'Masculino', '" + STATUS_ACTIVE + "', '" + DEFAULT_PERIOD + "', 120, '" + TEST_RC_STAFF_NUMBER + "', '" + TEST_RC_STUDENT_EMAIL + "')");
            
            statement.executeUpdate("INSERT INTO reporte (matricula, tipoReporte, numeroInforme, estadoEntrega, estadoRevision, archivoReportePDF, fechaReporte) VALUES ('" + TEST_RC_ENROLLMENT + "', '" + TEST_RC_REPORT_TYPE + "', " + TEST_RC_REPORT_NUMBER + ", 'A tiempo', 'Pendiente', '', '" + Date.valueOf(LocalDate.now()) + "')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM reporte WHERE matricula = '" + TEST_RC_ENROLLMENT + "'");
            statement.executeUpdate("UPDATE Alumno SET numeroPersonalProfesor = NULL WHERE matricula = '" + TEST_RC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_RC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor = '" + TEST_RC_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email IN ('" + TEST_RC_PROF_EMAIL + "', '" + TEST_RC_STUDENT_EMAIL + "')");
        }
    }

    private int fetchGeneratedReportId() throws SQLException {
        int generatedId = 0;
        String query = "SELECT idReporte FROM reporte WHERE matricula = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, TEST_RC_ENROLLMENT);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    generatedId = resultSet.getInt("idReporte");
                }
            }
        }
        return generatedId;
    }

    @Test
    public void testRegisterReportValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException {
        ReportDTO report = new ReportDTO();
        report.setStudentEnrollment(TEST_RC_ENROLLMENT);

        report.setReportType(TEST_RC_REPORT_TYPE);
        report.setReportedHours(2);

        report.setDeliveryDate(Date.valueOf(LocalDate.now()));
        report.setFileContent(TEST_DUMMY_BYTES);
        report.setDeliveryStatus(TEST_RC_STATUS);

        int rowsAffected = reportController.registerReport(report);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testRegisterReportNullEntityUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        try {
            reportController.registerReport(null);
            fail("Expected ValidationException due to null report object");
        } catch (ValidationException exception) {
            assertEquals("Error: La entidad del reporte no puede ser nula.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterReportInvalidEnrollmentUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ReportDTO report = new ReportDTO();
        report.setStudentEnrollment(TEST_RC_INVALID_ENROLLMENT);
        
        try {
            reportController.registerReport(report);
            fail("Expected ValidationException due to bad enrollment format");
        } catch (ValidationException exception) {
            assertEquals("Error: La matrícula del alumno asociada al reporte es inválida.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterReportEmptyTypeUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ReportDTO report = new ReportDTO();
        report.setStudentEnrollment(TEST_RC_ENROLLMENT);
        report.setReportType(EMPTY_STRING);
        
        try {
            reportController.registerReport(report);
            fail("Expected ValidationException due to empty report type");
        } catch (ValidationException exception) {
            assertEquals("Error: El tipo de reporte (Mensual, Parcial, Final, Autoevaluación) es obligatorio.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterReportInvalidReportedHoursUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ReportDTO report = new ReportDTO();
        report.setStudentEnrollment(TEST_RC_ENROLLMENT);
        report.setReportType(TEST_RC_REPORT_TYPE);
        report.setReportedHours(RESET); 
        
        try {
            reportController.registerReport(report);
            fail("Expected ValidationException due to correlative number zero");
        } catch (ValidationException exception) {
            assertEquals("Error: El número correlativo de informe debe ser mayor a cero.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterReportFutureDateUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ReportDTO report = new ReportDTO();
        report.setStudentEnrollment(TEST_RC_ENROLLMENT);
        report.setReportType(TEST_RC_REPORT_TYPE);
        report.setReportedHours(TEST_RC_REPORT_NUMBER);
        report.setDeliveryDate(Date.valueOf(LocalDate.now().plusDays(5))); // Fecha Futura
        
        try {
            reportController.registerReport(report);
            fail("Expected ValidationException due to future date constraint");
        } catch (ValidationException exception) {
            assertEquals("Error: La fecha del reporte no puede ser una fecha futura.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterReportEmptyFileContentUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ReportDTO report = new ReportDTO();
        report.setStudentEnrollment(TEST_RC_ENROLLMENT);
        report.setReportType(TEST_RC_REPORT_TYPE);
        report.setReportedHours(TEST_RC_REPORT_NUMBER);
        report.setDeliveryDate(Date.valueOf(LocalDate.now()));
        report.setFileContent(EMPTY_BYTE_ARRAY);
        
        try {
            reportController.registerReport(report);
            fail("Expected ValidationException due to empty binary content");
        } catch (ValidationException exception) {
            assertEquals("Error: El contenido binario del archivo PDF es obligatorio.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterReportOversizedFileUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ReportDTO report = new ReportDTO();
        report.setStudentEnrollment(TEST_RC_ENROLLMENT);
        report.setReportType(TEST_RC_REPORT_TYPE);
        report.setReportedHours(TEST_RC_REPORT_NUMBER);
        report.setDeliveryDate(Date.valueOf(LocalDate.now()));
        byte[] oversizedFile = new byte[MAX_FILE_SIZE + 1];
        report.setFileContent(oversizedFile);
        
        try {
            reportController.registerReport(report);
            fail("Expected ValidationException due to file exceeding max bounds");
        } catch (ValidationException exception) {
            assertEquals("Error: El archivo excede el tamaño permitido (" + MAX_FILE_SIZE_MB + "MB).", exception.getMessage());
        }
    }

    @Test
    public void testGetReportsByStudentValidFormatSuccessful() throws DatabaseSystemException {
        List<ReportDTO> reports = reportController.getReportsByStudent(TEST_RC_ENROLLMENT);
        boolean hasElements = reports.size() > RESET;
        assertTrue(hasElements);
    }

    @Test
    public void testGetReportByIdValidIdSuccessful() throws EntityNotFoundException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedReportId();
        ReportDTO report = reportController.getReportById(targetId);
        assertEquals(TEST_RC_ENROLLMENT, report.getStudentEnrollment());
    }

    @Test
    public void testGetReportsByProfessorValidStaffNumberSuccessful() throws DatabaseSystemException {
        List<ReportDTO> reports = reportController.getReportsByProfessor(TEST_RC_STAFF_NUMBER);
        boolean hasElements = reports.size() > RESET;
        assertTrue(hasElements);
    }

    @Test
    public void testEvaluateStudentReportValidDataSuccessful() throws DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedReportId();
        ReportDTO report = new ReportDTO();
        report.setIdReport(targetId);
        report.setReviewStatus(STATUS_APPROVED);
        report.setHoursCovered(TEST_RC_REPORTED_HOURS);
        
        reportController.evaluateStudentReport(report);
        
        ReportDTO updatedReport = reportController.getReportById(targetId);
        assertEquals(STATUS_APPROVED, updatedReport.getReviewStatus());
    }

    @Test
    public void testEvaluateStudentReportInvalidStatusUnsuccessful() throws DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedReportId();
        ReportDTO report = new ReportDTO();
        report.setIdReport(targetId);
        report.setReviewStatus(TEST_RC_INVALID_STATUS);
        report.setHoursCovered(TEST_RC_REPORTED_HOURS);
        
        try {
            reportController.evaluateStudentReport(report);
            fail("Expected ValidationException due to undefined status string");
        } catch (ValidationException exception) {
            assertEquals("Error: El estado del dictamen debe ser 'Aprobado' o 'Rechazado'.", exception.getMessage());
        }
    }

    @Test
    public void testEvaluateStudentReportNegativeHoursUnsuccessful() throws DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedReportId();
        ReportDTO report = new ReportDTO();
        report.setIdReport(targetId);
        report.setReviewStatus(STATUS_APPROVED);
        report.setHoursCovered(NEGATIVE_HOURS);
        
        try {
            reportController.evaluateStudentReport(report);
            fail("Expected ValidationException due to negative hours injected");
        } catch (ValidationException exception) {
            assertEquals("Error: Las horas cubiertas abonadas no pueden ser valores negativos.", exception.getMessage());
        }
    }
}