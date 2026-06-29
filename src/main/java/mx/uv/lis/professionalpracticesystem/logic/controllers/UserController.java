package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import static java.util.UUID.randomUUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.BusinessException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.InvalidPasswordException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.InvalidTokenException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.UserDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IUserDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.EmailManager;
import mx.uv.lis.professionalpracticesystem.logic.utils.PasswordManager;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;

/**
 * 
 * @author andre
 */
public class UserController {
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());
    private final UserDAO userDAO;
    private final StudentDAO studentDAO;
    private final ProfessorDAO professorDAO;
    public static String currentStudentEnrollment;
    
    private final DatabaseConnection databaseConnection;

    public UserController() {
        this.userDAO = new UserDAO();
        this.studentDAO = new StudentDAO();
        this.professorDAO = new ProfessorDAO();
        this.databaseConnection = new DatabaseConnection();
    }

    public UserDTO login(String email, String password) throws DatabaseSystemException, InvalidPasswordException {
        UserDTO user = this.userDAO.getUserByEmail(email);

        if (user != null) {
            boolean isPasswordCorrect = PasswordManager.checkPassword(password, user.getPassword());
            if (isPasswordCorrect) {
                if (SystemConstants.ROLE_PROFESSOR.equals(user.getRole())) {
                    user = this.checkProfessorSpecificRole(user);
                }
                
                if (SystemConstants.ROLE_STUDENT.equals(user.getRole())) {
                    try {
                        StudentDTO student = this.studentDAO.getStudentByEmail(email);
                        if (student != null) {
                            if (SystemConstants.STATUS_INACTIVE.equals(student.getStatus())) {
                                LOGGER.log(Level.WARNING, "Login blocked: Student account with email {0} is Inactive.", email);
                                throw new BusinessException("No es posible ingresar al sistema debido a que su estado actual es Inactivo. Favor de acudir con el Coordinador.");
                            }
                            
                            currentStudentEnrollment = student.getEnrollmentId();
                        }
                    } catch (DatabaseSystemException exception) {
                        LOGGER.log(Level.SEVERE, "Failed to recover student transaction context during authentication process", exception);
                        throw exception;
                    }
                }
                
                LOGGER.log(Level.INFO, "Successful login for: {0} as {1}", new Object[]{email, user.getRole()});
            } else {
                LOGGER.log(Level.WARNING, "Login failed: Incorrect password for user {0}", email);
                throw new InvalidPasswordException("La contraseña ingresada es incorrecta.");
            }
        }
        return user;
    }

    private UserDTO checkProfessorSpecificRole(UserDTO user) throws DatabaseSystemException {
        try {
            if (this.professorDAO.isCoordinator(user.getEmail())) {
                user.setRole(SystemConstants.ROLE_COORDINATOR);
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Database connection failure while verifying professor coordinator role validation", exception);
            throw new DatabaseSystemException("Error técnico de comunicación al validar los privilegios de acceso.", exception);
        }
        return user;
    }

    public boolean registerStudent(StudentDTO student, UserDTO user) throws DatabaseSystemException {
        Connection connection = null;
        try {
            connection = this.databaseConnection.getConnection();
            connection.setAutoCommit(false);

            StudentDTO existingStudent = this.studentDAO.getStudentByEnrollment(student.getEnrollmentId());

            if (existingStudent != null && SystemConstants.STATUS_ACTIVE.equals(existingStudent.getStatus())) {
                LOGGER.log(Level.WARNING, "Restricción de persistencia: Intento de re-registro para la matrícula activa: {0}", student.getEnrollmentId());
                throw new DatabaseSystemException("El alumno con matrícula " + student.getEnrollmentId() + " ya está activo.");
            }

            UserDTO existingUser = this.userDAO.getUserByEmail(user.getEmail(), connection);

            if (existingUser != null) {
                this.userDAO.updateUser(user, connection);
            } else {
                this.userDAO.saveUser(user, connection);
            }

            if (existingStudent != null) {
                this.studentDAO.updateStudentData(student, connection);
            } else {
                this.studentDAO.registerStudent(student, connection);
            }

            connection.commit();
            return true;
        } catch (SQLException exception) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Rollback failed", ex);
                }
            }
            throw new DatabaseSystemException("Error crítico en la transacción de registro.", exception);
        } finally {
            this.closeConnection(connection);
        }
    }

    private void performRollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException exception) {
                LOGGER.log(Level.SEVERE, "Fallo crítico al realizar rollback.", exception);
            }
        }
    }

    public boolean registerProfessor(ProfessorDTO professor, UserDTO user) throws DatabaseSystemException {
        Connection connection = null;
        boolean success = false;

        try {
            connection = this.databaseConnection.getConnection();
            connection.setAutoCommit(false);

            LOGGER.log(Level.INFO, "Validando disponibilidad del correo institucional: {0}", user.getEmail());
            if (this.userDAO.getUserByEmail(user.getEmail()) != null) {
                LOGGER.log(Level.WARNING, "Registro rechazado: El correo electrónico {0} ya se encuentra ocupado.", user.getEmail());
                throw new DatabaseSystemException("El correo institucional '" + user.getEmail() + "' ya está registrado en el sistema.");
            }

            this.userDAO.saveUser(user, connection);
            success = this.professorDAO.registerProfessor(professor, connection) > RESET;

            connection.commit();
            LOGGER.log(Level.INFO, "Transaction successful: Professor and User registered.");
        
        } catch (DatabaseSystemException exception) {
            if (connection != null) {
                try {
                    connection.rollback();
                    LOGGER.log(Level.WARNING, "Transaction rolled back due to duplicate identity constraints.");
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Critical failure during business exception rollback.", rollbackEx);
                }
            }
            throw exception;
            
        } catch (SQLException exception) {
            if (connection != null) {
                try {
                    connection.rollback();
                    LOGGER.log(Level.SEVERE, "Transaction failed: Rolling back professor registration due to infrastructure failure.", exception);
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Critical error: Failed to rollback connection.", rollbackEx);
                }
            }
            throw new DatabaseSystemException("Error técnico de infraestructura: No se pudo conectar de forma rentable con la base de datos.", exception);
        } finally {
            this.closeConnection(connection);
        }
        return success;
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException exception) {
                LOGGER.log(Level.SEVERE, "Failed to close database connection safely.", exception);
            }
        }
    }
    
    public void generateRecoveryToken(String email) throws DatabaseSystemException, EntityNotFoundException {
        UserDTO user = this.userDAO.getUserByEmail(email);
        if (user == null) {
            LOGGER.log(Level.WARNING, "Recovery transmission aborted: Email target {0} does not exist in registry.", email);
            throw new EntityNotFoundException("El correo electrónico ingresado no se encuentra registrado.");
        }

        String token = randomUUID().toString().substring(0, 6).toUpperCase();

        try {
            this.userDAO.saveRecoveryToken(email, token);
            EmailManager.sendRecoveryEmail(email, token);
            LOGGER.log(Level.INFO, "Recovery safe token generated and deployed successfully for target user: {0}", email);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Persistence transaction failure during recovery token generation routine", exception);
            throw exception;
        }
    }

    public void resetPassword(String token, String newPassword) throws DatabaseSystemException, InvalidTokenException {
        try {
            String newPasswordHash = PasswordManager.hashPassword(newPassword);
            boolean isSuccessful = this.userDAO.validateTokenAndResetPassword(token, newPasswordHash);
            
            if (!isSuccessful) {
                LOGGER.log(Level.WARNING, "Restoration attempt failed: Unique token is invalid or expired ({0})", token);
                throw new InvalidTokenException("El código de verificación es incorrecto, ya fue utilizado o ha expirado.");
            }
            
            LOGGER.log(Level.INFO, "The password credential state was successfully updated and synchronized in the engine.");
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Persistence mechanism error while executing user safe password update process", exception);
            throw exception;
        }
    }
    
    public boolean isCoordinatorAlreadyRegistered() throws DatabaseSystemException {
        LOGGER.log(Level.INFO, "Routing logical request towards infrastructure layer to verify coordinator unique constraint.");
        ProfessorDAO localProfessorDAO = new ProfessorDAO();
        return localProfessorDAO.isCoordinatorAlreadyRegistered();
    }
    
    public int checkSystemUserDensity() throws DatabaseSystemException {
        IUserDAO userDAO = new UserDAO();
        
        LOGGER.log(Level.INFO, "Executing system user identity density check bypass transaction.");
        return userDAO.getUserCount();
    }

    public boolean registerAdmin(UserDTO admin) 
            throws DatabaseSystemException {
        boolean isRegistered = false;
        
        try (Connection connection = this.databaseConnection.getConnection()) {
            
            LOGGER.log(Level.INFO, "Executing persistence transaction " 
                    + "for root administrator account.");
            
            this.userDAO.saveUser(admin, connection);
            isRegistered = true;
            
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "SQL connection pipeline failure " 
                    + "during root administrator persistence routine.", 
                    exception);
            
            throw new DatabaseSystemException("No se pudo completar el " 
                    + "registro del administrador raíz en el servidor. " 
                    + "Inténtelo más tarde.", exception);
        }
        return isRegistered;
    }
}