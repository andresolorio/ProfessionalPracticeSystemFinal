package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.EMPTY_STRING;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOCUMENT_VALIDATED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DOCUMENT_REJECTED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEXT_STATUS_UNDER_REVIEW;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEXT_STATUS_VALIDATED;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.TEXT_STATUS_REJECTED;

/**
 *
 * @author andre
 * @author cinth
 */
public class DocumentRowDTO {
    private final String documentName;
    private final String columnName;
    private int statusCode;
    private String temporaryFeedback = EMPTY_STRING;
    private boolean temporaryApprovedState;

    public DocumentRowDTO(String documentName, String columnName, 
            int statusCode) {
        this.documentName = documentName;
        this.columnName = columnName;
        this.statusCode = statusCode;
    }

    public String getDocumentName() {
        return this.documentName;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getTemporaryFeedback() {
        return this.temporaryFeedback;
    }

    public void setTemporaryFeedback(String temporaryFeedback) {
        this.temporaryFeedback = temporaryFeedback;
    }

    public boolean isTemporaryApprovedState() {
        return this.temporaryApprovedState;
    }

    public void setTemporaryApprovedState(boolean temporaryApprovedState) {
        this.temporaryApprovedState = temporaryApprovedState;
    }

    public String getStatusText() {
        String textOutput = TEXT_STATUS_UNDER_REVIEW;
        if (this.statusCode == DOCUMENT_VALIDATED) {
            textOutput = TEXT_STATUS_VALIDATED;
        } else if (this.statusCode == DOCUMENT_REJECTED) {
            textOutput = TEXT_STATUS_REJECTED;
        }
        return textOutput;
    }
}