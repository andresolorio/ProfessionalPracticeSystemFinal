package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers.EvaluateReportsListViewController;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers.ProfessorListViewController;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers.ReportEvaluationWizardViewController;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportDTO;

/**
 * @author andre
 */
public final class NavigationUtility {
    private static final Logger LOGGER = Logger.getLogger(NavigationUtility.class.getName());
    private static Stage loadingStage;

    public NavigationUtility() {
    }

    public static void navigateTo(Control controlReference, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtility.class.getResource(fxmlPath));
            Parent root = (Parent) loader.load();
            
            Stage stage = (Stage) controlReference.getScene().getWindow();
            Scene scene = new Scene(root);
            
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Navigation sequence aborted: Failed to resolve FXML source path context at destination: " + fxmlPath, exception);
            AlertUtility.showErrorAlert("Error de Sistema", "No se pudo cargar la interfaz de destino.");
        }
    }

    public static void navigateToModal(Control controlReference, String fxmlPath, String title, ReportDTO reportData, EvaluateReportsListViewController upstreamController) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtility.class.getResource(fxmlPath));
            Parent root = (Parent) loader.load();
            
            ReportEvaluationWizardViewController downstreamController = (ReportEvaluationWizardViewController) loader.getController();
            downstreamController.setReportData(reportData);
            
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(controlReference.getScene().getWindow());
            stage.showAndWait();
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Critical failure orchestrating modal window stream load sequence via NavigationUtility context", exception);
            AlertUtility.showErrorAlert("Error de Sistema", "No se pudo desplegar el asistente de dictamen.");
        }
    }
    
    public static void openProfessorListModal(String mode, String title) {
        if (mode == null || title == null) {
            LOGGER.log(Level.WARNING, "Navigation aborted: Missing configuration metadata attributes tokens.");
            return;
        }

        String fxmlPath = "/fxml/ProfessorListView.fxml";
        LOGGER.log(Level.INFO, "Initiating sequential modal transition window stage targeting resource: {0}", fxmlPath);

        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtility.class.getResource(fxmlPath));
            Parent root = (Parent) loader.load();

            ProfessorListViewController listController = loader.getController();
            if (listController != null) {
                listController.setMode(mode);
            } else {
                LOGGER.log(Level.WARNING, "Target FXML controller structure instance could not be binded.");
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.showAndWait();

        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "GUI framework infrastructure failure: Missing FXML context template resource asset stream", exception);
            AlertUtility.showErrorAlert("Error de Navegación", "No se pudo desplegar la lista de profesores de forma modal.");
        } catch (IllegalStateException exception) {
            LOGGER.log(Level.SEVERE, "FXMLLoader crashed due to location path mismatch definition", exception);
            AlertUtility.showErrorAlert("Error de Sistema", "Error de consistencia en las rutas de recursos FXML.");
        }
    }

    public static void showLoadingWindow(Control controlReference) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtility.class.getResource("/fxml/ProgressIndicatorView.fxml"));
            Parent root = loader.load();
            
            Stage ownerStage = (Stage) controlReference.getScene().getWindow();
            loadingStage = new Stage();
            loadingStage.setScene(new Scene(root));
            loadingStage.setTitle("Por favor espere");
            loadingStage.initModality(Modality.WINDOW_MODAL);
            loadingStage.initOwner(ownerStage);
            loadingStage.initStyle(StageStyle.UTILITY);
            loadingStage.setResizable(false);
            loadingStage.show();
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Critical structural exception: Failed to initialize layout stream for ProgressIndicatorView.fxml", exception);
            AlertUtility.showErrorAlert("Error de Sistema", "No se pudo cargar el indicador de progreso.");
        }
    }

    public static void closeLoadingWindow() {
        if (loadingStage != null) {
            if (loadingStage.isShowing()) {
                loadingStage.close();
            }
        }
    }
}