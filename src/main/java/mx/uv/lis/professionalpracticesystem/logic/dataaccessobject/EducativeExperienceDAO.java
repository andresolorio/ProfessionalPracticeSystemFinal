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
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EducativeExperienceDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IEducativeExperienceDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MYSQL_DUPLICATE_KEY_ERROR;

/**
 * 
 * @author cinth
 * @author andre
 */
public class EducativeExperienceDAO implements IEducativeExperienceDAO {

    private static final Logger LOGGER = Logger.getLogger(EducativeExperienceDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public EducativeExperienceDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

    @Override
    public int registerEducativeExperience(EducativeExperienceDTO experience) throws DataIntegrityException, DatabaseSystemException {
        int result = SystemConstants.RESET;
        String query = "INSERT INTO ExperienciaEducativa (nrc, "
                + "nombreExperienciaEducativa, seccion, numeroPersonalProfesor) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, experience.getNrc());
            statement.setString(2, experience.getEducativeExperienceName());
            statement.setString(3, experience.getSection());
            statement.setString(4, experience.getProfessorStaffNumber());

            result = statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error registering experience with NRC: " + experience.getNrc(), exception);
            if (exception.getErrorCode() == MYSQL_DUPLICATE_KEY_ERROR) {
                throw new DataIntegrityException("Error: El NRC " + experience.getNrc() + " ya está registrado.", exception);
            }
            throw new DatabaseSystemException("Error en la base de datos al registrar la experiencia educativa.", exception);
        }
        return result;
    }

    @Override
    public List<EducativeExperienceDTO> getAllEducativeExperiencesWithProfessors() throws DatabaseSystemException {
        List<EducativeExperienceDTO> experiences = new ArrayList<>();
        String query = "SELECT ee.nrc, ee.nombreExperienciaEducativa, ee.seccion, "
                + "ee.numeroPersonalProfesor, CONCAT(p.nombre, ' ', p.apellidoPaterno, "
                + "' ', COALESCE(p.apellidoMaterno, '')) AS nombreCompletoProfesor "
                + "FROM ExperienciaEducativa ee INNER JOIN Profesor p "
                + "ON ee.numeroPersonalProfesor = p.numeroPersonalProfesor";

        try (Connection connection = databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                experiences.add(mapResultSetToDTO(resultSet));
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error retrieving all experiences with professor names", exception);
            throw new DatabaseSystemException("Error al consultar las experiencias educativas y sus profesores.", exception);
        }
        return experiences;
    }

    @Override
    public EducativeExperienceDTO getEducativeExperienceWithProfessorByNrc(String nrc) throws EntityNotFoundException, DatabaseSystemException {
        EducativeExperienceDTO experience = null;
        String query = "SELECT ee.nrc, ee.nombreExperienciaEducativa, ee.seccion, "
                + "ee.numeroPersonalProfesor, CONCAT(p.nombre, ' ', p.apellidoPaterno, "
                + "' ', COALESCE(p.apellidoMaterno, '')) AS nombreCompletoProfesor "
                + "FROM ExperienciaEducativa ee INNER JOIN Profesor p "
                + "ON ee.numeroPersonalProfesor = p.numeroPersonalProfesor WHERE ee.nrc = ?";

        try (Connection connection = databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, Integer.parseInt(nrc));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    experience = mapResultSetToDTO(resultSet);
                } else {
                    throw new EntityNotFoundException("No se encontró la experiencia con NRC: " + nrc);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error retrieving by NRC: " + nrc, exception);
            throw new DatabaseSystemException("Error técnico al recuperar la experiencia por NRC.", exception);
        }
        return experience;
    }

    @Override
    public int updateEducativeExperience(EducativeExperienceDTO experience) throws DatabaseSystemException {
        int result = SystemConstants.RESET;
        String query = "UPDATE ExperienciaEducativa SET nombreExperienciaEducativa = ?, "
                + "seccion = ?, numeroPersonalProfesor = ? WHERE nrc = ?";

        try (Connection connection = databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, experience.getEducativeExperienceName());
            statement.setString(2, experience.getSection());
            statement.setString(3, experience.getProfessorStaffNumber());
            statement.setInt(4, experience.getNrc());

            result = statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error updating experience NRC: " + experience.getNrc(), exception);
            throw new DatabaseSystemException("Fallo técnico al intentar actualizar la experiencia.", exception);
        }
        return result;
    }

    @Override
    public List<EducativeExperienceDTO> getEducativeExperiencesWithProfessorsByProfessorEmail(String email) throws DatabaseSystemException {
        List<EducativeExperienceDTO> experiences = new ArrayList<>();
        String query = "SELECT ee.nrc, ee.nombreExperienciaEducativa, ee.seccion, "
                + "ee.numeroPersonalProfesor, CONCAT(p.nombre, ' ', p.apellidoPaterno, "
                + "' ', COALESCE(p.apellidoMaterno, '')) AS nombreCompletoProfesor "
                + "FROM ExperienciaEducativa ee INNER JOIN Profesor p "
                + "ON ee.numeroPersonalProfesor = p.numeroPersonalProfesor WHERE p.email = ?";

        try (Connection connection = databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    experiences.add(mapResultSetToDTO(resultSet));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error retrieving experiences for professor email: " + email, exception);
            throw new DatabaseSystemException("Error técnico al recuperar las experiencias educativas asignadas.", exception);
        }
        return experiences;
    }

    private EducativeExperienceDTO mapResultSetToDTO(ResultSet resultSet) throws SQLException {
        EducativeExperienceDTO educativeExperience = new EducativeExperienceDTO();
        educativeExperience.setNrc(resultSet.getInt("nrc"));
        educativeExperience.setEducativeExperienceName(resultSet.getString("nombreExperienciaEducativa"));
        educativeExperience.setSection(resultSet.getString("seccion"));
        educativeExperience.setProfessorStaffNumber(resultSet.getString("numeroPersonalProfesor"));
        educativeExperience.setProfessorName(resultSet.getString("nombreCompletoProfesor"));

        return educativeExperience;
    }
}
