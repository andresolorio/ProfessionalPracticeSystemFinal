package mx.uv.lis.professionalpracticesystem.graphicaluserinterface.generator;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityRowDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.PartialReportDTO;

import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.BODY_FONT_SIZE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_STRING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PATH_LOGO_UV;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PATH_LOGO_LIS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PDF_LOGO_SCALE_SIZE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_HEADER_IMAGES;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_GENERAL_INFO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_STUDENT_INFO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_SIGNATURES_GRID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PDF_WEEKS_PER_PARTIAL_REPORT;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_PARTIAL_ACTIVITY_GRID;

/**
 * @author cinth
 * @author andre
 */
public class PartialReportPDFGenerator extends BaseReportPDFGenerator {
    private static final Logger LOGGER = Logger.getLogger(PartialReportPDFGenerator.class.getName());

    @Override
    public void generateReport(Object reportData, String destinationDirectory) throws IOException {
        PartialReportDTO report = (PartialReportDTO) reportData;
        String fileName = "Reporte_Parcial.pdf";
        String fullPath = destinationDirectory + fileName;
        this.generatePdf(fullPath, new PartialReportContentCreator(report));
    }

    private static class PartialReportContentCreator implements DocumentContentConfigurator {
        private final PartialReportDTO report;

        public PartialReportContentCreator(PartialReportDTO report) {
            this.report = report;
        }

        @Override
        public void populateDocumentContent(Document document) throws IOException {
            Table headerImagesTable = new Table(UnitValue.createPercentArray(
                    WIDTHS_PERCENT_HEADER_IMAGES)).useAllAvailableWidth();
            try {
                InputStream logoUvStream = getClass().getResourceAsStream(PATH_LOGO_UV);
                InputStream logoLisStream = getClass().getResourceAsStream(PATH_LOGO_LIS);
                
                if (logoUvStream != null && logoLisStream != null) {
                    Image imgUv = new Image(ImageDataFactory.create(
                            logoUvStream.readAllBytes()))
                            .scaleToFit(PDF_LOGO_SCALE_SIZE, PDF_LOGO_SCALE_SIZE);
                    Image imgLis = new Image(ImageDataFactory.create(
                            logoLisStream.readAllBytes()))
                            .scaleToFit(PDF_LOGO_SCALE_SIZE, PDF_LOGO_SCALE_SIZE);
                    
                    headerImagesTable.addCell(new Cell().add(imgUv)
                            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.LEFT));
                    
                    Paragraph titleHeader = new Paragraph("FACULTAD DE ESTADÍSTICA E " 
                            + "INFORMÁTICA\nLicenciatura en Ingeniería de Software\n" 
                            + "Formato: INFORME PARCIAL EE Prácticas de Ingeniería Software")
                            .setBold().setFontSize(11f).setTextAlignment(TextAlignment.CENTER);
                    headerImagesTable.addCell(new Cell().add(titleHeader)
                            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.CENTER));
                    
                    headerImagesTable.addCell(new Cell().add(imgLis)
                            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.RIGHT));
                } else {
                    headerImagesTable.addCell(new Cell(1, 3).add(new Paragraph(
                            "FACULTAD DE ESTADÍSTICA E INFORMÁTICA - INFORME PARCIAL"))
                            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
                }
            } catch (IOException exception) {
                LOGGER.log(Level.SEVERE, "Failed to recover partial logos asset context binary data", exception);
            }
            document.add(headerImagesTable);
            document.add(new Paragraph(EMPTY_STRING));

            Table generalInfoTable = new Table(UnitValue.createPercentArray(WIDTHS_PERCENT_GENERAL_INFO)).useAllAvailableWidth();
            this.addCellPair(generalInfoTable, "Carrera:", "Licenciatura en Ingeniería de Software");
            this.addCellPair(generalInfoTable, "NRC:", this.report.getNrc());
            this.addCellPair(generalInfoTable, "Profesor:", this.report.getProfessorName());
            this.addCellPair(generalInfoTable, "Período escolar:", this.report.getSchoolPeriod());
            this.addCellPair(generalInfoTable, "Alumno(s):", this.report.getStudentNames());
            this.addCellPair(generalInfoTable, "Organización vinculada:", this.report.getLinkedOrganizationName());
            this.addCellPair(generalInfoTable, "Proyecto:", this.report.getProjectName());
            this.addCellPair(generalInfoTable, "Período del reporte y horas cubiertas:", this.report.getCoveragePeriodAndHours());
            this.addCellPair(generalInfoTable, "Fecha del reporte:", this.report.getReportDate());
            this.addCellPair(generalInfoTable, "Número del informe:", "Reporte Parcial");
            document.add(generalInfoTable);

            document.add(new Paragraph(EMPTY_STRING));
            Table infoTable = new Table(UnitValue.createPercentArray(
                    WIDTHS_PERCENT_STUDENT_INFO)).useAllAvailableWidth();
            this.addCellPair(infoTable, "Objetivo(s) general del proyecto", 
                    this.report.getGeneralObjectives());
            this.addCellPair(infoTable, "Metodología", this.report.getMethodology());
            document.add(infoTable);

            document.add(new Paragraph("Avance acumulado de actividades realizadas en " 
                    + "relación al plan de trabajo (Mes 1 al Mes 6)").setBold().setFontSize(BODY_FONT_SIZE));
            
            Table activityTable = new Table(UnitValue.createPercentArray(
                    WIDTHS_PERCENT_PARTIAL_ACTIVITY_GRID)).useAllAvailableWidth();
            this.buildActivityHeaders(activityTable);

