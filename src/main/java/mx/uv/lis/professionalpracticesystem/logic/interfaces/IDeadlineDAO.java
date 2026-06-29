package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DeadlineDTO;

/**
 * Defines the contract and data access operations for managing submission 
 * deadlines within the persistence layer.
 * 
 * @author cinth
 * @author andre
 */
public interface IDeadlineDAO {

    /**
     * Persists a new deadline or updates an existing submission date record.
     * 
     * @param deadline the data transfer object containing deadline information
     * @return the number of rows affected by the persistence operation
     * @throws DatabaseSystemException if an infrastructure or SQL query failure 
     * occurs during execution
     */
    int saveOrUpdateDeadline(DeadlineDTO deadline) throws DatabaseSystemException;

    /**
     * Retrieves the deadline configuration specific to a provided report type.
     * 
     * @param reportType the categorical type of the report (e.g., monthly)
     * @return the deadline data transfer object matching the report type, 
     * or null if not configured
     * @throws DatabaseSystemException if a query execution crash occurs
     */
    DeadlineDTO getDeadlineByReportType(String reportType) throws DatabaseSystemException;

    /**
     * Retrieves a list of deadlines associated with a specific student enrollment.
     * 
     * @param enrollmentId the unique academic enrollment identifier
     * @return a list of deadline data transfer objects linked to the student
     * @throws DatabaseSystemException if a data retrieval pipeline error occurs
     */
    List<DeadlineDTO> getDeadlinesByStudentEnrollment(String enrollmentId) throws DatabaseSystemException;

    /**
     * Retrieves a list of deadlines filtered by professor staff number.
     * 
     * @param staffNumber the unique internal staff identification token
     * @return a list of deadline data transfer objects managed by the professor
     * @throws DatabaseSystemException if a repository communication failure 
     * triggers a query crash
     */
    List<DeadlineDTO> getDeadlinesByProfessorStaffNumber(String staffNumber) throws DatabaseSystemException;
}