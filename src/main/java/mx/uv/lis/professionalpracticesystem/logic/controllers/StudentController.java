package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.DeadlineDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.DocumentDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DeadlineDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DocumentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IDeadlineDAO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IDocumentDAO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IStudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;

/**
 * @author andre
 * @author cinth
 */
public class StudentController {
    
    private static final Logger LOGGER = Logger.getLogger(
            StudentController.class.getName());
    private final IStudentDAO studentDAO;
    private final IDeadlineDAO deadlineDAO;
    private final IDocumentDAO documentDAO;

    public StudentController() {
        this.studentDAO = new StudentDAO();
        this.deadlineDAO = new DeadlineDAO();
        this.documentDAO = new DocumentDAO();
    }
    
    public DocumentDTO downloadStudentDocument(String enrollmentId, String documentType) throws DatabaseSystemException {
        DocumentDTO requestedDocument = null; 
        try {
            requestedDocument = this.documentDAO.getSingleDocumentByType(enrollmentId, documentType);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Business logic gateway failure while downloading document raw bytes", exception);
            throw exception;
        }
        return requestedDocument; 
    }

    public List<StudentDTO> getAssignedStudents(String professorEmail) throws DatabaseSystemException {
        List<StudentDTO> assignedStudents = null;
        try {
            assignedStudents = this.studentDAO.getStudentsByProfessorEmail(professorEmail);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Business logic exception catch: " 
                    + "Facilitating recovery routine for professor data " 
                    + "query flow", exception);
            throw exception;
        }
        return assignedStudents;
    }
    
    public Map<String, String> getStudentDocumentStatusSummary(String enrollmentId) throws DatabaseSystemException {
        Map<String, String> statusMap = null; 
        try {
            statusMap = this.documentDAO.getReviewStatusSummaryByEnrollment(enrollmentId);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Business logic framework layer failure while pulling document status map", exception);
            throw exception;
        }
        return statusMap; 
    }

    public void updateStudentDocumentStatus(String enrollmentId, String documentType, String status) throws DatabaseSystemException {
        if (enrollmentId == null || enrollmentId.trim().isEmpty() || documentType == null || status == null) {
            throw new DatabaseSystemException("Los datos de identificación son inválidos.");
        }

        try {
            String statusText = "Pendiente";
            if ("Aprobado".equalsIgnoreCase(status) || "Validado".equalsIgnoreCase(status)) {
                statusText = "Aprobado";
            } else if ("Rechazado".equalsIgnoreCase(status)) {
                statusText = "Rechazado";
            }

            DocumentDTO document = this.documentDAO.getSingleDocumentByType(enrollmentId, documentType);
            if (document != null) {
                document.setReviewStatus(statusText);
                this.documentDAO.updateDocument(document);
                LOGGER.log(Level.INFO, "Successfully dispatched persistence " 
                        + "update command for document: {0} with status: {1}", 
                        new Object[]{documentType, statusText});
            } else {
                LOGGER.log(Level.WARNING, "No row target found in Documentos table for student: {0} and type: {1}", 
                        new Object[]{enrollmentId, documentType});
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Failure inside logic layer context parameters matching database layout bounds", exception);
            throw exception;
        }
    }

    public void saveOrUpdateDeadline(DeadlineDTO deadline) 
            throws DatabaseSystemException {
        if (deadline == null || deadline.getReportType() == null || deadline.getDeadlineDate() == null) {
            throw new DatabaseSystemException("Los datos del plazo de entrega son insuficientes o inválidos.");
        }

        try {
            this.deadlineDAO.saveOrUpdateDeadline(deadline);
            LOGGER.log(Level.INFO, "Successfully orchestrated deadline constraint update for type: {0}", deadline.getReportType());
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Business logic gateway failure while dispatching deadline state change", exception);
            throw exception;
        }
    }

    public List<DeadlineDTO> getAllExistingDeadlines(String enrollmentId) throws DatabaseSystemException {
        List<DeadlineDTO> deadLinesList = null;
        try {
            deadLinesList = this.deadlineDAO.getDeadlinesByStudentEnrollment(enrollmentId);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Business logic infrastructure failure " 
                    + "compiling total active deadlines collection", exception);
            throw exception;
        }
        return deadLinesList;
    }
 
    public void validateReportSubmissionTime(ReportDTO studentReport) throws DatabaseSystemException {
        if (studentReport == null || studentReport.getReportType() == null) {
            throw new DatabaseSystemException("El reporte por evaluar no cuenta con un tipo definido.");
        }

        try {
            String enrollmentId = studentReport.getStudentEnrollment();
            List<DeadlineDTO> totalDeadlines = this.deadlineDAO.getDeadlinesByStudentEnrollment(enrollmentId);
            
            DeadlineDTO targetDeadline = null;
            for (DeadlineDTO currentDeadline : totalDeadlines) {
                boolean matchesType = currentDeadline.getReportType().equalsIgnoreCase(studentReport.getReportType());
                boolean matchesNumber = currentDeadline.getReportedNumber() == studentReport.getReportedHours();

                if (matchesType && matchesNumber) {
                    targetDeadline = currentDeadline;
                    break;
                }
            }
            
            LocalDateTime currentServerTime = java.time.LocalDateTime.now();
            
            if (targetDeadline != null && currentServerTime.isAfter(targetDeadline.getDeadlineDate())) {
                studentReport.setDeliveryStatus(SystemConstants.STATUS_LATE_SUBMISSION);
                LOGGER.log(Level.INFO, "Report submission categorized as LATE for student: {0}", studentReport.getStudentEnrollment());
            } else {
                studentReport.setDeliveryStatus(SystemConstants.STATUS_ON_TIME_SUBMISSION);
                LOGGER.log(Level.INFO, "Report submission categorized ON TIME for student: {0}", studentReport.getStudentEnrollment());
            }
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Critical crash during report time validation pipeline logic", exception);
            throw exception;
        }
    }
    
    public String getProfessorStaffNumber(String email) throws DatabaseSystemException {
        ProfessorDAO professorDAO = new ProfessorDAO();
        ProfessorDTO professor = professorDAO.getProfessorByEmail(email);
        
        String staffNumber = null;
        if (professor != null) {
            staffNumber = professor.getProfessorStaffNumber();
        }
        return staffNumber;
    }
    
    public List<DeadlineDTO> getDeadlinesByProfessorEmail(String professorEmail) throws DatabaseSystemException {
        List<DeadlineDTO> deadlines = null;
        try {
            String staffNumber = this.getProfessorStaffNumber(professorEmail);
            if (staffNumber != null) {
                List<DeadlineDTO> linesList = this.deadlineDAO.getDeadlinesByProfessorStaffNumber(staffNumber);
                deadlines = linesList;
            } else {
                deadlines = new java.util.ArrayList<>();
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Business logic gateway failure while retrieving professor deadline roster", exception);
            throw exception;
        }
        return deadlines;
    }
    
    public void validateStudentEnrollmentEligibility(StudentDTO student) 
            throws DatabaseSystemException {
        if (student == null) {
            throw new DatabaseSystemException("Los datos del estudiante proporcionados para validación son nulos.");
        }
        
        boolean isActive = SystemConstants.STATUS_ACTIVE.equalsIgnoreCase(student.getStatus());

        if (!isActive) {
            LOGGER.log(Level.WARNING, "Bloqueo de asignación: Intento de inscripción del alumno {0} rechazado por estatus " 
                    + "inactivo.", student.getEnrollmentId());
            throw new DatabaseSystemException("Operación inválida. El alumno con matrícula " + student.getEnrollmentId() 
                    + " no está Activo.");
        }
    }
}