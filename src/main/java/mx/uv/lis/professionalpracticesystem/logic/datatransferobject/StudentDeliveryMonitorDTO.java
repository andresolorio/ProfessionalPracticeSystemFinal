package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

/**
 *
 * @author cinth
 * @author andre
 */
public class StudentDeliveryMonitorDTO {

    private String enrollment;
    private String fullName;
    private String projectName;
    private int validatedReportsCount;
    private String selfEvaluationStatus;

    public StudentDeliveryMonitorDTO() {
    }

    public StudentDeliveryMonitorDTO(String enrollment, String fullName, String projectName,
            int validatedReportsCount, String selfEvaluationStatus) {
        this.enrollment = enrollment;
        this.fullName = fullName;
        this.projectName = projectName;
        this.validatedReportsCount = validatedReportsCount;
        this.selfEvaluationStatus = selfEvaluationStatus;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getValidatedReportsCount() {
        return validatedReportsCount;
    }

    public void setValidatedReportsCount(int validatedReportsCount) {
        this.validatedReportsCount = validatedReportsCount;
    }

    public String getSelfEvaluationStatus() {
        return selfEvaluationStatus;
    }

    public void setSelfEvaluationStatus(String selfEvaluationStatus) {
        this.selfEvaluationStatus = selfEvaluationStatus;
    }
}
