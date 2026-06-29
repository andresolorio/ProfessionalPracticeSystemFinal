package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EvaluationCriterionDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_STRING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_CRITERION_STATEMENT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @autor andre
 * @author cinth
 */
public class EvaluationCriterionDAOTest {
    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private EvaluationCriterionDAO evaluationCriterionDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        this.evaluationCriterionDAO = new EvaluationCriterionDAO();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM CriterioAutoevaluacion WHERE afirmacion = '" + TEST_CRITERION_STATEMENT + "'");
            statement.executeUpdate("INSERT INTO CriterioAutoevaluacion (afirmacion) VALUES ('" + TEST_CRITERION_STATEMENT + "')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM CriterioAutoevaluacion WHERE afirmacion = '" + TEST_CRITERION_STATEMENT + "'");
        }
    }

    private String getTargetCriterionStatement(List<EvaluationCriterionDTO> criteria) {
        String foundStatement = EMPTY_STRING;
        for (EvaluationCriterionDTO criterion : criteria) {
            if (TEST_CRITERION_STATEMENT.equals(criterion.getStatement())) {
                foundStatement = criterion.getStatement();
            }
        }
        return foundStatement;
    }

    @Test
    public void testGetAllCriteriaExistingDataSuccessful() throws DatabaseSystemException {
        List<EvaluationCriterionDTO> criteria = evaluationCriterionDAO.getAllCriteria();
        String actualStatement = getTargetCriterionStatement(criteria);
        assertEquals(TEST_CRITERION_STATEMENT, actualStatement);
    }
}
