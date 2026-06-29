package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andre
 * @author cinth
 */
public class PartialReportDTO {

    private String career;
    private String nrc;
    private String professorName;
    private String schoolPeriod;
    private String studentNames;
    private String linkedOrganizationName;
    private String projectName;
    private String coveragePeriodAndHours;
    private String reportDate;
    private String reportNumber;
    private String generalObjectives;
    private String methodology;
    private String currentResults;
    private String observations;
    private List<ActivityRowDTO> deliverablesList = new ArrayList<>();

    private List<ActivityRowDTO> activitiesList;

    public PartialReportDTO() {
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getSchoolPeriod() {
        return schoolPeriod;
    }

    public void setSchoolPeriod(String schoolPeriod) {
        this.schoolPeriod = schoolPeriod;
    }

    public String getStudentNames() {
        return studentNames;
    }

    public void setStudentNames(String studentNames) {
        this.studentNames = studentNames;
    }

    public String getLinkedOrganizationName() {
        return linkedOrganizationName;
    }

    public void setLinkedOrganizationName(String linkedOrganizationName) {
        this.linkedOrganizationName = linkedOrganizationName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCoveragePeriodAndHours() {
        return coveragePeriodAndHours;
    }

    public void setCoveragePeriodAndHours(String coveragePeriodAndHours) {
        this.coveragePeriodAndHours = coveragePeriodAndHours;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getReportNumber() {
        return reportNumber;
    }

    public void setReportNumber(String reportNumber) {
        this.reportNumber = reportNumber;
    }

    public String getGeneralObjectives() {
        return generalObjectives;
    }

    public void setGeneralObjectives(String generalObjectives) {
        this.generalObjectives = generalObjectives;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public String getCurrentResults() {
        return currentResults;
    }

    public void setCurrentResults(String currentResults) {
        this.currentResults = currentResults;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public List<ActivityRowDTO> getActivitiesList() {
        return activitiesList;
    }

    public void setActivitiesList(List<ActivityRowDTO> activitiesList) {
        this.activitiesList = activitiesList;
    }

    public List<ActivityRowDTO> getDeliverablesList() {
        return deliverablesList;
    }

    public void setDeliverablesList(List<ActivityRowDTO> deliverablesList) {
        this.deliverablesList = deliverablesList;
    }
}
