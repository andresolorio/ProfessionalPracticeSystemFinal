package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
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
public class ProfessorControllerTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private ProfessorController professorController;

@BeforeEach
    public void setUp() throws SQLException {
        this.professorController = new ProfessorController();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.executeUpdate("DELETE FROM reporte WHERE matricula = '" + TEST_PC_STUDENT_ENROLLMENT + "'");
            statement.executeUpdate("UPDATE Alumno SET numeroPersonalProfesor = NULL WHERE matricula = '" + TEST_PC_STUDENT_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_PC_STUDENT_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Proyecto WHERE idProyecto = " + TEST_PC_PROJECT_ID);
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor = '" + TEST_PC_STAFF_TARGET + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email IN ('" + TEST_PC_EMAIL_TARGET + "', '" + TEST_PC_STUDENT_EMAIL + "')");
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = 1"); 

            statement.executeUpdate("INSERT INTO OrganizacionVinculada (idOrganizacionVinculada, nombreEmpresa, direccion, telefono, ciudad, email, sector) " +
                                    "VALUES (1, 'Org Test', 'Dir', '1234567890', 'Ciudad', 'org@test.com', 'Privado')");
            
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_PC_EMAIL_TARGET + "', 'pass', '" + ROLE_PROFESSOR + "')");
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_PC_STUDENT_EMAIL + "', 'pass', '" + ROLE_STUDENT + "')");
            
            statement.executeUpdate("INSERT INTO Profesor (numeroPersonalProfesor, "
                    + "nombre, apellidoPaterno, apellidoMaterno, genero, estado, esCoordinador, email) "
                    + "VALUES ('" + TEST_PC_STAFF_TARGET + "', 'Controller', 'Test', "
                            + "'Prof', 'Femenino', '" + STATUS_ACTIVE + "', 0, '" + TEST_PC_EMAIL_TARGET + "')");
            
            statement.executeUpdate("INSERT INTO Proyecto (idProyecto, nombreProyecto, descripcion, objetivoGeneral, duracion, estado, vacantesTotales, vacantesDisponibles, idOrganizacionVinculada) VALUES (" + TEST_PC_PROJECT_ID + ", 'Controller Project', 'Test', 'Test', '400', '" + STATUS_ACTIVE + "', 1, 1, 1)");
            
            statement.executeUpdate("INSERT INTO Alumno (matricula, nombre, apellidoPaterno, genero, estado, periodo, creditosCubiertos, idProyecto, numeroPersonalProfesor, email) VALUES ('" + TEST_PC_STUDENT_ENROLLMENT + "', 'Student', 'Test', 'Masculino', '" + STATUS_ACTIVE + "', '" + DEFAULT_PERIOD + "', 120, " + TEST_PC_PROJECT_ID + ", '" + TEST_PC_STAFF_TARGET + "', '" + TEST_PC_STUDENT_EMAIL + "')");
            
            statement.executeUpdate("INSERT INTO reporte (matricula, tipoReporte, numeroInforme, estadoEntrega, estadoRevision, archivoReportePDF, fechaReporte) VALUES ('" + TEST_PC_STUDENT_ENROLLMENT + "', '" + TEST_PC_REPORT_TYPE + "', 1, 'A tiempo', 'Pendiente', '', '2026-06-01')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM reporte WHERE matricula = '" + TEST_PC_STUDENT_ENROLLMENT + "'");
            statement.executeUpdate("UPDATE Alumno SET numeroPersonalProfesor = NULL WHERE matricula = '" + TEST_PC_STUDENT_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_PC_STUDENT_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Proyecto WHERE idProyecto = " + TEST_PC_PROJECT_ID);
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = 1");
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor = '" + TEST_PC_STAFF_TARGET + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email IN ('" + TEST_PC_EMAIL_TARGET + "', '" + TEST_PC_STUDENT_EMAIL + "')");
        }
    }

    private int fetchGeneratedReportId() throws SQLException {
        int generatedId = 0;
        String query = "SELECT idReporte FROM reporte WHERE matricula = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, TEST_PC_STUDENT_ENROLLMENT);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    generatedId = resultSet.getInt("idReporte");
                }
            }
        }
        return generatedId;
    }

    @Test
    public void testGetAssignedStudentsReportsValidEmailSuccessful() throws DatabaseSystemException {
        List<ReportDTO> reports = professorController.getAssignedStudentsReports(TEST_PC_EMAIL_TARGET);
        assertEquals(TEST_PC_STUDENT_ENROLLMENT, reports.get(FIRST_ELEMENT_INDEX).getStudentEnrollment());
    }

    @Test
    public void testGetAssignedStudentsReportsInvalidEmailUnsuccessful() {
        try {
            professorController.getAssignedStudentsReports(EMPTY_STRING);
            fail("Expected DatabaseSystemException due to empty email was not thrown");
        } catch (DatabaseSystemException exception) {
            assertEquals("El correo de la sesión del docente es inválido.", exception.getMessage());
        }
    }

    @Test
    public void testEvaluateStudentReportRejectionWithoutObservationsUnsuccessful() throws SQLException {
        int targetReportId = fetchGeneratedReportId();
        ReportDTO report = new ReportDTO();
        report.setIdReport(targetReportId);
        report.setStudentEnrollment(TEST_PC_STUDENT_ENROLLMENT);
        report.setReviewStatus(TEST_PC_REPORT_STATUS_REJECTED);
        report.setObservations(EMPTY_STRING);
        
        try {
            professorController.evaluateStudentReport(report);
            fail("Expected DatabaseSystemException due to missing observations on rejection");
        } catch (DatabaseSystemException exception) {
            assertEquals("Es obligatorio ingresar observaciones si el reporte es rechazado.", exception.getMessage());
        }
    }

    @Test
    public void testEvaluateStudentReportValidDataSuccessful() throws DatabaseSystemException, SQLException {
        int targetReportId = fetchGeneratedReportId();
        ReportDTO report = new ReportDTO();
        report.setIdReport(targetReportId);
        report.setStudentEnrollment(TEST_PC_STUDENT_ENROLLMENT);
        report.setReportType(TEST_PC_REPORT_TYPE);
        report.setReviewStatus(TEST_PC_REPORT_STATUS_APPROVED);
        report.setHoursCovered(0);
        report.setObservations("Buen trabajo");
        
        professorController.evaluateStudentReport(report);
        
        String query = "SELECT estadoRevision FROM reporte WHERE idReporte = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, targetReportId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    assertEquals(TEST_PC_REPORT_STATUS_APPROVED, rs.getString("estadoRevision"));
                }
            }
        }
    }

    @Test
    public void testGetStudentProjectIdValidEnrollmentSuccessful() throws DatabaseSystemException {
        int projectId = professorController.getStudentProjectId(TEST_PC_STUDENT_ENROLLMENT);
        assertEquals(TEST_PC_PROJECT_ID, projectId);
    }
}