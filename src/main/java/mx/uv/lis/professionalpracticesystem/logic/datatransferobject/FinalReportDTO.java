package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import java.util.List;

/**
 *
 * @author andre
 * @author cinth
 */
public class FinalReportDTO {

    private String career;
    private String nrc;
    private String professorName;
    private String schoolPeriod;
    private String studentNames;
    private String linkedOrganizationName;
    private String projectName;
    private String totalHoursCovered;
    private String reportDate;
    private String generalObjectives;
    private String appliedMethodology;
    private String finalObservations;
    private List<ReportRowDTO> programmedActivities;
    private List<ReportRowDTO> deliverableProducts;

    public FinalReportDTO(String career, String nrc, String professorName,
            String schoolPeriod, String studentNames, String linkedOrganizationName,
            String projectName, String totalHoursCovered, String reportDate,
            String generalObjectives, String appliedMethodology, String finalObservations,
            List<ReportRowDTO> programmedActivities, List<ReportRowDTO> deliverableProducts) {
        this.career = career;
        this.nrc = nrc;
        this.professorName = professorName;
        this.schoolPeriod = schoolPeriod;
        this.studentNames = studentNames;
        this.linkedOrganizationName = linkedOrganizationName;
        this.projectName = projectName;
        this.totalHoursCovered = totalHoursCovered;
        this.reportDate = reportDate;
        this.generalObjectives = generalObjectives;
        this.appliedMethodology = appliedMethodology;
        this.finalObservations = finalObservations;
        this.programmedActivities = programmedActivities;
        this.deliverableProducts = deliverableProducts;
    }

    public FinalReportDTO() {
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

    public String getTotalHoursCovered() {
        return totalHoursCovered;
    }

    public void setTotalHoursCovered(String totalHoursCovered) {
        this.totalHoursCovered = totalHoursCovered;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getGeneralObjectives() {
        return generalObjectives;
    }

    public void setGeneralObjectives(String generalObjectives) {
        this.generalObjectives = generalObjectives;
    }

    public String getAppliedMethodology() {
        return appliedMethodology;
    }

    public void setAppliedMethodology(String appliedMethodology) {
        this.appliedMethodology = appliedMethodology;
    }

    public String getFinalObservations() {
        return finalObservations;
    }

    public void setFinalObservations(String finalObservations) {
        this.finalObservations = finalObservations;
    }

    public List getProgrammedActivities() {
        return programmedActivities;
    }

    public void setProgrammedActivities(List programmedActivities) {
        this.programmedActivities = programmedActivities;
    }

    public List getDeliverableProducts() {
        return deliverableProducts;
    }

    public void setDeliverableProducts(List deliverableProducts) {
        this.deliverableProducts = deliverableProducts;
    }

}
