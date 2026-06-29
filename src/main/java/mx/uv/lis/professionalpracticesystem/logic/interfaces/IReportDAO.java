package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;

/**
 * Defines the persistence contract and data access operations for managing 
 * academic reports within the system repository.
 * * @author andre
 * @author cinth
 */
public interface IReportDAO {

    /**
     * Persists a new student report record into the repository.
     * * @param report the data transfer object containing report metadata
     * @return the number of rows affected by the insertion operation
     * @throws DataIntegrityException if document constraints are violated
     * @throws DatabaseSystemException if an infrastructure query failure occurs
     */
    int saveReport(ReportDTO report) throws DataIntegrityException, DatabaseSystemException;
    
    /**
     * Retrieves all reports associated with a specific student enrollment.
     * * @param enrollmentId the unique academic enrollment identifier
     * @return a list of report data transfer objects linked to the student
     * @throws DatabaseSystemException if a data retrieval pipeline error occurs
     */
    List<ReportDTO> getReportsByEnrollment(String enrollmentId) throws DatabaseSystemException;
    
    /**
     * Fetches a specific report record by its unique system identifier.
     * * @param idReport the unique primary key identifier of the report
     * @return the matching report data transfer object
     * @throws EntityNotFoundException if no report record matches the ID
     * @throws DatabaseSystemException if the database connectivity crashes
     */
    ReportDTO getReportById(int idReport) throws EntityNotFoundException, DatabaseSystemException;
    
    /**
     * Retrieves a list of reports filtered by the professor's staff number.
     * * @param staffNumber the unique internal staff identification token
     * @return a list of report data transfer objects managed by the professor
     * @throws DatabaseSystemException if the relational query execution fails
     */
    List<ReportDTO> getReportsByProfessorStaffNumber(String staffNumber) throws DatabaseSystemException;
    
    /**
     * Updates the evaluation status and feedback for a specific report record.
     * * @param report the data transfer object with updated evaluation details
     * @throws DatabaseSystemException if a transactional update query fails
     */
    void updateReportEvaluation(ReportDTO report) throws DatabaseSystemException;
    
    /**
     * Verifies if a student has already submitted a report of a specific type.
     * * @param enrollmentId the unique academic enrollment identifier
     * @param reportType the categorical type of the report
     * @param reportNumber the sequential milestone number of the report
     * @return true if the report exists, false otherwise
     * @throws DatabaseSystemException if an infrastructure verification fails
     */
    boolean isReportSubmitted(String enrollmentId, String reportType, int reportNumber) throws DatabaseSystemException;
    
    /**
     * Overwrites an existing report record with new document content.
     * * @param report the data transfer object containing new binary content
     * @return the number of rows affected by the update operation
     * @throws DatabaseSystemException if an infrastructure storage failure occurs
     */
    int overwriteReport(ReportDTO report) throws DatabaseSystemException;
}