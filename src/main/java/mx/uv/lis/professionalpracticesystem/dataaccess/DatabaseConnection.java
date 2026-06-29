package mx.uv.lis.professionalpracticesystem.dataaccess;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author cinth
 * @author andre
 */

public class DatabaseConnection {
    private String url;
    private String userDatabase;
    private String passwordDatabase;
    private static Connection connection;
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    public DatabaseConnection() throws IllegalStateException {
        loadProperties();
    }

    private void loadProperties() throws IllegalStateException {
        Properties properties = new Properties();
        try (InputStream inputStream = 
                getClass().getClassLoader().getResourceAsStream("configdb.properties")) {
            
            if (inputStream != null) {
                properties.load(inputStream);
                this.url = properties.getProperty("database.url");
                this.userDatabase = properties.getProperty("database.user");
                this.passwordDatabase = properties.getProperty("database.password");
            } else {
                LOGGER.severe("Error: The file configdb.properties could not be found.");
                throw new IllegalStateException("Configuration file not found.");
            }
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Error loading database configuration properties", exception);
            throw new IllegalStateException("Error loading properties", exception);
        }
    }
    
    
    public Connection getConnection() throws SQLException {
        if (url == null || userDatabase == null || passwordDatabase == null) {
            LOGGER.log(Level.SEVERE, "Database connection link state " 
                    + "failure: Configuration properties fields are null.");
            throw new SQLException("Connection credentials not loaded.");
        }
        return DriverManager.getConnection(url, userDatabase, passwordDatabase);
    }
}