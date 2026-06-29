package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ReportDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_CONTENT_SIZE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_FILE_SIZE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_FILE_SIZE_MB;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MIN_VALID_ID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;

/**
 *
 * @author andre
 */
public class ReportController {
    private final ReportDAO reportDAO = new ReportDAO();

    public int registerReport(ReportDTO report) 
            throws DataIntegrityException, DatabaseSystemException {
        this.validateReportData(report);
        
        byte[] fileContent = report.getFileContent();
        if (fileContent == null || fileContent.length == EMPTY_CONTENT_SIZE) {
            throw new ValidationException("Error: El contenido binario del archivo PDF es obligatorio.");
        }

        if (fileContent.length > MAX_FILE_SIZE) {
            throw new ValidationException("Error: El archivo excede el tamaño permitido (" + MAX_FILE_SIZE_MB + "MB).");
        }

        return this.reportDAO.saveReport(report);
    }

    public List<ReportDTO> getReportsByStudent(String enrollment) 
            throws DatabaseSystemException {
        if (!Validator.isValidEnrollment(enrollment)) { 
            throw new ValidationException("Error: La matrícula ingresada no cuenta con un formato válido.");
        }
        return this.reportDAO.getReportsByEnrollment(enrollment);
    }

    public ReportDTO getReportById(int idReport) 
            throws EntityNotFoundException, DatabaseSystemException {
        if (idReport <= MIN_VALID_ID) {
            throw new ValidationException("Error: El ID del reporte debe ser un valor positivo.");
        }
        return this.reportDAO.getReportById(idReport);
    }
    
    public List<ReportDTO> getReportsByProfessor(String staffNumber) throws DatabaseSystemException {
        if (staffNumber == null || staffNumber.trim().isEmpty()) {
            throw new ValidationException("Error: El número de personal del profesor es obligatorio.");
        }
        return this.reportDAO.getReportsByProfessorStaffNumber(staffNumber);
    }
    
    public void evaluateStudentReport(ReportDTO report) throws DatabaseSystemException {
        if (report == null || report.getIdReport() <= MIN_VALID_ID) {
            throw new ValidationException("Error: El reporte por evaluar es inválido.");
        }
        if (!"Aprobado".equalsIgnoreCase(report.getReviewStatus()) && !"Rechazado".equalsIgnoreCase(report.getReviewStatus())) {
            throw new ValidationException("Error: El estado del dictamen debe ser 'Aprobado' o 'Rechazado'.");
        }
        if (report.getHoursCovered() < RESET) {
            throw new ValidationException("Error: Las horas cubiertas abonadas no pueden ser valores negativos.");
        }
        this.reportDAO.updateReportEvaluation(report);
    }

    private void validateReportData(ReportDTO report) {
        if (report == null) {
            throw new ValidationException("Error: La entidad del reporte no puede ser nula.");
        }

        if (!Validator.isValidEnrollment(report.getStudentEnrollment())) {
            throw new ValidationException("Error: La matrícula del alumno asociada al reporte es inválida.");
        }

        if (!Validator.isNotEmpty(report.getReportType())) {
            throw new ValidationException("Error: El tipo de reporte (Mensual, Parcial, Final, Autoevaluación) es obligatorio.");
        }

        if (report.getReportedHours() <= RESET) {
            throw new ValidationException("Error: El número correlativo de informe debe ser mayor a cero.");
        }

        if (report.getDeliveryDate() == null) {
            throw new ValidationException("Error: La fecha de emisión del reporte es obligatoria.");
        }

        if (!Validator.isNotFutureDate(report.getDeliveryDate())) {
            throw new ValidationException("Error: La fecha del reporte no puede ser una fecha futura.");
        }
    }
}