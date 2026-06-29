package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.LinkedOrganizationDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.ILinkedOrganizationDAO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MYSQL_DUPLICATE_KEY_ERROR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.SUCCESS;

/**
 * 
 * @author cinth
 * @author andre
 */
public class LinkedOrganizationDAO implements ILinkedOrganizationDAO {
    private static final Logger LOGGER = Logger.getLogger(LinkedOrganizationDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public LinkedOrganizationDAO() {
        this.databaseConnection = new DatabaseConnection();
    }


    @Override
    public int saveLinkedOrganization(LinkedOrganizationDTO organization) 
            throws DataIntegrityException, DatabaseSystemException {
        int result = RESET;
        String query = "INSERT INTO OrganizacionVinculada (nombreEmpresa, "
                + "direccion, telefono, ciudad, email, sector, "
                + "usuariosDirectos, usuariosIndirectos) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = this.databaseConnection.getConnection();
                PreparedStatement statement = connection
                .prepareStatement(query)) {
            
            statement.setString(1, organization.getLinkedOrganizationName());
            statement.setString(2, organization.getAddress());
            statement.setString(3, organization.getPhoneNumber());
            statement.setString(4, organization.getCity());
            statement.setString(5, organization.getEmail());
            statement.setString(6, organization.getSector());
            statement.setInt(7, organization.getDirectUsers());
            statement.setInt(8, organization.getIndirectUsers());
            
            result = statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "SQL connection pipeline write failure " 
                    + "during linked organization persistence routine.", exception);
            
            if (exception.getErrorCode() == MYSQL_DUPLICATE_KEY_ERROR) {
                throw new DataIntegrityException("Error: La organizacion " 
                        + "ya esta registrada.", exception);
            }
            throw new DatabaseSystemException("Error tecnico al registrar " 
                    + "la organizacion vinculada.", exception);
        }
        return result;
    }

    @Override
    public int updateLinkedOrganization(LinkedOrganizationDTO organization) throws DataIntegrityException, DatabaseSystemException {
        int result = SUCCESS;
        String query = "UPDATE OrganizacionVinculada SET nombreEmpresa = ?, "
                + "direccion = ?, telefono = ?, ciudad = ?, "
                + "email = ?, sector = ?, usuariosDirectos = ?, "
                + "usuariosIndirectos = ? WHERE idOrganizacionVinculada = ?";

        try (Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, organization.getLinkedOrganizationName());
            statement.setString(2, organization.getAddress());
            statement.setString(3, organization.getPhoneNumber());
            statement.setString(4, organization.getCity());
            statement.setString(5, organization.getEmail());
            statement.setString(6, organization.getSector());
            statement.setInt(7, organization.getDirectUsers());
            statement.setInt(8, organization.getIndirectUsers());
            statement.setInt(9, organization.getIdLinkedOrganization());
            result = statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error updating organization row", exception);
            if (exception.getErrorCode() == MYSQL_DUPLICATE_KEY_ERROR) {
                throw new DataIntegrityException("Error de integridad al actualizar la organizacion.", exception);
            }
            throw new DatabaseSystemException("Fallo tecnico al actualizar los datos de la organizacion.", exception);
        }
        return result;
    }

    @Override
    public List<LinkedOrganizationDTO> getAllLinkedOrganizations() throws DatabaseSystemException {
        List<LinkedOrganizationDTO> organizations = new ArrayList<>();
        String query = "SELECT * FROM OrganizacionVinculada";

        try (Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                organizations.add(mapResultSetToDTO(resultSet));
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error retrieving organizations list", exception);
            throw new DatabaseSystemException("Error al obtener la lista de organizaciones.", exception);
        }
        return organizations;
    }

    @Override
    public LinkedOrganizationDTO getLinkedOrganizationById(int idLinkedOrganization) throws EntityNotFoundException, DatabaseSystemException {
        LinkedOrganizationDTO organization = null;
        String query = "SELECT * FROM OrganizacionVinculada WHERE idOrganizacionVinculada = ?";

        try (Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idLinkedOrganization);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    organization = mapResultSetToDTO(resultSet);
                } else {
                    throw new EntityNotFoundException("No se encontro la organizacion con ID: " + idLinkedOrganization);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error retrieving organization by ID", exception);
            throw new DatabaseSystemException("Error tecnico al recuperar la organizacion solicitada.", exception);
        }
        return organization;
    }

    @Override
    public boolean hasOrganizations() throws DatabaseSystemException {
        boolean existOrganizations = false;
        String query = "SELECT 1 FROM OrganizacionVinculada LIMIT 1";

        try (Connection connection = this.databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
            
            existOrganizations = resultSet.next();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error checking organizations existence state", exception);
            throw new DatabaseSystemException("Error técnico al verificar la existencia de organizaciones.", exception);
        }
        return existOrganizations;
    }

    private LinkedOrganizationDTO mapResultSetToDTO(ResultSet resultSet) throws SQLException {
        LinkedOrganizationDTO linkedOrganization = new LinkedOrganizationDTO();
        linkedOrganization.setIdLinkedOrganization(resultSet.getInt("idOrganizacionVinculada"));
        linkedOrganization.setLinkedOrganizationName(resultSet.getString("nombreEmpresa"));
        linkedOrganization.setAddress(resultSet.getString("direccion"));
        linkedOrganization.setPhoneNumber(resultSet.getString("telefono"));
        linkedOrganization.setCity(resultSet.getString("ciudad"));
        linkedOrganization.setEmail(resultSet.getString("email"));
        linkedOrganization.setSector(resultSet.getString("sector"));
        linkedOrganization.setDirectUsers(resultSet.getInt("usuariosDirectos"));
        linkedOrganization.setIndirectUsers(resultSet.getInt("usuariosIndirectos"));
        return linkedOrganization;
    }
}