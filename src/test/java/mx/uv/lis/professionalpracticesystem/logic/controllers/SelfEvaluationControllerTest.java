package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EvaluationCriterionDTO;
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
public class SelfEvaluationControllerTest {

    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private SelfEvaluationController selfEvaluationController;

@BeforeEach
    public void setUp() throws SQLException {
        this.selfEvaluationController = new SelfEvaluationController();
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            // 1. Limpieza en orden estricto (Hijos -> Padres)
            statement.executeUpdate("DELETE FROM Autoevaluacion WHERE matricula = '" + TEST_SEC_ENROLLMENT + "'");
            statement.executeUpdate("UPDATE Alumno SET idProyecto = NULL WHERE matricula = '" + TEST_SEC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_SEC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_SEC_STUDENT_EMAIL + "'");
            statement.executeUpdate("DELETE FROM Proyecto WHERE idProyecto = " + TEST_SEC_PROJECT_ID);
            // IMPORTANTE: Limpiamos al responsable
            statement.executeUpdate("DELETE FROM ResponsableTecnico WHERE idOrganizacionVinculada = " + TEST_SEC_ORG_ID);
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = " + TEST_SEC_ORG_ID);
            statement.executeUpdate("DELETE FROM CriterioAutoevaluacion");

            // 2. Inserciones (Padres -> Hijos)
            statement.executeUpdate("INSERT INTO OrganizacionVinculada (idOrganizacionVinculada, nombreEmpresa, direccion, telefono, ciudad, email, sector) " +
                                    "VALUES (" + TEST_SEC_ORG_ID + ", '" + TEST_SEC_ORG_NAME + "', 'Testing Ave', '2288888888', 'Xalapa', 'test@org.mx', '" + SECTOR_PRIVATE + "')");
            
            // NUEVO: Insertamos el Responsable Técnico (ID 999 de prueba)
            statement.executeUpdate("INSERT INTO ResponsableTecnico (idResponsable, idOrganizacionVinculada, nombre, primerApellido, cargo) " +
                                    "VALUES (999, " + TEST_SEC_ORG_ID + ", 'Juan', 'Perez', 'Gerente')");

            // MODIFICADO: Vinculamos el proyecto al Responsable Técnico (idResponsable = 999)
            statement.executeUpdate("INSERT INTO Proyecto (idProyecto, nombreProyecto, descripcion, objetivoGeneral, duracion, estado, vacantesTotales, vacantesDisponibles, idOrganizacionVinculada, idResponsable) " +
                                    "VALUES (" + TEST_SEC_PROJECT_ID + ", '" + TEST_SEC_PROJECT_NAME + "', 'Desc', 'Obj', '400', '" + STATUS_ACTIVE + "', 1, 1, " + TEST_SEC_ORG_ID + ", 999)");
            
            statement.executeUpdate("INSERT INTO usuario (email, contraseña, rol) VALUES ('" + TEST_SEC_STUDENT_EMAIL + "', 'pass123', '" + ROLE_STUDENT + "')");

            statement.executeUpdate("INSERT INTO Alumno (matricula, nombre, apellidoPaterno, genero, estado, periodo, creditosCubiertos, idProyecto, email) " +
                                    "VALUES ('" + TEST_SEC_ENROLLMENT + "', 'Student', 'JUnit', 'Femenino', '" + STATUS_ACTIVE + "', '" + DEFAULT_PERIOD + "', 120, " + TEST_SEC_PROJECT_ID + ", '" + TEST_SEC_STUDENT_EMAIL + "')");
            
            statement.executeUpdate("INSERT INTO CriterioAutoevaluacion (idCriterio, afirmacion) VALUES (" + TEST_SEC_CRITERION_ID + ", 'Criterio de prueba')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM Autoevaluacion WHERE matricula = '" + TEST_SEC_ENROLLMENT + "'");
            statement.executeUpdate("UPDATE Alumno SET idProyecto = NULL WHERE matricula = '" + TEST_SEC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM Alumno WHERE matricula = '" + TEST_SEC_ENROLLMENT + "'");
            statement.executeUpdate("DELETE FROM usuario WHERE email = '" + TEST_SEC_STUDENT_EMAIL + "'");
            statement.executeUpdate("DELETE FROM Proyecto WHERE idProyecto = " + TEST_SEC_PROJECT_ID);
            // IMPORTANTE: Limpiamos al responsable también al final
            statement.executeUpdate("DELETE FROM ResponsableTecnico WHERE idOrganizacionVinculada = " + TEST_SEC_ORG_ID);
            statement.executeUpdate("DELETE FROM OrganizacionVinculada WHERE idOrganizacionVinculada = " + TEST_SEC_ORG_ID);
        }
    }

    @Test
    public void testGetEvaluationCriteriaExistingDataSuccessful() throws DatabaseSystemException {
        List<EvaluationCriterionDTO> criteria = selfEvaluationController.getEvaluationCriteria();
        boolean hasElements = criteria.size() > 0;
        assertTrue(hasElements, "El catálogo de criterios de autoevaluación no debería estar vacío.");
    }

    @Test
    public void testGenerateAndSaveSelfEvaluationValidDataSuccessful() throws DatabaseSystemException, DataIntegrityException, SQLException {
        List<EvaluationCriterionDTO> mockCriteriaList = new ArrayList<>();
        EvaluationCriterionDTO criterion = new EvaluationCriterionDTO();
        criterion.setIdCriterion(TEST_SEC_CRITERION_ID);
        criterion.setScore(TEST_SEC_CRITERION_SCORE);
        mockCriteriaList.add(criterion);
        
        selfEvaluationController.generateAndSaveSelfEvaluation(TEST_SEC_ENROLLMENT, mockCriteriaList);
        
        String query = "SELECT puntajeTotal FROM Autoevaluacion WHERE matricula = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, TEST_SEC_ENROLLMENT);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    assertEquals(TEST_SEC_CRITERION_SCORE, rs.getInt("puntajeTotal"));
                } else {
                    fail("El registro de autoevaluación no se insertó en la base de datos.");
                }
            }
        }
    }

    @Test
    public void testGenerateAndSaveSelfEvaluationMissingDependenciesUnsuccessful() throws DatabaseSystemException {
        List<EvaluationCriterionDTO> mockCriteriaList = new ArrayList<>();
        EvaluationCriterionDTO criterion = new EvaluationCriterionDTO();
        criterion.setScore(TEST_SEC_CRITERION_SCORE);
        mockCriteriaList.add(criterion);
        
        try {
            selfEvaluationController.generateAndSaveSelfEvaluation(TEST_SEC_INVALID_ENROLLMENT, mockCriteriaList);
            fail("Expected DataIntegrityException...");
        } catch (DataIntegrityException exception) {
            assertEquals("No se puede generar el formato porque el alumno no existe.", exception.getMessage());
        }
    }
}