package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.LinkedOrganizationDTO;

/**
 * Defines the persistence contract and data access operations for managing 
 * linked organizations within the system repository.
 * @author cinth
 * @author andre
 */
public interface ILinkedOrganizationDAO {
    
    /**
     * Persists a new linked organization record into the repository.
     * * @param organization the data transfer object containing organization details
     * @return the number of rows affected by the insertion operation
     * @throws DataIntegrityException if the organization details violate unique 
     * key constraints
     * @throws DatabaseSystemException if an infrastructure or SQL query failure 
     * occurs during execution
     */
    int saveLinkedOrganization(LinkedOrganizationDTO organization) throws DataIntegrityException, DatabaseSystemException;
    
    /**
     * Updates an existing linked organization record in the repository.
     * * @param organization the data transfer object with modified attributes
     * @return the number of rows affected by the update statement
     * @throws DataIntegrityException if the update violates integrity rules
     * @throws DatabaseSystemException if a query execution error occurs
     */
    int updateLinkedOrganization(LinkedOrganizationDTO organization) throws DataIntegrityException, DatabaseSystemException;
    
    /**
     * Retrieves a list of all linked organizations currently registered.
     * * @return a list of data transfer objects representing all organizations
     * @throws DatabaseSystemException if the retrieval query pipeline fails
     */
    List<LinkedOrganizationDTO> getAllLinkedOrganizations() throws DatabaseSystemException;
    
    /**
     * Fetches a specific linked organization by its unique system identifier.
     * * @param idLinkedOrganization the unique organization primary key
     * @return the data transfer object matching the ID
     * @throws EntityNotFoundException if no record matches the provided identifier
     * @throws DatabaseSystemException if an infrastructure query failure occurs
     */
    LinkedOrganizationDTO getLinkedOrganizationById(int idLinkedOrganization) throws EntityNotFoundException, DatabaseSystemException;
    
    /**
     * Verifies the presence of organizations within the data repository.
     * * @return true if at least one organization exists, false otherwise
     * @throws DatabaseSystemException if the existence check query crashes
     */
    boolean hasOrganizations() throws DatabaseSystemException;
}