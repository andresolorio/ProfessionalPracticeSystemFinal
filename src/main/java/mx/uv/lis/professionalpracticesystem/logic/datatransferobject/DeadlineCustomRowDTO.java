package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_STRING;

/**
 * 
 * @author andre
 * @author cinth
 */
public class DeadlineCustomRowDTO {
    private final int nrc;
    private final String reportType;
    private final int reportedNumber;
    private final LocalDateTime deadlineDate;

    public DeadlineCustomRowDTO(DeadlineDTO dto) {
        this.nrc = dto.getNrc();
        this.reportType = dto.getReportType();
        this.reportedNumber = dto.getReportedNumber();
        this.deadlineDate = dto.getDeadlineDate();
    }

    public int getNrc() {
        return this.nrc;
    }

    public String getReportType() {
        String decoratedType = this.reportType;
        if (decoratedType.contains("Mensual")) {
            decoratedType = "Reporte Mensual (Entrega " 
                    + this.reportedNumber + ")";
        } else if ("Parcial".equalsIgnoreCase(decoratedType)) {
            decoratedType = "Reporte Parcial";
        } else if ("Final".equalsIgnoreCase(decoratedType)) {
            decoratedType = "Informe Final";
        }
        return decoratedType;
    }

    public String getFormattedDate() {
        String formattedOutput = EMPTY_STRING;
        if (this.deadlineDate != null) {
            formattedOutput = this.deadlineDate.format(
                    DateTimeFormatter.ofPattern(
                    SystemConstants.DATE_PATTERN_DISPLAY));
        }
        return formattedOutput;
    }

    public String getStatusText() {
        String statusOutput = SystemConstants.STATUS_TEXT_NOT_ESTABLISHED;

        if (this.deadlineDate != null) {
            if (LocalDateTime.now().isAfter(this.deadlineDate)) {
                statusOutput = SystemConstants.STATUS_TEXT_EXPIRED;
            } else {
                statusOutput = SystemConstants.STATUS_TEXT_ACTIVE;
            }
        }
        return statusOutput;
    }
}