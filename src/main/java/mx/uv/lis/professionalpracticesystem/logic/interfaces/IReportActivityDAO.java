package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportActivityDTO;

/**
 * Defines the contract and persistence operations for managing activity 
 * completion status within report checklists.
 * * @author cinth
 * @author andre
 */
public interface IReportActivityDAO {
    
    /**
     * Persists a batch of completed activities associated with a report.
     * * @param completedActivities the list of data transfer objects containing 
     * activity completion details
     * @return the number of rows successfully processed and stored
     * @throws DatabaseSystemException if an infrastructure or SQL query failure 
     * occurs during the batch registration
     */
    int registerCompletedActivities(List<ReportActivityDTO> completedActivities) throws DatabaseSystemException;

    /**
     * Retrieves the cumulative checklist of activities for a specific report 
     * and project context.
     * * @param reportId the unique identifier of the target report
     * @param projectId the unique identifier of the linked project
     * @return a list of data transfer objects representing the activity status
     * @throws DatabaseSystemException if the database retrieval pipeline crashes
     */
    List<ReportActivityDTO> getChecklistByReport(int reportId, int projectId) throws DatabaseSystemException;
}