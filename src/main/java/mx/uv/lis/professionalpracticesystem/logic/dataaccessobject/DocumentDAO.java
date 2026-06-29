package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DocumentDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IDocumentDAO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MYSQL_DUPLICATE_KEY_ERROR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.SUCCESS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOCUMENT_STATUS_PENDING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_ACCEPTANCE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_ACCEPTANCE_ALT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_SCHEDULE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_SCHEDULE_ALT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_INSURANCE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_INSURANCE_ALT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_TIMELINE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_TIMELINE_ALT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_ORGANIZATION_EVAL;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOC_TYPE_ORGANIZATION_EVAL_ALT;

/**
 * 
 * @author cinth
 * @author andre
 */
public class DocumentDAO implements IDocumentDAO {
    private static final Logger LOGGER = Logger.getLogger(DocumentDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public DocumentDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

    @Override
    public int saveDocument(DocumentDTO document) throws DataIntegrityException, DatabaseSystemException {
        int result = SUCCESS;
        
        String query = "INSERT INTO Documentos (nombreArchivo, rutaArchivo, " 
                + "tipoDocumento, fechaEntrega, matricula, estadoRevision) "
                + "VALUES (?, ?, ?, ?, ?, '" + DOCUMENT_STATUS_PENDING + "') "
                + "ON DUPLICATE KEY UPDATE "
                + "nombreArchivo = VALUES(nombreArchivo), "
                + "rutaArchivo = VALUES(rutaArchivo), "
                + "fechaEntrega = VALUES(fechaEntrega), "
                + "estadoRevision = '" + DOCUMENT_STATUS_PENDING + "'";

        try (Connection connection = this.databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, document.getFileName());
            statement.setBytes(2, document.getFileData());
            statement.setString(3, document.getDocumentType());
            statement.setDate(4, document.getDeliveryDate());
            statement.setString(5, document.getEnrollment());

            result = statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database persist fail storing file binary stream: " + document.getFileName(), exception);
            if (exception.getErrorCode() == MYSQL_DUPLICATE_KEY_ERROR) {
                throw new DataIntegrityException("El documento ya se encuentra registrado.", exception);
            }
            throw new DatabaseSystemException("Error de conexión al intentar guardar el archivo.", exception);
        }
        return result;
    }

    @Override
    public List<DocumentDTO> getDocumentsByEnrollment(String enrollmentId) throws DatabaseSystemException {
        List<DocumentDTO> documents = new ArrayList<>();
        String query = "SELECT idDocumentos, nombreArchivo, rutaArchivo, tipoDocumento, " 
                + "fechaEntrega, matricula, estadoRevision FROM Documentos "
                + "WHERE matricula = ?";

        try (Connection connection = this.databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, enrollmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    documents.add(mapResultSetToDTO(resultSet));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Extraction infrastructure crashed for enrollment: " + enrollmentId, exception);
            throw new DatabaseSystemException("Error técnico al recuperar la lista de documentos.", exception);
        }
        return documents;
    }

    @Override
    public int updateDocument(DocumentDTO document) throws DataIntegrityException, DatabaseSystemException {
        int result = SUCCESS;
        String query = "UPDATE Documentos SET nombreArchivo = ?, rutaArchivo = ?, " 
                + "tipoDocumento = ?, fechaEntrega = ?, estadoRevision = ? "
                + "WHERE idDocumentos = ?";

        try (Connection connection = this.databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, document.getFileName());
            statement.setBytes(2, document.getFileData());
            statement.setString(3, document.getDocumentType());
            statement.setDate(4, document.getDeliveryDate());
            statement.setString(5, document.getReviewStatus());
            statement.setInt(6, document.getIdDocument());

            result = statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "SQL transactional failure modifying record unique ID: " + document.getIdDocument(), exception);
            throw new DatabaseSystemException("Error al intentar actualizar el archivo en el sistema.", exception);
        }
        return result;
    }
 
    @Override
    public DocumentDTO getSingleDocumentByType(String enrollmentId, String documentType) throws DatabaseSystemException {
        DocumentDTO document = null;
        String query = "SELECT idDocumentos, nombreArchivo, rutaArchivo, tipoDocumento, " 
                + "fechaEntrega, matricula, estadoRevision FROM Documentos "
                + "WHERE matricula = ? AND (tipoDocumento = ? OR tipoDocumento = ?)";

        String alternativeType = documentType;
        if (documentType.equals(DOC_TYPE_ACCEPTANCE)) {
            alternativeType = DOC_TYPE_ACCEPTANCE_ALT;
        } else if (documentType.equals(DOC_TYPE_SCHEDULE) || documentType.equals("Horario")) {
            alternativeType = DOC_TYPE_SCHEDULE_ALT;
        } else if (documentType.equals(DOC_TYPE_INSURANCE)) {
            alternativeType = DOC_TYPE_INSURANCE_ALT;
        } else if (documentType.equals(DOC_TYPE_TIMELINE)) {
            alternativeType = DOC_TYPE_TIMELINE_ALT;
        } else if (documentType.equals(DOC_TYPE_ORGANIZATION_EVAL) 
                || documentType.equals("Evaluación OV")) {
            alternativeType = DOC_TYPE_ORGANIZATION_EVAL_ALT;
        }

        try (Connection connection = this.databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, enrollmentId);
            statement.setString(2, documentType);
            statement.setString(3, alternativeType);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    document = mapResultSetToDTO(resultSet);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to fetch binary file payload data stream context for: " 
                    + enrollmentId + " matching type: " + documentType, exception);
            throw new DatabaseSystemException("Error técnico al recuperar el archivo PDF desde el servidor.", exception);
        }
        return document;
    }

    @Override
    public Map<String, String> getReviewStatusSummaryByEnrollment(String enrollmentId) throws DatabaseSystemException {
        Map<String, String> statusSummary = new HashMap<>();
        String query = "SELECT tipoDocumento, estadoRevision FROM Documentos WHERE matricula = ?";

        try (Connection connection = this.databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, enrollmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String targetDocumentTypeKey = resultSet.getString("tipoDocumento");
                    String activeReviewStatusValue = resultSet.getString("estadoRevision");
                    statusSummary.put(targetDocumentTypeKey, activeReviewStatusValue);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Technical aggregation collapse indexing metadata dataset for student: " + enrollmentId, exception);
            throw new DatabaseSystemException("Error al consultar el resumen de validación del expediente.", exception);
        }
        return statusSummary;
    }

    private DocumentDTO mapResultSetToDTO(ResultSet resultSet) throws SQLException {
        DocumentDTO document = new DocumentDTO();
        document.setIdDocument(resultSet.getInt("idDocumentos"));
        document.setFileName(resultSet.getString("nombreArchivo"));
        document.setFileData(resultSet.getBytes("rutaArchivo"));
        document.setDocumentType(resultSet.getString("tipoDocumento"));
        document.setDeliveryDate(resultSet.getDate("fechaEntrega"));
        document.setEnrollment(resultSet.getString("matricula"));
        document.setReviewStatus(resultSet.getString("estadoRevision"));
        return document;
    }
}