package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IUserDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;
import static mx.uv.lis.professionalpracticesystem.logic.utils.PasswordManager.hashPassword;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;

/**
 *
 * @author cinth
 * @author andre
 */
public class UserDAO implements IUserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public UserDAO() {
        this.databaseConnection = new DatabaseConnection();
    }
    
    @Override
    public UserDTO getUserByEmail(String email) throws DatabaseSystemException {
        Validator.isValidEmail(email);
        
        UserDTO userResult = null;
        String query = "SELECT * FROM usuario WHERE email = ?";

        try (Connection connection = this.databaseConnection.getConnection(); 
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userResult = this.mapResultSetToUserDTO(resultSet);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error occurred while searching user by email. SQL error code: " + exception.getErrorCode(), exception);
            throw new DatabaseSystemException("Error técnico al verificar credenciales.", exception);
        }
        return userResult;
    }

    @Override
    public int saveUser(UserDTO user, Connection connection) throws DatabaseSystemException {
        Validator.isValidUser(user);
        Validator.isValidEmail(user.getEmail());

        String query = "INSERT INTO usuario (email, contraseña, rol) VALUES (?, ?, ?) " 
                + "ON DUPLICATE KEY UPDATE contraseña = VALUES(contraseña), rol = VALUES(rol)";
                
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            String securelyHashedPassword = hashPassword(user.getPassword());
            
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, securelyHashedPassword);
            preparedStatement.setString(3, user.getRole());
            return preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database transactional failure during save or update operation for user credentials: " + user.getEmail(), exception);
            throw new DatabaseSystemException("Error técnico al registrar las credenciales de usuario.", exception);
        }
    }

    @Override
    public int updateUser(UserDTO user, Connection connection) throws DatabaseSystemException {
        Validator.isValidUser(user);
        Validator.isValidEmail(user.getEmail());

        String query = "UPDATE usuario SET contraseña = ?, rol = ? WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            String securelyHashedPassword = hashPassword(user.getPassword());
            
            preparedStatement.setString(1, securelyHashedPassword);
            preparedStatement.setString(2, user.getRole());
            preparedStatement.setString(3, user.getEmail());

            return preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database technical execution failure while updating user access credentials for: " + user.getEmail(), exception);
            throw new DatabaseSystemException("Error al actualizar la información de acceso del usuario.", exception);
        }
    }
    
    @Override
    public UserDTO getUserByEmail(String email, Connection connection) throws DatabaseSystemException {
        Validator.isValidEmail(email);
        
        UserDTO userResult = null;
        String query = "SELECT * FROM usuario WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userResult = this.mapResultSetToUserDTO(resultSet);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error occurred within active transaction context while fetching user: " + email, exception);
            throw new DatabaseSystemException("Error al verificar credenciales.", exception);
        }
        return userResult;
    }

    @Override
    public void saveRecoveryToken(String email, String token) throws DatabaseSystemException {
        Validator.isValidEmail(email);
        
        String query = "UPDATE usuario SET tokenRecuperacion = ?, tokenExpiracion = DATE_ADD(NOW(), " 
                + "INTERVAL 15 MINUTE) WHERE email = ?";
        
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, token);
            statement.setString(2, email);
            statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Critical database pipeline failure saving recovery token data payload for user: " + email, exception);
            throw new DatabaseSystemException("Error técnico al procesar el código de seguridad de recuperación.", exception);
        }
    }
    
    @Override
    public boolean validateTokenAndResetPassword(String token, String newPasswordHash) throws DatabaseSystemException {
        String checkQuery = "SELECT email FROM usuario WHERE tokenRecuperacion = ? AND tokenExpiracion > NOW()";
        String updateQuery = "UPDATE usuario SET contraseña = ?, tokenRecuperacion = NULL, " 
                           + "tokenExpiracion = NULL WHERE tokenRecuperacion = ?";
        boolean isResetSuccessful = false;
        
        try (Connection connection = databaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                 PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                
                checkStatement.setString(1, token);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        updateStatement.setString(1, newPasswordHash);
                        updateStatement.setString(2, token);
                        updateStatement.executeUpdate();
                        
                        connection.commit();
                        isResetSuccessful = true;
                    }
                }
            } catch (SQLException transactionException) {
                connection.rollback();
                throw transactionException;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Technical system infrastructure failure during security token validation and password reset flow", exception);
            throw new DatabaseSystemException("No se pudo completar el restablecimiento de contraseña. Inténtelo más tarde.", exception);
        }
        return isResetSuccessful; 
    }
    
    @Override
    public int getUserCount() throws DatabaseSystemException {
        int totalUsers = RESET;
        String query = "SELECT COUNT(*) AS total FROM usuario";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                totalUsers = resultSet.getInt("total");
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Infrastructure pipeline exception occurred while counting server user density metric registry", exception);
            throw new DatabaseSystemException("Error técnico al verificar la integridad de usuarios en la base de datos.", exception);
        }
        return totalUsers;
    }
    
    private UserDTO mapResultSetToUserDTO(ResultSet resultSet) throws SQLException {
        UserDTO user = new UserDTO();
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("contraseña"));
        user.setRole(resultSet.getString("rol"));
        return user;
    }
}