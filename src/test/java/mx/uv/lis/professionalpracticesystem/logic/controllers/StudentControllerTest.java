package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DeadlineDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DocumentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.*;

/**
 *
 * @author cinth
 */
public class StudentControllerTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private StudentController studentController;

    @BeforeEach
    public void setUp() throws SQLException {
        this.studentController = new StudentController();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.executeUpdate("DELETE FROM plazoentrega WHERE numeroPersonalProfesor = '" + TEST_STC_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM Documentos WHERE matricula = '" + TEST_STC_ENROLLMENT + "'");
            statement.executeUpdate("UPDATE Alumno SET numeroPersonalProfesor = NULL, nrc = NULL WHERE matricula = '" + TEST_STC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_STC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM ExperienciaEducativa WHERE nrc = " + TEST_STC_NRC);
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor = '" + TEST_STC_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email IN ('" + TEST_STC_PROF_EMAIL + "', '" + TEST_STC_STUDENT_EMAIL + "')");
            
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_STC_PROF_EMAIL + "', 'pass', '" + ROLE_PROFESSOR + "')");
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_STC_STUDENT_EMAIL + "', 'pass', '" + ROLE_STUDENT + "')");
            statement.executeUpdate("INSERT INTO Profesor (numeroPersonalProfesor, nombre, apellidoPaterno, genero, estado, email) VALUES ('" + TEST_STC_STAFF_NUMBER + "', 'Ctrl', 'Prof', 'Masculino', '" + STATUS_ACTIVE + "', '" + TEST_STC_PROF_EMAIL + "')");
            statement.executeUpdate("INSERT INTO ExperienciaEducativa (nrc, nombreExperienciaEducativa, numeroPersonalProfesor) VALUES (" + TEST_STC_NRC + ", 'Prácticas', '" + TEST_STC_STAFF_NUMBER + "')");
            
            statement.executeUpdate("INSERT INTO Alumno (matricula, nombre, apellidoPaterno, genero, estado, periodo, creditosCubiertos, numeroPersonalProfesor, nrc, email) VALUES ('" + TEST_STC_ENROLLMENT + "', 'Ctrl', 'Student', 'Femenino', '" + STATUS_ACTIVE + "', '" + DEFAULT_PERIOD + "', 120, '" + TEST_STC_STAFF_NUMBER + "', " + TEST_STC_NRC + ", '" + TEST_STC_STUDENT_EMAIL + "')");
            statement.executeUpdate("INSERT INTO Documentos (nombreArchivo, rutaArchivo, tipoDocumento, fechaEntrega, matricula, estadoRevision) VALUES ('test.pdf', '', '" + TEST_STC_DOC_TYPE + "', '" + Date.valueOf(LocalDate.now()) + "', '" + TEST_STC_ENROLLMENT + "', 'Pendiente')");
            
            LocalDateTime futureDate = LocalDateTime.now().plusDays(10);
            statement.executeUpdate("INSERT INTO plazoentrega (nrc, tipoReporte, numeroInforme, fechaLimite, numeroPersonalProfesor) VALUES (" + TEST_STC_NRC + ", '" + TEST_STC_REPORT_TYPE + "', " + TEST_STC_REPORT_NUMBER + ", '" + futureDate.toString().replace("T", " ") + "', '" + TEST_STC_STAFF_NUMBER + "')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM plazoentrega WHERE numeroPersonalProfesor = '" + TEST_STC_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM Documentos WHERE matricula = '" + TEST_STC_ENROLLMENT + "'");
            statement.executeUpdate("UPDATE Alumno SET numeroPersonalProfesor = NULL, nrc = NULL WHERE matricula = '" + TEST_STC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_STC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM ExperienciaEducativa WHERE nrc = " + TEST_STC_NRC);
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor = '" + TEST_STC_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email IN ('" + TEST_STC_PROF_EMAIL + "', '" + TEST_STC_STUDENT_EMAIL + "')");
        }
    }


    @Test
    public void testDownloadStudentDocumentExistingDataSuccessful() throws DatabaseSystemException {
        DocumentDTO document = studentController.downloadStudentDocument(TEST_STC_ENROLLMENT, TEST_STC_DOC_TYPE);
        assertEquals(TEST_STC_ENROLLMENT, document.getEnrollment());
    }

    @Test
    public void testGetAssignedStudentsValidEmailSuccessful() throws DatabaseSystemException {
        List<StudentDTO> students = studentController.getAssignedStudents(TEST_STC_PROF_EMAIL);
        assertTrue(students.size() > RESET, "Debe retornar la lista de estudiantes asignados al profesor");
    }

    @Test
    public void testGetStudentDocumentStatusSummaryValidEnrollmentSuccessful() throws DatabaseSystemException {
        Map<String, String> summary = studentController.getStudentDocumentStatusSummary(TEST_STC_ENROLLMENT);
        assertEquals("Pendiente", summary.get(TEST_STC_DOC_TYPE));
    }

    @Test
    public void testUpdateStudentDocumentStatusValidDataSuccessful() throws DatabaseSystemException {
        studentController.updateStudentDocumentStatus(TEST_STC_ENROLLMENT, TEST_STC_DOC_TYPE, "Aprobado");
        
        Map<String, String> summary = studentController.getStudentDocumentStatusSummary(TEST_STC_ENROLLMENT);
        assertEquals("Aprobado", summary.get(TEST_STC_DOC_TYPE));
    }

    @Test
    public void testUpdateStudentDocumentStatusInvalidDataUnsuccessful() {
        try {
            studentController.updateStudentDocumentStatus(EMPTY_STRING, TEST_STC_DOC_TYPE, "Aprobado");
            fail("Expected DatabaseSystemException due to empty enrollment");
        } catch (DatabaseSystemException exception) {
            assertEquals("Los datos de identificación son inválidos.", exception.getMessage());
        }
    }


    @Test
    public void testSaveOrUpdateDeadlineValidDataSuccessful() throws DatabaseSystemException {
        DeadlineDTO newDeadline = new DeadlineDTO();
        newDeadline.setReportType("Final");
        newDeadline.setReportedNumber(1);
        newDeadline.setNrc(TEST_STC_NRC);
        newDeadline.setStaffNumber(TEST_STC_STAFF_NUMBER);
        newDeadline.setDeadlineDate(LocalDateTime.now().plusDays(5));
        
        studentController.saveOrUpdateDeadline(newDeadline);
        
        List<DeadlineDTO> deadlines = studentController.getAllExistingDeadlines(TEST_STC_ENROLLMENT);
        assertTrue(deadlines.size() >= 2, "Debe existir el plazo inyectado y el nuevo plazo Final.");
    }

    @Test
    public void testSaveOrUpdateDeadlineInvalidDataUnsuccessful() {
        try {
            studentController.saveOrUpdateDeadline(null);
            fail("Expected DatabaseSystemException due to null deadline entity");
        } catch (DatabaseSystemException exception) {
            assertEquals("Los datos del plazo de entrega son insuficientes o inválidos.", exception.getMessage());
        }
    }

    @Test
    public void testGetAllExistingDeadlinesValidEnrollmentSuccessful() throws DatabaseSystemException {
        List<DeadlineDTO> deadlines = studentController.getAllExistingDeadlines(TEST_STC_ENROLLMENT);
        assertTrue(deadlines.size() > RESET, "Debería recuperar el plazo de entrega inyectado en setUp");
    }

    @Test
    public void testGetDeadlinesByProfessorEmailValidEmailSuccessful() throws DatabaseSystemException {
        List<DeadlineDTO> deadlines = studentController.getDeadlinesByProfessorEmail(TEST_STC_PROF_EMAIL);
        assertTrue(deadlines.size() > RESET, "Debería recuperar plazos ligados al profesor");
    }

    @Test
    public void testGetProfessorStaffNumberValidEmailSuccessful() throws DatabaseSystemException {
        String staffNumber = studentController.getProfessorStaffNumber(TEST_STC_PROF_EMAIL);
        assertEquals(TEST_STC_STAFF_NUMBER, staffNumber);
    }

    @Test
    public void testValidateReportSubmissionTimeOnTimeSuccessful() throws DatabaseSystemException {
        ReportDTO report = new ReportDTO();
        report.setStudentEnrollment(TEST_STC_ENROLLMENT);
        report.setReportType(TEST_STC_REPORT_TYPE);
        report.setReportedHours(TEST_STC_REPORT_NUMBER); // El controlador lo empareja con 'numeroInforme'
        
        studentController.validateReportSubmissionTime(report);
        
        assertEquals(STATUS_ON_TIME_SUBMISSION, report.getDeliveryStatus());
    }

    @Test
    public void testValidateReportSubmissionTimeLateSuccessful() throws DatabaseSystemException, SQLException {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(5);
        String updateQuery = "UPDATE plazoentrega SET fechaLimite = '" + pastDate.toString().replace("T", " ") + "' WHERE numeroPersonalProfesor = '" + TEST_STC_STAFF_NUMBER + "'";
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(updateQuery);
        }
        
        ReportDTO report = new ReportDTO();
        report.setStudentEnrollment(TEST_STC_ENROLLMENT);
        report.setReportType(TEST_STC_REPORT_TYPE);
        report.setReportedHours(TEST_STC_REPORT_NUMBER);
        
        studentController.validateReportSubmissionTime(report);
        
        assertEquals(STATUS_LATE_SUBMISSION, report.getDeliveryStatus());
    }

    @Test
    public void testValidateStudentEnrollmentEligibilityActiveStudentSuccessful() throws DatabaseSystemException {
        StudentDTO activeStudent = new StudentDTO();
        activeStudent.setEnrollmentId(TEST_STC_ENROLLMENT);
        activeStudent.setStatus(STATUS_ACTIVE);
        
        studentController.validateStudentEnrollmentEligibility(activeStudent);
    }

    @Test
    public void testValidateStudentEnrollmentEligibilityInactiveStudentUnsuccessful() {
        StudentDTO inactiveStudent = new StudentDTO();
        inactiveStudent.setEnrollmentId(TEST_STC_ENROLLMENT);
        inactiveStudent.setStatus("Inactivo");
        
        try {
            studentController.validateStudentEnrollmentEligibility(inactiveStudent);
            fail("Expected DatabaseSystemException due to inactive student");
        } catch (DatabaseSystemException exception) {
            assertTrue(exception.getMessage().contains("no está Activo"), "Debería rechazar al alumno inactivo.");
        }
    }
}