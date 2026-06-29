package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DATE_PATTERN;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TIME_PATTERN;

/**
 * 
 * @author andre
 * @author cinth
 */
public class DeadlineRowAdapterDTO {
    private final String reportType;
    private final String pureReportType;
    private final int reportNumber;
    private final String formattedDate;
    private final String formattedTime;
    private final String deliveryStatus;
    private final String revisionStatus;

    public DeadlineRowAdapterDTO(DeadlineDTO deadline, ReportDTO report) {
        this.pureReportType = deadline.getReportType();
        this.reportNumber = deadline.getReportedNumber();
        boolean isSubmitted = (report != null);

        if ("Mensual".equalsIgnoreCase(this.pureReportType)) {
            this.reportType = "Reporte Mensual (Entrega " 
                    + this.reportNumber + ")";
        } else {
            this.reportType = this.pureReportType;
        }

        if (isSubmitted) {
            this.revisionStatus = report.getReviewStatus();
        } else {
            this.revisionStatus = "Sin entregar";
        }

        LocalDateTime dateTarget = deadline.getDeadlineDate();
        if (dateTarget != null) {
            this.formattedDate = dateTarget.format(
                    DateTimeFormatter.ofPattern(DATE_PATTERN));
            this.formattedTime = dateTarget.format(
                    DateTimeFormatter.ofPattern(TIME_PATTERN));
            if (isSubmitted) {
                this.deliveryStatus = "Entregado";
            } else if (LocalDateTime.now().isAfter(dateTarget)) {
                this.deliveryStatus = "Plazo Vencido";
            } else {
                this.deliveryStatus = "Vigente";
            }
        } else {
            this.formattedDate = "--/--/----";
            this.formattedTime = "--:--";
            this.deliveryStatus = isSubmitted ? "Entregado" : "Sin fecha";
        }
    }

    public String getReportType() {
        return this.reportType;
    }

    public String getPureReportType() {
        return this.pureReportType;
    }

    public int getReportNumber() {
        return this.reportNumber;
    }

    public String getFormattedDate() {
        return this.formattedDate;
    }

    public String getFormattedTime() {
        return this.formattedTime;
    }

    public String getDeliveryStatus() {
        return this.deliveryStatus;
    }

    public String getRevisionStatus() {
        return this.revisionStatus;
    }
}