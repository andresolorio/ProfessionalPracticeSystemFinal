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
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EvaluationCriterionDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.LinkedOrganizationDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.BODY_FONT_SIZE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_STRING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MARK_X;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_SCORE_VALUE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MIN_SCORE_VALUE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.CELL_HEADER_FONT_SIZE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PDF_SCALE_TABLE_WIDTH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PATH_LOGO_UV;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PATH_LOGO_LIS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PDF_LOGO_SCALE_SIZE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PATH_SELFEVALUATIONS_DIRECTORY;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PREFIX_SELFEVALUATION_FILE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DATE_FORMAT_LONG_SPANISH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_HEADER_IMAGES;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_STUDENT_INFO_GRID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_SCALE_GRID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.WIDTHS_PERCENT_EVALUATION_GRID_SE;

/**
 * 
 * @author cinth
 * @author andre
 */
public final class SelfEvaluationPDFGenerator {
    
    private static final Logger LOGGER = Logger.getLogger(SelfEvaluationPDFGenerator.class.getName());

    private SelfEvaluationPDFGenerator() {
    }

    public static void generatePdfFile(StudentDTO student, ProjectDTO project,
            LinkedOrganizationDTO organization, 
            List<EvaluationCriterionDTO> criteria) throws IOException {

        String destinationPath = PATH_SELFEVALUATIONS_DIRECTORY 
                + PREFIX_SELFEVALUATION_FILE 
                + student.getEnrollmentId() + ".pdf";

        File directory = new File(destinationPath).getParentFile();
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }

        int totalScore = 0;
        for (EvaluationCriterionDTO criterion : criteria) {
            totalScore += criterion.getScore();
        }

