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
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IProjectDAO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MYSQL_DUPLICATE_KEY_ERROR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.SUCCESS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PROJECT_STATUS_ACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PROJECT_STATUS_INACTIVE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PROJECT_RESPONSIBLE_UNASSIGNED;

/**
 * 
 * @author cinth
 * @author andre
 */
public class ProjectDAO implements IProjectDAO {

    private static final Logger LOGGER = Logger.getLogger(ProjectDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public ProjectDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

    public ProjectDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<ProjectDTO> getAllProjects() throws DatabaseSystemException {
        List<ProjectDTO> projects = new ArrayList<>();
        String query = "SELECT * FROM Proyecto";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                projects.add(this.mapResultSetToDTO(resultSet));
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error mapping all projects " 
                    + "context from architecture repository.", exception);
            throw new DatabaseSystemException("Error técnico al recuperar " 
                    + "los proyectos del servidor.", exception);
        }
        return projects;
    }
    
    @Override
    public int saveProject(ProjectDTO project) throws DataIntegrityException, DatabaseSystemException {
        int result = RESET;
        String query = "INSERT INTO Proyecto (nombreProyecto, descripcion, "
                + "metodologia, objetivoGeneral, objetivoInmediato, "
                + "objetivoMediato, responsabilidades, recursos, duracion, "
                + "estado, idOrganizacionVinculada, vacantesTotales, "
                + "vacantesDisponibles, idResponsable) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, project.getProjectName());
            statement.setString(2, project.getDescription());
            statement.setString(3, project.getMethodology());
            statement.setString(4, project.getGeneralObjective());
            statement.setString(5, project.getImmediateObjective());
            statement.setString(6, project.getMediatedObjective());
            statement.setString(7, project.getResponsibilities());
            statement.setString(8, project.getResources());
            statement.setString(9, project.getDuration());
            statement.setString(10, PROJECT_STATUS_ACTIVE);
            statement.setInt(11, project.getIdLinkedOrganization());
            statement.setInt(12, project.getTotalVacancies());
            statement.setInt(13, project.getTotalVacancies());
            statement.setInt(14, project.getIdTechnicalResponsible());

            result = statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error saving project structure context", 
                    exception);
            if (exception.getErrorCode() == MYSQL_DUPLICATE_KEY_ERROR) {
                throw new DataIntegrityException("Error: El proyecto ya se encuentra registrado.", exception);
            }
            throw new DatabaseSystemException("Error tecnico al registrar el proyecto.", exception);
        }
        return result;
    }

    @Override
    public List<ProjectDTO> getAllAvailableProjects() 
            throws DatabaseSystemException {
        List<ProjectDTO> projects = new ArrayList<>();
        String query = "SELECT p.idProyecto, p.nombreProyecto, p.descripcion, "
                + "p.metodologia, p.objetivoGeneral, p.objetivoInmediato, "
                + "p.objetivoMediato, p.duracion, p.responsabilidades, p.recursos, "
                + "p.estado, p.idOrganizacionVinculada, p.vacantesTotales, "
                + "p.vacantesDisponibles, p.idResponsable, o.nombreEmpresa AS "
                + "nombreOrganizacion, IFNULL(CONCAT(r.nombre, ' ', r.primerApellido, "
                + "' ', IFNULL(r.segundoApellido, '')), '"
                + PROJECT_RESPONSIBLE_UNASSIGNED + "') AS nombreResponsable "
                + "FROM Proyecto p INNER JOIN OrganizacionVinculada o ON "
                + "p.idOrganizacionVinculada = o.idOrganizacionVinculada LEFT JOIN "
                + "ResponsableTecnico r ON p.idResponsable = r.idResponsable "
                + "WHERE p.estado = '" + PROJECT_STATUS_ACTIVE + "'";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement statement = connection.prepareStatement(query); 
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ProjectDTO project = this.mapResultSetToDTO(resultSet);
                project.setOrganizationName(resultSet.getString("nombreOrganizacion"));
                project.setTechnicalResponsibleName(resultSet.getString("nombreResponsable"));

                projects.add(project);
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error retrieving projects with organization metadata JOIN", exception);
            throw new DatabaseSystemException("Error al consultar la lista de proyectos.", exception);
        }
        return projects;
    }

    @Override
    public ProjectDTO getProjectById(int idProject) throws EntityNotFoundException, DatabaseSystemException {
        ProjectDTO project = null;
        String query = "SELECT p.*, o.nombreEmpresa AS nombreOrganizacion, "
                + "CONCAT(r.nombre, ' ', r.primerApellido, ' ', "
                + "IFNULL(r.segundoApellido, '')) AS nombreResponsable "
                + "FROM Proyecto p INNER JOIN OrganizacionVinculada o ON "
                + "p.idOrganizacionVinculada = o.idOrganizacionVinculada INNER JOIN "
                + "ResponsableTecnico r ON p.idResponsable = r.idResponsable "
                + "WHERE p.idProyecto = ? LIMIT 1";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idProject);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    project = this.mapResultSetToDTO(resultSet);
                    project.setOrganizationName(resultSet.getString("nombreOrganizacion"));
                    project.setTechnicalResponsibleName(resultSet.getString("nombreResponsable"));
                } else {
                    throw new EntityNotFoundException("No se encontro el " 
                            + "proyecto con ID: " + idProject);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error retrieving unique project by unique ID", exception);
            throw new DatabaseSystemException("Error tecnico al recuperar el proyecto.", exception);
        }
        return project;
    }

    @Override
    public int updateProject(ProjectDTO project) 
            throws DataIntegrityException, DatabaseSystemException {
        int rowsAffected = RESET;
        String query = "UPDATE Proyecto SET nombreProyecto = ?, "
                + "descripcion = ?, metodologia = ?, objetivoGeneral = ?, "
                + "objetivoInmediato = ?, objetivoMediato = ?, "
                + "duracion = ?, responsabilidades = ?, recursos = ?, "
                + "idOrganizacionVinculada = ?, vacantesTotales = ?, " 
                + "idResponsable = ? WHERE idProyecto = ?";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement statement = connection
                .prepareStatement(query)) {
            statement.setString(1, project.getProjectName());
            statement.setString(2, project.getDescription());
            statement.setString(3, project.getMethodology());
            statement.setString(4, project.getGeneralObjective());
            statement.setString(5, project.getImmediateObjective());
            statement.setString(6, project.getMediatedObjective());
            statement.setString(7, project.getDuration());
            statement.setString(8, project.getResponsibilities());
            statement.setString(9, project.getResources());
            statement.setInt(10, project.getIdLinkedOrganization());
            statement.setInt(11, project.getTotalVacancies());
            statement.setInt(12, project.getIdTechnicalResponsible());
            statement.setInt(13, project.getIdProject());

            rowsAffected = statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "SQL transactional query failure " 
                    + "executing operational modification for project data row.");
            
            if (exception.getErrorCode() == MYSQL_DUPLICATE_KEY_ERROR) {
                throw new DataIntegrityException("Error de integridad: Ya existe " 
                        + "un proyecto con ese nombre asignado.", exception);
            }
            
            if (exception.getErrorCode() == 1811 || exception.getErrorCode() == 4025 
                    || exception.getMessage().toLowerCase().contains("chk_")
                    || exception.getMessage().toLowerCase().contains("constraint")) {
                throw new DataIntegrityException("No se pueden reducir las vacantes " 
                        + "disponibles del proyecto por debajo del número de alumnos " 
                        + "que se encuentran asignados actualmente.", exception);
            }
            
            throw new DatabaseSystemException("Fallo técnico al intentar " 
                    + "actualizar el proyecto.", exception);
        }
        return rowsAffected;
    }

    @Override
    public int deactivateProject(int idProject) throws DatabaseSystemException {
        int rowsAffected = RESET;
        String query = "UPDATE Proyecto SET estado = '" + PROJECT_STATUS_INACTIVE + "' WHERE idProyecto = ?";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idProject);
            rowsAffected = statement.executeUpdate();
            LOGGER.log(Level.INFO, "Project successfully deactivated: ID {0}", idProject);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error overriding project state constraint: " + idProject, exception);
            throw new DatabaseSystemException("No se pudo dar de baja el proyecto en el sistema.");
        }
        return rowsAffected;
    }

    @Override
    public int getAssignedStudentsCount(int idProject) throws DatabaseSystemException {
        String query = "SELECT COUNT(*) FROM Alumno WHERE idProyecto = ?";
        int totalStudents = 0;

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idProject);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    totalStudents = resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed executing student aggregation count check", exception);
            throw new DatabaseSystemException("Error al verificar alumnos asignados.", exception);
        }
        return totalStudents;
    }

    @Override
    public List<ProjectDTO> getRequestedProjectsByStudent(String enrollmentId) throws DatabaseSystemException {
        List<ProjectDTO> projects = new ArrayList<>();
        String query = "SELECT p.idProyecto, p.nombreProyecto, p.vacantesTotales, "
                + "p.vacantesDisponibles FROM Proyecto p INNER JOIN SolicitudProyecto sp "
                + "ON p.idProyecto = sp.idProyecto WHERE sp.matricula = ? " 
                + "ORDER BY sp.prioridad ASC";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, enrollmentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ProjectDTO project = new ProjectDTO();
                    project.setIdProject(resultSet.getInt("idProyecto"));
                    project.setProjectName(resultSet.getString("nombreProyecto"));
                    project.setTotalVacancies(resultSet.getInt("vacantesTotales"));
                    project.setAvailableVacancies(resultSet.getInt("vacantesDisponibles"));
                    projects.add(project);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error retrieving requests for student: " + enrollmentId, exception);
            throw new DatabaseSystemException("Error al cargar las opciones seleccionadas por el alumno.", exception);
        }
        return projects;
    }

    @Override
    public boolean isStudentAlreadyAssigned(String enrollmentId) throws DatabaseSystemException {
        boolean isAssigned = false;
        String query = "SELECT idProyecto FROM Alumno WHERE matricula = ? AND idProyecto IS NOT NULL";

        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, enrollmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                isAssigned = resultSet.next();
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error checking student unique assignment bounds", exception);
            throw new DatabaseSystemException("Error al verificar asignación del alumno.");
        }
        return isAssigned;
    }

    @Override
    public int assignProjectToStudent(String enrollmentId, int projectId) throws DatabaseSystemException {
        int rowsAffected = RESET;
        String query = "UPDATE Alumno SET idProyecto = ? WHERE matricula = ?";
        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, projectId);
            preparedStatement.setString(2, enrollmentId);
            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error assigning project to student enrollment map: " + enrollmentId, exception);
            throw new DatabaseSystemException("Error al asignar el proyecto al alumno.");
        }
        return rowsAffected;
    }

    @Override
    public boolean isStudentAlreadyRegisteredInRequest(String enrollmentId) throws DatabaseSystemException {
        boolean hasSelections = false;
        String query = "SELECT COUNT(*) AS total FROM SolicitudProyecto " 
                + "WHERE matricula = ?";
        try (Connection connection = this.databaseConnection.getConnection(); 
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, enrollmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    hasSelections = resultSet.getInt("total") > SUCCESS;
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error checking existing selections for student: " + enrollmentId, exception);
            throw new DatabaseSystemException("Error al verificar si ya existen solicitudes previas.");
        }
        return hasSelections;
    }

    @Override
    public void saveProjectRequests(String enrollmentId, List<ProjectDTO> projects) throws DatabaseSystemException, DataIntegrityException {
        String deleteQuery = "DELETE FROM SolicitudProyecto WHERE matricula = ?";
        String insertQuery = "INSERT INTO SolicitudProyecto (matricula, idProyecto, prioridad) VALUES (?, ?, ?)";

        try (Connection connection = this.databaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, enrollmentId);
                deleteStmt.executeUpdate();
            }
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                int currentPriority = 1;
                for (ProjectDTO project : projects) {
                    insertStmt.setString(1, enrollmentId);
                    insertStmt.setInt(2, project.getIdProject());
                    insertStmt.setInt(3, currentPriority++);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
            connection.commit();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error saving transaction batch project requests for student: " + enrollmentId, exception);
            throw new DatabaseSystemException("Error técnico al procesar la solicitud de proyectos.", exception);
        }
    }

    private ProjectDTO mapResultSetToDTO(ResultSet resultSet) 
            throws SQLException {
        ProjectDTO project = new ProjectDTO();
        project.setIdProject(resultSet.getInt("idProyecto"));
        project.setProjectName(resultSet.getString("nombreProyecto"));
        project.setDescription(resultSet.getString("descripcion"));
        project.setMethodology(resultSet.getString("metodologia"));
        project.setGeneralObjective(resultSet.getString("objetivoGeneral"));
        project.setImmediateObjective(resultSet.getString("objetivoInmediato"));
        project.setMediatedObjective(resultSet.getString("objetivoMediato"));
        project.setDuration(resultSet.getString("duracion"));
        project.setResponsibilities(resultSet.getString("responsabilidades"));
        project.setResources(resultSet.getString("recursos"));
        project.setStatus(resultSet.getString("estado"));
        project.setIdLinkedOrganization(resultSet.getInt("idOrganizacionVinculada"));
        project.setTotalVacancies(resultSet.getInt("vacantesTotales"));
        project.setAvailableVacancies(resultSet.getInt("vacantesDisponibles"));
        project.setIdTechnicalResponsible(resultSet.getInt("idResponsable"));
        return project;
    }
}