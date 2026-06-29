package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DeadlineDTO;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.*;

/**
 * @autor andre
 * @author cinth
 */
public class DeadlineDAOTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private DeadlineDAO deadlineDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        this.deadlineDAO = new DeadlineDAO();
        try (Connection connection = databaseConnection.getConnection(); Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM plazoentrega WHERE numeroPersonalProfesor = '" + TEST_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM ExperienciaEducativa WHERE numeroPersonalProfesor = '" + TEST_STAFF_NUMBER + "'"); 
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor = '" + TEST_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_EMAIL + "'");
            
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_EMAIL + "', 'password123', 'Profesor')");
            statement.executeUpdate("INSERT INTO Profesor (numeroPersonalProfesor, nombre, apellidoPaterno, genero, estado, email) VALUES ('" + TEST_STAFF_NUMBER + "', 'JUnit', 'Teacher', 'Masculino', 'Activo', '" + TEST_EMAIL + "')");
            
            statement.executeUpdate("INSERT INTO ExperienciaEducativa (nrc, nombreExperienciaEducativa, numeroPersonalProfesor) VALUES (12345, 'Prácticas Profesionales', '" + TEST_STAFF_NUMBER + "')");
            statement.executeUpdate("INSERT INTO plazoentrega (nrc, tipoReporte, numeroInforme, fechaLimite, fechaActualizacion, numeroPersonalProfesor) VALUES (12345, '" + TARGET_REPORT_TYPE + "', 1, '2026-12-31 23:59:59', '2026-06-10 10:00:00', '" + TEST_STAFF_NUMBER + "')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM plazoentrega WHERE numeroPersonalProfesor = '" + TEST_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM ExperienciaEducativa WHERE numeroPersonalProfesor = '" + TEST_STAFF_NUMBER + "'"); 
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor = '" + TEST_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_EMAIL + "'");
            statement.executeUpdate("DELETE FROM plazoentrega WHERE tipoReporte = '" + NEW_REPORT_TYPE + "'");
        }
    }

    @Test
    public void testSaveOrUpdateDeadlineInsertNewRecordSuccessful() throws DatabaseSystemException {
        DeadlineDTO deadline = new DeadlineDTO();
        deadline.setNrc(TEST_NRC);
        deadline.setReportType(NEW_REPORT_TYPE);
        deadline.setDeadlineDate(LocalDateTime.of(TEST_YEAR, 8, 15, TEST_MAX_HOUR, TEST_MAX_MINUTE, TEST_MAX_SECOND));
        deadline.setStaffNumber(TEST_STAFF_NUMBER);
        int rowsAffected = deadlineDAO.saveOrUpdateDeadline(deadline);
        assertEquals(EXPECTED_ROWS_INSERTED, rowsAffected);
    }

    @Test
    public void testSaveOrUpdateDeadlineUpdateOnDuplicateKeySuccessful() throws DatabaseSystemException {
        DeadlineDTO deadline = new DeadlineDTO();
        deadline.setNrc(TEST_NRC);
        deadline.setReportType(TARGET_REPORT_TYPE);
        deadline.setDeadlineDate(LocalDateTime.of(TEST_YEAR, 11, 30, TEST_MAX_HOUR, TEST_MAX_MINUTE, TEST_MAX_SECOND));
        deadline.setStaffNumber(TEST_STAFF_NUMBER);
        int rowsAffected = deadlineDAO.saveOrUpdateDeadline(deadline);
        assertEquals(EXPECTED_ROWS_UPSERTED, rowsAffected);
    }

    @Test
    public void testGetDeadlineByReportTypeValidTypeSuccessful() throws DatabaseSystemException {
        DeadlineDTO deadline = deadlineDAO.getDeadlineByReportType(TARGET_REPORT_TYPE);
        assertEquals(TARGET_REPORT_TYPE, deadline.getReportType());
    }

    @Test
    public void testGetDeadlinesByProfessorValidStaffNumberSuccessful() throws DatabaseSystemException {
        List<DeadlineDTO> deadlines = deadlineDAO.getDeadlinesByProfessorStaffNumber(TEST_STAFF_NUMBER);
        assertEquals(TARGET_REPORT_TYPE, deadlines.get(FIRST_ELEMENT_INDEX).getReportType());
    }
}