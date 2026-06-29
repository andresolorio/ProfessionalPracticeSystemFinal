package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EvaluationCriterionDTO;

/**
 * Defines the contract for accessing evaluation criterion records within the 
 * persistence layer.
 * @author andre
 * @author cinth
 */
public interface IEvaluationCriterionDAO {

    /**
     * Retrieves the complete list of self-evaluation criteria defined in the 
     * system repository.
     * @return a list of data transfer objects containing criterion details
     * @throws DatabaseSystemException if an infrastructure query failure occurs
     */
    List<EvaluationCriterionDTO> getAllCriteria() throws DatabaseSystemException;
}