package mx.uv.lis.professionalpracticesystem.logic.utils;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStream;
import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.DocumentRowDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOCUMENT_REJECTED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOCUMENT_VALIDATED;

/**
 * 
 * @author cinth
 * @author andre
 */
public class EmailManager {

    private static final Logger LOGGER = Logger.getLogger(EmailManager.class.getName());
    private static String SENDER_EMAIL;
    private static String SENDER_PASSWORD;

    private static class EmailAuthenticator extends Authenticator {

        private final String username;
        private final String password;

        public EmailAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }

    static {
        Properties config = new Properties();
        try (InputStream is = EmailManager.class.getClassLoader().getResourceAsStream("configdb.properties")) {
            if (is != null) {
                config.load(is);
                SENDER_EMAIL = config.getProperty("mail.user");
                SENDER_PASSWORD = config.getProperty("mail.password");
            } else {
                LOGGER.log(Level.SEVERE, "Configuration file 'configdb.properties' not found in classpath");
            }
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Error loading email configuration properties", exception);
        }
    }

    public static void sendWelcomeEmail(String recipientEmail,
            String studentName, String temporaryPassword) {
        if (SENDER_EMAIL == null || SENDER_PASSWORD == null) {
            LOGGER.log(Level.SEVERE, "Email credentials not loaded in static scope. Aborting dispatch.");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new EmailAuthenticator(SENDER_EMAIL, SENDER_PASSWORD));

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Alta de Cuenta: Sistema de Prácticas Profesionales - FEI");

            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html><body style='font-family: Arial, sans-serif; color: #333333; line-height: 1.6; background-color: #f4f6f9; padding: 20px;'>");
            htmlContent.append("<div style='max-width: 550px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); border: 1px solid #eef2f5;'>");

            htmlContent.append("  <div style='background-color: #0c4c84; padding: 25px; text-align: center; color: #ffffff;'>");
            htmlContent.append("    <h2 style='margin: 0; font-size: 22px; font-weight: bold; letter-spacing: 0.5px;'>Sistema de Prácticas Profesionales</h2>");
            htmlContent.append("    <p style='margin: 6px 0 0 0; font-size: 13px; opacity: 0.85; text-transform: uppercase; letter-spacing: 1px;'>Facultad de Estadística e Informática</p>");
            htmlContent.append("  </div>");

            htmlContent.append("  <div style='padding: 30px; background-color: #ffffff;'>");
            htmlContent.append("    <p style='margin-top: 0; font-size: 15px;'>Estimado(a) alumno(a) <b>").append(studentName).append("</b>,</p>");
            htmlContent.append("    <p style='font-size: 14px; color: #555555;'>Tu registro en la plataforma oficial ha sido completado con éxito por la coordinación. A partir de este momento puedes ingresar a tu portal institucional utilizando las siguientes credenciales de acceso:</p>");

            htmlContent.append("    <div style='margin: 24px 0; padding: 20px; background-color: #f8f9fa; border-radius: 8px; border: 1px solid #ebedf0;'>");

            htmlContent.append("      <div style='margin-bottom: 15px; display: flex; align-items: center;'>");
            htmlContent.append("        <span style='font-size: 14px; color: #666666; width: 100px; display: inline-block;'><b>Usuario:</b></span>");
            htmlContent.append("        <span style='font-family: monospace; font-size: 14px; color: #0c4c84; font-weight: bold; background-color: #e6f0fa; padding: 4px 8px; border-radius: 4px;'>").append(recipientEmail).append("</span>");
            htmlContent.append("      </div>");

            htmlContent.append("      <div style='display: flex; align-items: center;'>");
            htmlContent.append("        <span style='font-size: 14px; color: #666666; width: 100px; display: inline-block;'><b>Contraseña:</b></span>");
            htmlContent.append("        <span style='font-family: monospace; font-size: 14px; color: #c82333; font-weight: bold; background-color: #fde8e8; padding: 4px 8px; border-radius: 4px;'>").append(temporaryPassword).append("</span>");
            htmlContent.append("      </div>");

            htmlContent.append("    </div>");

            htmlContent.append("    <p style='font-size: 14px; color: #555555;'>Una vez dentro del panel, podrás comenzar con la gestión de tus actividades académicas correspondientes.</p>");
            htmlContent.append("    <p style='color: #999999; font-size: 12px; margin-top: 30px; text-align: center; border-top: 1px solid #f0f2f5; padding-top: 15px;'><i>Este es un mensaje automático institucional, por favor no respondas directamente a él.</i></p>");
            htmlContent.append("  </div>");

            htmlContent.append("  <div style='background-color: #f8f9fa; padding: 15px; text-align: center; font-size: 11px; color: #777777; border-top: 1px solid #ebedf0;'>");
            htmlContent.append("    Universidad Veracruzana &bull; Licenciatura en Ingeniería de Software");
            htmlContent.append("  </div>");

            htmlContent.append("</div>");
            htmlContent.append("</body></html>");

            jakarta.mail.internet.MimeBodyPart htmlPart = new jakarta.mail.internet.MimeBodyPart();
            htmlPart.setContent(htmlContent.toString(), "text/html; charset=utf-8");

            jakarta.mail.internet.MimeMultipart multipart = new jakarta.mail.internet.MimeMultipart();
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);

