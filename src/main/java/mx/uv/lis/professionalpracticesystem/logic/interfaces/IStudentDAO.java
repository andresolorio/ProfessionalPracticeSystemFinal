package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.sql.Connection;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDeliveryMonitorDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentMonitorDTO;

/**
 * Defines the contract and data access operations for managing student records
 * within the persistence layer.
 * @author andre
 * @author cinth
 */
public interface IStudentDAO {
    
    /**
     * Persists a new student record within an active transaction connection.
     * * @param studentInformation the DTO containing details of the student
     * @param studentInformation
     * @param connection the active SQL database connection context
     * @return the number of rows affected by the execution statement
     * @throws DatabaseSystemException if an infrastructure query failure occurs
     */
    int registerStudent(StudentDTO studentInformation, Connection connection) throws DatabaseSystemException;

    /**
     * Fetches a student record filtered by their unique enrollment identifier.
     * * @param enrollmentId the unique academic enrollment identifier string
     * @return the matching student DTO context, or null if it does not exist
     * @throws DatabaseSystemException if a data retrieval pipeline crash occurs
     */
    StudentDTO getStudentByEnrollment(String enrollmentId) throws DatabaseSystemException;
        
    /**
     * Retrieves a list containing all active student dataset entries.
     * * @return a list of data transfer objects representing active students
     * @throws DatabaseSystemException if a sequential query processing fails
     */
    List<StudentDTO> getAllActiveStudents() throws DatabaseSystemException;
    
    /**
     * Retrieves a list containing all existing student record entries.
     * * @return a list of data transfer objects representing all students
     * @throws DatabaseSystemException if a sequential query processing fails
     */
    List<StudentDTO> getAllStudents() throws DatabaseSystemException;
   
    /**
     * Updates the status attribute of a specific student record.
     * * @param enrollmentId the unique academic enrollment identifier string
     * @param newStatus the new status value to assign to the student
     * @return the number of rows affected by the update query statement
     * @throws DatabaseSystemException if a database access error takes place
     */
    int updateStudentStatus(String enrollmentId, String newStatus) throws DatabaseSystemException;
   
    /**
     * Fetches a student record matching the provided institutional email.
     * * @param email the unique target institutional email address string
     * @return the matching student DTO context metadata, or null if not found
     * @throws DatabaseSystemException if a relational query statement crashes
     */
    StudentDTO getStudentByEmail(String email) throws DatabaseSystemException;
   
    /**
     * Retrieves all student records assigned to a specific professor email.
     * * @param professorEmail the institutional email address of the professor
     * @return a list of student DTOs linked to the specified professor
     * @throws DatabaseSystemException if a repository data retrieval fails
     */
    List<StudentDTO> getStudentsByProfessorEmail(String professorEmail) throws DatabaseSystemException;
    
    /**
     * Increments the total validated practicum hours accumulated by a student.
     * * @param enrollmentId the unique academic enrollment identifier string
     * @param hoursToIncrement the number of hours to add to the record
     * @return the number of rows affected by the modification statement
     * @throws DatabaseSystemException if a structural query failure occurs
     */
    int incrementStudentHours(String enrollmentId, int hoursToIncrement) throws DatabaseSystemException;
    
    /**
     * Retrieves the tracking progress roster of students linked to a professor.
     * * @param staffNumber the unique internal staff identification personal number
     * @return a list of monitor objects containing progress evaluation metadata
     * @throws DatabaseSystemException if a data aggregation query fails
     */
    List<StudentMonitorDTO> getStudentsProgressByProfessor(String staffNumber) throws DatabaseSystemException;
    
    /**
     * Assigns a specific academic project and assigns its reason to a student.
     * * @param enrollmentId the unique academic enrollment identifier string
     * @param projectId the unique primary key identifier of the project
     * @param assignmentReason the contextual reason for allocating the project
     * @return the number of rows affected by the assignment update
     * @throws DatabaseSystemException if a transactional SQL execution crashes
     */
    int assignProjectToStudent(String enrollmentId, int projectId, String assignmentReason) throws DatabaseSystemException;
    
    /**
     * Enrolls a student into a specific Educative Experience section via NRC.
     * * @param enrollmentId the unique academic enrollment identifier string
     * @param nrc the unique numeric record code of the target section
     * @return the number of rows affected by the update statement
     * @throws DatabaseSystemException if a verification constraint query fails
     */
    int assignExperienciaEducativaToStudent(String enrollmentId, int nrc) throws DatabaseSystemException;
    
    /**
     * Modifies the primary student metadata records using an active transaction.
     * * @param student the data transfer object with modified student details
     * @param connection the active SQL database connection context
     * @return the number of rows affected by the modification statement
     * @throws DatabaseSystemException if an infrastructure query failure occurs
     */
    int updateStudentData(StudentDTO student, Connection connection) throws DatabaseSystemException;
    
    /**
     * Retrieves the delivery monitoring status of students linked to a professor.
     * * @param staffNumber the unique internal staff identification personal number
     * @return a list of delivery monitor objects containing checklist metrics
     * @throws DatabaseSystemException if a validation processing query crashes
     */
    List<StudentDeliveryMonitorDTO> getStudentsDeliveriesByProfessor(String staffNumber) throws DatabaseSystemException;    
    
    /**
     * Fetches a student record including their assigned professor context data.
     * * @param enrollmentId the unique academic enrollment identifier string
     * @return the matching student DTO containing professor metadata attributes
     * @throws DatabaseSystemException if a relational query statement crashes
     */
    StudentDTO getStudentWithProfessorByEnrollment(String enrollmentId) throws DatabaseSystemException;
}