        try (PdfWriter pdfWriter = new PdfWriter(destinationPath);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                Document document = new Document(pdfDocument)) {

            Table headerImagesTable = new Table(UnitValue.createPercentArray(
                    WIDTHS_PERCENT_HEADER_IMAGES)).useAllAvailableWidth();
            headerImagesTable.setBorder(
                    com.itextpdf.layout.borders.Border.NO_BORDER);

            try {
                InputStream logoUvStream = SelfEvaluationPDFGenerator.class
                        .getResourceAsStream(PATH_LOGO_UV);
                InputStream logoLisStream = SelfEvaluationPDFGenerator.class
                        .getResourceAsStream(PATH_LOGO_LIS);

                if (logoUvStream != null && logoLisStream != null) {
                    Image imageUv = new Image(ImageDataFactory.create(
                            logoUvStream.readAllBytes()))
                            .scaleToFit(PDF_LOGO_SCALE_SIZE, PDF_LOGO_SCALE_SIZE);
                    Image imageLis = new Image(ImageDataFactory.create(
                            logoLisStream.readAllBytes()))
                            .scaleToFit(PDF_LOGO_SCALE_SIZE, PDF_LOGO_SCALE_SIZE);

                    headerImagesTable.addCell(new Cell().add(imageUv)
                            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                            .setHorizontalAlignment(HorizontalAlignment.LEFT));
                    
                    Cell textCell = new Cell();
                    textCell.add(new Paragraph("UNIVERSIDAD VERACRUZANA")
                            .setBold().setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(10f));
                    textCell.add(new Paragraph("FACULTAD DE ESTADÍSTICA E INFORMÁTICA")
                            .setBold().setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(9f));
                    textCell.add(new Paragraph("Licenciatura en Ingeniería de Software")
                            .setTextAlignment(TextAlignment.CENTER).setFontSize(8.5f));
                    textCell.add(new Paragraph("Formato: EVALUACIÓN DEL ALUMNO. " 
                            + "EE Prácticas de Ingeniería Software")
                            .setBold().setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(9f));
                    textCell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
                    headerImagesTable.addCell(textCell);

                    headerImagesTable.addCell(new Cell().add(imageLis)
                            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                            .setHorizontalAlignment(HorizontalAlignment.RIGHT));
                }
            } catch (IOException exception) { 
                LOGGER.log(Level.SEVERE, "Binary IO streams asset pipe crashed " 
                        + "while loading corporate design assets", exception);
            }
            
            document.add(headerImagesTable);
            document.add(new Paragraph("\n"));

            Table studentTable = new Table(UnitValue.createPercentArray(
                    WIDTHS_PERCENT_STUDENT_INFO_GRID)).useAllAvailableWidth();
            String fullName = getStudentFullName(student);
            
            studentTable.addCell(new Cell().add(new Paragraph("Nombre del alumno:"))
                    .setBold().setFontSize(BODY_FONT_SIZE)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            studentTable.addCell(new Cell().add(new Paragraph(fullName))
                    .setFontSize(BODY_FONT_SIZE));
            
            studentTable.addCell(new Cell().add(new Paragraph("Matrícula:"))
                    .setBold().setFontSize(BODY_FONT_SIZE)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            studentTable.addCell(new Cell().add(new Paragraph(
                    safeString(student.getEnrollmentId()))).setFontSize(BODY_FONT_SIZE));
            
            studentTable.addCell(new Cell().add(new Paragraph("Organización vinculada:"))
                    .setBold().setFontSize(BODY_FONT_SIZE)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            
            String orgName = "N/A";
            if (organization != null) {
                orgName = safeString(organization.getLinkedOrganizationName());
            }
            studentTable.addCell(new Cell().add(new Paragraph(orgName))
                    .setFontSize(BODY_FONT_SIZE));

            studentTable.addCell(new Cell().add(new Paragraph("Departamento:"))
                    .setBold().setFontSize(BODY_FONT_SIZE)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            
            String sectorName = "N/A";
            if (organization != null) {
                sectorName = safeString(organization.getSector());
            }
            studentTable.addCell(new Cell().add(new Paragraph(sectorName))
                    .setFontSize(BODY_FONT_SIZE));

            studentTable.addCell(new Cell().add(new Paragraph("Responsable del proyecto:"))
                    .setBold().setFontSize(BODY_FONT_SIZE)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            
            String profName = "N/A";
            if (student.getProfessorName() != null) {
                profName = safeString(student.getProfessorName());
            }
            studentTable.addCell(new Cell().add(new Paragraph(profName))
                    .setFontSize(BODY_FONT_SIZE));
               
            studentTable.addCell(new Cell().add(new Paragraph("Nombre del proyecto:"))
                    .setBold().setFontSize(BODY_FONT_SIZE)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            studentTable.addCell(new Cell().add(new Paragraph(
                    safeString(project.getProjectName()))).setFontSize(BODY_FONT_SIZE));
 
            document.add(studentTable);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("INSTRUCCIONES: Responde a cada una de las " 
                    + "afirmaciones presentadas, marcando con una \"X\" la casilla " 
                    + "correspondiente de acuerdo a los siguientes criterios:")
                    .setBold().setFontSize(BODY_FONT_SIZE));
            document.add(new Paragraph("\n"));

            Table scaleTable = new Table(UnitValue.createPercentArray(
                    WIDTHS_PERCENT_SCALE_GRID))
                    .setWidth(PDF_SCALE_TABLE_WIDTH)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);
            
            scaleTable.addHeaderCell(new Cell().add(new Paragraph("CRITERIOS"))
                    .setBold().setFontSize(CELL_HEADER_FONT_SIZE)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            scaleTable.addHeaderCell(new Cell().add(new Paragraph("PUNTOS"))
                    .setBold().setFontSize(CELL_HEADER_FONT_SIZE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            
            scaleTable.addCell(new Cell().add(new Paragraph("Totalmente en desacuerdo"))
                    .setFontSize(BODY_FONT_SIZE)); 
            scaleTable.addCell(new Cell().add(new Paragraph("1"))
                    .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
            scaleTable.addCell(new Cell().add(new Paragraph("En desacuerdo"))
                    .setFontSize(BODY_FONT_SIZE)); 
            scaleTable.addCell(new Cell().add(new Paragraph("2"))
                    .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
            scaleTable.addCell(new Cell().add(new Paragraph("Indeciso"))
                    .setFontSize(BODY_FONT_SIZE)); 
            scaleTable.addCell(new Cell().add(new Paragraph("3"))
                    .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
            scaleTable.addCell(new Cell().add(new Paragraph("De acuerdo"))
                    .setFontSize(BODY_FONT_SIZE)); 
            scaleTable.addCell(new Cell().add(new Paragraph("4"))
                    .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
            scaleTable.addCell(new Cell().add(new Paragraph("Totalmente de acuerdo"))
                    .setFontSize(BODY_FONT_SIZE)); 
            scaleTable.addCell(new Cell().add(new Paragraph("5"))
                    .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE));
            
            document.add(scaleTable);
            document.add(new Paragraph("\n"));

            Table mainEvaluationTable = new Table(UnitValue.createPercentArray(
                    WIDTHS_PERCENT_EVALUATION_GRID_SE)).useAllAvailableWidth();
            
            mainEvaluationTable.addHeaderCell(new Cell().add(new Paragraph("AFIRMACIONES"))
                    .setBold().setFontSize(CELL_HEADER_FONT_SIZE)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            
            for (int scoreColumn = MIN_SCORE_VALUE; scoreColumn <= MAX_SCORE_VALUE; 
                    scoreColumn++) {
                mainEvaluationTable.addHeaderCell(new Cell().add(
                        new Paragraph(String.valueOf(scoreColumn)))
                        .setBold().setFontSize(CELL_HEADER_FONT_SIZE)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            int criterionIndex = 1;
            for (EvaluationCriterionDTO criterion : criteria) {
                mainEvaluationTable.addCell(new Cell().add(new Paragraph(
                        criterionIndex + " " + safeString(criterion.getStatement())))
                        .setFontSize(BODY_FONT_SIZE));
                
                for (int currentScoreOption = MIN_SCORE_VALUE; 
                        currentScoreOption <= MAX_SCORE_VALUE; currentScoreOption++) {
                    String cellContent = EMPTY_STRING;
                    if (criterion.getScore() == currentScoreOption) {
                        cellContent = MARK_X;
                    }
                    mainEvaluationTable.addCell(new Cell().add(new Paragraph(cellContent))
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(BODY_FONT_SIZE));
                }
                criterionIndex++;
            }

            mainEvaluationTable.addCell(new Cell().add(new Paragraph("PUNTUACIÓN FINAL"))
                    .setBold().setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(BODY_FONT_SIZE)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            
            Cell totalScoreCell = new Cell(1, 5).add(
                    new Paragraph(String.valueOf(totalScore)))
                    .setBold().setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(BODY_FONT_SIZE);
            mainEvaluationTable.addCell(totalScoreCell);

            document.add(mainEvaluationTable);
            document.add(new Paragraph("\n"));

            String currentDate = LocalDate.now().format(
                    DateTimeFormatter.ofPattern(DATE_FORMAT_LONG_SPANISH));
            document.add(new Paragraph("LUGAR Y FECHA: Xalapa, Ver., a " + currentDate)
                    .setFontSize(BODY_FONT_SIZE));
            document.add(new Paragraph("\n\n\n\n"));

            Paragraph signatureLine = new Paragraph("___________________________________\n" 
                    + fullName.toUpperCase() + "\nNOMBRE Y FIRMA DEL ALUMNO")
                    .setTextAlignment(TextAlignment.CENTER).setFontSize(BODY_FONT_SIZE);
            document.add(signatureLine);
        }
    }

    private static String safeString(String text) {
        return (text == null) ? EMPTY_STRING : text;
    }

    private static String getStudentFullName(StudentDTO student) {
        String fullName = EMPTY_STRING;
        if (student != null) {
            fullName = (safeString(student.getFirstName()) + " "
                    + safeString(student.getPaternalLastName()) + " "
                    + safeString(student.getMaternalLastName())).trim();
        }
        return fullName;
    }
}