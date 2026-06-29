package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.sql.Connection;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;

/**
 * Defines the contract and persistence operations for managing professor 
 * records within the data access layer.
 * * @author andre
 * @author cinth
 */
public interface IProfessorDAO {

    /**
     * Persists a new professor record within an active transaction connection.
     * * @param professorInformation the DTO containing details of the professor
     * @param connection the active SQL database connection context
     * @return the number of rows affected by the execution statement
     * @throws DatabaseSystemException if an infrastructure query failure occurs
     */
    int registerProfessor(ProfessorDTO professorInformation, Connection connection) throws DatabaseSystemException;

    /**
     * Updates a professor status record to inactive in the data repository.
     * * @param professorStaffNumber the unique identification personal number
     * @return the number of rows affected by the update query statement
     * @throws DatabaseSystemException if a database access error takes place
     */
    int inactivateProfessor(String professorStaffNumber) throws DatabaseSystemException;

    /**
     * Modifies the coordinator role flag status constraint for a professor.
     * * @param professorStaffNumber the unique identification personal number
     * @param isCoordinator true to grant the role, false to restrict it
     * @return the number of rows affected by the update statement
     * @throws DatabaseSystemException if a transactional SQL execution crashes
     */
    int updateCoordinatorRole(String professorStaffNumber, boolean isCoordinator) throws DatabaseSystemException;

    /**
     * Removes the academic coordinator privileges from a specific professor.
     * * @param professorStaffNumber the unique identification personal number
     * @return the number of rows affected by the update query statement
     * @throws DatabaseSystemException if a database layer pipeline failure occurs
     */
    int removeCoordinatorRole(String professorStaffNumber) throws DatabaseSystemException;

    /**
     * Fetches a professor record filtered by their unique personal number.
     * * @param professorStaffNumber the unique identification personal number
     * @return the matching professor DTO context, or null if it does not exist
     * @throws DatabaseSystemException if a data retrieval pipeline crash occurs
     */
    ProfessorDTO getProfessorByPersonalNumber(String professorStaffNumber) throws DatabaseSystemException;

    /**
     * Retrieves a list containing all existing professor dataset entries.
     * * @return a list of data transfer objects representing all professors
     * @throws DatabaseSystemException if a sequential query processing fails
     */
    List<ProfessorDTO> getAllProfessors() throws DatabaseSystemException;

    /**
     * Fetches a professor record matching the provided institutional email.
     * * @param email the unique target institutional email address string
     * @return the matching professor DTO context metadata, or null if not found
     * @throws DatabaseSystemException if a relational query statement crashes
     */
    ProfessorDTO getProfessorByEmail(String email) throws DatabaseSystemException;

    /**
     * Checks if there is an active coordinator already stored in the system.
     * * @return true if an active coordinator exists, false otherwise
     * @throws DatabaseSystemException if an integrity verification query fails
     */
    boolean isCoordinatorAlreadyRegistered() throws DatabaseSystemException;

    /**
     * Verifies if a professor is linked to active student sections or EE.
     * * @param staffNumber the unique identification personal number
     * @return true if active assignments are found, false otherwise
     * @throws DatabaseSystemException if a verification constraint query fails
     */
    boolean hasActiveAssignments(String staffNumber) throws DatabaseSystemException;

    /**
     * Checks if the coordinator slot limit is reached, excluding one token.
     * * @param staffNumber the unique identification personal number to exclude
     * @return true if the active coordinator ceiling limit is reached
     * @throws DatabaseSystemException if a verification query execution fails
     */
    boolean isCoordinatorLimitReachedExcluding(String staffNumber) throws DatabaseSystemException;

    /**
     * Persists a new professor record using an isolated connection pipe.
     * * @param professorInformation the DTO containing details of the professor
     * @return the number of rows affected by the insertion query
     * @throws DatabaseSystemException if an infrastructure query failure occurs
     */
    int registerProfessor(ProfessorDTO professorInformation) throws DatabaseSystemException;

    /**
     * Aggregates the total count of active coordinators in the system.
     * * @return the total number of active concurrent coordinators found
     * @throws DatabaseSystemException if a data aggregation query fails
     */
    int getActiveCoordinatorsCount() throws DatabaseSystemException;

    /**
     * Checks the coordinator authorization mapping rule for a specific email.
     * * @param email the unique target institutional email address string
     * @return true if the email matches an active coordinator, false otherwise
     * @throws DatabaseSystemException if a validation processing query crashes
     */
    boolean isCoordinator(String email) throws DatabaseSystemException;
}