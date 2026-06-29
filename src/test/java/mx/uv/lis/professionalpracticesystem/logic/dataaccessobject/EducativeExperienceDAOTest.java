package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EducativeExperienceDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_STRING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EXPECTED_FULL_PROFESSOR_NAME;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EXPECTED_SINGLE_ROW_AFFECTED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.FIRST_ELEMENT_INDEX;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.ROLE_PROFESSOR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATUS_ACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_EE_NAME_NEW;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_EE_NAME_TARGET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_EE_NAME_UPDATED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_EE_NRC_NEW;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_EE_NRC_NON_EXISTENT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_EE_NRC_TARGET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_EE_NRC_TARGET_STRING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_EE_PROFESSOR_EMAIL;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_EE_SECTION;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_EE_STAFF_NUMBER;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @autor andre
 * @author cinth
 */
public class EducativeExperienceDAOTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private EducativeExperienceDAO experienceDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        this.experienceDAO = new EducativeExperienceDAO();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM ExperienciaEducativa WHERE numeroPersonalProfesor = '" + TEST_EE_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor = '" + TEST_EE_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_EE_PROFESSOR_EMAIL + "'");

            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_EE_PROFESSOR_EMAIL + "', 'password123', '" + ROLE_PROFESSOR + "')");
            statement.executeUpdate("INSERT INTO Profesor (numeroPersonalProfesor, nombre, apellidoPaterno, genero, estado, email) "
                    + "VALUES ('" + TEST_EE_STAFF_NUMBER + "', 'JUnit', 'Teacher', 'Masculino', '" + STATUS_ACTIVE + "', '" + TEST_EE_PROFESSOR_EMAIL + "')");
            statement.executeUpdate("INSERT INTO ExperienciaEducativa (nrc, nombreExperienciaEducativa, seccion, numeroPersonalProfesor) VALUES (" + TEST_EE_NRC_TARGET + ", '" + TEST_EE_NAME_TARGET + "', '" + TEST_EE_SECTION + "', '" + TEST_EE_STAFF_NUMBER + "')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM ExperienciaEducativa WHERE numeroPersonalProfesor = '" + TEST_EE_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor = '" + TEST_EE_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_EE_PROFESSOR_EMAIL + "'");
        }
    }

    private String fetchTargetExperienceName(List<EducativeExperienceDTO> experiences) {
        String foundName = EMPTY_STRING;
        for (EducativeExperienceDTO experience : experiences) {
            if (TEST_EE_NAME_TARGET.equals(experience.getEducativeExperienceName())) {
                foundName = experience.getEducativeExperienceName();
            }
        }
        return foundName;
    }

    @Test
    public void testRegisterEducativeExperienceValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException {
        EducativeExperienceDTO experience = new EducativeExperienceDTO();
        experience.setNrc(TEST_EE_NRC_NEW);
        experience.setEducativeExperienceName(TEST_EE_NAME_NEW);
        experience.setSection(TEST_EE_SECTION);
        experience.setProfessorStaffNumber(TEST_EE_STAFF_NUMBER);
        
        int rowsAffected = experienceDAO.registerEducativeExperience(experience);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testGetAllEducativeExperiencesWithProfessorsExistingDataSuccessful() throws DatabaseSystemException {
        List<EducativeExperienceDTO> experiences = experienceDAO.getAllEducativeExperiencesWithProfessors();
        String actualExperienceName = fetchTargetExperienceName(experiences);
        assertEquals(TEST_EE_NAME_TARGET, actualExperienceName);
    }

    @Test
    public void testGetEducativeExperienceWithProfessorByNrcExistingNrcSuccessful() throws EntityNotFoundException, DatabaseSystemException {
        EducativeExperienceDTO experience = experienceDAO.getEducativeExperienceWithProfessorByNrc(TEST_EE_NRC_TARGET_STRING);
        assertEquals(TEST_EE_NAME_TARGET, experience.getEducativeExperienceName());
    }

    @Test
    public void testGetEducativeExperienceWithProfessorByNrcNonExistentNrcUnsuccessful() throws DatabaseSystemException {
        try {
            experienceDAO.getEducativeExperienceWithProfessorByNrc(TEST_EE_NRC_NON_EXISTENT);
            fail("Expected EntityNotFoundException was not thrown");
        } catch (EntityNotFoundException exception) {
            assertEquals("No se encontró la experiencia con NRC: " + TEST_EE_NRC_NON_EXISTENT, exception.getMessage());
        }
    }

    @Test
    public void testUpdateEducativeExperienceValidDataSuccessful() throws DatabaseSystemException {
        EducativeExperienceDTO experience = new EducativeExperienceDTO();
        experience.setNrc(TEST_EE_NRC_TARGET);
        experience.setEducativeExperienceName(TEST_EE_NAME_UPDATED);
        experience.setSection(TEST_EE_SECTION);
        experience.setProfessorStaffNumber(TEST_EE_STAFF_NUMBER);
        
        int rowsAffected = experienceDAO.updateEducativeExperience(experience);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testGetEducativeExperiencesWithProfessorsByProfessorEmailValidEmailSuccessful() throws DatabaseSystemException {
        List<EducativeExperienceDTO> experiences = experienceDAO.getEducativeExperiencesWithProfessorsByProfessorEmail(TEST_EE_PROFESSOR_EMAIL);
        assertEquals(EXPECTED_FULL_PROFESSOR_NAME, experiences.get(FIRST_ELEMENT_INDEX).getProfessorName());
    }
}