            Transport.send(message);
            LOGGER.log(Level.INFO, "Welcome email successfully dispatched to recipient: {0}", recipientEmail);
        } catch (MessagingException exception) {
            LOGGER.log(Level.SEVERE, "Failed to complete email transmission context for address: " + recipientEmail, exception);
        }
    }

    public static boolean sendRecoveryEmail(String targetEmail, String token) {
        boolean isSent = false;

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(properties, new EmailAuthenticator(SENDER_EMAIL, SENDER_PASSWORD));

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(targetEmail));
            message.setSubject("Restablecer Contraseña - Sistema de Prácticas Profesionales FEI");

            String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>"
                    + "<div style='max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;'>"
                    + "  <div style='background-color: #0c4c84; padding: 20px; text-align: center; color: white;'>"
                    + "    <h2>Sistema de Prácticas Profesionales</h2>"
                    + "  </div>"
                    + "  <div style='padding: 25px; background-color: #ffffff;'>"
                    + "    <p>Estimado usuario,</p>"
                    + "    <p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta institucional en el sistema.</p>"
                    + "    <p>Para continuar con el proceso, introduce el siguiente <b>código de verificación</b> en la aplicación:</p>"
                    + "    <div style='background-color: #f4f6f9; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 3px; color: #0c4c84; border-radius: 4px; margin: 20px 0; border: 1px dashed #0c4c84;'>"
                    + token
                    + "    </div>"
                    + "    <p style='color: #777; font-size: 13px;'><i>Nota: Este código es de uso único y expirará en los próximos 15 minutos por razones de seguridad. Si tú no solicitaste este cambio, puedes ignorar este correo de forma segura.</i></p>"
                    + "  </div>"
                    + "  <div style='background-color: #f4f6f9; padding: 15px; text-align: center; font-size: 11px; color: #777; border-top: 1px solid #ddd Rhine;'>"
                    + "    Facultad de Estadística e Informática - Universidad Veracruzana"
                    + "  </div>"
                    + "</div>"
                    + "</body></html>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            LOGGER.log(Level.INFO, "Account safety recovery verification token dispatched to: {0}", targetEmail);
            isSent = true;

        } catch (MessagingException exception) {
            LOGGER.log(Level.SEVERE, "Critical failure dispatching security transmission to target address: " + targetEmail, exception);
        }

        return isSent;
    }

    public static void sendInitialDocumentsEvaluationReport(
            String recipientEmail, String studentName,
            List<DocumentRowDTO> evaluatedDocuments) 
            throws DatabaseSystemException {

        if (SENDER_EMAIL == null || SENDER_PASSWORD == null) {
            LOGGER.log(Level.SEVERE, "Aborting operation dispatch: " 
                    + "Core email infrastructure credentials are empty.");
            throw new DatabaseSystemException("Las credenciales del sistema " 
                    + "de correo no están cargadas.");
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(properties, 
                new EmailAuthenticator(SENDER_EMAIL, SENDER_PASSWORD));

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, 
                    InternetAddress.parse(recipientEmail));
            message.setSubject("Resultado de Verificación de Documentos " 
                    + "Iniciales - SPP FEI");

            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html><body style='font-family: Arial, " 
                    + "sans-serif; color: #333; line-height: 1.6;'>");
            htmlContent.append("<div style='max-width: 650px; margin: 0 auto; " 
                    + "border: 1px solid #ddd; border-radius: 8px; " 
                    + "overflow: hidden;'>");

            htmlContent.append("<div style='background-color: #0c4c84; " 
                    + "padding: 20px; text-align: center; color: white;'>");
            htmlContent.append("<h2 style='margin: 0;'>Sistema de Prácticas " 
                    + "Profesionales</h2>");
            htmlContent.append("<p style='margin: 5px 0 0 0; font-size: 14px; " 
                    + "opacity: 0.9;'>Dictamen del Expediente Inicial</p>");
            htmlContent.append("</div>");

            htmlContent.append("<div style='padding: 25px; " 
                    + "background-color: #ffffff;'>");
            htmlContent.append("<p>Estimado(a) <b>").append(studentName)
                    .append("</b>,</p>");
            htmlContent.append("<p>Tu profesor ha concluido la revisión de " 
                    + "los formatos iniciales. A continuación se presenta el " 
                    + "reporte detallado:</p>");

            htmlContent.append("<table style='width: 100%; border-collapse: " 
                    + "collapse; margin: 20px 0; font-size: 14px;'>");
            htmlContent.append("<thead>");
            htmlContent.append("<tr style='background-color: #0c4c84; " 
                    + "color: white;'>");
            htmlContent.append("<th style='padding: 10px; " 
                    + "text-align: left;'>Tipo de Documento</th>");
            htmlContent.append("<th style='padding: 10px; text-align: center; " 
                    + "width: 110px;'>Estado</th>");
            htmlContent.append("<th style='padding: 10px; " 
                    + "text-align: left;'>Retroalimentación</th>");
            htmlContent.append("</tr>");
            htmlContent.append("</thead>");
            htmlContent.append("tbody");

            for (DocumentRowDTO row : evaluatedDocuments) {
                String badgeColor;
                String textStatus;

                if (row.getStatusCode() == DOCUMENT_VALIDATED) {
                    badgeColor = "#218838";
                    textStatus = "Validado";
                } else if (row.getStatusCode() == DOCUMENT_REJECTED) {
                    badgeColor = "#c82333";
                    textStatus = "Rechazado";
                } else {
                    badgeColor = "#e0a800";
                    textStatus = "En revisión";
                }

                String observation;
                if (row.getTemporaryFeedback() == null 
                        || row.getTemporaryFeedback().trim().isEmpty()) {
                    observation = "<span style='color: #999; " 
                            + "font-style: italic;'>Sin observaciones.</span>";
                } else {
                    observation = row.getTemporaryFeedback().trim();
                }

                htmlContent.append("<tr style='border-bottom: 1px solid #ddd;'>");
                htmlContent.append("<td style='padding: 10px; font-weight: " 
                        + "bold; color: #333;'>").append(row.getDocumentName())
                        .append("</td>");
                htmlContent.append("<td style='padding: 10px; " 
                        + "text-align: center;'>");
                htmlContent.append("<span style='background-color: ")
                        .append(badgeColor).append("; color: white; padding: " 
                        + "4px 8px; border-radius: 4px; font-size: 11px; " 
                        + "font-weight: bold; display: inline-block; min-width: " 
                        + "80px;'>").append(textStatus).append("</span>");
                htmlContent.append("</td>");
                htmlContent.append("<td style='padding: 10px; color: #55; " 
                        + "font-size: 13px;'>").append(observation)
                        .append("</td>");
                htmlContent.append("</tr>");
            }

            htmlContent.append("</tbody>");
            htmlContent.append("</table>");

            htmlContent.append("<p style='margin-top: 20px; font-size: 14px;'" 
                    + ">En caso de contar con formatos en estado Rechazado, " 
                    + "es obligatorio ingresar al sistema para corregirlos.</p>");
            htmlContent.append("</div>");

            htmlContent.append("<div style='background-color: #f4f6f9; " 
                    + "padding: 15px; text-align: center; font-size: 11px; " 
                    + "color: #777; border-top: 1px solid #ddd;'>");
            htmlContent.append("Facultad de Estadística e Informática - " 
                    + "Universidad Veracruzana");
            htmlContent.append("</div>");
            htmlContent.append("</div>");
            htmlContent.append("</body></html>");

            message.setContent(htmlContent.toString(), 
                    "text/html; charset=utf-8");

            Transport.send(message);
            LOGGER.log(Level.INFO, "Automated HTML document evaluation report " 
                    + "successfully sent to: {0}", recipientEmail);

        } catch (MessagingException exception) {
            LOGGER.log(Level.SEVERE, "JavaMail communication stream crashed " 
                    + "dispatching evaluation notification.", exception);
            throw new DatabaseSystemException("Fallo en el servicio de correo " 
                    + "al enviar el reporte de evaluación.", exception);
        }
    }

    public static void sendReportEvaluationEmail(String recipientEmail, String reportType,
            String reviewStatus, int hoursCovered, String observations) throws DatabaseSystemException {

        if (SENDER_EMAIL == null || SENDER_PASSWORD == null) {
            LOGGER.log(Level.SEVERE, "Aborting operation dispatch: Core email infrastructure credentials are empty.");
            throw new DatabaseSystemException("Las credenciales del sistema de correo no están cargadas.");
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(properties, new EmailAuthenticator(SENDER_EMAIL, SENDER_PASSWORD));

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Resultado de Evaluación de Reporte - SPP FEI");

            String badgeColor;
            if ("Aprobado".equalsIgnoreCase(reviewStatus)) {
                badgeColor = "#218838";
            } else {
                badgeColor = "#c82333";
            }

            String hoursSection = "";
            if ("Aprobado".equalsIgnoreCase(reviewStatus)) {
                hoursSection = "<p><b>Horas acreditadas en esta entrega:</b> " + hoursCovered + " horas</p>";
            }

            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html><body style='font-family: Arial, sans-serif; color: #33; line-height: 1.6;'>");
            htmlContent.append("<div style='max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;'>");

            htmlContent.append("  <div style='background-color: #0c4c84; padding: 20px; text-align: center; color: white;'>");
            htmlContent.append("    <h2 style='margin: 0;'>Sistema de Prácticas Profesionales</h2>");
            htmlContent.append("    <p style='margin: 5px 0 0 0; font-size: 14px; opacity: 0.9;'>Notificación de Dictamen de Reporte</p>");
            htmlContent.append("  </div>");

            htmlContent.append("  <div style='padding: 25px; background-color: #ffffff;'>");
            htmlContent.append("    <p>Estimado(a) alumno(a),</p>");
            htmlContent.append("    <p>Tu profesor asignado ha concluido la revisión técnica del documento que cargaste a la plataforma.</p>");

            htmlContent.append("    <div style='margin: 20px 0; padding: 15px; background-color: #f8f9fa; border-left: 4px solid ").append(badgeColor).append("; border-radius: 4px;'>");
            htmlContent.append("      <p style='margin: 0 0 8px 0;'><b>Tipo de documento:</b> ").append(reportType).append("</p>");
            htmlContent.append("      <p style='margin: 0 0 8px 0;'><b>Dictamen:</b> ");
            htmlContent.append("        <span style='background-color: ").append(badgeColor).append("; color: white; padding: 3px 8px; border-radius: 4px; font-size: 12px; font-weight: bold;'>").append(reviewStatus.toUpperCase()).append("</span>");
            htmlContent.append("      </p>");
            htmlContent.append(hoursSection);
            htmlContent.append("    </div>");

            htmlContent.append("    <p><b>Observaciones y retroalimentación del docente:</b></p>");
            htmlContent.append("    <div style='background-color: #f4f6f9; padding: 15px; border-radius: 4px; color: #55; font-style: italic; border: 1px solid #e9ecef;'>");
            htmlContent.append(observations.replace("\n", "<br/>"));
            htmlContent.append("    </div>");

            if ("Rechazado".equalsIgnoreCase(reviewStatus)) {
                htmlContent.append("<p style='margin-top: 20px; color: #c82333; font-weight: bold;'>Nota: Al haber sido rechazado, es obligatorio que corrijas las observaciones señaladas y vuelvas a subir el archivo PDF firmado al sistema para una nueva revisión.</p>");
            }

            htmlContent.append("    <p style='color: #777; font-size: 13px; margin-top: 25px;'><i>Por favor, no respondas a este mensaje, ya que fue emitido desde una cuenta automatizada.</i></p>");
            htmlContent.append("  </div>");

            htmlContent.append("  <div style='background-color: #f4f6f9; padding: 15px; text-align: center; font-size: 11px; color: #777; border-top: 1px solid #ddd;'>");
            htmlContent.append("    Facultad de Estadística e Informática - Universidad Veracruzana");
            htmlContent.append("  </div>");
            htmlContent.append("</div>");
            htmlContent.append("</body></html>");

            message.setContent(htmlContent.toString(), "text/html; charset=utf-8");
            Transport.send(message);
            LOGGER.log(Level.INFO, "Evaluation report email status successfully sent to: {0}", recipientEmail);

        } catch (MessagingException exception) {
            LOGGER.log(Level.SEVERE, "Critical failure inside communication stream while attempting to dispatch evaluation notification matrix", exception);
            throw new DatabaseSystemException("Error en el servicio de correo al notificar la evaluación.", exception);
        }
    }

    public static void sendProjectAssignmentNotification(String recipientEmail, String studentName,
            String projectName, String organizationName, String technicalResponsible, String assignmentReason) {

        if (SENDER_EMAIL == null || SENDER_PASSWORD == null) {
            LOGGER.log(Level.SEVERE, "Aborting operation dispatch: Email infrastructure credentials are empty.");
            return;
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(properties, new EmailAuthenticator(SENDER_EMAIL, SENDER_PASSWORD));

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Asignación Oficial de Proyecto de Prácticas Profesionales - SPP FEI");

            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html><body style='font-family: Arial, sans-serif; color: #33; line-height: 1.6;'>");
            htmlContent.append("<div style='max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;'>");
            htmlContent.append("  <div style='background-color: #0c4c84; padding: 20px; text-align: center; color: white;'>");
            htmlContent.append("    <h2 style='margin: 0;'>Sistema de Prácticas Profesionales</h2>");
            htmlContent.append("    <p style='margin: 5px 0 0 0; font-size: 14px; opacity: 0.9;'>Orden de Asignación Oficial</p>");
            htmlContent.append("  </div>");
            htmlContent.append("  <div style='padding: 25px; background-color: #ffffff;'>");
            htmlContent.append("    <p>Estimado(a) alumno(a) <b>").append(studentName).append("</b>,</p>");
            htmlContent.append("    <p>Te notificamos que el Comité de Prácticas Profesionales de la Facultad ha concluido el proceso de asignación de plazas. Se te ha vinculado de forma oficial al siguiente proyecto:</p>");

            htmlContent.append("    <div style='margin: 20px 0; padding: 15px; background-color: #f8f9fa; border-left: 4px solid #0c4c84; border-radius: 4px;'>");
            htmlContent.append("      <p style='margin: 0 0 8px 0;'><b>Proyecto:</b> ").append(projectName).append("</p>");
            htmlContent.append("      <p style='margin: 0 0 8px 0;'><b>Organización:</b> ").append(organizationName).append("</p>");
            htmlContent.append("      <p style='margin: 0;'><b>Responsable Técnico:</b> ").append(technicalResponsible).append("</p>");
            htmlContent.append("    </div>");

            htmlContent.append("    <p><b>Dictamen y Justificación de la Asignación:</b></p>");
            htmlContent.append("    <div style='background-color: #f4f6f9; padding: 15px; border-radius: 4px; color: #55; font-style: italic; border: 1px solid #e9ecef; margin-bottom: 20px;'>");
            htmlContent.append(assignmentReason.replace("\n", "<br/>"));
            htmlContent.append("    </div>");

            htmlContent.append("    <p style='font-size: 14px;'>A partir de este momento debes ponerte en contacto con el responsable técnico de la organización vinculada para coordinar tus horarios y dar inicio a tus actividades académicas.</p>");
            htmlContent.append("    <p style='color: #777; font-size: 13px; margin-top: 25px;'><i>Por favor, no respondas a este mensaje, ya que fue emitido desde una cuenta automatizada institucional.</i></p>");
            htmlContent.append("  </div>");
            htmlContent.append("  <div style='background-color: #f4f6f9; padding: 15px; text-align: center; font-size: 11px; color: #777; border-top: 1px solid #ddd;'>");
            htmlContent.append("    Facultad de Estadística e Informática - Universidad Veracruzana");
            htmlContent.append("  </div>");
            htmlContent.append("</div>");
            htmlContent.append("</body></html>");

            message.setContent(htmlContent.toString(), "text/html; charset=utf-8");
            Transport.send(message);
            LOGGER.log(Level.INFO, "Project allocation email dispatched successfully to student: {0}", recipientEmail);

        } catch (MessagingException exception) {
            LOGGER.log(Level.SEVERE, "Critical failure pushing allocation notification to stream: " + recipientEmail, exception);
        }
    }

    public static void sendSelfEvaluationApprovalEmail(String recipientEmail,
            String observations) throws DatabaseSystemException {
        if (SENDER_EMAIL == null || SENDER_PASSWORD == null) {
            LOGGER.log(Level.SEVERE, "Aborting email dispatch: Core infrastructure SMTP credentials are empty.");
            throw new DatabaseSystemException("Las credenciales del sistema de correo no están cargadas.");
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(properties,new EmailAuthenticator(SENDER_EMAIL, SENDER_PASSWORD));

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Validación de Autoevaluación Exitosa - SPP FEI");

            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html><body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>");
            htmlContent.append("<div style='max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;'>");

            htmlContent.append("  <div style='background-color: #0c4c84; padding: 20px; text-align: center; color: white;'>");
            htmlContent.append("    <h2 style='margin: 0;'>Sistema de Prácticas Profesionales</h2>");
            htmlContent.append("    <p style='margin: 5px 0 0 0; font-size: 14px; opacity: 0.9;'>Notificación de Liberación de Autoevaluación</p>");
            htmlContent.append("  </div>");

            htmlContent.append("  <div style='padding: 25px; background-color: #ffffff;'>");
            htmlContent.append("    <p>Estimado(a) alumno(a),</p>");
            htmlContent.append("    <p>Te notificamos que tu profesor asignado ha revisado y <b>APROBADO</b> tu formato de Autoevaluación de Prácticas Profesionales de manera exitosa.</p>");

            htmlContent.append("    <div style='margin: 20px 0; padding: 15px; background-color: #f8f9fa; border-left: 4px solid #218838; border-radius: 4px;'>");
            htmlContent.append("      <p style='margin: 0 0 5px 0;'><b>Tipo de documento:</b> Autoevaluación del Practicante</p>");
            htmlContent.append("      <p style='margin: 0;'><b>Estado del Hito:</b> <span style='color: #218838; font-weight: bold;'>VALIDADO / EXPEDIENTE CERRADO</span></p>");
            htmlContent.append("    </div>");

            htmlContent.append("    <p><b>Comentarios y retroalimentación del docente:</b></p>");
            htmlContent.append("    <div style='background-color: #f4f6f9; padding: 15px; border-radius: 4px; color: #55; font-style: italic; border: 1px solid #e9ecef;'>");
            htmlContent.append(observations.replace("\n", "<br/>"));
            htmlContent.append("    </div>");

            htmlContent.append("<p style='margin-top: 20px; font-weight: bold; color: #0c4c84;'>¡Felicidades! Al haber completado este hito operativo, tu estatus institucional ha sido actualizado. El sistema te habilitará a la brevedad la descarga automática de tu Oficio de Liberación Oficial.</p>");
            htmlContent.append("    <p style='color: #777; font-size: 13px; margin-top: 25px;'><i>Por favor, no respondas a este mensaje, ya que fue emitido desde una cuenta automatizada.</i></p>");
            htmlContent.append("  </div>");

            htmlContent.append("  <div style='background-color: #f4f6f9; padding: 15px; text-align: center; font-size: 11px; color: #777; border-top: 1px solid #ddd;'>");
            htmlContent.append("    Facultad de Estadística e Informática - Universidad Veracruzana");
            htmlContent.append("  </div>");
            htmlContent.append("</div>");
            htmlContent.append("</body></html>");

            message.setContent(htmlContent.toString(), "text/html; charset=utf-8");
            Transport.send(message);
            LOGGER.log(Level.INFO, "Self evaluation approval notification successfully dispatched to: {0}", recipientEmail);

        } catch (MessagingException exception) {
            LOGGER.log(Level.SEVERE, "Critical failure inside communication stream while attempting to dispatch evaluation verification", exception);
            throw new DatabaseSystemException("Error en el servicio de correo al notificar la validación.", exception);
        }
    }

    public static void sendFinalReportEvaluationEmail(String recipientEmail, String studentName,
            String reviewStatus, String observations) throws DatabaseSystemException {

        if (SENDER_EMAIL == null || SENDER_PASSWORD == null) {
            LOGGER.log(Level.SEVERE, "Aborting transmission process: Core email server credentials are not found.");
            throw new DatabaseSystemException("Las credenciales del sistema de correo no están cargadas.");
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(properties,
                new EmailAuthenticator(SENDER_EMAIL, SENDER_PASSWORD));

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            String subject;
            if ("Aprobado".equalsIgnoreCase(reviewStatus)) {
                subject = "Validación Exitosa: Informe Final de Prácticas Profesionales";
            } else {
                subject = "Acción Requerida: Informe Final Rechazado - SPP FEI";
            }
            message.setSubject(subject);

            String badgeColor;
            if ("Aprobado".equalsIgnoreCase(reviewStatus)) {
                badgeColor = "#218838";
            } else {
                badgeColor = "#c82333";
            }

            String headerText;
            if ("Aprobado".equalsIgnoreCase(reviewStatus)) {
                headerText = "¡Informe Final Validado!";
            } else {
                headerText = "Informe Final Rechazado";
            }

            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html><body style='font-family: Arial, sans-serif; color: #33; line-height: 1.6;'>");
            htmlContent.append("<div style='max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;'>");

            htmlContent.append("  <div style='background-color: ").append(badgeColor).append("; padding: 20px; text-align: center; color: white;'>");
            htmlContent.append("    <h2 style='margin: 0;'>Sistema de Prácticas Profesionales</h2>");
            htmlContent.append("    <p style='margin: 5px 0 0 0; font-size: 14px; opacity: 0.9;'>").append(headerText).append("</p>");
            htmlContent.append("  </div>");

            htmlContent.append("  <div style='padding: 25px; background-color: #ffffff;'>");
            htmlContent.append("    <p>Estimado(a) alumno(a) <b>").append(studentName).append("</b>,</p>");

            if ("Aprobado".equalsIgnoreCase(reviewStatus)) {
                htmlContent.append("    <p>Te informamos con agrado que tu profesor asignado ha concluido la revisión de tu <b>Informe Final técnico</b> y el dictamen ha sido establecido como <b>APROBADO</b>. Tu hito de cierre ha sido asentado correctamente en tu expediente digital.</p>");
            } else {
                htmlContent.append("    <p>Te notificamos que tras la revisión de tu <b>Informe Final técnico</b>, el profesor ha emitido un dictamen de <b>RECHAZADO</b> debido a inconsistencias o faltas observadas en el documento entregado.</p>");
            }

            htmlContent.append("    <div style='margin: 20px 0; padding: 15px; background-color: #f8f9fa; border-left: 4px solid ").append(badgeColor).append("; border-radius: 4px;'>");
            htmlContent.append("      <p style='margin: 0 0 5px 0;'><b>Entregable:</b> Informe Final de Prácticas Profesionales</p>");
            htmlContent.append("      <p style='margin: 0;'><b>Dictamen Oficial:</b> <span style='color: ").append(badgeColor).append("; font-weight: bold;'>").append(reviewStatus.toUpperCase()).append("</span></p>");
            htmlContent.append("    </div>");

            htmlContent.append("    <p><b>Observaciones y comentarios del docente:</b></p>");
            htmlContent.append("    <div style='background-color: #f4f6f9; padding: 15px; border-radius: 4px; color: #55; font-style: italic; border: 1px solid #e9ecef;'>");
            htmlContent.append(observations.replace("\n", "<br/>"));
            htmlContent.append("    </div>");

            if ("Aprobado".equalsIgnoreCase(reviewStatus)) {
                htmlContent.append("<p style='margin-top: 20px; font-weight: bold; color: #0c4c84;'>Con esta validación, has completado satisfactoriamente los requisitos documentales de tus prácticas en la Facultad de Estadística e Informática.</p>");
            } else {
                htmlContent.append("<p style='margin-top: 20px; color: #c82333; font-weight: bold;'>¡Importante! Es de carácter obligatorio que atiendas las observaciones del profesor, realices las correcciones correspondientes en tu PDF y vuelvas a cargarlo a la plataforma a la brevedad para evitar retrasos en tus trámites académicos.</p>");
            }

            htmlContent.append("    <p style='color: #777; font-size: 13px; margin-top: 25px;'><i>Por favor, no respondas a este mensaje, ya que fue emitido desde una cuenta automatizada.</i></p>");
            htmlContent.append("  </div>");

            htmlContent.append("  <div style='background-color: #f4f6f9; padding: 15px; text-align: center; font-size: 11px; color: #777; border-top: 1px solid #ddd;'>");
            htmlContent.append("    Facultad de Estadística e Informática - Universidad Veracruzana");
            htmlContent.append("  </div>");
            htmlContent.append("</div>");
            htmlContent.append("</body></html>");

            message.setContent(htmlContent.toString(), "text/html; charset=utf-8");
            Transport.send(message);
            LOGGER.log(Level.INFO, "Final report evaluation notification email successfully dispatched to: {0}", recipientEmail);

        } catch (MessagingException exception) {
            LOGGER.log(Level.SEVERE, "Network and stream infrastructure breakdown trying to dispatch final report validation email to: " + recipientEmail, exception);
            throw new DatabaseSystemException("Error en el servicio de correo al notificar la evaluación.", exception);
        }
    }
}
