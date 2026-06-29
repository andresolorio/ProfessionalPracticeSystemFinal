package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import java.time.LocalDateTime;

/**
 *
 * @author andre
 * @author cinth
 */
public class ActivitySubmissionDTO {

    private int idSubmission;
    private String studentEnrollment;
    private int idActivity;
    private String observations;
    private byte[] fileContent;
    private float grade;
    private LocalDateTime submissionDate;

    public ActivitySubmissionDTO() {
    }

    public ActivitySubmissionDTO(int idSubmission, String studentEnrollment,
            int idActivity, String observations, byte[] fileContent, float grade,
            LocalDateTime submissionDate) {
        this.idSubmission = idSubmission;
        this.studentEnrollment = studentEnrollment;
        this.idActivity = idActivity;
        this.observations = observations;
        this.fileContent = fileContent;
        this.grade = grade;
        this.submissionDate = submissionDate;
    }

    public int getIdSubmission() {
        return idSubmission;
    }

    public void setIdSubmission(int idSubmission) {
        this.idSubmission = idSubmission;
    }

    public String getStudentEnrollment() {
        return studentEnrollment;
    }

    public void setStudentEnrollment(String studentEnrollment) {
        this.studentEnrollment = studentEnrollment;
    }

    public int getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(int idActivity) {
        this.idActivity = idActivity;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
}
