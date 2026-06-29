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
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.INITIAL_COUNTER_INDEX;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PATH_LOGO_UV;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PATH_LOGO_LIS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PDF_LOGO_SCALE_SIZE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PDF_WEEKS_PER_MONTH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PDF_EVALUATION_MAX_SCORE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_HEADER_IMAGES;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_GENERAL_INFO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_STUDENT_INFO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_ACTIVITY_GRID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_CRITERIA_GRID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_EVALUATION_GRID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_SIGNATURES_GRID;

/**
 * 
 * @author cinth
 * @author andre
 */
public class MonthlyReportPDFGenerator extends BaseReportPDFGenerator {
    private static final Logger LOGGER = Logger.getLogger(MonthlyReportPDFGenerator.class.getName());

    @Override
    public void generateReport(Object reportData, String destinationDirectory) throws IOException {
        PartialReportDTO report = (PartialReportDTO) reportData;
        String fileName = "Reporte_Mensual_" + report.getReportNumber() + ".pdf";
        String fullPath = destinationDirectory + fileName;
        
        this.generatePdf(fullPath, new MonthlyReportContentCreator(report));
    }

    private static class MonthlyReportContentCreator implements DocumentContentConfigurator {
        private final PartialReportDTO report;

        public MonthlyReportContentCreator(PartialReportDTO report) {
            this.report = report;
        }

