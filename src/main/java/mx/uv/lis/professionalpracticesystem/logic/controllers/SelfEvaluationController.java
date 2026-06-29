package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.EvaluationCriterionDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.LinkedOrganizationDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.SelfEvaluationDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EvaluationCriterionDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.LinkedOrganizationDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.SelfEvaluationDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.generator.SelfEvaluationPDFGenerator;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PATH_SELFEVALUATIONS_DIRECTORY;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PREFIX_SELFEVALUATION_FILE;

/**
 * 
 * @author cinth
 * @author andre
 */
public class SelfEvaluationController {

    private static final Logger LOGGER = Logger.getLogger(SelfEvaluationController.class.getName());
    private final SelfEvaluationDAO selfEvaluationDAO = new SelfEvaluationDAO();
    private final EvaluationCriterionDAO evaluationCriterionDAO = new EvaluationCriterionDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final LinkedOrganizationDAO organizationDAO = new LinkedOrganizationDAO();

    public List<EvaluationCriterionDTO> getEvaluationCriteria() throws DatabaseSystemException {
        return evaluationCriterionDAO.getAllCriteria();
    }

    public void generateAndSaveSelfEvaluation(String enrollmentId, List<EvaluationCriterionDTO> criteriaList) 
            throws DatabaseSystemException, DataIntegrityException {

        int totalScore = 0;
        for (EvaluationCriterionDTO criterion : criteriaList) {
            totalScore += criterion.getScore();
        }

        String fullPath = PATH_SELFEVALUATIONS_DIRECTORY + PREFIX_SELFEVALUATION_FILE + enrollmentId + ".pdf";

        try {
            StudentDTO student = studentDAO.getStudentByEnrollment(enrollmentId);
            ProjectDTO project = projectDAO.getProjectById(student.getIdProject());
            
            LinkedOrganizationDTO organization = organizationDAO.getLinkedOrganizationById(project.getIdLinkedOrganization());
            
            SelfEvaluationPDFGenerator.generatePdfFile(student, project, organization, criteriaList);

            SelfEvaluationDTO selfEvaluationData = new SelfEvaluationDTO();
            selfEvaluationData.setEnrollment(enrollmentId);
            selfEvaluationData.setTotalScore(totalScore);
            selfEvaluationData.setFilePath(fullPath);

            selfEvaluationDAO.saveSelfEvaluation(selfEvaluationData);

        } catch (EntityNotFoundException exception) {
            LOGGER.log(Level.SEVERE, "Data integrity violation fetching student enrollment context: " 
                    + enrollmentId, exception);
            throw new DataIntegrityException("No se puede generar el formato porque el alumno no tiene "
                    + "un proyecto válido asignado.");
                    
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Critical IO stream failure writing self evaluation PDF file asset: " 
                    + enrollmentId, exception);
            throw new DatabaseSystemException("No se pudo generar el archivo PDF de autoevaluación debido "
                    + "a un fallo del sistema de archivos.");
        }
    }
}