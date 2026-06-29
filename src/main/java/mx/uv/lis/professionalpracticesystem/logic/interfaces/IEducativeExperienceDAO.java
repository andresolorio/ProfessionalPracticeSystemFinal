package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EducativeExperienceDTO;

/**
 * Defines the contract and persistence operations for managing educative 
 * experience records within the data access layer.
 * * @author cinth
 * @author andre
 */
public interface IEducativeExperienceDAO {

    /**
     * Persists a new educative experience record in the repository.
     * * @param experience the data transfer object containing experience details
     * @return the number of rows affected by the insertion
     * @throws DataIntegrityException if the NRC violates uniqueness constraints
     * @throws DatabaseSystemException if an infrastructure or SQL query failure 
     * occurs during execution
     */
    int registerEducativeExperience(EducativeExperienceDTO experience) throws DataIntegrityException, DatabaseSystemException;

    /**
     * Retrieves all educative experience records including associated 
     * professor information.
     * * @return a list of data transfer objects with professor details
     * @throws DatabaseSystemException if a transactional data retrieval 
     * crash occurs
     */
    List<EducativeExperienceDTO> getAllEducativeExperiencesWithProfessors() throws DatabaseSystemException;

    /**
     * Fetches a specific educative experience record identified by its NRC.
     * * @param nrc the unique numeric record code of the educative experience
     * @return the data transfer object with professor details
     * @throws EntityNotFoundException if no record matches the provided NRC
     * @throws DatabaseSystemException if the database connection pipeline fails
     */
    EducativeExperienceDTO getEducativeExperienceWithProfessorByNrc(String nrc) throws EntityNotFoundException, DatabaseSystemException;

    /**
     * Updates an existing educative experience record in the repository.
     * * @param experience the data transfer object with modified attributes
     * @return the number of rows affected by the update statement
     * @throws DatabaseSystemException if a query execution error occurs
     */
    int updateEducativeExperience(EducativeExperienceDTO experience) throws DatabaseSystemException;

    /**
     * Retrieves a list of educative experiences assigned to a specific professor.
     * * @param email the institutional email address of the target professor
     * @return a list of educative experiences assigned to the professor
     * @throws DatabaseSystemException if the retrieval query fails
     */
    List<EducativeExperienceDTO> getEducativeExperiencesWithProfessorsByProfessorEmail(String email) throws DatabaseSystemException;
}