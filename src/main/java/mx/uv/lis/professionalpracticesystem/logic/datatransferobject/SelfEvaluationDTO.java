/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import java.time.LocalDateTime;

/**
 * 
 * @author cinth
 */

public class SelfEvaluationDTO {
    private int idSelfEvaluation;
    private String enrollment;
    private int totalScore;
    private String filePath;
    private LocalDateTime uploadDate;

    public SelfEvaluationDTO() {
    }

    public SelfEvaluationDTO(int idSelfEvaluation, String enrollment, 
            int totalScore, String filePath, LocalDateTime uploadDate) {
        this.idSelfEvaluation = idSelfEvaluation;
        this.enrollment = enrollment;
        this.totalScore = totalScore;
        this.filePath = filePath;
        this.uploadDate = uploadDate;
    }

    public int getIdSelfEvaluation() {
        return idSelfEvaluation;
    }

    public void setIdSelfEvaluation(int idSelfEvaluation) {
        this.idSelfEvaluation = idSelfEvaluation;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }
    
    
}