        @Override
        public void populateDocumentContent(Document document) throws IOException {
            Table headerImagesTable = new Table(UnitValue.createPercentArray(WIDTHS_PERCENT_HEADER_IMAGES)).useAllAvailableWidth();
            try {
                InputStream logoUvStream = getClass().getResourceAsStream(PATH_LOGO_UV);
                InputStream logoLisStream = getClass().getResourceAsStream(PATH_LOGO_LIS);
                
                if (logoUvStream != null && logoLisStream != null) {
                    Image imageUv = new Image(ImageDataFactory.create(
                            logoUvStream.readAllBytes()))
                            .scaleToFit(PDF_LOGO_SCALE_SIZE, PDF_LOGO_SCALE_SIZE);
                    Image imageLis = new Image(ImageDataFactory.create(
                            logoLisStream.readAllBytes()))
                            .scaleToFit(PDF_LOGO_SCALE_SIZE, PDF_LOGO_SCALE_SIZE);
                    
                    headerImagesTable.addCell(new Cell().add(imageUv)
                            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.LEFT));
                    
                    Paragraph titleHeader = new Paragraph("FACULTAD DE ESTADÍSTICA E " 
                            + "INFORMÁTICA\nLicenciatura en Ingeniería de Software\n" 
                            + "Formato: INFORME MENSUAL EE Prácticas de Ingeniería Software")
                            .setBold().setFontSize(11f).setTextAlignment(TextAlignment.CENTER);
                    headerImagesTable.addCell(new Cell().add(titleHeader)
                            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.CENTER));
                    
                    headerImagesTable.addCell(new Cell().add(imageLis)
                            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.RIGHT));
                } else {
                    headerImagesTable.addCell(new Cell(1, 3).add(new Paragraph(
                            "FACULTAD DE ESTADÍSTICA E INFORMÁTICA - INFORME MENSUAL"))
                            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
                }
            } catch (IOException exception) {
                LOGGER.log(Level.SEVERE, "Failed to recover logo binary resource streams context", exception);
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
            this.addCellPair(generalInfoTable, "Número del informe:", this.report.getReportNumber());
            document.add(generalInfoTable);

            document.add(new Paragraph(EMPTY_STRING));
            Table infoTable = new Table(UnitValue.createPercentArray(WIDTHS_PERCENT_STUDENT_INFO)).useAllAvailableWidth();
            this.addCellPair(infoTable, "Objetivo(s) general del proyecto", this.report.getGeneralObjectives());
            this.addCellPair(infoTable, "Metodología", this.report.getMethodology());
            document.add(infoTable);

            document.add(new Paragraph("Avance de actividades realizadas en relación al plan de trabajo")
                    .setBold().setFontSize(BODY_FONT_SIZE));
            
            Table activityTable = new Table(UnitValue.createPercentArray(WIDTHS_PERCENT_ACTIVITY_GRID)).useAllAvailableWidth();
            this.buildActivityHeaders(activityTable);

            if (this.report.getActivitiesList() != null) {
                for (ActivityRowDTO activity : this.report.getActivitiesList()) {
                    activityTable.addCell(new Cell(2, 1).add(new Paragraph(activity.getDescription()))
                            .setFontSize(BODY_FONT_SIZE));
                    activityTable.addCell(new Cell().add(new Paragraph("Plan")).setFontSize(BODY_FONT_SIZE));
                    
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekOne() ? "X" : " "))
                            .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekTwo() ? "X" : " "))
                            .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekThree() ? "X" : " "))
                            .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isPlanWeekFour() ? "X" : " "))
                            .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
                    
                    activityTable.addCell(new Cell().add(new Paragraph("Real"))
                            .setFontSize(BODY_FONT_SIZE));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekOne() ? "X" : " "))
                            .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekTwo() ? "X" : " "))
                            .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekThree() ? "X" : " "))
                            .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
                    activityTable.addCell(new Cell().add(new Paragraph(activity.isRealWeekFour() ? "X" : " "))
                            .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
                }
            }
            document.add(activityTable);

            document.add(new Paragraph(EMPTY_STRING));
            Table resultsTable = new Table(UnitValue.createPercentArray(WIDTHS_PERCENT_STUDENT_INFO))
                    .useAllAvailableWidth();
            this.addCellPair(resultsTable, "Resultados obtenidos al momento", this.report.getCurrentResults());
            document.add(resultsTable);

            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(headerImagesTable);
            document.add(new Paragraph(EMPTY_STRING));

            Table obsTable = new Table(UnitValue.createPercentArray(WIDTHS_PERCENT_STUDENT_INFO)).useAllAvailableWidth();
            this.addCellPair(obsTable, "Observaciones", this.report.getObservations());
            document.add(obsTable);

            document.add(new Paragraph(EMPTY_STRING));
            document.add(new Paragraph("NOTA: El presente documento debe entregarse anexando:")
                    .setBold().setFontSize(BODY_FONT_SIZE));
            document.add(new Paragraph("•  Bitácora de trabajo.").setFontSize(BODY_FONT_SIZE));
            document.add(new Paragraph("•  Formato de evaluación de desempeño del o los estudiantes, " 
                    + "debidamente firmado y sellado por el Responsable Técnico designado por " 
                    + "la organización vinculada.").setFontSize(BODY_FONT_SIZE));

            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(headerImagesTable);
            document.add(new Paragraph(EMPTY_STRING));

            Table rtHeaderTable = new Table(UnitValue.createPercentArray(new float[]{100f})).useAllAvailableWidth();
            rtHeaderTable.addCell(new Cell().add(new Paragraph("Sección EXCLUSIVA para llenado por parte " 
                    + "del Responsable Técnico designado por la Organización Vinculada"))
                    .setBold().setFontSize(BODY_FONT_SIZE).setTextAlignment(TextAlignment.CENTER).setPadding(6f));
            document.add(rtHeaderTable);
            document.add(new Paragraph(EMPTY_STRING));

            Table criteriaTable = new Table(UnitValue.createPercentArray(
                    WIDTHS_PERCENT_CRITERIA_GRID)).useAllAvailableWidth();
            criteriaTable.addCell(new Cell().add(new Paragraph("CRITERIOS"))
                    .setBold().setTextAlignment(TextAlignment.CENTER));
            criteriaTable.addCell(new Cell().add(new Paragraph("PUNTUACIÓN"))
                    .setBold().setTextAlignment(TextAlignment.CENTER));
            this.addCriteriaRow(criteriaTable, "Nunca", "1");
            this.addCriteriaRow(criteriaTable, "Pocas veces", "2");
            this.addCriteriaRow(criteriaTable, "Algunas veces", "3");
            this.addCriteriaRow(criteriaTable, "Muchas veces", "4");
            this.addCriteriaRow(criteriaTable, "Siempre", "5");
            document.add(criteriaTable);
            document.add(new Paragraph(EMPTY_STRING));

            Table evaluationGrid = new Table(UnitValue.createPercentArray(
                    WIDTHS_PERCENT_EVALUATION_GRID)).useAllAvailableWidth();
            evaluationGrid.addHeaderCell(new Cell(1, 2).add(new Paragraph(
                    "FORMATO DE EVALUACIÓN DE DESEMPEÑO")).setBold());
            
            for (int scoreHeaderIndex = 1; scoreHeaderIndex <= PDF_EVALUATION_MAX_SCORE; scoreHeaderIndex++) {
                evaluationGrid.addHeaderCell(new Cell().add(new Paragraph(
                        String.valueOf(scoreHeaderIndex))).setBold().setTextAlignment(TextAlignment.CENTER));
            }
            this.addEvaluationItem(evaluationGrid, "1", "Responsabilidad en las actividades asignadas.");
            this.addEvaluationItem(evaluationGrid, "2", "Aporte de ideas para la toma de decisiones en la solución.");
            this.addEvaluationItem(evaluationGrid, "3", "Organización en el desarrollo del trabajo.");
            this.addEvaluationItem(evaluationGrid, "4", "Aplicación de conocimientos teórico-prácticos en el desarrollo de sus actividades.");
            this.addEvaluationItem(evaluationGrid, "5", "Realizó las actividades encomendadas correctamente.");
            document.add(evaluationGrid);
            document.add(new Paragraph(EMPTY_STRING));

            Table signaturesTable = new Table(UnitValue.createPercentArray(
                    WIDTHS_PERCENT_SIGNATURES_GRID)).useAllAvailableWidth();
            signaturesTable.addCell(new Cell().add(new Paragraph("\n\n\n\n")).setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("\n\n\n\n")).setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("Nombre(s) y Firma(s) de los Estudiantes\n" 
                    + "miembros del equipo (si aplica)")).setBold().setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("Nombre, Puesto y Firma del Responsable\nTécnico " 
                    + "designado por la organización vinculada")).setBold().setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("\n\n\n\n")).setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("\n\n\n\n")).setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("Vo. Bo.\n\n___________________________________\n" 
                    + "Nombre y Firma del Profesor de la EE")).setBold().setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(BODY_FONT_SIZE));
            signaturesTable.addCell(new Cell().add(new Paragraph("Sello de la organización vinculada"))
                    .setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
            document.add(signaturesTable);
        }

        private void addCellPair(Table table, String label, String value) {
            table.addCell(new Cell().add(new Paragraph(label)).setBold().setFontSize(BODY_FONT_SIZE));
            table.addCell(new Cell().add(new Paragraph(value == null ? EMPTY_STRING : value)).setFontSize(BODY_FONT_SIZE));
        }

        private void addCriteriaRow(Table table, String text, String score) {
            table.addCell(new Cell().add(new Paragraph(text)).setFontSize(BODY_FONT_SIZE));
            table.addCell(new Cell().add(new Paragraph(score)).setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(BODY_FONT_SIZE));
        }

        private void addEvaluationItem(Table table, String num, String description) {
            table.addCell(new Cell().add(new Paragraph(num)).setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(BODY_FONT_SIZE));
            table.addCell(new Cell().add(new Paragraph(description)).setFontSize(BODY_FONT_SIZE));
            
            for (int blankCellIndex = INITIAL_COUNTER_INDEX; blankCellIndex < PDF_EVALUATION_MAX_SCORE; blankCellIndex++) {
                table.addCell(new Cell().add(new Paragraph(" ")));
            }
        }

        private void buildActivityHeaders(Table table) {
            table.addHeaderCell(new Cell(2, 1).add(new Paragraph("Actividades")).setBold().setFontSize(BODY_FONT_SIZE));
            table.addHeaderCell(new Cell(2, 1).add(new Paragraph("Tiempo")).setBold().setFontSize(BODY_FONT_SIZE));
            
            table.addHeaderCell(new Cell(1, 4).add(new Paragraph("Semanas de Cobertura del Mes"))
                    .setBold().setFontSize(BODY_FONT_SIZE).setTextAlignment(TextAlignment.CENTER));
            
            for (int weekIndex = 1; weekIndex <= PDF_WEEKS_PER_MONTH; weekIndex++) {
                table.addHeaderCell(new Cell().add(new Paragraph("S" + weekIndex))
                        .setFontSize(BODY_FONT_SIZE).setTextAlignment(TextAlignment.CENTER));
            }
        }
    }
}