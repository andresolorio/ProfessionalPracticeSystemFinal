package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.FinalReportDeliverableDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DEFAULT_PERIOD;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EXPECTED_SINGLE_ROW_AFFECTED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.FIRST_ELEMENT_INDEX;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.ROLE_STUDENT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATUS_ACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_FRD_ADVANCE_NEW;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_FRD_ADVANCE_TARGET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_FRD_EMAIL;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_FRD_ENROLLMENT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_FRD_NON_EXISTENT_ENROLLMENT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_FRD_OBSERVATIONS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_FRD_RESULT_NEW;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_FRD_RESULT_TARGET;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @autor andre
 * @author cinth
 */
public class FinalReportDeliverableDAOTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private FinalReportDeliverableDAO deliverableDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        this.deliverableDAO = new FinalReportDeliverableDAO();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.executeUpdate("DELETE FROM informe_final_entregable WHERE matricula = '" + TEST_FRD_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_FRD_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_FRD_EMAIL + "'");
            
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_FRD_EMAIL + "', 'password123', '" + ROLE_STUDENT + "')");
            statement.executeUpdate("INSERT INTO Alumno (matricula, nombre, apellidoPaterno, genero, estado, periodo, creditosCubiertos, email) VALUES ('" + TEST_FRD_ENROLLMENT + "', 'JUnit', 'Student', 'Femenino', '" + STATUS_ACTIVE + "', '" + DEFAULT_PERIOD + "', 120, '" + TEST_FRD_EMAIL + "')");
            statement.executeUpdate("INSERT INTO informe_final_entregable (matricula, resultadoEntregable, porcentajeAvance, observaciones) VALUES ('" + TEST_FRD_ENROLLMENT + "', '" + TEST_FRD_RESULT_TARGET + "', " + TEST_FRD_ADVANCE_TARGET + ", '" + TEST_FRD_OBSERVATIONS + "')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM informe_final_entregable WHERE matricula = '" + TEST_FRD_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_FRD_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_FRD_EMAIL + "'");
        }
    }

    @Test
    public void testSaveDeliverablesValidListSuccessful() throws DatabaseSystemException, SQLException {
        List<FinalReportDeliverableDTO> deliverables = new ArrayList<>();
        FinalReportDeliverableDTO deliverable = new FinalReportDeliverableDTO();
        deliverable.setEnrollmentId(TEST_FRD_ENROLLMENT);
        deliverable.setDeliverableResult(TEST_FRD_RESULT_NEW);
        deliverable.setAdvancePercentage(TEST_FRD_ADVANCE_NEW);
        deliverable.setObservations(TEST_FRD_OBSERVATIONS);
        deliverables.add(deliverable);
        
        try (Connection connection = databaseConnection.getConnection()) {
            int rowsAffected = deliverableDAO.saveDeliverables(deliverables, connection);
            assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
        }
    }

    @Test
    public void testSaveDeliverablesFkConstraintViolationUnsuccessful() throws SQLException {
        List<FinalReportDeliverableDTO> deliverables = new ArrayList<>();
        FinalReportDeliverableDTO deliverable = new FinalReportDeliverableDTO();
        deliverable.setEnrollmentId(TEST_FRD_NON_EXISTENT_ENROLLMENT);
        deliverable.setDeliverableResult(TEST_FRD_RESULT_NEW);
        deliverable.setAdvancePercentage(TEST_FRD_ADVANCE_NEW);
        deliverable.setObservations(TEST_FRD_OBSERVATIONS);
        deliverables.add(deliverable);
        
        try (Connection connection = databaseConnection.getConnection()) {
            try {
                deliverableDAO.saveDeliverables(deliverables, connection);
                fail("Expected DatabaseSystemException due to foreign key violation was not thrown.");
            } catch (DatabaseSystemException exception) {
                assertEquals("Error al registrar los entregables finales.", exception.getMessage());
            }
        }
    }

    @Test
    public void testGetDeliverablesByStudentEnrollmentExistingDataSuccessful() throws DatabaseSystemException {
        List<FinalReportDeliverableDTO> deliverables = deliverableDAO.getDeliverablesByStudentEnrollment(TEST_FRD_ENROLLMENT);
        assertEquals(TEST_FRD_RESULT_TARGET, deliverables.get(FIRST_ELEMENT_INDEX).getDeliverableResult());
    }

    @Test
    public void testGetDeliverablesByStudentEnrollmentNoDataSuccessful() throws DatabaseSystemException {
        List<FinalReportDeliverableDTO> deliverables = deliverableDAO.getDeliverablesByStudentEnrollment(TEST_FRD_NON_EXISTENT_ENROLLMENT);
        assertTrue(deliverables.isEmpty(), "La lista de entregables debería estar vacía para un estudiante sin registros.");
    }
}