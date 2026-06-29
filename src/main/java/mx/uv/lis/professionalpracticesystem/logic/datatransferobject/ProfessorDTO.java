package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import java.time.LocalDateTime;

/**
 *
 * @author andre
 * @author cinth
 */
public class ProfessorDTO {

    private String professorStaffNumber;
    private String firstName;
    private String paternalLastName;
    private String maternalLastName;
    private String gender;
    private String status;
    private Boolean isCoordinator;
    private LocalDateTime registrationDate;
    private LocalDateTime deactivationDate;
    private String email;

    public ProfessorDTO() {
    }

    public ProfessorDTO(String professorStaffNumber, String firstName,
            String paternalLastName, String status, String gender,
            LocalDateTime registrationDate, LocalDateTime deactivationDate,
            Boolean isCoordinator, String email) {
        this.professorStaffNumber = professorStaffNumber;
        this.firstName = firstName;
        this.paternalLastName = paternalLastName;
        this.status = status;
        this.gender = gender;
        this.registrationDate = registrationDate;
        this.deactivationDate = deactivationDate;
        this.isCoordinator = isCoordinator;
        this.email = email;
    }

    public String getProfessorStaffNumber() {
        return professorStaffNumber;
    }

    public void setProfessorStaffNumber(String professorStaffNumber) {
        this.professorStaffNumber = professorStaffNumber;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(LocalDateTime deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    public Boolean getIsCoordinator() {
        return isCoordinator;
    }

    public void setIsCoordinator(Boolean isCoordinator) {
        this.isCoordinator = isCoordinator;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
