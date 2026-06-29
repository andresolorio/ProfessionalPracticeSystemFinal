package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EducativeExperienceDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @autor andre
 * @author cinth
 */
public class EducativeExperienceControllerTest {
    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private EducativeExperienceController experienceController;

    @BeforeEach
    public void setUp() throws SQLException {
        this.experienceController = new EducativeExperienceController();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM ExperienciaEducativa WHERE numeroPersonalProfesor = '" + TEST_EE_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor = '" + TEST_EE_STAFF_NUMBER + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_EE_PROFESSOR_EMAIL + "'");
            
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_EE_PROFESSOR_EMAIL + "', 'password123', '" + ROLE_PROFESSOR + "')");
            statement.executeUpdate("INSERT INTO Profesor (numeroPersonalProfesor, nombre, apellidoPaterno, genero, estado, email) VALUES ('" + TEST_EE_STAFF_NUMBER + "', 'JUnit', 'Teacher', 'Masculino', '" + STATUS_ACTIVE + "', '" + TEST_EE_PROFESSOR_EMAIL + "')");
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

    @Test
    public void testRegisterExperienceEducativeValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException {
        EducativeExperienceDTO experience = new EducativeExperienceDTO();
        experience.setNrc(TEST_EE_NRC_NEW);
        experience.setEducativeExperienceName(TEST_EE_NAME_NEW);
        experience.setSection(TEST_EE_SECTION);
        experience.setProfessorStaffNumber(TEST_EE_STAFF_NUMBER);
        
        int rowsAffected = experienceController.registerExperienceEducative(experience);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testRegisterExperienceEducativeEmptyNameUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        EducativeExperienceDTO experience = new EducativeExperienceDTO();
        experience.setNrc(TEST_EE_NRC_NEW);
        experience.setEducativeExperienceName(EMPTY_STRING);
        experience.setSection(TEST_EE_SECTION);
        experience.setProfessorStaffNumber(TEST_EE_STAFF_NUMBER);
        
        try {
            experienceController.registerExperienceEducative(experience);
            fail("Expected ValidationException due to empty name constraint");
        } catch (ValidationException exception) {
            assertEquals("Error: El nombre de la experiencia educativa es obligatorio.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterExperienceEducativeExceededNameLengthUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        EducativeExperienceDTO experience = new EducativeExperienceDTO();
        experience.setNrc(TEST_EE_NRC_NEW);
        experience.setEducativeExperienceName("Esta es una cadena generada con "
                + "el unico proposito de exceder el limite maximo de cien caracteres "
                + "permitido en la validacion del controlador para lanzar la excepcion correspondienteeeeeeeeeeeeeeeeeeeeeeeee.");
        experience.setSection(TEST_EE_SECTION);
        experience.setProfessorStaffNumber(TEST_EE_STAFF_NUMBER);
        
        try {
            experienceController.registerExperienceEducative(experience);
            fail("Expected ValidationException due to maximum name length constraint");
        } catch (ValidationException exception) {
            assertEquals("Error: El nombre de la experiencia no puede exceder los 100 caracteres.", exception.getMessage());
        }
    }

    @Test
    public void testGetAllExperiencesExistingDataSuccessful() throws DatabaseSystemException {
        List<EducativeExperienceDTO> experiences = experienceController.getAllExperiences();
        boolean isTestExperienceFound = false;
        
        for (EducativeExperienceDTO experience : experiences) {
            if (TEST_EE_NAME_TARGET.equals(experience.getEducativeExperienceName())) {
                isTestExperienceFound = true;
                break;
            }
        }
        
        assertTrue(isTestExperienceFound, "La experiencia educativa de prueba no se encontró en la lista general.");
    }

    @Test
    public void testGetExperienceEducativeByNrcValidNrcSuccessful() throws EntityNotFoundException, DatabaseSystemException {
        EducativeExperienceDTO experience = experienceController.getExperienceEducativeByNrc(TEST_EE_NRC_TARGET_STRING);
        assertEquals(TEST_EE_NAME_TARGET, experience.getEducativeExperienceName());
    }

    @Test
    public void testGetExperienceEducativeByNrcInvalidPatternUnsuccessful() throws EntityNotFoundException, DatabaseSystemException {
        try {
            experienceController.getExperienceEducativeByNrc(INVALID_NRC_STRING);
            fail("Expected ValidationException due to non-numeric NRC token");
        } catch (ValidationException exception) {
            assertEquals("Error: El NRC debe ser un valor numerico.", exception.getMessage());
        }
    }
}
