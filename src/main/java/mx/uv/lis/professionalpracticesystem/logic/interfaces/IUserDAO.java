package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.sql.Connection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;

/**
 * Defines the contract and persistence operations for managing system
 * credentials and authentication users within the data access layer.
 *
 * * @author cinth
 * @author andre
 */
public interface IUserDAO {

    /**
     * Persists a new system user record within an active transaction
     * connection.
     *
     * * @param user the data transfer object containing user account details
     * @param connection the active SQL database connection context
     * @return the number of rows affected by the execution statement
     * @throws DatabaseSystemException if an infrastructure query failure occurs
     */
    int saveUser(UserDTO user, Connection connection) throws DatabaseSystemException;

    /**
     * Saves a secure recovery token linked to a specific user email.
     *
     * * @param email the unique target institutional email address string
     * @param token the generated alphanumeric security recovery token
     * @throws DatabaseSystemException if a database transactional query crashes
     */
    void saveRecoveryToken(String email, String token) throws DatabaseSystemException;

    /**
     * Validates a password recovery token and updates the user password hash.
     *
     * * @param token the security recovery token to validate
     * @param newPasswordHash the new encrypted password hash value to store
     * @return true if the token is valid and the password is reset successfully
     * @throws DatabaseSystemException if an unexpected SQL data modification
     * exception occurs
     */
    boolean validateTokenAndResetPassword(String token, String newPasswordHash) throws DatabaseSystemException;

    /**
     * Fetches a user record by email within an active transaction connection.
     *
     * * @param email the unique target institutional email address string
     * @param connection the active SQL database connection context
     * @return the matching user DTO context, or null if it does not exist
     * @throws DatabaseSystemException if a data retrieval pipeline crash occurs
     */
    UserDTO getUserByEmail(String email, Connection connection) throws DatabaseSystemException;

    /**
     * Fetches a user record matching the provided email using an isolated pipe.
     *
     * * @param email the unique target institutional email address string
     * @return the matching user DTO context metadata, or null if not found
     * @throws DatabaseSystemException if a relational query statement crashes
     */
    UserDTO getUserByEmail(String email) throws DatabaseSystemException;

    /**
     * Modifies the primary user metadata records using an active transaction.
     *
     * * @param user the data transfer object with modified user details
     * @param connection the active SQL database connection context
     * @return the number of rows affected by the modification statement
     * @throws DatabaseSystemException if an infrastructure query failure occurs
     */
    int updateUser(UserDTO user, Connection connection) throws DatabaseSystemException;

    /**
     * Aggregates the total count of registered users in the system repository.
     *
     * * @return the total number of general system users found
     * @throws DatabaseSystemException if a data aggregation query fails
     */
    int getUserCount() throws DatabaseSystemException;
}
