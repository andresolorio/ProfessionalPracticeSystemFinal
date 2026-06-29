package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityDTO;

/**
 * Defines the contract and database operations for managing project activities
 * within the data access layer.
 * @author cinth
 * @author andre
 */
public interface IActivityDAO {
    
    /**
     * Persists a new project activity record into the database.
     * * @param activity the data transfer object containing activity details
     * @return the number of rows affected by the insertion query
     * @throws DatabaseSystemException if an infrastructure or network failure 
     * occurs with MySQL
     */
    int registerActivity(ActivityDTO activity) throws DatabaseSystemException;
    
    /**
     * Retrieves all activities associated with a specific project identifier.
     * * @param idProject the unique identifier of the target project
     * @return a list of data transfer objects representing the activities
     * @throws DatabaseSystemException if a database transactional query crashes
     */
    List<ActivityDTO> getActivitiesByIdProject(int idProject) 
            throws DatabaseSystemException;
    
    /**
     * Fetches a unique project activity matching the specified identifier.
     * * @param idActivity the unique primary key identifier of the activity
     * @return the data transfer object with matching activity context, 
     * or null if not found
     * @throws DatabaseSystemException if an unexpected SQL data retrieval 
     * exception occurs
     */
    ActivityDTO getActivityById(int idActivity) throws DatabaseSystemException;
    
    /**
     * Updates an existing project activity dataset record in the repository.
     * * @param activity the data transfer object with modified activity attributes
     * @return the number of rows affected by the modification statement
     * @throws DatabaseSystemException if a structural database query failure 
     * takes place
     */
    int updateActivity(ActivityDTO activity) throws DatabaseSystemException;
}