package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.generator;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import java.io.File;
import java.io.IOException;
import mx.uv.lis.professionalpracticesystem.logic.interfaces.IReportGenerator;

/**
 * 
 * @author cinth
 * @author andre
 */
public abstract class BaseReportPDFGenerator implements IReportGenerator {

    public BaseReportPDFGenerator() {
    }

    protected void ensureDirectoryExists(String fullPath) {
        File file = new File(fullPath);
        File parentDirectory = file.getParentFile();
        
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }
    }

    protected void generatePdf(String fullPath, 
            DocumentContentConfigurator configurator) throws IOException {
        this.ensureDirectoryExists(fullPath);

        try (PdfWriter pdfWriter = new PdfWriter(fullPath); 
                PdfDocument pdfDocument = new PdfDocument(pdfWriter); 
                Document document = new Document(pdfDocument)) {

            configurator.populateDocumentContent(document);
        }
    }

    public interface DocumentContentConfigurator {

        void populateDocumentContent(Document document) throws IOException;
    }
}