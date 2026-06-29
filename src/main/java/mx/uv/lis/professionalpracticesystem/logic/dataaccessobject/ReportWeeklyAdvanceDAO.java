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
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportWeeklyAdvanceDTO;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IReportWeeklyAdvanceDAO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.VALUE_CONVERSION_TRUE_INT;

/**
 * 
 * @author cinth
 * @author andre
 */
public class ReportWeeklyAdvanceDAO implements IReportWeeklyAdvanceDAO {
    private static final Logger LOGGER = Logger.getLogger(ReportWeeklyAdvanceDAO.class.getName());
    private final DatabaseConnection databaseConnection;


    public ReportWeeklyAdvanceDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

    @Override
    public int saveWeeklyAdvances(List<ReportWeeklyAdvanceDTO> advances, Connection connection) throws DatabaseSystemException {
        
        String query = "INSERT INTO reporte_avance_semanal (idReporte, " 
                + "idActividad, tipoRegistro, semana1, semana2, semana3, " 
                + "semana4) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY " 
                + "UPDATE semana1 = VALUES(semana1), semana2 = VALUES(semana2), " 
                + "semana3 = VALUES(semana3), semana4 = VALUES(semana4)";
        
        int[] result;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (ReportWeeklyAdvanceDTO advance : advances) {
                statement.setInt(1, advance.getIdReport());
                statement.setInt(2, advance.getIdActivity());
                statement.setString(3, advance.getRegistrationType());     
                statement.setInt(4, advance.isWeekOne() ? VALUE_CONVERSION_TRUE_INT : RESET);
                statement.setInt(5, advance.isWeekTwo() ? VALUE_CONVERSION_TRUE_INT : RESET);
                statement.setInt(6, advance.isWeekThree() ? VALUE_CONVERSION_TRUE_INT : RESET);
                statement.setInt(7, advance.isWeekFour() ? VALUE_CONVERSION_TRUE_INT : RESET);
                statement.addBatch();
            }
            result = statement.executeBatch();
            return result.length;
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Critical SQL exception batch processing report weekly advance portfolio", exception);
            throw new DatabaseSystemException("Error al guardar el avance de actividades semanales.", exception);
        }
    }

    @Override
    public List<ReportWeeklyAdvanceDTO> getWeeklyAdvancesByReport(int idReport) throws DatabaseSystemException {
        List<ReportWeeklyAdvanceDTO> weeklyAdvancesList = new ArrayList<>();
        String query = "SELECT * FROM reporte_avance_semanal WHERE idReporte = ?";

        try (Connection connection = this.databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, idReport);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    weeklyAdvancesList.add(this.mapResultSetToDTO(resultSet));
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed database stream query fetching " 
                    + "weekly progress portfolio for report ID: " + idReport, exception);
            throw new DatabaseSystemException("Error técnico al recuperar el histórico de la bitácora.", exception);
        }
        return weeklyAdvancesList;
    }

    private ReportWeeklyAdvanceDTO mapResultSetToDTO(ResultSet resultSet) 
            throws SQLException {
        ReportWeeklyAdvanceDTO weeklyAdvance = new ReportWeeklyAdvanceDTO();
        weeklyAdvance.setIdReport(resultSet.getInt("idReporte"));
        weeklyAdvance.setIdActivity(resultSet.getInt("idActividad"));
        weeklyAdvance.setRegistrationType(resultSet.getString("tipoRegistro"));
        weeklyAdvance.setWeekOne(resultSet.getInt("semana1") == VALUE_CONVERSION_TRUE_INT);
        weeklyAdvance.setWeekTwo(resultSet.getInt("semana2") == VALUE_CONVERSION_TRUE_INT);
        weeklyAdvance.setWeekThree(resultSet.getInt("semana3") == VALUE_CONVERSION_TRUE_INT);
        weeklyAdvance.setWeekFour(resultSet.getInt("semana4") == VALUE_CONVERSION_TRUE_INT);
        
        return weeklyAdvance;
    }
}