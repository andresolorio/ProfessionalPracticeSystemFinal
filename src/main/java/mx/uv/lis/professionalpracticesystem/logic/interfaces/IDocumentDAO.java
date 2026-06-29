package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.util.List;
import java.util.Map;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DocumentDTO;

/**
 * Defines the contract and operations for managing student documents within the
 * persistence layer.
 * * @author cinth
 * @author andre
 */
public interface IDocumentDAO {  
    
    /**
     * Persists a new document record associated with a specific student.
     * * @param document the data transfer object containing document metadata
     * @return the number of rows affected by the insertion
     * @throws DataIntegrityException if document constraints are violated
     * @throws DatabaseSystemException if an infrastructure query failure occurs
     */
    int saveDocument(DocumentDTO document) throws DataIntegrityException, DatabaseSystemException;

    /**
     * Retrieves all document records belonging to a specific student enrollment.
     * * @param enrollment the unique academic enrollment identifier
     * @return a list of document data transfer objects found in the repository
     * @throws DatabaseSystemException if a transactional data retrieval crash occurs
     */
    List<DocumentDTO> getDocumentsByEnrollment(String enrollment) throws DatabaseSystemException;

    /**
     * Updates an existing document record stored in the database.
     * * @param document the data transfer object with updated document details
     * @return the number of rows affected by the update operation
     * @throws DataIntegrityException if document constraints are violated
     * @throws DatabaseSystemException if a query execution failure occurs
     */
    int updateDocument(DocumentDTO document) throws DataIntegrityException, DatabaseSystemException;
           
    /**
     * Fetches a specific document of a given type for a student.
     * * @param enrollment the student's unique academic identifier
     * @param documentType the category or type of the required document
     * @return the found document data transfer object, or null if not present
     * @throws DatabaseSystemException if the binary data retrieval stream crashes
     */
    DocumentDTO getSingleDocumentByType(String enrollment, String documentType) throws DatabaseSystemException;
    
    /**
     * Compiles a summary map of review statuses for all student documents.
     * * @param enrollment the student's unique academic identifier
     * @return a map where keys are document types and values are review statuses
     * @throws DatabaseSystemException if the status aggregation query fails
     */
    Map<String, String> getReviewStatusSummaryByEnrollment(String enrollment) throws DatabaseSystemException;
}