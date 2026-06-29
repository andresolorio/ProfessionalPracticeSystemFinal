package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.sql.Connection;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportWeeklyAdvanceDTO;

/**
 * 
 * @author cinth
 * @author andre
 */
public interface IReportWeeklyAdvanceDAO {
    
    int saveWeeklyAdvances(List<ReportWeeklyAdvanceDTO> advances, Connection connection) throws DatabaseSystemException;
    
    List<ReportWeeklyAdvanceDTO> getWeeklyAdvancesByReport(int idReport) throws DatabaseSystemException;
}