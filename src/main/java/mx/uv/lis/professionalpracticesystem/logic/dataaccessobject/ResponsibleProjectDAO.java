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
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ResponsibleProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IResponsibleProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MYSQL_DUPLICATE_KEY_ERROR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;

/**
 * 
 * @author cinth
 * @author andre
 */
public class ResponsibleProjectDAO implements IResponsibleProjectDAO {
    private static final Logger LOGGER = Logger.getLogger(ResponsibleProjectDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public ResponsibleProjectDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

    public ResponsibleProjectDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
    
    @Override
    public List<ResponsibleProjectDTO> getAllResponsibles() 
            throws DatabaseSystemException {
        List<ResponsibleProjectDTO> responsibles = new ArrayList<>();
        String query = "SELECT * FROM ResponsableTecnico";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                responsibles.add(mapResultSetToDTO(resultSet));
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error occurred while " 
                    + "retrieving all responsibles catalog context", exception);
            throw new DatabaseSystemException("Error al consultar los " 
                    + "responsables en el servidor.", exception);
        }
        return responsibles;
    }

    @Override
    public int registerResponsible(ResponsibleProjectDTO responsible) throws DataIntegrityException, DatabaseSystemException {
        Validator.isValidResponsible(responsible);

        int result = RESET;
        String query = "INSERT INTO ResponsableTecnico (idOrganizacionVinculada, nombre, primerApellido, "
                + "segundoApellido, cargo) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, responsible.getIdLinkedOrganization());
            statement.setString(2, responsible.getFirstName());
            statement.setString(3, responsible.getLastName());
            statement.setString(4, responsible.getSecondLastName());
            statement.setString(5, responsible.getPosition());
            result = statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error occurred while registering the responsible", exception);
            if (exception.getErrorCode() == MYSQL_DUPLICATE_KEY_ERROR) {
                throw new DataIntegrityException("Error: El responsable ya se encuentra registrado.", exception);
            }
            throw new DatabaseSystemException("Error técnico al registrar al responsable del proyecto.", exception);
        }
        return result;
    }

    @Override
    public List<ResponsibleProjectDTO> getResponsiblesByOrganization(int idLinkedOrganization) throws DatabaseSystemException {
        Validator.isValidId(idLinkedOrganization);

        List<ResponsibleProjectDTO> responsibles = new ArrayList<>();
        String query = "SELECT * FROM ResponsableTecnico WHERE idOrganizacionVinculada = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idLinkedOrganization);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    responsibles.add(mapResultSetToDTO(resultSet));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error occurred while retrieving responsibles by organization", exception);
            throw new DatabaseSystemException("Error al consultar los responsables en la base de datos.", exception);
        }
        return responsibles;
    }

    @Override
    public ResponsibleProjectDTO getResponsibleById(int idResponsible) throws EntityNotFoundException, DatabaseSystemException {
        Validator.isValidId(idResponsible);

        ResponsibleProjectDTO responsible = null;
        String query = "SELECT * FROM ResponsableTecnico WHERE idResponsable = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idResponsible);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    responsible = mapResultSetToDTO(resultSet);
                } else {
                    throw new EntityNotFoundException("No se encontró el responsable con el ID solicitado.");
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error occurred while retrieving responsible by ID", exception);
            throw new DatabaseSystemException("Error técnico al recuperar el responsable.", exception);
        }
        return responsible;
    }

    @Override
    public int updateResponsible(ResponsibleProjectDTO responsible) throws DataIntegrityException, DatabaseSystemException {
        Validator.isValidResponsible(responsible);

        int result = RESET;
        String query = "UPDATE ResponsableTecnico SET nombre = ?, "
                + "primerApellido = ?, segundoApellido = ?, cargo = ? "
                + "WHERE idResponsable = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, responsible.getFirstName());
            statement.setString(2, responsible.getLastName());
            statement.setString(3, responsible.getSecondLastName());
            statement.setString(4, responsible.getPosition());
            statement.setInt(5, responsible.getIdResponsible());
            result = statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error occurred while updating the responsible", exception);
            if (exception.getErrorCode() == MYSQL_DUPLICATE_KEY_ERROR) {
                throw new DataIntegrityException("Error de integridad al actualizar al responsable.", exception);
            }
            throw new DatabaseSystemException("Fallo técnico al intentar actualizar los datos del responsable.", exception);
        }
        return result;
    }

    private ResponsibleProjectDTO mapResultSetToDTO(ResultSet resultSet) throws SQLException {
        ResponsibleProjectDTO responsibleProject = new ResponsibleProjectDTO();
        responsibleProject.setIdResponsible(resultSet.getInt("idResponsable"));
        responsibleProject.setIdLinkedOrganization(resultSet.getInt("idOrganizacionVinculada"));
        responsibleProject.setFirstName(resultSet.getString("nombre"));
        responsibleProject.setLastName(resultSet.getString("primerApellido"));
        responsibleProject.setSecondLastName(resultSet.getString("segundoApellido"));
        responsibleProject.setPosition(resultSet.getString("cargo"));
        return responsibleProject;
    }
}