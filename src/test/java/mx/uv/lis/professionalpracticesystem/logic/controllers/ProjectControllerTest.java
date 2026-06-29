package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.*;

/**
 *
 * @author cinth
 */
public class ProjectControllerTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private ProjectController projectController;

    @BeforeEach
    public void setUp() throws SQLException {
        this.projectController = new ProjectController();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.executeUpdate("UPDATE Alumno SET idProyecto = NULL WHERE matricula = '" + TEST_PROJECT_STUDENT_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_PROJECT_STUDENT_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_PROJECT_STUDENT_EMAIL + "'");
            
            statement.executeUpdate("DELETE FROM Proyecto WHERE nombreProyecto LIKE 'JUnit %'");
            statement.executeUpdate("DELETE FROM ResponsableTecnico WHERE idResponsable = " + TEST_PROJECT_RESP_ID);
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = " + TEST_PROJECT_ORG_ID);
            
            statement.executeUpdate("INSERT INTO OrganizacionVinculada (idOrganizacionVinculada, nombreEmpresa, direccion, telefono, ciudad, email, sector) VALUES (" + TEST_PROJECT_ORG_ID + ", '" + TEST_PROJECT_ORG_NAME + "', 'Avenida Pruebas 123', '2281112233', 'Xalapa', 'contacto@org.mx', '" + SECTOR_PRIVATE + "')");
            statement.executeUpdate("INSERT INTO ResponsableTecnico (idResponsable, idOrganizacionVinculada, nombre, primerApellido, cargo) VALUES (" + TEST_PROJECT_RESP_ID + ", " + TEST_PROJECT_ORG_ID + ", 'Tech', 'Manager', 'CTO')");
            
            statement.executeUpdate("INSERT INTO Proyecto (nombreProyecto, descripcion, objetivoGeneral, duracion, responsabilidades, recursos, estado, vacantesTotales, vacantesDisponibles, idOrganizacionVinculada, idResponsable) VALUES ('" + TEST_PROJECT_NAME_TARGET + "', '" + TEST_PROJECT_DESC + "', '" + TEST_PROJECT_OBJ_GEN + "', '" + TEST_PROJECT_DURATION + "', '" + TEST_PROJECT_RESPONSIBILITIES + "', '" + TEST_PROJECT_RESOURCES + "', '" + PROJECT_STATUS_ACTIVE + "', " + TEST_PROJECT_VACANCIES + ", " + TEST_PROJECT_VACANCIES + ", " + TEST_PROJECT_ORG_ID + ", " + TEST_PROJECT_RESP_ID + ")");
            
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_PROJECT_STUDENT_EMAIL + "', 'pass123', '" + ROLE_STUDENT + "')");
            statement.executeUpdate("INSERT INTO Alumno (matricula, nombre, apellidoPaterno, genero, estado, periodo, creditosCubiertos, email) VALUES ('" + TEST_PROJECT_STUDENT_ENROLLMENT + "', 'JUnit', 'Student', 'Femenino', '" + STATUS_ACTIVE + "', '" + DEFAULT_PERIOD + "', 120, '" + TEST_PROJECT_STUDENT_EMAIL + "')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("UPDATE Alumno SET idProyecto = NULL WHERE matricula = '" + TEST_PROJECT_STUDENT_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_PROJECT_STUDENT_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_PROJECT_STUDENT_EMAIL + "'");
            statement.executeUpdate("DELETE FROM Proyecto WHERE nombreProyecto LIKE 'JUnit %'");
            statement.executeUpdate("DELETE FROM ResponsableTecnico WHERE idResponsable = " + TEST_PROJECT_RESP_ID);
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = " + TEST_PROJECT_ORG_ID);
        }
    }

    private int fetchGeneratedProjectId() throws SQLException {
        int generatedId = 0;
        String query = "SELECT idProyecto FROM Proyecto WHERE nombreProyecto = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, TEST_PROJECT_NAME_TARGET);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    generatedId = resultSet.getInt("idProyecto");
                }
            }
        }
        return generatedId;
    }

    @Test
    public void testRegisterProjectValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException {
        ProjectDTO project = new ProjectDTO();
        project.setProjectName(TEST_PROJECT_NAME_NEW);
        project.setDescription(TEST_PROJECT_DESC);
        project.setMethodology(TEST_PROJECT_METHODOLOGY);
        project.setGeneralObjective(TEST_PROJECT_OBJ_GEN);
        project.setDuration(TEST_PROJECT_DURATION);
        project.setResponsibilities(TEST_PROJECT_RESPONSIBILITIES);
        project.setIdLinkedOrganization(TEST_PROJECT_ORG_ID);
        project.setIdTechnicalResponsible(TEST_PROJECT_RESP_ID);
        project.setTotalVacancies(TEST_PROJECT_VACANCIES);
        
        int rowsAffected = projectController.registerProject(project);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testRegisterProjectEmptyNameUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ProjectDTO project = new ProjectDTO();
        project.setProjectName(EMPTY_STRING);
        
        try {
            projectController.registerProject(project);
            fail("Expected ValidationException due to empty project name");
        } catch (ValidationException exception) {
            assertEquals("Error: El nombre del proyecto es obligatorio.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterProjectExceededNameLengthUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ProjectDTO project = new ProjectDTO();
        project.setProjectName(TEST_NAME_EXCEEDING_LENGTH);
        
        try {
            projectController.registerProject(project);
            fail("Expected ValidationException due to name length boundary");
        } catch (ValidationException exception) {
            assertEquals("Error: El nombre no puede exceder los " + MAX_NAME_LENGTH + " caracteres.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterProjectEmptyDescriptionUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ProjectDTO project = new ProjectDTO();
        project.setProjectName(TEST_PROJECT_NAME_NEW);
        project.setDescription(EMPTY_STRING);
        
        try {
            projectController.registerProject(project);
            fail("Expected ValidationException due to empty description");
        } catch (ValidationException exception) {
            assertEquals("Error: La descripcion del proyecto es obligatoria.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterProjectEmptyObjectiveUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ProjectDTO project = new ProjectDTO();
        project.setProjectName(TEST_PROJECT_NAME_NEW);
        project.setDescription(TEST_PROJECT_DESC);
        project.setGeneralObjective(EMPTY_STRING);
        
        try {
            projectController.registerProject(project);
            fail("Expected ValidationException due to empty general objective");
        } catch (ValidationException exception) {
            assertEquals("Error: El objetivo general es obligatorio.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterProjectEmptyDurationUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ProjectDTO project = new ProjectDTO();
        project.setProjectName(TEST_PROJECT_NAME_NEW);
        project.setDescription(TEST_PROJECT_DESC);
        project.setGeneralObjective(TEST_PROJECT_OBJ_GEN);
        project.setDuration(EMPTY_STRING);
        
        try {
            projectController.registerProject(project);
            fail("Expected ValidationException due to empty duration");
        } catch (ValidationException exception) {
            assertEquals("Error: Debe especificar la duracion del proyecto.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterProjectEmptyResponsibilitiesUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ProjectDTO project = new ProjectDTO();
        project.setProjectName(TEST_PROJECT_NAME_NEW);
        project.setDescription(TEST_PROJECT_DESC);
        project.setGeneralObjective(TEST_PROJECT_OBJ_GEN);
        project.setDuration(TEST_PROJECT_DURATION);
        project.setResponsibilities(EMPTY_STRING);
        
        try {
            projectController.registerProject(project);
            fail("Expected ValidationException due to empty responsibilities");
        } catch (ValidationException exception) {
            assertEquals("Error: Las responsabilidades son obligatorias.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterProjectInvalidOrganizationIdUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ProjectDTO project = new ProjectDTO();
        project.setProjectName(TEST_PROJECT_NAME_NEW);
        project.setDescription(TEST_PROJECT_DESC);
        project.setGeneralObjective(TEST_PROJECT_OBJ_GEN);
        project.setDuration(TEST_PROJECT_DURATION);
        project.setResponsibilities(TEST_PROJECT_RESPONSIBILITIES);
        project.setIdLinkedOrganization(MIN_VALID_ID);
        
        try {
            projectController.registerProject(project);
            fail("Expected ValidationException due to invalid organization identifier bound");
        } catch (ValidationException exception) {
            assertEquals("Error: El proyecto debe estar vinculado a una organizacion valida.", exception.getMessage());
        }
    }

    @Test
    public void testGetAvailableProjectsExistingDataSuccessful() throws DatabaseSystemException {
        List<ProjectDTO> projects = projectController.getAvailableProjects();
        boolean hasElements = projects.size() > 0;
        assertTrue(hasElements, "Debe existir al menos un proyecto disponible");
    }

    @Test
    public void testGetProjectByIdInvalidIdUnsuccessful() throws EntityNotFoundException, DatabaseSystemException {
        try {
            projectController.getProjectById(MIN_VALID_ID);
            fail("Expected ValidationException due to invalid project identifier");
        } catch (ValidationException exception) {
            assertEquals("Error: El ID del proyecto proporcionado no es valido.", exception.getMessage());
        }
    }

    @Test
    public void testUpdateProjectInvalidIdUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ProjectDTO project = new ProjectDTO();
        project.setIdProject(MIN_VALID_ID);
        project.setProjectName(TEST_PROJECT_NAME_UPDATED);
        project.setDescription(TEST_PROJECT_DESC);
        project.setGeneralObjective(TEST_PROJECT_OBJ_GEN);
        project.setDuration(TEST_PROJECT_DURATION);
        project.setResponsibilities(TEST_PROJECT_RESPONSIBILITIES);
        
        try {
            projectController.updateProject(project);
            fail("Expected ValidationException due to invalid project ID update attempt");
        } catch (ValidationException exception) {
            assertEquals("Error: No se puede actualizar un proyecto sin un identificador valido.", exception.getMessage());
        }
    }

    @Test
    public void testCanDeactivateProjectNoAssignedStudentsSuccessful() throws DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedProjectId();
        boolean canDeactivate = projectController.canDeactivateProject(targetId);
        assertTrue(canDeactivate, "Debería permitir dar de baja un proyecto sin alumnos asignados");
    }

    @Test
    public void testCanDeactivateProjectWithAssignedStudentsSuccessful() throws DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedProjectId();
        // Asignamos al estudiante de prueba al proyecto
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE Alumno SET idProyecto = ? WHERE matricula = ?")) {
            statement.setInt(1, targetId);
            statement.setString(2, TEST_PROJECT_STUDENT_ENROLLMENT);
            statement.executeUpdate();
        }
        
        boolean canDeactivate = projectController.canDeactivateProject(targetId);
        assertFalse(canDeactivate, "No debería permitir dar de baja un proyecto con alumnos asignados");
    }

    @Test
    public void testCanDeactivateProjectInvalidIdUnsuccessful() throws DatabaseSystemException {
        try {
            projectController.canDeactivateProject(MIN_VALID_ID);
            fail("Expected ValidationException due to invalid project ID deactivation check");
        } catch (ValidationException exception) {
            assertEquals("ID de proyecto no válido.", exception.getMessage());
        }
    }

    @Test
    public void testDeactivateProjectValidIdSuccessful() throws DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedProjectId();
        int rowsAffected = projectController.deactivateProject(targetId);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }
}