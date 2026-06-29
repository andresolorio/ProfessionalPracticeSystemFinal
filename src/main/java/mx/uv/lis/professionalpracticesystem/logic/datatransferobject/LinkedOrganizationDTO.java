package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

/**
 *
 * @author cinth
 * @author andre
 */

public class LinkedOrganizationDTO {
    private int idLinkedOrganization;
    private String linkedOrganizationName;
    private String address;
    private String phoneNumber;
    private String city;
    private String email;
    private String sector;
    private int directUsers;
    private int indirectUsers;
    
    public LinkedOrganizationDTO() {
    }

    public LinkedOrganizationDTO(int idLinkedOrganization, String linkedOrganizationName, 
            String address, String phoneNumber, String city, String email, 
            String sector, int directUsers, int indirectUsers) {
        this.idLinkedOrganization = idLinkedOrganization;
        this.linkedOrganizationName = linkedOrganizationName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.email = email;
        this.sector = sector;
        this.directUsers = directUsers;
        this.indirectUsers = indirectUsers;
    }
  

    public int getIdLinkedOrganization() {
        return idLinkedOrganization;
    }

    public void setIdLinkedOrganization(int idLinkedOrganization) {
        this.idLinkedOrganization = idLinkedOrganization;
    }

    public String getLinkedOrganizationName() {
        return linkedOrganizationName;
    }

    public void setLinkedOrganizationName(String name) {
        this.linkedOrganizationName = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public int getDirectUsers() {
        return directUsers;
    }

    public void setDirectUsers(int directUsers) {
        this.directUsers = directUsers;
    }

    public int getIndirectUsers() {
        return indirectUsers;
    }

    public void setIndirectUsers(int indirectUsers) {
        this.indirectUsers = indirectUsers;
    }
}
