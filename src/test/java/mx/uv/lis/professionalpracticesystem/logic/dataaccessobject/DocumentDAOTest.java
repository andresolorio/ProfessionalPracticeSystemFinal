package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DocumentDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOCUMENT_STATUS_PENDING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EXPECTED_ROWS_UPSERTED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EXPECTED_SINGLE_ROW_AFFECTED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.FIRST_ELEMENT_INDEX;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.ROLE_STUDENT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATUS_ACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_DAY_FIFTEEN;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_DOC_EMAIL;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_DOC_ENROLLMENT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_DOC_NAME_NEW;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_DOC_NAME_TARGET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_DOC_NAME_UPDATED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_DOC_TYPE_NEW;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_DOC_TYPE_TARGET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_DUMMY_BYTES;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_MONTH_JUNE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEST_YEAR;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @autor andre
 * @author cinth
 */
public class DocumentDAOTest {
    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private DocumentDAO documentDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        this.documentDAO = new DocumentDAO();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM Documentos WHERE matricula = '" + TEST_DOC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_DOC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_DOC_EMAIL + "'");
            
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_DOC_EMAIL + "', 'password123', '" + ROLE_STUDENT + "')");
            statement.executeUpdate("INSERT INTO Alumno (matricula, nombre, apellidoPaterno, genero, estado, creditosCubiertos, email) VALUES ('" + TEST_DOC_ENROLLMENT + "', 'JUnit', 'Student', 'Masculino', '" + STATUS_ACTIVE + "', 120, '" + TEST_DOC_EMAIL + "')");
            statement.executeUpdate("INSERT INTO Documentos (nombreArchivo, rutaArchivo, tipoDocumento, fechaEntrega, matricula) VALUES ('" + TEST_DOC_NAME_TARGET + "', '', '" + TEST_DOC_TYPE_TARGET + "', '2026-06-10', '" + TEST_DOC_ENROLLMENT + "')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM Documentos WHERE matricula = '" + TEST_DOC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_DOC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_DOC_EMAIL + "'");
        }
    }

    private int fetchGeneratedDocumentId() throws SQLException {
        int generatedId = 0;
        String query = "SELECT idDocumentos FROM Documentos WHERE matricula = ? AND nombreArchivo = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, TEST_DOC_ENROLLMENT);
            statement.setString(2, TEST_DOC_NAME_TARGET);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    generatedId = resultSet.getInt("idDocumentos");
                }
            }
        }
        return generatedId;
    }

    @Test
    public void testSaveDocumentInsertNewRecordSuccessful() throws DataIntegrityException, DatabaseSystemException {
        DocumentDTO document = new DocumentDTO();
        document.setFileName(TEST_DOC_NAME_NEW);
        document.setFileData(TEST_DUMMY_BYTES);
        document.setDocumentType(TEST_DOC_TYPE_NEW);
        document.setDeliveryDate(Date.valueOf(LocalDate.of(TEST_YEAR, TEST_MONTH_JUNE, TEST_DAY_FIFTEEN)));
        document.setEnrollment(TEST_DOC_ENROLLMENT);
        
        int rowsAffected = documentDAO.saveDocument(document);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testSaveDocumentUpdateOnDuplicateKeySuccessful() throws DataIntegrityException, DatabaseSystemException {
        DocumentDTO document = new DocumentDTO();
        document.setFileName(TEST_DOC_NAME_UPDATED);
        document.setFileData(TEST_DUMMY_BYTES);
        document.setDocumentType(TEST_DOC_TYPE_TARGET);
        document.setDeliveryDate(Date.valueOf(LocalDate.of(TEST_YEAR, TEST_MONTH_JUNE, TEST_DAY_FIFTEEN)));
        document.setEnrollment(TEST_DOC_ENROLLMENT);
        
        int rowsAffected = documentDAO.saveDocument(document);
        assertEquals(EXPECTED_ROWS_UPSERTED, rowsAffected);
    }

    @Test
    public void testGetDocumentsByEnrollmentValidEnrollmentSuccessful() throws DatabaseSystemException {
        List<DocumentDTO> documents = documentDAO.getDocumentsByEnrollment(TEST_DOC_ENROLLMENT);
        assertEquals(TEST_DOC_NAME_TARGET, documents.get(FIRST_ELEMENT_INDEX).getFileName());
    }

@Test
    public void testUpdateDocumentValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedDocumentId();
        DocumentDTO document = new DocumentDTO();
        document.setIdDocument(targetId);
        document.setFileName(TEST_DOC_NAME_UPDATED);
        document.setFileData(TEST_DUMMY_BYTES);
        document.setDocumentType(TEST_DOC_TYPE_TARGET);
        document.setDeliveryDate(Date.valueOf(LocalDate.of(TEST_YEAR, TEST_MONTH_JUNE, TEST_DAY_FIFTEEN)));
        
        document.setReviewStatus(DOCUMENT_STATUS_PENDING); 
        
        int rowsAffected = documentDAO.updateDocument(document);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testGetSingleDocumentByTypeValidDataSuccessful() throws DatabaseSystemException {
        DocumentDTO document = documentDAO.getSingleDocumentByType(TEST_DOC_ENROLLMENT, TEST_DOC_TYPE_TARGET);
        assertEquals(TEST_DOC_NAME_TARGET, document.getFileName());
    }

}
