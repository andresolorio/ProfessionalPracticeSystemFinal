package mx.uv.lis.professionalpracticesystem.logic.interfaces;

import java.io.IOException;

/**
 * Defines the contract for generating system-specific report documents.
 * @author cinth
 * @author andre
 */
public interface IReportGenerator {

    /**
     * Generates a report file based on the provided data context and saves 
     * it to the specified directory.
     * * @param reportData the data object (DTO) containing report content
     * @param destinationDirectory the file system path where the report 
     * will be persisted
     * @throws IOException if a file system write error or binary output 
     * stream failure occurs
     */
    void generateReport(Object reportData, String destinationDirectory) throws IOException;
}