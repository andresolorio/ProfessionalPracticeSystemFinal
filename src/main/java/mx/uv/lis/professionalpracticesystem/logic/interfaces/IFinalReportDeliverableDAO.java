package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.sql.Connection;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.FinalReportDeliverableDTO;

/**
 * Defines the contract for managing final report deliverable records within 
 * the persistence layer.
 * 
 * @author cinth
 * @author andre
 */
public interface IFinalReportDeliverableDAO {

    /**
     * Persists a batch of final report deliverables into the database.
     * 
     * @param deliverables the list of data transfer objects to persist
     * @param connection the active database connection context for transactions
     * @return the number of rows successfully processed by the operation
     * @throws DatabaseSystemException if an infrastructure or SQL query failure 
     * occurs during the batch execution
     */
    int saveDeliverables(List<FinalReportDeliverableDTO> deliverables, Connection connection) throws DatabaseSystemException;

    /**
     * Retrieves all final report deliverables associated with a specific 
     * student enrollment identifier.
     * 
     * @param enrollmentId the unique academic enrollment identifier
     * @return a list of data transfer objects linked to the student
     * @throws DatabaseSystemException if a query execution crash occurs
     */
    List<FinalReportDeliverableDTO> getDeliverablesByStudentEnrollment(String enrollmentId) throws DatabaseSystemException;
}