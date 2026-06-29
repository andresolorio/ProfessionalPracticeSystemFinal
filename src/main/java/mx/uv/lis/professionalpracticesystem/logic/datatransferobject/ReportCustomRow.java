package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;


import java.text.SimpleDateFormat;

/**
 * 
 * @author cinth
 * @author andre
 */
public class ReportCustomRow {
    private final int idReport;
    private final String enrollment;
    private final String reportType;
    private final String formattedDate;
    private final String deliveryStatus;
    private final String revisionStatus;
    private final int hoursCovered;
    private String nrc;

    public ReportCustomRow(ReportDTO dto) {
        this.idReport = dto.getIdReport();
        this.enrollment = dto.getStudentEnrollment();
        this.reportType = dto.getReportType();
        this.deliveryStatus = dto.getDeliveryStatus();
        this.revisionStatus = dto.getReviewStatus();
        this.hoursCovered = dto.getHoursCovered();
        
        if (dto.getDeliveryDate() != null) {
            this.formattedDate = new SimpleDateFormat("dd/MM/yyyy")
                    .format(dto.getDeliveryDate());
        } else {
            this.formattedDate = "No registrada";
        }
        
        this.nrc = "";
    }

    public int getIdReport() {
        return this.idReport;
    }

    public String getEnrollment() {
        return this.enrollment;
    }

    public String getReportType() {
        return this.reportType;
    }

    public String getFormattedDate() {
        return this.formattedDate;
    }

    public String getDeliveryStatus() {
        return this.deliveryStatus;
    }

    public String getRevisionStatus() {
        return this.revisionStatus;
    }

    public int getHoursCovered() {
        return this.hoursCovered;
    }
    
    public String getNrc() {
        return this.nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }
}