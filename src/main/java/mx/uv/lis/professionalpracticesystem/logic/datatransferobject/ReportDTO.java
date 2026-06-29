package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import java.sql.Date;

/**
 *
 * @author andre
 */
public class ReportDTO {
    private int idReport;
    private String studentEnrollment;
    private String reportType;
    private int reportedHours;
    private int hoursCovered;
    private Date deliveryDate;
    private String deliveryStatus = "A tiempo";
    private String reviewStatus = "Pendiente";
    private String observations;
    private byte[] fileContent;

    public ReportDTO() {
    }

    public ReportDTO(int idReport, String studentEnrollment, String reportType, int reportedHours, 
            int hoursCovered, Date deliveryDate, String deliveryStatus, String reviewStatus, 
            String observations, byte[] fileContent) {
        this.idReport = idReport;
        this.studentEnrollment = studentEnrollment;
        this.reportType = reportType;
        this.reportedHours = reportedHours;
        this.hoursCovered = hoursCovered;
        this.deliveryDate = deliveryDate;
        this.deliveryStatus = deliveryStatus;
        this.reviewStatus = reviewStatus;
        this.observations = observations;
        this.fileContent = fileContent;
    }

    public int getIdReport() {
        return this.idReport;
    }

    public void setIdReport(int idReport) {
        this.idReport = idReport;
    }

    public String getStudentEnrollment() {
        return this.studentEnrollment;
    }

    public void setStudentEnrollment(String studentEnrollment) {
        this.studentEnrollment = studentEnrollment;
    }

    public String getReportType() {
        return this.reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public int getReportedHours() {
        return this.reportedHours;
    }

    public void setReportedHours(int reportedHours) {
        this.reportedHours = reportedHours;
    }

    public int getHoursCovered() {
        return this.hoursCovered;
    }

    public void setHoursCovered(int hoursCovered) {
        this.hoursCovered = hoursCovered;
    }

    public Date getDeliveryDate() {
        return this.deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryStatus() {
        return this.deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getReviewStatus() {
        return this.reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getObservations() {
        return this.observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public byte[] getFileContent() {
        return this.fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
}