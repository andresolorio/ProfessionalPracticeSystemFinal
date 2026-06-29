package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

/**
 *
 * @author cinth
 * @author andre
 */
public class FinalReportDeliverableDTO {

    private int idDeliverable;
    private String enrollmentId;
    private String deliverableResult;
    private int advancePercentage;
    private String observations;

    public FinalReportDeliverableDTO() {
    }

    public FinalReportDeliverableDTO(int idDeliverable, String enrollmentId,
            String deliverableResult, int advancePercentage, String observations) {
        this.idDeliverable = idDeliverable;
        this.enrollmentId = enrollmentId;
        this.deliverableResult = deliverableResult;
        this.advancePercentage = advancePercentage;
        this.observations = observations;
    }

    public int getIdDeliverable() {
        return idDeliverable;
    }

    public void setIdDeliverable(int idDeliverable) {
        this.idDeliverable = idDeliverable;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getDeliverableResult() {
        return deliverableResult;
    }

    public void setDeliverableResult(String deliverableResult) {
        this.deliverableResult = deliverableResult;
    }

    public int getAdvancePercentage() {
        return advancePercentage;
    }

    public void setAdvancePercentage(int advancePercentage) {
        this.advancePercentage = advancePercentage;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

}
