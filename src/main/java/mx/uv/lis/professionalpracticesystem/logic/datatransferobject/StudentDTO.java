package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

/**
 * 
 * @author cinth
 */

public class StudentDTO {
    
    private String enrollmentId;
    private String firstName;
    private String paternalLastName;
    private String maternalLastName;
    private String gender;
    private String status;
    private String period; 
    private int coveredCredits;
    private int coveredHours;
    private float grade;
    private String coordinatorPersonalNumber;
    private int nrc;
    private String email;
    private int idProject;
    private String assignmentReason;
    private String professorName;

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public StudentDTO() {
    }

    public StudentDTO(String enrollmentId, String firstName, String paternalLastName, 
            String maternalLastName, String gender, String status, String period, 
            int coveredCredits, int coveredHours, float grade, String coordinatorPersonalNumber, 
            int nrc, String email, int idProject, String assignmentReason) {
        this.enrollmentId = enrollmentId;
        this.firstName = firstName;
        this.paternalLastName = paternalLastName;
        this.maternalLastName = maternalLastName;
        this.gender = gender;
        this.status = status;
        this.period = period;
        this.coveredCredits = coveredCredits;
        this.coveredHours = coveredHours;
        this.grade = grade;
        this.coordinatorPersonalNumber = coordinatorPersonalNumber;
        this.email = email;
        this.idProject = idProject;
        this.assignmentReason = assignmentReason;
    }

    
    
    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPaternalLastName() {
        return paternalLastName;
    }

    public void setPaternalLastName(String paternalLastName) {
        this.paternalLastName = paternalLastName;
    }

    public String getMaternalLastName() {
        return maternalLastName;
    }

    public void setMaternalLastName(String maternalLastName) {
        this.maternalLastName = maternalLastName;
    }
    
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getCoveredCredits() {
        return coveredCredits;
    }

    public void setCoveredCredits(int coveredCredits) {
        this.coveredCredits = coveredCredits;
    }

    public int getCoveredHours() {
        return coveredHours;
    }

    public void setCoveredHours(int coveredHours) {
        this.coveredHours = coveredHours;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public String getCoordinatorPersonalNumber() {
        return coordinatorPersonalNumber;
    }

    public void setCoordinatorPersonalNumber(String coordinatorPersonalNumber) {
        this.coordinatorPersonalNumber = coordinatorPersonalNumber;
    }

    public int getNrc() {
        return nrc;
    }

    public void setNrc(int nrc) {
        this.nrc = nrc;
    } 
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdProject() {
        return idProject;
    }

    public void setIdProject(int idProject) {
        this.idProject = idProject;
    }

    public String getAssignmentReason() {
        return assignmentReason;
    }

    public void setAssignmentReason(String assignmentReason) {
        this.assignmentReason = assignmentReason;
    }
}