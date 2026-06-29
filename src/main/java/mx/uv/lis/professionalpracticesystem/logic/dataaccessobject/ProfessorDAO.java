package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import static java.lang.String.valueOf;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProfessorDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IProfessorDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MYSQL_DUPLICATE_KEY_ERROR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.SUCCESS;

/**
 * 
 * @author andre
 * @author cinth
 */
public class ProfessorDAO implements IProfessorDAO {
    private static final Logger LOGGER = Logger.getLogger(ProfessorDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public ProfessorDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

    public ProfessorDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public int registerProfessor(ProfessorDTO professorInformation) throws DatabaseSystemException {
        try (Connection connection = this.databaseConnection.getConnection()) {
            return this.registerProfessor(professorInformation, connection);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to open isolated network connection pipe for registry context", exception);
            throw new DatabaseSystemException("Error técnico de comunicación con el servidor cloud.", exception);
        }
    }

    @Override
    public int registerProfessor(ProfessorDTO professorInformation, Connection connection) throws DatabaseSystemException {
        int rowsAffected = RESET;
        String query = "INSERT INTO Profesor (numeroPersonalProfesor, nombre, " 
                + "apellidoPaterno, apellidoMaterno, genero, estado, esCoordinador, email) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, professorInformation.getProfessorStaffNumber());
            preparedStatement.setString(2, professorInformation.getFirstName());
            preparedStatement.setString(3, professorInformation.getPaternalLastName());
            preparedStatement.setString(4, professorInformation.getMaternalLastName());
            preparedStatement.setString(5, professorInformation.getGender());
            preparedStatement.setString(6, SystemConstants.STATUS_ACTIVE);
            preparedStatement.setBoolean(7, professorInformation.getIsCoordinator());
            preparedStatement.setString(8, professorInformation.getEmail());

            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.WARNING, "Database operation suspended: Attempted registration with duplicated constraints");
            this.handleDuplicateEntryException(exception, professorInformation);
            throw new DatabaseSystemException("Error técnico al registrar profesor en la transacción de la base de datos.", exception);
        }
        return rowsAffected;
    }

    @Override
    public int inactivateProfessor(String professorStaffNumber) 
            throws DatabaseSystemException {
        int rowsAffected = RESET;
        String query = "UPDATE Profesor SET estado = ?, fechaBaja = ? " 
                + "WHERE numeroPersonalProfesor = ?";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement preparedStatement = connection
                .prepareStatement(query)) {

            preparedStatement.setString(1, SystemConstants.STATUS_INACTIVE);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(
                    LocalDateTime.now()));
            preparedStatement.setString(3, professorStaffNumber);

            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "SQL data modification write error " 
                    + "detected inside professor inactivation database query routine.");
            
            throw new DatabaseSystemException("Error al conectar con la " 
                    + "base de datos para procesar la inactivación.", exception);
        }
        return rowsAffected;
    }

    @Override
    public int updateCoordinatorRole(String professorStaffNumber, boolean isCoordinator) throws DatabaseSystemException {
        int rowsAffected = RESET;
        String query = "UPDATE Profesor SET esCoordinador = ? WHERE numeroPersonalProfesor = ?";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setBoolean(1, isCoordinator);
            preparedStatement.setString(2, professorStaffNumber);

            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to modify organizational role flag for token: " + professorStaffNumber, exception);
            throw new DatabaseSystemException("Error al intentar cambiar el rol en la base de datos.", exception);
        }
        return rowsAffected;
    }

    @Override
    public ProfessorDTO getProfessorByPersonalNumber(String professorStaffNumber) throws DatabaseSystemException {
        ProfessorDTO professorResult = null;
        String query = "SELECT * FROM Profesor WHERE numeroPersonalProfesor = ?";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, professorStaffNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    professorResult = this.mapResultSetToDTO(resultSet);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "SQL sequential query failure matching key: " + professorStaffNumber, exception);
            throw new DatabaseSystemException("Error al buscar al profesor en el sistema.", exception);
        }
        return professorResult;
    }

    @Override
    public List<ProfessorDTO> getAllProfessors() throws DatabaseSystemException {
        List<ProfessorDTO> professorsList = new ArrayList<>();
        String query = "SELECT * FROM Profesor";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement preparedStatement = connection.prepareStatement(query); 
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                professorsList.add(this.mapResultSetToDTO(resultSet));
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "SQL Error retrieving all existing professors for auditing context", exception);
            throw new DatabaseSystemException("Error al obtener la lista completa de profesores.", exception);
        }
        return professorsList;
    }

    @Override
    public int removeCoordinatorRole(String professorStaffNumber) throws DatabaseSystemException {
        return this.updateCoordinatorRole(professorStaffNumber, false);
    }

    @Override
    public boolean isCoordinator(String email) throws DatabaseSystemException {
        boolean isCoordinatorRole = false;
        String query = "SELECT esCoordinador FROM Profesor WHERE email = ?";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    isCoordinatorRole = resultSet.getBoolean("esCoordinador");
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error checking coordinator role authorization mapping for token: " + email, exception);
            throw new DatabaseSystemException("Error técnico al validar el rol de la sesión académica.", exception);
        }
        return isCoordinatorRole;
    }

    @Override
    public ProfessorDTO getProfessorByEmail(String email) throws DatabaseSystemException {
        ProfessorDTO professorResult = null;
        String query = "SELECT * FROM Profesor WHERE email = ?";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    professorResult = this.mapResultSetToDTO(resultSet);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to match profile dataset matching identity: " + email, exception);
            throw new DatabaseSystemException("Error técnico al recuperar los datos del profesor.", exception);
        }
        return professorResult;
    }

    @Override
    public boolean isCoordinatorAlreadyRegistered() throws DatabaseSystemException {
        boolean exists = false;
        String query = "SELECT COUNT(*) FROM Profesor WHERE esCoordinador = 1 AND estado = '" + SystemConstants.STATUS_ACTIVE + "'";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement statement = connection.prepareStatement(query); 
                ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                exists = resultSet.getInt(1) > SUCCESS;
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database integrity query failure: Failed to verify active coordinator monopoly", exception);
            throw new DatabaseSystemException("Error técnico al verificar la existencia del coordinador en el servidor.", exception);
        }
        return exists;
    }

    @Override
    public boolean hasActiveAssignments(String staffNumber) throws DatabaseSystemException {
        boolean hasAssignments = false;
        String studentQuery = "SELECT COUNT(*) FROM Alumno WHERE numeroPersonalProfesor = ?";
        String eeQuery = "SELECT COUNT(*) FROM ExperienciaEducativa WHERE numeroPersonalProfesor = ?";

        try (Connection connection = this.databaseConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(studentQuery)) {
                statement.setString(1, staffNumber);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        if (resultSet.getInt(1) > SUCCESS) {
                            return true;
                        }
                    }
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(eeQuery)) {
                statement.setString(1, staffNumber);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        if (resultSet.getInt(1) > SUCCESS) {
                            hasAssignments = true;
                        }
                    }
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error checking active assignments constraint validation for professor: " + staffNumber, exception);
            throw new DatabaseSystemException("Error técnico al validar las restricciones del docente en el servidor.", exception);
        }
        return hasAssignments;
    }

    @Override
    public boolean isCoordinatorLimitReachedExcluding(String staffNumber) throws DatabaseSystemException {
        boolean limitReached = false;
        String query = "SELECT COUNT(*) FROM Profesor WHERE esCoordinador = 1 AND estado = '" 
                + SystemConstants.STATUS_ACTIVE + "' AND numeroPersonalProfesor != ?";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, staffNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    if (resultSet.getInt(1) > SUCCESS) {
                        limitReached = true;
                    }
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error executing coordinator exclusivity verification " 
                    + "query for staff number: " + staffNumber, exception);
            throw new DatabaseSystemException("Error técnico al verificar los roles concurrentes en el servidor.", exception);
        }
        return limitReached;
    }

    @Override
    public int getActiveCoordinatorsCount() throws DatabaseSystemException {
        int count = 0;
        String query = "SELECT COUNT(*) FROM Profesor WHERE esCoordinador = 1 AND estado = '" 
                + SystemConstants.STATUS_ACTIVE + "'";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement statement = connection.prepareStatement(query); 
                ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error executing active coordinators count defense query", exception);
            throw new DatabaseSystemException("Error técnico al verificar la persistencia de roles.", exception);
        }
        return count;
    }

    private void handleDuplicateEntryException(SQLException exception, ProfessorDTO professor) throws DatabaseSystemException {
        if (exception.getErrorCode() == MYSQL_DUPLICATE_KEY_ERROR) {
            String databaseMessage = exception.getMessage().toLowerCase();
            if (databaseMessage.contains("primary") || databaseMessage.contains("numeropersonalprofesor")) {
                throw new DatabaseSystemException("El número de personal '" + professor.getProfessorStaffNumber() 
                        + "' ya se encuentra asignado a otro docente.", exception);
            } else if (databaseMessage.contains("email") || databaseMessage.contains("key_email")) {
                throw new DatabaseSystemException("El correo institucional '" 
                        + professor.getEmail() + "' ya está registrado en el sistema.", exception);
            } else {
                throw new DatabaseSystemException("Los datos ingresados pertenecen a un " 
                        + "registro de personal docente que ya existe.", exception);
            }
        }
    }

    @Override
    public List<String> getNrcsByProfessorEmail(String email) 
            throws DatabaseSystemException {
        List<String> educativeeprienceList = new ArrayList<>();
        String query = "SELECT CONCAT(ee.nrc, ' - ', ee.nombreExperienciaEducativa) "
                + "AS eeCompleta FROM ExperienciaEducativa ee "
                + "INNER JOIN Profesor p ON p.numeroPersonalProfesor = "
                + "ee.numeroPersonalProfesor WHERE p.email = ?";

        try (Connection connection = this.databaseConnection.getConnection();
                PreparedStatement statement = connection
                .prepareStatement(query)) {

            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    educativeeprienceList.add(resultSet.getString("eeCompleta"));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Technical error fetching professor course "
                    + "catalog details linked to session key context.");
            throw new DatabaseSystemException("Error técnico al recuperar las "
                    + "Experiencias Educativas impartidas.", exception);
        }
        return educativeeprienceList;
    }
    
    private ProfessorDTO mapResultSetToDTO(ResultSet resultSet) throws SQLException {
        ProfessorDTO professor = new ProfessorDTO();
        professor.setProfessorStaffNumber(resultSet.getString("numeroPersonalProfesor"));
        professor.setFirstName(resultSet.getString("nombre"));
        professor.setPaternalLastName(resultSet.getString("apellidoPaterno"));
        professor.setMaternalLastName(resultSet.getString("apellidoMaterno"));
        professor.setGender(resultSet.getString("genero"));
        professor.setStatus(resultSet.getString("estado"));
        professor.setEmail(resultSet.getString("email"));
        professor.setIsCoordinator(resultSet.getBoolean("esCoordinador"));
        return professor;
    }
}