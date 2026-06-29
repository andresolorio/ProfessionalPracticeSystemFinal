package mx.uv.lis.professionalpracticesystem.logic.utils;

import java.sql.Date;
import java.util.regex.Pattern;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ResponsibleProjectDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.StudentDTO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.UserDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.BASE64_SIZE_FACTOR;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMAIL_PATTERN;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.ENROLLMENT_PATTERN;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_CREDITS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_GRADE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MIN_CREDITS;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MIN_GRADE;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.NAME_PATTERN;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.NRC_PATTERN;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PHONE_PATTERN;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.RESET;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.STAFF_NUMBER_PATTERN;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.ZERO_THRESHOLD;

/**
 * 
 * @author cinth
 * @author andre
 */
public class Validator {
    public static void isValidId(int id) {
        if (id <= RESET) {
            throw new ValidationException("El identificador numérico " 
                    + "proporcionado es inválido o menor/igual a cero.");
        }
    }

    public static void isValidResponsible(ResponsibleProjectDTO responsible) {
        if (responsible == null) {
            throw new ValidationException("Los datos del responsable del proyecto no pueden ser nulos.");
        }
    }

    public static void checkEnrollmentFormat(String enrollment) {
        if (enrollment == null || enrollment.trim().isEmpty() 
                || !enrollment.matches(ENROLLMENT_PATTERN)) {
            throw new ValidationException("La matrícula es inválida, nula o vacía.");
        }
    }

    public static boolean isValidNRC(String nrc) {
        return nrc != null && Pattern.matches(NRC_PATTERN, nrc);
    }

    public static boolean isValidEmail(String email) {
        return email != null && Pattern.matches(EMAIL_PATTERN, email);
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && Pattern.matches(PHONE_PATTERN, phone);
    }

    public static boolean isValidStaffNumber(String staffNumber) {
        return staffNumber != null && Pattern.matches(STAFF_NUMBER_PATTERN, 
                staffNumber);
    }

    public static boolean isValidEnrollment(String enrollment) {
        return enrollment != null && Pattern.matches(ENROLLMENT_PATTERN, enrollment);
    }

    public static boolean isValidGrade(float grade) {
        return grade >= MIN_GRADE && grade <= MAX_GRADE;
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isNotFutureDate(Date date) {
        long currentTime = System.currentTimeMillis();
        Date today = new Date(currentTime);
        return !date.after(today);
    }

    public static boolean isValidFileSize(String fileBase64, int maxSizeBytes) {
        if (fileBase64 == null) {
            return false;
        }
        int estimatedSize = (int) (fileBase64.length() * BASE64_SIZE_FACTOR);
        return estimatedSize <= maxSizeBytes;
    }

    public static boolean isPositive(int value) {
        return value >= ZERO_THRESHOLD;
    }

    public static boolean isValidCreditRange(int credits) {
        return credits >= MIN_CREDITS && credits <= MAX_CREDITS;
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return name.matches(NAME_PATTERN);
    }

    public static void isValidStudent(StudentDTO student) {
        if (student == null) {
            throw new ValidationException("Los datos del estudiante no pueden ser nulos.");
        }
    }

    public static void isValidUser(UserDTO user) {
        if (user == null) {
            throw new ValidationException("Los datos de las credenciales de usuario no pueden ser nulos.");
        }
    }
}