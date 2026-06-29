package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.generator;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityRowDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.PartialReportDTO;

public class FinalReportPDFGenerator {

    private static final Logger LOGGER = Logger.getLogger(FinalReportPDFGenerator.class.getName());
    private static final float BODY_FONT_SIZE = 8.0f;
    private static final float HEADER_FONT_SIZE = 8.5f;

    public void generateFinalReport(PartialReportDTO reportData, String destinationDirectory) throws Exception {
        String fileName = "Reporte_Final_Practicas.pdf";
        File file = new File(destinationDirectory + fileName);
        
        try (PdfWriter writer = new PdfWriter(file);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {
            
            float[] headerWidths = {15f, 70f, 15f};
            Table headerImagesTable = new Table(UnitValue.createPercentArray(headerWidths)).useAllAvailableWidth();
            headerImagesTable.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);

            try {
                InputStream logoUvStream = getClass().getResourceAsStream("/images/logo_uv.png");
                InputStream logoLisStream = getClass().getResourceAsStream("/images/logo_lis.png");

                if (logoUvStream != null && logoLisStream != null) {
                    Image imgUv = new Image(ImageDataFactory.create(logoUvStream.readAllBytes())).scaleToFit(55f, 55f);
                    Image imgLis = new Image(ImageDataFactory.create(logoLisStream.readAllBytes())).scaleToFit(55f, 55f);

                    headerImagesTable.addCell(new Cell().add(imgUv).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
                    
                    Cell textCell = new Cell();
                    textCell.add(new Paragraph("UNIVERSIDAD VERACRUZANA").setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(10f));
                    textCell.add(new Paragraph("FACULTAD DE ESTADÍSTICA E INFORMÁTICA").setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(9f));
                    textCell.add(new Paragraph("Licenciatura en Ingeniería de Software").setTextAlignment(TextAlignment.CENTER).setFontSize(8.5f));
                    textCell.add(new Paragraph("Formato: INFORME FINAL EE Prácticas de Ingeniería Software").setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(9f));
                    textCell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
                    headerImagesTable.addCell(textCell);

                    headerImagesTable.addCell(new Cell().add(imgLis).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
                } else {
                    headerImagesTable.addCell(new Cell(1, 3).add(new Paragraph("FACULTAD DE ESTADÍSTICA E INFORMÁTICA - INFORME FINAL")).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
                }
            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, "Fallo técnico al recuperar los recursos binarios de los logos", exception);
                headerImagesTable.addCell(new Cell(1, 3).add(new Paragraph("FACULTAD DE ESTADÍSTICA E INFORMÁTICA - INFORME FINAL")).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            }
            
            document.add(headerImagesTable);
            document.add(new Paragraph("\n"));

            float[] metadataWidths = {20f, 30f, 20f, 30f};
            Table metadataTable = new Table(UnitValue.createPercentArray(metadataWidths)).useAllAvailableWidth();
            
            metadataTable.addCell(new Cell(1, 4).add(new Paragraph("Datos Generales de la EE").setBold().setFontSize(HEADER_FONT_SIZE)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            metadataTable.addCell(new Cell().add(new Paragraph("Carrera:").setBold().setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph("Ingeniería de Software").setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph("NRC:").setBold().setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph(reportData.getNrc() != null ? reportData.getNrc() : "").setFontSize(BODY_FONT_SIZE)));
            
            metadataTable.addCell(new Cell().add(new Paragraph("Profesor:").setBold().setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph(reportData.getProfessorName() != null ? reportData.getProfessorName() : "").setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph("Período escolar:").setBold().setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph(reportData.getSchoolPeriod() != null ? reportData.getSchoolPeriod() : "").setFontSize(BODY_FONT_SIZE)));
            
            metadataTable.addCell(new Cell(1, 4).add(new Paragraph("Datos del Proyecto").setBold().setFontSize(HEADER_FONT_SIZE)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            metadataTable.addCell(new Cell().add(new Paragraph("Alumno(s):").setBold().setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph(reportData.getStudentNames() != null ? reportData.getStudentNames() : "").setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph("Organización vinculada:").setBold().setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph(reportData.getLinkedOrganizationName() != null ? reportData.getLinkedOrganizationName() : "").setFontSize(BODY_FONT_SIZE)));
            
            metadataTable.addCell(new Cell().add(new Paragraph("Proyecto:").setBold().setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph(reportData.getProjectName() != null ? reportData.getProjectName() : "").setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph("Total de horas cubiertas:").setBold().setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph("420 Horas").setFontSize(BODY_FONT_SIZE)));
            
            metadataTable.addCell(new Cell().add(new Paragraph("Fecha del reporte:").setBold().setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph(reportData.getReportDate() != null ? reportData.getReportDate() : "").setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph("Reporte:").setBold().setFontSize(BODY_FONT_SIZE)));
            metadataTable.addCell(new Cell().add(new Paragraph("FINAL").setFontSize(BODY_FONT_SIZE)));
            document.add(metadataTable);
            document.add(new Paragraph("\n"));

            Table objectiveTable = new Table(1).useAllAvailableWidth();
            objectiveTable.addCell(new Cell().add(new Paragraph("Objetivo(s) general del proyecto").setBold().setFontSize(HEADER_FONT_SIZE)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            objectiveTable.addCell(new Cell().add(new Paragraph(reportData.getGeneralObjectives() != null && !reportData.getGeneralObjectives().isEmpty() ? reportData.getGeneralObjectives() : " ")).setFontSize(BODY_FONT_SIZE).setPadding(5f));
            document.add(objectiveTable);
            document.add(new Paragraph("\n"));

            Table methodologyTable = new Table(1).useAllAvailableWidth();
            methodologyTable.addCell(new Cell().add(new Paragraph("Metodología aplicada").setBold().setFontSize(HEADER_FONT_SIZE)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            methodologyTable.addCell(new Cell().add(new Paragraph(reportData.getMethodology() != null && !reportData.getMethodology().isEmpty() ? reportData.getMethodology() : " ")).setFontSize(BODY_FONT_SIZE).setPadding(5f));
            document.add(methodologyTable);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Avance de actividades realizadas en relación al plan de trabajo").setBold().setFontSize(HEADER_FONT_SIZE));
            float[] tableWidths = {55f, 15f, 30f};
            Table activitiesTable = new Table(UnitValue.createPercentArray(tableWidths)).useAllAvailableWidth();
            
            activitiesTable.addHeaderCell(new Cell().add(new Paragraph("Actividad programada").setBold().setFontSize(HEADER_FONT_SIZE)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            activitiesTable.addHeaderCell(new Cell().add(new Paragraph("% Avance").setBold().setFontSize(HEADER_FONT_SIZE).setTextAlignment(TextAlignment.CENTER)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            activitiesTable.addHeaderCell(new Cell().add(new Paragraph("Observaciones").setBold().setFontSize(HEADER_FONT_SIZE)).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            if (reportData.getActivitiesList() != null) {
                for (ActivityRowDTO activity : reportData.getActivitiesList()) {
                    String adv = (activity.getAdvance() != null && !activity.getAdvance().trim().isEmpty()) ? activity.getAdvance() : "0%";
                    String obs = (activity.getObservations() != null) ? activity.getObservations() : "";
                    activitiesTable.addCell(new Cell().add(new Paragraph(activity.getDescription())).setFontSize(BODY_FONT_SIZE));
                    activitiesTable.addCell(new Cell().add(new Paragraph(adv)).setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
                    activitiesTable.addCell(new Cell().add(new Paragraph(obs)).setFontSize(BODY_FONT_SIZE));
                }
            }
            document.add(activitiesTable);
            document.add(new Paragraph("NOTA: En caso de que alguna actividad programada no hubiera sido cubierta con un 100% de avance, indicar las razones y acuerdos en la columna de observaciones.").setFontSize(6.5f).setItalic());
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Resultados en término de productos comprometidos").setBold().setFontSize(HEADER_FONT_SIZE));
            Table deliverablesTable = new Table(UnitValue.createPercentArray(tableWidths)).useAllAvailableWidth();
            
            deliverablesTable.addHeaderCell(new Cell().add(new Paragraph("Resultado entregable").setBold().setFontSize(HEADER_FONT_SIZE)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            deliverablesTable.addHeaderCell(new Cell().add(new Paragraph("% Avance").setBold().setFontSize(HEADER_FONT_SIZE).setTextAlignment(TextAlignment.CENTER)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            deliverablesTable.addHeaderCell(new Cell().add(new Paragraph("Observaciones").setBold().setFontSize(HEADER_FONT_SIZE)).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            if (reportData.getDeliverablesList() != null) {
                for (ActivityRowDTO deliverable : reportData.getDeliverablesList()) {
                    String adv = (deliverable.getAdvance() != null && !deliverable.getAdvance().trim().isEmpty()) ? deliverable.getAdvance() : "0%";
                    String obs = (deliverable.getObservations() != null) ? deliverable.getObservations() : "";
                    deliverablesTable.addCell(new Cell().add(new Paragraph(deliverable.getDescription())).setFontSize(BODY_FONT_SIZE));
                    deliverablesTable.addCell(new Cell().add(new Paragraph(adv)).setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
                    deliverablesTable.addCell(new Cell().add(new Paragraph(obs)).setFontSize(BODY_FONT_SIZE));
                }
            }
            document.add(deliverablesTable);
            document.add(new Paragraph("NOTA: En caso de que algún producto comprometido no hubiera sido cubierto al 100%, indicar las razones y acuerdos en la columna de observaciones.").setFontSize(6.5f).setItalic());
            document.add(new Paragraph("\n"));

            Table generalObservationsTable = new Table(1).useAllAvailableWidth();
            generalObservationsTable.addCell(new Cell().add(new Paragraph("Observaciones Generales").setBold().setFontSize(HEADER_FONT_SIZE)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            generalObservationsTable.addCell(new Cell().add(new Paragraph(reportData.getObservations() != null ? reportData.getObservations() : " ")).setFontSize(BODY_FONT_SIZE).setPadding(6f));
            document.add(generalObservationsTable);
            
            document.add(new Paragraph("\nNOTA: El presente documento debe entregarse anexando:\n• Formato de autoevaluación del alumno.\n• Formato de evaluación por parte de la Organización Vinculada, debidamente firmado y sellado por el Responsable Técnico designado.").setFontSize(7f).setItalic());

            document.add(new com.itextpdf.layout.element.AreaBreak(com.itextpdf.layout.properties.AreaBreakType.NEXT_PAGE));
            
            document.add(new Paragraph("FACULTAD DE ESTADÍSTICA E INFORMÁTICA").setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(10f));
            document.add(new Paragraph("Formato: INFORME FINAL EE Prácticas de Ingeniería Software\n\n").setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(9f));

            float[] signBoxWidths = {50f, 50f};
            Table signGridTable = new Table(UnitValue.createPercentArray(signBoxWidths)).useAllAvailableWidth();
            
            signGridTable.addCell(new Cell().add(new Paragraph("\n\n\n\n\n")).setFontSize(BODY_FONT_SIZE));
            signGridTable.addCell(new Cell().add(new Paragraph("Vo. Bo.\n\nNombre, Puesto y Firma del Responsable Técnico designado por la organización vinculada\n\n\n")).setFontSize(BODY_FONT_SIZE).setTextAlignment(TextAlignment.CENTER));
            
            signGridTable.addCell(new Cell().add(new Paragraph("Nombre(s) y Firma(s) de los Estudiantes miembros del equipo (si aplica)").setBold().setFontSize(BODY_FONT_SIZE).setTextAlignment(TextAlignment.CENTER)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            signGridTable.addCell(new Cell().add(new Paragraph("Sello de la organización vinculada").setBold().setFontSize(BODY_FONT_SIZE).setTextAlignment(TextAlignment.CENTER)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            
            document.add(signGridTable);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Fallo crítico compilando iText 7 del Reporte Final", ex);
            throw ex;
        }
    }
}