package mx.uv.lis.professionalpracticesystem.logic.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PATH_LOGS_DIRECTORY;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PATH_LOGS_FILE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.LOG_FILE_SIZE_LIMIT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.LOG_FILE_COUNT;

/**
 *
 * @author andre
 * @author cinth
 */
public final class LoggingConfigurator {

    private LoggingConfigurator() {
    }

    public static void initializeLogger() {
        try {
            File logDirectory = new File(PATH_LOGS_DIRECTORY);
            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
            }

            LogManager.getLogManager().reset();

            FileHandler fileHandler = new FileHandler(
                    PATH_LOGS_FILE,
                    LOG_FILE_SIZE_LIMIT,
                    LOG_FILE_COUNT,
                    true
            );
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new SimpleFormatter());

            Logger rootLogger = Logger.getLogger("");
            rootLogger.setLevel(Level.INFO);
            rootLogger.addHandler(fileHandler);
            rootLogger.addHandler(consoleHandler);

        } catch (IOException | SecurityException exception) {
            System.err.println("Critical error initializing global file logging infrastructure: " + exception.getMessage());
        }
    }
}
