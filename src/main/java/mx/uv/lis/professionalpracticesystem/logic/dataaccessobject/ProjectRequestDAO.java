package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectRequestDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PROJECT_REQUEST_MIN_PRIORITY;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PROJECT_REQUEST_MAX_PRIORITY;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATEMENT_PARAMETER_INDEX_FIRST;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATEMENT_PARAMETER_INDEX_SECOND;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STATEMENT_PARAMETER_INDEX_THIRD;

/**
 * 
 * @author andre
 * @author cinth
 */
public class ProjectRequestDAO {
    private static final Logger LOGGER = Logger.getLogger(
            ProjectRequestDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public ProjectRequestDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

    public ProjectRequestDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    private void validateProjectRequestFields(
            ProjectRequestDTO projectRequestInformation) {
        if (projectRequestInformation == null) {
            throw new ValidationException("Los datos de la solicitud "
                    + "de proyecto no pueden ser nulos.");
        }
        
        String enrollmentId = projectRequestInformation.getEnrollment();
        if (enrollmentId == null || enrollmentId.trim().isEmpty()) {
            throw new ValidationException("La matrícula del estudiante "
                    + "es obligatoria y no puede estar vacía.");
        }
        if (projectRequestInformation.getProjectId() <= RESET) {
            throw new ValidationException("El identificador numérico "
                    + "del proyecto proporcionado es inválido.");
        }
        
        int priorityValue = projectRequestInformation.getPriority();
        if (priorityValue < PROJECT_REQUEST_MIN_PRIORITY 
                || priorityValue > PROJECT_REQUEST_MAX_PRIORITY) {
            throw new ValidationException("La prioridad de la solicitud "
                    + "debe ser un valor numérico entre 1 y 3.");
        }
    }

    public int saveProjectRequest(ProjectRequestDTO projectRequestInformation) 
            throws DatabaseSystemException {
        validateProjectRequestFields(projectRequestInformation);

        int rowsAffected = RESET;
        String insertQuery = "INSERT INTO SolicitudProyecto (matricula, "
                + "idProyecto, prioridad) VALUES (?, ?, ?)";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement insertStatement = connection
                     .prepareStatement(insertQuery)) {

            insertStatement.setString(STATEMENT_PARAMETER_INDEX_FIRST, 
                    projectRequestInformation.getEnrollment());
            insertStatement.setInt(STATEMENT_PARAMETER_INDEX_SECOND, 
                    projectRequestInformation.getProjectId());
            insertStatement.setInt(STATEMENT_PARAMETER_INDEX_THIRD, 
                    projectRequestInformation.getPriority());
            
            rowsAffected = insertStatement.executeUpdate();

        } catch (SQLException exception) {
            String failureLogMessage = String.format(
                    "Failure inserting project request partition. " 
                    + "EnrollmentId: %s, ProjectId: %d, Priority: %d",
                    projectRequestInformation.getEnrollment(), 
                    projectRequestInformation.getProjectId(),
                    projectRequestInformation.getPriority());
            
            LOGGER.log(Level.SEVERE, failureLogMessage, exception);
            throw new DatabaseSystemException("Error técnico al registrar la "
                    + "solicitud de proyecto en la base de datos.", exception);
        }

        return rowsAffected;
    }
}