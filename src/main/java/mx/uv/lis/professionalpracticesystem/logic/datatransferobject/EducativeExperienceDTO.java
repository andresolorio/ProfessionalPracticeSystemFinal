package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

/**
 *
 * @author cinth
 * @author andre
 */
public class EducativeExperienceDTO {

    private int nrc;
    private String educativeExperienceName;
    private String section;
    private String professorStaffNumber;
    private String professorName;

    public EducativeExperienceDTO() {
    }

    public EducativeExperienceDTO(int nrc, String section,
            String educativeExperienceName, String professorStaffNumber) {
        this.nrc = nrc;
        this.section = section;
        this.educativeExperienceName = educativeExperienceName;
        this.professorStaffNumber = professorStaffNumber;
        this.professorName = professorName;
    }

    public int getNrc() {
        return nrc;
    }

    public void setNrc(int nrc) {
        this.nrc = nrc;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getEducativeExperienceName() {
        return educativeExperienceName;
    }

    public void setEducativeExperienceName(String educativeExperienceName) {
        this.educativeExperienceName = educativeExperienceName;
    }

    public String getProfessorStaffNumber() {
        return professorStaffNumber;
    }

    public void setProfessorStaffNumber(String professorStaffNumber) {
        this.professorStaffNumber = professorStaffNumber;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }
}
