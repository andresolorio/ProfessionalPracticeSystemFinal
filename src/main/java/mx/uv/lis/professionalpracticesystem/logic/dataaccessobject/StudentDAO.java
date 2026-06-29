package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDeliveryMonitorDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentMonitorDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IStudentDAO;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MYSQL_DUPLICATE_KEY_ERROR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;

/**
 * 
 * @author andre
 * @author cinth
 */
public class StudentDAO implements IStudentDAO {
    private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());
    private final DatabaseConnection databaseConnection;

    public StudentDAO() {
        this.databaseConnection = new DatabaseConnection();
    }
   
    public StudentDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public int registerStudent(StudentDTO student, Connection connection) throws DatabaseSystemException, DataIntegrityException {
        Validator.isValidStudent(student);
        Validator.isValidEnrollment(student.getEnrollmentId());
        Validator.isValidEmail(student.getEmail());

        String studentQuery = "INSERT INTO Alumno (matricula, nombre, apellidoPaterno, apellidoMaterno, " 
                + "genero, estado, periodo, creditosCubiertos, calificacion, " 
                + "numeroPersonalProfesor, nrc, email, idProyecto) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        int result = RESET;

        try (PreparedStatement preparedStatement = connection.prepareStatement(studentQuery)) {
            preparedStatement.setString(1, student.getEnrollmentId());
            preparedStatement.setString(2, student.getFirstName());
            preparedStatement.setString(3, student.getPaternalLastName());
            preparedStatement.setString(4, student.getMaternalLastName());
            preparedStatement.setString(5, student.getGender());
            preparedStatement.setString(6, student.getStatus());
            preparedStatement.setString(7, student.getPeriod());
            preparedStatement.setInt(8, student.getCoveredCredits());
            preparedStatement.setFloat(9, student.getGrade());
            preparedStatement.setString(10, student.getCoordinatorPersonalNumber());          
            
            if (student.getNrc() <= RESET) {
                preparedStatement.setNull(11, Types.INTEGER);
            } else {
                preparedStatement.setInt(11, student.getNrc());
            }          
            
            preparedStatement.setString(12, student.getEmail());         
            if (student.getIdProject() == RESET) {
                preparedStatement.setNull(13, Types.INTEGER);
            } else {
                preparedStatement.setInt(13, student.getIdProject());
            }
            
            result = preparedStatement.executeUpdate();

        } catch (SQLException exception) {
            LOGGER.log(Level.WARNING, "Persistence operation interrupted: Attempted to register student with existing unique data.", exception);
            
            if (exception.getErrorCode() == MYSQL_DUPLICATE_KEY_ERROR) {
                String errorMessage = exception.getMessage().toLowerCase();
                
                if (errorMessage.contains("email") || errorMessage.contains("uq") || errorMessage.contains("correo")) {
                    throw new DataIntegrityException("El correo electrónico institucional '" + student.getEmail() 
                            + "' ya se encuentra registrado en el sistema. Por favor, asigne uno diferente.", exception);
                }
                
                throw new DataIntegrityException("La matrícula '" + student.getEnrollmentId() 
                        + "' ya corresponde a un alumno registrado en la institución.", exception);
            }
            
            throw new DatabaseSystemException("Error técnico al procesar el registro del alumno.", exception);
        }
        return result;
    }

    @Override
    public StudentDTO getStudentByEnrollment(String enrollmentId) throws DatabaseSystemException {
        Validator.isValidEnrollment(enrollmentId);
        
        StudentDTO studentDTO = null;
        String query = "SELECT * FROM Alumno WHERE matricula = ?";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setString(1, enrollmentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    studentDTO = this.mapResultSetToDTO(resultSet);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error occurred while searching student by enrollment: " + enrollmentId, exception);
            throw new DatabaseSystemException("Error técnico al buscar al alumno por matrícula.", exception);
        }
        return studentDTO;
    }
 
    @Override
    public List<StudentDTO> getAllActiveStudents() throws DatabaseSystemException {
        List<StudentDTO> students = new ArrayList<>();
        String query = "SELECT * FROM Alumno WHERE estado = 'Activo'";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            while (resultSet.next()) {
                students.add(this.mapResultSetToDTO(resultSet));
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error occurred while fetching all active students list", exception);
            throw new DatabaseSystemException("Error técnico al recuperar la lista de alumnos.", exception);
        }
        return students;
    }
    
    @Override
    public List<StudentDTO> getAllStudents() throws DatabaseSystemException {
        List<StudentDTO> students = new ArrayList<>();
        String query = "SELECT * FROM Alumno";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            while (resultSet.next()) {
                students.add(this.mapResultSetToDTO(resultSet));
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database execution failure: " 
                    + "Failed to fetch all students historic dataset rows.");
            
            throw new DatabaseSystemException("Error técnico al recuperar " 
                    + "la lista completa de alumnos.", exception);
        }
        return students;
    }

    @Override
    public int updateStudentStatus(String enrollmentId, String newStatus) throws DatabaseSystemException {
        Validator.isValidEnrollment(enrollmentId);
        
        String query = "UPDATE Alumno SET estado = ?, "
                     + "periodo = NULL, "
                     + "idProyecto = NULL, "
                     + "numeroPersonalProfesor = NULL, "
                     + "nrc = NULL, "
                     + "motivoAsignacion = NULL "
                     + "WHERE matricula = ?";
        
        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setString(1, newStatus);
            preparedStatement.setString(2, enrollmentId);
            
            return preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error occurred during student administrative withdrawal: " + enrollmentId, exception);
            throw new DatabaseSystemException("Fallo técnico al realizar la baja administrativa del alumno.", exception);
        }
    }
  
    @Override
    public StudentDTO getStudentByEmail(String email) throws DatabaseSystemException {
        Validator.isValidEmail(email);
        
        StudentDTO studentResult = null;
        String query = "SELECT * FROM Alumno WHERE email = ?";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    studentResult = this.mapResultSetToDTO(resultSet);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error occurred while searching student by email: " + email, exception);
            throw new DatabaseSystemException("Error técnico al recuperar los datos del estudiante.", exception);
        }
        return studentResult;
    }

    @Override
    public List<StudentDTO> getStudentsByProfessorEmail(String professorEmail) throws DatabaseSystemException {
        Validator.isValidEmail(professorEmail);
        
        List<StudentDTO> students = new ArrayList<>();
        String query = "SELECT * FROM Alumno WHERE numeroPersonalProfesor = " 
                     + "(SELECT numeroPersonalProfesor FROM Profesor WHERE email = ?)";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setString(1, professorEmail);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(this.mapResultSetToDTO(resultSet));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database execution failure: Failed to query student roster for professor email: " + professorEmail, exception);
            throw new DatabaseSystemException("Error técnico al recuperar la lista de sus alumnos asignados.", exception);
        }
        return students;
    }
 
    @Override
    public int incrementStudentHours(String enrollmentId, int hoursToIncrement) throws DatabaseSystemException {
        Validator.isValidEnrollment(enrollmentId);
        
        int rowsAffected = RESET;
        String query = "UPDATE Alumno SET horasCubiertas = horasCubiertas + ? WHERE matricula = ?";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setInt(1, hoursToIncrement);
            preparedStatement.setString(2, enrollmentId);

            rowsAffected = preparedStatement.executeUpdate();
            LOGGER.log(Level.INFO, "Successfully incremented {0} hours for student: {1}", 
                    new Object[]{hoursToIncrement, enrollmentId});
                    
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database pipeline error during cumulative student hours increment execution", exception);
            throw new DatabaseSystemException("Error técnico al actualizar las horas del alumno.", exception);
        }
        return rowsAffected;
    }
  
    @Override
    public List<StudentMonitorDTO> getStudentsProgressByProfessor(String staffNumber) throws DatabaseSystemException {
        Validator.isValidStaffNumber(staffNumber);
        
        List<StudentMonitorDTO> studentsProgress = new ArrayList<>();
        String query = "SELECT * FROM vw_monitoreo_horas_practicantes WHERE numeroPersonalProfesor = ?";

        try (Connection connection = this.databaseConnection.getConnection(); 
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, staffNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    StudentMonitorDTO student = new StudentMonitorDTO();
                    student.setEnrollment(resultSet.getString("matricula"));
                    student.setFullName(resultSet.getString("nombreCompleto"));
                    student.setProjectName(resultSet.getString("proyecto"));
                    student.setHoursCovered(resultSet.getInt("horasCubiertas"));
                    student.setHoursRemaining(resultSet.getInt("horasRestantes"));

                    studentsProgress.add(student);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error occurred while querying hours progress view for professor: " + staffNumber, exception);
            throw new DatabaseSystemException("Error técnico al recuperar el progreso de las horas de los practicantes.", exception);
        }
        return studentsProgress;
    }
 
    @Override
    public int assignProjectToStudent(String enrollmentId, int projectId, String assignmentReason) throws DatabaseSystemException {
        Validator.isValidEnrollment(enrollmentId);
        
        int rowsAffected = RESET;
        String query = "UPDATE Alumno SET idProyecto = ?, motivoAsignacion = ? WHERE matricula = ?";
        
        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            if (projectId <= RESET) {
                preparedStatement.setNull(1, Types.INTEGER);
            } else {
                preparedStatement.setInt(1, projectId);
            }
            
            preparedStatement.setString(2, assignmentReason);
            preparedStatement.setString(3, enrollmentId);

            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database transactional failure during project allocation mapping context for student: " + enrollmentId, exception);
            throw new DatabaseSystemException("Error técnico al registrar la asignación del proyecto en el sistema.", exception);
        }
        return rowsAffected;
    }
    
    @Override
    public List<StudentDeliveryMonitorDTO> getStudentsDeliveriesByProfessor(String staffNumber) throws DatabaseSystemException {
        Validator.isValidStaffNumber(staffNumber);
        
        List<StudentDeliveryMonitorDTO> deliveriesProgress = new ArrayList<>();
        String query = "SELECT * FROM vw_monitoreo_entregas_practicantes WHERE numeroPersonalProfesor = ?";

        try (Connection connection = this.databaseConnection.getConnection(); 
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, staffNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    StudentDeliveryMonitorDTO student = new StudentDeliveryMonitorDTO();
                    
                    student.setEnrollment(resultSet.getString("matricula"));
                    student.setFullName(resultSet.getString("nombreCompleto"));
                    student.setProjectName(resultSet.getString("proyecto"));
                    student.setValidatedReportsCount(resultSet.getInt("reportesValidados"));
                    student.setSelfEvaluationStatus(resultSet.getString("autoevaluacion"));

                    deliveriesProgress.add(student);
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database error occurred while querying deliveries progress view for professor: " + staffNumber, exception);
            throw new DatabaseSystemException("Error técnico al recuperar el hito de entregas desde la base de datos.", exception);
        }
        return deliveriesProgress;
    }
    
    @Override
    public int assignExperienciaEducativaToStudent(String enrollmentId, int nrc) throws DatabaseSystemException {
        Validator.isValidEnrollment(enrollmentId);
        Validator.isValidNRC(String.valueOf(nrc));
        
        int rowsAffected = RESET;
        String query = "UPDATE Alumno SET nrc = ? WHERE matricula = ?";
        
        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            if (nrc <= RESET) {
                preparedStatement.setNull(1, java.sql.Types.INTEGER);
            } else {
                preparedStatement.setInt(1, nrc);
            }
            preparedStatement.setString(2, enrollmentId);

            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database technical error linking student to educational experience with NRC: " + nrc, exception);
            throw new DatabaseSystemException("Error técnico al registrar la asignación de la Experiencia Educativa.", exception);
        }
        return rowsAffected;
    }

    @Override
    public int updateStudentData(StudentDTO student, Connection connection) throws DatabaseSystemException {
        Validator.isValidStudent(student);
        Validator.isValidEnrollment(student.getEnrollmentId());
        Validator.isValidEmail(student.getEmail());

        String query = "UPDATE Alumno SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, "
                     + "genero = ?, estado = ?, periodo = ?, creditosCubiertos = ?, "
                     + "calificacion = ?, numeroPersonalProfesor = ?, nrc = ?, "
                     + "email = ?, idProyecto = ?, motivoAsignacion = ? "
                     + "WHERE matricula = ?";
        
        int rowsAffected = RESET;
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, student.getFirstName());
            preparedStatement.setString(2, student.getPaternalLastName());
            preparedStatement.setString(3, student.getMaternalLastName());
            preparedStatement.setString(4, student.getGender());
            preparedStatement.setString(5, student.getStatus());
            preparedStatement.setString(6, student.getPeriod());
            preparedStatement.setInt(7, student.getCoveredCredits());
            preparedStatement.setFloat(8, student.getGrade());
            preparedStatement.setString(9, student.getCoordinatorPersonalNumber());
            
            if (student.getNrc() <= RESET) {
                preparedStatement.setNull(10, Types.INTEGER);
            } else {
                preparedStatement.setInt(10, student.getNrc());
            }
            
            preparedStatement.setString(11, student.getEmail());
            
            if (student.getIdProject() == RESET) {
                preparedStatement.setNull(12, Types.INTEGER);
            } else {
                preparedStatement.setInt(12, student.getIdProject());
            }
            
            preparedStatement.setString(13, student.getAssignmentReason());
            preparedStatement.setString(14, student.getEnrollmentId());
            
            rowsAffected = preparedStatement.executeUpdate();
            
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database technical error occurred while updating student data for: " + student.getEnrollmentId(), exception);
            throw new DatabaseSystemException("Error técnico al actualizar la información del estudiante en la base de datos.", exception);
        }
        return rowsAffected;
    }
  
    @Override
    public StudentDTO getStudentWithProfessorByEnrollment(String enrollmentId) throws DatabaseSystemException {
        Validator.isValidEnrollment(enrollmentId);
        
        StudentDTO studentDTO = null;
        String query = "SELECT a.*, CONCAT(p.nombre, ' ', p.apellidoPaterno) AS nombreProfesor "
                + "FROM Alumno a "
                + "LEFT JOIN Profesor p ON a.numeroPersonalProfesor = p.numeroPersonalProfesor "
                + "WHERE a.matricula = ?";

        try (Connection connection = this.databaseConnection.getConnection(); 
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, enrollmentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    studentDTO = this.mapResultSetToDTO(resultSet);
                    studentDTO.setProfessorName(resultSet.getString("nombreProfesor"));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Database technical error occurred while retrieving student with professor details for: " + enrollmentId, exception);
            throw new DatabaseSystemException("Error técnico al recuperar la información académica del estudiante.", exception);
        }
        return studentDTO;
    }
    
    private StudentDTO mapResultSetToDTO(ResultSet resultSet) throws SQLException {
        StudentDTO student = new StudentDTO();
        student.setEnrollmentId(resultSet.getString("matricula"));
        student.setFirstName(resultSet.getString("nombre"));
        student.setPaternalLastName(resultSet.getString("apellidoPaterno"));
        student.setMaternalLastName(resultSet.getString("apellidoMaterno"));
        student.setGender(resultSet.getString("genero"));
        student.setStatus(resultSet.getString("estado"));
        student.setPeriod(resultSet.getString("periodo"));
        student.setCoveredCredits(resultSet.getInt("creditosCubiertos"));
        student.setCoveredHours(resultSet.getInt("horasCubiertas"));
        student.setGrade(resultSet.getFloat("calificacion"));
        student.setCoordinatorPersonalNumber(resultSet.getString("numeroPersonalProfesor"));  
        student.setNrc(resultSet.getInt("nrc"));      
        student.setEmail(resultSet.getString("email"));
        student.setIdProject(resultSet.getInt("idProyecto"));
        student.setAssignmentReason(resultSet.getString("motivoAsignacion"));
        return student;
    }
}