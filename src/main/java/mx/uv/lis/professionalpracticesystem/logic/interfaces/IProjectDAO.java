package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;

/**
 * Defines the contract and data access operations for managing academic 
 * projects within the persistence layer.
 * @author cinth
 * @author andre
 */
public interface IProjectDAO {
    
    /**
     * Retrieves a list of all academic projects registered in the server.
     * * @return A list containing all available ProjectDTO records.
     * @throws DatabaseSystemException If a server infrastructure failure occurs
     * during the data retrieval process.
     */
    List<ProjectDTO> getAllProjects() throws DatabaseSystemException;

    /**
     * Persists a new project record into the database.
     * * @param project the data transfer object containing project details
     * @return the number of rows affected by the insertion query
     * @throws DataIntegrityException if a unique key constraint is violated
     * @throws DatabaseSystemException if an infrastructure database error occurs
     */
    int saveProject(ProjectDTO project) throws DataIntegrityException, DatabaseSystemException;

    /**
     * Updates the project status record to inactive in the repository.
     * * @param idProject the unique primary key identifier of the project
     * @return the number of rows affected by the update query statement
     * @throws DatabaseSystemException if a database access pipeline error occurs
     */
    int deactivateProject(int idProject) throws DatabaseSystemException;

    /**
     * Checks if a student has already submitted a project selection request.
     * * @param enrollmentId the unique academic enrollment identifier
     * @return true if previous selection request rows exist, false otherwise
     * @throws DatabaseSystemException if a database validation query crashes
     */
    boolean isStudentAlreadyRegisteredInRequest(String enrollmentId) throws DatabaseSystemException;

    /**
     * Assigns a specific project identifier to a target student record.
     * * @param enrollmentId the unique academic enrollment identifier
     * @param projectId the unique primary key identifier of the project
     * @return the number of rows affected by the assignment update
     * @throws DatabaseSystemException if a transactional assignment execution 
     * fails
     */
    int assignProjectToStudent(String enrollmentId, int projectId) throws DatabaseSystemException;

    /**
     * Retrieves the prioritized list of requested projects for a student.
     * * @param enrollmentId the unique academic enrollment identifier
     * @return a list of project data transfer objects sorted by priority
     * @throws DatabaseSystemException if a sequential retrieval query crashes
     */
    List<ProjectDTO> getRequestedProjectsByStudent(String enrollmentId) throws DatabaseSystemException;

    /**
     * Retrieves a list of all currently active and available projects.
     * * @return a list of project data transfer objects matching active filters
     * @throws DatabaseSystemException if an unexpected SQL exception occurs
     */
    List<ProjectDTO> getAllAvailableProjects() throws DatabaseSystemException;

    /**
     * Fetches a single project record filtered by its unique system identifier.
     * * @param idProject the unique primary key identifier of the project
     * @return the matching project data transfer object with relational data
     * @throws EntityNotFoundException if no record matches the provided identifier
     * @throws DatabaseSystemException if a data retrieval pipeline failure occurs
     */
    ProjectDTO getProjectById(int idProject) throws EntityNotFoundException, DatabaseSystemException;

    /**
     * Updates an existing project dataset record in the repository.
     * * @param project the data transfer object with modified project attributes
     * @return the number of rows affected by the modification statement
     * @throws DataIntegrityException if an integrity constraint is violated
     * @throws DatabaseSystemException if a structural database query failure 
     * takes place
     */
    int updateProject(ProjectDTO project) throws DataIntegrityException, DatabaseSystemException;

    /**
     * Aggregates the total count of students currently assigned to a project.
     * * @param idProject the unique primary key identifier of the project
     * @return the total number of assigned concurrent students found
     * @throws DatabaseSystemException if a data aggregation query fails
     */
    int getAssignedStudentsCount(int idProject) throws DatabaseSystemException;

    /**
     * Verifies if a student enrollment is already assigned to any project.
     * * @param enrollmentId the unique academic enrollment identifier
     * @return true if the student is formally linked to a project, false otherwise
     * @throws DatabaseSystemException if a verification check query crashes
     */
    boolean isStudentAlreadyAssigned(String enrollmentId) throws DatabaseSystemException;

    /**
     * Saves a prioritized batch of project selection requests for a student.
     * * @param enrollmentId the unique academic enrollment identifier
     * @param projects the list of project DTO entries to associate with priorities
     * @throws DatabaseSystemException if a transactional batch query execution 
fails
     * @throws DataIntegrityException if batch data violates repository constraints
     */
    void saveProjectRequests(String enrollmentId, List<ProjectDTO> projects) throws DatabaseSystemException, DataIntegrityException;
}