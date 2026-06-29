package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.dataaccess.DatabaseConnection;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;

/**
 *
 * @author cinth
 * @author andre
 */
public class PeriodDAO {
    private static final Logger LOGGER = Logger.getLogger(PeriodDAO.class.getName());
    private final DatabaseConnection databaseConnection;


    public PeriodDAO() {
        this.databaseConnection = new DatabaseConnection();
    }
   
    public String getCurrentPeriodFromServer() throws DatabaseSystemException {
        String currentPeriod = null;
        String query = "SELECT fn_obtener_periodo_actual() AS periodo";

        try (Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                currentPeriod = resultSet.getString("periodo");
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Fallo crítico al invocar la función de periodo en el servidor", exception);
            throw new DatabaseSystemException("Error al sincronizar el periodo escolar con la base de datos.", exception);
        }
        return currentPeriod;
    }
}