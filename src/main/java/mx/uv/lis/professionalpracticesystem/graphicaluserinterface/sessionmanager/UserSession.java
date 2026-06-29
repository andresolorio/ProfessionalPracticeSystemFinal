package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager;

import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;

/**
 *
 * @author andre
 * @author cinth
 */

public class UserSession {
    private static UserSession instance;
    private UserDTO loggedUser;
    private String userRole;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(UserDTO user) {
        this.loggedUser = user;
        if (user != null) {
            this.userRole = user.getRole();
        }
    }

    public UserDTO getLoggedUser() {
        return loggedUser;
    }

    public String getUserRole() {
        return userRole;
    }

    public boolean isAdministrator() {
        return "ADMINISTRADOR".equals(userRole);
    }
    
    public boolean isStudent() {
        return "ESTUDIANTE".equals(userRole);
    }

    public void logout() {
        this.loggedUser = null;
        this.userRole = null;
        instance = null;
    }
}