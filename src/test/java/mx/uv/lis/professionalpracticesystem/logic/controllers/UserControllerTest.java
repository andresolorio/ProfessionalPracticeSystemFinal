package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.BusinessException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.InvalidPasswordException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.InvalidTokenException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.*;

/**
 *
 * @author cinth
 */
public class UserControllerTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private UserController userController;

    @BeforeEach
    public void setUp() throws SQLException {
        this.userController = new UserController();
        try (Connection connection = databaseConnection.getConnection(); Statement statement = connection.createStatement()) {

            statement.executeUpdate("UPDATE Alumno SET numeroPersonalProfesor = NULL WHERE matricula IN ('" + TEST_UC_STUDENT_ENROLLMENT + "', '" + TEST_UC_INACTIVE_ENROLLMENT + "')");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula IN ('" + TEST_UC_STUDENT_ENROLLMENT + "', '" + TEST_UC_INACTIVE_ENROLLMENT + "')");
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor IN ('" + TEST_UC_STAFF_NUMBER + "', '" + TEST_UC_NEW_STAFF_NUMBER + "')");
            statement.executeUpdate("DELETE FROM usuario WHERE email IN ('" + TEST_UC_STUDENT_EMAIL + "', '" + TEST_UC_INACTIVE_STUDENT_EMAIL + "', '" + TEST_UC_PROFESSOR_EMAIL + "', '" + TEST_UC_NEW_PROFESSOR_EMAIL + "')");
            
            String dynamicHash = mx.uv.lis.professionalpracticesystem.logic.utils.PasswordManager.hashPassword(TEST_UC_PASSWORD_PLAIN);
            
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol, tokenRecuperacion, tokenExpiracion) VALUES ('" + TEST_UC_STUDENT_EMAIL + "', '" + dynamicHash + "', '" + ROLE_STUDENT + "', '" + TEST_UC_RECOVERY_TOKEN + "', DATE_ADD(NOW(), INTERVAL 1 DAY))");
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_UC_INACTIVE_STUDENT_EMAIL + "', '" + dynamicHash + "', '" + ROLE_STUDENT + "')");
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_UC_PROFESSOR_EMAIL + "', '" + dynamicHash + "', '" + ROLE_PROFESSOR + "')");
            
            statement.executeUpdate("INSERT INTO Profesor (numeroPersonalProfesor, nombre, apellidoPaterno, genero, estado, esCoordinador, email) VALUES ('" + TEST_UC_STAFF_NUMBER + "', 'UserCtrl', 'Prof', 'Masculino', '" + STATUS_ACTIVE + "', 0, '" + TEST_UC_PROFESSOR_EMAIL + "')");
            statement.executeUpdate("INSERT INTO Alumno (matricula, nombre, apellidoPaterno, genero, estado, periodo, creditosCubiertos, email) VALUES ('" + TEST_UC_STUDENT_ENROLLMENT + "', 'UserCtrl', 'Student', 'Femenino', '" + STATUS_ACTIVE + "', '" + DEFAULT_PERIOD + "', 120, '" + TEST_UC_STUDENT_EMAIL + "')");
            statement.executeUpdate("INSERT INTO Alumno (matricula, nombre, apellidoPaterno, genero, estado, periodo, creditosCubiertos, email) VALUES ('" + TEST_UC_INACTIVE_ENROLLMENT + "', 'UserCtrl', 'Inactive', 'Masculino', 'Inactivo', '" + DEFAULT_PERIOD + "', 120, '" + TEST_UC_INACTIVE_STUDENT_EMAIL + "')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("UPDATE Alumno SET numeroPersonalProfesor = NULL WHERE matricula IN ('" + TEST_UC_STUDENT_ENROLLMENT + "', '" + TEST_UC_INACTIVE_ENROLLMENT + "')");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula IN ('" + TEST_UC_STUDENT_ENROLLMENT + "', '" + TEST_UC_INACTIVE_ENROLLMENT + "')");
            statement.executeUpdate("DELETE FROM Profesor WHERE numeroPersonalProfesor IN ('" + TEST_UC_STAFF_NUMBER + "', '" + TEST_UC_NEW_STAFF_NUMBER + "')");
            statement.executeUpdate("DELETE FROM usuario WHERE email IN ('" + TEST_UC_STUDENT_EMAIL + "', '" + TEST_UC_INACTIVE_STUDENT_EMAIL + "', '" + TEST_UC_PROFESSOR_EMAIL + "', '" + TEST_UC_NEW_PROFESSOR_EMAIL + "')");
        }
    }


    @Test
    public void testLoginValidCredentialsSuccessful() throws DatabaseSystemException, InvalidPasswordException {
        UserDTO user = userController.login(TEST_UC_STUDENT_EMAIL, TEST_UC_PASSWORD_PLAIN);
        assertNotNull(user, "El usuario debería recuperar su información al hacer login");
        assertEquals(ROLE_STUDENT, user.getRole());
    }

    @Test
    public void testLoginInvalidPasswordUnsuccessful() throws DatabaseSystemException {
        try {
            userController.login(TEST_UC_STUDENT_EMAIL, TEST_UC_PASSWORD_WRONG);
            fail("Expected InvalidPasswordException was not thrown");
        } catch (InvalidPasswordException exception) {
            assertEquals("La contraseña ingresada es incorrecta.", exception.getMessage());
        }
    }

    @Test
    public void testLoginInactiveStudentUnsuccessful() throws DatabaseSystemException, InvalidPasswordException {
        try {
            userController.login(TEST_UC_INACTIVE_STUDENT_EMAIL, TEST_UC_PASSWORD_PLAIN);
            fail("Expected BusinessException due to inactive student role was not thrown");
        } catch (BusinessException exception) {
            assertEquals("No es posible ingresar al sistema debido a que su estado actual es Inactivo. Favor de acudir con el Coordinador.", exception.getMessage());
        }
    }


    @Test
    public void testRegisterStudentNewUserSuccessful() throws DatabaseSystemException {
        StudentDTO newStudent = new StudentDTO();
        newStudent.setEnrollmentId("S11111111");
        newStudent.setFirstName("Nuevo");
        newStudent.setPaternalLastName("Estudiante");
        newStudent.setGender("Masculino");
        newStudent.setStatus(STATUS_ACTIVE);
        newStudent.setPeriod(DEFAULT_PERIOD);
        newStudent.setCoveredCredits(120);
        newStudent.setEmail("new.student@uv.mx");

        UserDTO newUser = new UserDTO();
        newUser.setEmail("new.student@uv.mx");
        newUser.setPassword(TEST_UC_PASSWORD_PLAIN);
        newUser.setRole(ROLE_STUDENT);

        boolean isRegistered = userController.registerStudent(newStudent, newUser);
        assertTrue(isRegistered, "El estudiante y su usuario deberían registrarse correctamente en una transacción");
        
        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Alumno WHERE matricula = 'S11111111'");
            stmt.executeUpdate("DELETE FROM usuario WHERE email = 'new.student@uv.mx'");
        } catch (SQLException ignored) {}
    }

    @Test
    public void testRegisterStudentAlreadyActiveUnsuccessful() {
        StudentDTO existingStudent = new StudentDTO();
        existingStudent.setEnrollmentId(TEST_UC_STUDENT_ENROLLMENT); // Matrícula que ya existe y está Activa
        existingStudent.setEmail(TEST_UC_STUDENT_EMAIL);
        
        UserDTO existingUser = new UserDTO();
        existingUser.setEmail(TEST_UC_STUDENT_EMAIL);

        try {
            userController.registerStudent(existingStudent, existingUser);
            fail("Expected DatabaseSystemException due to re-registration of an active student");
        } catch (DatabaseSystemException exception) {
            assertTrue(exception.getMessage().contains("ya está activo"), "Debería bloquear el registro si el estudiante ya está activo");
        }
    }

    @Test
    public void testRegisterProfessorNewUserSuccessful() throws DatabaseSystemException {
        ProfessorDTO newProfessor = new ProfessorDTO();
        newProfessor.setProfessorStaffNumber(TEST_UC_NEW_STAFF_NUMBER);
        newProfessor.setFirstName("Nuevo");
        newProfessor.setPaternalLastName("Profesor");
        newProfessor.setGender("Femenino");
        newProfessor.setEmail(TEST_UC_NEW_PROFESSOR_EMAIL);
        newProfessor.setIsCoordinator(false);
        
        UserDTO newUser = new UserDTO();
        newUser.setEmail(TEST_UC_NEW_PROFESSOR_EMAIL);
        newUser.setPassword(TEST_UC_PASSWORD_PLAIN);
        newUser.setRole(ROLE_PROFESSOR);

        boolean isRegistered = userController.registerProfessor(newProfessor, newUser);
        assertTrue(isRegistered, "El profesor y su usuario deberían registrarse correctamente en una transacción");
    }

    @Test
    public void testRegisterProfessorDuplicatedEmailUnsuccessful() {
        ProfessorDTO professor = new ProfessorDTO();
        professor.setEmail(TEST_UC_PROFESSOR_EMAIL); 
        
        UserDTO user = new UserDTO();
        user.setEmail(TEST_UC_PROFESSOR_EMAIL);

        try {
            userController.registerProfessor(professor, user);
            fail("Expected DatabaseSystemException due to duplicated email constraint");
        } catch (DatabaseSystemException exception) {
            assertTrue(exception.getMessage().contains("ya está registrado en el sistema"), "Debería abortar la transacción por correo duplicado");
        }
    }


    @Test
    public void testGenerateRecoveryTokenValidEmailSuccessful() throws DatabaseSystemException, EntityNotFoundException {
        userController.generateRecoveryToken(TEST_UC_STUDENT_EMAIL);
    }

    @Test
    public void testGenerateRecoveryTokenInvalidEmailUnsuccessful() throws DatabaseSystemException {
        try {
            userController.generateRecoveryToken("fantasma@uv.mx");
            fail("Expected EntityNotFoundException due to unregistered email");
        } catch (EntityNotFoundException exception) {
            assertEquals("El correo electrónico ingresado no se encuentra registrado.", exception.getMessage());
        }
    }

    @Test
    public void testResetPasswordValidTokenSuccessful() throws DatabaseSystemException, InvalidTokenException {
        userController.resetPassword(TEST_UC_RECOVERY_TOKEN, "NuevaContrasena321");
    }

    @Test
    public void testResetPasswordInvalidTokenUnsuccessful() throws DatabaseSystemException {
        try {
            userController.resetPassword("TOKENFALSO", "NuevaContrasena321");
            fail("Expected InvalidTokenException due to non-matching token");
        } catch (InvalidTokenException exception) {
            assertTrue(exception.getMessage().contains("incorrecto"), "Debe rechazar tokens inválidos o expirados.");
        }
    }


    @Test
    public void testCheckSystemUserDensitySuccessful() throws DatabaseSystemException {
        int userCount = userController.checkSystemUserDensity();
        assertTrue(userCount >= 3, "Debería retornar al menos los 3 usuarios base inyectados en la prueba");
    }
}