package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IProfessorDAO;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.sessionmanager.UserSession;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WELCOME_PREFIX;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.SESSION_DATE_PREFIX;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.AUXILIARY_MODE_LABEL;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_STUDENT_SELECTOR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_STAGE_ASSIGNED_EE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_STAGE_STUDENT_HOURS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_STAGE_STUDENT_DELIVERIES;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_STAGE_DEADLINE_CONFIG;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_STAGE_EVALUATE_REPORTS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_LOGIN;

/**
 *
 * @author cinth
 * @author andre
 */
public class ProfessorMenuViewController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            ProfessorMenuViewController.class.getName());
    private static final String DATE_TIME_FORMAT_PATTERN = "dd/MM/yyyy HH:mm";

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label sessionDetailsLabel;
    @FXML
    private Button verifyInitialDocumentsButton;
    @FXML
    private Button viewAssignedEEButton;
    @FXML
    private Button checkStudentHoursButton;
    @FXML
    private Button checkStudentDeliveriesButton;
    @FXML
    private Button configureGeneralDeadlinesButton;
    @FXML
    private Button evaluateGeneralReportsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button backToCoordinatorMenuButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loadUserSessionData();
    }

    private void loadUserSessionData() {
        UserDTO loggedUser = UserSession.getInstance().getLoggedUser();

        if (loggedUser != null) {
            try {
                LOGGER.log(Level.INFO, "Synchronously validating professor " 
                        + "dashboard session state metadata indicators.");
                
                IProfessorDAO professorDAO = new ProfessorDAO();
                ProfessorDTO currentProfessor = professorDAO
                        .getProfessorByEmail(loggedUser.getEmail());

                if (currentProfessor != null) {
                    String fullName = currentProfessor.getFirstName() + " "
                            + currentProfessor.getPaternalLastName() + " "
                            + currentProfessor.getMaternalLastName();

                    this.welcomeLabel.setText(WELCOME_PREFIX + fullName);

                    LocalDateTime currentDateTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter
                            .ofPattern(DATE_TIME_FORMAT_PATTERN);
                    this.sessionDetailsLabel.setText(SESSION_DATE_PREFIX 
                            + currentDateTime.format(formatter));

                    if (SystemConstants.ROLE_COORDINATOR.equalsIgnoreCase(
                            loggedUser.getRole())) {
                        this.backToCoordinatorMenuButton.setVisible(true);
                        this.backToCoordinatorMenuButton.setManaged(true);
                        this.backToCoordinatorMenuButton.setOnAction(
                                new BackToCoordinatorMenuEventHandler(this));
                    } else {
                        this.backToCoordinatorMenuButton.setVisible(false);
                        this.backToCoordinatorMenuButton.setManaged(false);
                    }

                    LOGGER.log(Level.INFO, "Professor session context loaded "
                            + "successfully for user: {0}", 
                            loggedUser.getEmail());
                }
            } catch (DatabaseSystemException exception) {
                LOGGER.log(Level.SEVERE, "Server infrastructure data pipeline " 
                        + "failure updating workspace elements.");
                
                this.welcomeLabel.setText(WELCOME_PREFIX 
                        + loggedUser.getEmail());
                this.sessionDetailsLabel.setText(SESSION_DATE_PREFIX 
                        + AUXILIARY_MODE_LABEL);
            }
        } else {
            LOGGER.log(Level.SEVERE, "Security anomaly: Unauthorized access "
                    + "attempt intercepted in ProfessorMenuView workspace");
            this.handleEnforcedLogout();
        }
    }

    @FXML
    private void handleVerifyInitialDocuments(ActionEvent event) {
        LOGGER.log(Level.INFO, "User triggered system routing toward "
                + "StudentSelectorForVerificationView context workspace.");

        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_STUDENT_SELECTOR_FOR_VERIFICATION, 
                TITLE_STUDENT_SELECTOR);
    }

    @FXML
    private void handleViewAssignedEE(ActionEvent event) {
        LOGGER.log(Level.INFO, "User triggered redirection to Assigned "
                + "Educative Experiences window workspace.");

        NavigationUtility.navigateTo(this.logoutButton,
                ViewConstants.VIEW_ASSIGNED_EDUCATIONAL_EXPERIENCES, TITLE_STAGE_ASSIGNED_EE);
    }

    @FXML
    private void handleCheckStudentHours(ActionEvent event) {
        LOGGER.log(Level.INFO, "User triggered redirection context routing "
                + "to Practicum Student Hours Monitor window workspace.");

        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_STUDENT_HOURS_MONITOR, 
                TITLE_STAGE_STUDENT_HOURS);
    }

    @FXML
    private void handleCheckStudentDeliveries(ActionEvent event) {
        LOGGER.log(Level.INFO, "User triggered redirection context routing "
                + "to Student Deliveries Monitor window pipeline workspace.");

        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_STUDENT_DELIVERIES_MONITOR, 
                TITLE_STAGE_STUDENT_DELIVERIES);
    }

    @FXML
    private void handleConfigureGeneralDeadlines(ActionEvent event) {
        LOGGER.log(Level.INFO, "User triggered redirection context routing "
                + "to General Delivery Deadlines Configurations view layout.");

        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_DEADLINE_CONFIGURATION, 
                TITLE_STAGE_DEADLINE_CONFIG);
    }

    @FXML
    private void handleEvaluateGeneralReports(ActionEvent event) {
        LOGGER.log(Level.INFO, "User triggered redirection context routing "
                + "to Evaluate General Reports List workspace view layout.");

        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_EVALUATE_REPORTS_LIST, 
                TITLE_STAGE_EVALUATE_REPORTS);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        LOGGER.log(Level.INFO, "Professor requested explicit session termination.");
        this.handleEnforcedLogout();
    }

    private void handleEnforcedLogout() {
        UserSession.getInstance().logout();
        LOGGER.log(Level.INFO, "Session terminated instance safely dropped. "
                + "Redirecting client user agent to login stage window context.");

        NavigationUtility.navigateTo(this.logoutButton, 
                ViewConstants.VIEW_LOGIN, TITLE_LOGIN);
    }
    
    public void executeReturnToCoordinatorSequence() {
        LOGGER.log(Level.INFO, "Returning performance context back " 
                + "to the primary CoordinatorMenuView layout stage.");

        NavigationUtility.navigateTo(this.backToCoordinatorMenuButton, 
                ViewConstants.VIEW_COORDINATOR_MENU, 
                SystemConstants.TITLE_COORDINATOR_MENU);
    }

    private static class BackToCoordinatorMenuEventHandler 
            implements EventHandler<ActionEvent> {
        private final ProfessorMenuViewController controller;

        public BackToCoordinatorMenuEventHandler(
                ProfessorMenuViewController controller) {
            this.controller = controller;
        }

        @Override
        public void handle(ActionEvent event) {
            this.controller.executeReturnToCoordinatorSequence();
        }
    }
}