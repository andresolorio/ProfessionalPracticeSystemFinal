package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.LinkedOrganizationDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @autor andre
 * @author cinth
 */
public class LinkedOrganizationControllerTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private LinkedOrganizationController organizationController;

    @BeforeEach
    public void setUp() throws SQLException {
        this.organizationController = new LinkedOrganizationController();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE nombreEmpresa LIKE 'LOC %'");
            
            statement.executeUpdate("INSERT INTO OrganizacionVinculada (nombreEmpresa, direccion, telefono, ciudad, email, sector, usuariosDirectos, usuariosIndirectos) VALUES ('" + TEST_LOC_ORG_TARGET + "', '" + TEST_LOC_ADDRESS + "', '" + TEST_LOC_PHONE_VALID + "', '" + TEST_LOC_CITY + "', '" + TEST_LOC_EMAIL_VALID + "', '" + SECTOR_PUBLIC + "', 10, 50)");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE nombreEmpresa LIKE 'LOC %'");
        }
    }

    private int fetchGeneratedOrganizationId() throws SQLException {
        int generatedId = 0;
        String query = "SELECT idOrganizacionVinculada FROM OrganizacionVinculada WHERE nombreEmpresa = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, TEST_LOC_ORG_TARGET);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    generatedId = resultSet.getInt("idOrganizacionVinculada");
                }
            }
        }
        return generatedId;
    }

    @Test
    public void testRegisterLinkedOrganizationValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException {
        LinkedOrganizationDTO org = new LinkedOrganizationDTO();
        org.setLinkedOrganizationName(TEST_LOC_ORG_NEW);
        org.setAddress(TEST_LOC_ADDRESS);
        org.setPhoneNumber(TEST_LOC_PHONE_VALID);
        org.setCity(TEST_LOC_CITY);
        org.setEmail(TEST_LOC_EMAIL_VALID);
        org.setSector(SECTOR_PRIVATE);
        org.setDirectUsers(10);
        org.setIndirectUsers(50);
        
        int rowsAffected = organizationController.registerLinkedOrganization(org);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testRegisterLinkedOrganizationEmptyNameUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        LinkedOrganizationDTO org = new LinkedOrganizationDTO();
        org.setLinkedOrganizationName(EMPTY_STRING);
        
        try {
            organizationController.registerLinkedOrganization(org);
            fail("Expected ValidationException due to empty organization name was not thrown.");
        } catch (ValidationException exception) {
            assertEquals("Error: El nombre de la empresa es obligatorio.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterLinkedOrganizationNameExceedsLimitUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        LinkedOrganizationDTO org = new LinkedOrganizationDTO();
        org.setLinkedOrganizationName("Esta cadena es inusualmente larguisima y su unico proposito es exceder los limites de la validacion del controlador para lanzar la excepcion");
        
        try {
            organizationController.registerLinkedOrganization(org);
            fail("Expected ValidationException due to name boundary constraint was not thrown.");
        } catch (ValidationException exception) {
            assertEquals("Error: El nombre no puede exceder los " + MAX_NAME_LENGTH + " caracteres.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterLinkedOrganizationInvalidEmailUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        LinkedOrganizationDTO org = new LinkedOrganizationDTO();
        org.setLinkedOrganizationName(TEST_LOC_ORG_NEW);
        org.setEmail(TEST_LOC_EMAIL_INVALID);
        
        try {
            organizationController.registerLinkedOrganization(org);
            fail("Expected ValidationException due to invalid email format was not thrown.");
        } catch (ValidationException exception) {
            assertEquals("Error: El formato del correo electronico es invalido.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterLinkedOrganizationInvalidPhoneUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        LinkedOrganizationDTO org = new LinkedOrganizationDTO();
        org.setLinkedOrganizationName(TEST_LOC_ORG_NEW);
        org.setEmail(TEST_LOC_EMAIL_VALID);
        org.setPhoneNumber(TEST_LOC_PHONE_INVALID); 
        
        try {
            organizationController.registerLinkedOrganization(org);
            fail("Expected ValidationException due to invalid phone number format was not thrown.");
        } catch (ValidationException exception) {
            assertEquals("Error: El numero telefonico debe tener " + PHONE_NUMBER_LENGTH + " digitos.", exception.getMessage());
        }
    }

    @Test
    public void testRegisterLinkedOrganizationEmptyCityUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        LinkedOrganizationDTO org = new LinkedOrganizationDTO();
        org.setLinkedOrganizationName(TEST_LOC_ORG_NEW);
        org.setEmail(TEST_LOC_EMAIL_VALID);
        org.setPhoneNumber(TEST_LOC_PHONE_VALID);
        org.setCity(EMPTY_STRING);
        
        try {
            organizationController.registerLinkedOrganization(org);
            fail("Expected ValidationException due to empty city field was not thrown.");
        } catch (ValidationException exception) {
            assertEquals("Error: El campo de ciudad no puede estar vacio.", exception.getMessage());
        }
    }

    @Test
    public void testGetAllOrganizationsExistingDataSuccessful() throws DatabaseSystemException {
        List<LinkedOrganizationDTO> organizations = organizationController.getAllOrganizations();
        boolean hasElements = organizations.size() > RESET;
        assertTrue(hasElements, "El catálogo de organizaciones no debe estar vacío.");
    }

    @Test
    public void testGetOrganizationByIdValidIdSuccessful() throws EntityNotFoundException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedOrganizationId();
        LinkedOrganizationDTO org = organizationController.getOrganizationById(targetId);
        assertEquals(TEST_LOC_ORG_TARGET, org.getLinkedOrganizationName());
    }

    @Test
    public void testGetOrganizationByIdInvalidIdUnsuccessful() throws EntityNotFoundException, DatabaseSystemException {
        try {
            organizationController.getOrganizationById(MIN_VALID_ID); 
            fail("Expected ValidationException due to invalid search ID bound was not thrown.");
        } catch (ValidationException exception) {
            assertEquals("Error: El ID de la organizacion debe ser un valor positivo.", exception.getMessage());
        }
    }

    @Test
    public void testUpdateLinkedOrganizationValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedOrganizationId();
        LinkedOrganizationDTO org = new LinkedOrganizationDTO();
        org.setIdLinkedOrganization(targetId);
        org.setLinkedOrganizationName(TEST_LOC_ORG_UPDATED);
        org.setAddress(TEST_LOC_ADDRESS);
        org.setPhoneNumber(TEST_LOC_PHONE_VALID);
        org.setCity(TEST_LOC_CITY);
        org.setEmail(TEST_LOC_EMAIL_VALID);
        org.setSector(SECTOR_PRIVATE);
        org.setDirectUsers(20);
        org.setIndirectUsers(100);
        
        int rowsAffected = organizationController.updateLinkedOrganization(org);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testUpdateLinkedOrganizationInvalidIdUnsuccessful() throws DataIntegrityException, DatabaseSystemException {
        LinkedOrganizationDTO org = new LinkedOrganizationDTO();
        org.setIdLinkedOrganization(MIN_VALID_ID); // Valor 0
        org.setLinkedOrganizationName(TEST_LOC_ORG_UPDATED);
        org.setPhoneNumber(TEST_LOC_PHONE_VALID);
        org.setCity(TEST_LOC_CITY);
        org.setEmail(TEST_LOC_EMAIL_VALID);
        
        try {
            organizationController.updateLinkedOrganization(org);
            fail("Expected ValidationException due to invalid update ID bound was not thrown.");
        } catch (ValidationException exception) {
            assertEquals("Error: El ID de la organizacion no es valido para actualizar.", exception.getMessage());
        }
    }
}