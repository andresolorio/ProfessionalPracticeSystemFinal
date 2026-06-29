package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ResponsibleProjectDTO;

/**
 * Defines the persistence contract and data access operations for managing 
 * project responsible records within the system repository.
 * @author cinth
 * @author andre
 */
public interface IResponsibleProjectDAO {
    
    /**
     * Retrieves the complete catalog of technical responsible personnel 
     * registered in the server.
     * * @return A list of all ResponsibleProjectDTO records found.
     * @throws DatabaseSystemException If a server communication failure occurs
     * during the data retrieval process.
     */
    List<ResponsibleProjectDTO> getAllResponsibles() throws DatabaseSystemException;
    
    /**
     * Persists a new project responsible record into the repository.
     * * @param responsible the data transfer object containing responsible details
     * @return the number of rows affected by the insertion operation
     * @throws DataIntegrityException if the responsible details violate unique 
     * key constraints
     * @throws DatabaseSystemException if an infrastructure or SQL query failure 
     * occurs during execution
     */
    int registerResponsible(ResponsibleProjectDTO responsible) throws DataIntegrityException, DatabaseSystemException;
    
    /**
     * Retrieves all project responsibles associated with a specific organization.
     * * @param idLinkedOrganization the unique organization primary key identifier
     * @return a list of data transfer objects representing the responsibles
     * @throws DatabaseSystemException if the retrieval query pipeline fails
     */
    List<ResponsibleProjectDTO> getResponsiblesByOrganization(int idLinkedOrganization) throws DatabaseSystemException;
    
    /**
     * Fetches a specific project responsible record by its unique identifier.
     * * @param idResponsible the unique primary key identifier of the responsible
     * @return the data transfer object matching the ID
     * @throws EntityNotFoundException if no record matches the provided identifier
     * @throws DatabaseSystemException if an infrastructure query failure occurs
     */
    ResponsibleProjectDTO getResponsibleById(int idResponsible) throws EntityNotFoundException, DatabaseSystemException;
    
    /**
     * Updates an existing project responsible record in the repository.
     * * @param responsible the data transfer object with modified attributes
     * @return the number of rows affected by the update statement
     * @throws DataIntegrityException if the update violates integrity rules
     * @throws DatabaseSystemException if a query execution error occurs
     */
    int updateResponsible(ResponsibleProjectDTO responsible) throws DataIntegrityException, DatabaseSystemException;
}