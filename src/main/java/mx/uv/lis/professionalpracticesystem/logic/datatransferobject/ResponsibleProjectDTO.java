package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

/**
 *
 * @author andre
 */

public class ResponsibleProjectDTO {
    private int idResponsible;
    private int idLinkedOrganization;
    private String firstName;
    private String lastName;
    private String secondLastName;
    private String position;

    public ResponsibleProjectDTO() {
    }

    public ResponsibleProjectDTO(int idResponsible, int idLinkedOrganization, 
            String firstName, String lastName, String secondLastName, String position) {
        this.idResponsible = idResponsible;
        this.idLinkedOrganization = idLinkedOrganization;
        this.firstName = firstName;
        this.lastName = lastName;
        this.secondLastName = secondLastName;
        this.position = position;
    } 

    public int getIdResponsible() {
        return idResponsible;
    }

    public void setIdResponsible(int idResponsible) {
        this.idResponsible = idResponsible;
    }

    public int getIdLinkedOrganization() {
        return idLinkedOrganization;
    }

    public void setIdLinkedOrganization(int idLinkedOrganization) {
        this.idLinkedOrganization = idLinkedOrganization;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}