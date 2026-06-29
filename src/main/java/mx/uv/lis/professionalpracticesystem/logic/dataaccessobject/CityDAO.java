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

/**
 * 
 * @author cinth
 * @author andre
 */
public class CityDAO {
    private static final Logger LOGGER = Logger.getLogger(CityDAO.class.getName());

    public List<String> getAllAvailableCities() throws DatabaseSystemException {
        List<String> cities = new ArrayList<>();
        String query = "SELECT cityName FROM City ORDER BY cityName ASC;";
        DatabaseConnection databaseConnection = new DatabaseConnection();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                cities.add(resultSet.getString("cityName"));
            }
            LOGGER.log(Level.INFO, "Synchronously retrieved {0} cities " 
                    + "from the database repository server.", cities.size());

        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "SQL error executing query " 
                    + "within server connection stream context.", exception);
            throw new DatabaseSystemException("No se pudieron recuperar las " 
                    + "ciudades desde el servidor de base de datos.", exception);
        }
        return cities;
    }
}