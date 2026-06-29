package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_STRING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EXPECTED_SINGLE_ROW_AFFECTED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.ROLE_PROFESSOR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATUS_ACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATUS_INACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROF_EMAIL_INACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROF_EMAIL_NEW;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROF_EMAIL_TARGET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROF_FIRST_NAME;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROF_MATERNAL;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROF_PATERNAL;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROF_STAFF_INACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROF_STAFF_NEW;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_PROF_STAFF_TARGET;
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
public class ProfessorDAOTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private ProfessorDAO professorDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        this.professorDAO = new ProfessorDAO();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.executeUpdate("UPDATE Alumno SET numeroPersonalProfesor = NULL WHERE numeroPersonalProfesor IN ('" + TEST_PROF_STAFF_TARGET + "', '" + TEST_PROF_STAFF_NEW + "', '" + TEST_PROF_STAFF_INACTIVE + "')");
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor IN ('" + TEST_PROF_STAFF_TARGET + "', '" + TEST_PROF_STAFF_NEW + "', '" + TEST_PROF_STAFF_INACTIVE + "')");
            statement.executeUpdate("DELETE FROM usuario WHERE email IN ('" + TEST_PROF_EMAIL_TARGET + "', '" + TEST_PROF_EMAIL_NEW + "', '" + TEST_PROF_EMAIL_INACTIVE + "')");
            
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_PROF_EMAIL_TARGET + "', 'pass', '" + ROLE_PROFESSOR + "')");
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_PROF_EMAIL_INACTIVE + "', 'pass', '" + ROLE_PROFESSOR + "')");
            
            statement.executeUpdate("INSERT INTO Profesor (numeroPersonalProfesor, nombre, apellidoPaterno, apellidoMaterno, genero, estado, esCoordinador, email) VALUES ('" + TEST_PROF_STAFF_TARGET + "', '" + TEST_PROF_FIRST_NAME + "', '" + TEST_PROF_PATERNAL + "', '" + TEST_PROF_MATERNAL + "', 'Femenino', '" + STATUS_ACTIVE + "', 0, '" + TEST_PROF_EMAIL_TARGET + "')");
            statement.executeUpdate("INSERT INTO Profesor (numeroPersonalProfesor, nombre, apellidoPaterno, apellidoMaterno, genero, estado, esCoordinador, email) VALUES ('" + TEST_PROF_STAFF_INACTIVE + "', '" + TEST_PROF_FIRST_NAME + "', '" + TEST_PROF_PATERNAL + "', '" + TEST_PROF_MATERNAL + "', 'Femenino', '" + STATUS_ACTIVE + "', 0, '" + TEST_PROF_EMAIL_INACTIVE + "')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("UPDATE Alumno SET numeroPersonalProfesor = NULL WHERE numeroPersonalProfesor IN ('" + TEST_PROF_STAFF_TARGET + "', '" + TEST_PROF_STAFF_NEW + "', '" + TEST_PROF_STAFF_INACTIVE + "')");
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor IN ('" + TEST_PROF_STAFF_TARGET + "', '" + TEST_PROF_STAFF_NEW + "', '" + TEST_PROF_STAFF_INACTIVE + "')");
            statement.executeUpdate("DELETE FROM usuario WHERE email IN ('" + TEST_PROF_EMAIL_TARGET + "', '" + TEST_PROF_EMAIL_NEW + "', '" + TEST_PROF_EMAIL_INACTIVE + "')");
        }
    }

    private String fetchTargetProfessorStaffNumber(List<ProfessorDTO> professors) {
        String foundStaffNumber = EMPTY_STRING;
        for (ProfessorDTO professor : professors) {
            if (TEST_PROF_STAFF_TARGET.equals(professor.getProfessorStaffNumber())) {
                foundStaffNumber = professor.getProfessorStaffNumber();
            }
        }
        return foundStaffNumber;
    }

    @Test
    public void testRegisterProfessorValidDataSuccessful() throws DatabaseSystemException, SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_PROF_EMAIL_NEW + "', 'pass', '" + ROLE_PROFESSOR + "')");
        }

        ProfessorDTO professor = new ProfessorDTO();
        professor.setProfessorStaffNumber(TEST_PROF_STAFF_NEW);
        professor.setFirstName(TEST_PROF_FIRST_NAME);
        professor.setPaternalLastName(TEST_PROF_PATERNAL);
        professor.setMaternalLastName(TEST_PROF_MATERNAL);
        professor.setGender("Masculino");
        professor.setIsCoordinator(false);
        professor.setEmail(TEST_PROF_EMAIL_NEW);
        
        int rowsAffected = professorDAO.registerProfessor(professor);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testRegisterProfessorDuplicateStaffNumberUnsuccessful() throws DatabaseSystemException {
        ProfessorDTO professor = new ProfessorDTO();
        professor.setProfessorStaffNumber(TEST_PROF_STAFF_TARGET);
        professor.setFirstName(TEST_PROF_FIRST_NAME);
        professor.setPaternalLastName(TEST_PROF_PATERNAL);
        professor.setMaternalLastName(TEST_PROF_MATERNAL);
        professor.setGender("Femenino");
        professor.setIsCoordinator(false);
        professor.setEmail(TEST_PROF_EMAIL_NEW); 
        
        try {
            professorDAO.registerProfessor(professor);
            fail("Expected DatabaseSystemException due to duplicated primary key was not thrown");
        } catch (DatabaseSystemException exception) {
            assertEquals("El número de personal '" + TEST_PROF_STAFF_TARGET + "' ya se encuentra asignado a otro docente.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterProfessorDuplicateEmailUnsuccessful() throws DatabaseSystemException {
        ProfessorDTO professor = new ProfessorDTO();
        professor.setProfessorStaffNumber(TEST_PROF_STAFF_NEW);
        professor.setFirstName(TEST_PROF_FIRST_NAME);
        professor.setPaternalLastName(TEST_PROF_PATERNAL);
        professor.setMaternalLastName(TEST_PROF_MATERNAL);
        professor.setGender("Femenino");
        professor.setIsCoordinator(false);
        professor.setEmail(TEST_PROF_EMAIL_TARGET); 
        
        try {
            professorDAO.registerProfessor(professor);
            fail("Expected DatabaseSystemException due to duplicated unique email was not thrown");
        } catch (DatabaseSystemException exception) {
            assertEquals("El correo institucional '" + TEST_PROF_EMAIL_TARGET + "' ya está registrado en el sistema.", exception.getMessage());
        }
    }

    @Test
    public void testInactivateProfessorValidStaffNumberSuccessful() throws DatabaseSystemException {
        int rowsAffected = professorDAO.inactivateProfessor(TEST_PROF_STAFF_INACTIVE);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
        
        ProfessorDTO updatedProfessor = professorDAO.getProfessorByPersonalNumber(TEST_PROF_STAFF_INACTIVE);
        assertEquals(STATUS_INACTIVE, updatedProfessor.getStatus());
    }

    @Test
    public void testUpdateCoordinatorRoleGrantRoleSuccessful() throws DatabaseSystemException {
        int rowsAffected = professorDAO.updateCoordinatorRole(TEST_PROF_STAFF_TARGET, true);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
        
        boolean isNowCoordinator = professorDAO.isCoordinator(TEST_PROF_EMAIL_TARGET);
        assertTrue(isNowCoordinator, "El rol del profesor debería haber cambiado a Coordinador.");
    }

    @Test
    public void testRemoveCoordinatorRoleValidStaffNumberSuccessful() throws DatabaseSystemException {
        professorDAO.updateCoordinatorRole(TEST_PROF_STAFF_TARGET, true);
        
        int rowsAffected = professorDAO.removeCoordinatorRole(TEST_PROF_STAFF_TARGET);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
        
        boolean isStillCoordinator = professorDAO.isCoordinator(TEST_PROF_EMAIL_TARGET);
        assertFalse(isStillCoordinator, "El rol del profesor debería haber sido removido.");
    }

    @Test
    public void testGetProfessorByPersonalNumberExistingDataSuccessful() throws DatabaseSystemException {
        ProfessorDTO professor = professorDAO.getProfessorByPersonalNumber(TEST_PROF_STAFF_TARGET);
        assertEquals(TEST_PROF_EMAIL_TARGET, professor.getEmail());
    }

    @Test
    public void testGetAllProfessorsExistingDataSuccessful() throws DatabaseSystemException {
        List<ProfessorDTO> professors = professorDAO.getAllProfessors();
        String actualStaffNumber = fetchTargetProfessorStaffNumber(professors);
        assertEquals(TEST_PROF_STAFF_TARGET, actualStaffNumber);
    }

    @Test
    public void testGetProfessorByEmailExistingDataSuccessful() throws DatabaseSystemException {
        ProfessorDTO professor = professorDAO.getProfessorByEmail(TEST_PROF_EMAIL_TARGET);
        assertEquals(TEST_PROF_STAFF_TARGET, professor.getProfessorStaffNumber());
    }

    @Test
    public void testHasActiveAssignmentsNoAssignmentsSuccessful() throws DatabaseSystemException {
        boolean hasAssignments = professorDAO.hasActiveAssignments(TEST_PROF_STAFF_TARGET);
        assertFalse(hasAssignments, "El profesor de prueba no debería tener asignaciones activas.");
    }
}