            if (this.report.getActivitiesList() != null) {
                for (ActivityRowDTO activity : this.report.getActivitiesList()) {
                    activityTable.addCell(new Cell(2, 1).add(new Paragraph(
                            activity.getDescription())).setFontSize(7f));                  
                    activityTable.addCell(new Cell().add(new Paragraph("Plan"))
                            .setFontSize(6f));
                    
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekOne() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekTwo() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekThree() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekFour() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekFive() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekSix() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekSeven() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekEight() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekNine() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekTen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekEleven() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekTwelve() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekThirteen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekFourteen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekFifteen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekSixteen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekSeventeen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekEighteen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekNineteen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekTwenty() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekTwentyOne() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekTwentyTwo() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekTwentyThree() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekTwentyFour() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));

                    activityTable.addCell(new Cell().add(new Paragraph("Real")).setFontSize(6f));
                    
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekOne() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekTwo() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekThree() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekFour() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekFive() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekSix() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekSeven() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekEight() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekNine() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekTen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekEleven() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekTwelve() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekThirteen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekFourteen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekFifteen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekSixteen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekSeventeen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekEighteen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekNineteen() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekTwenty() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekTwentyOne() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekTwentyTwo() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekTwentyThree() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekTwentyFour() ? "X" : " ")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f));
                }
            }
            document.add(activityTable);

            document.add(new Paragraph(EMPTY_STRING));
            Table resultsTable = new Table(UnitValue.createPercentArray(
                    WIDTHS_PERCENT_STUDENT_INFO)).useAllAvailableWidth();
            this.addCellPair(resultsTable, "Resultados obtenidos al momento", 
                    this.report.getCurrentResults());
            document.add(resultsTable);

            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(headerImagesTable);
            document.add(new Paragraph(EMPTY_STRING));

            Table obsTable = new Table(UnitValue.createPercentArray(
                    WIDTHS_PERCENT_STUDENT_INFO)).useAllAvailableWidth();
            this.addCellPair(obsTable, "Observaciones", this.report.getObservations());
            document.add(obsTable);

            document.add(new Paragraph(EMPTY_STRING));
            document.add(new Paragraph("NOTA: El presente documento debe entregarse anexando:")
                    .setBold().setFontSize(BODY_FONT_SIZE));
            document.add(new Paragraph("•  Bitácora de trabajo acumulada.").setFontSize(BODY_FONT_SIZE));
            document.add(new Paragraph("•  Formato de evaluación intermedia de desempeño, " 
                    + "debidamente firmado y sellado por la organización vinculada.")
                    .setFontSize(BODY_FONT_SIZE));

            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(headerImagesTable);
            document.add(new Paragraph(EMPTY_STRING));

            Table rtHeaderTable = new Table(UnitValue.createPercentArray(
                    new float[]{100f})).useAllAvailableWidth();
            rtHeaderTable.addCell(new Cell().add(new Paragraph("Sección EXCLUSIVA para " 
                    + "llenado por parte del Profesor de la EE (Evaluación Parcial)"))
                    .setBold().setFontSize(BODY_FONT_SIZE).setTextAlignment(TextAlignment.CENTER).setPadding(6f));
            document.add(rtHeaderTable);
            document.add(new Paragraph(EMPTY_STRING));

            Table signaturesTable = new Table(UnitValue.createPercentArray(
                    WIDTHS_PERCENT_SIGNATURES_GRID)).useAllAvailableWidth();
            signaturesTable.addCell(new Cell().add(new Paragraph("\n\n\n\n")).setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("\n\n\n\n")).setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("Nombre y Firma del Estudiante"))
                    .setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("Nombre y Firma del Responsable Técnico\n" 
                    + "de la Organización Vinculada")).setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("\n\n\n\n")).setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("\n\n\n\n")).setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("Vo. Bo.\n\n___________________________________\n" 
                    + "Nombre y Firma del Profesor de la EE")).setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("Sello de la organización vinculada"))
                    .setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
            document.add(signaturesTable);
        }

        private void addCellPair(Table table, String label, String value) {
            table.addCell(new Cell().add(new Paragraph(label)).setBold().setFontSize(BODY_FONT_SIZE));
            table.addCell(new Cell().add(new Paragraph(value == null ? EMPTY_STRING : value)).setFontSize(BODY_FONT_SIZE));
        }

        private void buildActivityHeaders(Table table) {
            table.addHeaderCell(new Cell(2, 1).add(new Paragraph("Actividades")).setBold().setFontSize(9f));
            table.addHeaderCell(new Cell(2, 1).add(new Paragraph("Tiempo")).setBold().setFontSize(9f));
            
            table.addHeaderCell(new Cell(1, 4).add(new Paragraph("Mes 1")).setBold().setFontSize(7f).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell(1, 4).add(new Paragraph("Mes 2")).setBold().setFontSize(7f).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell(1, 4).add(new Paragraph("Mes 3")).setBold().setFontSize(7f).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell(1, 4).add(new Paragraph("Mes 4")).setBold().setFontSize(7f).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell(1, 4).add(new Paragraph("Mes 5")).setBold().setFontSize(7f).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell(1, 4).add(new Paragraph("Mes 6")).setBold().setFontSize(7f).setTextAlignment(TextAlignment.CENTER));
            
            for (int weekIndex = 1; weekIndex <= PDF_WEEKS_PER_PARTIAL_REPORT; weekIndex++) {
                table.addHeaderCell(new Cell().add(new Paragraph("S" + weekIndex))
                        .setFontSize(6f).setTextAlignment(TextAlignment.CENTER));
            }
        }
    }
}