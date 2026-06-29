package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.AlertUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.NavigationUtility;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.utils.ViewConstants;
import mx.uv.lis.professionalpracticesystem.logic.controllers.StudentController;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.DocumentDAO;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DocumentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DocumentRowDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.utils.EmailManager;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOCUMENT_REJECTED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOCUMENT_VALIDATED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOCUMENT_UNDER_REVIEW;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_ACCEPTANCE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_SCHEDULE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_INSURANCE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_ACTIVITY;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_EVALUATION;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_STRING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TITLE_STUDENT_SELECTOR;

/**
 *
 * @author andre
 * @author cinth
 */
public class InitialDocumentsVerificationViewController 
        implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            InitialDocumentsVerificationViewController.class.getName());

    @FXML
    private Label studentInfoLabel;
    @FXML
    private TableView<DocumentRowDTO> documentsTableView;
    @FXML
    private TableColumn<DocumentRowDTO, String> documentNameTableColumn;
    @FXML
    private TableColumn<DocumentRowDTO, String> documentStatusTableColumn;
    @FXML
    private Button viewPdfButton;
    @FXML
    private CheckBox approveCheckBox;
    @FXML
    private TextArea feedbackTextArea;
    @FXML
    private Label emailNoticeLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    private final StudentController studentController;
    private final DocumentDAO documentDAO;
    private StudentDTO selectedStudent;
    private ObservableList<DocumentRowDTO> documentRowsObservableList;

    public InitialDocumentsVerificationViewController() {
        this.studentController = new StudentController();
        this.documentDAO = new DocumentDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.configureTableColumns();
        this.setupTableSelectionListener();
    }

    private void configureTableColumns() {
        this.documentNameTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("documentName"));
        this.documentStatusTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("statusText"));
    }

    private void setupTableSelectionListener() {
        this.documentsTableView.getSelectionModel().selectedItemProperty()
                .addListener(new TableRowSelectionChangeListener(this));
    }

    public void setSelectedStudentContext(StudentDTO student) {
        this.selectedStudent = student;
        if (this.selectedStudent != null) {
            String fullStudentName = this.selectedStudent.getFirstName() + " "
                    + this.selectedStudent.getPaternalLastName() + " "
                    + this.selectedStudent.getMaternalLastName();
            this.studentInfoLabel.setText("Practicante: " 
                    + this.selectedStudent.getEnrollmentId() + " - " 
                    + fullStudentName);
            this.loadStudentDocumentStatus();
        }
    }

    public void handleDocumentRowSelection(DocumentRowDTO oldDocument, 
            DocumentRowDTO selectedDocument) {
        
        if (oldDocument != null && oldDocument.getStatusCode() 
                != DOCUMENT_VALIDATED) {
            oldDocument.setTemporaryFeedback(this.feedbackTextArea.getText());
            oldDocument.setTemporaryApprovedState(this.approveCheckBox
                    .isSelected());
        }

        if (selectedDocument != null) {
            if (selectedDocument.getStatusCode() == DOCUMENT_VALIDATED) {
                this.approveCheckBox.setSelected(true);
                this.approveCheckBox.setDisable(true);
                this.feedbackTextArea.setText("Este formato ya ha sido " 
                        + "verificado y aprobado de forma definitiva.");
                this.feedbackTextArea.setDisable(true);
                this.saveButton.setDisable(true);
            } else {
                this.approveCheckBox.setSelected(selectedDocument
                        .isTemporaryApprovedState());
                this.approveCheckBox.setDisable(false);
                this.feedbackTextArea.setText(selectedDocument
                        .getTemporaryFeedback());
                this.feedbackTextArea.setDisable(selectedDocument
                        .isTemporaryApprovedState());
                this.saveButton.setDisable(false);
            }
        }
    }

    private void loadStudentDocumentStatus() {
        try {
            LOGGER.log(Level.INFO, "Fetching initial formats summary " 
                    + "synchronously from application server layers.");

            Map<String, String> currentSummary = this.studentController
                    .getStudentDocumentStatusSummary(this.selectedStudent
                    .getEnrollmentId());
            
            DocumentRowDTO[] rows = new DocumentRowDTO[]{
                new DocumentRowDTO("Oficio de Aceptación", DOC_TYPE_ACCEPTANCE, 
                        this.mapStringStatusToNumeric(currentSummary
                        .get(DOC_TYPE_ACCEPTANCE))),
                new DocumentRowDTO("Horario Escolar", DOC_TYPE_SCHEDULE, 
                        this.mapStringStatusToNumeric(currentSummary
                        .get(DOC_TYPE_SCHEDULE))),
                new DocumentRowDTO("Certificado de Seguro", DOC_TYPE_INSURANCE, 
                        this.mapStringStatusToNumeric(currentSummary
                        .get(DOC_TYPE_INSURANCE))),
                new DocumentRowDTO("Cronograma de Actividades", 
                        DOC_TYPE_ACTIVITY, this.mapStringStatusToNumeric(
                        currentSummary.get(DOC_TYPE_ACTIVITY))),
                new DocumentRowDTO("Evaluación Organización Vinculada", 
                        DOC_TYPE_EVALUATION, this.mapStringStatusToNumeric(
                        currentSummary.get(DOC_TYPE_EVALUATION)))
            };

            this.documentRowsObservableList = FXCollections
                    .observableArrayList(rows);
            this.documentsTableView.setItems(this.documentRowsObservableList);
            
        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server pipeline error loading student " 
                    + "documents metrics verification dataset.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "recuperar el estado de los documentos del servidor.");
        }
    }
    
    private int mapStringStatusToNumeric(String statusText) {
        int numericCode = DOCUMENT_UNDER_REVIEW;
        if ("Aprobado".equalsIgnoreCase(statusText)) {
            numericCode = DOCUMENT_VALIDATED;
        } else if ("Rechazado".equalsIgnoreCase(statusText)) {
            numericCode = DOCUMENT_REJECTED;
        }
        return numericCode;
    }
    
    @FXML
    private void handleApproveToggle(ActionEvent event) {
        boolean isApproved = this.approveCheckBox.isSelected();
        this.feedbackTextArea.setDisable(isApproved);
        if (isApproved) {
            this.feedbackTextArea.setText(EMPTY_STRING);
        }
    }

    @FXML
    private void handleViewPdf(ActionEvent event) {
        DocumentRowDTO selectedRow = this.documentsTableView
                .getSelectionModel().getSelectedItem();
        
        if (selectedRow == null) {
            AlertUtility.showWarningAlert("Selección Requerida", "Por favor, " 
                    + "seleccione un documento de la lista para visualizar.");
            return;
        }
        
        try {
            this.viewPdfButton.setDisable(true);
            String enrollmentId = this.selectedStudent.getEnrollmentId();
            String documentType = selectedRow.getColumnName();

            LOGGER.log(Level.INFO, "Downloading target visual PDF stream " 
                    + "payload from server backend ecosystem.");
            
            DocumentDTO downloadedDoc = this.studentController
                    .downloadStudentDocument(enrollmentId, documentType);

            if (downloadedDoc != null && downloadedDoc.getFileData() != null) {
                java.nio.file.Path tempFile = java.nio.file.Files
                        .createTempFile("SPP_Document_" + enrollmentId 
                        + "_", ".pdf");
                java.nio.file.Files.write(tempFile, downloadedDoc
                        .getFileData());
                tempFile.toFile().deleteOnExit();

                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(tempFile.toFile());
                } else {
                    AlertUtility.showWarningAlert("Entorno No Soportado", 
                            "El sistema operativo no soporta la apertura " 
                            + "automática de archivos.");
                }
            } else {
                AlertUtility.showWarningAlert("Documento Inexistente", 
                        "El alumno aún no ha cargado este archivo al sistema.");
            }
        } catch (IOException | DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Server communication failure or local " 
                    + "disk IO stream cache writing failure.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "No se pudo " 
                    + "descargar el archivo PDF desde el servidor central.");
        } finally {
            this.viewPdfButton.setDisable(false);
        }
    }

    @FXML
    private void handleSaveValidation(ActionEvent event) {
        DocumentRowDTO currentSelected = this.documentsTableView
                .getSelectionModel().getSelectedItem();
        
        if (currentSelected != null && currentSelected.getStatusCode() 
                != DOCUMENT_VALIDATED) {
            currentSelected.setTemporaryFeedback(this.feedbackTextArea
                    .getText());
            currentSelected.setTemporaryApprovedState(this.approveCheckBox
                    .isSelected());
        }

        if (this.documentRowsObservableList == null 
                || this.documentRowsObservableList.isEmpty()) {
            AlertUtility.showWarningAlert("Sin datos", "No hay ningún " 
                    + "documento cargado en la lista para validar.");
            return;
        }

        try {
            this.saveButton.setDisable(true);
            LOGGER.log(Level.INFO, "Initiating sequential evaluation save " 
                    + "stream and batch notification processing on server.");

            for (DocumentRowDTO row : this.documentRowsObservableList) {
                if (row.getStatusCode() == DOCUMENT_VALIDATED) {
                    continue;
                }

                int numericStatusCode = DOCUMENT_REJECTED;
                String stringStatus = "Rechazado";

                if (row.isTemporaryApprovedState()) {
                    numericStatusCode = DOCUMENT_VALIDATED;
                    stringStatus = "Aprobado";
                }

                row.setStatusCode(numericStatusCode);

                DocumentDTO docTarget = this.documentDAO
                        .getSingleDocumentByType(this.selectedStudent
                        .getEnrollmentId(), row.getColumnName());
                
                if (docTarget != null) {
                    docTarget.setReviewStatus(stringStatus);
                    this.documentDAO.updateDocument(docTarget);
                }
            }
            
            EmailManager.sendInitialDocumentsEvaluationReport(
                    this.selectedStudent.getEmail(),
                    this.selectedStudent.getFirstName(),
                    this.documentRowsObservableList
            );

            AlertUtility.showInformationAlert("Validación Guardada", "Se " 
                    + "han guardado los estados de revisión con éxito.");
            this.loadStudentDocumentStatus();

        } catch (DatabaseSystemException exception) {
            LOGGER.log(Level.SEVERE, "Batch statement persistence runtime " 
                    + "failure within verification context mapping.", exception);
            
            AlertUtility.showErrorAlert("Error de Servidor", "Ocurrió un " 
                    + "fallo al intentar guardar las validaciones.");
        } finally {
            this.saveButton.setDisable(false);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        LOGGER.log(Level.INFO, "Aborting documents verification catalog focus. " 
                + "Routing screen context back to StudentSelectorView.");

        NavigationUtility.navigateTo(this.cancelButton, 
                ViewConstants.VIEW_STUDENT_SELECTOR_FOR_VERIFICATION, 
                TITLE_STUDENT_SELECTOR);
    }

    private static class TableRowSelectionChangeListener 
            implements ChangeListener<DocumentRowDTO> {
        private final InitialDocumentsVerificationViewController 
                controllerReference;

        public TableRowSelectionChangeListener(
                InitialDocumentsVerificationViewController controller) {
            this.controllerReference = controller;
        }

        @Override
        public void changed(ObservableValue<? extends DocumentRowDTO> observable, 
                DocumentRowDTO oldDocument, DocumentRowDTO selectedDocument) {
            this.controllerReference.handleDocumentRowSelection(
                    oldDocument, selectedDocument);
        }
    }
}