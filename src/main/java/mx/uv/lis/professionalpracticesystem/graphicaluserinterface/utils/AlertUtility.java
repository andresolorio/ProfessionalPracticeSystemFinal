package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * 
 * @author andre
 */
public final class AlertUtility {

    public AlertUtility() {
    }

    public static void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showInformationAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showWarningAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static boolean showConfirmationAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        
        ButtonType response = alert.showAndWait().orElse(ButtonType.NO);
        boolean isConfirmed = false;
        if (response == ButtonType.YES) {
            isConfirmed = true;
        }
        return isConfirmed;
    }
}