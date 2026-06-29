package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ResponsibleProjectDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.*;

/**
 *
 * @author cinth
 */
public class ResponsibleProjectControllerTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private ResponsibleProjectController responsibleController;

    @BeforeEach
    public void setUp() throws SQLException {
        this.responsibleController = new ResponsibleProjectController();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.executeUpdate("DELETE FROM ResponsableTecnico WHERE nombre LIKE 'JUnit%'");
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = " + TEST_RP_ORG_ID);
            
            statement.executeUpdate("INSERT INTO OrganizacionVinculada (idOrganizacionVinculada, nombreEmpresa, direccion, telefono, ciudad, email, sector) VALUES (" + TEST_RP_ORG_ID + ", '" + TEST_RP_ORG_NAME + "', 'Avenida Testing', '2288888888', 'Xalapa', 'test@org.mx', '" + SECTOR_PRIVATE + "')");
            
            statement.executeUpdate("INSERT INTO ResponsableTecnico (idOrganizacionVinculada, nombre, primerApellido, cargo) VALUES (" + TEST_RP_ORG_ID + ", '" + TEST_RP_FIRST_NAME_TARGET + "', '" + TEST_RP_LAST_NAME + "', '" + TEST_RP_POSITION + "')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM ResponsableTecnico WHERE nombre LIKE 'JUnit%'");
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = " + TEST_RP_ORG_ID);
        }
    }

    private int fetchGeneratedResponsibleId() throws SQLException {
        int generatedId = 0;
        String query = "SELECT idResponsable FROM ResponsableTecnico WHERE nombre = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, TEST_RP_FIRST_NAME_TARGET);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    generatedId = resultSet.getInt("idResponsable");
                }
            }
        }
        return generatedId;
    }


    @Test
    public void testRegisterResponsibleValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException {
        ResponsibleProjectDTO responsible = new ResponsibleProjectDTO();
        responsible.setIdLinkedOrganization(TEST_RP_ORG_ID);
        responsible.setFirstName(TEST_RP_FIRST_NAME_NEW);
        responsible.setLastName(TEST_RP_LAST_NAME);
        responsible.setPosition(TEST_RP_POSITION);
        
        int rowsAffected = responsibleController.registerResponsible(responsible);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testRegisterResponsibleEmptyFirstNameUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ResponsibleProjectDTO responsible = new ResponsibleProjectDTO();
        responsible.setIdLinkedOrganization(TEST_RP_ORG_ID);
        responsible.setFirstName(EMPTY_STRING);
        responsible.setLastName(TEST_RP_LAST_NAME);
        responsible.setPosition(TEST_RP_POSITION);
        
        try {
            responsibleController.registerResponsible(responsible);
            fail("Expected ValidationException due to empty first name");
        } catch (ValidationException exception) {
            assertEquals("Error: El nombre es obligatorio.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterResponsibleExceededFirstNameLengthUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ResponsibleProjectDTO responsible = new ResponsibleProjectDTO();
        responsible.setIdLinkedOrganization(TEST_RP_ORG_ID);
        responsible.setFirstName(TEST_STRING_EXCEEDING_LIMIT);
        responsible.setLastName(TEST_RP_LAST_NAME);
        responsible.setPosition(TEST_RP_POSITION);
        
        try {
            responsibleController.registerResponsible(responsible);
            fail("Expected ValidationException due to exceeded first name length");
        } catch (ValidationException exception) {
            assertEquals("Error: El nombre no puede exceder los " + MAX_NAME_LENGTH + " caracteres.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterResponsibleEmptyLastNameUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ResponsibleProjectDTO responsible = new ResponsibleProjectDTO();
        responsible.setIdLinkedOrganization(TEST_RP_ORG_ID);
        responsible.setFirstName(TEST_RP_FIRST_NAME_NEW);
        responsible.setLastName(EMPTY_STRING);
        responsible.setPosition(TEST_RP_POSITION);
        
        try {
            responsibleController.registerResponsible(responsible);
            fail("Expected ValidationException due to empty last name");
        } catch (ValidationException exception) {
            assertEquals("Error: El primer apellido es obligatorio.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterResponsibleEmptyPositionUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ResponsibleProjectDTO responsible = new ResponsibleProjectDTO();
        responsible.setIdLinkedOrganization(TEST_RP_ORG_ID);
        responsible.setFirstName(TEST_RP_FIRST_NAME_NEW);
        responsible.setLastName(TEST_RP_LAST_NAME);
        responsible.setPosition(EMPTY_STRING);
        
        try {
            responsibleController.registerResponsible(responsible);
            fail("Expected ValidationException due to empty position");
        } catch (ValidationException exception) {
            assertEquals("Error: El cargo del responsable es obligatorio.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterResponsibleExceededPositionLengthUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ResponsibleProjectDTO responsible = new ResponsibleProjectDTO();
        responsible.setIdLinkedOrganization(TEST_RP_ORG_ID);
        responsible.setFirstName(TEST_RP_FIRST_NAME_NEW);
        responsible.setLastName(TEST_RP_LAST_NAME);
        responsible.setPosition(TEST_STRING_EXCEEDING_LIMIT);
        
        try {
            responsibleController.registerResponsible(responsible);
            fail("Expected ValidationException due to exceeded position length");
        } catch (ValidationException exception) {
            assertEquals("Error: El cargo no puede exceder los " + MAX_POSITION_LENGTH + " caracteres.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterResponsibleInvalidOrganizationIdUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ResponsibleProjectDTO responsible = new ResponsibleProjectDTO();
        responsible.setIdLinkedOrganization(MIN_VALID_ID); // Valor 0 o negativo
        responsible.setFirstName(TEST_RP_FIRST_NAME_NEW);
        responsible.setLastName(TEST_RP_LAST_NAME);
        responsible.setPosition(TEST_RP_POSITION);
        
        try {
            responsibleController.registerResponsible(responsible);
            fail("Expected ValidationException due to invalid organization ID limit bound");
        } catch (ValidationException exception) {
            assertEquals("Error: El responsable debe estar asociado a una organizacion vinculada valida.", exception.getMessage());
        }
    }

    // --- PRUEBAS DE RECUPERACIÓN Y ACTUALIZACIÓN ---

    @Test
    public void testGetResponsiblesByOrganizationValidIdSuccessful() throws DatabaseSystemException {
        List<ResponsibleProjectDTO> responsibles = responsibleController.getResponsiblesByOrganization(TEST_RP_ORG_ID);
        boolean hasElements = responsibles.size() > 0;
        assertTrue(hasElements, "Debe existir al menos un responsable asociado a la organización");
    }

    @Test
    public void testGetResponsiblesByOrganizationInvalidIdUnsuccessful() throws DatabaseSystemException {
        try {
            responsibleController.getResponsiblesByOrganization(MIN_VALID_ID);
            fail("Expected ValidationException due to invalid organization ID bound check");
        } catch (ValidationException exception) {
            assertEquals("Error: El ID de la organizacion no es valido.", exception.getMessage());
        }
    }

    @Test
    public void testGetResponsibleByIdExistingIdSuccessful() throws EntityNotFoundException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedResponsibleId();
        ResponsibleProjectDTO responsible = responsibleController.getResponsibleById(targetId);
        assertEquals(TEST_RP_FIRST_NAME_TARGET, responsible.getFirstName());
    }

    @Test
    public void testGetResponsibleByIdInvalidIdUnsuccessful() throws EntityNotFoundException, DatabaseSystemException {
        try {
            responsibleController.getResponsibleById(MIN_VALID_ID);
            fail("Expected ValidationException due to invalid responsible ID bound check");
        } catch (ValidationException exception) {
            assertEquals("Error: El ID del responsable proporcionado no es valido.", exception.getMessage());
        }
    }

    @Test
    public void testUpdateResponsibleValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedResponsibleId();
        ResponsibleProjectDTO responsible = new ResponsibleProjectDTO();
        responsible.setIdResponsible(targetId);
        responsible.setIdLinkedOrganization(TEST_RP_ORG_ID);
        responsible.setFirstName(TEST_RP_FIRST_NAME_UPDATED);
        responsible.setLastName(TEST_RP_LAST_NAME);
        responsible.setPosition(TEST_RP_POSITION);
        
        int rowsAffected = responsibleController.updateResponsible(responsible);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testUpdateResponsibleInvalidIdUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        ResponsibleProjectDTO responsible = new ResponsibleProjectDTO();
        responsible.setIdResponsible(MIN_VALID_ID); // Valor 0 o negativo
        responsible.setIdLinkedOrganization(TEST_RP_ORG_ID);
        responsible.setFirstName(TEST_RP_FIRST_NAME_UPDATED);
        responsible.setLastName(TEST_RP_LAST_NAME);
        responsible.setPosition(TEST_RP_POSITION);
        
        try {
            responsibleController.updateResponsible(responsible);
            fail("Expected ValidationException due to invalid primary key bound check for update");
        } catch (ValidationException exception) {
            assertEquals("Error: No se puede actualizar un responsable sin un ID valido.", exception.getMessage());
        }
    }
}