package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ReportDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IReportDAO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.EmailManager.sendReportEvaluationEmail;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_PROJECTS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.IS_COORDINATOR;

/**
 * 
 * @author andre
 * @author cinth
 */
public class ProfessorController {
    private static final Logger LOGGER = Logger.getLogger(ProfessorController.class.getName());
    private final IProfessorDAO professorDAO;
    private final IReportDAO reportDAO;

    public ProfessorController() {
        this.professorDAO = new ProfessorDAO();
        this.reportDAO = new ReportDAO();
    }

    public List<ReportDTO> getAssignedStudentsReports(String professorEmail) throws DatabaseSystemException {
        if (professorEmail == null || professorEmail.trim().isEmpty()) {
            throw new DatabaseSystemException("El correo de la sesión del docente es inválido.");
        }

        try {
            ProfessorDTO professor = this.professorDAO.getProfessorByEmail(professorEmail);
            if (professor == null) {
                throw new DatabaseSystemException("No se encontró ningún docente asociado al correo de la sesión.");
            }

            return this.reportDAO.getReportsByProfessorStaffNumber(
                    professor.getProfessorStaffNumber());
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Business logic framework layer failure while pulling assigned reports stream", exception);
            throw exception;
        }
    }

    public void evaluateStudentReport(ReportDTO report) 
            throws DatabaseSystemException {
        if (report == null || report.getIdReport() <= EMPTY_PROJECTS) {
            throw new DatabaseSystemException("Los datos del reporte a evaluar son insuficientes o corruptos.");
        }
        
        boolean isRejected = "Rechazado".equalsIgnoreCase(report.getReviewStatus());
        boolean hasNoObservations = report.getObservations() == null || report.getObservations().trim().isEmpty();

        if (isRejected && hasNoObservations) {
            throw new DatabaseSystemException("Es obligatorio ingresar observaciones si el reporte es rechazado.");
        }

        try {
            this.reportDAO.updateReportEvaluation(report);
            LOGGER.log(Level.INFO, "Report evaluation successfully dispatched for ID: {0}", report.getIdReport());
            
            StudentDAO studentDAO = new StudentDAO();
            StudentDTO student = studentDAO.getStudentByEnrollment(report.getStudentEnrollment());
            
            String studentEmail = "";
            if (student != null) {
                studentEmail = student.getEmail();
            }

            sendReportEvaluationEmail(
                studentEmail, 
                report.getReportType(), 
                report.getReviewStatus(), 
                report.getHoursCovered(), 
                report.getObservations()
            );
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Business logic failure during report evaluation transaction or email dispatch", exception);
            throw exception;
        }
    }
    
    public List<ReportDTO> getAssignedStudentsReportsByStaffNumber(String professorStaffNumber) throws DatabaseSystemException {
        if (professorStaffNumber == null || professorStaffNumber.trim().isEmpty()) {
            throw new DatabaseSystemException("El número de personal proporcionado es inválido.");
        }

        try {
            LOGGER.log(Level.INFO, "Pulling reports directly from core DAO framework using staff number key: {0}", professorStaffNumber);
            return this.reportDAO.getReportsByProfessorStaffNumber(professorStaffNumber);
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Business logic framework layer failure while "
                    + "pulling assigned reports via staff number stream", exception);
            throw exception;
        }
    }

    public int getStudentProjectId(String enrollment) 
            throws DatabaseSystemException {
        if (enrollment == null || enrollment.trim().isEmpty()) {
            throw new DatabaseSystemException("La matrícula del estudiante provista es inválida.");
        }
        int projectId = 0;
        try {
            StudentDAO studentDAO = new StudentDAO();
            StudentDTO student = studentDAO.getStudentByEnrollment(enrollment);
            if (student != null) {
                projectId = student.getIdProject();
            }
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Business logic infrastructure failure "
                    + "while fetching student project cross-match target: " + enrollment, exception);
            throw exception;
        }
        return projectId;
    }
    
    public int inactivateProfessor(String professorStaffNumber) 
            throws DatabaseSystemException {
        if (professorStaffNumber == null 
                || professorStaffNumber.trim().isEmpty()) {
            throw new DatabaseSystemException("El número de personal " 
                    + "proporcionado para la inactivación es inválido.");
        }

        try {
            LOGGER.log(Level.INFO, "Validating business criteria for " 
                    + "staff member token context: {0}", professorStaffNumber);

            ProfessorDTO professor = this.professorDAO
                    .getProfessorByPersonalNumber(professorStaffNumber);
            
            if (professor == null) {
                throw new DatabaseSystemException("No se encontró ningún " 
                        + "docente registrado con el número de personal.");
            }

            if (professor.getIsCoordinator()) {
                int activeCoordinators = this.professorDAO
                        .getActiveCoordinatorsCount();
                
                if (activeCoordinators <= IS_COORDINATOR) {
                    LOGGER.log(Level.WARNING, "Aborting operation: Target is " 
                            + "the unique active coordinator in the system.");
                    
                    throw new DatabaseSystemException("No se puede inactivar " 
                            + "al coordinador debido a que es el único usuario " 
                            + "activo con este rol en el sistema. Primero " 
                            + "asigne un nuevo coordinador vigente.");
                }
            }

            return this.professorDAO.inactivateProfessor(professorStaffNumber);

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Exception pipeline sequence caught inside " 
                    + "professor logic controller boundary operations.");
            throw exception;
        }
    }
}