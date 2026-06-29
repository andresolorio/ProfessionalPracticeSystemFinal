package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// Importación estática del catálogo centralizado
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.*;

/**
 *
 * @author cinth
 */
public class ProjectRequestDAOTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private ProjectRequestDAO projectRequestDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        this.projectRequestDAO = new ProjectRequestDAO(databaseConnection);
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            // 1. Limpieza inicial (respetando orden de llaves foráneas)
            statement.executeUpdate("DELETE FROM SolicitudProyecto WHERE matricula = '" + TEST_PR_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM estadodocumentosalumno WHERE matricula = '" + TEST_PR_ENROLLMENT + "'");
            statement.executeUpdate("UPDATE Alumno SET idProyecto = NULL WHERE matricula = '" + TEST_PR_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_PR_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_PR_STUDENT_EMAIL + "'");
            statement.executeUpdate("DELETE FROM Proyecto WHERE idProyecto = " + TEST_PR_PROJECT_ID);
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = " + TEST_PR_ORG_ID);
            
            // 2. Inserción de Infraestructura Base
            statement.executeUpdate("INSERT INTO OrganizacionVinculada (idOrganizacionVinculada, nombreEmpresa, direccion, telefono, ciudad, email, sector) VALUES (" + TEST_PR_ORG_ID + ", '" + TEST_PR_ORG_NAME + "', 'Avenida PR', '2288888888', 'Xalapa', 'pr@org.mx', '" + SECTOR_PRIVATE + "')");
            statement.executeUpdate("INSERT INTO Proyecto (idProyecto, nombreProyecto, descripcion, objetivoGeneral, duracion, estado, vacantesTotales, vacantesDisponibles, idOrganizacionVinculada) VALUES (" + TEST_PR_PROJECT_ID + ", '" + TEST_PR_PROJECT_NAME + "', 'Desc', 'Obj', '400', '" + STATUS_ACTIVE + "', 1, 1, " + TEST_PR_ORG_ID + ")");
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_PR_STUDENT_EMAIL + "', 'pass', '" + ROLE_STUDENT + "')");
            statement.executeUpdate("INSERT INTO Alumno (matricula, nombre, apellidoPaterno, genero, estado, periodo, creditosCubiertos, email) VALUES ('" + TEST_PR_ENROLLMENT + "', 'PR', 'Student', 'Femenino', '" + STATUS_ACTIVE + "', '" + DEFAULT_PERIOD + "', 120, '" + TEST_PR_STUDENT_EMAIL + "')");
            
            // 3. Inserción de la tabla puente de estados documentales (inicializada en FALSE)
            statement.executeUpdate("INSERT INTO estadodocumentosalumno (matricula, evaluacionOV, autoevaluacion, solicitudPracticas) VALUES ('" + TEST_PR_ENROLLMENT + "', " + EXPECTED_STATE_FALSE + ", " + EXPECTED_STATE_FALSE + ", " + EXPECTED_STATE_FALSE + ")");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM SolicitudProyecto WHERE matricula = '" + TEST_PR_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM estadodocumentosalumno WHERE matricula = '" + TEST_PR_ENROLLMENT + "'");
            statement.executeUpdate("UPDATE Alumno SET idProyecto = NULL WHERE matricula = '" + TEST_PR_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_PR_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_PR_STUDENT_EMAIL + "'");
            statement.executeUpdate("DELETE FROM Proyecto WHERE idProyecto = " + TEST_PR_PROJECT_ID);
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = " + TEST_PR_ORG_ID);
        }
    }

    // --- BLOQUE 1: TRANSACCIONES ATÓMICAS (HAPPY & UNHAPPY PATHS) ---

    @Test
    public void testSaveProjectRequestValidDataSuccessful() throws DatabaseSystemException, SQLException {
        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setEnrollment(TEST_PR_ENROLLMENT);
        request.setProjectId(TEST_PR_PROJECT_ID);
        request.setPriority(TEST_PR_VALID_PRIORITY);

        int rowsAffected = projectRequestDAO.saveProjectRequest(request);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
        
        // Verificación de integridad: Comprobar que el UPDATE en estadodocumentosalumno funcionó
        String statusCheckQuery = "SELECT solicitudPracticas FROM estadodocumentosalumno WHERE matricula = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(statusCheckQuery)) {
            statement.setString(STATEMENT_PARAMETER_INDEX_FIRST, TEST_PR_ENROLLMENT);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int statusValue = resultSet.getInt("solicitudPracticas");
                    assertEquals(EXPECTED_STATE_TRUE, statusValue, "La bandera de solicitud de prácticas debió actualizarse a TRUE (1).");
                } else {
                    fail("No se encontró el registro de estado del estudiante.");
                }
            }
        }
    }

    @Test
    public void testSaveProjectRequestRollbackOnFkViolationUnsuccessful() {
        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setEnrollment(TEST_PR_ENROLLMENT);
        // Enviamos un ID de proyecto válido matemáticamente, pero inexistente en BD
        request.setProjectId(TEST_PR_NON_EXISTENT_PROJECT_ID); 
        request.setPriority(TEST_PR_VALID_PRIORITY);

        try {
            // Pasará la validación del DTO, pero fallará el INSERT en SQL disparando el ROLLBACK
            projectRequestDAO.saveProjectRequest(request);
            fail("Expected DatabaseSystemException due to foreign key violation was not thrown.");
        } catch (DatabaseSystemException exception) {
            assertEquals("Error técnico al registrar la solicitud de proyecto en la base de datis.", exception.getMessage());
        }
    }

    // --- BLOQUE 2: REGLAS DE VALIDACIÓN (DTO FIELDS) ---

    @Test
    public void testSaveProjectRequestNullEntityUnsuccessful() throws DatabaseSystemException {
        try {
            projectRequestDAO.saveProjectRequest(null);
            fail("Expected ValidationException due to null object was not thrown.");
        } catch (ValidationException exception) {
            assertEquals("Los datos de la solicitud de proyecto no pueden ser nulos.", exception.getMessage());
        }
    }

    @Test
    public void testSaveProjectRequestEmptyEnrollmentUnsuccessful() throws DatabaseSystemException {
        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setEnrollment(EMPTY_STRING);
        request.setProjectId(TEST_PR_PROJECT_ID);
        request.setPriority(TEST_PR_VALID_PRIORITY);

        try {
            projectRequestDAO.saveProjectRequest(request);
            fail("Expected ValidationException due to empty enrollment was not thrown.");
        } catch (ValidationException exception) {
            assertEquals("La matrícula del estudiante es obligatoria y no puede estar vacía.", exception.getMessage());
        }
    }

    @Test
    public void testSaveProjectRequestInvalidProjectIdUnsuccessful() throws DatabaseSystemException {
        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setEnrollment(TEST_PR_ENROLLMENT);
        request.setProjectId(RESET); // Valor 0
        request.setPriority(TEST_PR_VALID_PRIORITY);

        try {
            projectRequestDAO.saveProjectRequest(request);
            fail("Expected ValidationException due to invalid project ID (<= 0) was not thrown.");
        } catch (ValidationException exception) {
            assertEquals("El identificador numérico del proyecto proporcionado es inválido.", exception.getMessage());
        }
    }

    @Test
    public void testSaveProjectRequestPriorityTooLowUnsuccessful() throws DatabaseSystemException {
        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setEnrollment(TEST_PR_ENROLLMENT);
        request.setProjectId(TEST_PR_PROJECT_ID);
        request.setPriority(TEST_PR_INVALID_PRIORITY_LOW); // Valor 0, límite inferior es 1

        try {
            projectRequestDAO.saveProjectRequest(request);
            fail("Expected ValidationException due to priority out of lower bound was not thrown.");
        } catch (ValidationException exception) {
            assertEquals("La prioridad de la solicitud debe ser un valor numérico entre 1 y 3.", exception.getMessage());
        }
    }

    @Test
    public void testSaveProjectRequestPriorityTooHighUnsuccessful() throws DatabaseSystemException {
        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setEnrollment(TEST_PR_ENROLLMENT);
        request.setProjectId(TEST_PR_PROJECT_ID);
        request.setPriority(TEST_PR_INVALID_PRIORITY_HIGH); // Valor 4, límite superior es 3

        try {
            projectRequestDAO.saveProjectRequest(request);
            fail("Expected ValidationException due to priority out of upper bound was not thrown.");
        } catch (ValidationException exception) {
            assertEquals("La prioridad de la solicitud debe ser un valor numérico entre 1 y 3.", exception.getMessage());
        }
    }
}