/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import java.time.LocalDateTime;

/**
 * @author cinth
 * 
 */

public class ProjectRequestDTO {
    private int requestId;
    private String enrollment;
    private int projectId;
    private LocalDateTime uploadDate;
    private int priority;
    
    public ProjectRequestDTO() {
    }

    public ProjectRequestDTO(int requestId, String enrollment, int projectId, 
            LocalDateTime uploadDate, int priority) {
        this.requestId = requestId;
        this.enrollment = enrollment;
        this.projectId = projectId;
        this.uploadDate = uploadDate;
        this.priority = priority;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}