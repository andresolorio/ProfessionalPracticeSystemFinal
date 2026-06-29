package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EvaluationCriterionDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IEvaluationCriterionDAO;

/**
 * 
 * @author cinth
 * @author andre
 */
public class EvaluationCriterionDAO implements IEvaluationCriterionDAO {
    private static final Logger LOGGER = Logger.getLogger(EvaluationCriterionDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public EvaluationCriterionDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

    @Override
    public List<EvaluationCriterionDTO> getAllCriteria() throws DatabaseSystemException {
        List<EvaluationCriterionDTO> evaluationCriteriaList = new ArrayList<>();
        String query = "SELECT idCriterio, afirmacion FROM CriterioAutoevaluacion";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement preparedStatement = connection
                        .prepareStatement(query); 
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                evaluationCriteriaList.add(mapResultSetToDTO(resultSet));
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database query processing crash recovering self evaluation criteria metrics", exception);
            throw new DatabaseSystemException("Error técnico al recuperar los criterios de evaluación.", exception);
        }
        return evaluationCriteriaList;
    }

    private EvaluationCriterionDTO mapResultSetToDTO(ResultSet resultSet) throws SQLException {
        EvaluationCriterionDTO evaluationCriterion = new EvaluationCriterionDTO();
        evaluationCriterion.setIdCriterion(resultSet.getInt("idCriterio"));
        evaluationCriterion.setStatement(resultSet.getString("afirmacion"));
        return evaluationCriterion;
    }
}