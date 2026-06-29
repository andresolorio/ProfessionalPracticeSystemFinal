package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

/**
 *
 * @author cinth
 */
public class ReportRowDTO {
    private String concept;
    private int advancePercentage;
    private String observations;

    public ReportRowDTO() {
    }
    
    public String getConcept() {
        String output = concept;
        return output;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public int getAdvancePercentage() {
        int output = advancePercentage;
        return output;
    }

    public void setAdvancePercentage(int advancePercentage) {
        this.advancePercentage = advancePercentage;
    }

    public String getObservations() {
        String output = observations;
        return output;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
