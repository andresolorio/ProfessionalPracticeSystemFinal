package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.SelfEvaluationDTO;

/**
 * Defines the contract and persistence operations for managing student 
 * self-evaluation records within the data access layer.
 * @author andre
 * @author cinth
 */
public interface ISelfEvaluationDAO {

    /**
     * Persists a completed self-evaluation record into the database.
     * * @param selfEvaluationData the data transfer object containing the 
     * evaluation criteria and scores
     * @throws DatabaseSystemException if an infrastructure or SQL query failure 
     * occurs during the save operation
     */
    void saveSelfEvaluation(SelfEvaluationDTO selfEvaluationData) throws DatabaseSystemException;

    /**
     * Retrieves the self-evaluation record associated with a specific student 
     * enrollment identifier.
     * * @param enrollmentId the unique academic enrollment identifier
     * @return the data transfer object with self-evaluation details, or 
     * null if no record exists
     * @throws DatabaseSystemException if the data retrieval pipeline crashes
     */
    SelfEvaluationDTO getSelfEvaluationByEnrollment(String enrollmentId) throws DatabaseSystemException;
}