package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import java.time.LocalDateTime;

/**
 *
 * @author cinth
 * @author andre
 */
public class DeadlineDTO {

    private int idDeadline;
    private int nrc;
    private String reportType;
    private LocalDateTime deadlineDate;
    private LocalDateTime lastUpdate;
    private String staffNumber;
    private int reportedNumber;

    public DeadlineDTO() {
    }

    public DeadlineDTO(int idDeadline, int nrc, String reportType, LocalDateTime deadlineDate,
            LocalDateTime lastUpdate, String staffNumber, int reportedNumber) {
        this.idDeadline = idDeadline;
        this.nrc = nrc;
        this.reportType = reportType;
        this.deadlineDate = deadlineDate;
        this.lastUpdate = lastUpdate;
        this.staffNumber = staffNumber;
        this.reportedNumber = reportedNumber;
    }

    public int getIdDeadline() {
        return idDeadline;
    }

    public void setIdDeadline(int idDeadline) {
        this.idDeadline = idDeadline;
    }

    public int getNrc() {
        return nrc;
    }

    public void setNrc(int nrc) {
        this.nrc = nrc;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public int getReportedNumber() {
        return reportedNumber;
    }

    public void setReportedNumber(int reportedNumber) {
        this.reportedNumber = reportedNumber;
    }
}
