package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DEFAULT_PERIOD;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EXPECTED_SINGLE_ROW_AFFECTED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.FIRST_ELEMENT_INDEX;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MIN_VALID_ID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PROJECT_STATUS_ACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.ROLE_STUDENT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.SECTOR_PRIVATE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATUS_ACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_DESC;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_DURATION;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_METHODOLOGY;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_NAME_NEW;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_NAME_TARGET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_NAME_UPDATED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_OBJ_GEN;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_OBJ_IMM;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_OBJ_MED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_ORG_ID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_ORG_NAME;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_RESOURCES;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_RESPONSIBILITIES;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_RESP_ID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_STUDENT_EMAIL;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_STUDENT_ENROLLMENT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROJECT_VACANCIES;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @autor andre
 * @author cinth
 */
public class ProjectDAOTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private ProjectDAO projectDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        this.projectDAO = new ProjectDAO(databaseConnection);
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.executeUpdate("DELETE FROM SolicitudProyecto WHERE matricula = '" + TEST_PROJECT_STUDENT_ENROLLMENT + "'");
            statement.executeUpdate("UPDATE Alumno SET idProyecto = NULL WHERE matricula = '" + TEST_PROJECT_STUDENT_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_PROJECT_STUDENT_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_PROJECT_STUDENT_EMAIL + "'");
            
            statement.executeUpdate("DELETE FROM Proyecto WHERE nombreProyecto LIKE 'JUnit %'");
            statement.executeUpdate("DELETE FROM ResponsableTecnico WHERE idResponsable = " + TEST_PROJECT_RESP_ID);
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = " + TEST_PROJECT_ORG_ID);
            
            statement.executeUpdate("INSERT INTO OrganizacionVinculada (idOrganizacionVinculada, nombreEmpresa, direccion, telefono, ciudad, email, sector) VALUES (" + TEST_PROJECT_ORG_ID + ", '" + TEST_PROJECT_ORG_NAME + "', 'Avenida Pruebas 123', '2281112233', 'Xalapa', 'contacto@org.mx', '" + SECTOR_PRIVATE + "')");
            statement.executeUpdate("INSERT INTO ResponsableTecnico (idResponsable, idOrganizacionVinculada, nombre, primerApellido, cargo) VALUES (" + TEST_PROJECT_RESP_ID + ", " + TEST_PROJECT_ORG_ID + ", 'Tech', 'Manager', 'CTO')");
            
            statement.executeUpdate("INSERT INTO Proyecto (nombreProyecto, descripcion, objetivoGeneral, duracion, estado, vacantesTotales, vacantesDisponibles, idOrganizacionVinculada, idResponsable) VALUES ('" + TEST_PROJECT_NAME_TARGET + "', '" + TEST_PROJECT_DESC + "', '" + TEST_PROJECT_OBJ_GEN + "', '" + TEST_PROJECT_DURATION + "', '" + PROJECT_STATUS_ACTIVE + "', " + TEST_PROJECT_VACANCIES + ", " + TEST_PROJECT_VACANCIES + ", " + TEST_PROJECT_ORG_ID + ", " + TEST_PROJECT_RESP_ID + ")");
            
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_PROJECT_STUDENT_EMAIL + "', 'pass123', '" + ROLE_STUDENT + "')");
            statement.executeUpdate("INSERT INTO Alumno (matricula, nombre, apellidoPaterno, genero, estado, periodo, creditosCubiertos, email) VALUES ('" + TEST_PROJECT_STUDENT_ENROLLMENT + "', 'JUnit', 'Student', 'Femenino', '" + STATUS_ACTIVE + "', '" + DEFAULT_PERIOD + "', 120, '" + TEST_PROJECT_STUDENT_EMAIL + "')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM SolicitudProyecto WHERE matricula = '" + TEST_PROJECT_STUDENT_ENROLLMENT + "'");
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
    public void testSaveProjectValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException {
        ProjectDTO project = new ProjectDTO();
        project.setProjectName(TEST_PROJECT_NAME_NEW);
        project.setDescription(TEST_PROJECT_DESC);
        project.setMethodology(TEST_PROJECT_METHODOLOGY);
        project.setGeneralObjective(TEST_PROJECT_OBJ_GEN);
        project.setImmediateObjective(TEST_PROJECT_OBJ_IMM);
        project.setMediatedObjective(TEST_PROJECT_OBJ_MED);
        project.setResponsibilities(TEST_PROJECT_RESPONSIBILITIES);
        project.setResources(TEST_PROJECT_RESOURCES);
        project.setDuration(TEST_PROJECT_DURATION);
        project.setIdLinkedOrganization(TEST_PROJECT_ORG_ID);
        project.setIdTechnicalResponsible(TEST_PROJECT_RESP_ID);
        project.setTotalVacancies(TEST_PROJECT_VACANCIES);
        
        int rowsAffected = projectDAO.saveProject(project);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testSaveProjectInvalidForeignKeyUnsuccessful() throws DataIntegrityException {
        ProjectDTO project = new ProjectDTO();
        project.setProjectName(TEST_PROJECT_NAME_NEW);
        project.setDescription(TEST_PROJECT_DESC);
        project.setGeneralObjective(TEST_PROJECT_OBJ_GEN);
        project.setDuration(TEST_PROJECT_DURATION);
        project.setIdLinkedOrganization(MIN_VALID_ID); 
        project.setIdTechnicalResponsible(TEST_PROJECT_RESP_ID);
        project.setTotalVacancies(TEST_PROJECT_VACANCIES);
        
        try {
            projectDAO.saveProject(project);
            fail("Expected DatabaseSystemException due to foreign key violation was not thrown");
        } catch (DatabaseSystemException exception) {
            assertEquals("Error tecnico al registrar el proyecto.", exception.getMessage());
        }
    }

    @Test
    public void testGetAllAvailableProjectsExistingDataSuccessful() throws DatabaseSystemException {
        List<ProjectDTO> projects = projectDAO.getAllAvailableProjects();
        boolean hasElements = projects.size() > RESET;
        assertTrue(hasElements, "La lista de proyectos disponibles no debe estar vacía");
    }

    @Test
    public void testGetProjectByIdExistingIdSuccessful() throws EntityNotFoundException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedProjectId();
        ProjectDTO project = projectDAO.getProjectById(targetId);
        assertEquals(TEST_PROJECT_NAME_TARGET, project.getProjectName());
    }

    @Test
    public void testGetProjectByIdNonExistentIdUnsuccessful() throws DatabaseSystemException {
        try {
            projectDAO.getProjectById(MIN_VALID_ID);
            fail("Expected EntityNotFoundException was not thrown");
        } catch (EntityNotFoundException exception) {
            assertEquals("No se encontro el proyecto con ID: " + MIN_VALID_ID, exception.getMessage());
        }
    }

    @Test
    public void testUpdateProjectValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedProjectId();
        ProjectDTO project = new ProjectDTO();
        project.setIdProject(targetId);
        project.setProjectName(TEST_PROJECT_NAME_UPDATED);
        project.setDescription(TEST_PROJECT_DESC);
        project.setMethodology(TEST_PROJECT_METHODOLOGY);
        project.setGeneralObjective(TEST_PROJECT_OBJ_GEN);
        project.setImmediateObjective(TEST_PROJECT_OBJ_IMM);
        project.setMediatedObjective(TEST_PROJECT_OBJ_MED);
        project.setResponsibilities(TEST_PROJECT_RESPONSIBILITIES);
        project.setResources(TEST_PROJECT_RESOURCES);
        project.setDuration(TEST_PROJECT_DURATION);
        project.setIdLinkedOrganization(TEST_PROJECT_ORG_ID);
        project.setIdTechnicalResponsible(TEST_PROJECT_RESP_ID);
        project.setTotalVacancies(TEST_PROJECT_VACANCIES);
        
        int rowsAffected = projectDAO.updateProject(project);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testDeactivateProjectValidIdSuccessful() throws DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedProjectId();
        int rowsAffected = projectDAO.deactivateProject(targetId);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testAssignProjectToStudentValidDataSuccessful() throws DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedProjectId();
        int rowsAffected = projectDAO.assignProjectToStudent(TEST_PROJECT_STUDENT_ENROLLMENT, targetId);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testIsStudentAlreadyAssignedAssignedSuccessful() throws DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedProjectId();
        projectDAO.assignProjectToStudent(TEST_PROJECT_STUDENT_ENROLLMENT, targetId);
        
        boolean isAssigned = projectDAO.isStudentAlreadyAssigned(TEST_PROJECT_STUDENT_ENROLLMENT);
        assertTrue(isAssigned);
    }

    @Test
    public void testIsStudentAlreadyAssignedNotAssignedSuccessful() throws DatabaseSystemException {
        boolean isAssigned = projectDAO.isStudentAlreadyAssigned(TEST_PROJECT_STUDENT_ENROLLMENT);
        assertFalse(isAssigned);
    }

    @Test
    public void testGetAssignedStudentsCountExistingAssignmentsSuccessful() throws DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedProjectId();
        projectDAO.assignProjectToStudent(TEST_PROJECT_STUDENT_ENROLLMENT, targetId);
        
        int assignedCount = projectDAO.getAssignedStudentsCount(targetId);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, assignedCount);
    }

    @Test
    public void testSaveProjectRequestsValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedProjectId();
        ProjectDTO project = new ProjectDTO();
        project.setIdProject(targetId);
        
        List<ProjectDTO> requests = new ArrayList<>();
        requests.add(project);
        
        projectDAO.saveProjectRequests(TEST_PROJECT_STUDENT_ENROLLMENT, requests);
        
        boolean hasSelections = projectDAO.isStudentAlreadyRegisteredInRequest(TEST_PROJECT_STUDENT_ENROLLMENT);
        assertTrue(hasSelections);
    }

    @Test
    public void testGetRequestedProjectsByStudentExistingDataSuccessful() throws DataIntegrityException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedProjectId();
        ProjectDTO project = new ProjectDTO();
        project.setIdProject(targetId);
        List<ProjectDTO> requests = new ArrayList<>();
        requests.add(project);
        projectDAO.saveProjectRequests(TEST_PROJECT_STUDENT_ENROLLMENT, requests);
        
        List<ProjectDTO> retrievedRequests = projectDAO.getRequestedProjectsByStudent(TEST_PROJECT_STUDENT_ENROLLMENT);
        assertEquals(targetId, retrievedRequests.get(FIRST_ELEMENT_INDEX).getIdProject());
    }

    @Test
    public void testGetAllProjectsExistingDataSuccessful() throws DatabaseSystemException {
        List<ProjectDTO> projects = projectDAO.getAllProjects();
        boolean hasElements = projects.size() > RESET;
        assertTrue(hasElements);
    }
}