package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

/**
 *
 * @author andre
 */
public class ReportActivityDTO {
    private int reportId;
    private int activityId;
    private boolean completed;
    private String activityName;
    private boolean alreadyApprovedPast;

    public ReportActivityDTO() {
    }

    public ReportActivityDTO(int reportId, int activityId, boolean completed, 
            String activityName, boolean alreadyApprovedPast) {
        this.reportId = reportId;
        this.activityId = activityId;
        this.completed = completed;
        this.activityName = activityName;
        this.alreadyApprovedPast = alreadyApprovedPast;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public boolean isAlreadyApprovedPast() {
        return alreadyApprovedPast;
    }

    public void setAlreadyApprovedPast(boolean alreadyApprovedPast) {
        this.alreadyApprovedPast = alreadyApprovedPast;
    }
}