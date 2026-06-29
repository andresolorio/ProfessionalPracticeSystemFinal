package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.generator;

import java.util.ArrayList;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.StudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityRowDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.PartialReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportActivityDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.CAREER_SOFTWARE_ENGINEERING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.REPORT_COVERAGE_PREFIX;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.REPORT_HOURS_SEPARATOR;

/**
 * 
 * @author cinth
 * @author andre
 */
public class ReportDataMapper {

    public ReportDataMapper() {
    }

    public PartialReportDTO compilePartialReportData(ReportDTO report, 
                                                     List<ReportActivityDTO> activities, 
                                                     String textResultsFromGui, 
                                                     String textObservationsFromGui) 
                                                     throws DatabaseSystemException, EntityNotFoundException {
            
        PartialReportDTO combinedDTO = new PartialReportDTO();
        StudentDAO studentDAO = new StudentDAO();
        StudentDTO studentData = studentDAO.getStudentWithProfessorByEnrollment(report.getStudentEnrollment());
        
        if (studentData != null) {
            String studentFullName = studentData.getFirstName() + " " 
                    + studentData.getPaternalLastName() + " " 
                    + studentData.getMaternalLastName();
                    
            combinedDTO.setStudentNames(studentFullName);
            combinedDTO.setProfessorName(studentData.getProfessorName());
            combinedDTO.setSchoolPeriod(studentData.getPeriod());
            combinedDTO.setNrc(String.valueOf(studentData.getNrc()));
            
            ProjectDAO projectDAO = new ProjectDAO();
            ProjectDTO projectData = projectDAO.getProjectById(
                    studentData.getIdProject());
            
            if (projectData != null) {
                combinedDTO.setProjectName(projectData.getProjectName());
                combinedDTO.setLinkedOrganizationName(projectData.getOrganizationName());
                combinedDTO.setGeneralObjectives(projectData.getGeneralObjective());
                combinedDTO.setMethodology(projectData.getMethodology());
            }
        }

        combinedDTO.setCareer(CAREER_SOFTWARE_ENGINEERING);
        combinedDTO.setReportNumber(String.valueOf(report.getReportedHours()));
        combinedDTO.setReportDate(report.getDeliveryDate().toString());
        
        String coverageDetails = REPORT_COVERAGE_PREFIX + report.getReportedHours() + REPORT_HOURS_SEPARATOR + report.getHoursCovered();
        combinedDTO.setCoveragePeriodAndHours(coverageDetails);
        
        combinedDTO.setCurrentResults(textResultsFromGui);
        combinedDTO.setObservations(textObservationsFromGui);
        
        List<ActivityRowDTO> timelineRows = new ArrayList<>();
        for (ReportActivityDTO activity : activities) {
            ActivityRowDTO activityRow = new ActivityRowDTO();
            activityRow.setDescription(activity.getActivityName());
            
            activityRow.setPlanWeekOne(true);
            activityRow.setRealWeekOne(activity.isCompleted());
            
            timelineRows.add(activityRow);
        }
        combinedDTO.setActivitiesList(timelineRows);
        
        return combinedDTO;
    }
}