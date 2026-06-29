package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

/**
 *
 * @author cinth
 * @author andre
 */
public class StudentMonitorDTO {

    private String enrollment;
    private String fullName;
    private String projectName;
    private int hoursCovered;
    private int hoursRemaining;
    private String nrc;

    public StudentMonitorDTO() {
    }

    public StudentMonitorDTO(String enrollment, String fullName, String projectName,
            int hoursCovered, int hoursRemaining) {
        this.enrollment = enrollment;
        this.fullName = fullName;
        this.projectName = projectName;
        this.hoursCovered = hoursCovered;
        this.hoursRemaining = hoursRemaining;
        this.nrc = "";
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

    public int getHoursCovered() {
        return hoursCovered;
    }

    public void setHoursCovered(int hoursCovered) {
        this.hoursCovered = hoursCovered;
    }

    public int getHoursRemaining() {
        return hoursRemaining;
    }

    public void setHoursRemaining(int hoursRemaining) {
        this.hoursRemaining = hoursRemaining;
    }
    
    public String getNrc() {
        return this.nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }
}
