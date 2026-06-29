package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import java.sql.Date;

/**
 *
 * @author cinth
 * @author andre
 */
public class DocumentDTO {

    private int idDocument;
    private String fileName;
    private byte[] fileData;
    private String documentType;
    private Date deliveryDate;
    private String enrollment;
    private String reviewStatus;

    public DocumentDTO() {
    }

    public DocumentDTO(int idDocument, String fileName, byte[] fileData,
            String documentType, Date deliveryDate, String enrollment, String reviewStatus) {
        this.idDocument = idDocument;
        this.fileName = fileName;
        this.fileData = fileData;
        this.documentType = documentType;
        this.deliveryDate = deliveryDate;
        this.enrollment = enrollment;
        this.reviewStatus = reviewStatus;
    }

    public int getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
}
