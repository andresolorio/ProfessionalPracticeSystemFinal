package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

/**
 * 
 * @author andre
 */
public class ProjectDTO {
    private int idProject;
    private String projectName;
    private String description;
    private String methodology;
    private String generalObjective;
    private String immediateObjective;
    private String mediatedObjective;
    private String duration;  
    private String responsibilities;
    private String resources;
    private String status;
    private int idLinkedOrganization;
    private String organizationName;
    private int totalVacancies;
    private int availableVacancies;
    private int idTechnicalResponsible;
    private String technicalResponsibleName;

    public ProjectDTO(int idProject, String projectName, String description, 
            String methodology, String generalObjective, String immediateObjective, 
            int vacancy, String status, int idLinkedOrganization, String mediatedObjective, 
            String duration, String responsibilities, String resources, String organizationName,
            int totalVacancies, int availableVacancies, int idTechnicalResponsible, String technicalResponsibleName) {
        this.idProject = idProject;
        this.projectName = projectName;
        this.description = description;
        this.methodology = methodology;
        this.generalObjective = generalObjective;
        this.immediateObjective = immediateObjective;
        this.status = status;
        this.idLinkedOrganization = idLinkedOrganization;
        this.mediatedObjective = mediatedObjective;
        this.duration = duration;
        this.responsibilities = responsibilities;
        this.resources = resources;
        this.organizationName = organizationName;
        this.totalVacancies = totalVacancies;
        this.availableVacancies = availableVacancies;
        this.idTechnicalResponsible = idTechnicalResponsible;
        this.technicalResponsibleName = technicalResponsibleName;
    }
    
    public ProjectDTO() {
    }

    public int getIdProject() {
        return idProject;
    }

    public void setIdProject(int idProject) {
        this.idProject = idProject;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public String getGeneralObjective() {
        return generalObjective;
    }

    public void setGeneralObjective(String generalObjective) {
        this.generalObjective = generalObjective;
    }

    public String getImmediateObjective() {
        return immediateObjective;
    }

    public void setImmediateObjective(String immediateObjective) {
        this.immediateObjective = immediateObjective;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIdLinkedOrganization() {
        return idLinkedOrganization;
    }

    public void setIdLinkedOrganization(int idLinkedOrganization) {
        this.idLinkedOrganization = idLinkedOrganization;
    }

    public String getMediatedObjective() {
        return mediatedObjective;
    }

    public void setMediatedObjective(String mediatedObjective) {
        this.mediatedObjective = mediatedObjective;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public int getTotalVacancies() {
        return totalVacancies;
    }

    public void setTotalVacancies(int totalVacancies) {
        this.totalVacancies = totalVacancies;
    }

    public int getAvailableVacancies() {
        return availableVacancies;
    }

    public void setAvailableVacancies(int availableVacancies) {
        this.availableVacancies = availableVacancies;
    }

    public int getIdTechnicalResponsible() {
        return idTechnicalResponsible;
    }

    public void setIdTechnicalResponsible(int idTechnicalResponsible) {
        this.idTechnicalResponsible = idTechnicalResponsible;
    }

    public String getTechnicalResponsibleName() {
        return technicalResponsibleName;
    }

    public void setTechnicalResponsibleName(String technicalResponsibleName) {
        this.technicalResponsibleName = technicalResponsibleName;
    }
}