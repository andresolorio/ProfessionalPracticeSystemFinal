package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

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
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.LinkedOrganizationDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.*;

/**
 * @autor andre
 * @author cinth
 */
public class LinkedOrganizationDAOTest {
   private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private LinkedOrganizationDAO organizationDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        this.organizationDAO = new LinkedOrganizationDAO();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE nombreEmpresa = '" + TEST_ORG_NAME_TARGET + "'");
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE nombreEmpresa = '" + TEST_ORG_NAME_NEW + "'");
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE nombreEmpresa = '" + TEST_ORG_NAME_UPDATED + "'");
            statement.executeUpdate("INSERT INTO OrganizacionVinculada (nombreEmpresa, direccion, telefono, ciudad, email, sector, usuariosDirectos, usuariosIndirectos) VALUES ('" + TEST_ORG_NAME_TARGET + "', '" + TEST_ORG_ADDRESS + "', '" + TEST_ORG_PHONE + "', '" + TEST_ORG_CITY + "', '" + TEST_ORG_EMAIL + "', '" + SECTOR_PUBLIC + "', " + TEST_ORG_DIRECT_USERS + ", " + TEST_ORG_INDIRECT_USERS + ")");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE nombreEmpresa = '" + TEST_ORG_NAME_TARGET + "'");
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE nombreEmpresa = '" + TEST_ORG_NAME_NEW + "'");
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE nombreEmpresa = '" + TEST_ORG_NAME_UPDATED + "'");
        }
    }

    private int fetchGeneratedOrganizationId() throws SQLException {
        int generatedId = 0;
        String query = "SELECT idOrganizacionVinculada FROM OrganizacionVinculada WHERE nombreEmpresa = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, TEST_ORG_NAME_TARGET);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    generatedId = resultSet.getInt("idOrganizacionVinculada");
                }
            }
        }
        return generatedId;
    }

    @Test
    public void testSaveLinkedOrganizationValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO();
        organization.setLinkedOrganizationName(TEST_ORG_NAME_NEW);
        organization.setAddress(TEST_ORG_ADDRESS);
        organization.setPhoneNumber(TEST_ORG_PHONE);
        organization.setCity(TEST_ORG_CITY);
        organization.setEmail(TEST_ORG_EMAIL);
        organization.setSector(SECTOR_PRIVATE);
        organization.setDirectUsers(TEST_ORG_DIRECT_USERS);
        organization.setIndirectUsers(TEST_ORG_INDIRECT_USERS);
        
        int rowsAffected = organizationDAO.saveLinkedOrganization(organization);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testSaveLinkedOrganizationDuplicateNameUnsuccessful() throws DatabaseSystemException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO();
        organization.setLinkedOrganizationName(TEST_ORG_NAME_TARGET);
        organization.setAddress(TEST_ORG_ADDRESS);
        organization.setPhoneNumber(TEST_ORG_PHONE);
        organization.setCity(TEST_ORG_CITY);
        organization.setEmail(TEST_ORG_EMAIL);
        organization.setSector(SECTOR_PRIVATE);
        
        try {
            organizationDAO.saveLinkedOrganization(organization);
            fail("Expected DataIntegrityException due to unique name constraint violation");
        } catch (DataIntegrityException exception) {
            assertEquals("Error: La organizacion ya esta registrada.", exception.getMessage());
        }
    }

    @Test
    public void testUpdateLinkedOrganizationValidDataSuccessful() throws DataIntegrityException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedOrganizationId();
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO();
        organization.setIdLinkedOrganization(targetId);
        organization.setLinkedOrganizationName(TEST_ORG_NAME_UPDATED);
        organization.setAddress(TEST_ORG_ADDRESS);
        organization.setPhoneNumber(TEST_ORG_PHONE);
        organization.setCity(TEST_ORG_CITY);
        organization.setEmail(TEST_ORG_EMAIL);
        organization.setSector(SECTOR_PUBLIC);
        
        int rowsAffected = organizationDAO.updateLinkedOrganization(organization);
        assertEquals(EXPECTED_SINGLE_ROW_AFFECTED, rowsAffected);
    }

    @Test
    public void testGetAllLinkedOrganizationsExistingDataSuccessful() throws DatabaseSystemException {
        List<LinkedOrganizationDTO> organizations = organizationDAO.getAllLinkedOrganizations();
        int listSize = organizations.size();
        boolean hasElements = listSize > 0;
        assertEquals(true, hasElements);
    }

    @Test
    public void testGetLinkedOrganizationByIdExistingIdSuccessful() throws EntityNotFoundException, DatabaseSystemException, SQLException {
        int targetId = fetchGeneratedOrganizationId();
        LinkedOrganizationDTO organization = organizationDAO.getLinkedOrganizationById(targetId);
        assertEquals(TEST_ORG_NAME_TARGET, organization.getLinkedOrganizationName());
    }

    @Test
    public void testGetLinkedOrganizationByIdNonExistentIdUnsuccessful() throws DatabaseSystemException {
        try {
            organizationDAO.getLinkedOrganizationById(-1);
            fail("Expected EntityNotFoundException was not thrown");
        } catch (EntityNotFoundException exception) {
            assertEquals("No se encontro la organizacion con ID: -1", exception.getMessage());
        }
    }
}