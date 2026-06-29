package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

/**
 *
 * @author andre
 * @author cinth
 */
public class EvaluationCriterionDTO {

    private int idCriterion;
    private String statement;
    private int score;

    public EvaluationCriterionDTO() {
    }

    public EvaluationCriterionDTO(int idCriterion, String statement, int score) {
        this.idCriterion = idCriterion;
        this.statement = statement;
        this.score = score;
    }

    public int getIdCriterion() {
        return idCriterion;
    }

    public void setIdCriterion(int idCriterion) {
        this.idCriterion = idCriterion;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
